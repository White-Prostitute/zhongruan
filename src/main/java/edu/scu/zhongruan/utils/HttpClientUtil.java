package edu.scu.zhongruan.utils;


import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.Map;

public class HttpClientUtil {

    public static String postJson(Map<String, String> params, String url) throws IOException {
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
        JSONObject jsonBody = new JSONObject();
        params.forEach(jsonBody::put);
        StringEntity entity = new StringEntity(jsonBody.toJSONString());
        post.setEntity(entity);
        HttpResponse execute = client.execute(post);
        return UsuUtil.readStrFromInputStream(execute.getEntity().getContent());
    }

}
