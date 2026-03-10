package com.waim.module.core.domain.group.repository;

import com.waim.module.core.domain.group.model.entity.GroupRoleEntity;
import com.waim.module.core.domain.group.model.entity.id.GroupRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRoleRepository extends JpaRepository<GroupRoleEntity, GroupRoleId> {
    List<GroupRoleEntity> findByGroup_Uid(String groupUid);

    List<GroupRoleEntity> findByGroup_UidAndUser_Uid(String groupUid, String userUid);

    void deleteByGroup_UidAndUser_Uid(String groupUid, String userUid);
}
