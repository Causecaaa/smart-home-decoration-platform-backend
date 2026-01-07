package org.homedecoration.identity.designer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.homedecoration.identity.user.entity.User;

@Getter
@Setter
@Entity
@Table(name = "designer")
public class Designer {

    @Id
    @Column(name = "user_id")
    private Long userId;

    /**
     * 与 User 一对一关联
     * 设计师本质仍然是用户
     */
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    // ===== 设计师职业信息 =====

    @Column(name = "real_name", length = 50)
    private String realName;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "style", length = 100)
    private String style;

    @Column(name = "bio", length = 500)
    private String bio;

    // ===== 平台业务字段 =====

    @Column(name = "rating")
    private Double rating;

    @Column(name = "order_count")
    private Integer orderCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "verify_status", length = 20)
    private VerifyStatus verifyStatus;

    @Column(name = "enabled")
    private Boolean enabled;

    public enum VerifyStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
