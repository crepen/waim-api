package com.waim.module.core.domain.user.model.entity;

import com.waim.module.core.common.model.entity.GlobalTimeEntity;
import com.waim.module.core.domain.project.model.entity.ProjectEntity;
import com.waim.module.data.domain.user.log.UserLogAction;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@Table(
        name = "ael_log_user",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "사용자 로그 관리 Table"
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserLogEntity extends GlobalTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "idx" , length = 36 , nullable = false , unique = true,
            comment = "IDX"
    )
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_uid",
            nullable = false,
            comment = "사용자 UID"
    )
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "action" , length = 30 , nullable = false,
            columnDefinition = "VARCHAR(30)",
            comment = "Action Type"
    )
    private UserLogAction logActionType;

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
