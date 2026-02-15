package com.waim.core.domain.project.model.entity;

import com.waim.core.common.model.entity.CommonTimestampEntity;
import com.waim.core.domain.project.model.dto.enumable.ProjectRole;
import com.waim.core.domain.project.model.entity.id.ProjectRoleId;
import com.waim.core.domain.user.model.entity.UserEntity;
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
        name = "project_role",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_user_role",
                        columnNames = {"project_uid", "user_uid", "role"}
                )
        },
        comment = "프로젝트 - 사용자 권한 관리 Table"
)
public class ProjectRoleEntity extends CommonTimestampEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_uid", nullable = false,
            comment = "프로젝트 UID"
    )
    private ProjectEntity project;

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_uid", nullable = false,
            comment = "사용자 UID"
    )
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "role" , nullable = false,
            columnDefinition = "VARCHAR(30)",
            comment = "사용자 프로젝트 역할"
    )
    private ProjectRole role;

    @PrePersist
    protected void onCreate() {

    }

    @PreUpdate
    protected void onUpdate() {

    }
}
