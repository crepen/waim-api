package com.waim.api.domain.configure.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGlobalConfigRequest {
    private String key;
    private String value;

    @JsonProperty("is_encrypt")
    @Builder.Default
    private Boolean encrypt = false;

    @JsonProperty("is_encrypt")
    public Boolean isEncrypt() {
        return encrypt != null && encrypt;
    }

}
