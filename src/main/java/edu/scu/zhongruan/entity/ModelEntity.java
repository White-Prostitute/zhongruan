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
@TableName("model")
public class ModelEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private String name;
	/**
	 * 
	 */
	private String desc;
	/**
	 * 
	 */
	@TableId
	private Integer id;

}
