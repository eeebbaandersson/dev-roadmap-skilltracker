package org.example.devroadmapskilltracker.skill.controller;

import org.example.devroadmapskilltracker.skill.service.SkillService;
import org.example.devroadmapskilltracker.skill.dto.CreateSkillDTO;
import org.example.devroadmapskilltracker.skill.dto.SkillDTO;
import org.example.devroadmapskilltracker.skill.dto.UpdateSkillDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

   private final SkillService skillService;

   public  SkillController(SkillService skillService) {
       this.skillService = skillService;

   }


    @GetMapping
    public Page<SkillDTO> getSkills(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String tag,
            Pageable pageable) {
       return skillService.getSkills(title, tag, pageable);
    }

    @GetMapping("/{id}")
    public SkillDTO getSkillById(@PathVariable Long id) {
        return skillService.getSkillById(id);
    }

    @PostMapping
    public ResponseEntity<SkillDTO> createSkill(@RequestBody CreateSkillDTO dto) {

       SkillDTO createdSkill = skillService.createSkill(dto);

       return ResponseEntity.status(HttpStatus.CREATED).body(createdSkill);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillDTO> updateSkillById(@PathVariable Long id, @RequestBody UpdateSkillDTO dto) {

       SkillDTO updatedSkill = skillService.updateSkill(id, dto);

       return ResponseEntity.ok(updatedSkill);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkillById(@PathVariable Long id) {

       skillService.deleteSkill(id);

       return ResponseEntity.noContent().build();

    }
}
