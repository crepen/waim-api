package com.waim.core.domain.project.repository.spec;

import com.waim.core.domain.project.model.entity.ProjectEntity;
import com.waim.core.domain.project.model.entity.ProjectRoleEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProjectSpecification {
    public static Specification<ProjectEntity> existsUserProjectName(String projectName , String userUid){
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("projectName") , projectName));
            predicates.add(criteriaBuilder.equal(root.get("projectOwner").get("uid") , userUid));

            return criteriaBuilder.and(predicates);
        });
    }

    public static Specification<ProjectEntity> existsUserProjectAlias(String projectAlias , String userUid){
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("projectAlias") , projectAlias));
            predicates.add(criteriaBuilder.equal(root.get("projectOwner").get("uid") , userUid));

            return criteriaBuilder.and(predicates);
        });
    }

    public static Specification<ProjectEntity> searchUserProjectAlias(String projectOwnerId , String projectAlias){
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("projectAlias") , projectAlias));
            predicates.add(criteriaBuilder.equal(root.get("projectOwner").get("userId") , projectOwnerId));

            return criteriaBuilder.and(predicates);
        });
    }



}
