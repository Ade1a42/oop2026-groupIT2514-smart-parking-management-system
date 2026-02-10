package parking.UI;


import parking.persistence.database.DatabaseConnection;
import parking.persistence.database.IDB;
import parking.business.factory.ParkingSpotFactory;
import parking.domain.model.ParkingSpot;
import parking.domain.model.Reservation;
import parking.domain.model.Vehicle;
import parking.persistence.repository.ParkingSpotRepository;
import parking.persistence.repository.ReservationRepository;
import parking.persistence.repository.VehicleRepository;
import parking.business.service.ReservationService;  
import parking.business.service.PricingService;      
import parking.business.util.VehicleFilters;


import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main() {
        System.out.println("=== SMART PARKING MANAGEMENT SYSTEM ===");

        try {
            IDB db = new DatabaseConnection();
            VehicleRepository vehicleRepo = new VehicleRepository(db);
            ParkingSpotRepository spotRepo = new ParkingSpotRepository(db);
            ReservationRepository reservationRepo = new ReservationRepository(db);

            // ADD THESE SERVICES
            ReservationService reservationService = new ReservationService(reservationRepo, spotRepo);
            PricingService pricingService = new PricingService();

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

                        // USE THE SERVICE INSTEAD OF DIRECT REPOSITORY CALLS
                        try {
                            Reservation reservation = reservationService.createReservation(v.getId(), spot.getId());
                            System.out.println("Reserved spot " + spot.getSpotNumber());
                            System.out.println("Reservation ID: " + reservation.getId());
                        } catch (Exception e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;

                    case "7":
                        System.out.print("Reservation ID: ");
                        int rid = Integer.parseInt(scanner.nextLine());

                        // First get the reservation
                        Reservation res = reservationRepo.findById(rid);
                        if (res == null || !res.getStatus().equals("ACTIVE")) {
                            System.out.println("Reservation not active.");
                            break;
                        }

                        System.out.print("Enter hours parked: ");
                        int hours = Integer.parseInt(scanner.nextLine());

                        // USE PRICING SERVICE
                        double total = pricingService.calculateReservationCost(res, hours);

                        // APPLY THE COST TO RESERVATION
                        pricingService.applyCostToReservation(res, total);

                        // USE RESERVATION SERVICE TO FINISH
                        try {
                            reservationService.finishReservation(rid, total);
                            System.out.println("Total cost: $" + total);
                        } catch (Exception e) {
                            System.out.println("Error finishing reservation: " + e.getMessage());
                        }
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