package org.example.devroadmapskilltracker.skill;

import org.example.devroadmapskilltracker.skill.dto.CreateSkillDTO;
import org.example.devroadmapskilltracker.skill.dto.SkillDTO;
import org.example.devroadmapskilltracker.skill.dto.UpdateSkillDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SkillMapperTest {

    private  SkillMapper skillMapper;

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
                LocalDate.now(),
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
        assertThat(result.getDateAdded()).isEqualTo(LocalDate.now());
        assertThat(result.getTag()).isEqualTo("Backend");
        assertThat(result.getId()).isNull();
    }

    // SkillDTO toDTO
    @Test
    void shouldMapEntityToSkillDTO() {
        // Arrange
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Docker Basics");
        skill.setStatus(SkillStatus.BACKLOG);
        skill.setDescription("Improving Docker knowledge");
        skill.setSource("https://www.docker.com");
        skill.setDateAdded(LocalDate.now());
        skill.setTag("DevOps");

        // Act
        SkillDTO result = skillMapper.toDTO(skill);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Docker Basics");
        assertThat(result.status()).isEqualTo(SkillStatus.BACKLOG);
        assertThat(result.description()).isEqualTo("Improving Docker knowledge");
        assertThat(result.source()).isEqualTo("https://www.docker.com");
        assertThat(result.dateAdded()).isEqualTo(LocalDate.now());
        assertThat(result.tag()).isEqualTo("DevOps");

    }

    // UpdateSkillDTO
    @Test
    void shouldUpdateExistingEntityFromUpdateSkillDTO() {
        // Arrange
        Skill existingSkill = new Skill();
        existingSkill.setId(5L);
        existingSkill.setTitle("Old Title");
        existingSkill.setStatus(SkillStatus.BACKLOG);
        existingSkill.setDescription("Old description ");
        existingSkill.setSource("Old Source");
        existingSkill.setDateAdded(LocalDate.of(2023, 1, 1));
        existingSkill.setTag("Old Tag");

        // DTO med ny data
        UpdateSkillDTO updatedDto = new UpdateSkillDTO(
                5L,
                "New Awesome Title",
                SkillStatus.MASTERED,
                "Updated description",
                "Source",
                LocalDate.now(),
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
        assertThat(existingSkill.getDateAdded()).isEqualTo(LocalDate.now());
        assertThat(existingSkill.getTag()).isEqualTo("Java");

    }

}
