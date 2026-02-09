package com.waim.api.domain.configure.model.response;

import lombok.Builder;
import lombok.Getter;

@Builder
public record ConfigResponse (
        String key,
        String value
){}
