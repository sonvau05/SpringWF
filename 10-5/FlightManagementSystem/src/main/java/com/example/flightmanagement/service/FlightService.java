package com.example.flightmanagement.service;

import com.example.flightmanagement.entity.Flight;
import com.example.flightmanagement.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Flight getFlightById(Integer id) {
        return flightRepository.findById(id).orElseThrow();
    }

    public void saveFlight(Flight flight) {
        flightRepository.save(flight);
    }

    public void deleteFlight(Integer id) {
        flightRepository.deleteById(id);
    }
}
