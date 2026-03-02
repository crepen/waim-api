package com.waim.module.core.domain.user.repository;

import com.waim.module.core.domain.user.model.entity.UserAttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAttributeRepository extends JpaRepository<UserAttributeEntity , Long>  , JpaSpecificationExecutor<UserAttributeEntity> {
    Optional<UserAttributeEntity> findByAttrKey(String key);
}
