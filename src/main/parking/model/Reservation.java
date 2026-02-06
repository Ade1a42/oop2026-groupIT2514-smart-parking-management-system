package parking.model;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private int vehicleId;
    private int spotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double totalCost;
    private String status;  // active, completed, canceled

    private Reservation(ReservationBuilder builder) {
        this.vehicleId = builder.vehicleId;
        this.spotId = builder.spotId;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.totalCost = builder.totalCost;
        this.status = builder.status;
    }

    // existing constructors for backward compatibility
    public Reservation() {}
    public Reservation(int vehicleId, int spotId) {
        this.vehicleId = vehicleId;
        this.spotId = spotId;
        this.startTime = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    public static class ReservationBuilder {
        private final int vehicleId;
        private final int spotId;

        private LocalDateTime startTime = LocalDateTime.now();
        private LocalDateTime endTime;
        private double totalCost = 0.0;
        private String status = "ACTIVE";

        public ReservationBuilder(int vehicleId, int spotId) {
            this.vehicleId = vehicleId;
            this.spotId = spotId;
        }

        public ReservationBuilder withStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public ReservationBuilder withEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public ReservationBuilder withTotalCost(double totalCost) {
            this.totalCost = totalCost;
            return this;
        }

        public ReservationBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public Reservation build() {
            validateReservation();

            return new Reservation(this);
        }


        private void validateReservation() {
            if (endTime != null && endTime.isBefore(startTime)) {
                throw new IllegalArgumentException("End time cannot be before start time!");
            }
            if (totalCost < 0) {
                throw new IllegalArgumentException("Total cost cannot be negative!");
            }
        }
    }

    // setters and getters

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setSpotId(int spotId) {
        this.spotId = spotId;
    }

    public int getSpotId() {
        return spotId;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
