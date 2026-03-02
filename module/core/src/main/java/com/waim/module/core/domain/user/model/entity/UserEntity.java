package com.waim.module.core.domain.user.model.entity;


import com.waim.module.core.common.converter.EntityStringCryptoConverter;
import com.waim.module.core.common.model.entity.GlobalTimeEntity;
import com.waim.module.core.domain.user.listener.UserDataListener;
import com.waim.module.data.domain.user.UserRole;
import com.waim.module.data.domain.user.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "aod_user",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "사용자 Table"
)
@EntityListeners(UserDataListener.class)
public class UserEntity  extends GlobalTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "uid" ,
            unique = true , nullable = false, length = 36,
            comment = "Unique ID"
    )
    private String uid;

    @Column(
            name = "user_id" ,
            length = 100 , nullable = false,
            comment = "사용자 ID"
    )
    private String userId;

    @Column(
            name = "user_name",
            length = 100, nullable = false,
            comment = "사용자명"
    )
    private String userName;

    @Column(
            name = "user_password",
            length = 200, nullable = false,
            comment = "사용자 비밀번호"
    )
    private String userPassword;

    @Column(
            name = "user_email" ,
            length = 300 , nullable = false,
            comment = "사용자 이메일 (암호화)"
    )
    @Convert(converter = EntityStringCryptoConverter.class)
    private String userEmail;


    @Column(
            name = "user_email_hash" ,
            length = 300 , nullable = false,
            comment = "사용자 이메일 Hash (암호화 검색용)"
    )
    private String userEmailHash;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(
        name = "user_status",
        length = 30 , nullable = false, columnDefinition = "VARCHAR(30)",
        comment = "사용자 상태"
    )
    private UserStatus userStatus = UserStatus.ACTIVE;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(
            name = "user_role",
            length = 30 , nullable = false, columnDefinition = "VARCHAR(30)",
            comment = "사용자 권한"
    )
    private UserRole userRole = UserRole.GENERAL;

    @Builder.Default
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            targetEntity = UserAttributeEntity.class
    )
    private List<UserAttributeEntity> aodUserAttr = new ArrayList<>();



    public void addAttribute(UserAttributeEntity attribute) {
        this.aodUserAttr.add(attribute);
        attribute.setUser(this);
    }


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

