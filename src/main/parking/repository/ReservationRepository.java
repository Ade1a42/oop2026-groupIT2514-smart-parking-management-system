package parking.repository;

import parking.database.IDB;
import parking.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {

    private final IDB db;

    public ReservationRepository(IDB db) {
        this.db = db;
    }

    public Reservation save(Reservation reservation) {
        String sql = """
                INSERT INTO reservations(vehicle_id, spot_id, start_time, end_time, total_cost, status)
                VALUES (?, ?, ?, ?, ?, ?) RETURNING id
                """;

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservation.getVehicleId());
            stmt.setInt(2, reservation.getSpotId());
            stmt.setTimestamp(3, Timestamp.valueOf(reservation.getStartTime()));

            if (reservation.getEndTime() != null)
                stmt.setTimestamp(4, Timestamp.valueOf(reservation.getEndTime()));
            else
                stmt.setNull(4, Types.TIMESTAMP);

            stmt.setDouble(5, reservation.getTotalCost());
            stmt.setString(6, reservation.getStatus());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                reservation.setId(rs.getInt("id"));
            }

            return reservation;

        } catch (SQLException e) {
            System.out.println("Error saving reservation: " + e.getMessage());
            return null;
        }
    }

    public Reservation findById(int id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setVehicleId(rs.getInt("vehicle_id"));
                r.setSpotId(rs.getInt("spot_id"));

                Timestamp start = rs.getTimestamp("start_time");
                if (start != null) r.setStartTime(start.toLocalDateTime());

                Timestamp end = rs.getTimestamp("end_time");
                if (end != null) r.setEndTime(end.toLocalDateTime());

                r.setTotalCost(rs.getDouble("total_cost"));
                r.setStatus(rs.getString("status"));

                return r;
            }

        } catch (SQLException e) {
            System.out.println("Error finding reservation: " + e.getMessage());
        }

        return null;
    }

    public boolean update(Reservation reservation) {
        String sql = """
                UPDATE reservations
                SET end_time = ?, total_cost = ?, status = ?
                WHERE id = ?
                """;

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (reservation.getEndTime() != null)
                stmt.setTimestamp(1, Timestamp.valueOf(reservation.getEndTime()));
            else
                stmt.setNull(1, Types.TIMESTAMP);

            stmt.setDouble(2, reservation.getTotalCost());
            stmt.setString(3, reservation.getStatus());
            stmt.setInt(4, reservation.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating reservation: " + e.getMessage());
            return false;
        }
    }

    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setVehicleId(rs.getInt("vehicle_id"));
                r.setSpotId(rs.getInt("spot_id"));
                r.setTotalCost(rs.getDouble("total_cost"));
                r.setStatus(rs.getString("status"));
                list.add(r);
            }

        } catch (SQLException e) {
            System.out.println("Error reading reservations: " + e.getMessage());
        }

        return list;
    }
}
