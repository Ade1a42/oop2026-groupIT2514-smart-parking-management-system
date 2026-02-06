package parking.util;

import parking.model.Vehicle;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VehicleFilters {

    public static List<Vehicle> filter(List<Vehicle> vehicles, Predicate<Vehicle> predicate) {
        return vehicles.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
}
