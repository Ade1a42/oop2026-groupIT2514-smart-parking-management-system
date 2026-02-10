package parking.business.service;

import parking.domain.exception.IllegalArgumentException;
import parking.domain.model.Reservation;
import parking.domain.model.ParkingSpot;
import parking.persistence.repository.ReservationRepository;
import parking.persistence.repository.ParkingSpotRepository;

import java.time.LocalDateTime;

public class ReservationService {
    private ReservationRepository reservationRepo;
    private ParkingSpotRepository spotRepo;

    public ReservationService(ReservationRepository reservationRepo, ParkingSpotRepository spotRepo) {
        this.reservationRepo = reservationRepo;
        this.spotRepo = spotRepo;
    }

    // Create reservation
    public Reservation createReservation(int vehicleId, int spotId) {
        // Business logic for creating a reservation
        ParkingSpot spot = spotRepo.findById(spotId);
        if (spot == null || !"AVAILABLE".equals(spot.getStatus())) {
            throw new IllegalArgumentException("Spot is not available");
        }

        Reservation reservation = new Reservation.ReservationBuilder(vehicleId, spotId)
                .withStartTime(LocalDateTime.now())
                .withStatus("ACTIVE")
                .build();

        reservationRepo.save(reservation);

        // Update spot status
        spot.setStatus("OCCUPIED");
        spotRepo.update(spot);

        return reservation;
    }

    // Finish reservation
    public Reservation finishReservation(int reservationId, double totalCost) {
        Reservation res = reservationRepo.findById(reservationId);
        if (res == null || !"ACTIVE".equals(res.getStatus())) {
            throw new IllegalArgumentException("Reservation not active");
        }

        res.setEndTime(LocalDateTime.now());
        res.setTotalCost(totalCost);
        res.setStatus("COMPLETED");
        reservationRepo.update(res);

        // Free up the spot
        ParkingSpot spot = spotRepo.findById(res.getSpotId());
        spot.setStatus("AVAILABLE");
        spotRepo.update(spot);

        return res;
    }
}