package com.waim.api.domain.project.controller;

import com.waim.api.common.model.CommonPageable;
import com.waim.api.common.model.response.BasePageableResponse;
import com.waim.api.common.model.response.BaseResponse;
import com.waim.api.domain.project.model.request.AddProjectRequest;
import com.waim.api.domain.project.model.request.SearchProjectRequest;
import com.waim.module.core.domain.project.model.entity.ProjectEntity;
import com.waim.module.core.domain.project.model.error.ProjectNotFoundException;
import com.waim.module.core.domain.project.service.ProjectService;
import com.waim.module.data.common.security.SecurityUserDetail;
import com.waim.module.data.domain.project.AddProjectProp;
import com.waim.module.data.domain.project.ProjectData;
import com.waim.module.data.domain.project.RemoveProjectProp;
import com.waim.module.data.domain.project.SearchProjectProp;
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

import java.util.List;
import java.util.Optional;

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
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PageableDefault(size = 10 , sort = "updateAt" , direction = Sort.Direction.DESC)Pageable pageable,
            SearchProjectRequest reqParam
    ) {

        Page<ProjectEntity> projectDataList = projectService.searchProject(
                SearchProjectProp.builder()
                        .keyword(reqParam.searchKeyword())
                        .searchUserUid(userDetail.getUniqueId())
                        .pageable(pageable)
                        .build()
        );

        List<ProjectData> resList = projectDataList.map(
                x -> ProjectData.builder()
                        .uid(x.getUid())
                        .projectName(x.getProjectName())
                        .projectAlias(x.getProjectAlias())
                        .projectOwnerName(x.getProjectOwner().getUserName())
                        .projectOwnerUid(x.getProjectOwner().getUid())
                        .createTimestamp(
                                x.getCreateAt()
                                        .toInstant().toEpochMilli()
                        )
                        .updateTimestamp(
                                x.getUpdateAt()
                                        .toInstant().toEpochMilli()
                        )
                        .build()
        ).stream().toList();


        return ResponseEntity.ok(
                BasePageableResponse.Success.builder()
                        .result(resList)
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
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @RequestBody AddProjectRequest reqBody
    ){
        projectService.addProject(
                AddProjectProp.builder()
                        .projectName(reqBody.getProjectName())
                        .projectAlias(reqBody.getProjectAlias())
                        .projectOwnerUserUid(userDetail.getUniqueId())
                        .build()
        );

        return ResponseEntity.ok()
                .body(
                        BaseResponse.Success.builder()
                                .build()
                );
    }


    @GetMapping("{projectUid}")
    public ResponseEntity<?> getProject(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PathVariable String projectUid
    ) {

        Optional<ProjectEntity> findProject = projectService.getProjectInfo(projectUid, userDetail.getUniqueId());

        if (findProject.isEmpty()) {
            throw new ProjectNotFoundException();
        }

        var result = ProjectData.builder()
                .uid(findProject.get().getUid())
                .projectName(findProject.get().getProjectName())
                .projectAlias(findProject.get().getProjectAlias())
                .projectOwnerName(findProject.get().getProjectOwner().getUserName())
                .projectOwnerUid(findProject.get().getProjectOwner().getUid())
                .createTimestamp(
                        findProject.get().getCreateAt()
                                .toInstant().toEpochMilli()
                )
                .updateTimestamp(
                        findProject.get().getUpdateAt()
                                .toInstant().toEpochMilli()
                )
                .build();


        return ResponseEntity.ok().body(
                BaseResponse.Success.builder()
                        .result(result)
                        .build()
        );
    }




    @DeleteMapping("{projectUid}")
    public ResponseEntity<?> deleteProject(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PathVariable String projectUid
    ){



        projectService.removeProject(
                RemoveProjectProp.builder()
                        .isAdmin(false)
                        .projectUid(projectUid)
                        .actionUserUid(userDetail.getUniqueId())
                        .build()
        );

        return ResponseEntity.ok()
                .body(
                        BaseResponse.Success.builder()
                                .build()
                );
    }
}
