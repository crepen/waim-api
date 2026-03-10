package com.waim.api.domain.group.model.request;

import org.springframework.web.bind.annotation.BindParam;

public record SearchGroupRequest(
        @BindParam("keyword")
        String keyword
) {}
