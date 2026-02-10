package com.waim.core.domain.user.model.entity;

import com.waim.core.common.model.entity.CommonTimestampEntity;
import com.waim.core.domain.project.model.entity.ProjectEntity;
import com.waim.core.domain.user.model.UserRole;
import com.waim.core.domain.user.model.UserState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@Table(name = "base_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity extends CommonTimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uid", length = 36, nullable = false)
    private String uid;

    @Column(name = "user_id" , length = 100 , nullable = false)
    private String userId;

    @Column(name = "user_name", length = 100, nullable = false)
    private String userName;

    @Column(name = "user_password", length = 200, nullable = false)
    private String userPassword;

    @Column(name = "user_email" , length = 200 , nullable = false)
    private String userEmail;

    @Column(name = "user_email_hash" , length = 300 , nullable = false)
    private String userEmailHash;

    @Enumerated(EnumType.STRING) // DB에 문자열로 저장 (가독성 및 유지보수 유리)
    @Column(name = "user_state", length = 20, nullable = false)
    @Builder.Default
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
                .role(roleObj.getValue())
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
