package org.example.devroadmapskilltracker;

import org.example.devroadmapskilltracker.skill.SkillService;
import org.example.devroadmapskilltracker.skill.dto.SkillDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
