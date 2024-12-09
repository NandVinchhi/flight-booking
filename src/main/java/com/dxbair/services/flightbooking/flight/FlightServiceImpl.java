package com.dxbair.services.flightbooking.flight;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dxbair.services.flightbooking.domain.entity.Flight;
import com.dxbair.services.flightbooking.domain.repo.FlightRepository;

@Service
@Transactional(readOnly = true)
public class FlightServiceImpl implements FlightService {

	@Autowired
	private FlightRepository flightRepo;

	@Override
	public Flight getFlightById(String flightId) {
		return flightRepo.findById(flightId).orElseThrow(() -> new FlightNotFoundException(flightId));
	}

	@Override
	public List<Flight> getAllFlights() {
		return flightRepo.findAll();
	}

	@Override
	@Transactional
	public Flight createFlight(Flight flight) {
		flight.setId(null); // Ensure we create a new flight
		return flightRepo.save(flight);
	}

	@Override
	@Transactional
	public Flight updateFlight(String flightId, Flight flight) {
		Flight existingFlight = getFlightById(flightId);
		
		existingFlight.setDeparture(flight.getDeparture());
		existingFlight.setArrival(flight.getArrival());
		existingFlight.setDepartureDate(flight.getDepartureDate());
		existingFlight.setArrivalDate(flight.getArrivalDate());
		
		return flightRepo.save(existingFlight);
	}

	@Override
	@Transactional
	public void deleteFlight(String flightId) {
		Flight flight = getFlightById(flightId);
		flightRepo.delete(flight);
	}
}
