package org.evd.runtime;

import jdk.internal.vm.Continuation;
import org.evd.runtime.support.function.Function0;
import org.evd.runtime.support.function.Function1;

import java.io.Closeable;

public class Task {
    /**
     * 对协程栈的封装
     */
    public static class ContinuationWrapper implements Runnable, Closeable {
        /** service */
        private final Service service;
        /** 栈 */
        private final Continuation continuation;

        // ----- 以下的参数会变化，不同的逻辑设置为不同的task -----
        /** 执行的逻辑 */
        private Runnable task;
        /** 返回的结果 */
        private Object result;
        /** 协程id（回调id） */
        private long conId;

        public ContinuationWrapper(Service service) {
            this.service = service;
            this.continuation = new Continuation(service.getScope(), this);
        }

        /**
         * 绑定task（要执行的逻辑），并执行协程id
         * @param task
         * @param conId
         */
        public void bindTask(Runnable task, long conId) {
            this.task = task;
            this.conId = conId;
        }

        /**
         * 由协程执行
         */
        @Override
        public void run() {
            while (true) {
                doWork();
                Continuation.yield(continuation.getScope());
            }
        }

        private void doWork() {
            if (task == null){
                // TODO 警告
                return;
            }
            // 先放入service中，因为task.run()可能发生协程yield
            // 如果不保存，则无法拿到栈恢复执行
            service.holdContinuation(this);

            task.run();

            // 执行结束，移除
            service.unHoldContinuation(this);
        }

        @Override
        public void close() {
            // 清理临时变量
            task = null;
            result = null;
            conId = 0;
        }

        /**
         * 执行或继续执行run()函数中的逻辑
        */
        public void runVirtual(){
            continuation.run();
        }

        public long getConId(){
            return conId;
        }

        public void setResult(Object result){
            this.result = result;
        }

        /**
         * 协程进入阻塞，等待结果
         * @return
         */
        public Object waitResult() {
            // 协程进入阻塞，因为此时result为null
            // 需要等其他协程setResult后并runVirtual唤醒协程，才能执行return result;
            Continuation.yield(continuation.getScope());
            return result;
        }
    }

    public static class TaskParam0 implements Runnable {
        private final Function0 func;
        public TaskParam0(Function0 func) {
            this.func = func;
        }

        @Override
        public void run() {
            try {
                func.apply();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static class TaskParam1<T1> implements Runnable {
        private final Function1<T1> func;
        private final T1 t1;
        public TaskParam1(Function1<T1> func, T1 t1) {
            this.func = func;
            this.t1 = t1;
        }

        @Override
        public void run() {
            try {
                func.apply(t1);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}