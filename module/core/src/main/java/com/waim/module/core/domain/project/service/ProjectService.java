package com.waim.module.core.domain.project.service;

import com.waim.module.core.domain.auth.model.error.AuthForbiddenException;
import com.waim.module.core.domain.project.model.entity.ProjectEntity;
import com.waim.module.core.domain.project.model.entity.ProjectRoleEntity;
import com.waim.module.core.domain.project.model.error.ProjectAlreadyDeleteException;
import com.waim.module.core.domain.project.model.error.ProjectDuplicateAliasException;
import com.waim.module.core.domain.project.model.error.ProjectEmptyAliasException;
import com.waim.module.core.domain.project.model.error.ProjectEmptyGroupUidException;
import com.waim.module.core.domain.project.model.error.ProjectEmptyNameException;
import com.waim.module.core.domain.project.model.error.ProjectEmptyUidException;
import com.waim.module.core.domain.project.model.error.ProjectInvalidAliasException;
import com.waim.module.core.domain.project.model.error.ProjectNotFoundException;
import com.waim.module.core.domain.project.model.error.ProjectPermissionAlreadyExistsException;
import com.waim.module.core.domain.project.repository.ProjectRepository;
import com.waim.module.core.domain.project.repository.ProjectRoleRepository;
import com.waim.module.core.domain.group.model.entity.GroupEntity;
import com.waim.module.core.domain.group.model.error.GroupNotFoundException;
import com.waim.module.core.domain.group.repository.GroupRepository;
import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.core.domain.user.model.error.UserNotFoundException;
import com.waim.module.core.domain.user.repository.UserRepository;
import com.waim.module.core.domain.user.service.UserService;
import com.waim.module.data.domain.project.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final EntityManager entityManager;


    @Transactional
    public Page<ProjectEntity> searchProject(SearchProjectProp searchProp) {

        List<ProjectStatus> targetStatuses = (searchProp.getRoles() == null || searchProp.getRoles().isEmpty())
                ? List.of(ProjectStatus.ACTIVE)
                : searchProp.getRoles();

        Specification<ProjectEntity> spec = (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            // Search Keyword
            if (StringUtils.hasText(searchProp.getKeyword())) {
                String pattern = "%" + searchProp.getKeyword().toLowerCase() + "%";

                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("projectAlias")), pattern),
                                cb.like(cb.lower(root.get("projectName")), pattern)
                        )
                );
            }

            if (StringUtils.hasText(searchProp.getGroupUid())) {
                predicates.add(
                        cb.equal(root.get("projectGroup").get("uid"), searchProp.getGroupUid())
                );
            }

            // Admin -> All Search
            // Not Admin -> Role : READ Search
            if (!searchProp.isAdmin()) {
                Join<ProjectEntity, ProjectRoleEntity> projectRoleJoin = root.join("projectRoles", JoinType.INNER);

                String roleUserUid = searchProp.getSearchUserUid() == null ? "UID_NULL" : searchProp.getSearchUserUid();

                predicates.add(
                        cb.and(
                        cb.equal(projectRoleJoin.get("role"), ProjectRole.ROLE_PROJECT_READ),
                                cb.equal(projectRoleJoin.get("user").get("uid"), roleUserUid)
                        )
                );
            }

            // Search Project Status
            predicates.add(
                    root.get("projectStatus").in(targetStatuses)
            );

            return cb.and(predicates);
        };

        return projectRepository.findAll(spec, searchProp.getPageable());
    }


    @Transactional
    public void addProject(AddProjectProp prop){

        String creatorUserUid = resolveCreatorUserUid(prop);
        UserEntity creatorUser = userRepository.findByUid(creatorUserUid)
                .orElseThrow(UserNotFoundException::new);

        if(!StringUtils.hasText(prop.getProjectName())){
            throw new ProjectEmptyNameException();
        }

        if(!StringUtils.hasText(prop.getProjectAlias())){
            throw new ProjectEmptyAliasException();
        }
        else if(!prop.getProjectAlias().matches("^[a-z0-9-]+$")){
            throw new ProjectInvalidAliasException();
        }

        if (!StringUtils.hasText(prop.getGroupUid())) {
            throw new ProjectEmptyGroupUidException();
        }


        ProjectEntity insertProject = ProjectEntity.builder()
                .projectName(prop.getProjectName())
                .projectAlias(prop.getProjectAlias())
                .projectStatus(ProjectStatus.ACTIVE)
                .projectOwner(
                    creatorUser
                )
                .build();

        GroupEntity matchGroup = groupRepository.findById(prop.getGroupUid())
                .orElseThrow(GroupNotFoundException::new);

        if (projectRepository.existsByProjectAliasAndProjectGroup_Uid(prop.getProjectAlias(), matchGroup.getUid())) {
            throw new ProjectDuplicateAliasException();
        }

        insertProject.setProjectGroup(matchGroup);

        projectRepository.save(insertProject);

        List<ProjectRoleEntity> ownerRoles = mapProjectRoleLabel("OWNER").stream()
            .map(role -> ProjectRoleEntity.builder()
                .project(insertProject)
                .user(creatorUser)
                .role(role)
                .build())
            .toList();

        projectRoleRepository.saveAll(ownerRoles);
    }

    private String resolveCreatorUserUid(AddProjectProp prop) {
        if (StringUtils.hasText(prop.getActionUserUid())) {
            return prop.getActionUserUid();
        }

        if (StringUtils.hasText(prop.getProjectOwnerUserUid())) {
            return prop.getProjectOwnerUserUid();
        }

        throw new UserNotFoundException();
    }



    public Optional<ProjectEntity> getProjectInfo(String projectUid , String actionUserUid) {
        return projectRepository.findOne((root, query, cb) -> {
            query.distinct(true);

            Join<ProjectEntity, ProjectRoleEntity> projectRoleJoin = root.join("projectRoles", JoinType.INNER);

            return cb.and(
                    cb.equal(root.get("uid") , projectUid),
                    cb.equal(projectRoleJoin.get("user").get("uid") , actionUserUid),
                    cb.equal(projectRoleJoin.get("role") , ProjectRole.ROLE_PROJECT_READ)
            );
        });
    }

    public void removeProject(RemoveProjectProp removeProp) {
        if (!StringUtils.hasText(removeProp.getProjectUid())) {
            // Empty project uid
            throw new ProjectEmptyUidException();
        }

        Specification<ProjectEntity> spec = (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.equal(root.get("uid"), removeProp.getProjectUid())
            );

            if (!removeProp.isAdmin()) {
                Join<ProjectEntity, ProjectRoleEntity> projectRoleJoin = root.join("projectRoles", JoinType.INNER);

                predicates.add(
                        cb.and(
                        cb.equal(projectRoleJoin.get("user").get("uid"), removeProp.getActionUserUid()),
                    cb.equal(projectRoleJoin.get("role"), ProjectRole.ROLE_PROJECT_MODIFY)
                        )
                );
            }

            return cb.and(predicates);
        };


        Optional<ProjectEntity> matchProject = projectRepository.findOne(spec);

        if (matchProject.isEmpty()) {
            throw new ProjectNotFoundException();
        }
        else if(matchProject.get().getProjectStatus() == ProjectStatus.DELETED){
            throw new ProjectAlreadyDeleteException();
        }


        matchProject.get().setProjectStatus(ProjectStatus.DELETED);

        projectRepository.save(matchProject.get());
    }

    @Transactional(readOnly = true)
    public List<ProjectPermissionData> getProjectPermissions(String projectUid) {
        ProjectEntity project = projectRepository.findById(projectUid)
                .orElseThrow(ProjectNotFoundException::new);

        List<ProjectRoleEntity> roleList = projectRoleRepository.findByProject_Uid(projectUid);

        Map<String, List<ProjectRoleEntity>> groupedByUser = new LinkedHashMap<>();

        for (ProjectRoleEntity roleEntity : roleList) {
            String userUid = roleEntity.getUser().getUid();
            groupedByUser.computeIfAbsent(userUid, key -> new ArrayList<>()).add(roleEntity);
        }

        return groupedByUser.values().stream()
                .map(userRoles -> {
                    ProjectRole maxRole = userRoles.stream()
                            .map(ProjectRoleEntity::getRole)
                            .max(Comparator.comparingInt(this::projectRolePriority))
                            .orElse(ProjectRole.ROLE_PROJECT_READ);

                    UserEntity user = userRoles.getFirst().getUser();

                    return ProjectPermissionData.builder()
                            .uid(user.getUid())
                            .projectUid(project.getUid())
                            .userUid(user.getUid())
                            .userId(user.getUserId())
                            .userName(user.getUserName())
                            .role(toProjectRoleLabel(maxRole))
                            .build();
                })
                .toList();
    }

    @Transactional
        public void addProjectPermission(String projectUid, String userIdOrEmail, String roleLabel, String actionUserUid, boolean isAdmin) {
        ProjectEntity project = projectRepository.findById(projectUid)
            .orElseThrow(ProjectNotFoundException::new);

        validateProjectPermissionGrantable(projectUid, project, actionUserUid, isAdmin);

        UserEntity targetUser = findUserByIdOrEmail(userIdOrEmail);

        List<ProjectRoleEntity> existingRoles = projectRoleRepository.findByProject_UidAndUser_Uid(projectUid, targetUser.getUid());
        Set<ProjectRole> existingRoleSet = existingRoles.stream()
            .map(ProjectRoleEntity::getRole)
            .collect(Collectors.toSet());

        Set<ProjectRole> requestedRoleSet = Set.copyOf(mapProjectRoleLabel(roleLabel));

        if (!requestedRoleSet.isEmpty() && existingRoleSet.containsAll(requestedRoleSet)) {
            throw new ProjectPermissionAlreadyExistsException();
        }

        List<ProjectRoleEntity> insertList = requestedRoleSet.stream()
            .filter(role -> !existingRoleSet.contains(role))
            .map(role -> ProjectRoleEntity.builder()
                .project(project)
                .user(targetUser)
                .role(role)
                .build())
            .toList();

        if (!insertList.isEmpty()) {
            projectRoleRepository.saveAll(insertList);
        }
        }

        @Transactional
    public void upsertProjectPermission(String projectUid, String userIdOrEmail, String roleLabel, String actionUserUid, boolean isAdmin) {
        ProjectEntity project = projectRepository.findById(projectUid)
                .orElseThrow(ProjectNotFoundException::new);

        validateProjectPermissionGrantable(projectUid, project, actionUserUid, isAdmin);

        UserEntity targetUser = findUserByIdOrEmail(userIdOrEmail);

        projectRoleRepository.deleteByProject_UidAndUser_Uid(projectUid, targetUser.getUid());

        List<ProjectRoleEntity> insertList = mapProjectRoleLabel(roleLabel).stream()
            .map(role -> ProjectRoleEntity.builder()
                .project(project)
                .user(targetUser)
                .role(role)
                .build())
            .toList();

        projectRoleRepository.saveAll(insertList);
        }

        private void validateProjectPermissionGrantable(String projectUid, ProjectEntity project, String actionUserUid, boolean isAdmin) {

        if (!isAdmin && !project.getProjectOwner().getUid().equals(actionUserUid)) {
            List<ProjectRoleEntity> actionRoles = projectRoleRepository.findByProject_UidAndUser_Uid(projectUid, actionUserUid);
            boolean hasUserModifyRole = actionRoles.stream().anyMatch(x -> x.getRole() == ProjectRole.ROLE_PROJECT_USER_MODIFY);
            if (!hasUserModifyRole) {
                throw new AuthForbiddenException();
            }
        }
    }

    @Transactional
    public void removeProjectPermission(String projectUid, String targetUserUid, String actionUserUid, boolean isAdmin) {
        ProjectEntity project = projectRepository.findById(projectUid)
                .orElseThrow(ProjectNotFoundException::new);

        if (!isAdmin && !project.getProjectOwner().getUid().equals(actionUserUid)) {
            List<ProjectRoleEntity> actionRoles = projectRoleRepository.findByProject_UidAndUser_Uid(projectUid, actionUserUid);
            boolean hasUserModifyRole = actionRoles.stream().anyMatch(x -> x.getRole() == ProjectRole.ROLE_PROJECT_USER_MODIFY);
            if (!hasUserModifyRole) {
                throw new AuthForbiddenException();
            }
        }

        if (!StringUtils.hasText(targetUserUid)) {
            throw new ProjectEmptyUidException();
        }

        if (project.getProjectOwner().getUid().equals(targetUserUid)) {
            return;
        }

        projectRoleRepository.deleteByProject_UidAndUser_Uid(projectUid, targetUserUid);
    }

        private UserEntity findUserByIdOrEmail(String userIdOrEmail) {
        if (!StringUtils.hasText(userIdOrEmail)) {
            throw new ProjectNotFoundException();
        }

        return userService.findActiveUserByIdOrEmail(userIdOrEmail)
                .orElseThrow(ProjectNotFoundException::new);
    }

        @Transactional(readOnly = true)
        public List<ProjectPermissionMetaData> getProjectPermissionMeta() {
        return List.of(
            ProjectPermissionMetaData.builder()
                .role(ProjectRole.ROLE_PROJECT_READ.name())
                .displayName("Project Read")
                .description("프로젝트 읽기 권한")
                .build(),
            ProjectPermissionMetaData.builder()
                .role(ProjectRole.ROLE_PROJECT_MODIFY.name())
                .displayName("Project Modify")
                .description("프로젝트 수정 권한")
                .build(),
            ProjectPermissionMetaData.builder()
                .role(ProjectRole.ROLE_PROJECT_USER_READ.name())
                .displayName("Project User Read")
                .description("프로젝트 유저 권한 읽기 권한")
                .build(),
            ProjectPermissionMetaData.builder()
                .role(ProjectRole.ROLE_PROJECT_USER_MODIFY.name())
                .displayName("Project User Modify")
                .description("프로젝트 유저 권한 수정 권한")
                .build()
        );
        }

    private List<ProjectRole> mapProjectRoleLabel(String roleLabel) {
        if (!StringUtils.hasText(roleLabel)) {
            throw new ProjectNotFoundException();
        }

        String normalized = roleLabel.trim().toUpperCase();

        if ("GENERAL".equals(normalized)) {
            return List.of(
                    ProjectRole.ROLE_PROJECT_READ
            );
        }

        if ("ROLE_PROJECT_READ".equals(normalized)) {
            return List.of(ProjectRole.ROLE_PROJECT_READ);
        }

        if ("ROLE_PROJECT_MODIFY".equals(normalized)) {
            return List.of(ProjectRole.ROLE_PROJECT_MODIFY);
        }

        if ("ROLE_PROJECT_USER_READ".equals(normalized)) {
            return List.of(ProjectRole.ROLE_PROJECT_USER_READ);
        }

        if ("ROLE_PROJECT_USER_MODIFY".equals(normalized)) {
            return List.of(ProjectRole.ROLE_PROJECT_USER_MODIFY);
        }

        if ("EDITOR".equals(normalized)) {
            return List.of(
                    ProjectRole.ROLE_PROJECT_READ,
                    ProjectRole.ROLE_PROJECT_MODIFY
            );
        }

        if ("OWNER".equals(normalized)) {
            return List.of(
                    ProjectRole.ROLE_PROJECT_READ,
                    ProjectRole.ROLE_PROJECT_MODIFY,
                    ProjectRole.ROLE_PROJECT_USER_READ,
                    ProjectRole.ROLE_PROJECT_USER_MODIFY
            );
        }

        throw new ProjectNotFoundException();
    }

    private int projectRolePriority(ProjectRole role) {
        if (role == ProjectRole.ROLE_PROJECT_USER_MODIFY) {
            return 3;
        }
        if (role == ProjectRole.ROLE_PROJECT_MODIFY) {
            return 2;
        }
        return 1;
    }

    private String toProjectRoleLabel(ProjectRole role) {
        if (role == ProjectRole.ROLE_PROJECT_USER_MODIFY) {
            return "OWNER";
        }
        if (role == ProjectRole.ROLE_PROJECT_MODIFY) {
            return "EDITOR";
        }
        return "GENERAL";
    }

}
