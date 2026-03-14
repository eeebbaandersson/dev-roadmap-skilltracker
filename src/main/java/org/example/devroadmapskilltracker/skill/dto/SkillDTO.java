package org.example.devroadmapskilltracker.skill.dto;

import org.example.devroadmapskilltracker.skill.SkillStatus;

import java.time.LocalDateTime;

public record SkillDTO(
        Long id,
        String title,
        SkillStatus status,
        String description,
        String source,
        LocalDateTime dateAdded,
        LocalDateTime updatedAt,
        LocalDateTime completedAt,
        String tag
        ) {}
