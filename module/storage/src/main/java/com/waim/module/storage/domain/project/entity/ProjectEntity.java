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
        name = "project",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "프로젝트 Table"
)
public class ProjectEntity extends GlobalTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "uid" , unique = true , nullable = false, length = 36,
            comment = "Unique ID"
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


}
