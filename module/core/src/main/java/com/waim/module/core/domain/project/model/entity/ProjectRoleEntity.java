package com.waim.module.core.domain.project.model.entity;


import com.waim.module.core.domain.project.model.entity.id.ProjectRoleId;
import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.data.domain.project.ProjectRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@IdClass(ProjectRoleId.class)
@Table(
        name = "aod_project_role",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "프로젝트 - 사용자 권한 관리 Table"
)
public class ProjectRoleEntity {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_uid",
            nullable = false,
            comment = "프로젝트 UID"
    )
    private ProjectEntity project;

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_uid",
            nullable = false,
            comment = "사용자 UID"
    )
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "role" ,
            nullable = false, columnDefinition = "VARCHAR(30)",
            comment = "사용자 프로젝트 역할"
    )
    private ProjectRole role;







    @PrePersist
    protected void onCreate() {

    }

    @PreUpdate
    protected void onUpdate() {

    }

    @PreRemove
    protected void onRemove() {

    }
}
