package edu.scu.zhongruan.controller.request;

import lombok.Data;

@Data
public class QueryPatientRequest {

    private Integer pageIndex;

    private Integer pageSize;

    private Filter filter;

    @Data
    public static class Filter{
        private String name;

        private Byte sex;

        private String address;
    }

}
