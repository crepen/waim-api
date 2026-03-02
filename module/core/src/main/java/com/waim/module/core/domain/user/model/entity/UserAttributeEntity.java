package com.waim.module.core.domain.user.model.entity;

import com.waim.module.core.common.converter.EntityStringCryptoConverter;
import com.waim.module.core.domain.user.listener.UserAttributeDataListener;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "aod_user_attr",
        indexes = @Index(name = "idx_user_attr_user_uid", columnList = "user_uid"),
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "사용자 속성 Table"
)
@EntityListeners(UserAttributeDataListener.class)
public class UserAttributeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "idx" ,
            nullable = false , unique = true, updatable = false,
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

    @Column(
            name = "attr_key",
            nullable = false,
            comment = "속성 키"
    )
    private String attrKey;

    @Column(
            name = "attr_value",
            comment = "속성 값"
    )
    @Convert(converter = EntityStringCryptoConverter.class)
    private String attrValue;

}
