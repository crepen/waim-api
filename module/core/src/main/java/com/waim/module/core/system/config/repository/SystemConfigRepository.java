package com.waim.module.core.system.config.repository;

import com.waim.module.core.system.config.model.entity.SystemConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfigEntity , Long> , JpaSpecificationExecutor<SystemConfigEntity> {

}
