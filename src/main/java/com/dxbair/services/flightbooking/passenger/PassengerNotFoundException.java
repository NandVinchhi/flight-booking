package com.dxbair.services.flightbooking.passenger;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PassengerNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PassengerNotFoundException(String passengerId) {
		super("Could not find passenger with id: " + passengerId);
	}
}
