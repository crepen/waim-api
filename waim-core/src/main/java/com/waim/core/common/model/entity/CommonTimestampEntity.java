package com.waim.core.common.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 테이블로 매핑되지 않고 자식 엔티티에게 매핑 정보만 제공
@EntityListeners(AuditingEntityListener.class) // Spring Data JPA Auditing 활성화
public abstract class CommonTimestampEntity {

    @CreationTimestamp // 생성 시 자동 기록
    @Column(
            name = "create_at", nullable = false, updatable = false ,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            comment = "생성일"
    )
    private LocalDateTime createAt;

    @UpdateTimestamp // 수정 시 자동 기록
    @Column(
            name = "update_at", nullable = false ,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            comment = "마지막 수정일"
    )
    private LocalDateTime updateAt;


}
