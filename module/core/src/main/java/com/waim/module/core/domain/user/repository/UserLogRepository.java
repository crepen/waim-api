package com.waim.module.core.domain.user.repository;

import com.waim.module.core.domain.user.model.entity.UserLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogRepository extends JpaRepository<UserLogEntity , Long> , JpaSpecificationExecutor<UserLogEntity> {
}
