package com.dxbair.services.flightbooking.passenger;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dxbair.services.flightbooking.domain.entity.Passenger;
import com.dxbair.services.flightbooking.domain.repo.PassengerRepository;

@Service
@Transactional(readOnly = true)
public class PassengerServiceImpl implements PassengerService {

	@Autowired
	private PassengerRepository passengerRepo;

	@Override
	public Passenger getPassengerById(String passengerId) {
		return passengerRepo.findById(passengerId).orElseThrow(() -> new PassengerNotFoundException(passengerId));
	}

	@Override
	public List<Passenger> getAllPassengers() {
		return passengerRepo.findAll();
	}

	@Override
	@Transactional
	public Passenger createPassenger(Passenger passenger) {
		passenger.setId(null); // Ensure we create a new passenger
		return passengerRepo.save(passenger);
	}

	@Override
	@Transactional
	public Passenger updatePassenger(String passengerId, Passenger passenger) {
		Passenger existingPassenger = getPassengerById(passengerId);
		
		existingPassenger.setFirstName(passenger.getFirstName());
		existingPassenger.setLastName(passenger.getLastName());
		existingPassenger.setEmail(passenger.getEmail());
		
		return passengerRepo.save(existingPassenger);
	}

	@Override
	@Transactional
	public void deletePassenger(String passengerId) {
		Passenger passenger = getPassengerById(passengerId);
		passengerRepo.delete(passenger);
	}
}
