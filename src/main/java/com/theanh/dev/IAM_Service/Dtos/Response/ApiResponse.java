package com.theanh.dev.IAM_Service.Dtos.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code;
    private String status;
    private Timestamp timestamp;
    private String message;
    private T data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    public class PageApiResponse<T> extends ApiResponse<List<T>> {
        private PageableResponse page = new PageableResponse();
        @Data
        public static class PageableResponse implements Serializable {
            private int pageIndex;
            private int pageSize;
            private long total;
        }
    }

    public static final String ASC_SYMBOL = "asc";
    public static final String DESC_SYMBOL = "desc";

    @Min(value = 1, message = "Page index must be greater than 0")
    @Max(value = 1000, message = "Page index be less than 1000")
    @Builder.Default protected int pageIndex = 1;

    @Min(value = 1, message = "Page size must be greater than 0")
    @Max(value = 500, message = "Page size must be less than or equal to 500")
    @Builder.Default protected int pageSize = 30;

    protected String sortBy;
}
