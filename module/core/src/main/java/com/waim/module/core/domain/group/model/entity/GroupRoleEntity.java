package com.waim.module.core.domain.group.model.entity;

import com.waim.module.core.domain.group.model.entity.id.GroupRoleId;
import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.data.domain.group.GroupRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@IdClass(GroupRoleId.class)
@Table(
        name = "aod_group_role",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "그룹 - 사용자 권한 관리 Table"
)
public class GroupRoleEntity {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "group_uid",
            nullable = false,
            comment = "그룹 UID"
    )
    private GroupEntity group;

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_uid",
            nullable = false,
            comment = "사용자 UID"
    )
    private UserEntity user;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(
            name = "role",
            nullable = false, columnDefinition = "VARCHAR(30)",
            comment = "사용자 그룹 역할"
    )
    private GroupRole role;
}
