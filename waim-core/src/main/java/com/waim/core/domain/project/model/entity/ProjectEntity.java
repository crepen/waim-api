package com.waim.core.domain.project.model.entity;

import com.waim.core.common.model.entity.CommonTimestampEntity;
import com.waim.core.domain.project.model.entity.listener.ProjectEntityListener;
import com.waim.core.domain.user.model.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;


/**
 * Project Entity
 */
@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "project")
@EntityListeners(ProjectEntityListener.class)
public class ProjectEntity extends CommonTimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uid" , unique = true , nullable = false)
    private String uid;

    /**
     * 프로젝트 약칭
     * 
     * @apiNote 영문(소문자) , 숫자만 허용
     */
    @Column(name = "project_alias" , unique = true , nullable = false)
    private String projectAlias;

    @Column(name = "project_name" , unique = true , nullable = false)
    private String projectName;


    @ManyToOne(fetch = FetchType.LAZY) // 성능을 위해 지연 로딩 권장
    @JoinColumn(name = "project_owner_uid", referencedColumnName = "uid", nullable = false)
    private UserEntity projectOwner;

    @PrePersist
    protected void onCreate() {

    }
}
