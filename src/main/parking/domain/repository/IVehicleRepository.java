package parking.domain.repository;

import parking.domain.model.Vehicle;
import java.util.List;

public interface IVehicleRepository {
    Vehicle create(Vehicle vehicle);
    Vehicle findById(int id);
    Vehicle findByPlateNumber(String plateNumber);
    List<Vehicle> findAll();
    Vehicle update(Vehicle vehicle);
    boolean delete(int id);
}