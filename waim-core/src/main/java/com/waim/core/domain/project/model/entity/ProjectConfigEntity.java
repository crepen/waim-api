package com.waim.core.domain.project.model.entity;


import com.waim.core.common.config.converter.DataCryptoConverter;
import com.waim.core.common.model.entity.CommonTimestampEntity;
import com.waim.core.domain.project.model.entity.id.ProjectConfigId;
import com.waim.core.domain.project.service.listener.ProjectConfigEventListener;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@IdClass(ProjectConfigId.class)
@EntityListeners(ProjectConfigEventListener.class)
@Table(
        name = "project_config",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_config",
                        columnNames = {"project_uid" , "config_key"}
                )
        },
        comment = "프로젝트 설정 관리 Table"
)
public class ProjectConfigEntity extends CommonTimestampEntity {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_uid", nullable = false,
            comment = "프로젝트 UID"
    )
    private ProjectEntity project;

    @Column(
            name = "config_key", nullable = false, length = 100,
            comment = "프로젝트 설정 KEY"
    )
    private String configKey;

    @Column(
            name = "config_value", nullable = false, length = 500,
            comment = "프로젝트 설정 VALUE"
    )
    @Convert(converter = DataCryptoConverter.class)
    private String configValue;

    @PrePersist
    protected void onCreate() {

    }

    @PreUpdate
    protected void onUpdate() {

    }
}
