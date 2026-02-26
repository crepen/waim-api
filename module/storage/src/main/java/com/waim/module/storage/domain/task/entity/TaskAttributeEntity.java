package com.waim.module.storage.domain.task.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "task-attr",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "프로젝트 Task 속성 Table"
        
)
public class TaskAttributeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "idx" ,
            nullable = false , unique = true, updatable = false,
            comment = "IDX"
    )
    private Long idx;

    @Column(
            name = "task_uid",
            nullable = false ,
            comment = "Task UID"
    )
    private String taskUid;

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
