package com.waim.core.domain.project.model.dto;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

@Builder
public record ProjectSearchOption(
        String searchKeyword,
        String searchUserUid,
        Pageable pageable
) {}
