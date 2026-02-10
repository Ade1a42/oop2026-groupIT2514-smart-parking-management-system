package parking.persistence.repository;

import parking.domain.model.ParkingSpot;
import parking.domain.repository.IParkingSpotRepository;
import parking.persistence.database.IDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParkingSpotRepository implements IParkingSpotRepository {
    private final IDB db;

    public ParkingSpotRepository(IDB db) {
        this.db = db;
    }

    @Override
    public ParkingSpot create(ParkingSpot spot) {
        String sql = "INSERT INTO parking_spots (spot_number, type, status, zone) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, spot.getSpotNumber());
            stmt.setString(2, spot.getType());
            stmt.setString(3, spot.getStatus());
            stmt.setString(4, spot.getZone());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                spot.setId(rs.getInt("id"));
            }
            return spot;

        } catch (SQLException e) {
            System.err.println("Error creating parking spot: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ParkingSpot findById(int id) {
        String sql = "SELECT * FROM parking_spots WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new ParkingSpot(
                        rs.getInt("id"),
                        rs.getString("spot_number"),
                        rs.getString("type"),
                        rs.getString("status"),
                        rs.getString("zone")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error finding parking spot: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ParkingSpot> findAll() {
        List<ParkingSpot> spots = new ArrayList<>();
        String sql = "SELECT * FROM parking_spots ORDER BY id";

        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                spots.add(new ParkingSpot(
                        rs.getInt("id"),
                        rs.getString("spot_number"),
                        rs.getString("type"),
                        rs.getString("status"),
                        rs.getString("zone")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching parking spots: " + e.getMessage());
        }
        return spots;
    }

    @Override
    public List<ParkingSpot> findByStatus(String status) {
        List<ParkingSpot> spots = new ArrayList<>();
        String sql = "SELECT * FROM parking_spots WHERE status = ? ORDER BY id";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.toUpperCase());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                spots.add(new ParkingSpot(
                        rs.getInt("id"),
                        rs.getString("spot_number"),
                        rs.getString("type"),
                        rs.getString("status"),
                        rs.getString("zone")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding spots by status: " + e.getMessage());
        }
        return spots;
    }

    @Override
    public ParkingSpot update(ParkingSpot spot) {
        String sql = "UPDATE parking_spots SET spot_number = ?, type = ?, status = ?, zone = ? WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, spot.getSpotNumber());
            stmt.setString(2, spot.getType());
            stmt.setString(3, spot.getStatus());
            stmt.setString(4, spot.getZone());
            stmt.setInt(5, spot.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                return spot;
            }
        } catch (SQLException e) {
            System.err.println("Error updating parking spot: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM parking_spots WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting parking spot: " + e.getMessage());
            return false;
        }
    }
}