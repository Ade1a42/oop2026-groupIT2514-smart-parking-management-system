package parking.persistence.repository;

import parking.domain.model.Vehicle;
import parking.domain.repository.Repository;
import parking.persistence.database.IDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepository implements Repository<Vehicle> {

    private final IDB db;

    public VehicleRepository(IDB db) {
        this.db = db;
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (plate_number, owner_name) VALUES (?, ?) RETURNING id";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicle.getPlateNumber());
            stmt.setString(2, vehicle.getOwnerName());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                vehicle.setId(rs.getInt("id"));
            }
            return vehicle;

        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Vehicle findById(int id) {
        String sql = "SELECT * FROM vehicles WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Vehicle(
                        rs.getInt("id"),
                        rs.getString("plate_number"),
                        rs.getString("owner_name")
                );
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";

        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vehicles.add(new Vehicle(
                        rs.getInt("id"),
                        rs.getString("plate_number"),
                        rs.getString("owner_name")
                ));
            }
        } catch (SQLException e) {
            return vehicles;
        }
        return vehicles;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM vehicles WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public Vehicle findByPlateNumber(String plateNumber) {
        String sql = "SELECT * FROM vehicles WHERE plate_number = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, plateNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Vehicle(
                        rs.getInt("id"),
                        rs.getString("plate_number"),
                        rs.getString("owner_name")
                );
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }
}
