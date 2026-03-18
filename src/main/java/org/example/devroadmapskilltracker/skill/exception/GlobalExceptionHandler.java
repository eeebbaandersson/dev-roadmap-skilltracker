package org.example.devroadmapskilltracker.skill.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {


    // Handle 404
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException exception, Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        model.addAttribute("errorTitle", "Skill Not Found");
        return "skills/error";
    }

    // Handle 400
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException exception, Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        model.addAttribute("errorTitle", "Invalid Input");
        return "skills/error";

    }










}
