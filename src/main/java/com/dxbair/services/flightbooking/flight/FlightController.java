package com.dxbair.services.flightbooking.flight;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dxbair.services.flightbooking.domain.entity.Flight;

@RestController
@RequestMapping("flights")
public class FlightController {

	@Autowired
	private FlightService flightService;

	@GetMapping
	public @ResponseBody List<Flight> getAllFlights() {
		return flightService.getAllFlights();
	}

	@GetMapping("/{flight-id}")
	public @ResponseBody Flight getFlightById(@PathVariable("flight-id") String flightId) {
		return flightService.getFlightById(flightId);
	}
}
