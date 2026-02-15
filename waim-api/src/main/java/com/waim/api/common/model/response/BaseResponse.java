package com.waim.api.common.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


public class BaseResponse {
    @Getter
    @Setter
    @SuperBuilder
    public static class Error extends Common {
        @Builder.Default
        private boolean state = false;

        private String code;
    }

    @Getter
    @Setter
    @SuperBuilder
    public static class Success extends Common {
        @Builder.Default
        private boolean state = true;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Object result;

    }


    @Getter
    @Setter
    @SuperBuilder
    private static class Common{
        private boolean state;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String message;

        @Builder.Default
        private Long timestamp = System.currentTimeMillis();
    }
}
