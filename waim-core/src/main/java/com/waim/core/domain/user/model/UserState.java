package com.waim.core.domain.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserState {
    PENDING("PENDING"),
    ACTIVE("ACTIVE"),
    WITHDRAWN("WITHDRAWN"),
    SUSPENDED("SUSPENDED");

    private final String text;
}