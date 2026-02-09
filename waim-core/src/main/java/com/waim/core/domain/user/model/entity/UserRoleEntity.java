package com.waim.core.domain.user.model.entity;

import com.waim.core.domain.log.repository.listener.UserRoleEntityListener;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@Table(name = "base_user_role")
@EntityListeners(UserRoleEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleEntity {
    @Id
    @Column(name = "uid", nullable = false, length = 100)
    private String uid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uid")
    private UserEntity user;

    @Column(name = "role", nullable = false, length = 100)
    private String role;

    @PrePersist
    protected void onCreate() {
        if(this.uid == null) this.uid = UUID.randomUUID().toString();
    }


}