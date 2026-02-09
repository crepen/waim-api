package com.waim.core.domain.configure.model.entity;

import com.waim.core.common.model.entity.CommonTimestampEntity;
import com.waim.core.domain.user.model.UserState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "global_config")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GlobalConfigEntity extends CommonTimestampEntity {
    @Id
    @Column(name = "config_key" , length = 100 , nullable = false , unique = true)
    private String key;

    @Column(name = "config_value" , length = 1000 )
    private String value;

    @Builder.Default // 빌더 사용 시 기본값 유지
    @Column(name = "is_encrypt", nullable = false)
    private boolean isEncrypt = false;
}
