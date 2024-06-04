## 版本介绍
- springBoot: 3.2.0
- JDK: 22
- maven: 3.9.6
- springCloudAlibaba: 2023.0.0.0-RC1
- springCloud: 2023.0.0
本来打算使用Nacos作为注册中心才引入的springCloudAlibaba，后来放弃了Nacos，想要自己学习建造一个。所以springCloud和springCloudAlibaba依赖无需引入！
为什么用java22？当然是因为我使用的IDEA2024.1.1创建项目时可选择的最低java版本已经是17了。而且后面用到etcd来实现注册中心会用到一些包，需要java版本必须在11以上。
## 项目最终目标
仅使用注解就能完成服务提供者的服务注入，以及使用注解就能完成代理对象注入。
1. 序列化器实现
2. 基于ETCD实现注册中心，支持心跳检测和续期机制，支持消费端服务缓存。
3. 负载均衡实现
4. 重试机制实现
5. 容错机制实现

### 2024-05-24 目标（实际到27号才勉强算是完成）
使用JDK动态代理实现简易的远程调用。通过重写 InvocationHandler.invoke() 实现代理对象增强，大概是如下流程。
![](/jpg/动态代理简易流程.jpg)

启动rpc-consumer和rpc-provider两个模块，调用前者控制器中提供的url同时consumer会调用provider。

### 目录结构
- RPC 提供了拦截器和请求前置处理器，定义了远程访问的请求body类和响应格式，创建动态代理工厂，一个序列化反序列化的工具，一个本地服务注册器。
- rpc-commen 没有什么重要的东西。一个用于测试的User实体类，一个需要被服务提供者实现的interface抽象方法。
- rpc-consumer 服务消费者，验证功能时调用其提供的接口。
- rpc-provider 服务提供者，对commen中的interface做了重写。
- rpc-official 未来真正实现rpcDemo功能的模块。

### 介绍
- 动态代理对consumer的调用动作进行增强。
- 工厂模式创建代理对象。
- provider启动时自动将服务注册到自定义注册中心。
- 对象序列化反序列化。
- 反射调用本地注册中心中的方法。

代码写起来有些慢，实际上逻辑很简单，执行流程啥的就不写了，第一个版本到这里结束。
（其中还有很多问题，比如调用地址的硬编码问题，服务提供者响应信息未传递给服务调用者问题……）

### 2024-05-28 新目标：解决2024-05-24中出现的硬编码问题。实现全局配置加载器。
5月29号记录一下目标进度为100%
重新建了一个模块 **rpc-official** , **RPC**模块中的代码已经复制进来了，以后所有的扩展会在这个模块中编写。
全局配置加载器的简单实现由以下几个类实现。
- RpcConstant: 定义了RPC需要用到的常量，全局配置以“rpc”开头就是在这里定义的。
- RpcConfig: 定义了有哪些可配置项
- ConfigUtils: 读取配置内容的工具类
- RpcHolder: 使用枚举实现了一个单例，用来加载配置

rpc-provider模块的application.properties中添加如下配置
rpc.name=rpc-provider
rpc.version=11.0
rpc.serverHost=127.0.0.1
rpc.serverPort=8010

在RpcProviderApplication添加了如下语句用于测试配置加载功能：
RpcConfig rpcConfig = RpcHolder.INSTANCE.getRpcConfig();
打个断点启动项目会发现配置文件中的内容已经被加载到rpcConfig对象中。


### 2024-05-29 新目标：修改RPC模块中遗留的简易本地服务注册器LocalRegistry
#### 准备
下载etcd，[下载链接](https://github.com/etcd-io/etcd/releases)。
下载对应系统的软件版本，以windows版本举例。下载之后得到一个压缩包，解压之后包含<b>etcd.exe(服务本身), etcdctl.exe(客户端，用于操作etcd), etcdutl.exe(备份回复工具)</b>。

下载etcd图形化工具，这里用的是etcdKeeper，[下载链接](https://github.com/evildecay/etcdkeeper/)。

先启动etcd.exe，再启动etcdkeeper.exe，自己实验用别搞什么登录验证，启动这两个进程就可以通过http://127.0.0.1:8080/etcdkeeper/访问图形界面。
![](/jpg/etcdKeeper.png)

### 2024-06-04 注册中心的基础功能已经实现了。
在rpc-official模块中引入了junit-jupiter(junit依赖不适用springboot3)。添加了一个RegistryTest测试类。
<strong>EtcdRegistry是实现注册中心的核心类、包含了服务注册，本地缓存注册、心跳检测(注册时间续期)、服务下线、服务列表获取</strong>
SpiLoader类是spi机制的实现，这个项目中使用的序列化反序列化器是原生java提供，如果有更好的选择可以使用SpiLoader动态切换。注册中心实现方案同理，本项目基于etcd实现，如果要使用zookeeper实现也可以使用SpiLoader切换。
- 启动etcd和etcdKeeper，启动rpc-provider模块会发现该服务已经注册到etcd啦。
- 如果服务手动关闭会出发springboot提供的shoutDownHook功能自动从注册中心下线
- 若因某些原因未能触发shoutDownHook，服务死亡后丢失心跳检测的续期操作etcd会在30秒后自动离线该服务

### 2024-06-04 新目标：注册中心和服务提供者消费者没有强依赖，作为一个独立的服务。注册和调用通过注解完成，像Feign那样





