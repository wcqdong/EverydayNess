done
1. 线程池
2. 协程
3. 本地rpc
4. apt-rpc生成工具

   
todo
1. 远程rpc
2. 序列化
3. 动态加载Service，实现真正的服务模块化开发
4. 完善apt-rpc生成工具
5. 单线程负责所有service的tick任务投递到线程池，防止每个service投递造成线程池唤醒次数过多


Getting Started
![image](https://github.com/wcqdong/EverydayNess/assets/26830796/5b665f27-e2d0-4928-ad35-c1c29c151fd9)

jvm参数：因为使用Continuation，要加一下参数
--add-exports java.base/jdk.internal.vm=ALL-UNNAMED
启动参数：暂时没介入服务注册，用config下的Bootstrap-all.yml作为服务发现，node1表示启动哪个节点配置
Bootstrap-all.yml node1
