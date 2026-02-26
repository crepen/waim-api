package com.waim.core.domain.user.model.entity;


import com.waim.core.common.model.entity.CommonTimestampEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@Table(
        name = "user_config",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "사용자 설정 관리 Table"
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserConfigEntity extends CommonTimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "idx", nullable = false,
            comment = "사용자 설정 IDX"
    )
    private Integer id;

    @Column(
            name = "user_uid", nullable = false, length = 100,
            comment = "사용자 UID"
    )
    private String userUid;

    @Column(
            name = "config_key", nullable = false, length = 100,
            comment = "사용자 설정 키"
    )
    private String configKey;

    @Column(
            name = "config_value", length = 100,
            comment = "사용자 설정 값"
    )
    private String configValue;


    @PrePersist
    protected void onCreate() {

    }

    @PreUpdate
    protected void onUpdate() {

    }
}
