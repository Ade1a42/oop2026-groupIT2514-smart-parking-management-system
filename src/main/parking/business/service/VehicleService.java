package parking.business.service;

import parking.domain.repository.IVehicleRepository;
import parking.domain.model.Vehicle;
import parking.domain.exception.InvalidVehiclePlate;
import java.util.List;

public class VehicleService {
    private final IVehicleRepository repository;

    public VehicleService(IVehicleRepository repository) {
        this.repository = repository;
    }

    public Vehicle registerVehicle(String plateNumber, String ownerName) throws InvalidVehiclePlate {
        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            throw new InvalidVehiclePlate("Plate number cannot be empty");
        }

        Vehicle existing = repository.findByPlateNumber(plateNumber);
        if (existing != null) {
            throw new InvalidVehiclePlate("Vehicle with plate " + plateNumber + " already registered");
        }

        Vehicle vehicle = new Vehicle(plateNumber, ownerName);
        return repository.create(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return repository.findAll();
    }

    public Vehicle getVehicleById(int id) {
        return repository.findById(id);
    }

    public Vehicle getVehicleByPlate(String plateNumber) {
        return repository.findByPlateNumber(plateNumber);
    }

    public Vehicle updateVehicle(Vehicle vehicle) throws InvalidVehiclePlate {
        if (vehicle.getPlateNumber() == null || vehicle.getPlateNumber().trim().isEmpty()) {
            throw new InvalidVehiclePlate("Plate number cannot be empty");
        }

        Vehicle existing = repository.findByPlateNumber(vehicle.getPlateNumber());
        if (existing != null && existing.getId() != vehicle.getId()) {
            throw new InvalidVehiclePlate("Another vehicle with plate " + vehicle.getPlateNumber() + " already exists");
        }

        return repository.update(vehicle);
    }

    public boolean deleteVehicle(int id) {
        return repository.delete(id);
    }
}