package com.waim.core.domain.user.repoisitory;

import com.waim.core.domain.user.model.entity.UserConfigEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserConfigRepository extends JpaRepository<UserConfigEntity , Integer> , JpaSpecificationExecutor<UserConfigEntity> {
    List<UserConfigEntity> findAllByUserUid(String uid);
    Optional<UserConfigEntity> findByUserUidAndConfigKey(String uid, String key);
}
