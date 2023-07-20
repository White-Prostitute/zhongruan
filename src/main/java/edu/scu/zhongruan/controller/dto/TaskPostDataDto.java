package edu.scu.zhongruan.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskPostDataDto {

    private String id;

    private List<Measurement> measurement;

    @Data
    public static class Measurement{
        private Integer index_from_0;

        private String labelType;

        private Double surfaceArea;

        private Double volume;
    }

}
