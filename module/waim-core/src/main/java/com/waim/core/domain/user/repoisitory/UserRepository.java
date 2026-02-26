package com.waim.core.domain.user.repoisitory;

import com.waim.core.domain.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity , String> , JpaSpecificationExecutor<UserEntity> {
    Optional<UserEntity> findByUserId(String userId);
    Optional<UserEntity> findByUid(String uid);
    Optional<UserEntity> findByUserName(String userName);

}
