package org.example.devroadmapskilltracker.skill.controller;

import jakarta.validation.Valid;
import org.example.devroadmapskilltracker.skill.service.SkillService;
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
            @PageableDefault(size =  3, sort = "title" ) Pageable pageable,
            Model model) {

        Page<SkillDTO> skillPage = skillService.getSkills(title, title, pageable);

        model.addAttribute("skills", skillPage.getContent());

        // Information till "Load-more"-knapp
        model.addAttribute("currentSize", pageable.getPageSize());
        model.addAttribute("hasNext", skillPage.hasNext());

        // Skicka med sökparametrarna tillbaka så att sökfält inte töms
        model.addAttribute("titleFilter", title != null ? title : "");

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
            return "skills/create";
        }

        try {
            skillService.createSkill(dto);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("title", "error.title", e.getMessage());
            return "skills/create";
        }

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
            skillService.updateSkill(id, dto);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("title", "error.title", e.getMessage());
            return "skills/update";
        }

        return "redirect:/skills";
    }

    @DeleteMapping("/{id}")
    public String deleteSkill(@PathVariable Long id) {

        skillService.deleteSkill(id);
        return "redirect:/skills";
    }


}
