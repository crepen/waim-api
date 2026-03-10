package com.waim.module.core.domain.group.repository;

import com.waim.module.core.domain.group.model.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, String>, JpaSpecificationExecutor<GroupEntity> {
    boolean existsByGroupAlias(String groupAlias);

    long countByParentGroupUid_Uid(String parentGroupUid);
}
