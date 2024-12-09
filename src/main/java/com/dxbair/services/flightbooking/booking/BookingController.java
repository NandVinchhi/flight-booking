package com.dxbair.services.flightbooking.booking;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.dxbair.services.flightbooking.booking.model.FlightBookingModel;
import com.dxbair.services.flightbooking.booking.model.FlightBookingSummaryModel;
import com.dxbair.services.flightbooking.booking.model.converter.ToFlightBookingModelConverter;
import com.dxbair.services.flightbooking.domain.entity.Flight;
import com.dxbair.services.flightbooking.domain.entity.FlightBooking;
import com.dxbair.services.flightbooking.domain.entity.Passenger;
import com.dxbair.services.flightbooking.booking.model.BookingRequest;
import com.dxbair.services.flightbooking.passenger.PassengerService;
import com.dxbair.services.flightbooking.flight.FlightService;

@RestController
@RequestMapping("bookings")
public class BookingController {

	private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

	@Autowired
	private BookingService bookingService;

	@Autowired
	private ToFlightBookingModelConverter toBookingModelConverter;

	@Autowired
	private PassengerService passengerService;

	@Autowired
	private FlightService flightService;

	@GetMapping("/{bookingId}")
	public @ResponseBody FlightBookingModel getBookingById(@PathVariable String bookingId) {

		logger.info("Finding booking by id ... " + bookingId);
		return toBookingModelConverter.convert(bookingService.getBooking(bookingId));
	}

	@PostMapping
	public @ResponseBody FlightBookingModel createBooking(@RequestBody BookingRequest bookingRequest) {
		Passenger passenger = passengerService.getPassengerById(bookingRequest.getPassengerId());
		Set<Flight> flights = bookingRequest.getFlightIds().stream()
				.map(flightService::getFlightById)
				.collect(Collectors.toSet());
		if (passenger == null || flights == null) {
			throw new BookingNotFoundException(bookingRequest.getPassengerId());
		}
		return toBookingModelConverter.convert(bookingService.createBooking(bookingRequest));
	}

	@PutMapping("/{bookingId}")
	public @ResponseBody FlightBookingModel updateBooking(@PathVariable String bookingId,
			@RequestBody BookingRequest bookingRequest) {
		Passenger passenger = passengerService.getPassengerById(bookingRequest.getPassengerId());
		Set<Flight> flights = bookingRequest.getFlightIds().stream()
				.map(flightService::getFlightById)
				.collect(Collectors.toSet());
		if (passenger == null || flights == null) {
			throw new BookingNotFoundException(bookingRequest.getPassengerId());
		}
		return toBookingModelConverter.convert(bookingService.updateBooking(bookingId, bookingRequest));
	}

	@DeleteMapping("/{bookingId}")
	public @ResponseBody void deleteBooking(@PathVariable String bookingId) {
		bookingService.deleteBooking(bookingId);
	}

	@GetMapping
	public @ResponseBody List<FlightBookingSummaryModel> getBookings(
			@RequestParam(required = false, name = "uid") String passengerId,
			@RequestParam(required = false, name = "multi-flights", defaultValue = "false") boolean multiFlights) {

		logger.info("Finding booking by passengerId ... " + passengerId);

		List<FlightBooking> bookings = null;

		if (StringUtils.hasText(passengerId)) {

			if (multiFlights) {
				bookings = bookingService.getAllMultiFlightBookingsByPassenger(passengerId);
			} else {
				bookings = bookingService.getAllBookingsByPassenger(passengerId);
			}
		} else {
			throw new BookingNotFoundException(null);
		}

		List<FlightBookingSummaryModel> bookingModels = new ArrayList<>(bookings.size());
		bookings.stream().forEach(booking -> {
			bookingModels.add(new FlightBookingSummaryModel(booking.getId(), booking.getPassenger().getLastName(),
					((Flight) booking.getFlights().toArray()[0]).getDeparture()));
		});
		return bookingModels;
	}
}
