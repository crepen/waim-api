package com.waim.module.storage.domain.task.entity;

import com.waim.module.storage.common.entity.GlobalTimeEntity;
import com.waim.module.storage.domain.project.entity.ProjectEntity;
import com.waim.module.storage.domain.task.dto.TaskStatus;
import com.waim.module.storage.domain.task.dto.TaskType;
import com.waim.module.storage.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "task",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "프로젝트 Task Table"
)
public class TaskEntity extends GlobalTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "uid" ,
            unique = true , nullable = false, length = 36,
            comment = "Task Unique ID"
    )
    private String uid;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_uid", referencedColumnName = "uid" , nullable = false,
            comment = "프로젝트 UID"
    )
    private ProjectEntity project;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "owner_uid" , referencedColumnName = "uid" , nullable = false,
            comment = "소유자 UID"
    )
    private UserEntity owner;


    @Column(
            name = "interval_delay",
            length = 30,
            comment = "Task 반복 간격"
    )
    private String intervalDelay;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "task_type",
            length = 30, columnDefinition = "VARCHAR(30)",
            comment = "Task Type"
    )
    private TaskType taskType;


    @Builder.Default
    @Column(
            name = "next_run_timestamp",
            columnDefinition = "TIMESTAMP",
            comment = "다음 Task 실행 시각"
    )
    private OffsetDateTime nextRunTimestamp = OffsetDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);


    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(
            name = "task_status",
            nullable = false, columnDefinition = "VARCHAR(30)",
            comment = "Task Status"
    )
    private TaskStatus taskStatus = TaskStatus.ACTIVE;


    @Builder.Default
    @OneToMany(mappedBy = "taskUid" , cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    private List<TaskAttributeEntity> taskAttributes = new ArrayList<>();


    @PrePersist
    protected void onCreate() {

    }

    @PreUpdate
    protected void onUpdate() {

    }
}
