package edu.scu.zhongruan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * 
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-06-10 14:56:17
 */
@Data
@TableName("patient")
public class PatientEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private String id;
	/**
	 * 
	 */
	private String name;
	/**
	 * 0 -> 女 ； 1 -> 男
	 */
	private Integer sex;
	/**
	 * 性别描述
	 */
	@TableField(exist = false)
	private String sexDesc;
	/**
	 * 
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date birthday;
	/**
	 * 
	 */
	private String phoneNumber;
	/**
	 * 
	 */
	private String address;
	/**
	 * 最新修改时间
	 */
	private Date lastModify;

	public void setSex(Integer sex){
		this.sex = sex;
		this.sexDesc = sex == 0 ? "女":"男";
	}

	public void setSexDesc(String sexDesc){
		this.sexDesc = sexDesc;
		this.sex = "男".equals(sexDesc)?1:0;
	}
}
