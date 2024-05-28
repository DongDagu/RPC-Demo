## 项目最终目标
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

### 介绍
- 动态代理对consumer的调用动作进行增强。
- 工厂模式创建代理对象。
- provider启动时自动将服务注册到自定义注册中心。
- 对象序列化反序列化。
- 反射调用本地注册中心中的方法。

代码写起来有些慢，实际上逻辑很简单，执行流程啥的就不写了，第一个版本到这里结束。
（其中还有很多问题，比如调用地址的硬编码问题，服务提供者响应信息未传递给服务调用者问题……）


