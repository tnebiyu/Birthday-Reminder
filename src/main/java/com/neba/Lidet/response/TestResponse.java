package com.neba.Lidet.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestResponse {
    private Data data;
    private boolean error;
    private String error_msg;



    @lombok.Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Data{
        private String name;

    }
}
