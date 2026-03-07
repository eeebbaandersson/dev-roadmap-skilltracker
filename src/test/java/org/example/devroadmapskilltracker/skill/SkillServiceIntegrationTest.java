package org.example.devroadmapskilltracker.skill;

import org.example.devroadmapskilltracker.skill.dto.CreateSkillDTO;
import org.example.devroadmapskilltracker.skill.dto.SkillDTO;
import org.example.devroadmapskilltracker.skill.dto.UpdateSkillDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional // Gör en rollback av ändringar i databasen efter varje test
class SkillServiceIntegrationTest {

    @Autowired
    private SkillService skillService;

    @Test
    void shouldSaveAndRetrieveSkill() {
        // Arrange
        CreateSkillDTO dto = new CreateSkillDTO(
                "Integration Test Skill",
                SkillStatus.BACKLOG,
                "Testing DB connection",
                "https://docs.spring.io",
                LocalDate.now(),
                "Test"
        );

        // Act
        SkillDTO saved = skillService.createSkill(dto);

        // Assert
        assertThat(saved).isNotNull();
        assertThat(saved.title()).isEqualTo("Integration Test Skill");
    }

    @Test
    void shouldThrowException_WhenTitleAlreadyExists() {
        // Arrange
        CreateSkillDTO dto = new CreateSkillDTO(
                "Duplicate Title",
                SkillStatus.BACKLOG,
                "...",
                "https://test.io",
                LocalDate.now(),
                "Test"
        );
        skillService.createSkill(dto);

        // Act & Assert
        assertThatThrownBy(() -> skillService.createSkill(dto))
                .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("A skill with title: " +dto.title() + " already exists");
    }

    @Test
    void shouldThrowException_WhenDateIsInTheFuture() {
        // Arrange
        CreateSkillDTO futureDto = new CreateSkillDTO(
                "Future Skill",
                SkillStatus.BACKLOG,
                "...",
                "https://test.io",
                LocalDate.now().plusDays(1),
                "Tag"
        );

        // Act & Assert
        assertThatThrownBy(() -> skillService.createSkill(futureDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Date cannot be in the future.");

    }

    @Test
    void shouldUpdateExistingSkill() {
        // Arrange -> Skapa/spara en initial skill
        CreateSkillDTO createDto = new CreateSkillDTO(
                "Original Title",
                SkillStatus.BACKLOG,
                "Original description",
                "https://test.io",
                LocalDate.now(),
                "Original Tag"
        );

        SkillDTO savedInitial = skillService.createSkill(createDto);
        Long id = savedInitial.id();

        // Skapa en UpdateSkillDTO med ny data
        UpdateSkillDTO updateDto = new UpdateSkillDTO(
                id,
                "Updated Title",
                SkillStatus.IN_PROGRESS,
                "Updated description",
                "https://test.io",
                LocalDate.now(),
                "Updated Tag"
        );

        // Anropa servicen för att uppdatera
        SkillDTO updatedResult = skillService.updateSkill(id, updateDto);

        // Assert
        assertThat(updatedResult.id()).isEqualTo(id);
        assertThat(updatedResult.title()).isEqualTo("Updated Title");
        assertThat(updatedResult.status()).isEqualTo(SkillStatus.IN_PROGRESS);
        assertThat(updatedResult.description()).isEqualTo("Updated description");

        // Hämta från databsen igen för att kontrollerade att det sparades korrekt
        SkillDTO fetchedFromDB = skillService.getSkillById(id);
        assertThat(fetchedFromDB.title()).isEqualTo("Updated Title");
    }

    @Test
    void shouldThrowException_WhenUpdatingNonExistingSkill() {
        // Arrange
        Long nonExistingId = 99L;
        UpdateSkillDTO updateDto = new UpdateSkillDTO(
                nonExistingId, "Title",
                SkillStatus.BACKLOG,
                "Description",
                "https://test.io",
                LocalDate.now(),
                "Tag"
        );

        // Act & Assert
        assertThatThrownBy(() -> skillService.updateSkill(nonExistingId, updateDto))
        .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Error: Could not find skill with id: " + nonExistingId);

    }

    @Test
    void shouldDeleteChosenSkill() {
        // Arrange -
        CreateSkillDTO dto = new CreateSkillDTO(
                "Java Docs",
                SkillStatus.BACKLOG,
                "Improving ability to create Java Docs",
                "https://docs.oracle.com",
                LocalDate.now(),
                "Java"
        );

        SkillDTO saved = skillService.createSkill(dto);
        Long id = saved.id();

        // Act
       skillService.deleteSkill(id);

       // Assert
        assertThatThrownBy(() -> skillService.getSkillById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Error: Could not find skill with id: " + id);
    }


}
