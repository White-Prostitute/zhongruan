package edu.scu.zhongruan;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import edu.scu.zhongruan.controller.TaskController;
import edu.scu.zhongruan.entity.DoctorEntity;
import edu.scu.zhongruan.entity.PatientEntity;
import edu.scu.zhongruan.service.TaskService;
import edu.scu.zhongruan.utils.R;
import edu.scu.zhongruan.vo.TaskVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@SpringBootTest
class ZhongruanApplicationTests {

    @Autowired
    TaskService service;

    @Autowired
    TaskController controller;


    @Test
    void contextLoads() {
        DoctorEntity entity = new DoctorEntity();
        entity.setAccount("1");
        List<TaskVo> taskVos = service.allTask(entity);
        for (TaskVo vo : taskVos) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区为上海
            String localTime = sdf.format(vo.getBeginTime());
            System.out.println(localTime); // 输出本地时间
        }
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
