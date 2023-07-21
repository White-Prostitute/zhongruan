package edu.scu.zhongruan.controller.dto;

import lombok.Data;

//python端回传给java端文件数据
@Data
public class TaskCompleteDto {

    private String id;

    private String base64fileData;

    private String fileType;

}
