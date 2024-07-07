package org.evd.game.runtime;

import jdk.internal.vm.ContinuationScope;
import org.evd.game.runtime.call.Call;
import org.evd.game.runtime.call.CallBase;
import org.evd.game.runtime.call.CallPoint;
import org.evd.game.runtime.call.CallResult;
import org.evd.game.runtime.serialize.CallPulseBuffer;
import org.evd.game.runtime.support.LogCore;
import org.evd.game.runtime.support.SysException;
import org.evd.game.runtime.support.function.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 服务
 */
public class Service extends TickCase{

    public void addCall_snt(CallBase call) {
        calls.add(call);
    }

    public CallBase removeCallFrameReferences_st(long callId) {
        return callFrameReferences.remove(callId);
    }

    enum ServiceStatus{
        New,
        Running,
        PendingKill,
        Closed
    }
    /** node */
    protected final Node node;    public Node getNode() { return node; }
    /** 线程池名字 */
    private final String scheduledName;     public String getScheduledName(){ return scheduledName; }

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
    public static Service getCurrent(){
        return threadLocal.get();
    }
    /** rpc调用路由到接收函数的类 */
    private RPCImplBase methodFunctionProxy;
    /** 本service的调用点 */
    private final CallPoint callPoint;
    /** 远程请求RPC缓冲区,引用 */
    private final Map<Long, CallBase> callFrameReferences = new HashMap<>();
    public void addCallFrameReferences(CallBase call) { callFrameReferences.put(call.id, call);}
    /** 远程请求RPC缓冲区 */
    private final Map<String, CallPulseBuffer> callFrameBuffers = new HashMap<>();

    public Service(Node node, String name, String scheduledName, long tickInterval){
        super(name, tickInterval);
        this.node = node;
        this.scheduledName = scheduledName;
        // scope与service同名
        scope = new ContinuationScope(name);
        callPoint = new CallPoint(node.getId(), name);

    }

    public Service(Node node, String name, String scheduledName){
        this(node, name, scheduledName, TICK_INTERVAL);
    }

    @Override
    protected void init_t() {
        // 加入到services
        node.attachToNode(this);

        // 修改状态
        status = CaseStatus.Running;
        // 先执行初始化
        initVirtual_t();
    }

    /**
     * init方法交给协程执行
     * 因为init中可能存在异步操作，异步可能触发协程yield，导致线程yield
     */
    private void initVirtual_t() {
        // 申请一个协程
        Task.ContinuationWrapper continuation = continuationPool.apply();
        // 绑定行为
        continuation.bindTask(new Task.TaskParam0(this::init), applyConId());
        // 设置为当前正在执行
        runningContinuation = continuation;
        // 执行协程
        continuation.runVirtual();
        // 取消正在执行
        this.runningContinuation = null;
    }

    @Override
    protected void pulse() {
        // service放到threadLocal，以便于逻辑中从当前上线文中获取
        threadLocal.set(this);

        pulseAffirm_st();
        pulseCalls_st();

        tickVirtual_st();

        pulseTask_st();
        pulseEntity_st();

        //刷新call发送缓冲区
        flushCallFrameBuffers_st();

        // 逻辑结束后移除，因为下次tick会分配其他线程
        threadLocal.remove();
    }

    /**
     * tick交给协程执行
     */
    private void tickVirtual_st() {
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

    private void pulseEntity_st() {
        // todo 处理标脏的数据实体
    }

    private void pulseTask_st() {
        // todo 定时任务
    }

    /**
     * 刷新远程调用RPC缓冲区
     */
    private void flushCallFrameBuffers_st() {
        for (CallPulseBuffer frameCache : callFrameBuffers.values()) {
            try {
                frameCache.flush_st(node);
            } catch (Throwable e) {
                // 不做任何处理 仅仅抛出异常
                // 避免因为一个任务的出错 造成后续的任务无法继续执行 需要等到下一个心跳
                LogCore.core.error("", e);
                /*log.error("", e);*/
            }
        }
        // 正常应该不会出现，为了保证安全，防止由于错误导致内存问题
        if(!callFrameReferences.isEmpty()){
            LogCore.core.error("【port】flush rpc缓冲区后，仍有残留的引用类型call");
            callFrameReferences.clear();
        }
    }

    /**
     * 从并发队列中转移到本线程内的队列
     * 如果取一个执行一个，可能因为执行时间长 同时并发队列一直被add，导致源源不断从并发队列中取出call，从而导致此帧时间过长
     */
    private void pulseAffirm_st() {
        while (!calls.isEmpty()){
            affirmCalls.add(calls.poll());
        }
    }

    /**
     * 执行call请求
     */
    private void pulseCalls_st() {
        for (CallBase call : affirmCalls){
            dispatchCall_st(call);
        }
        affirmCalls.clear();
    }

    private void dispatchCall_st(CallBase callbase) {
        Task.ContinuationWrapper context;
        // 发送的call
        if (callbase instanceof Call call){
            // 申请一个协程
            context = continuationPool.apply();
            // 绑定行为
            context.bindTask(new Task.TaskParam1<>(this::dispatch_st, call), applyConId());
        }
        // 返回的callResult
        else {
            CallResult callResult = (CallResult)callbase;
            context = continuations.get(callResult.id);
            // 已经超时
            if (context == null){
                LogCore.core.warn("callback is null");
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
    public void dispatch_st(Call call){
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

            sendCall_st(callReturn);
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

        sendCall_st(call);
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

        call.id = runningContinuation.getConId();

        call.methodKey = methodKey;
        call.methodParam = params;

        call.needResult = true;

        sendCall_st(call);

        Task.ContinuationWrapper thisContinuation = runningContinuation;
        // 等待结果，内部会阻塞当前协程，直到call请求的结果返回
        return thisContinuation.waitResult();
    }

    /**
     * 发送call请求
     * @param call
     */
    private void sendCall_st(CallBase call) {
        String toNodeId = call.to.nodeId;
        CallPulseBuffer buffer = callFrameBuffers.get(toNodeId);

        // 如果之前没有缓冲 那么就初始化一个
        if (buffer == null) {
            buffer = new CallPulseBuffer(toNodeId);
            callFrameBuffers.put(toNodeId, buffer);
        }

        // 将要发送内容放入发送缓冲中
        // 先尝试写入 如果失败(一般都是缓冲剩余空间不足)则先清空缓冲 后再尝试写入
        // 如果还是失败 那证明有可能是发送内容过大 不进行缓冲 直接发送
        if (!buffer.writeCall(this, call)) {
            //日志 第一次尝试写入缓冲失败
            LogCore.core.warn("第一次尝试写入缓冲失败：bufferLen={}, nodeId={}, portId={}, remoteNodeId={}", buffer.getLength(), getId(), node.getId(), toNodeId);

            //刷新缓冲区
            buffer.flush_st(node);
            //再次尝试写入缓冲
            if (!buffer.writeCall(this, call)) {
                //日志 第二次尝试写入缓冲失败
                LogCore.core.error("第二次尝试写入缓冲失败, call请求最大支持2M：bufferLen={}", buffer.getLength());
            }
        }
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


    @Override
    public void onClose(){

        node.remove(this);

        // 回收
        for (Map.Entry<String, CallPulseBuffer> en : callFrameBuffers.entrySet()){
            en.getValue().close();
        }
        callFrameBuffers.clear();
    }

    /**
     * 获取系统时间
     *
     * @return
     */
    public static long getTime() {
        return getCurrent().getTimeCurrent();
    }

}

