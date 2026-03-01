package com.waim.module.data.domain.project;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
public class SearchProjectProp {
    private String keyword;
    private String searchUserUid;
    private Pageable pageable;
    private boolean isAdmin;
    private List<ProjectStatus> roles;
}
