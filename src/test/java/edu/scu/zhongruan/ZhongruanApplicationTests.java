package edu.scu.zhongruan;

import com.alibaba.fastjson.JSONObject;
import edu.scu.zhongruan.controller.dto.TaskPostDataDto;
import edu.scu.zhongruan.entity.TaskEntity;
import edu.scu.zhongruan.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ZhongruanApplicationTests {

    @Autowired
    TaskService service;

    @Test
    void contextLoads() {
        String json = "{\n" +
                "    \"id\": \"Bf6BE2b9-10BC-7BD3-Cdb0-55fDEbb394DF\",\n" +
                "    \"measurement\": [\n" +
                "        {\n" +
                "            \"index_from_0\": 93,\n" +
                "            \"labelType\": \"ssdxtgy\",\n" +
                "            \"surfaceArea\": 50460.226323521725,\n" +
                "            \"volume\": 524696.432868692\n" +
                "        },\n" +
                "        {\n" +
                "            \"index_from_0\": 94,\n" +
                "            \"labelType\": \"tctjjvphd\",\n" +
                "            \"surfaceArea\": 13498.9654,\n" +
                "            \"volume\": 252310.09227982114\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        TaskPostDataDto taskPostDataDto = JSONObject.parseObject(json, TaskPostDataDto.class);
        TaskEntity entity = new TaskEntity();
        entity.setMeasurement(JSONObject.toJSONString(taskPostDataDto.getMeasurement()));
        System.out.println(entity);
    }

}
