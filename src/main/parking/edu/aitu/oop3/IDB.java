package parking.edu.aitu.oop3;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDB {
    Connection getConnection() throws SQLException;
}
