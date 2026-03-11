package com.waim.api.domain.project.controller;

import com.waim.api.common.model.CommonPageable;
import com.waim.api.common.model.response.BasePageableResponse;
import com.waim.api.common.model.response.BaseResponse;
import com.waim.api.domain.project.model.request.UpsertProjectJobRequest;
import com.waim.api.domain.project.model.response.ProjectJobLogResponse;
import com.waim.api.domain.project.model.response.ProjectJobResponse;
import com.waim.module.core.domain.project.model.error.ProjectNotFoundException;
import com.waim.module.core.domain.project.service.ProjectService;
import com.waim.module.core.domain.task.model.data.TaskStatus;
import com.waim.module.core.domain.task.model.data.TaskType;
import com.waim.module.core.domain.task.model.entity.TaskEntity;
import com.waim.module.core.domain.task.model.entity.TaskRunLogEntity;
import com.waim.module.core.domain.task.model.error.TaskNotFoundException;
import com.waim.module.core.domain.task.service.TaskService;
import com.waim.module.data.common.security.SecurityUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/project/{projectUid}/job")
public class ProjectJobController {

    private final ProjectService projectService;
    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "프로젝트 Job 목록 조회")
    public ResponseEntity<?> searchJobs(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PathVariable String projectUid,
            @PageableDefault(size = 20, sort = "updateAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        ensureProjectReadable(projectUid, userDetail);

        Page<TaskEntity> page = taskService.searchProjectTasks(projectUid, pageable);

        var result = page.getContent().stream()
                .map(task -> toJobResponse(task, taskService.getTaskAttributesMap(task.getUid())))
                .toList();

        return ResponseEntity.ok(
                BasePageableResponse.Success.builder()
                        .result(result)
                        .pageable(CommonPageable.cast(page))
                        .build()
        );
    }

    @PutMapping
    @Operation(summary = "프로젝트 Job 생성")
    public ResponseEntity<?> addJob(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PathVariable String projectUid,
            @RequestBody UpsertProjectJobRequest reqBody
    ) {
        var project = ensureProjectReadable(projectUid, userDetail);
        var owner = project.getProjectOwner();

        TaskEntity saved = taskService.addTask(
                project,
                owner,
                parseTaskType(reqBody.getTaskType()),
                reqBody.getIntervalDelay(),
                parseTaskStatus(reqBody.getTaskStatus()),
                reqBody.getAttributes()
        );

        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .result(toJobResponse(saved, reqBody.getAttributes() == null ? Map.of() : reqBody.getAttributes()))
                        .build()
        );
    }

    @PostMapping("/{jobUid}")
    @Operation(summary = "프로젝트 Job 수정")
    public ResponseEntity<?> updateJob(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PathVariable String projectUid,
            @PathVariable String jobUid,
            @RequestBody UpsertProjectJobRequest reqBody
    ) {
        ensureProjectReadable(projectUid, userDetail);
        TaskEntity before = taskService.getTask(jobUid).orElseThrow(TaskNotFoundException::new);

        if (!projectUid.equals(before.getProject().getUid())) {
            throw new TaskNotFoundException();
        }

        TaskEntity updated = taskService.updateTask(
                jobUid,
                StringUtils.hasText(reqBody.getTaskType()) ? parseTaskType(reqBody.getTaskType()) : null,
                reqBody.getIntervalDelay(),
                parseTaskStatus(reqBody.getTaskStatus()),
                reqBody.getAttributes()
        );

        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .result(toJobResponse(updated, taskService.getTaskAttributesMap(updated.getUid())))
                        .build()
        );
    }

    @DeleteMapping("/{jobUid}")
    @Operation(summary = "프로젝트 Job 삭제")
    public ResponseEntity<?> removeJob(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PathVariable String projectUid,
            @PathVariable String jobUid
    ) {
        ensureProjectReadable(projectUid, userDetail);

        TaskEntity before = taskService.getTask(jobUid).orElseThrow(TaskNotFoundException::new);

        if (!projectUid.equals(before.getProject().getUid())) {
            throw new TaskNotFoundException();
        }

        taskService.deleteTask(jobUid);

        return ResponseEntity.ok(BaseResponse.Success.builder().build());
    }

    @GetMapping("/log")
    @Operation(summary = "프로젝트 Job 로그 조회")
    public ResponseEntity<?> searchJobLogs(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PathVariable String projectUid,
            @RequestParam(name = "job_uid", required = false) String jobUid,
            @PageableDefault(size = 50, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        ensureProjectReadable(projectUid, userDetail);

        Page<TaskRunLogEntity> page = taskService.searchProjectTaskLogs(projectUid, jobUid, pageable);

        var result = page.getContent().stream().map(log ->
                ProjectJobLogResponse.builder()
                        .idx(log.getIdx())
                        .projectUid(log.getProjectUid())
                        .taskUid(log.getTaskUid())
                        .taskType(log.getTaskType().name())
                        .runStatus(log.getRunStatus().name())
                        .responseStatus(log.getResponseStatus())
                        .durationMs(log.getDurationMs())
                        .message(log.getMessage())
                        .createTimestamp(log.getCreateAt() == null ? null : log.getCreateAt().toInstant().toEpochMilli())
                        .build()
        ).toList();

        return ResponseEntity.ok(
                BasePageableResponse.Success.builder()
                        .result(result)
                        .pageable(CommonPageable.cast(page))
                        .build()
        );
    }

    private com.waim.module.core.domain.project.model.entity.ProjectEntity ensureProjectReadable(String projectUid, SecurityUserDetail userDetail) {
        return projectService.getProjectInfo(projectUid, userDetail.getUniqueId())
                .orElseThrow(ProjectNotFoundException::new);
    }

    private TaskType parseTaskType(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "INTERFACE", "API_CRAWLER" -> TaskType.API_CRAWLER;
            case "SCHEDULER" -> TaskType.SCHEDULER;
            case "HOOK", "API_HOOK" -> TaskType.API_HOOK;
            default -> TaskType.valueOf(normalized);
        };
    }

    private TaskStatus parseTaskStatus(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return TaskStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }

    private ProjectJobResponse toJobResponse(TaskEntity task, Map<String, String> attrs) {
        return ProjectJobResponse.builder()
                .uid(task.getUid())
                .projectUid(task.getProject().getUid())
                .ownerUid(task.getOwner().getUid())
                .taskType(task.getTaskType().name())
                .taskStatus(task.getTaskStatus().name())
                .intervalDelay(task.getIntervalDelay())
                .nextRunTimestamp(task.getNextRunTimestamp() == null ? null : task.getNextRunTimestamp().toInstant().toEpochMilli())
                .attributes(attrs == null ? Map.of() : attrs)
                .createTimestamp(task.getCreateAt() == null ? null : task.getCreateAt().toInstant().toEpochMilli())
                .updateTimestamp(task.getUpdateAt() == null ? null : task.getUpdateAt().toInstant().toEpochMilli())
                .build();
    }
}
