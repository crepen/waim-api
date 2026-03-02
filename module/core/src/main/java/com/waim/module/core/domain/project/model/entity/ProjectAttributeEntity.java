package com.waim.module.core.domain.project.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "aod_project_attr",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "프로젝트 Task 속성 Table"

)
public class ProjectAttributeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "idx" ,
            nullable = false , unique = true, updatable = false,
            comment = "IDX"
    )
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_uid",
            nullable = false,
            comment = "프로젝트 UID"
    )
    private ProjectEntity project;

    @Column(
            name = "attr_key",
            nullable = false,
            comment = "Task 속성 키"
    )
    private String attrKey;

    @Column(
            name = "attr_value",
            comment = "Task 속성 값"
    )
    private String attrValue;
}
