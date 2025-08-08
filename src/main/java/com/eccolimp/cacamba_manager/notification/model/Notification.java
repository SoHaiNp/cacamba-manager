package com.eccolimp.cacamba_manager.notification.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NotificationType type;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 400)
    private String message;

    @Column(name = "aluguel_id")
    private Long aluguelId;

    @Column(nullable = false)
    private boolean readFlag = false;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate = LocalDate.now();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Notification() {}

    public Notification(NotificationType type, String title, String message, Long aluguelId) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.aluguelId = aluguelId;
    }

    public Long getId() { return id; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Long getAluguelId() { return aluguelId; }
    public void setAluguelId(Long aluguelId) { this.aluguelId = aluguelId; }
    public boolean isReadFlag() { return readFlag; }
    public void setReadFlag(boolean readFlag) { this.readFlag = readFlag; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}


