package com.waim.module.storage.domain.user.repository;

import com.waim.module.storage.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity , String> , JpaSpecificationExecutor<UserEntity> {

}
