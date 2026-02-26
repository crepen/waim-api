package com.waim.module.storage.domain.project.entity;

import com.waim.module.storage.common.entity.GlobalTimeEntity;
import com.waim.module.storage.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "role-project-user",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "프로젝트-사용자 권한 Table"
)
public class ProjectUserRoleEntity extends GlobalTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "uid" , unique = true , nullable = false, length = 36,
            comment = "Unique ID"
    )
    private String uid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_uid", referencedColumnName = "uid", nullable = false,
            comment = "프로젝트 UID"
    )
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_uid", referencedColumnName = "uid", nullable = false,
            comment = "사용자 UID"
    )
    private UserEntity user;




    @PrePersist
    protected void onCreate() {

    }

    @PreUpdate
    protected void onUpdate() {

    }
}
