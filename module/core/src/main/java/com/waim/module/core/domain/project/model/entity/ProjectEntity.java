package com.waim.module.core.domain.project.model.entity;

import com.waim.module.core.common.model.entity.GlobalTimeEntity;
import com.waim.module.core.domain.project.listener.ProjectDataListener;
import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.data.domain.project.ProjectStatus;
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
        name = "aob_project",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "프로젝트 Table"
)
@EntityListeners(ProjectDataListener.class)
public class ProjectEntity extends GlobalTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "uid",
            unique = true, nullable = false, length = 36,
            comment = "Unique ID"
    )
    private String uid;

    @Column(
            name = "project_alias",
            unique = true, nullable = false,
            comment = "프로젝트 약자 (영문(소문자) , 숫자만 허용)"
    )
    private String projectAlias;

    @Column(
            name = "project_name",
            unique = true, nullable = false,
            comment = "프로젝트명"
    )
    private String projectName;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(
            name = "project_status",
            nullable = false, columnDefinition = "VARCHAR(20)",
            comment = "프로젝트 상태"
    )
    private ProjectStatus projectStatus = ProjectStatus.ACTIVE;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_owner_uid",
            referencedColumnName = "uid", nullable = false,
            comment = "프로젝트 소유자 UID"
    )
    private UserEntity projectOwner;



    @Builder.Default
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProjectRoleEntity> projectRoles = new ArrayList<>();


    public void addProjectRole(ProjectRoleEntity role) {
        if (this.projectRoles == null) {
            this.projectRoles = new ArrayList<>();
        }
        this.projectRoles.add(role);
        role.setProject(this);
    }



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
