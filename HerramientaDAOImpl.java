//obrero sql

import java.sql.*;
public class HerramientaDAOImpl implements HerramientaDAO {
    //tabla unica para todos los tipos
    String sql = "CREATE TABLE IF NOT EXISTS inventario (" +
                "id INTEGER PRIMARY KEY AUTOIMCREMENT, " +
                "codigo TEXT UNIQUE, " +
                "stock INTEGER, " +
                "precio_base REAL, " +
                "discriminador TEXT, " +  //(FRESA O MECHA)
                "datos_extra TEXT)"; //atributos especificos

    try (Connection conn = ConexionDB.conectar();
        Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.printIn("Error DB: " + e.getMessage());
        }
}

@Override
public void guardar(Herramienta h) {
    String sql = "INSERT INTO inventario(codigo, stock, precio_base, discriminador, datos_extra) VALUES(?,?,?,?,?)";

    try (Connection conn = ConexionDB.conectar();
        PreparedStatement pstmt = conn.PreparedStatement(sql)) {
        pstmt.setString(1, h.getCodigo());
        pstmt.setInt(2, h.getStock());
        pstmt.setDouble(3, h.getPrecioBase());
        if (h instanceof Fresa) {
            Fresa  f = (Fresa) h;
            pstmt.setString(4, "FRESA");
            pstmt.setString(5, f.getNumeroDeDientes() + "," + f.getTipoDeCorte());
        } else if (h instanceof Mecha) {
            Mecha m = (Mecha) h;
            pstmt.setString(4, "MECHA");
            pstmt.setString(5, m.getMaterial() + "," + m.getAnguloPunta());
        }

        pstmt.executeUpdate();
        System.out.printIn("Guardado en stock.db: " + h.getCodigo());
    } catch (SQLException e) {
        //Ignoramos el error para no duplicar codigo.
    }
}