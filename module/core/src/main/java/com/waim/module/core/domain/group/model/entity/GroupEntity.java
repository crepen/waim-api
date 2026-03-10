package com.waim.module.core.domain.group.model.entity;

import com.waim.module.core.domain.project.model.entity.ProjectEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "aod_group",
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
            comment = "그룹 약자 (영소문자 , 숫자 , _ 허용)"
    )
    private String groupAlias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent_group_uid",
            referencedColumnName = "uid", nullable = true,
            comment = "상위 그룹 UID"
    )
    private GroupEntity parentGroupUid;

    @Builder.Default
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "parentGroupUid"
    )
    private List<GroupEntity> childGroups = new ArrayList<>();

    @Builder.Default
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "projectGroup"
    )
    private List<ProjectEntity> projects = new ArrayList<>();

        @Builder.Default
        @OneToMany(
                        fetch = FetchType.LAZY,
                        mappedBy = "group",
                        cascade = CascadeType.ALL,
                        orphanRemoval = true
        )
        private List<GroupRoleEntity> groupRoles = new ArrayList<>();

        public void addGroupRole(GroupRoleEntity role) {
                if (this.groupRoles == null) {
                        this.groupRoles = new ArrayList<>();
                }
                this.groupRoles.add(role);
                role.setGroup(this);
        }
}
