package com.example.demo.controller;

import com.example.demo.model.Scheduler;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.SchedulerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
public class ScheduleController {

    @Autowired
    private SchedulerRepository schedulerRepository;

    @Autowired
    private ExamRepository examRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/view-schedule")
    public String viewSchedule(Model model) {
        model.addAttribute("schedules", schedulerRepository.findAll());
        return "view-schedule";
    }

    @GetMapping("/new-schedule")
    public String newScheduleForm(Model model) {
        model.addAttribute("scheduler", new Scheduler());
        model.addAttribute("exams", examRepository.findAll());
        return "new-schedule";
    }

    @PostMapping("/new-schedule")
    public String saveSchedule(@Valid @ModelAttribute("scheduler") Scheduler scheduler, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("exams", examRepository.findAll());
            return "new-schedule";
        }
        schedulerRepository.save(scheduler);
        return "redirect:/view-schedule";
    }
}
