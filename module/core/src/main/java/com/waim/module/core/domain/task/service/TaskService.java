package com.waim.module.core.domain.task.service;


import com.waim.module.core.domain.task.model.data.TaskStatus;
import com.waim.module.core.domain.task.model.data.TaskType;
import com.waim.module.core.domain.task.model.data.TaskRunStatus;
import com.waim.module.core.domain.task.model.entity.TaskEntity;
import com.waim.module.core.domain.task.model.entity.TaskAttributeEntity;
import com.waim.module.core.domain.task.model.entity.TaskRunLogEntity;
import com.waim.module.core.domain.task.model.error.TaskIntervalTooShortException;
import com.waim.module.core.domain.task.model.error.TaskInvalidTypeException;
import com.waim.module.core.domain.task.model.error.TaskNotFoundException;
import com.waim.module.core.domain.project.model.entity.ProjectEntity;
import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.core.domain.task.repository.TaskAttributeRepository;
import com.waim.module.core.domain.task.repository.TaskRunLogRepository;
import com.waim.module.core.domain.task.repository.TaskRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {
        private static final long MIN_LOOP_INTERVAL_SECONDS = 10L;
        private static final String ATTR_SOURCE_URL = "SOURCE_URL";
        private static final String ATTR_TARGET_URL = "TARGET_URL";

    private final TaskRepository taskRepository;
        private final TaskAttributeRepository taskAttributeRepository;
        private final TaskRunLogRepository taskRunLogRepository;

    /**
     * API Loop Task 중 Next Run Time이 도래한 Task 조회
     *
     * @return Task List
     */
        @Transactional(readOnly = true)
    public List<TaskEntity> getLoopActiveTask(){
        Specification<TaskEntity> spec = ((root, query, criteriaBuilder) -> {
            query.distinct(true);

            OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    criteriaBuilder.equal(root.get("taskStatus") , TaskStatus.ACTIVE)
            );

            predicates.add(
                    root.get("taskType").in(
                            TaskType.API_CRAWLER,
                            TaskType.SCHEDULER
                    )
            );

            predicates.add(
                    criteriaBuilder.or(
                            criteriaBuilder.isNull(root.get("nextRunTimestamp")),
                            criteriaBuilder.lessThanOrEqualTo(root.get("nextRunTimestamp"), now)
                    )
            );

            return criteriaBuilder.and(predicates);
        });

        return taskRepository.findAll(spec);
    }

        @Transactional(readOnly = true)
        public Page<TaskEntity> searchProjectTasks(String projectUid, Pageable pageable) {
                Specification<TaskEntity> spec = (root, query, cb) -> cb.equal(root.get("project").get("uid"), projectUid);
                return taskRepository.findAll(spec, pageable);
        }

        @Transactional(readOnly = true)
        public Optional<TaskEntity> getTask(String taskUid) {
                return taskRepository.findById(taskUid);
        }

        @Transactional
        public TaskEntity addTask(
                        ProjectEntity project,
                        UserEntity owner,
                        TaskType taskType,
                        String intervalDelay,
                        TaskStatus taskStatus,
                        Map<String, String> attributes
        ) {
                TaskType resolvedType = normalizeTaskType(taskType);
                validateLoopTaskIntervalIfNeeded(resolvedType, intervalDelay);
                validateTaskAttributesByType(resolvedType, attributes);

                TaskEntity taskEntity = TaskEntity.builder()
                                .project(project)
                                .owner(owner)
                                .taskType(resolvedType)
                                .intervalDelay(intervalDelay)
                                .taskStatus(taskStatus == null ? TaskStatus.ACTIVE : taskStatus)
                                .build();

                TaskEntity saved = taskRepository.save(taskEntity);
                replaceTaskAttributes(saved.getUid(), attributes);
                return saved;
        }

        @Transactional
        public TaskEntity updateTask(
                        String taskUid,
                        TaskType taskType,
                        String intervalDelay,
                        TaskStatus taskStatus,
                        Map<String, String> attributes
        ) {
                TaskEntity taskEntity = taskRepository.findById(taskUid).orElseThrow(TaskNotFoundException::new);

                if (taskType != null) {
                        TaskType resolvedType = normalizeTaskType(taskType);
                        taskEntity.setTaskType(resolvedType);
                }

                if (StringUtils.hasText(intervalDelay)) {
                        validateLoopTaskIntervalIfNeeded(taskEntity.getTaskType(), intervalDelay);
                        taskEntity.setIntervalDelay(intervalDelay.trim());
                }

                if (taskStatus != null) {
                        taskEntity.setTaskStatus(taskStatus);
                }

                TaskEntity saved = taskRepository.save(taskEntity);

                if (attributes != null) {
                        validateTaskAttributesByType(taskEntity.getTaskType(), attributes);
                        replaceTaskAttributes(saved.getUid(), attributes);
                }

                return saved;
        }

        @Transactional
        public void deleteTask(String taskUid) {
                TaskEntity taskEntity = taskRepository.findById(taskUid).orElseThrow(TaskNotFoundException::new);
                taskAttributeRepository.deleteAllByTaskUid(taskUid);
                taskRepository.delete(taskEntity);
        }

        @Transactional(readOnly = true)
        public Page<TaskRunLogEntity> searchProjectTaskLogs(String projectUid, String taskUid, Pageable pageable) {
                Specification<TaskRunLogEntity> spec = (root, query, cb) -> {
                        List<Predicate> predicates = new ArrayList<>();
                        predicates.add(cb.equal(root.get("projectUid"), projectUid));

                        if (StringUtils.hasText(taskUid)) {
                                predicates.add(cb.equal(root.get("taskUid"), taskUid));
                        }

                        return cb.and(predicates.toArray(new Predicate[0]));
                };

                return taskRunLogRepository.findAll(spec, pageable);
        }

        @Transactional
        public void writeRunLog(
                        String projectUid,
                        String taskUid,
                        TaskType taskType,
                        TaskRunStatus runStatus,
                        Integer responseStatus,
                        Long durationMs,
                        String message
        ) {
                TaskRunLogEntity logEntity = TaskRunLogEntity.builder()
                                .projectUid(projectUid)
                                .taskUid(taskUid)
                                .taskType(taskType)
                                .runStatus(runStatus)
                                .responseStatus(responseStatus)
                                .durationMs(durationMs)
                                .message(message)
                                .build();

                taskRunLogRepository.save(logEntity);
        }

        @Transactional(readOnly = true)
        public Map<String, String> getTaskAttributesMap(String taskUid) {
                return taskAttributeRepository.findAllByTaskUid(taskUid).stream()
                                .collect(Collectors.toMap(
                                                TaskAttributeEntity::getAttrKey,
                                                x -> x.getAttrValue() == null ? "" : x.getAttrValue(),
                                                (left, right) -> right,
                                                HashMap::new
                                ));
        }

        @Transactional(readOnly = true)
        public List<TaskAttributeEntity> getTaskAttributes(String taskUid) {
                return taskAttributeRepository.findAllByTaskUid(taskUid);
        }

        private void replaceTaskAttributes(String taskUid, Map<String, String> attributes) {
                taskAttributeRepository.deleteAllByTaskUid(taskUid);

                if (attributes == null || attributes.isEmpty()) {
                        return;
                }

                List<TaskAttributeEntity> entities = attributes.entrySet().stream()
                                .map(entry -> TaskAttributeEntity.builder()
                                                .taskUid(taskUid)
                                                .attrKey(entry.getKey())
                                                .attrValue(entry.getValue())
                                                .build()
                                )
                                .toList();

                taskAttributeRepository.saveAll(entities);
        }

        private TaskType normalizeTaskType(TaskType taskType) {
                if (taskType == null) {
                        throw new TaskInvalidTypeException();
                }

                return taskType;
        }

        private void validateLoopTaskIntervalIfNeeded(TaskType taskType, String intervalDelay) {
                if (taskType != TaskType.API_CRAWLER && taskType != TaskType.SCHEDULER) {
                        return;
                }

                if (!StringUtils.hasText(intervalDelay)) {
                        throw new TaskIntervalTooShortException();
                }

                long intervalSec;

                try {
                        intervalSec = DurationStyle.SIMPLE.parse(intervalDelay.trim()).toSeconds();
                }
                catch (Exception ex) {
                        throw new TaskIntervalTooShortException();
                }

                if (intervalSec < MIN_LOOP_INTERVAL_SECONDS) {
                        throw new TaskIntervalTooShortException();
                }
        }

        private void validateTaskAttributesByType(TaskType taskType, Map<String, String> attributes) {
                if (taskType == TaskType.API_HOOK) {
                        return;
                }

                String sourceUrl = attributes == null ? null : attributes.get(ATTR_SOURCE_URL);
                if (!StringUtils.hasText(sourceUrl)) {
                        throw new TaskInvalidTypeException();
                }

                if (taskType == TaskType.API_CRAWLER) {
                        String targetUrl = attributes.get(ATTR_TARGET_URL);
                        if (!StringUtils.hasText(targetUrl)) {
                                throw new TaskInvalidTypeException();
                        }
                }
        }
}
