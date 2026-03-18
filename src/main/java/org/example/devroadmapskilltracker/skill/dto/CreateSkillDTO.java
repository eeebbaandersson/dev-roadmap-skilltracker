package org.example.devroadmapskilltracker.skill.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.devroadmapskilltracker.skill.SkillStatus;
import org.hibernate.validator.constraints.URL;

public record CreateSkillDTO(
        @NotBlank(message = "A title is required") String title,
        @NotNull(message = "Status is required") SkillStatus status,
        @NotBlank(message = "A description is required") String description,
        @URL(message = "Resource must have a valid URL format") String source,
        @NotBlank(message = "Tag is required") String tag
) {}
