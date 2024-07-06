package org.evd.BootStrap;

import org.evd.game.common.ClassFinder;
import org.evd.game.common.ConstPath;
import org.evd.BootStrap.config.NodeConfig;
import org.evd.BootStrap.config.NodeInfo;
import org.evd.BootStrap.config.ScheduleInfo;
import org.evd.BootStrap.config.ServiceInfo;
import org.evd.game.runtime.DistributeConfig;
import org.evd.game.runtime.Node;
import org.evd.game.runtime.Service;
import org.evd.game.runtime.support.LogCore;
import org.evd.game.runtime.support.SysException;
import org.evd.game.runtime.support.TupleUtils;
import org.evd.game.runtime.support.TwoTuple;
import org.evd.game.runtime.annotation.Module;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.List;

public class Main {
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        if (args.length < 2){
//            Log.error("param error");
//            Log.error("     Param1: BootStrap file name");
//            Log.error("     Param2: Name of node");
//
//            throw new SysException("param Error");
//        }
        String bootStrapName = "Bootstrap-all.yml";
        String nodeName = "node1";
        if (args.length > 0){
            bootStrapName = args[0];
        }
        if (args.length > 1){
            nodeName = args[1];
        }

        String configPath = ConstPath.CONFIGURATION_PATH + bootStrapName;
        NodeConfig config = NodeConfig.load(configPath);

        final String nName = nodeName;
        Optional<NodeInfo> nodeInfoOptional = config.getNodes().stream().filter(n->n.getName().equals(nName)).findFirst();
        if (nodeInfoOptional.isEmpty()){
            LogCore.core.error("[{}] node config not exist", nodeName);
            return;
        }

        NodeInfo nodeInfo = nodeInfoOptional.get();
        Node node = new Node(nodeName, nodeInfo.getAddr());
        for (ScheduleInfo scheduleInfo : nodeInfo.getSchedule()){
            node.createExecutor(scheduleInfo.getName(), scheduleInfo.getNum());
            for (ServiceInfo serviceInfo : scheduleInfo.getServices()){
                Class<ServiceInfo> clazz = (Class<ServiceInfo>) Class.forName("org.evd.game." + serviceInfo.getClassName() + "." + serviceInfo.getClassName());
                if (clazz == null){
                    throw new SysException("service class not exist org.evd.service.{}", serviceInfo.getClassName());
                }
                // TODO 按service名加载 XXXService.jar


                Constructor con = clazz.getConstructor(Node.class, String.class, String.class);
                if (serviceInfo.getNum() < 0){
                    Service service = (Service)con.newInstance(node, serviceInfo.getName(), scheduleInfo.getName());
                    node.addService(service);
                    DistributeConfig.addSingleService(service);
                }else{
                    for (int i=1; i<=serviceInfo.getNum(); ++i){
                        Service service = (Service)con.newInstance(node, serviceInfo.getName() + i, scheduleInfo.getName());
                        node.addService(service);
                    }
                }
            }
        }

        List<TwoTuple<Integer, Method>> starters = new ArrayList<>();
        List<TwoTuple<Integer, Method>> enders = new ArrayList<>();
        List<Class<?>> sources = ClassFinder.getAllClass("org.evd.game");
        for (Class<?> clazz : sources){
            if (clazz.isAnnotationPresent(Module.class)){
                for (Method method : clazz.getDeclaredMethods()){
                    if (Modifier.isStatic(method.getModifiers())){
                        Module.OnStart starter = method.getAnnotation(Module.OnStart.class);
                        Module.OnEnd ender = method.getAnnotation(Module.OnEnd.class);
                        if (starter != null){
                            starters.add(TupleUtils.tuple(starter.priority(), method));
                        }
                        if (ender != null){
                            enders.add(TupleUtils.tuple(ender.priority(), method));
                        }
                    }

                }
            }
        }
        // 按starter的优先级排序
        starters.sort(Comparator.comparingInt(o -> o.first));
        enders.sort(Comparator.comparingInt(o -> o.first));
        for (TwoTuple<Integer, Method> starter : starters){
            starter.second.invoke(null, node);
        }

        // 节点启动
        node.start();

        // 系统关闭时进行清理
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                for (TwoTuple<Integer, Method> ender : enders){
                    try {
                        ender.second.invoke(null);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
                // TODO 处理service.close函数
                // TODO 处理各jar包的end函数
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

    }

}