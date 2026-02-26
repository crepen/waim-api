package com.waim.module.storage.domain.task.entity;

import com.waim.module.storage.common.entity.GlobalTimeEntity;
import com.waim.module.storage.domain.project.entity.ProjectEntity;
import com.waim.module.storage.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "task",
        options = "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci",
        comment = "프로젝트 Task Table"
)
public class TaskEntity extends GlobalTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "uid" ,
            unique = true , nullable = false, length = 36,
            comment = "Task Unique ID"
    )
    private String uid;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_uid", referencedColumnName = "uid" , nullable = false,
            comment = "프로젝트 UID"
    )
    private ProjectEntity project;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "owner_uid" , referencedColumnName = "uid" , nullable = false,
            comment = "소유자 UID"
    )
    private UserEntity owner;


    @Column(
            name = "role",
            length = 100 , nullable = false
    )
    private String role;



    @PrePersist
    protected void onCreate() {

    }

    @PreUpdate
    protected void onUpdate() {

    }
}
