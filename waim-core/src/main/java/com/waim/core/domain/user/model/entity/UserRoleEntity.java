package com.waim.core.domain.user.model.entity;

import com.waim.core.domain.log.repository.listener.UserRoleEntityListener;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@Table(
        name = "user_role",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "사용자 권한 관리 Table"
)
@EntityListeners(UserRoleEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "uid", nullable = false, length = 36,
            comment = "사용자 권한 Unique ID"
    )
    private String uid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_uid",
            comment = "사용자 UID"
    )
    private UserEntity user;

    @Column(
            name = "role", nullable = false, length = 100,
            comment = "사용자 부여 권한"
    )
    private String role;

    @PrePersist
    protected void onCreate() {

    }


}