package edu.scu.zhongruan.controller.request;

import lombok.Data;

@Data
public class QueryTaskRequest {

    private Integer pageIndex;

    private Integer pageSize;

    private Filter filter;


    @Data
    public static class Filter{

        private String creatorId;

        private String creatorName;

        private Integer modeType;

    }

}
