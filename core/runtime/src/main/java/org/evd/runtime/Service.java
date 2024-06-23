package org.evd.runtime;

import jdk.internal.vm.ContinuationScope;
import org.evd.runtime.support.*;
import org.evd.runtime.support.function.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

public class Service {

    enum ServiceStatus{
        New,
        Running,
        PendingKill,
        Closed
    }
    // tick间隔
    private final static int TICK_INTERVAL = 20;
    /** node */
    private final Node node;    public Node getNode() { return node; }
    /** 服务名 */
    private final String name;  public String getName() { return this.name; }
    /** 线程池名字 */
    private final String scheduledName;     public String getScheduledName(){ return scheduledName; }
    /** 调度器 */
    private ScheduledExecutor scheduledExecutor;
    /** 服务状态 */
    public volatile ServiceStatus status = ServiceStatus.New;
    /** tick任务，因为tick要在协程中执行，所有封装为task */
    private final Task.TaskParam0 tickTask = new Task.TaskParam0 (this::pulse);
    /** service的接收队列 */
    private final ConcurrentLinkedDeque<CallBase> calls = new ConcurrentLinkedDeque<>();
    /** 此帧要执行的calls */
    private final List<CallBase> affirmCalls = new ArrayList<>();
    /** 协程的组，与service同名 */
    private final ContinuationScope scope; public ContinuationScope getScope() { return scope; }
    /** 协程池 */
    private final ContinuationPool continuationPool = new ContinuationPool(this);
    /** id分配器 */
    private long conIdAlloc = 1;
    private long applyConId(){return conIdAlloc++;}
    /** 当前正在执行的写成 */
    private Task.ContinuationWrapper runningContinuation;
    /** 执行中和阻塞的协程 */
    private final Map<Long, Task.ContinuationWrapper> continuations = new HashMap<>();
    /** ThreadLocal */
    private final static ThreadLocal<Service> threadLocal = new ThreadLocal<>();
    public static Service GetCurrent(){ return threadLocal.get(); }
    /** rpc调用路由到接收函数的类 */
    private RPCImplBase methodFunctionProxy;
    /** 本service的调用点 */
    private final CallPoint callPoint;

    public Service(Node node, String name, String scheduledName){
        this.node = node;
        this.name = name;
        this.scheduledName = scheduledName;
        // scope与service同名
        scope = new ContinuationScope(name);
        callPoint = new CallPoint(node.getName(), name);
    }

    /**
     * 绑定调度器
     * @param scheduledExecutor
     */
    public void bindScheduledExecutor(ScheduledExecutor scheduledExecutor) {
        if (this.scheduledExecutor != null){
            Log.warning("[{}]服务已经绑定了[{}]调度器，不能再次绑定[{}]", name, this.scheduledExecutor.getName(), scheduledExecutor.getName());
            return;
        }
        this.scheduledExecutor = scheduledExecutor;
    }

    /**
     * 当service加入到node
     * 由系统线程池执行
     */
    final void onAttacheToNode() {
        // 修改状态
        status = Service.ServiceStatus.Running;
        // 先执行初始化
        initVirtual();
        // 马上就可以执行pulse了
        pulse();
    }

    /**
     * init方法交给协程执行
     * 因为init中可能存在异步操作，异步可能触发协程yield，导致线程yield
     */
    private void initVirtual() {
        // 申请一个协程
        Task.ContinuationWrapper context = continuationPool.apply();
        // 绑定行为
        context.bindTask(new Task.TaskParam0(this::init), applyConId());
        // 设置为当前正在执行
        runningContinuation = context;
        // 执行协程
        context.runVirtual();
        // 取消正在执行
        this.runningContinuation = null;
    }

    /**
     * init由协程执行，交给子类继承
     */
    public void init() {
    }

    private void pulse() {
        // TODO 这一帧的时间
        // service放到threadLocal，以便于逻辑中从当前上线文中获取
        threadLocal.set(this);

        pulseAffirm();
        pulseCalls();

        tickVirtual();

        pulseTask();
        pulseEntity();

        // 逻辑结束后移除，因为下次tick会分配其他线程
        threadLocal.remove();

        // TODO 计时心跳，心跳间隔时间动态变化
        if (status == ServiceStatus.Running){
            scheduledExecutor.schedule(tickTask,
                    TICK_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * tick交给协程执行
     */
    private void tickVirtual() {
        // 申请一个协程
        Task.ContinuationWrapper context = continuationPool.apply();
        // 绑定行为
        context.bindTask(new Task.TaskParam0(this::tick), applyConId());
        // 设置为当前正在执行
        runningContinuation = context;
        // 执行协程
        context.runVirtual();

        this.runningContinuation = null;
    }

    public void tick() {

    }

    private void pulseEntity() {
        // todo 处理标脏的数据实体
    }

    private void pulseTask() {
        // todo 定时任务
    }

    /**
     * 从并发队列中转移到本线程内的队列
     * 如果取一个执行一个，可能因为执行时间长 同时并发队列一直被add，导致源源不断从并发队列中取出call，从而导致此帧时间过长
     */
    private void pulseAffirm() {
        while (!calls.isEmpty()){
            affirmCalls.add(calls.poll());
        }
    }

    /**
     * 执行call请求
     */
    private void pulseCalls() {
        for (CallBase call : affirmCalls){
            dispatchCall(call);
        }
        affirmCalls.clear();
    }

    private void dispatchCall(CallBase callbase) {
        Task.ContinuationWrapper context;
        // 发送的call
        if (callbase instanceof Call call){
            // 申请一个协程
            context = continuationPool.apply();
            // 绑定行为
            context.bindTask(new Task.TaskParam1<>(this::dispatch, call), applyConId());
        }
        // 返回的callResult
        else {
            CallResult callResult = (CallResult)callbase;
            context = continuations.get(callResult.continuationId);
            // 已经超时
            if (context == null){
                Log.console("callback is null");
                return;
            }
            // 赋值返回
            context.setResult(callResult.result);
        }
        // 设置为正在执行
        runningContinuation = context;
        // 执行协程
        context.runVirtual();
        // 清理当前上下文
        runningContinuation = null;
    }

    /**
     * 派发到对应的rpc监听函数
     * @param call
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void dispatch(Call call){
        Object func = getMethodFunction(call.methodKey);
        Object[] m = call.methodParam;
        if (call.needResult){
            Object result = null;
            switch (call.methodParam.length) {
                case 0: result = ((ReturnFunction0) func).apply(); break;
                case 1: result = ((ReturnFunction1) func).apply(m[0]); break;
                case 2: result = ((ReturnFunction2) func).apply(m[0], m[1]); break;
                case 3: result = ((ReturnFunction3) func).apply(m[0], m[1], m[2]); break;
                case 4: result = ((ReturnFunction4) func).apply(m[0], m[1], m[2], m[3]); break;
                case 5: result = ((ReturnFunction5) func).apply(m[0], m[1], m[2], m[3], m[4]); break;
                case 6: result = ((ReturnFunction6) func).apply(m[0], m[1], m[2], m[3], m[4], m[5]); break;
                case 7: result = ((ReturnFunction7) func).apply(m[0], m[1], m[2], m[3], m[4], m[5], m[6]); break;
                case 8: result = ((ReturnFunction8) func).apply(m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7]); break;
                case 9: result = ((ReturnFunction9) func).apply(m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8]); break;
                case 10: result = ((ReturnFunction10) func).apply(m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8], m[9]); break;
                default: break;
            }
            CallResult callReturn = call.createReturn();
            callReturn.result = result;

            node.send(callReturn);
        }else{
            try {
                switch (call.methodParam.length) {
                    case 0: ((Function0) func).apply(); break;
                    case 1: ((Function1) func).apply(m[0]); break;
                    case 2: ((Function2) func).apply(m[0], m[1]); break;
                    case 3: ((Function3) func).apply(m[0], m[1], m[2]); break;
                    case 4: ((Function4) func).apply(m[0], m[1], m[2], m[3]); break;
                    case 5: ((Function5) func).apply(m[0], m[1], m[2], m[3], m[4]); break;
                    case 6: ((Function6) func).apply(m[0], m[1], m[2], m[3], m[4], m[5]); break;
                    case 7: ((Function7) func).apply(m[0], m[1], m[2], m[3], m[4], m[5], m[6]); break;
                    case 8: ((Function8) func).apply(m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7]); break;
                    case 9: ((Function9) func).apply(m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8]); break;
                    case 10: ((Function10) func).apply(m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8], m[9]); break;
                    default: break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void holdContinuation(Task.ContinuationWrapper conTask){
        continuations.put(conTask.getConId(), conTask);
    }
    public void unHoldContinuation(Task.ContinuationWrapper conTask){
        continuations.remove(conTask.getConId());
        // 回收
        continuationPool.recycle(conTask);
    }

    /**
     * 创建call请求，并发送到目标service
     * 针对不需要返回结果的call请求
     * @param toCallPoint
     * @param methodKey
     * @param params
     */
    public void call(CallPoint toCallPoint, int methodKey, Object[] params) {
        Call call = new Call();
        call.from = this.callPoint;
        call.to = toCallPoint;

        call.methodKey = methodKey;
        call.methodParam = params;

        // 发送到目标线程
        getNode().send(call);
    }

    /**
     * 创建call请求，并发送到目标service
     * 针对需要返回结果的call请求
     * @param toCallPoint
     * @param methodKey
     * @param params
     */
    public Object callWait(CallPoint toCallPoint, int methodKey, Object[] params) {
        Call call = new Call();
        call.from = this.callPoint;
        call.to = toCallPoint;

        call.continuationId = runningContinuation.getConId();

        call.methodKey = methodKey;
        call.methodParam = params;

        call.needResult = true;

        // 发送到目标线程
        getNode().send(call);

        // 等待结果，内部会阻塞当前协程，直到call请求的结果返回
        return runningContinuation.waitResult();
    }

    public boolean isRunning() {
        return status == ServiceStatus.Running;
    }

    public void addCall(CallBase call) {
        calls.add(call);
    }

    /**
     * 通过methodKey获得函数指针
     * @param methodKey
     * @return
     */
    private Object getMethodFunction(int methodKey) {
        try {
            // 获取对应的代理类
            if (methodFunctionProxy == null) {
                // 命名规范，[子类]ServiceImp.java由工具自动生成
                Class<?> cls = Class.forName(getClass().getName() + "Impl");
                Constructor<?> c = cls.getDeclaredConstructor();
                c.setAccessible(true);
                methodFunctionProxy = (RPCImplBase)c.newInstance();
            }

            // 通过代理类 获取函数引用
            return methodFunctionProxy.getMethodFunction(this, methodKey);
        } catch (Exception e) {
            throw new SysException(e);
        }
    }

}

