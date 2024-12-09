package com.dxbair.services.flightbooking.booking;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.dxbair.services.flightbooking.domain.entity.Flight;
import com.dxbair.services.flightbooking.domain.entity.FlightBooking;
import com.dxbair.services.flightbooking.domain.entity.Passenger;
import com.dxbair.services.flightbooking.domain.repo.FlightBookingRepository;
import com.dxbair.services.flightbooking.domain.repo.FlightRepository;
import com.dxbair.services.flightbooking.domain.repo.PassengerRepository;
import com.dxbair.services.flightbooking.flight.FlightNotFoundException;
import com.dxbair.services.flightbooking.passenger.PassengerNotFoundException;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
	private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

	@Autowired
	private FlightBookingRepository bookingRepo;

	@Autowired
	private PassengerRepository passengerRepo;

	@Autowired
	private FlightRepository flightRepo;

	@Override
	public FlightBooking getBooking(String bookingId) {
		return bookingRepo.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
	}

	@Override
	public List<FlightBooking> getAllBookingsByPassenger(String passengerId) {
		List<FlightBooking> bookings = bookingRepo.findByPassengerId(passengerId);
		if (CollectionUtils.isEmpty(bookings))
			throw new BookingNotFoundForPassengerException(passengerId);
		return bookings;
	}

	@Override
	public List<FlightBooking> getAllMultiFlightBookingsByPassenger(String passengerId) {
		return getAllBookingsByPassenger(passengerId).stream()
				.filter(booking -> booking.getFlights().size() > 1)
				.collect(Collectors.toList());
	}

	@Override
	public List<FlightBooking> getAllMultiFlightBookings() {
		return bookingRepo.findAll().stream()
				.filter(booking -> booking.getFlights().size() > 1)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public FlightBooking createBooking(FlightBooking booking) {
		booking.setId(null); // Ensure we create a new booking
		
		// Verify passenger exists
		Passenger passenger = passengerRepo.findById(booking.getPassenger().getId())
				.orElseThrow(() -> new PassengerNotFoundException(booking.getPassenger().getId()));
		booking.setPassenger(passenger);
		
		// Verify all flights exist
		booking.getFlights().forEach(flight -> {
			flightRepo.findById(flight.getId())
					.orElseThrow(() -> new FlightNotFoundException(flight.getId()));
		});
		
		return bookingRepo.save(booking);
	}

	@Override
	@Transactional
	public FlightBooking updateBooking(String bookingId, FlightBooking booking) {
		FlightBooking existingBooking = getBooking(bookingId);
		
		// Verify passenger exists
		Passenger passenger = passengerRepo.findById(booking.getPassenger().getId())
				.orElseThrow(() -> new PassengerNotFoundException(booking.getPassenger().getId()));
		existingBooking.setPassenger(passenger);
		
		// Verify all flights exist
		booking.getFlights().forEach(flight -> {
			flightRepo.findById(flight.getId())
					.orElseThrow(() -> new FlightNotFoundException(flight.getId()));
		});
		existingBooking.setFlights(booking.getFlights());
		
		return bookingRepo.save(existingBooking);
	}

	@Override
	@Transactional
	public void deleteBooking(String bookingId) {
		FlightBooking booking = getBooking(bookingId);
		bookingRepo.delete(booking);
	}

	@Override
	@Transactional
	public void createSampleBookings() {
		List<Passenger> allPassengers = passengerRepo.findAll();
		final Random flightSelector = new Random(1);
		
		int[] passCounter = new int[1];
		
		allPassengers.stream().forEach(passenger -> {
			passCounter[0]++;
			
			if(passCounter[0] % 3 == 0) {
				FlightBooking booking = new FlightBooking();
				booking.setPassenger(passenger);
				booking.setFlights(flightRepo.findAll().stream()
						.filter(f -> flightSelector.nextBoolean())
						.collect(Collectors.toSet()));
				bookingRepo.save(booking);
			}
		});
	}
}
