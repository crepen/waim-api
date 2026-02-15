package com.waim.core.domain.user.model.entity;

import com.waim.core.common.model.entity.CommonTimestampEntity;
import com.waim.core.domain.project.model.entity.ProjectEntity;
import com.waim.core.domain.user.model.dto.enumable.UserRole;
import com.waim.core.domain.user.model.dto.enumable.UserState;
import com.waim.core.domain.user.service.listener.UserEventListener;
import com.waim.core.domain.user.service.listener.UserRoleEventListener;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(
        name = "user",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "사용자 Table"
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(UserEventListener.class)
public class UserEntity extends CommonTimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "uid", length = 36, nullable = false,
            comment = "사용자 Unique ID"
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
            name = "user_email" , length = 200 , nullable = false,
            comment = "사용자 이메일"
    )
    private String userEmail;

    @Column(
            name = "user_email_hash" , length = 300 , nullable = false,
            comment = "사용자 이메일 Hash"
    )
    private String userEmailHash;

    @Builder.Default
    @Enumerated(EnumType.STRING) // DB에 문자열로 저장 (가독성 및 유지보수 유리)
    @Column(
            name = "user_state", length = 20, nullable = false,
            columnDefinition = "VARCHAR(20)",
            comment = "사용자 상태"
    )
    private UserState userState = UserState.PENDING;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRoleEntity> roles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "projectOwner", cascade = CascadeType.ALL)
    private List<ProjectEntity> projects = new ArrayList<>();


    //#region METHODS

    public void addRole(UserRole roleObj) {
        UserRoleEntity role = UserRoleEntity.builder()
                .user(this)
                .role(roleObj.name())
                .build();
        this.roles.add(role);
    }

    public void addRole(String roleStr){
        UserRoleEntity role = UserRoleEntity.builder()
                .user(this)
                .role(roleStr)
                .build();
        this.roles.add(role);
    }

    public void addRoles(String ...roles){
        List<UserRoleEntity> roleList = new ArrayList<>();
        for(String roleStr : roles){
            roleList.add(
                    UserRoleEntity.builder()
                            .user(this)
                            .role(roleStr)
                            .build()
            );
        }

        this.roles.addAll(roleList);
    }


    public boolean isExistRole(String roleStr){
        for(UserRoleEntity role : this.roles){
            if(role.getRole().equals(roleStr)){
                return true;
            }
        }

        return false;
    }

    public void removeRole(String roleStr){
        this.roles.removeIf(role -> role.getRole().equals(roleStr));
    }

    public void removeRoles(String... roles){
        for(String roleStr : roles){
            this.roles.removeIf(role -> role.getRole().equals(roleStr));
        }
    }

    public void removeAllRoles(){
        this.roles.removeAll(this.getRoles());
    }


    //#endregion METHODS

    @PrePersist
    protected void onCreate() {
        if(this.userState == null){
            this.userState = UserState.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {

    }
}
