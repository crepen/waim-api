package com.waim.core.domain.log.model.entity;

import com.waim.core.domain.log.model.UserRoleLogState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@Table(name = "base_log_user_role")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserRoleLogEntity {
    @Id
    @Column(name = "uid" , length = 100 , nullable = false)
    private String uid;

    @Column(name = "user_uid" , length = 100 , nullable = false)
    private String userUid;

    @Column(name = "role" , length = 100 , nullable = false)
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "state" , length = 40 , nullable = false)
    private UserRoleLogState state;

    @Column(name = "ip" , length = 100 , nullable = false)
    private String ip;

    @Column(name = "act_user_uid" ,length = 100 , nullable = false)
    private String actUserUid;

    @CreationTimestamp // 로그 생성 시간 자동 기록
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @PrePersist
    protected void onCreate() {
        if (this.uid == null) {
            this.uid = UUID.randomUUID().toString();
        }
    }
}
