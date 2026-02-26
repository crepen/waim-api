package com.waim.core.domain.configure.model.entity;

import com.waim.core.common.model.entity.CommonTimestampEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "global_config",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "시스템 설정 관리 Table"
)
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GlobalConfigEntity extends CommonTimestampEntity {
    @Id
    @Column(
            name = "config_key" , length = 100 , nullable = false , unique = true ,
            comment = "설정 키"
    )
    private String key;

    @Column(
            name = "config_value" , length = 1000,
            comment = "설정 값"
    )
    private String value;

    @Builder.Default // 빌더 사용 시 기본값 유지
    @Column(
            name = "is_encrypt", nullable = false ,
            columnDefinition = "TINYINT(1)",
            comment = "설정 암호화 여부"
    )
    private boolean isEncrypt = false;
}
