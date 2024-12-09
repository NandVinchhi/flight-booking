package com.dxbair.services.flightbooking.booking;

import java.util.List;

import com.dxbair.services.flightbooking.domain.entity.FlightBooking;
import com.dxbair.services.flightbooking.booking.model.BookingRequest;

public interface BookingService {
	
	FlightBooking getBooking(String bookingId);
	
	List<FlightBooking> getAllBookingsByPassenger(String passengerId);

	List<FlightBooking> getAllMultiFlightBookingsByPassenger(String passengerId);

	List<FlightBooking> getAllMultiFlightBookings();
	
	void createSampleBookings();

	FlightBooking createBooking(BookingRequest bookingRequest);

	FlightBooking updateBooking(String bookingId, BookingRequest bookingRequest);

	void deleteBooking(String bookingId);

}
