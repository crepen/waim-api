package com.waim.core.domain.log.model.entity;

import com.waim.core.common.model.entity.CommonTimestampEntity;
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
@Table(
        name = "log_user_role",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "사용자 권한 관리 로그 Table"
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserRoleLogEntity extends CommonTimestampEntity {
    @Id
    @Column(
            name = "uid" , length = 100 , nullable = false , unique = true,
            comment = "사용자 권한 로그 Unique ID"
    )
    private String uid;

    @Column(
            name = "user_uid" , length = 100 , nullable = false,
            comment = "사용자 UID"
    )
    private String userUid;

    @Column(
            name = "role" , length = 100 , nullable = false,
            comment = "사용자 부여 권한"
    )
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "state" , length = 30 , nullable = false ,
            columnDefinition = "VARCHAR(30)",
            comment = "로그 상태"
    )
    private UserRoleLogState state;

    @Column(
            name = "ip" , length = 100 , nullable = false,
            comment = "변경 IP"
    )
    private String ip;

    @Column(
            name = "act_user_uid" ,length = 100 , nullable = false,
            comment = "변경 사용자 UID"
    )
    private String actUserUid;


    @PrePersist
    protected void onCreate() {
        if (this.uid == null) {
            this.uid = UUID.randomUUID().toString();
        }
    }
}
