package com.waim.module.data.domain.group;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@Builder
public class SearchGroupProp {
    private String keyword;
    private Pageable pageable;
}
