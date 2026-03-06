package org.example.devroadmapskilltracker.skill;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;


import java.time.LocalDate;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Title is required")
    @Enumerated(EnumType.STRING) // Sparar texten (ex. "BACKLOG") istället för en siffra i databasen
    private SkillStatus status;

    @NotBlank(message = "Description is required")
    @Column(columnDefinition = "TEXT") // MySQl skapar ett större textfält
    private String description;

    @URL(message = "Must be a valid URL")
    private String source;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate dateAdded;

    @NotBlank(message = "Tag is required")
    private String tag; // Ex: "Databas", "Testning", "Ramverk"

    public Skill() {}


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

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
