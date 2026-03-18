package org.example.devroadmapskilltracker.skill.exception;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;



@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException exception, Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        model.addAttribute("errorTitle", "Skill Not Found");
        return "skills/error";
    }


    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException exception, Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        model.addAttribute("errorTitle", "Invalid Input");
        return "skills/error";

    }


    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleAllUncaughtErrors(Exception exception, Model model) {
        logger.error("Unexpected Error", exception);

        model.addAttribute("errorMessage", "Something went wrong. Try again later.");
        model.addAttribute("errorTitle", "Unexpected Error");
        return "skills/error";

    }


}
