package com.dxbair.services.flightbooking.passenger;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dxbair.services.flightbooking.domain.entity.Passenger;

@RestController
@RequestMapping("passengers")
public class PassengerController {

	@Autowired
	private PassengerService passengerService;

	private static final Logger logger = LoggerFactory.getLogger(PassengerController.class);

	@GetMapping
	public @ResponseBody List<Passenger> getAllPassengers() {
		return passengerService.getAllPassengers();
	}

	@GetMapping("/{passenger-id}")
	public @ResponseBody Passenger getPassengerById(@PathVariable("passenger-id") String passengerId) {
		return passengerService.getPassengerById(passengerId);
	}

	@PostMapping
	public ResponseEntity<Passenger> createPassenger(@RequestBody Passenger passenger) {
		Passenger createdPassenger = passengerService.createPassenger(passenger);
		return new ResponseEntity<>(createdPassenger, HttpStatus.CREATED);
	}

	@PutMapping("/{passenger-id}")
	public ResponseEntity<Passenger> updatePassenger(@PathVariable("passenger-id") String passengerId, @RequestBody Passenger passenger) {
		Passenger updatedPassenger = passengerService.updatePassenger(passengerId, passenger);
		return ResponseEntity.ok(updatedPassenger);
	}

	@DeleteMapping("/{passenger-id}")
	public ResponseEntity<Void> deletePassenger(@PathVariable("passenger-id") String passengerId) {
		passengerService.deletePassenger(passengerId);
		return ResponseEntity.noContent().build();
	}
}
