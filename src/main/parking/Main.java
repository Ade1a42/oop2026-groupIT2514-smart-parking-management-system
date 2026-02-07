package parking;

import parking.config.TariffConfig;
import parking.database.DatabaseConnection;
import parking.database.IDB;
import parking.factory.ParkingSpotFactory;
import parking.model.ParkingSpot;
import parking.model.Reservation;
import parking.model.Vehicle;
import parking.repository.ParkingSpotRepository;
import parking.repository.ReservationRepository;
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
            ReservationRepository reservationRepo = new ReservationRepository(db);

            Scanner scanner = new Scanner(System.in);

            boolean running = true;

            while (running) {
                System.out.println("\nSelect an option:");
                System.out.println("1. Register vehicle");
                System.out.println("2. List all vehicles (with lambda filter)");
                System.out.println("3. Add parking spot (Factory)");
                System.out.println("4. List all parking spots");
                System.out.println("5. Show available parking spots");
                System.out.println("6. Reserve a parking spot (Builder)");
                System.out.println("7. Finish reservation & calculate fee (Singleton)");
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

                        ParkingSpot newSpot = ParkingSpotFactory.createSpot(type, number, zone);
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
                            System.out.println("No free spots.");
                            break;
                        }

                        System.out.print("Vehicle ID: ");
                        int vid = Integer.parseInt(scanner.nextLine());
                        Vehicle v = vehicleRepo.findById(vid);

                        if (v == null) {
                            System.out.println("Vehicle not found.");
                            break;
                        }

                        ParkingSpot spot = spots.get(0);

                        Reservation reservation = new Reservation.ReservationBuilder(v.getId(), spot.getId())
                                .withStartTime(LocalDateTime.now())
                                .withStatus("ACTIVE")
                                .build();

                        reservationRepo.save(reservation);

                        spot.setStatus("OCCUPIED");
                        spotRepo.update(spot);

                        System.out.println("Reserved spot " + spot.getSpotNumber());
                        break;


                    case "7":
                        System.out.print("Reservation ID: ");
                        int rid = Integer.parseInt(scanner.nextLine());

                        Reservation res = reservationRepo.findById(rid);
                        if (res == null || !res.getStatus().equals("ACTIVE")) {
                            System.out.println("Reservation not active.");
                            break;
                        }

                        System.out.print("Enter hours parked: ");
                        int hours = Integer.parseInt(scanner.nextLine());

                        TariffConfig tariff = TariffConfig.getInstance();
                        double total = Math.min(hours * tariff.getHourlyRate(), tariff.getDailyMax());

                        res.setEndTime(LocalDateTime.now());
                        res.setTotalCost(total);
                        res.setStatus("COMPLETED");
                        reservationRepo.update(res);

                        ParkingSpot s = spotRepo.findById(res.getSpotId());
                        s.setStatus("AVAILABLE");
                        spotRepo.update(s);

                        System.out.println("Total cost: " + total);
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
