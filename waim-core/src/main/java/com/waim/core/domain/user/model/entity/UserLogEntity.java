package com.waim.core.domain.user.model.entity;

import com.waim.core.common.model.entity.CommonTimestampEntity;
import com.waim.core.domain.user.model.dto.enumable.UserEventAction;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@Table(
        name = "log_user",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "사용자 로그 관리 Table"
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserLogEntity extends CommonTimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "uid" , length = 36 , nullable = false , unique = true,
            comment = "사용자 로그 Unique ID"
    )
    private String uid;

    @Column(
            name = "user_uid" , length = 36 , nullable = false,
            comment = "사용자 UID"
    )
    private String userUid;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "log_state" , length = 30 , nullable = false,
            columnDefinition = "VARCHAR(30)",
            comment = "로그 상태"
    )
    private UserEventAction logState;

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

    }

    @PreUpdate
    protected void onUpdate() {

    }

    @PreRemove
    protected void onRemove() {

    }
}
