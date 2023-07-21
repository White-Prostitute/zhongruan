package edu.scu.zhongruan.vo;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonFormat;
import edu.scu.zhongruan.entity.DoctorEntity;
import edu.scu.zhongruan.entity.PatientEntity;
import edu.scu.zhongruan.entity.TaskEntity;
import edu.scu.zhongruan.enums.TaskStatusEnum;
import lombok.Data;

import java.util.Date;

@Data
public class TaskVo {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private String id;
    /**
     *
     */
    private String modelType;
    /**
     *
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime;
    /**
     *
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     *
     */
    private Integer costTime;
    /**
     *
     */
    private String creatorId;
    /**
     *
     */
    private String patientId;
    /**
     * 文件类型
     */
    private String fileType;
    /**
     * 医嘱
     */
    private String advice;
    /**
     * 备注
     */
    private String notes;
    /**
     * 测量数据
     */
    private JSONArray measurementData;
    /**
     * 任务状态 0:处理中 1:数据传输中 2:完成 3:异常
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 医生信息
     */
    private DoctorEntity doctor;

    /**
     * 病人信息
     */
    private PatientEntity patient;

    public void buildFromEntity(TaskEntity entity){
        this.id = entity.getId();
        this.measurementData = entity.getMeasurementData();
        this.creatorId = entity.getCreatorId();
        this.advice = entity.getAdvice();
        this.notes = entity.getNotes();
        this.status = entity.getStatus();
        this.statusDesc = TaskStatusEnum.getDesc(this.status);
        this.fileType = entity.getFileType();
        this.beginTime = entity.getBeginTime();
        this.endTime = entity.getEndTime();
        this.costTime = entity.getCostTime();
        this.modelType = entity.getModelType();
        this.patientId = entity.getPatientId();
    }

}
