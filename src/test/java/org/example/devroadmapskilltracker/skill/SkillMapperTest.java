package org.example.devroadmapskilltracker.skill;

import org.example.devroadmapskilltracker.skill.dto.CreateSkillDTO;
import org.example.devroadmapskilltracker.skill.dto.SkillDTO;
import org.example.devroadmapskilltracker.skill.dto.UpdateSkillDTO;
import org.example.devroadmapskilltracker.skill.service.SkillMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SkillMapperTest {

    private SkillMapper skillMapper;

    @BeforeEach
    void setUp() {
        skillMapper = new SkillMapper();
    }

    // CreateSkillDTO --> Entity
    @Test
    void shouldMapCreateSkillDTOToEntity() {
        // Arrange
        CreateSkillDTO dto = new CreateSkillDTO(
                "Java Spring Boot",
                SkillStatus.IN_PROGRESS,
                "Learning to build REST APIs",
                "https://spring.io",
                "Backend"
        );

        // Act
        Skill result = skillMapper.toEntity(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Java Spring Boot");
        assertThat(result.getStatus()).isEqualTo(SkillStatus.IN_PROGRESS);
        assertThat(result.getDescription()).isEqualTo("Learning to build REST APIs");
        assertThat(result.getSource()).isEqualTo("https://spring.io");
        assertThat(result.getDateAdded()).isNull();
        assertThat(result.getTag()).isEqualTo("Backend");
        assertThat(result.getId()).isNull();
    }

    // SkillDTO toDTO
    @Test
    void shouldMapEntityToSkillDTO() {
        // Arrange
        LocalDateTime addedTime = LocalDateTime.now().minusDays(1);
        Skill skill = new Skill(
                1L,
                "Docker Basics",
                SkillStatus.BACKLOG,
                "Improving Docker knowledge",
                "https://www.docker.com",
                addedTime,
                "DevOps"
        );

        // Act
        SkillDTO result = skillMapper.toDTO(skill);

        // Assert
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Docker Basics");
        assertThat(result.status()).isEqualTo(SkillStatus.BACKLOG);
        assertThat(result.description()).isEqualTo("Improving Docker knowledge");
        assertThat(result.source()).isEqualTo("https://www.docker.com");
        assertThat(result.dateAdded()).isEqualTo(addedTime);
        assertThat(result.tag()).isEqualTo("DevOps");

    }


    // UpdateSkillDTO
    @Test
    void shouldUpdateExistingEntityFromUpdateSkillDTO() {
        // Arrange
        LocalDateTime originalDate = LocalDateTime.now().minusWeeks(1);

        Skill existingSkill = new Skill(
                5L,
                "Old Title",
                SkillStatus.BACKLOG,
                "Old description",
                "Old Source",
                originalDate,
                "Old Tag"
        );



        // DTO med ny data
        UpdateSkillDTO updatedDto = new UpdateSkillDTO(
                5L,
                "New Awesome Title",
                SkillStatus.MASTERED,
                "Updated description",
                "Source",
                "Java"
        );

        // Act
        skillMapper.updateEntityFromDTO(updatedDto, existingSkill);

        // Assert
        assertThat(existingSkill.getId()).isEqualTo(5L);
        assertThat(existingSkill.getTitle()).isEqualTo("New Awesome Title");
        assertThat(existingSkill.getStatus()).isEqualTo(SkillStatus.MASTERED);
        assertThat(existingSkill.getDescription()).isEqualTo("Updated description");
        assertThat(existingSkill.getSource()).isEqualTo("Source");
        assertThat(existingSkill.getDateAdded()).isEqualTo(originalDate);
        assertThat(existingSkill.getTag()).isEqualTo("Java");

    }

}
