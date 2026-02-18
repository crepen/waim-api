package com.waim.core.domain.project.service;

import com.waim.core.common.model.error.WAIMException;
import com.waim.core.common.util.date.DateUtil;
import com.waim.core.common.util.date.LocalDateTimeExtension;
import com.waim.core.domain.project.model.dto.ProjectData;
import com.waim.core.domain.project.model.dto.ProjectSearchOption;
import com.waim.core.domain.project.model.dto.enumable.ProjectRole;
import com.waim.core.domain.project.model.dto.enumable.ProjectStatus;
import com.waim.core.domain.project.model.entity.ProjectEntity;
import com.waim.core.domain.project.model.entity.ProjectRoleEntity;
import com.waim.core.domain.project.model.error.ProjectErrorCode;
import com.waim.core.domain.project.model.error.ProjectUidUndefinedException;
import com.waim.core.domain.project.repository.ProjectRepository;
import com.waim.core.domain.project.repository.spec.ProjectSpecification;
import com.waim.core.domain.user.model.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.swing.text.html.Option;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@ExtensionMethod({LocalDateTimeExtension.class})
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final EntityManager entityManager;
    private final ProjectRoleService projectRoleService;


    /**
     * <h3>프로젝트 검색</h3>
     *
     * @return
     */

    public Page<ProjectEntity> searchProjectPageable(ProjectSearchOption searchOption) {
        Specification<ProjectEntity> spec = ((root, query, cb) -> {
            query.distinct(true);
            return cb.conjunction();
        });

        spec = spec.and((root, query, cb) -> {
            Join<ProjectEntity, ProjectRoleEntity> projectRoleJoin = root.join("projectRoles", JoinType.INNER);
            return cb.equal(projectRoleJoin.get("user").get("uid"), searchOption.searchUserUid());
        });

        if (StringUtils.hasText(searchOption.searchKeyword())) {
            spec = spec.and((root, query, cb) -> {
                String pattern = "%" + searchOption.searchKeyword().toLowerCase() + "%";

                return cb.or(
                        cb.like(cb.lower(root.get("projectAlias")), pattern),
                        cb.like(cb.lower(root.get("projectName")), pattern)
                );
            });
        }


        spec = spec.and((root, query, cb) -> {
             return cb.equal(root.get("projectStatus"), ProjectStatus.ACTIVE);
        });


        return projectRepository.findAll(spec, searchOption.pageable());
    }




    /**
     * <h3>사용자 프로젝트 생성</h3>
     *
     * <p>
     *
     * </p>
     *
     * @param projectName   프로젝트명
     * @param projectAlias  프로젝트 약칭
     * @param createUserUid 프로젝트 생성자 UID
     */
    public void addProject(
            String projectName,
            String projectAlias,
            String createUserUid
    ) {
        if (!StringUtils.hasText(projectName)) {
            throw new WAIMException(ProjectErrorCode.PROJECT_NAME_EMPTY);
        } else if (projectRepository.exists(ProjectSpecification.existsUserProjectName(projectName, createUserUid))) {
            throw new WAIMException(ProjectErrorCode.PROJECT_NAME_DUPLICATE);
        }
//        else if(projectName.matches("")){
//            // TODO : Project Name Length Check
//        }

        if (!StringUtils.hasText(projectAlias)) {
            throw new WAIMException(ProjectErrorCode.PROJECT_ALIAS_EMPTY);
        } else if (!projectAlias.matches("^[a-z0-9-]*$")) {
            throw new WAIMException(ProjectErrorCode.PROJECT_ALIAS_NOT_ALLOW);
        } else if (projectRepository.exists(ProjectSpecification.existsUserProjectAlias(projectAlias, createUserUid))) {
            throw new WAIMException(ProjectErrorCode.PROJECT_ALIAS_DUPLICATE);
        }

        UserEntity actionUserEntity = entityManager.getReference(
                UserEntity.class,
                createUserUid
        );


        ProjectEntity insertEntity = ProjectEntity.builder()
                .projectName(projectName)
                .projectAlias(projectAlias)
                .projectOwner(actionUserEntity)
                .build();



        var storeProjectEntity = projectRepository.save(insertEntity);

        projectRoleService.addRole(
                Optional.of(storeProjectEntity),
                Optional.of(actionUserEntity),
                ProjectRole.ROLE_READ , ProjectRole.ROLE_WRITE , ProjectRole.ROLE_DELETE
        );
    }


    /**
     * <h3>프로젝트 정보 조회</h3>
     * <p>
     * 프로젝트 UID로 검색
     * </p>
     *
     * @param uid Project UID
     */
    public Optional<ProjectEntity> getProject(String uid) {

        // TODO : Validation check
        if(!StringUtils.hasText(uid)){
            throw new WAIMException();
        }

        return projectRepository.findByUid(uid);
    }

    public Optional<ProjectEntity> getActiveProject(String ownerUid) {
        if(!StringUtils.hasText(ownerUid)){
            throw new ProjectUidUndefinedException();
        }

        Specification<ProjectEntity> spec = ((root, query, cb) -> {
            query.distinct(true);

            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.equal(root.get("projectOwner").get("uid"), ownerUid));
            predicate = cb.and(predicate, cb.equal(root.get("projectStatus") , ProjectStatus.ACTIVE));
            return predicate;
        });

        return projectRepository.findOne(spec);
    }

    public Optional<ProjectEntity> getActiveProjectUsingAliasAndOwnerUid(String alias, String ownerUid) {

        Specification<ProjectEntity> spec = ((root, query, cb) -> {
            query.distinct(true);

            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.equal(root.get("projectAlias"), alias));
            predicate = cb.and(predicate, cb.equal(root.get("projectOwner").get("uid"), ownerUid));
            predicate = cb.and(predicate, cb.equal(root.get("projectStatus") , ProjectStatus.ACTIVE));

            return predicate;
        });

        return projectRepository.findOne(spec);
    }


    /**
     * <h3>프로젝트 정보 조회</h3>
     * <p>
     * 프로젝트 UID로 검색
     * </p>
     *
     * @param userId       Owner user id
     * @param projectAlias Project Alias
     */
    public Optional<ProjectEntity> getProject(String userId, String projectAlias) {

        // TODO : Validation check

        return projectRepository.findOne(ProjectSpecification.searchUserProjectAlias(userId, projectAlias));
    }

    @Transactional
    public void removeProject(String projectUid , String actionUserUid){

        if(!StringUtils.hasText(projectUid)){
            throw new WAIMException(ProjectErrorCode.PROJECT_UID_EMPTY);
        }

        Specification<ProjectEntity> spec = (root, query, cb) -> cb.equal(root.get("uid") , projectUid);

        if(StringUtils.hasText(actionUserUid)){
            spec = spec.and((root, query, cb) -> {
                Join<ProjectEntity, ProjectRoleEntity> roleJoin = root.join("projectRoles");
                return cb.equal(roleJoin.get("user").get("uid"), actionUserUid);
            });
        }

        var matchProject = projectRepository.findOne(spec);

        if(matchProject.isEmpty()){
            throw new WAIMException(ProjectErrorCode.PROJECT_NOT_FOUND);
        }



        matchProject.get().setProjectStatus(ProjectStatus.DELETED);

        projectRepository.save(matchProject.get());

    }

}
