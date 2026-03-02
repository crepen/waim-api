package com.waim.module.core.system.config.model.entity;

import com.waim.module.core.common.converter.EntityStringCryptoConverter;
import com.waim.module.core.common.model.entity.GlobalTimeEntity;
import com.waim.module.core.domain.user.listener.UserDataListener;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "aos_system_config",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "시스템 설정 Table"
)
public class SystemConfigEntity extends GlobalTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "idx" ,
            unique = true , nullable = false, length = 36,
            comment = "Index"
    )
    private Long idx;


    @Column(
            name = "config_key",
            unique = true , nullable = false , length = 300,
            comment = "Config Key"
    )
    private String configKey;


    @Column(
            name = "config_value",
            nullable = false,
            length = 2000,
            comment = "Config Value"
    )
    @Convert(converter = EntityStringCryptoConverter.class)
    private String configValue;
}
