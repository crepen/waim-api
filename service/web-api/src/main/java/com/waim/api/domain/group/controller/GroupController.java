package com.waim.api.domain.group.controller;

import com.waim.api.common.model.CommonPageable;
import com.waim.api.common.model.response.BasePageableResponse;
import com.waim.api.common.model.response.BaseResponse;
import com.waim.api.domain.group.model.request.AddGroupRequest;
import com.waim.api.domain.group.model.request.GroupPermissionRequest;
import com.waim.api.domain.group.model.request.SearchGroupRequest;
import com.waim.api.domain.group.model.request.UpdateGroupRequest;
import com.waim.module.core.domain.group.model.error.GroupNotFoundException;
import com.waim.module.core.domain.group.service.GroupService;
import com.waim.module.data.common.security.SecurityUserDetail;
import com.waim.module.data.domain.group.AddGroupProp;
import com.waim.module.data.domain.group.GroupData;
import com.waim.module.data.domain.group.GroupPermissionMetaData;
import com.waim.module.data.domain.group.GroupPermissionData;
import com.waim.module.data.domain.group.RemoveGroupProp;
import com.waim.module.data.domain.group.SearchGroupProp;
import com.waim.module.data.domain.group.UpdateGroupProp;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    @Operation(summary = "그룹 검색")
    public ResponseEntity<?> searchGroup(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PageableDefault(size = 20, sort = "groupName", direction = Sort.Direction.ASC) Pageable pageable,
            SearchGroupRequest reqParam
    ) {
        Page<GroupData> resultPage = groupService.searchGroup(
                        SearchGroupProp.builder()
                                .keyword(reqParam.keyword())
                                .pageable(pageable)
                                .build()
                )
                .map(x -> GroupData.builder()
                        .uid(x.getUid())
                        .groupName(x.getGroupName())
                        .groupAlias(x.getGroupAlias())
                        .parentGroupUid(x.getParentGroupUid() == null ? null : x.getParentGroupUid().getUid())
                        .childGroupCount(groupService.getChildGroupCount(x.getUid()))
                        .linkedProjectCount(groupService.getLinkedProjectCount(x.getUid()))
                        .build());

        return ResponseEntity.ok(
                BasePageableResponse.Success.builder()
                        .result(resultPage.getContent())
                        .pageable(CommonPageable.cast(resultPage))
                        .build()
        );
    }

    @PutMapping
    @Operation(summary = "그룹 생성")
    public ResponseEntity<?> addGroup(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @RequestBody AddGroupRequest reqBody
    ) {
        groupService.addGroup(
                AddGroupProp.builder()
                        .groupName(reqBody.getGroupName())
                        .groupAlias(reqBody.getGroupAlias())
                        .parentGroupUid(reqBody.getParentGroupUid())
                        .actionUserUid(userDetail.getUniqueId())
                        .build()
        );

        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .build()
        );
    }

    @GetMapping("{groupUid}")
    @Operation(summary = "그룹 단건 조회")
    public ResponseEntity<?> getGroup(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PathVariable String groupUid
    ) {
        var groupEntity = groupService.getGroup(groupUid)
                .orElseThrow(GroupNotFoundException::new);

        GroupData result = GroupData.builder()
                .uid(groupEntity.getUid())
                .groupName(groupEntity.getGroupName())
                .groupAlias(groupEntity.getGroupAlias())
                .parentGroupUid(groupEntity.getParentGroupUid() == null ? null : groupEntity.getParentGroupUid().getUid())
                .childGroupCount(groupService.getChildGroupCount(groupEntity.getUid()))
                .linkedProjectCount(groupService.getLinkedProjectCount(groupEntity.getUid()))
                .build();

        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .result(result)
                        .build()
        );
    }

    @PostMapping("{groupUid}")
    @Operation(summary = "그룹 수정")
    public ResponseEntity<?> updateGroup(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PathVariable String groupUid,
            @RequestBody UpdateGroupRequest reqBody
    ) {
        groupService.updateGroup(
                UpdateGroupProp.builder()
                        .groupUid(groupUid)
                        .groupName(reqBody.getGroupName())
                        .groupAlias(reqBody.getGroupAlias())
                        .parentGroupUid(reqBody.getParentGroupUid())
                        .build()
        );

        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .build()
        );
    }

    @DeleteMapping("{groupUid}")
    @Operation(summary = "그룹 삭제")
    public ResponseEntity<?> removeGroup(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PathVariable String groupUid
    ) {
        groupService.removeGroup(
                RemoveGroupProp.builder()
                        .groupUid(groupUid)
                        .build()
        );

        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .build()
        );
    }

    @GetMapping("{groupUid}/permission")
    @Operation(summary = "그룹 사용자 권한 조회")
    public ResponseEntity<?> getGroupPermissions(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PathVariable String groupUid
    ) {
        List<GroupPermissionData> result = groupService.getGroupPermissions(groupUid);

        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .result(result)
                        .build()
        );
    }

    @PutMapping("{groupUid}/permission")
    @Operation(summary = "그룹 사용자 권한 추가")
    public ResponseEntity<?> addGroupPermission(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PathVariable String groupUid,
            @RequestBody GroupPermissionRequest reqBody
    ) {
                String targetUser = reqBody.getUserId();
        if (targetUser == null || targetUser.isBlank()) {
                        targetUser = reqBody.getUserEmail();
        }

        groupService.upsertGroupPermission(
                groupUid,
                targetUser,
                reqBody.getRole(),
                userDetail.getUniqueId(),
                isAdmin(userDetail)
        );

        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .build()
        );
    }

    @PostMapping("{groupUid}/permission/{permissionUid}")
    @Operation(summary = "그룹 사용자 권한 수정")
    public ResponseEntity<?> updateGroupPermission(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PathVariable String groupUid,
            @PathVariable String permissionUid,
            @RequestBody GroupPermissionRequest reqBody
    ) {
        groupService.upsertGroupPermission(
                groupUid,
                permissionUid,
                reqBody.getRole(),
                userDetail.getUniqueId(),
                isAdmin(userDetail)
        );

        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .build()
        );
    }

    @DeleteMapping("{groupUid}/permission/{permissionUid}")
    @Operation(summary = "그룹 사용자 권한 삭제")
    public ResponseEntity<?> removeGroupPermission(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PathVariable String groupUid,
            @PathVariable String permissionUid
    ) {
        groupService.removeGroupPermission(
                groupUid,
                permissionUid,
                userDetail.getUniqueId(),
                isAdmin(userDetail)
        );

        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .build()
        );
    }

    @GetMapping("permission/meta")
    @Operation(summary = "그룹 권한 메타 정보 조회")
    public ResponseEntity<?> getGroupPermissionMeta(
            @AuthenticationPrincipal SecurityUserDetail userDetail
    ) {
        List<GroupPermissionMetaData> result = groupService.getGroupPermissionMeta();

        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .result(result)
                        .build()
        );
    }

    private boolean isAdmin(SecurityUserDetail userDetail) {
        return userDetail != null
                && userDetail.getRoles() != null
                && userDetail.getRoles().stream().anyMatch(role -> role != null && role.toLowerCase().contains("admin"));
    }
}
