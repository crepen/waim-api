package com.waim.api.domain.auth.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTokenUserDataResponse {
    @JsonProperty("unique_id")
    private String uniqueId;
    private String id;
    private String name;
    private String email;
    private List<String> roles;
}
