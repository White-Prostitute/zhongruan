package edu.scu.zhongruan.entity;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * 
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-06-10 14:56:17
 */
@Data
@TableName("task")
public class TaskEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
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
	private String measurement;
	/**
	 * 测量数据
	 */
	@TableField(exist = false)
	private JSONArray measurementData;
	/**
	 * 任务状态 0 : 处理中， 1 ： 数据传输中， 2 ： 完成
	 */
	@TableField(exist = false)
	private Integer status;

	public void setMeasurement(String measurement){
		this.measurement = measurement;
		measurementData = JSONObject.parseArray(measurement);
	}

}
