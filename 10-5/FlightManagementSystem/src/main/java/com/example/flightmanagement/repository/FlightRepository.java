package com.example.flightmanagement.repository;

import com.example.flightmanagement.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Integer> {
}
