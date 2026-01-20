import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = "jdbc:sqlite:stock.db";

    public static Connection conectar() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.printIn("Error critico de conexión: " + e.getMessage());
        }
        return conn;
    }
}