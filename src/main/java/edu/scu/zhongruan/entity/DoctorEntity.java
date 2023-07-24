package edu.scu.zhongruan.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author liguohua
 * @email 3537136394@qq.com
 * @date 2023-06-10 14:56:17
 */
@Data
@TableName("doctor")
public class DoctorEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private String name;
	/**
	 * 
	 */
	@TableId
	private String account;
	/**
	 * 
	 */
	private String password;

	private String workplace;

	private String post;

	private String address;

	private String avatarUrl;

	private String phoneNumber;

}
