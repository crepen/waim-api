package com.waim.core.domain.user.repoisitory;

import com.waim.core.domain.user.model.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity , String> , JpaSpecificationExecutor<UserRoleEntity> {
}
