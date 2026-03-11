package com.waim.module.core.domain.task.model.entity;

import com.waim.module.core.common.model.entity.GlobalTimeEntity;
import com.waim.module.core.domain.task.model.data.TaskRunStatus;
import com.waim.module.core.domain.task.model.data.TaskType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "aod_task_run_log",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "프로젝트 Task 실행 로그"
)
public class TaskRunLogEntity extends GlobalTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", nullable = false, updatable = false, unique = true)
    private Long idx;

    @Column(name = "project_uid", nullable = false, length = 36)
    private String projectUid;

    @Column(name = "task_uid", nullable = false, length = 36)
    private String taskUid;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, length = 30)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(name = "run_status", nullable = false, length = 30)
    private TaskRunStatus runStatus;

    @Column(name = "response_status")
    private Integer responseStatus;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "message", length = 1000)
    private String message;
}
