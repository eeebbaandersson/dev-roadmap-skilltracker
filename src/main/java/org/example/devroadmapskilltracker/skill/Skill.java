package org.example.devroadmapskilltracker.skill;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "skills")
@EntityListeners(AuditingEntityListener.class)
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING) // Sparar texten (ex. "BACKLOG") istället för en siffra i databasen
    private SkillStatus status;

    @NotBlank(message = "Description is required")
    @Column(columnDefinition = "TEXT") // MySQl skapar ett större textfält
    private String description;

    @URL(message = "Must be a valid URL")
    private String source;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDateTime dateAdded;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @NotBlank(message = "Tag is required")
    private String tag; // Ex: "Databas", "Testning", "Ramverk"

    public Skill() {}

    // Todo: Addera saknade attributer
    public Skill(String title, String description, LocalDateTime dateAdded, LocalDateTime updatedAt, LocalDateTime completedAt, String tag, SkillStatus status) {
        this.title = title;
        this.description = description;
        this.dateAdded = dateAdded;
        this.updatedAt = updatedAt;
        this.completedAt = completedAt;
        this.tag = tag;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SkillStatus getStatus() {
        return status;
    }

    public void setStatus(SkillStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Skill skill)) return false;
        return Objects.equals(id, skill.id) && Objects.equals(title, skill.title) && status == skill.status && Objects.equals(description, skill.description) && Objects.equals(source, skill.source) && Objects.equals(dateAdded, skill.dateAdded) && Objects.equals(updatedAt, skill.updatedAt) && Objects.equals(completedAt, skill.completedAt) && Objects.equals(tag, skill.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, status, description, source, dateAdded, updatedAt, completedAt, tag);
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", source='" + source + '\'' +
                ", dateAdded=" + dateAdded +
                ", updatedAt=" + updatedAt +
                ", completedAt=" + completedAt +
                ", tag='" + tag + '\'' +
                '}';
    }
}
