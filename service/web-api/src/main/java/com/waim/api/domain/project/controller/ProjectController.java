package com.waim.api.domain.project.controller;

import com.waim.api.common.model.CommonPageable;
import com.waim.api.common.model.response.BasePageableResponse;
import com.waim.api.common.model.response.BaseResponse;
import com.waim.api.domain.project.model.request.AddProjectRequest;
import com.waim.api.domain.project.model.request.SearchProjectRequest;
import com.waim.core.common.model.error.WAIMException;
import com.waim.core.common.util.jwt.model.JwtUserDetail;
import com.waim.core.domain.project.model.dto.ProjectData;
import com.waim.core.domain.project.model.dto.ProjectSearchOption;
import com.waim.core.domain.project.model.entity.ProjectEntity;
import com.waim.core.domain.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping()
    @Operation(
            summary = "프로젝트 검색"
    )
    public ResponseEntity<?> searchProject(
            @AuthenticationPrincipal JwtUserDetail userDetail,
            @PageableDefault(size = 10 , sort = "updateAt" , direction = Sort.Direction.DESC)Pageable pageable,
            SearchProjectRequest reqParam
    ) {

        Page<ProjectEntity> projectDataList = projectService.searchProjectPageable(
                ProjectSearchOption.builder()
                        .searchKeyword(reqParam.searchKeyword())
                        .searchUserUid(userDetail.getUserUid())
                        .pageable(pageable)
                        .build()
        );


        return ResponseEntity.ok(
                BasePageableResponse.Success.builder()
                        .result(projectDataList.map(ProjectEntity::castDataDto).get())
                        .pageable(CommonPageable.cast(projectDataList))
                        .build()
        );
    }



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



    @GetMapping("{projectOwnerUserId}/{projectAlias}")
    public ResponseEntity<?> getProject(
            @AuthenticationPrincipal JwtUserDetail userDetail,
            @PathVariable String projectAlias,
            @PathVariable String projectOwnerUserId
    ){
        var projectData = projectService.getProject(projectOwnerUserId, projectAlias);

        if(projectData.isEmpty()){
            // TODO : Not found Exception
            throw new WAIMException();
        }

        return ResponseEntity.ok()
                .body(
                        BaseResponse.Success.builder()
                                .result(projectData.get().castDataDto())
                                .build()
                );
    }


    @GetMapping("{projectUid}")
    public ResponseEntity<?> getProject(
            @AuthenticationPrincipal JwtUserDetail userDetail,
            @PathVariable String projectUid
    ){
        var projectData = projectService.getProject(projectUid);

        if(projectData.isEmpty()){
            // TODO : Not found Exception
            throw new WAIMException();
        }

        return ResponseEntity.ok()
                .body(
                        BaseResponse.Success.builder()
                                .result(projectData.get().castDataDto())
                                .build()
                );
    }

    @DeleteMapping("{projectUid}")
    public ResponseEntity<?> deleteProject(
            @AuthenticationPrincipal JwtUserDetail userDetail,
            @PathVariable String projectUid
    ){
        projectService.removeProject(
                projectUid,
                userDetail.getUserUid()
        );

        return ResponseEntity.ok()
                .body(
                        BaseResponse.Success.builder()
                                .build()
                );
    }
}
