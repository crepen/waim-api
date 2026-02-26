package com.waim.module.storage.domain.project.entity;

import com.waim.module.storage.common.entity.GlobalTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "project-config",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "프로젝트 설정 Table"
)
public class ProjectConfigEntity extends GlobalTimeEntity {
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


    @Column(
            name = "config_key", nullable = false , length = 300,
            comment = "프로젝트 설정 키"
    )
    private String configKey;


    @Column(
            name = "config_value" , length = 2000,
            comment = "프로젝트 설정 값"
    )
    private String configValue;




    @PrePersist
    protected void onCreate() {

    }

    @PreUpdate
    protected void onUpdate() {

    }
}
