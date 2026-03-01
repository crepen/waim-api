package com.waim.module.core.domain.project.service;

import com.waim.module.core.domain.project.model.entity.ProjectEntity;
import com.waim.module.core.domain.project.model.entity.ProjectRoleEntity;
import com.waim.module.core.domain.project.model.error.ProjectAlreadyDeleteException;
import com.waim.module.core.domain.project.model.error.ProjectEmptyUidException;
import com.waim.module.core.domain.project.model.error.ProjectNotFoundException;
import com.waim.module.core.domain.project.repository.ProjectRepository;
import com.waim.module.core.domain.user.model.entity.UserEntity;
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
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EntityManager entityManager;


    @Transactional
    public Page<ProjectEntity> searchProject(SearchProjectProp searchProp) {

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

            // Admin -> All Search
            // Not Admin -> Role : READ Search
            if (!searchProp.isAdmin()) {
                Join<ProjectEntity, ProjectRoleEntity> projectRoleJoin = root.join("projectRoles", JoinType.INNER);

                String roleUserUid = searchProp.getSearchUserUid() == null ? "UID_NULL" : searchProp.getSearchUserUid();

                predicates.add(
                        cb.and(
                                cb.equal(projectRoleJoin.get("role"), ProjectRole.ROLE_READ),
                                cb.equal(projectRoleJoin.get("user").get("uid"), roleUserUid)
                        )
                );
            }

            // Search Project Status
            predicates.add(
                    root.get("projectStatus").in(searchProp.getRoles())
            );

            return cb.and(predicates);
        };

        return projectRepository.findAll(spec, searchProp.getPageable());
    }


    @Transactional
    public void addProject(AddProjectProp prop){


        if(!StringUtils.hasText(prop.getProjectName())){
            // Empty Project Name
            // TODO : EXCEPTION
        }

        if(!StringUtils.hasText(prop.getProjectAlias())){
            // Empty Project Alias
            // TODO : EXCEPTION
        }
        else if(prop.getProjectAlias().matches("^[a-z0-9-]*$")){
            // Project Alias Rule Invalid
            // TODO : EXCEPTION
        }


        List<ProjectEntity> matchProject = getDuplicateProject(
                prop.getProjectName(),
                prop.getProjectAlias(),
                prop.getProjectOwnerUserUid()
        );

        if(!matchProject.isEmpty()){

            if(matchProject.stream().anyMatch(x -> x.getProjectName().equals(prop.getProjectName()))){
                // Duplicate project name
                // TODO : EXCEPTION
            }

            if(matchProject.stream().anyMatch(x -> x.getProjectAlias().equals(prop.getProjectAlias()))){
                // Duplicate project alias
                // TODO : EXCEPTION
            }

        }


        ProjectEntity insertProject = ProjectEntity.builder()
                .projectName(prop.getProjectName())
                .projectAlias(prop.getProjectAlias())
                .projectStatus(ProjectStatus.ACTIVE)
                .projectOwner(
                        entityManager.getReference(
                                UserEntity.class,
                                prop.getProjectOwnerUserUid()
                        )
                )
                .build();

        projectRepository.save(insertProject);


        // TODO : ADD ROLE (OR EVENT)
    }



    public Optional<ProjectEntity> getProjectInfo(String projectUid , String actionUserUid) {
        return projectRepository.findOne((root, query, cb) -> {
            query.distinct(true);
            return cb.and(
                    cb.equal(root.get("uid") , projectUid),
                    cb.equal(root.get("role").get("user_uid") , actionUserUid)
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
                predicates.add(
                        cb.and(
                                cb.equal(root.get("projectRoles").get("uid"), removeProp.getActionUserUid()),
                                cb.equal(root.get("projectRoles").get("role"), ProjectRole.ROLE_READ)
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



    private List<ProjectEntity> getDuplicateProject(String projectName , String projectAlias , String ownerUid){
        Specification<ProjectEntity> spec = (
                (root, query, cb) -> {
                    query.distinct(true);

                    return cb.and(
                            cb.or(
                                    cb.equal(root.get("projectName") , projectName),
                                    cb.equal(root.get("projectAlias") , projectAlias)
                            ),
                            cb.equal(root.get("projectOwner") , ownerUid)
                    );
                }
        );

        return projectRepository.findAll(spec);
    }

}
