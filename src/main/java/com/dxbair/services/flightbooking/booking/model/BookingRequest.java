package com.dxbair.services.flightbooking.booking.model;

import java.util.Set;

public class BookingRequest {
    private String passengerId;
    private Set<String> flightIds;

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public Set<String> getFlightIds() {
        return flightIds;
    }

    public void setFlightIds(Set<String> flightIds) {
        this.flightIds = flightIds;
    }
}