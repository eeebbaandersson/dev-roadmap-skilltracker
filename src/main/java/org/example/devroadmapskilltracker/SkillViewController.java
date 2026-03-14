package org.example.devroadmapskilltracker;

import jakarta.validation.Valid;
import org.example.devroadmapskilltracker.skill.SkillService;
import org.example.devroadmapskilltracker.skill.SkillStatus;
import org.example.devroadmapskilltracker.skill.dto.CreateSkillDTO;
import org.example.devroadmapskilltracker.skill.dto.SkillDTO;
import org.example.devroadmapskilltracker.skill.dto.UpdateSkillDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/skills")
public class SkillViewController {

    private final SkillService skillService;

    public SkillViewController(SkillService skillService) {
        this.skillService = skillService;
    }


    @GetMapping
    public String getSkills(
            @RequestParam(required = false) String title,
            @PageableDefault(size =  12) Pageable pageable,
            Model model) {

        // Hämtar datan från service
        Page<SkillDTO> skillPage = skillService.getSkills(title, title, pageable);

        // Fyller på Modellen med allt som Thymeleaf behöver
        model.addAttribute("skills", skillPage.getContent());
        model.addAttribute("currentPage", skillPage.getNumber());
        model.addAttribute("totalPages", skillPage.getTotalPages());
        model.addAttribute("totalItems", skillPage.getTotalElements());

        // Skicka med sökparametrarna tillbaka så att sökfält inte töms
        model.addAttribute("titleFilter", title);

        // Returnera namnet på HTML-filen (vyn)
        return "skills/home";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        // Skapar ett tomt skal för formuläret
        CreateSkillDTO emptyDto = new CreateSkillDTO(
                "",
                SkillStatus.BACKLOG,
                "",
                "https://",
                LocalDateTime.now(),
                ""
        );
        model.addAttribute("skill", emptyDto);
        return "skills/create";

    }

    @PostMapping
    public String createSkill(@Valid @ModelAttribute("skill") CreateSkillDTO dto,
                              BindingResult bindingResult, Model model) {

        // Kontroll av valideringsfel
        if (bindingResult.hasErrors()) {
            // Finns det fel, skicka tillbaka användarn till formuläret
            return "skills/create";
        }

        // Anropa service för att skapa ny skill
        try {
            skillService.createSkill(dto);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("title", "error.title", e.getMessage());
            return "skills/create";
        }

        // Går allt igenom skicka tillbaka användaren till home-sidan
        return "redirect:/skills";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        // Hämta befintlig data för att fylla i formuläret
        SkillDTO skill = skillService.getSkillById(id);
        model.addAttribute("skill", skill);
        return "skills/update";

    }

    @PostMapping("/{id}")
    public String updateSkill(@PathVariable Long id,
                              @Valid @ModelAttribute("skill")UpdateSkillDTO dto,
                              BindingResult bindingResult, Model model){

        // Vid misslyckad validering
        if (bindingResult.hasErrors()) {
            return "skills/update";
        }

        try {
            // Anropa service för att uppdatera
            skillService.updateSkill(id, dto);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("title", "error.title", e.getMessage());
            return "skills/update";
        }

        // Går allt igenom skicka tillbaka användaren till home-sidan
        return "redirect:/skills";
    }

    @DeleteMapping("/{id}")
    public String deleteSkill(@PathVariable Long id) {
        // Anropar service för att ta bort vald skill
        skillService.deleteSkill(id);

        // Går allt igenom skicka tillbaka användaren till home-sidan
        return "redirect:/skills";
    }


}
