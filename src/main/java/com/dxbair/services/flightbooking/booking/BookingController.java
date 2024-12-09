package com.dxbair.services.flightbooking.booking;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.dxbair.services.flightbooking.booking.model.FlightBookingModel;
import com.dxbair.services.flightbooking.booking.model.FlightBookingSummaryModel;
import com.dxbair.services.flightbooking.booking.model.converter.ToFlightBookingModelConverter;
import com.dxbair.services.flightbooking.domain.entity.Flight;
import com.dxbair.services.flightbooking.domain.entity.FlightBooking;

@RestController
@RequestMapping("bookings")
public class BookingController {

	private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

	@Autowired
	private BookingService bookingService;

	@Autowired
	private ToFlightBookingModelConverter toBookingModelConverter;

	@GetMapping("/{bookingId}")
	public @ResponseBody FlightBookingModel getBookingById(@PathVariable String bookingId) {
		logger.info("Finding booking by id ... " + bookingId);
		return toBookingModelConverter.convert(bookingService.getBooking(bookingId));
	}

	@GetMapping
	public @ResponseBody List<FlightBookingSummaryModel> getBookings(
			@RequestParam(required = false, name = "uid") String passengerId,
			@RequestParam(required = false, name = "multi-flights", defaultValue = "false") boolean multiFlights) {

		logger.info("Finding bookings for passengerId={} and multiFlights={}", passengerId, multiFlights);

		List<FlightBooking> bookings;
		if (!StringUtils.hasLength(passengerId)) {
			if (multiFlights) {
				bookings = bookingService.getAllMultiFlightBookings();
			} else {
				throw new BookingNotFoundException(null);
			}
		} else {
			if (multiFlights) {
				bookings = bookingService.getAllMultiFlightBookingsByPassenger(passengerId);
			} else {
				bookings = bookingService.getAllBookingsByPassenger(passengerId);
			}
		}

		List<FlightBookingSummaryModel> bookingModels = new ArrayList<>(bookings.size());
		bookings.forEach(booking -> {
			bookingModels.add(new FlightBookingSummaryModel(booking.getId(), booking.getPassenger().getLastName(),
					((Flight) booking.getFlights().toArray()[0]).getDeparture()));
		});
		return bookingModels;
	}

	@PostMapping
	public ResponseEntity<FlightBookingModel> createBooking(@RequestBody FlightBooking booking) {
		FlightBooking createdBooking = bookingService.createBooking(booking);
		return new ResponseEntity<>(toBookingModelConverter.convert(createdBooking), HttpStatus.CREATED);
	}

	@PutMapping("/{bookingId}")
	public ResponseEntity<FlightBookingModel> updateBooking(@PathVariable String bookingId, @RequestBody FlightBooking booking) {
		FlightBooking updatedBooking = bookingService.updateBooking(bookingId, booking);
		return ResponseEntity.ok(toBookingModelConverter.convert(updatedBooking));
	}

	@DeleteMapping("/{bookingId}")
	public ResponseEntity<Void> deleteBooking(@PathVariable String bookingId) {
		bookingService.deleteBooking(bookingId);
		return ResponseEntity.noContent().build();
	}
}
