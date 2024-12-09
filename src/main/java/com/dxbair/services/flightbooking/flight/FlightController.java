package com.dxbair.services.flightbooking.flight;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dxbair.services.flightbooking.domain.entity.Flight;

@RestController
@RequestMapping("flights")
public class FlightController {
	
	@Autowired
	private FlightService flightService;
	
	private static final Logger logger = LoggerFactory.getLogger(FlightController.class);
	
	@GetMapping
	public @ResponseBody List<Flight> getAllFlights() {
		return flightService.getAllFlights();
	}
	
	@GetMapping("/{flight-id}")
	public @ResponseBody Flight getFlightById(@PathVariable("flight-id") String flightId) {
		return flightService.getFlightById(flightId);
	}

	@PostMapping
	public ResponseEntity<Flight> createFlight(@RequestBody Flight flight) {
		Flight createdFlight = flightService.createFlight(flight);
		return new ResponseEntity<>(createdFlight, HttpStatus.CREATED);
	}

	@PutMapping("/{flight-id}")
	public ResponseEntity<Flight> updateFlight(@PathVariable("flight-id") String flightId, @RequestBody Flight flight) {
		Flight updatedFlight = flightService.updateFlight(flightId, flight);
		return ResponseEntity.ok(updatedFlight);
	}

	@DeleteMapping("/{flight-id}")
	public ResponseEntity<Void> deleteFlight(@PathVariable("flight-id") String flightId) {
		flightService.deleteFlight(flightId);
		return ResponseEntity.noContent().build();
	}
}
