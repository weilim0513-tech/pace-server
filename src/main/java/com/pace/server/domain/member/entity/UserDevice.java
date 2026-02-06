package com.pace.server.domain.member.entity;

import com.pace.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_device", indexes = {
        @Index(name = "idx_device_user", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserDevice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_uuid", nullable = false, length = 100)
    private String deviceUuid;

    @Column(name = "fcm_token", length = 255)
    private String fcmToken;

    @Column(name = "device_type", length = 20)
    private String deviceType; // ANDROID, IOS

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    // Business Methods
    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
