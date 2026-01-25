package parking;

import parking.database.DatabaseConnection;
import parking.service.ParkingSpotRepository;
import parking.service.VehicleService;
import parking.model.ParkingSpot;

public class Main {
    public static void main(String[] args) {
        // Get database instance (Singleton)
        DatabaseConnection db = DatabaseConnection.getInstance();

        // Create repositories
        ParkingSpotRepository spotRepo = new ParkingSpotRepository(db);
        // VehicleRepository vehicleRepo = new VehicleRepository(db);

        // Create services with dependency injection
        ParkingSpotService spotService = new ParkingSpotService(spotRepo);
        // VehicleService vehicleService = new VehicleService(vehicleRepo);

        // DEMONSTRATE BASIC USER FLOWS:

        // 1. List all parking spots
        System.out.println("=== All Parking Spots ===");
        for (ParkingSpot spot : spotService.getAllSpots()) {
            System.out.println(spot);
        }

        // 2. Add a new parking spot
        System.out.println("\n=== Adding New Spot ===");
        ParkingSpot newSpot = spotService.addParkingSpot("A3", "STANDARD", "ZONE_A");
        System.out.println("Added: " + newSpot);

        // 3. Find spot by ID
        System.out.println("\n=== Finding Spot by ID ===");
        ParkingSpot foundSpot = spotService.getSpotById(1);
        System.out.println("Found: " + foundSpot);

        // 4. Get available spots (with exception handling)
        System.out.println("\n=== Available Spots ===");
        try {
            for (ParkingSpot spot : spotService.getAvailableSpots()) {
                System.out.println(spot);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}