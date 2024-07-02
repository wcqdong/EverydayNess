package org.evd.BootStrap;

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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class Main {
    enum Color{
        blue("1"),
        red("2");
        Color(String s){

        }
    }
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Color c = Color.values()[1];

        int a = (int)Color.blue.ordinal();
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

        node.start();

        // 系统关闭时进行清理
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                // 再等待2秒 持久化数据
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        }));

    }
}