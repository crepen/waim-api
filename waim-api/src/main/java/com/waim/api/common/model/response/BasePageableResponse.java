package com.waim.api.common.model.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.waim.api.common.model.CommonPageable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

public class BasePageableResponse {

    @Getter
    @Setter
    @SuperBuilder
    public static class Success extends BaseResponse.Success {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CommonPageable pageable;
    }

    @Getter
    @Setter
    @SuperBuilder
    public static class Error extends BaseResponse.Error {}
}
