package org.evd.game.runtime;

import org.evd.game.runtime.support.LogCore;
import org.evd.game.runtime.support.SysException;

import java.util.concurrent.TimeUnit;

public abstract class TickCase {
    // tick间隔
    protected final static int TICK_INTERVAL = 5;
    enum CaseStatus{
        New,
        Running,
        PendingKill,
        Closed
    }

    /** 服务状态 */
    public volatile CaseStatus status = CaseStatus.New;
    protected final String id;
    protected long timeCurrent;
    /** tick任务，因为tick要在协程中执行，所有封装为task */
    private final Task.TaskParam0 tickTask = new Task.TaskParam0 (this::pulseCase_t);
    /** 统计帧频 */
    protected final FrameStatistics frame = new FrameStatistics(this);
    /** 调度器 */
    private ScheduledExecutor scheduledExecutor;
    private final long tickInterval;


    public TickCase(String name, long tickInterval){
        this.id = name;
        this.tickInterval = tickInterval;
    }
    /**
     * 当前线程开始时间(毫秒)
     */
    public long getTimeCurrent() {
        return timeCurrent;
    }

    public void pulseCase_t(){
        timeCurrent = System.currentTimeMillis();

        pulse();

        long timeFinish = System.currentTimeMillis();

        long timeFrame = timeFinish - timeCurrent;

        // 统计时间
        frame.tick_t(timeFinish, timeFrame);

        if (status == CaseStatus.Running){
            // 计时心跳，心跳间隔时间动态变化
            long pulseLeftTime = tickInterval - timeFrame;
            if (pulseLeftTime <= 0)
                scheduledExecutor.submit(tickTask);
            else
                scheduledExecutor.schedule(tickTask, pulseLeftTime, TimeUnit.MILLISECONDS);

        // service被停止
        }else if(status == CaseStatus.PendingKill){
            status = CaseStatus.Closed;
            onClose();
        }
    }

    protected void stop(){
        status = CaseStatus.PendingKill;
        onStop();
    }

    private void onStop() {
    }

    protected void onClose() {
    }

    public final void start(){
        // 不能重复启动
        if (status != CaseStatus.New){
            throw new SysException("node已经运行过");
        }
        if (scheduledExecutor == null){
            throw new SysException("[{}] start error, because scheduledExecutor is null", id);
        }
        status = CaseStatus.Running;
        onStart();

        // 提交task，task中会添加并启动service
        scheduledExecutor.submit(new Task.TaskParam0(this::initCase_t));
    }
    private void initCase_t(){
        init_t();
        scheduledExecutor.submit(tickTask);
    }
    protected void init_t(){

    }
    /**
     * init由协程执行，交给子类继承
     */
    public void init(){

    }

    /**
     * 绑定调度器
     * @param scheduledExecutor
     */
    public void bindScheduledExecutor(ScheduledExecutor scheduledExecutor) {
        if (this.scheduledExecutor != null){
            LogCore.core.warn("[{}]服务已经绑定了[{}]调度器，不能再次绑定[{}]", id, this.scheduledExecutor.getName(), scheduledExecutor.getName());
            return;
        }
        this.scheduledExecutor = scheduledExecutor;
    }

    public boolean isRunning() {
        return status == CaseStatus.Running;
    }

    /**
     * 获取名字
     */
    public String getId(){
        return id;
    }

    protected abstract void pulse();
    protected void onStart(){

    }
}
