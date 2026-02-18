package com.waim.core.common.model.dto;

import lombok.Getter;

public record ConfigItem(
        String key,
        String value,
        boolean secure
){}
