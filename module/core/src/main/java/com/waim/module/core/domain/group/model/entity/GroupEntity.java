package com.waim.module.core.domain.group.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "aob_group",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "그룹 Table"
)
public class GroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "uid",
            unique = true, nullable = false, length = 36,
            comment = "Unique ID"
    )
    private String uid;

    @Column(
            name = "group_name",
            nullable = false , length = 100,
            comment = "그룹명"
    )
    private String groupName;

    @Column(
            name = "group_alias",
            nullable = false , length = 50,
            comment = "그룹 약자 (영소문자 , 숫자 , '_' 허용)"
    )
    private String groupAlias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent_group_uid",
            referencedColumnName = "uid", nullable = false,
            comment = "상위 그룹 UID"
    )
    private GroupEntity parentGroupUid;
}
