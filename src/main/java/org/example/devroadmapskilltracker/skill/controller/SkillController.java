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

   // Generell sökning efter skills
    @GetMapping
    public Page<SkillDTO> getSkills(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String tag,
            Pageable pageable) {
       return skillService.getSkills(title, tag, pageable);
    }


    // Sökning via specifik skill ID
    @GetMapping("/{id}")
    public SkillDTO getSkillById(@PathVariable Long id) {
        return skillService.getSkillById(id);
    }

   // Skapa/lägg till en ny skill
    @PostMapping
    public ResponseEntity<SkillDTO> createSkill(@RequestBody CreateSkillDTO dto) {
       // Anropar service för att spara
       SkillDTO createdSkill = skillService.createSkill(dto);

       // Returnerar svaret med status 201
       return ResponseEntity.status(HttpStatus.CREATED).body(createdSkill);
    }

    // Uppdatera en befintlig skill
    @PutMapping("/{id}")
    public ResponseEntity<SkillDTO> updateSkillById(@PathVariable Long id, @RequestBody UpdateSkillDTO dto) {

       // Anropar service för att uppdatera
       SkillDTO updatedSkill = skillService.updateSkill(id, dto);

        // Returnerar svaret med status 200
       return ResponseEntity.ok(updatedSkill);
    }

    // Ta bort vald skill
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkillById(@PathVariable Long id) {
       // Anropar service för att ta bort vald skill
       skillService.deleteSkill(id);

       // Returnerar status 204
       return ResponseEntity.noContent().build();

    }




}
