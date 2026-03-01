package com.waim.module.data.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FindUserProp {
    private String userUid;
}
