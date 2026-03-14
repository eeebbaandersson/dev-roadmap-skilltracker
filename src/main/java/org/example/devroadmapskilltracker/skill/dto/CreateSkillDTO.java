package org.example.devroadmapskilltracker.skill.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.devroadmapskilltracker.skill.SkillStatus;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateSkillDTO(
        @NotBlank(message = "Title is required") String title,
        @NotNull(message = "Status is required") SkillStatus status,
        @NotBlank(message = "Description is required") String description,
        @URL(message = "Must be a valid URL") String source,
        @NotNull(message = "Date is required") LocalDateTime dateAdded,
        @NotBlank(message = "Tag is required") String tag
) {}
