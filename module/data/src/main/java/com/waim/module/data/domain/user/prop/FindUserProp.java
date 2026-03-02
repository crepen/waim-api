package com.waim.module.data.domain.user.prop;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FindUserProp {
    private String userUid;
}
