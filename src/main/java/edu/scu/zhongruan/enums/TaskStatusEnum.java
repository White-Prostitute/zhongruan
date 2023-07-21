package edu.scu.zhongruan.enums;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

public enum TaskStatusEnum {
    WAITING_CALCULATE(0, "等待计算"),
    RECEIVING_RESPONSE(1, "等待结果回传"),
    COMPLETE(2, "任务已完成"),
    ERROR(3, "任务异常");

    private final int code;
    private final String desc;

    private static final Map<Integer, String> statusMap = new HashMap<>();

    static {
        TaskStatusEnum[] values = TaskStatusEnum.values();
        for (TaskStatusEnum value : values) {
            statusMap.put(value.getCode(), value.getDesc());
        }
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDesc(int code){
        return statusMap.get(code);
    }

    TaskStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
