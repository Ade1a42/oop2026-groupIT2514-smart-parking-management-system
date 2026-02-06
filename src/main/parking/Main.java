package parking;

import parking.database.DatabaseConnection;
import parking.database.IDB;
import parking.model.ParkingSpot;
import parking.model.Vehicle;
import parking.repository.ParkingSpotRepository;
import parking.repository.VehicleRepository;
import parking.exception.InvalidVehiclePlate;
import parking.exception.NoFreeSpots;

import java.sql.*;
import java.time.Duration;
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
                System.out.println("2. List all vehicles");
                System.out.println("3. Add parking spot");
                System.out.println("4. List all parking spots");
                System.out.println("5. Show available parking spots");
                System.out.println("6. Reserve a parking spot");
                System.out.println("7. Release a parking spot and calculate fee");
                System.out.println("8. Exit");
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
                        System.out.println("All vehicles:");
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

                        System.out.println("Added parking spot: " + newSpot.getSpotNumber());
                        break;

                    case "4":
                        List<ParkingSpot> allSpots = spotRepo.findAll();
                        System.out.println("All spots:");
                        for (ParkingSpot s : allSpots) {
                            System.out.println(s.getId() + ": " + s.getSpotNumber() + " [" + s.getType() + "] Status=" + s.getStatus());
                        }
                        break;

                    case "5":
                        List<ParkingSpot> freeSpots = spotRepo.findByStatus("AVAILABLE");
                        if (freeSpots.isEmpty()) {
                            System.out.println("No free spots available.");
                        } else {
                            System.out.println("Free spots:");
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
                        System.out.println("Reserved spot " + spotToReserve.getSpotNumber() + " for vehicle " + v.getPlateNumber());
                        break;

                    case "7":
                        System.out.print("Spot ID to release: ");
                        int sid = Integer.parseInt(scanner.nextLine());
                        ParkingSpot spotToRelease = spotRepo.findById(sid);

                        if (spotToRelease == null || !activeReservations.containsKey(sid)) {
                            System.out.println("Spot not reserved.");
                            break;
                        }

                        // Вводим количество часов вручную
                        System.out.print("Enter number of hours parked: ");
                        int hoursParked = Integer.parseInt(scanner.nextLine());

                        double rate = 0;
                        try (Connection conn = db.getConnection();
                             PreparedStatement stmt = conn.prepareStatement("SELECT hourly_rate FROM tariffs WHERE spot_type = ?")) {
                            stmt.setString(1, spotToRelease.getType());
                            ResultSet rs = stmt.executeQuery();
                            if (rs.next()) {
                                rate = rs.getDouble("hourly_rate");
                            }
                        } catch (SQLException e) {
                            System.out.println("Error fetching tariff: " + e.getMessage());
                        }

                        double fee = rate * hoursParked;

                        // Освобождаем место
                        spotToRelease.setStatus("AVAILABLE");
                        spotRepo.update(spotToRelease);
                        activeReservations.remove(sid);

                        System.out.println("Spot released. Total fee: " + fee);
                        break;


                    case "8":
                        running = false;
                        System.out.println("Exiting...");
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
