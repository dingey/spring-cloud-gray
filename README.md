# spring cloud 灰度支持

## 快速开始
支持eureka和nacos为注册中心的灰度方案

### 1、引入依赖
```
<dependency>
    <groupId>com.github.dingey</groupId>
    <artifactId>spring-cloud-gray</artifactId>
    <version>0.2.2</version>
</dependency>
```
### 2、灰度配置

#### 2.1、开启灰度配置
```
gray:
  enable: true
  isolation: true
```
isolation是否隔离灰度应用，false下没有版本匹配的实例时会调用所有实例，true没有匹配实例时直接返回503。
#### 2.2、eureka配置方式
```
eureka.instance.metadata-map.version: 1.0
```
#### 2.3、nacos配置方式
```
spring.cloud.nacos.discovery.metadata.version: 1.0
```
也可以通过nacos控制台配置实例的元数据信息

### 3、灰度调用
请求头里加header字段，带上灰度的版本号
```
curl --request GET 'http://localhost:8090' --header 'version: 1.0'
```
### 4、版本依赖

| spring boot   | spring cloud  |  nacos  | 版本 | 状态 |
| ---- | ---- | ---- |---- |---- |
| 2.x.x.RELEASE | Greenwich.x | 2.1.x.RELEASE | 0.2.0 | ✅ |
| 2.x.x.RELEASE | Hoxton.x | 2.2.x.RELEASE | 0.2.2 | 🏗 |

### 5、原理
通过修改ribbon的负载均衡,实现<code>IRule</code>接口来支持灰度的请求分发。
