package com.waim.core.domain.configure.model.event;

import lombok.Builder;

@Builder
public record UpdateGlobalConfigEvent(
        String key ,
        String value,
        boolean isEncrypt
) {}
