package com.dxbair.services.flightbooking.passenger;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dxbair.services.flightbooking.domain.entity.Passenger;

@RestController
@RequestMapping("passengers")
public class PassengerController {

	@Autowired
	private PassengerService passengerService;

	@GetMapping
	public @ResponseBody List<Passenger> getAllPassengers() {
		return passengerService.getAllPassengers();
	}

	@GetMapping("/{passenger-id}")
	public @ResponseBody Passenger getPassengerById(@PathVariable("passenger-id") String passengerId) {
		return passengerService.getPassengerById(passengerId);
	}

	@PostMapping
	public @ResponseBody Passenger savePassenger(@RequestBody Passenger passenger) {
		return passengerService.savePassenger(passenger);
	}

	@PutMapping("/{passenger-id}")
	public @ResponseBody Passenger updatePassenger(@PathVariable("passenger-id") String passengerId,
			@RequestBody Passenger passenger) {
		passenger.setId(passengerId);
		return passengerService.savePassenger(passenger);
	}

	@DeleteMapping("/{passenger-id}")
	public @ResponseBody void deletePassenger(@PathVariable("passenger-id") String passengerId) {
		passengerService.deletePassenger(passengerId);
	}
}
