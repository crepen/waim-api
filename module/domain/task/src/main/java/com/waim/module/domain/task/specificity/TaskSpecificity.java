package com.waim.module.domain.task.specificity;

import com.waim.module.storage.domain.task.dto.TaskStatus;
import com.waim.module.storage.domain.task.dto.TaskType;
import com.waim.module.storage.domain.task.entity.TaskEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;



public class TaskSpecificity {
    public static Specification<TaskEntity> findType(TaskType type){
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    /**
     * API Loop Task 중 Next Run Time이 도래한 Task 조회
     *
     * @return Task List
     */
    public static Specification<TaskEntity> findLoopTaskActiveItem(){
        return (root, query, cb) -> {

            query.distinct(true);

            OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.equal(root.get("taskStatus") , TaskStatus.ACTIVE)
            );

            predicates.add(
                    root.get("taskType").in(
                            TaskType.API_CRAWLER,
                            TaskType.SCHEDULER
                    )
            );

            predicates.add(
                    cb.or(
                            cb.isNull(root.get("nextRunTimestamp")),
                            cb.lessThan(root.get("nextRunTimestamp"), now)
                    )
            );

            return cb.and(predicates);
        };
    }
}
