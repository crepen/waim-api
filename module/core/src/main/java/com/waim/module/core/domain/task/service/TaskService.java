package com.waim.module.core.domain.task.service;


import com.waim.module.core.domain.task.model.data.TaskStatus;
import com.waim.module.core.domain.task.model.data.TaskType;
import com.waim.module.core.domain.task.model.entity.TaskEntity;
import com.waim.module.core.domain.task.repository.TaskRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    /**
     * API Loop Task 중 Next Run Time이 도래한 Task 조회
     *
     * @return Task List
     */
    @Transactional
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
                            criteriaBuilder.lessThan(root.get("nextRunTimestamp"), now)
                    )
            );

            return criteriaBuilder.and(predicates);
        });

        return taskRepository.findAll(spec);
    }
}
