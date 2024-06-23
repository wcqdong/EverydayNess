package org.evd.runtime;

import org.evd.runtime.support.Log;
import org.evd.runtime.support.SysException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 节点，代表一个进程
 */
public class Node {
    enum NodeStatus{
        New,
        Running,
        PendingKill,
        Closed
    }

    private final String name;
    /** 多个线程池，把有阻塞service和非阻塞service放到不同的线程 */
    private final List<ScheduledExecutor> scheduledExecutors = new ArrayList<>();
    /** 状态 */
    private NodeStatus status = NodeStatus.New;
    /** node包含的services */
    private final ConcurrentHashMap<Object, Service> services = new ConcurrentHashMap<>();

    public Node(String name){
        this.name = name;
    }

    public void createExecutor(String name, int threadNum){
        if (status != NodeStatus.New){
            return;
        }

        scheduledExecutors.add(new ScheduledExecutor(name, threadNum));
    }

    /**
     * 启动
     * @throws RuntimeException
     */
    public void Start() throws RuntimeException {
        // 不能重复启动
        if (status != NodeStatus.New){
            throw new SysException("node已经运行过");
        }
        if (scheduledExecutors.isEmpty()){
            throw new SysException("node还为创建线程池");
        }
        List<Service> pendingAdd = new ArrayList<>();
        for (Map.Entry<Object, Service> entry: services.entrySet()){
            pendingAdd.add(entry.getValue());
        }
        // 清理services，后面会重新addService
        services.clear();

        // 修改状态
        status = NodeStatus.Running;
        // addService
        for (Service service : pendingAdd){
            addService(service);
        }
    }

    /**
     * 创建任务异步添加到service
     * @param service
     */
    public void addService(Service service){
        // node还未启动，services起到pending暂存的作用
        if (status == NodeStatus.New){
            services.put(service.getName(), service);
        }else{
            Optional<ScheduledExecutor> result = scheduledExecutors.stream().filter(s->s.getName().equals(service.getScheduledName())).findFirst();
            if (result.isEmpty()){
                Log.console("[{}]服务找不到对应的调度器[{}]", service.getName(), service.getScheduledName());
                return;
            }
            ScheduledExecutor scheduledExecutor = result.get();
            service.bindScheduledExecutor(scheduledExecutor);
            // 提交task，task中会添加并启动service
            scheduledExecutor.submit(new Task.TaskParam1<>(this::attachService, service));
        }
    }

    /**
     * 真正执行service添加到node上
     * @param service
     */
    private void attachService(Service service){
        // 加入到services
        services.put(service.getName(), service);

        service.onAttacheToNode();
    }

    public String getName(){
        return name;
    }

    /**
     * 发送call请求，由node路由到目标service
     * @param call
     */
    public void send(CallBase call) {
        // 如果是同节点，直接投递到目标service
        if (name.equals(call.to.nodeId)){
            Service toService = services.get(call.to.servId);
            if (toService == null){
//                throw new SysException("service not exist " + call.to);
                return;
            }
            if (!toService.isRunning()){
                Log.console("service is not running " + call.to);
                return;
            }
            // 加入到目标service的消息队列
            toService.addCall(call);
        }else{
            // TODO 发送到目标node
        }
    }
}


