package com.waim.module.storage.domain.user.entity;

import com.waim.module.storage.common.converter.EntityStringCryptoConverter;
import com.waim.module.storage.common.entity.GlobalTimeEntity;
import com.waim.module.storage.domain.user.listener.UserEntityListener;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "user",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "사용자 Table"
)
@EntityListeners(UserEntityListener.class)
public class UserEntity  extends GlobalTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "uid" , unique = true , nullable = false, length = 36,
            comment = "Unique ID"
    )
    private String uid;

    @Column(
            name = "user_id" , length = 100 , nullable = false,
            comment = "사용자 ID"
    )
    private String userId;

    @Column(
            name = "user_name", length = 100, nullable = false,
            comment = "사용자명"
    )
    private String userName;

    @Column(
            name = "user_password", length = 200, nullable = false,
            comment = "사용자 비밀번호"
    )
    private String userPassword;

    @Column(
            name = "user_email" , length = 300 , nullable = false,
            comment = "사용자 이메일 (암호화)"
    )
    @Convert(converter = EntityStringCryptoConverter.class)
    private String userEmail;


    @Column(
            name = "user_email_hash" , length = 300 , nullable = false,
            comment = "사용자 이메일 Hash (암호화 검색용)"
    )
    private String userEmailHash;


    @PrePersist
    protected void onCreate() {

    }

    @PreUpdate
    protected void onUpdate() {

    }
}
