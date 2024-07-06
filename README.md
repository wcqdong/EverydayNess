   
# todo
* (done)线程池负载均衡service.pulse()
* (done)协程执行rpc、init、tick
* rpc
  * (done)本地rpc
  * 远程rpc
  * (done)apt-rpc生成工具
* Serialize
  * (done)类
  * (done)枚举
  * pb协议
  * apt-serialize生成工具
* Module
  * (done)service以模块化方式启动
  * 模块化加载
* Gradle
  * 打包到Release文件夹



# Getting Started
![image](https://github.com/wcqdong/EverydayNess/assets/26830796/5b665f27-e2d0-4928-ad35-c1c29c151fd9)

jvm参数：因为使用Continuation，要加一下参数
--add-exports java.base/jdk.internal.vm=ALL-UNNAMED

启动参数：暂时没接入服务注册，用config下的Bootstrap-all.yml作为服务发现，node1表示启动哪个节点配置
Bootstrap-all.yml node1

# 目录结构

首字母大写的工程为主工程，首字母小写的工程为被引用的工程

* EverydayNess
  * BootStrap：引导程序，负责工程启动，动态加载配置的Service模块
  * core: 核心代码
    * runtime：线程模型等核心逻辑
    * gencode：生成代码工具，用apt实现
  * common：多个工程公用的代码
    * main：代码
    * gen：gencode生成的代码（rpc代理类、序列化类……）
  * Services： 
    * ConnService：连接服example
      * main：代码
      * gen：gencode生成的代码（rpc分发类、序列化类）
    * StageService：场景服example
      * main：代码
      * gen：gencode生成的代码（rpc分发类、序列化类）

# 注解说明
* @Module: [类] 每个子工程可以看做一个Module
  * @Module.OnStart: [方法] 当Module启动时由主线程执行
  * @Module.OnEnd: [方法] 当Module结束时由主线程执行
* @Actor: [类] 代表一个独立服务单元service，且service必须继承Service.java,会生成该service的代理类serviceProxy，用于调用方使用
* @rpc: [方法] 可被远程调用的函数。会在该serviceProxy中生成对应函数名、形参的代理函数
  * （必须）只能标注在Service的public成员函数上
* @SerializeClass: [类] 序列化类，当类对象作为rpc参数时必须为类标记该注解
  * （必须）需要继承ISerializable接口
  * （必须）需要有无参构造函数
* @SerializeField: [变量] 序列化字段，需要序列化的成员变量
  * （必须）需要有该成员变量的get、set函数

# 运行原理



