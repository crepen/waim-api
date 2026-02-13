package com.waim.core.domain.user.repoisitory;

import com.waim.core.domain.user.model.dto.enumable.UserRole;
import com.waim.core.domain.user.model.entity.UserRoleEntity;
import com.waim.core.domain.user.model.entity.UserEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<UserEntity> loginIdOrEmail(String id ,String encEmail){
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(StringUtils.hasText(id)){
                predicates.add(criteriaBuilder.equal(root.get("userId"), id));
            }

            if(StringUtils.hasText(encEmail)){
                predicates.add(criteriaBuilder.equal(root.get("userEmailHash"), encEmail));
            }

            return criteriaBuilder.or(predicates);
        });
    }

    public static Specification<UserEntity> duplicateMatchUser(String userName , String userId , String userEmailEnc){
        return ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if(StringUtils.hasText(userName)){
                predicates.add(criteriaBuilder.equal(root.get("userName"), userName));
            }

            if(StringUtils.hasText(userId)){
                predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
            }

            if(StringUtils.hasText(userEmailEnc)){
                predicates.add(criteriaBuilder.equal(root.get("userEmailHash"), userEmailEnc));
            }

            return criteriaBuilder.or(predicates);
        });
    }

    public static Specification<UserEntity> hasRole(UserRole role) {
        return (root, query, criteriaBuilder) -> {
            if (role == null) return null;

            Join<UserEntity, UserRoleEntity> userRoles = root.join("roles", JoinType.INNER);

            return criteriaBuilder.equal(userRoles.get("role"), role.getValue());
        };
    }

    public static Specification<UserEntity> hasRoles(String... roles) {
        return (root, query, criteriaBuilder) -> {
            if (roles == null || roles.length == 0) {
                return null;
            }

            // 2. Join 수행 (roles 필드가 UserEntity에 List 등으로 매핑되어 있어야 함)
            Join<UserEntity, UserRoleEntity> userRoles = root.join("roles", JoinType.INNER);

            // 3. IN 절 생성 및 파라미터 추가
            CriteriaBuilder.In<String> inClause = criteriaBuilder.in(userRoles.get("role"));
            for (String role : roles) {
                inClause.value(role);
            }

            return inClause;
        };
    }
}
