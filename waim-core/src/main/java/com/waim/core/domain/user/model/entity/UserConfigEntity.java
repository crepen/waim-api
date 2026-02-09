package com.waim.core.domain.user.model.entity;


import com.waim.core.domain.user.model.UserState;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@Table(name = "user_config")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", nullable = false)
    private Integer id;

    @Column(name = "user_uid", nullable = false, length = 100)
    private String userUid;

    @Column(name = "config_key", nullable = false, length = 100)
    private String configKey;

    @Column(name = "config_value", length = 100)
    private String configValue;

    @CreationTimestamp // INSERT 시점의 시간 자동 입력
    @Column(name = "create_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createAt;

    @UpdateTimestamp // UPDATE 시점의 시간 자동 업데이트
    @Column(name = "update_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        if(createAt == null) {
            this.createAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if(this.updateAt == null){
            this.updateAt = LocalDateTime.now();
        }
    }
}
