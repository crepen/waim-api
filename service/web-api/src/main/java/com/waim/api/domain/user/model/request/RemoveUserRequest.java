package com.waim.api.domain.user.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemoveUserRequest {
    @JsonProperty("user_uid")
    private String userUid;

}
