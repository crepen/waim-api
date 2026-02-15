package com.waim.core.domain.user.model.dto.enumable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserState {
    PENDING,
    ACTIVE,
    WITHDRAWN,
    SUSPENDED
}