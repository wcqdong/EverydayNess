   
# todo
* (done)线程池负载均衡service.pulse()
* (done)协程执行逻辑
* rpc
  * (done)本地rpc
  * 远程rpc
  * (done)apt-rpc生成工具
* 序列化
  * (done)类
  * (done)枚举
  * pb协议
  * apt-serialize生成工具
* 动态加载Service，实现真正的服务模块化开发
* Module
  * (done)service以模块化方式启动
  * 模块化加载



# Getting Started
![image](https://github.com/wcqdong/EverydayNess/assets/26830796/5b665f27-e2d0-4928-ad35-c1c29c151fd9)

jvm参数：因为使用Continuation，要加一下参数
--add-exports java.base/jdk.internal.vm=ALL-UNNAMED

启动参数：暂时没接入服务注册，用config下的Bootstrap-all.yml作为服务发现，node1表示启动哪个节点配置
Bootstrap-all.yml node1
