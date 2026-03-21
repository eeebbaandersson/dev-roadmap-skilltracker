package org.example.devroadmapskilltracker.skill;

import org.example.devroadmapskilltracker.skill.dto.CreateSkillDTO;
import org.example.devroadmapskilltracker.skill.dto.SkillDTO;
import org.example.devroadmapskilltracker.skill.dto.UpdateSkillDTO;
import org.example.devroadmapskilltracker.skill.exception.ResourceNotFoundException;
import org.example.devroadmapskilltracker.skill.service.SkillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional // Gör en auto-rollback av ändringar i databasen efter varje test
class SkillServiceIntegrationTest {

    @Autowired
    private SkillService skillService;
    @Autowired
    private SkillRepository skillRepository;

    // Rensar all data från DataInitializer innan testen börjar --> Löser tidigare problem med för många träffar i Title/tag-search
    @BeforeEach
    void setUp() {
        skillRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveSkill() {
        // Arrange
        CreateSkillDTO dto = new CreateSkillDTO(
                "Integration Test Skill",
                SkillStatus.BACKLOG,
                "Testing DB connection",
                "https://docs.spring.io",
                "Test"
        );

        // Act
        SkillDTO saved = skillService.createSkill(dto);

        // Assert
        assertThat(saved).isNotNull();
        assertThat(saved.title()).isEqualTo("Integration Test Skill");

        assertThat(saved.dateAdded()).isNotNull();
        assertThat(saved.dateAdded()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void shouldThrowException_WhenTitleAlreadyExists() {
        // Arrange
        CreateSkillDTO dto = new CreateSkillDTO(
                "Duplicate Title",
                SkillStatus.BACKLOG,
                "...",
                "https://test.io",
                "Test"
        );
        skillService.createSkill(dto);

        // Act & Assert
        assertThatThrownBy(() -> skillService.createSkill(dto))
                .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("A skill with title: " +dto.title() + " already exists");
    }

    @Test
    void shouldReturnFilteredSkills_WhenSearchingByTitleAndTag() {
        // Arrange - Skapa flera skills
        skillService.createSkill(new CreateSkillDTO("Java Spring", SkillStatus.BACKLOG, "...", "https://test.io", "Backend"));
        skillService.createSkill(new CreateSkillDTO("React Frontend", SkillStatus.BACKLOG, "...", "https://test.io", "Frontend"));
        skillService.createSkill(new CreateSkillDTO("Java Hibernate", SkillStatus.BACKLOG, "...", "https://test.io", "Database"));

        // Act 1: Sök på bara titel (Java)
        Page<SkillDTO> titleSearch = skillService.getSkills("Java", null, Pageable.unpaged());

        // Act 2: Sök på bara tag (Frontend)
        Page<SkillDTO> tagSearch = skillService.getSkills(null, "Frontend", Pageable.unpaged());

        // Act 3: Sök på något som inte finns (Python)
        Page<SkillDTO> titleSearch2 = skillService.getSkills("Python", null, Pageable.unpaged());

        // Assert
        assertThat(titleSearch.getContent())
                .hasSize(2)
                .extracting(SkillDTO::title)
                        .containsExactlyInAnyOrder("Java Spring", "Java Hibernate");

        assertThat(tagSearch.getContent())
                .hasSize(1)
                .extracting(SkillDTO::tag)
                .containsExactlyInAnyOrder("Frontend");

        assertThat(titleSearch2.getContent()).isEmpty();

    }


    @Test
    void shouldUpdateExistingSkill() {
        // Arrange -> Skapa/spara en initial skill
        CreateSkillDTO createDto = new CreateSkillDTO(
                "Original Title",
                SkillStatus.BACKLOG,
                "Original description",
                "https://test.io",
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
                "Updated Tag"
        );

        // Anropa servicen för att uppdatera
        SkillDTO updatedResult = skillService.updateSkill(id, updateDto);

        // Assert
        assertThat(updatedResult.id()).isEqualTo(id);
        assertThat(updatedResult.title()).isEqualTo("Updated Title");
        assertThat(updatedResult.status()).isEqualTo(SkillStatus.IN_PROGRESS);
        assertThat(updatedResult.description()).isEqualTo("Updated description");

        // Hämta från databasen igen för att kontrollera att det sparades korrekt
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
                "Tag"
        );

        // Act & Assert
        assertThatThrownBy(() -> skillService.updateSkill(nonExistingId, updateDto))
        .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Error: Could not find skill with id: " + nonExistingId);

    }

    // Todo: Addera testfall för deleteSkill - Happy Path?
    // shouldDeleteChosenSkill


    @Test
    void shouldThrowException_WhenDeletingNonExistingSkill() {
        // Arrange
        CreateSkillDTO dto = new CreateSkillDTO(
                "Java Docs",
                SkillStatus.BACKLOG,
                "Improving ability to create Java Docs",
                "https://docs.oracle.com",
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

    @Test
    void shouldSetCompletedAt_WhenSkillStatusChangesToMastered() {
        // Arrange -> Skapa/spara en initial skill
        CreateSkillDTO skill = new CreateSkillDTO(
                "Test",
                SkillStatus.IN_PROGRESS,
                "Original description",
                "https://test.io",
                "Tag"
        );

        SkillDTO savedSkill = skillService.createSkill(skill);
        Long id = savedSkill.id();

        // Act
        UpdateSkillDTO updatedSkill = new UpdateSkillDTO(
                id,
                "Test",
                SkillStatus.MASTERED,
                "Original description",
                "https://test.io",
                "Tag"
        );

        skillService.updateSkill(id, updatedSkill);

        // Assert
        SkillDTO result = skillService.getSkillById(id); // Hämtar ID:et på nytt från databasen

        assertThat(result.status()).isEqualTo(SkillStatus.MASTERED);
        assertThat(result.completedAt()).isNotNull();
        assertThat(result.completedAt()).isCloseTo(LocalDateTime.now(), within(2, ChronoUnit.SECONDS));
    }

    // Verifiera updatedAT (SPRING BOOTSs JPA Auditing)
    @Test
    void shouldUpdateTimestamp_WhenSkillIsModified() throws InterruptedException {
        // Arrange -> Skapa/spara en initial skill
        CreateSkillDTO skill = new CreateSkillDTO(
                "Maven",
                SkillStatus.IN_PROGRESS,
                "Original description",
                "https://test.io",
                "Tag"
        );

        SkillDTO savedSkill = skillService.createSkill(skill);
        Long id = savedSkill.id();
        LocalDateTime timeBeforeUpdate = savedSkill.updatedAt();

        // Simulerar en kort fördröjning
        Thread.sleep(100);

        // Act
        UpdateSkillDTO updatedSkill = new UpdateSkillDTO(
                id,
                "Maven",
                SkillStatus.IN_PROGRESS,
                "Updated description to trigger auditing",
                "https://test.io",
                "Tag"
        );

        skillService.updateSkill(id, updatedSkill);

        // Assert
        SkillDTO result = skillService.getSkillById(id);

        // Kontroll av att updatedAt är efter den första undansparade tiden, använder AssertJs inbyggda marginal isCloseTo
        assertThat(result.updatedAt()).isCloseTo(LocalDateTime.now(), within(2, ChronoUnit.SECONDS));
        assertThat(result.updatedAt()).isAfterOrEqualTo(timeBeforeUpdate);
        // Kontroll att beskrivningen uppdaterades
        assertThat(result.description()).isEqualTo("Updated description to trigger auditing");
    }

}
