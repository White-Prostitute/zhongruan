package edu.scu.zhongruan;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import edu.scu.zhongruan.entity.DoctorEntity;
import edu.scu.zhongruan.entity.PatientEntity;
import edu.scu.zhongruan.service.TaskService;
import edu.scu.zhongruan.vo.TaskVo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.GetDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Objects;

@SpringBootTest
class ZhongruanApplicationTests {

    @Autowired
    TaskService service;

    @Autowired
    CuratorFramework curatorFramework;

    @Test
    void contextLoads() throws Exception {
        System.out.println(Objects.isNull(curatorFramework));
        GetDataBuilder data = curatorFramework.getData();
        byte[] bytes = data.forPath("/test/age");
        System.out.println(new String(bytes));
    }


    public TaskVo mockTaskVo(){
        TaskVo taskVo = new TaskVo();
        taskVo.setId("123456");
        taskVo.setModelType("modelType");
        taskVo.setBeginTime(new Date());
        taskVo.setEndTime(new Date());
        taskVo.setCostTime(100);
        taskVo.setCreatorId("creatorId");
        taskVo.setPatientId("patientId");
        taskVo.setFileType("fileType");
        taskVo.setAdvice("advice");
        taskVo.setNotes("notes");
        taskVo.setStatusDesc("等待结果");
        JSONArray array =  new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("index_from_0", 1);
        obj.put("labelType", "daw");
        obj.put("surfaceArea", 189.103);
        obj.put("volume", 190.098);
        array.add(obj);
        taskVo.setMeasurementData(array);
        taskVo.setStatus(0);
        DoctorEntity doctor = new DoctorEntity();
        doctor.setName("doctorName");
        doctor.setAccount("doctorAccount");
        doctor.setPassword("doctorPassword");
        taskVo.setDoctor(doctor);
        PatientEntity patient = new PatientEntity();
        patient.setId("patientId");
        patient.setName("patientName");
        patient.setSex(0);
        patient.setSexDesc("女");
        patient.setBirthday(new Date());
        patient.setPhoneNumber("123456789");
        patient.setAddress("address");
        patient.setLastModify(new Date());
        taskVo.setPatient(patient);
        return taskVo;
    }
}
