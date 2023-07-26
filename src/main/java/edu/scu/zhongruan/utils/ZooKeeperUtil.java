package edu.scu.zhongruan.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZooKeeperUtil implements InitializingBean {

    @Value("${zookeeper.connect-string}")
    private String connectString;

    @Value("${zookeeper.session-timeout}")
    private int sessionTimeout;

    @Value("${zookeeper.connection-timeout}")
    private int connectionTimeout;

    private static CuratorFramework curatorFramework;

    private static final String BASE_NODE = "/zhongruan";


    @Override
    public void afterPropertiesSet() throws Exception {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(sessionTimeout)
                .connectionTimeoutMs(connectionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        curatorFramework.start();
        curatorFramework.create().creatingParentContainersIfNeeded().forPath(BASE_NODE);
    }

    //获取oss bucket名称
    public static String getOssBucketName() throws Exception {
        byte[] bytes = curatorFramework.getData().forPath(BASE_NODE + "/oss/bucket_name");
        return new String(bytes);
    }

    //获取oss endpoint
    public static String getOssEndpoint() throws Exception {
        byte[] bytes = curatorFramework.getData().forPath(BASE_NODE + "/oss/endpoint");
        return new String(bytes);
    }

    //获取oss access key
    public static String getOssAccessKeyId() throws Exception {
        byte[] bytes = curatorFramework.getData().forPath(BASE_NODE + "/oss/access_key_id");
        return new String(bytes);
    }

    //获取oss access key secret
    public static String getOssAccessSecret() throws Exception {
        byte[] bytes = curatorFramework.getData().forPath(BASE_NODE + "/oss/access_key_secret");
        return new String(bytes);
    }


}
