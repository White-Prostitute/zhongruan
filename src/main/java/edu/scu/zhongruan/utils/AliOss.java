package edu.scu.zhongruan.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.InputStream;

@Slf4j
public class AliOss {

    // 填写Bucket名称，例如examplebucket。
    private static String bucketName = "scu-zrb-a3";
    // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
    private static String endpoint = "oss-cn-chengdu.aliyuncs.com";
    // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
    private static String accessKeyId = "LTAI5tEsNfqsEQDsbGZ5HUEG";
    private static String accessKeySecret = "WDOt6FfqupM7K1zPUWqyehG8sHpyfv";

    //初始化oss信息
    //TODO 如果拉取配置时间过长就换一种配置拉取策略
//    static {
//        getConfigInfo();
//    }

    //从zookeeper拉取配置信息
    public static void getConfigInfo(){
        try {
            bucketName = ZooKeeperUtil.getOssBucketName();
            endpoint = ZooKeeperUtil.getOssEndpoint();
            accessKeyId = ZooKeeperUtil.getOssAccessKeyId();
            accessKeySecret = ZooKeeperUtil.getOssAccessSecret();
            log.info("拉取oss配置 bucket name {} endpoint {} access key {} secret {}",
                    bucketName, endpoint, accessKeyId, accessKeySecret);
        } catch (Exception e) {
            log.error("拉取oss配置错误", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 将文件上传到阿里云对象存储服务
     * @param fis 需要上传的文件文件流
     * @param fileName  上传时的文件名
     */
    public static void upload(InputStream fis, String fileName) throws FileNotFoundException {
        //创建OSS对象
        getConfigInfo();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            PutObjectResult result = ossClient.putObject(bucketName, fileName, fis);
        }
        finally {
            //OSS关闭服务，不然会造成OOM
            ossClient.shutdown();
        }
    }

    public static InputStream getFile(String fileName){
        //创建OSS对象
        getConfigInfo();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        OSSObject object = ossClient.getObject(bucketName, fileName);
        return object.getObjectContent();
    }

    public static void deleteFile(String fileName){
        getConfigInfo();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.deleteObject(bucketName, fileName);
    }

}
