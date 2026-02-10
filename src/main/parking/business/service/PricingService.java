package parking.business.service;

import parking.business.config.TariffConfig;
import parking.domain.model.Reservation;

public class PricingService {

    public double calculateReservationCost(Reservation reservation, int hours) {
        // Get tariff configuration (Singleton)
        TariffConfig tariff = TariffConfig.getInstance();

        // Business logic for pricing
        double baseCost = hours * tariff.getHourlyRate();

        // Apply daily max limit
        double totalCost = Math.min(baseCost, tariff.getDailyMax());

        // Future: Add spot type multipliers (Electric, Disabled, etc.)
        return totalCost;
    }

    public void applyCostToReservation(Reservation reservation, double cost) {
        reservation.setTotalCost(cost);
    }
}