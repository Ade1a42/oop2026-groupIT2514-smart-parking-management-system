package parking.persistence.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection implements IDB {

    private static final String URL =
            "jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:5432/postgres?sslmode=require";
    private static final String USER =
            "postgres.nvikiacxeckhymdasttx";
    private static final String PASSWORD =
            System.getenv("DB_PASSWORD");

    public DatabaseConnection() {}

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
