package com.waim.module.core.domain.group.service;

import com.waim.module.core.domain.auth.model.error.AuthForbiddenException;
import com.waim.module.core.domain.group.model.entity.GroupEntity;
import com.waim.module.core.domain.group.model.entity.GroupRoleEntity;
import com.waim.module.core.domain.group.model.error.*;
import com.waim.module.core.domain.group.repository.GroupRepository;
import com.waim.module.core.domain.group.repository.GroupRoleRepository;
import com.waim.module.core.domain.project.repository.ProjectRepository;
import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.core.domain.user.model.error.UserNotFoundException;
import com.waim.module.core.domain.user.repository.UserRepository;
import com.waim.module.core.domain.user.service.UserService;
import com.waim.module.data.domain.group.AddGroupProp;
import com.waim.module.data.domain.group.GroupPermissionMetaData;
import com.waim.module.data.domain.group.GroupPermissionData;
import com.waim.module.data.domain.group.GroupRole;
import com.waim.module.data.domain.group.RemoveGroupProp;
import com.waim.module.data.domain.group.SearchGroupProp;
import com.waim.module.data.domain.group.UpdateGroupProp;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupRoleRepository groupRoleRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Page<GroupEntity> searchGroup(SearchGroupProp searchProp) {
        Specification<GroupEntity> spec = (root, query, cb) -> {
            query.distinct(true);

            var predicates = new ArrayList<Predicate>();

            if (StringUtils.hasText(searchProp.getKeyword())) {
                String pattern = "%" + searchProp.getKeyword().toLowerCase() + "%";
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("groupName")), pattern),
                                cb.like(cb.lower(root.get("groupAlias")), pattern)
                        )
                );
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };

        return groupRepository.findAll(spec, searchProp.getPageable());
    }

    @Transactional(readOnly = true)
    public Optional<GroupEntity> getGroup(String groupUid) {
        validateGroupUid(groupUid);
        return groupRepository.findById(groupUid);
    }

    @Transactional
    public GroupEntity addGroup(AddGroupProp prop) {
        validateGroupName(prop.getGroupName());
        validateGroupAlias(prop.getGroupAlias());

        if (groupRepository.existsByGroupAlias(prop.getGroupAlias())) {
            throw new GroupDuplicateAliasException();
        }

        GroupEntity newGroup = GroupEntity.builder()
                .groupName(prop.getGroupName())
                .groupAlias(prop.getGroupAlias())
                .build();

        if (StringUtils.hasText(prop.getParentGroupUid())) {
            GroupEntity parentGroup = groupRepository.findById(prop.getParentGroupUid())
                    .orElseThrow(GroupNotFoundException::new);
            newGroup.setParentGroupUid(parentGroup);
        }

        GroupEntity savedGroup = groupRepository.save(newGroup);

        if (!StringUtils.hasText(prop.getActionUserUid())) {
            throw new UserNotFoundException();
        }

        UserEntity actionUser = userRepository.findByUid(prop.getActionUserUid())
                .orElseThrow(UserNotFoundException::new);

        List<GroupRoleEntity> ownerRoles = mapGroupRoleLabel("ADMIN").stream()
                .map(role -> GroupRoleEntity.builder()
                        .group(savedGroup)
                        .user(actionUser)
                        .role(role)
                        .build())
                .toList();

        groupRoleRepository.saveAll(ownerRoles);

        return savedGroup;
    }

    @Transactional
    public GroupEntity updateGroup(UpdateGroupProp prop) {
        validateGroupUid(prop.getGroupUid());
        validateGroupName(prop.getGroupName());
        validateGroupAlias(prop.getGroupAlias());

        GroupEntity groupEntity = groupRepository.findById(prop.getGroupUid())
                .orElseThrow(GroupNotFoundException::new);

        if (!groupEntity.getGroupAlias().equals(prop.getGroupAlias())
                && groupRepository.existsByGroupAlias(prop.getGroupAlias())) {
            throw new GroupDuplicateAliasException();
        }

        groupEntity.setGroupName(prop.getGroupName());
        groupEntity.setGroupAlias(prop.getGroupAlias());

        if (StringUtils.hasText(prop.getParentGroupUid())) {
            if (groupEntity.getUid().equals(prop.getParentGroupUid())) {
                throw new GroupInvalidParentException();
            }

            GroupEntity parentGroup = groupRepository.findById(prop.getParentGroupUid())
                    .orElseThrow(GroupNotFoundException::new);
            groupEntity.setParentGroupUid(parentGroup);
        }
        else {
            groupEntity.setParentGroupUid(null);
        }

        return groupRepository.save(groupEntity);
    }

    @Transactional
    public void removeGroup(RemoveGroupProp prop) {
        validateGroupUid(prop.getGroupUid());

        GroupEntity groupEntity = groupRepository.findById(prop.getGroupUid())
                .orElseThrow(GroupNotFoundException::new);

        long childCount = groupRepository.countByParentGroupUid_Uid(groupEntity.getUid());
        if (childCount > 0) {
            throw new GroupChildExistsException();
        }

        long projectCount = projectRepository.countByProjectGroup_Uid(groupEntity.getUid());
        if (projectCount > 0) {
            throw new GroupProjectExistsException();
        }

        groupRepository.delete(groupEntity);
    }

    @Transactional(readOnly = true)
    public long getChildGroupCount(String groupUid) {
        validateGroupUid(groupUid);
        return groupRepository.countByParentGroupUid_Uid(groupUid);
    }

    @Transactional(readOnly = true)
    public long getLinkedProjectCount(String groupUid) {
        validateGroupUid(groupUid);
        return projectRepository.countByProjectGroup_Uid(groupUid);
    }

    @Transactional(readOnly = true)
    public List<GroupPermissionData> getGroupPermissions(String groupUid) {
        GroupEntity group = groupRepository.findById(groupUid)
                .orElseThrow(GroupNotFoundException::new);

        List<GroupRoleEntity> roleList = groupRoleRepository.findByGroup_Uid(groupUid);

        Map<String, List<GroupRoleEntity>> groupedByUser = new LinkedHashMap<>();

        for (GroupRoleEntity roleEntity : roleList) {
            String userUid = roleEntity.getUser().getUid();
            groupedByUser.computeIfAbsent(userUid, key -> new ArrayList<>()).add(roleEntity);
        }

        return groupedByUser.values().stream()
                .map(userRoles -> {
                    GroupRole maxRole = userRoles.stream()
                            .map(GroupRoleEntity::getRole)
                            .max(Comparator.comparingInt(this::groupRolePriority))
                            .orElse(GroupRole.ROLE_GROUP_READ);

                    UserEntity user = userRoles.getFirst().getUser();

                    return GroupPermissionData.builder()
                            .uid(user.getUid())
                            .groupUid(group.getUid())
                            .userUid(user.getUid())
                            .userId(user.getUserId())
                            .userName(user.getUserName())
                            .role(toGroupRoleLabel(maxRole))
                            .build();
                })
                .toList();
    }

    @Transactional
    public void upsertGroupPermission(String groupUid, String userIdOrEmail, String roleLabel, String actionUserUid, boolean isAdmin) {
        GroupEntity group = groupRepository.findById(groupUid)
                .orElseThrow(GroupNotFoundException::new);

        if (!isAdmin && !hasGroupAdminPermission(groupUid, actionUserUid)) {
            throw new AuthForbiddenException();
        }

        UserEntity targetUser = findUserByIdOrEmail(userIdOrEmail);

        groupRoleRepository.deleteByGroup_UidAndUser_Uid(groupUid, targetUser.getUid());

        List<GroupRoleEntity> insertList = mapGroupRoleLabel(roleLabel).stream()
                .map(role -> GroupRoleEntity.builder()
                        .group(group)
                        .user(targetUser)
                        .role(role)
                        .build())
                .toList();

        groupRoleRepository.saveAll(insertList);
    }

    @Transactional
    public void removeGroupPermission(String groupUid, String targetUserUid, String actionUserUid, boolean isAdmin) {
        groupRepository.findById(groupUid)
                .orElseThrow(GroupNotFoundException::new);

        if (!isAdmin && !hasGroupAdminPermission(groupUid, actionUserUid)) {
            throw new AuthForbiddenException();
        }

        if (!StringUtils.hasText(targetUserUid)) {
            throw new GroupEmptyUidException();
        }

        groupRoleRepository.deleteByGroup_UidAndUser_Uid(groupUid, targetUserUid);
    }

    private void validateGroupUid(String groupUid) {
        if (!StringUtils.hasText(groupUid)) {
            throw new GroupEmptyUidException();
        }
    }

    private void validateGroupName(String groupName) {
        if (!StringUtils.hasText(groupName)) {
            throw new GroupEmptyNameException();
        }
    }

    private void validateGroupAlias(String groupAlias) {
        if (!StringUtils.hasText(groupAlias)) {
            throw new GroupEmptyAliasException();
        }

        if (!groupAlias.matches("^[a-z0-9_]+$")) {
            throw new GroupInvalidAliasException();
        }
    }

    private boolean hasGroupAdminPermission(String groupUid, String actionUserUid) {
        if (!StringUtils.hasText(actionUserUid)) {
            return false;
        }

        List<GroupRoleEntity> roleList = groupRoleRepository.findByGroup_UidAndUser_Uid(groupUid, actionUserUid);
        return roleList.stream().anyMatch(x -> x.getRole() == GroupRole.ROLE_GROUP_USER_MODIFY);
    }

        private UserEntity findUserByIdOrEmail(String userIdOrEmail) {
        if (!StringUtils.hasText(userIdOrEmail)) {
            throw new GroupNotFoundException();
        }

        return userService.findActiveUserByIdOrEmail(userIdOrEmail)
                .orElseThrow(GroupNotFoundException::new);
    }

        @Transactional(readOnly = true)
        public List<GroupPermissionMetaData> getGroupPermissionMeta() {
        return List.of(
            GroupPermissionMetaData.builder()
                .role(GroupRole.ROLE_GROUP_READ.name())
                .displayName("Group Read")
                .description("그룹 읽기 권한")
                .build(),
            GroupPermissionMetaData.builder()
                .role(GroupRole.ROLE_GROUP_MODIFY.name())
                .displayName("Group Modify")
                .description("그룹 수정 권한")
                .build(),
            GroupPermissionMetaData.builder()
                .role(GroupRole.ROLE_GROUP_PROJECT_MODIFY.name())
                .displayName("Group Project Modify")
                .description("그룹 내 프로젝트 수정 권한")
                .build(),
            GroupPermissionMetaData.builder()
                .role(GroupRole.ROLE_GROUP_PROJECT_READ.name())
                .displayName("Group Project Read")
                .description("그룹 내 프로젝트 읽기 권한")
                .build(),
            GroupPermissionMetaData.builder()
                .role(GroupRole.ROLE_GROUP_USER_READ.name())
                .displayName("Group User Read")
                .description("그룹 유저 권한 읽기 권한")
                .build(),
            GroupPermissionMetaData.builder()
                .role(GroupRole.ROLE_GROUP_USER_MODIFY.name())
                .displayName("Group User Modify")
                .description("그룹 유저 권한 수정 권한")
                .build()
        );
        }

    private List<GroupRole> mapGroupRoleLabel(String roleLabel) {
        if (!StringUtils.hasText(roleLabel)) {
            throw new GroupNotFoundException();
        }

        String normalized = roleLabel.trim().toUpperCase();

        if ("GENERAL".equals(normalized)) {
            return List.of(
                    GroupRole.ROLE_GROUP_READ,
                    GroupRole.ROLE_GROUP_PROJECT_READ
            );
        }

        if ("ROLE_GROUP_READ".equals(normalized)) {
            return List.of(GroupRole.ROLE_GROUP_READ);
        }

        if ("ROLE_GROUP_MODIFY".equals(normalized)) {
            return List.of(GroupRole.ROLE_GROUP_MODIFY);
        }

        if ("ROLE_GROUP_PROJECT_READ".equals(normalized)) {
            return List.of(GroupRole.ROLE_GROUP_PROJECT_READ);
        }

        if ("ROLE_GROUP_PROJECT_MODIFY".equals(normalized)) {
            return List.of(GroupRole.ROLE_GROUP_PROJECT_MODIFY);
        }

        if ("ROLE_GROUP_USER_READ".equals(normalized)) {
            return List.of(GroupRole.ROLE_GROUP_USER_READ);
        }

        if ("ROLE_GROUP_USER_MODIFY".equals(normalized)) {
            return List.of(GroupRole.ROLE_GROUP_USER_MODIFY);
        }

        if ("EDITOR".equals(normalized)) {
            return List.of(
                    GroupRole.ROLE_GROUP_READ,
                    GroupRole.ROLE_GROUP_MODIFY,
                    GroupRole.ROLE_GROUP_PROJECT_READ,
                    GroupRole.ROLE_GROUP_PROJECT_MODIFY
            );
        }

        if ("ADMIN".equals(normalized)) {
            return List.of(
                    GroupRole.ROLE_GROUP_READ,
                    GroupRole.ROLE_GROUP_MODIFY,
                    GroupRole.ROLE_GROUP_PROJECT_READ,
                    GroupRole.ROLE_GROUP_PROJECT_MODIFY,
                    GroupRole.ROLE_GROUP_USER_READ,
                    GroupRole.ROLE_GROUP_USER_MODIFY
            );
        }

        throw new GroupNotFoundException();
    }

    private int groupRolePriority(GroupRole role) {
        if (role == GroupRole.ROLE_GROUP_USER_MODIFY) {
            return 3;
        }
        if (role == GroupRole.ROLE_GROUP_MODIFY || role == GroupRole.ROLE_GROUP_PROJECT_MODIFY) {
            return 2;
        }
        return 1;
    }

    private String toGroupRoleLabel(GroupRole role) {
        if (role == GroupRole.ROLE_GROUP_USER_MODIFY) {
            return "ADMIN";
        }
        if (role == GroupRole.ROLE_GROUP_MODIFY || role == GroupRole.ROLE_GROUP_PROJECT_MODIFY) {
            return "EDITOR";
        }
        return "GENERAL";
    }
}
