package com.waim.api.domain.project.model.request;

import lombok.*;
import org.springframework.web.bind.annotation.BindParam;

public record SearchProjectRequest (
        @BindParam("keyword")
        String searchKeyword
){}
