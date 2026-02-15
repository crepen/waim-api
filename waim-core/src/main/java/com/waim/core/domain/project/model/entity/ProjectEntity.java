package com.waim.core.domain.project.model.entity;

import com.waim.core.common.model.entity.CommonTimestampEntity;
import com.waim.core.domain.project.model.dto.ProjectData;
import com.waim.core.domain.project.model.dto.enumable.ProjectRole;
import com.waim.core.domain.project.model.dto.enumable.ProjectStatus;
import com.waim.core.domain.project.service.listener.ProjectEventListener;
import com.waim.core.domain.user.model.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


/**
 * Project Entity
 */
@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
        name = "project",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "프로젝트 Table"
)
@EntityListeners(ProjectEventListener.class)
public class ProjectEntity extends CommonTimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "uid" , unique = true , nullable = false, length = 36,
            comment = "프로젝트 Unique ID"
    )
    private String uid;

    @Column(
            name = "project_alias" , unique = true , nullable = false,
            comment = "프로젝트 약자 (영문(소문자) , 숫자만 허용)"
    )
    private String projectAlias;

    @Column(
            name = "project_name" , unique = true , nullable = false,
            comment = "프로젝트명"
    )
    private String projectName;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(
            name = "project_status", nullable = false,
            columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'",
            comment = "프로젝트 상태"
    )
    private ProjectStatus projectStatus = ProjectStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY) // 성능을 위해 지연 로딩 권장
    @JoinColumn(
            name = "project_owner_uid", referencedColumnName = "uid", nullable = false,
            comment = "프로젝트 소유자 UID"
    )
    private UserEntity projectOwner;

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectRoleEntity> projectRoles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {

    }

    @PreUpdate
    protected void onUpdate() {

    }



    // region ##### METHOD ######

    public void addRole(ProjectRoleEntity ...roles) {
        for(ProjectRoleEntity role : roles) {
            this.projectRoles.add(role);
            role.setProject(this);
        }

    }

    public void addRole(UserEntity actionUserEntity, ProjectRole ...roles){
        for(ProjectRole role : roles){
            var roleEntity = ProjectRoleEntity.builder()
                    .user(actionUserEntity)
                    .role(role)
                    .build();

            this.projectRoles.add(roleEntity);
            roleEntity.setProject(this);
        }
    }


    public ProjectData castDataDto(){
        return ProjectData.builder()
                .uid(this.getUid())
                .projectName(this.getProjectName())
                .projectAlias(this.getProjectAlias())
                .projectOwnerName(this.getProjectOwner().getUserName())
                .projectOwnerUid(this.getProjectOwner().getUid())
                .createTimestamp(
                        this.getCreateAt()
                                .atZone(ZoneId.systemDefault())
                                .toInstant().toEpochMilli()
                )
                .updateTimestamp(
                        this.getUpdateAt()
                                .atZone(ZoneId.systemDefault())
                                .toInstant().toEpochMilli()
                )
                .build();
    }

    // endregion ##### METHOD ######
}
