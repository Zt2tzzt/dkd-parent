# 人员管理之阿里云 OSS 应用

在若依框架目前的实现中，默认是把图片存储到了服务器本地的目录，通过服务进行访问，

这样做存储的是比较省事，但是缺点也有很多：

- 硬件与网络要求：服务器通常需要高性能的硬件和稳定的网络环境，以保证文件传输的效率和稳定性。这可能会增加硬件和网络资源的成本和维护难度。
- 管理难度：服务器目录需要管理员进行配置和管理，包括权限设置、备份策略等。如果管理不善或配置不当，可能会引发一些安全问题和性能问题。
- 性能瓶颈：如果服务器处理能力不足或网络带宽不够，可能会导致性能瓶颈，影响文件上传、下载和访问的速度。
- 单点故障风险：服务器故障可能导致所有存储在其上的文件无法访问，尽管可以通过备份和冗余措施来降低这种风险，但单点故障的风险仍然存在。

为了解决上述问题呢，通常有两种解决方案：

- 自己搭建存储服务器，如：fastDFS 、MinIO
- 使用现成的云服务，如：阿里云，腾讯云，华为云

## 一、阿里云 OSS 介绍

阿里云对象存储 OSS（Object Storage Service），是一款海量、安全、低成本、高可靠的云存储服务。

使用 OSS，您可以通过网络，随时存储和调用包括文本、图片、音频和视频等在内的各种文件。

在项目中，使用第三方服务的通用思路，如下图所示：

```mermaid
graph LR
A[准备工作] -->B[参照官方 SDK 编写入门程序]
    B --> C[集成使用]
```

> SDK（Software Development Kit）软件开发工具包，包括辅助软件开发的依赖（比如 jar 包）、代码示例等等；

阿里云 OSS 对象存储服务，具体的使用步骤如下：

```mermaid
graph LR
A[注册阿里云（实名认证）] -->B[充值]
    B --> C[开通对象存储（OSS）服务]
    C --> D[创建 Bucket]
    D --> E[获取 AccessKey]
    E --> F[参照官方 SDK 编写入门程序]
    F --> G[案例继承]
```

Bucket 存储空间，是用户用于存储对象（Object）（即文件）的容器，所有的对象（文件），都必须隶属于某个存储空间。

在 aplication-dev.yaml 

dkd-admin/src/main/resources/application-dev.yml

```yaml
dkd:
  alioss:
    access-key-id: xxxxxx
    access-key-secret: xxxxxx
    bucket-name: xxxxxx
    endpoint: xxxxxx
```

在 dkd-common 模块的 pom.xml 文件中，引入依赖

dkd-common/pom.xml

```xml
<!-- 阿里云 OSS -->
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>3.17.4</version>
</dependency>
<!-- 以下三个坐标，若依默认集成了，不导入仪可以-->
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.1</version>
</dependency>
<dependency>
    <groupId>javax.activation</groupId>
    <artifactId>activation</artifactId>
    <version>1.1.1</version>
</dependency>
<!-- no more than 2.3.3-->
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>2.3.3</version>
</dependency>
```

参考[文档](https://help.aliyun.com/zh/oss/developer-reference/simple-upload-11?spm=a2c4g.11186623.help-menu-31815.d_3_2_0_4_0_0.59f856e5pAb3Qc)，编写测试类：

dkd-common/src/test/java/com/dkd/common/test/Demo.java

## 二、X File Store

[X File Store](https://x-file-storage.xuyanwu.cn/#/) 旨在用一行代码将文件存储到本地、FTP、SFTP、WebDAV、阿里云 OSS、华为云 OBS、七牛云 Kodo、腾讯云 COS、百度云 BOS、又拍云 USS、MinIO、 Amazon S3、GoogleCloud Storage、FastDFS、 Azure Blob Storage、Cloudflare R2、金山云 KS3、美团云 MSS、京东云 OSS、天翼云 OOS、移动 云EOS、沃云 OSS、 网易数帆 NOS、Ucloud US3、青云 QingStor、平安云 OBS、首云 OSS、IBM COS、其它兼容 S3 协议的存储平台。

在 dkd-common 模块的 pom.xml 文件中，引入依赖

dkd-common/pom.xml

```xml
<!-- x-file-storage-->
<dependency>
    <groupId>org.dromara.x-file-storage</groupId>
    <artifactId>x-file-storage-spring</artifactId>
    <version>2.2.1</version>
</dependency>

<!-- 阿里云 OSS -->
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>3.17.4</version>
</dependency>
<!-- 以下三个坐标，若依默认集成了，不导入仪可以-->
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.1</version>
</dependency>
<dependency>
    <groupId>javax.activation</groupId>
    <artifactId>activation</artifactId>
    <version>1.1.1</version>
</dependency>
<!-- no more than 2.3.3-->
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>2.3.3</version>
</dependency>
```

在 application.yml 中，进行配置：

dkd-admin/src/main/resources/application.yml

```yaml
dromara:
  x-file-storage: #文件存储配置
    default-platform: aliyun-oss-1 #默认使用的存储平台
    thumbnail-suffix: ".min.jpg" #缩略图后缀，例如【.min.jpg】【.png】
    # 对应平台的配置写在这里，注意缩进要对齐
    aliyun-oss:
      - platform: aliyun-oss-1 # 存储平台标识
        enable-storage: true  # 启用存储
        access-key: ${dkd.alioss.access-key-id}
        secret-key: ${dkd.alioss.access-key-secret}
        end-point: ${dkd.alioss.endpoint}
        bucket-name: ${dkd.alioss.bucket-name}
        domain: ${dkd.alioss.domain} # 访问域名，注意“/”结尾，例如：https://abc.oss-cn-shanghai.aliyuncs.com/
        base-path: dkd-images/ # 基础路径
```

在启动类上，增加 `EnableFileStorage` 注解。

dkd-admin/src/main/java/com/dkd/DkdApplication.java

```java
@EnableFileStorage
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class DkdApplication
{
  ……
}
```

参考[文档示例](https://x-file-storage.xuyanwu.cn/#/快速入门?id=上传)；

找到文件上传的控制器类 `CommonController`，以及对应的方法：

dkd-admin/src/main/java/com/dkd/web/controller/common/CommonController.java

```java
/**
 * 通用上传请求（单个）
 */
@PostMapping("/upload")
public AjaxResult uploadFile(MultipartFile file) throws Exception
{
    try
    {
        // 上传文件路径
        String filePath = RuoYiConfig.getUploadPath();
        // 上传并返回新文件名称
        String fileName = FileUploadUtils.upload(filePath, file);
        String url = serverConfig.getUrl() + fileName;
        AjaxResult ajax = AjaxResult.success();
        ajax.put("url", url);
        ajax.put("fileName", fileName);
        ajax.put("newFileName", FileUtils.getName(fileName));
        ajax.put("originalFilename", file.getOriginalFilename());
        return ajax;
    }
    catch (Exception e)
    {
        return AjaxResult.error(e.getMessage());
    }
}
```

将上方代码进行改造：

- 注入 X File Storage 的 Bean 对象
- 改造上方代码的逻辑。

dkd-admin/src/main/java/com/dkd/web/controller/common/CommonController.java

```java
@Autowired
private FileStorageService fileStorageService;//注入实列

……

/**
 * 通用上传请求（单个）
 */
@PostMapping("/upload")
public AjaxResult uploadFile(MultipartFile file) throws Exception
{
    try
    {
        // 上传文件路径
        //String filePath = RuoYiConfig.getUploadPath();
        // 上传并返回新文件名称
        //String fileName = FileUploadUtils.upload(filePath, file);
        //String url = serverConfig.getUrl() + fileName;

        // 指定 OSS 保存文件路径：dkd-images/2024/12/09/文件名
        String objectName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/";

        FileInfo fileInfo = fileStorageService.of(file)
                .setPath(objectName) //保存到相对路径下，为了方便管理，不需要可以不写
                .upload();

        AjaxResult ajax = AjaxResult.success();
        ajax.put("url", fileInfo.getUrl());
        ajax.put("fileName", fileInfo.getUrl()); // 注意：这里的值需要改为 url，因为前端的访问地址会做一个判断，如果以 http 开头，就直接显示此图片。
        ajax.put("newFileName", fileInfo.getUrl());
        ajax.put("originalFilename", file.getOriginalFilename());
        return ajax;
    }
    catch (Exception e)
    {
        return AjaxResult.error(e.getMessage());
    }
}
```

前端相关文件：

src/components/ImageUpload/index.vue

```javascript
watch(() => props.modelValue, val => {
  if (val) {
    // 首先将值转为数组
    const list = Array.isArray(val) ? val : props.modelValue.split(",");
    // 然后将数组转为对象数组
    fileList.value = list.map(item => {
      if (typeof item === "string") {
        if (item.indexOf(baseUrl) === -1   && item.indexOf("http") === -1) {
          item = { name: baseUrl + item, url: baseUrl + item };
        } else {
          item = { name: item, url: item };
        }
      }
      return item;
    });
  } else {
    fileList.value = [];
    return [];
  }
},{ deep: true, immediate: true });
```

