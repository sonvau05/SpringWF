package com.example.flightmanagement.controller;

import com.example.flightmanagement.entity.Flight;
import com.example.flightmanagement.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public String listFlights(Model model) {
        model.addAttribute("flights", flightService.getAllFlights());
        return "flights/list";
    }

    @GetMapping("/new")
    public String newFlight(Model model) {
        model.addAttribute("flight", new Flight());
        return "flights/new";
    }

    @PostMapping
    public String saveFlight(@Valid @ModelAttribute Flight flight, BindingResult result) {
        if (result.hasErrors()) {
            return "flights/new";
        }

        flightService.saveFlight(flight);
        return "redirect:/flights";
    }

    @GetMapping("/edit/{id}")
    public String editFlight(@PathVariable Integer id, Model model) {
        model.addAttribute("flight", flightService.getFlightById(id));
        return "flights/edit";
    }

    @PostMapping("/update/{id}")
    public String updateFlight(@PathVariable Integer id,
                               @Valid @ModelAttribute Flight flight,
                               BindingResult result) {

        if (result.hasErrors()) {
            return "flights/edit";
        }

        flight.setId(id);
        flightService.saveFlight(flight);

        return "redirect:/flights";
    }

    @PostMapping("/delete/{id}")
    public String deleteFlight(@PathVariable Integer id) {
        flightService.deleteFlight(id);
        return "redirect:/flights";
    }
}
