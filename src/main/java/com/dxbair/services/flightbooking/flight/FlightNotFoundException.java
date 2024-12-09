package com.dxbair.services.flightbooking.flight;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FlightNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FlightNotFoundException(String flightId) {
		super("Could not find flight with id: " + flightId);
	}
}
