package org.example.devroadmapskilltracker.skill;

import org.example.devroadmapskilltracker.skill.controller.SkillViewController;
import org.example.devroadmapskilltracker.skill.dto.CreateSkillDTO;
import org.example.devroadmapskilltracker.skill.dto.SkillDTO;
import org.example.devroadmapskilltracker.skill.dto.UpdateSkillDTO;
import org.example.devroadmapskilltracker.skill.exception.ResourceNotFoundException;
import org.example.devroadmapskilltracker.skill.service.SkillService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SkillViewController.class)
class SkillViewControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private SkillService skillService;

    @MockitoBean
    private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMappingContext;

    @Test
    void goToHomePage_shouldRedirectToSkills() throws  Exception{
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/skills"));
    }

    @Test
    void getSkills_shouldReturnHomepage() throws Exception {
        Page<SkillDTO> emptyPage = new PageImpl<>(List.of());

        Mockito.when(skillService.getSkills(any(), any(), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/skills")
                        .param("title", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("skills/home"))
                .andExpect(model().attributeExists( "currentSize", "hasNext", "titleFilter"));
    }

    @Test
    void getSkills_WithContent_shouldViewCurrentSkills() throws Exception {
      SkillDTO mockSkill = new SkillDTO(
              1L,
              "Java",
              SkillStatus.IN_PROGRESS,
              "Learning Java fundamentals",
              "",
              null,
              null,
              null,
              "Backend");

        Page<SkillDTO> skillPage = new PageImpl<>(List.of(mockSkill));

        Mockito.when(skillService.getSkills(any(), any(), any(Pageable.class)))
                .thenReturn(skillPage);

        mockMvc.perform(get("/skills"))
                .andExpect(status().isOk())
                .andExpect(view().name("skills/home"))
                .andExpect(model().attributeExists("inProgressSkills"))
                .andExpect(model().attribute("inProgressSkills", hasItem(mockSkill
                )))
                .andExpect(model().attribute("backlogSkills", hasSize(0)))
                .andExpect(model().attribute("masteredSkills", hasSize(0)))
                .andExpect(model().attribute("currentSize",3))
                .andExpect(model().attribute("hasNext",false));


    }


    @Test
    void loadMoreSkills_shouldReturnFragmentWithSkills() throws Exception {
        SkillDTO mockSkill = new SkillDTO(2L, "React",
                SkillStatus.BACKLOG, "Desc", "",
                null, null, null, "Frontend");

        Page<SkillDTO> skillPage = new PageImpl<>(List.of(mockSkill));

        Mockito.when(skillService.getSkills(any(), any(), any(Pageable.class)))
                .thenReturn(skillPage);

        mockMvc.perform(get("/skills/load-more")
                .param("page", "1")
                .param("title", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("skills/home :: skill-loader"))
                .andExpect(model().attributeExists("skills"))
                .andExpect(model().attribute("skills", hasSize(1)))
                .andExpect(model().attribute("skills", hasItem(mockSkill)));
    }

    @Test
    void showCreateForm_shouldReturnCreateViewWithEmptySkillData() throws Exception {
        mockMvc.perform(get("/skills/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("skills/create"))
                .andExpect(model().attributeExists("skill"))
                .andExpect(model().attribute("skill", instanceOf(CreateSkillDTO.class)));
    }


    @Test
    void createSkill_withValidData_shouldRedirectToHomepage() throws Exception {
        mockMvc.perform(post("/skills")
                .param("title", "React")
                .param("status", "BACKLOG")
                .param("description", "Learn frontend library")
                .param("source","https://react.dev")
                .param("tag", "Frontend"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/skills"));

        Mockito.verify(skillService, Mockito.times(1)).createSkill(any());
    }


    @Test
    void createSkill_withInvalidData_shouldReturnCreateView() throws Exception {
        mockMvc.perform(post("/skills")
                        .param("title", " ")
                        .param("status", "BACKLOG")
                        .param("description", "Learn frontend libary")
                        .param("source", "https://react.dev")
                        .param("tag", "Frontend"))
                .andExpect(status().isOk())
                .andExpect(view().name("skills/create"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("skill", "title"));

        Mockito.verify(skillService, Mockito.never()).createSkill(any());
    }

    @Test
    void createSkill_whenServiceThrowsIllegalArgument_shouldReturnCreateView() throws Exception {
        String duplicateTitle = "Existing Title";
        String expectedErrorMessage = "A skill with title: " + duplicateTitle + " already exists.";

        CreateSkillDTO dto = new CreateSkillDTO(
                "Existing Title",
                SkillStatus.IN_PROGRESS,
                "Learning Java fundamentals",
                "",
                "Backend");

        Mockito.when(skillService.createSkill(eq(dto)))
                .thenThrow(new IllegalArgumentException(expectedErrorMessage));

        mockMvc.perform(post("/skills")
                        .param("title", dto.title())
                        .param("status", dto.status().name())
                        .flashAttr("skill", dto))
                .andExpect(status().isOk())
                .andExpect(view().name("skills/create"))
                .andExpect(model().hasErrors())
                .andExpect(content().string(containsString(expectedErrorMessage)));
    }


    @Test
    void showUpdateForm_shouldReturnUpdateViewWithExistingSkillData() throws Exception {
        Long skillId = 1L;
        SkillDTO mockSkill = new SkillDTO(
                skillId,
                "Docker",
                SkillStatus.IN_PROGRESS,
                "Learn containerization and how to manage images.",
                "",
                null,
                null,
                null,
                "DevOps");

        Mockito.when(skillService.getSkillById(skillId)).thenReturn(mockSkill);

        mockMvc.perform(get("/skills/update/{id}",  skillId))
                .andExpect(status().isOk())
                .andExpect(view().name("skills/update"))
                .andExpect(model().attributeExists("skill"))
                .andExpect(model().attribute("skill", mockSkill));
    }

    @Test
    void updateSkill_withValidData_shouldRedirectToHomepage() throws Exception {
        Long skillId = 2L;
        mockMvc.perform(post("/skills/{id}",  skillId)
                        .param("title", "REST APIs")
                        .param("status", "IN_PROGRESS")
                        .param("description", "Designing and implementing scalable endpoints.")
                        .param("source","https://test.dev")
                        .param("tag", "Backend"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/skills"));

        Mockito.verify(skillService, Mockito.times(1)).updateSkill(eq(skillId), any());

    }


    @Test
    void updateSkill_withInvalidData_shouldReturnUpdateView() throws Exception {
        Long skillId = 5L;
        mockMvc.perform(post("/skills/{id}",  skillId)
                .param("title", " ")
                .param("status", "BACKLOG")
                .param("description", "Learn frontend library")
                .param("source", "https://react.dev")
                .param("tag", "Frontend"))
                .andExpect(status().isOk())
                .andExpect(view().name("skills/update"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("skill", "title"));

        Mockito.verify(skillService, Mockito.never()).updateSkill(any(), any());

    }

    @Test
    void updateSkill_shouldReturnErrorView_WhenSkillNotFoundById() throws Exception {
        Long nonExistentSkillId = 999L;
        String expectedMessage = "Error: Could not find skill with id " + nonExistentSkillId;

        Mockito.when(skillService.getSkillById(nonExistentSkillId))
                .thenThrow(new ResourceNotFoundException(expectedMessage));

        mockMvc.perform(get("/skills/update/{id}",  nonExistentSkillId))
                .andExpect(status().isNotFound())
                .andExpect(view().name("skills/error"))
                .andExpect(model().attribute("errorTitle", "Skill Not Found"))
                .andExpect(model().attribute("errorMessage", expectedMessage));
    }

    @Test
    void updateSkill_whenServiceThrowsIllegalArgument_shouldReturnUpdateView() throws Exception {
        Long skillId = 1L;
        String duplicateTitle = "Existing Title";
        String expectedErrorMessage = "A skill with title: " + duplicateTitle + " already exists.";

        UpdateSkillDTO dto = new UpdateSkillDTO(
                skillId,
                duplicateTitle,
                SkillStatus.IN_PROGRESS,
                "Description",
                "https://test.com",
                "Java"
        );

        Mockito.when(skillService.updateSkill(eq(skillId), any()))
                .thenThrow(new IllegalArgumentException(expectedErrorMessage));

        mockMvc.perform(post("/skills/{id}", skillId)
                        .param("title", dto.title())
                        .param("status", dto.status().name())
                        .flashAttr("skill", dto))
                .andExpect(status().isOk())
                .andExpect(view().name("skills/update"))
                .andExpect(model().hasErrors())
                .andExpect(content().string(containsString(expectedErrorMessage)));

    }


    @Test
    void deleteSkill_shouldRedirectToHomepage() throws Exception {
        Long skillId = 3L;
        mockMvc.perform(delete("/skills/{id}",  skillId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/skills"));

        Mockito.verify(skillService, Mockito.times(1)).deleteSkill((skillId));
    }


    @Test
    void deleteSkill_shouldReturnErrorView_WhenSkillNotFoundById() throws Exception {
        Long nonExistentSkillId = 500L;
        String expectedMessage = "Error: Could not find skill with id " + nonExistentSkillId;

        Mockito.doThrow(new ResourceNotFoundException(expectedMessage))
                .when(skillService).deleteSkill(nonExistentSkillId);

        mockMvc.perform(delete("/skills/{id}",  nonExistentSkillId))
                .andExpect(status().isNotFound())
                .andExpect(view().name("skills/error"))
                .andExpect(model().attribute("errorTitle", "Skill Not Found"))
                .andExpect(model().attribute("errorMessage", expectedMessage));
    }

    @Test
    void anyMethod_shouldReturnErrorView_WhenRunTimeExceptionOccurs() throws Exception {
        Mockito.when(skillService.getSkills(any(), any(), any()))
                .thenThrow(new RuntimeException("Database connection lost"));

        mockMvc.perform(get("/skills"))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("skills/error"))
                .andExpect(model().attribute("errorTitle", "Unexpected Error"))
                .andExpect(model().attribute("errorMessage", "Something went wrong. Try again later."));
    }

    @Test
    void anyMethod_shouldReturnErrorView_WhenIllegalArgumentExceptionOccurs() throws Exception {
        Mockito.when(skillService.getSkills(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid data provided"));

        mockMvc.perform(get("/skills"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("skills/error"))
                .andExpect(model().attribute("errorTitle", "Invalid Input"));
    }
}
