package org.evd.game.runtime;

import org.evd.game.runtime.support.LogCore;
import org.evd.game.runtime.support.SysException;

import java.util.concurrent.TimeUnit;

public abstract class TickCase {
    // tick间隔
    protected final static int TICK_INTERVAL = 20;
    enum CaseStatus{
        New,
        Running,
        PendingKill,
        Closed
    }

    /** 服务状态 */
    public volatile CaseStatus status = CaseStatus.New;
    protected final String name;
    protected long timeCurrent;
    /** tick任务，因为tick要在协程中执行，所有封装为task */
    private final Task.TaskParam0 tickTask = new Task.TaskParam0 (this::pulseCase);
    /** 统计帧频 */
    protected final FrameStatistics frame = new FrameStatistics(this);
    /** 调度器 */
    private ScheduledExecutor scheduledExecutor;
    private long tickInterval;


    public TickCase(String name, long tickInterval){
        this.name = name;
        this.tickInterval = tickInterval;
    }
    /**
     * 当前线程开始时间(毫秒)
     */
    public long getTimeCurrent() {
        return timeCurrent;
    }

    public void pulseCase(){
        timeCurrent = System.currentTimeMillis();

        pulse();

        long timeFinish = System.currentTimeMillis();

        long timeFrame = timeFinish - timeCurrent;

        frame.tick(timeFinish, timeFrame);

        // TODO 计时心跳，心跳间隔时间动态变化
        if (status == CaseStatus.Running){
            scheduledExecutor.schedule(tickTask,
                    tickInterval, TimeUnit.MILLISECONDS);
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
            throw new SysException("[{}] start error, because scheduledExecutor is null", name);
        }
        status = CaseStatus.Running;
        onStart();
    }

    /**
     * 绑定调度器
     * @param scheduledExecutor
     */
    public void bindScheduledExecutor(ScheduledExecutor scheduledExecutor) {
        if (this.scheduledExecutor != null){
            LogCore.core.warn("[{}]服务已经绑定了[{}]调度器，不能再次绑定[{}]", name, this.scheduledExecutor.getName(), scheduledExecutor.getName());
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
    public String getName(){
        return name;
    }

    protected abstract void pulse();
    protected void onStart(){

    }
}
