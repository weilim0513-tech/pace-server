package com.pace.server.domain.wake.entity;

import com.pace.server.domain.member.entity.User;
import com.pace.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "todo", indexes = {
        @Index(name = "idx_todo_date", columnList = "user_id, target_date")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Todo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String content;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "is_done")
    @Builder.Default
    private boolean isDone = false;

    @Column(name = "display_order")
    @Builder.Default
    private int displayOrder = 0;

    // Business Methods
    public void toggleDone() {
        this.isDone = !this.isDone;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public Todo postponeTo(LocalDate newDate) {
        return Todo.builder()
                .user(this.user)
                .content(this.content)
                .targetDate(newDate)
                .isDone(false)
                .build();
    }
}
