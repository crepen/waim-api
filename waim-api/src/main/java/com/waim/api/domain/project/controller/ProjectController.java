package com.waim.api.domain.project.controller;

import com.waim.api.common.model.response.BaseResponse;
import com.waim.api.domain.project.model.request.AddProjectRequest;
import com.waim.core.common.util.jwt.model.JwtUserDetail;
import com.waim.core.domain.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    @PutMapping()
    @Operation(
            summary = "프로젝트 생성",
            description = "로그인 사용자의 프로젝트 생성"
    )
    public ResponseEntity<?> addProject(
            @AuthenticationPrincipal JwtUserDetail userDetail,
            @RequestBody AddProjectRequest reqBody
    ){
        projectService.addProject(
                reqBody.getProjectName(),
                reqBody.getProjectAlias(),
                userDetail.getUserUid()
        );

        return ResponseEntity.ok()
                .body(
                        BaseResponse.Success.builder()
                                .build()
                );
    }
}
