package com.jadyer.seed.boot.remoting.server;

/**
 * 封装springhttpinvoker
 * -------------------------------------------------------------------------------------------------
 * 注意：使用springhttpinvoker过程中，实体类必须实现Serializable接口，因为它要走Java序列化和反序列化
 * -------------------------------------------------------------------------------------------------
 * 关于远程调用，Spring支持RMI服务和基于HTTP的服务（Hessian/Burlap）
 * 虽然RMI使用Java标准的对象序列化，但它很难穿越防火墙
 * Hessian/Burlap虽能穿越防火墙，但使用的是自己私有的一套对象序列化机制
 * 于是Spring的httpinvoker应运而生
 * 其作为Spring框架的一部分，通过Java序列化机制（让程序员高兴的事）执行基于HTTP的远程调用（让防火墙高兴的事）
 * 这使得springhttpinvoker成为一个引人注目的对RMI或Hessian/Burlap的比较完美的远程调用解决方案
 * 不过它有个重大限制：它是一个只在Spring框架中提供的远程调用解决方案，所以客户端和服务端也必须是Spring应用
 * -------------------------------------------------------------------------------------------------
 * 补充一下：关于hession和fastjson的对比
 * 用温少的话说就是：hession很挫的，如果连hession都比不过，还敢号称fast么？
 * 参见https://www.oschina.net/question/1999248_2154688
 * -------------------------------------------------------------------------------------------------
 * 关于服务端输出SpringHttpInvoker服务的写法，示例如下
 * public class FileInfoVO implements Serializable {
 *     private static final long serialVersionUID = 8286183711093206236L;
 *     //setter和getter略
 *     private Integer id;
 *     private String name;
 * }
 * public interface IFSService {
 *     FileInfoVO getById(int id);
 * }
 * @RemoteService(IFSService.class)
 * public class IFSServiceImpl implements IFSService {
 *     @Resource
 *     private FileInfoRepository fileInfoRepository;
 *     @Override
 *     public FileInfoVO getById(int id){
 *         return BeanUtil.copyProperties(this.fileInfoRepository.findOne(id), FileInfoVO.class);
 *     }
 * }
 * -------------------------------------------------------------------------------------------------
 * 给客户端打包SDK的时候，只需要把FileInfoVO.java以及IFSService.java打包进去，就可以了
 * 所以可以把VO类和接口类放到一个单独的maven-module里面，这样会编译成一个独立jar，再使用下面的命令打包即可
 * mvn deploy:deploy-file -Dversion=1.2 -Dfile=sdk-1.2.jar -Dsources=sdk-1.2-sources.jar -DgroupId=com.jadyer.seed -DartifactId=seed-sdk -Dpackaging=jar -DrepositoryId=msxf-nexus-release -Durl=http://nexus.jadyer.cn/nexus/content/repositories/releases/
 * <dependency>
 *     <groupId>com.jadyer.seed</groupId>
 *     <artifactId>seed-sdk</artifactId>
 *     <version>1.2</version>
 * </dependency>
 * 打包之前要先配置私服用户密码：D:\Develop\Code\mvnrepo\settings.xml
 * <servers>
 *     <server>
 *         <id>msxf-nexus-release</id>
 *         <username>jadyer</username>
 *         <password>111111</password>
 *     </server>
 *     <server>
 *         <id>msxf-nexus-snapshot</id>
 *         <username>jadyer</username>
 *         <password>111111</password>
 *     </server>
 * </servers>
 * -------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2016/11/22 19:42.
 */
//@Configuration
@RemoteServiceScan("com.jadyer.seed")
public class RemotingConfiguration {}