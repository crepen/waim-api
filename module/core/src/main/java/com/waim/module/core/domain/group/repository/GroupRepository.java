package com.waim.module.core.domain.group.repository;

import com.waim.module.core.domain.group.model.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, String>, JpaSpecificationExecutor<GroupEntity> {
    boolean existsByGroupAliasAndParentGroupUid_Uid(String groupAlias, String parentGroupUid);

    boolean existsByGroupAliasAndParentGroupUidIsNull(String groupAlias);

    boolean existsByGroupAliasAndParentGroupUid_UidAndUidNot(String groupAlias, String parentGroupUid, String uid);

    boolean existsByGroupAliasAndParentGroupUidIsNullAndUidNot(String groupAlias, String uid);

    long countByParentGroupUid_Uid(String parentGroupUid);
}
