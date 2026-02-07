package parking;

import parking.config.TariffConfig;
import parking.database.DatabaseConnection;
import parking.database.IDB;
import parking.factory.ParkingSpotFactory;
import parking.model.ParkingSpot;
import parking.model.Reservation;
import parking.model.Vehicle;
import parking.repository.ParkingSpotRepository;
import parking.repository.VehicleRepository;
import parking.util.VehicleFilters;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== SMART PARKING MANAGEMENT SYSTEM ===");

        try {
            IDB db = new DatabaseConnection();
            VehicleRepository vehicleRepo = new VehicleRepository(db);
            ParkingSpotRepository spotRepo = new ParkingSpotRepository(db);
            Scanner scanner = new Scanner(System.in);

            java.util.Map<Integer, LocalDateTime> activeReservations = new java.util.HashMap<>();

            boolean running = true;

            while (running) {
                System.out.println("\nSelect an option:");
                System.out.println("1. Register vehicle");
                System.out.println("2. List all vehicles (with lambda filter)");
                System.out.println("3. Add parking spot");
                System.out.println("4. List all parking spots");
                System.out.println("5. Show available parking spots");
                System.out.println("6. Reserve a parking spot");
                System.out.println("7. Release a parking spot and calculate fee");
                System.out.println("8. Exit");
                System.out.println("9. Smart Reservation with All Patterns");
                System.out.print("Enter option: ");

                String option = scanner.nextLine();

                switch (option) {
                    case "1":
                        System.out.print("Plate number: ");
                        String plate = scanner.nextLine();
                        System.out.print("Owner name: ");
                        String owner = scanner.nextLine();
                        Vehicle vehicle = vehicleRepo.save(new Vehicle(plate, owner));
                        System.out.println("Vehicle registered: " + vehicle.getPlateNumber());
                        break;

                    case "2":
                        List<Vehicle> vehicles = vehicleRepo.findAll();

                        System.out.print("Filter by owner name (or press Enter to skip): ");
                        String filterText = scanner.nextLine();

                        if (!filterText.isBlank()) {
                            vehicles = VehicleFilters.filter(
                                    vehicles,
                                    v -> v.getOwnerName() != null &&
                                            v.getOwnerName().toLowerCase().contains(filterText.toLowerCase())
                            );
                        }

                        System.out.println("Vehicles:");
                        for (Vehicle v : vehicles) {
                            System.out.println(v.getId() + ": " + v.getPlateNumber() + " - " + v.getOwnerName());
                        }
                        break;

                    case "3":
                        System.out.print("Spot number: ");
                        String number = scanner.nextLine();
                        System.out.print("Type (STANDARD/ELECTRIC/DISABLED): ");
                        String type = scanner.nextLine().toUpperCase();
                        System.out.print("Zone: ");
                        String zone = scanner.nextLine();
                        ParkingSpot newSpot = new ParkingSpot(0, number, type, "AVAILABLE", zone);
                        spotRepo.create(newSpot);
                        System.out.println("Added parking spot: " + newSpot.getSpotNumber());
                        break;

                    case "4":
                        List<ParkingSpot> allSpots = spotRepo.findAll();
                        for (ParkingSpot s : allSpots) {
                            System.out.println(s.getId() + ": " + s.getSpotNumber() +
                                    " [" + s.getType() + "] Status=" + s.getStatus());
                        }
                        break;

                    case "5":
                        List<ParkingSpot> freeSpots = spotRepo.findByStatus("AVAILABLE");
                        if (freeSpots.isEmpty()) {
                            System.out.println("No free spots available.");
                        } else {
                            for (ParkingSpot s : freeSpots) {
                                System.out.println(s.getId() + ": " + s.getSpotNumber() + " [" + s.getType() + "]");
                            }
                        }
                        break;

                    case "6":
                        List<ParkingSpot> spots = spotRepo.findByStatus("AVAILABLE");
                        if (spots.isEmpty()) {
                            System.out.println("No free spots to reserve.");
                            break;
                        }
                        System.out.print("Vehicle ID: ");
                        int vid = Integer.parseInt(scanner.nextLine());
                        Vehicle v = vehicleRepo.findById(vid);
                        if (v == null) {
                            System.out.println("Vehicle not found.");
                            break;
                        }
                        ParkingSpot spotToReserve = spots.get(0);
                        spotToReserve.setStatus("OCCUPIED");
                        spotRepo.update(spotToReserve);
                        activeReservations.put(spotToReserve.getId(), LocalDateTime.now());
                        System.out.println("Reserved spot " + spotToReserve.getSpotNumber());
                        break;

                    case "7":
                        System.out.print("Spot ID to release: ");
                        int sid = Integer.parseInt(scanner.nextLine());
                        ParkingSpot spotToRelease = spotRepo.findById(sid);

                        if (spotToRelease == null || !activeReservations.containsKey(sid)) {
                            System.out.println("Spot not reserved.");
                            break;
                        }

                        System.out.print("Enter number of hours parked: ");
                        int hours = Integer.parseInt(scanner.nextLine());

                        double rate = 0;
                        try (Connection conn = db.getConnection();
                             PreparedStatement stmt = conn.prepareStatement(
                                     "SELECT hourly_rate FROM tariffs WHERE spot_type = ?")) {
                            stmt.setString(1, spotToRelease.getType());
                            ResultSet rs = stmt.executeQuery();
                            if (rs.next()) {
                                rate = rs.getDouble("hourly_rate");
                            }
                        }

                        double fee = rate * hours;
                        spotToRelease.setStatus("AVAILABLE");
                        spotRepo.update(spotToRelease);
                        activeReservations.remove(sid);

                        System.out.println("Spot released. Total fee: " + fee);
                        break;

                    case "8":
                        running = false;
                        System.out.println("Exiting...");
                        break;

                    case "9":
                        System.out.println("\n=== A4: Smart Reservation (All Patterns) ===");

                        // Show available spots
                        List<ParkingSpot> freeSpotss = spotRepo.findByStatus("AVAILABLE");
                        if (freeSpotss.isEmpty()) {
                            System.out.println("No free spots.");
                            break;
                        }

                        System.out.println("Available spots:");
                        for (int i = 0; i < freeSpotss.size(); i++) {
                            ParkingSpot spot = freeSpotss.get(i);
                            System.out.println((i + 1) + ". " + spot.getSpotNumber() + " [" + spot.getType() + "]");
                        }

                        System.out.print("Select spot: ");
                        ParkingSpot selectedSpot = freeSpotss.get(Integer.parseInt(scanner.nextLine()) - 1);

                        System.out.print("Vehicle ID: ");
                        int vehicleId = Integer.parseInt(scanner.nextLine());
                        Vehicle vehiclee = vehicleRepo.findById(vehicleId);
                        if (vehiclee == null) {
                            System.out.println("Vehicle not found!");
                            break;
                        }

                        System.out.print("Hours: ");
                        int hourss = Integer.parseInt(scanner.nextLine());

                        // 1. SINGLETON
                        TariffConfig config = TariffConfig.getInstance();
                        double ratee = config.getHourlyRate();

                        // 2. FACTORY
                        ParkingSpot factorySpot = ParkingSpotFactory.createSpot(
                                selectedSpot.getType(),
                                selectedSpot.getSpotNumber(),
                                selectedSpot.getZone()
                        );

                        // 3. BUILDER
                        Reservation reservation = new Reservation.ReservationBuilder(vehicleId, selectedSpot.getId())
                                .withTotalCost(ratee * hourss)
                                .build();


                        selectedSpot.setStatus("OCCUPIED");
                        spotRepo.update(selectedSpot);
                        activeReservations.put(selectedSpot.getId(), LocalDateTime.now());

                        System.out.println("✓ Created with all A4 patterns");
                        break;

                    default:
                        System.out.println("Invalid option.");
                }
            }

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
