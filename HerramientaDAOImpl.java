import java.sql.*;

public class HerramientaDAOImpl implements HerramientaDAO {

    @Override
    public void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS inventario (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "codigo TEXT UNIQUE, " +
                     "stock INTEGER, " +
                     "precio_base REAL, " +
                     "discriminador TEXT, " + 
                     "datos_extra TEXT)";

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error DB: " + e.getMessage());
        }
    }

    @Override
    public void guardar(Herramienta h) {
        String sql = "INSERT INTO inventario(codigo, stock, precio_base, discriminador, datos_extra) VALUES(?,?,?,?,?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, h.getCodigo());
            pstmt.setInt(2, h.getStock());
            pstmt.setDouble(3, h.getPrecioBase());

            if (h instanceof Fresa) {
                Fresa f = (Fresa) h;
                pstmt.setString(4, "FRESA");
                pstmt.setString(5, f.getNumeroDeDientes() + "," + f.getTipoDeCorte());
            } else if (h instanceof Mecha) {
                Mecha m = (Mecha) h;
                pstmt.setString(4, "MECHA");
                pstmt.setString(5, m.getMaterial() + "," + m.getAnguloPunta());
            }

            pstmt.executeUpdate();
            System.out.println("Guardado en stock.db: " + h.getCodigo());

        } catch (SQLException e) {
        }
    }

    @Override
    public Herramienta buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM inventario WHERE codigo = ?";
        Herramienta resultado = null;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) { 
        
            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String tipo = rs.getString("discriminador");
                String extras = rs.getString("datos_extra");

                if (tipo.equals("FRESA")) {
                    String[] datos = extras.split(",");
                    resultado = new Fresa(
                        rs.getString("codigo"),
                        rs.getDouble("precio_base"),
                        rs.getInt("stock"),
                        Integer.parseInt(datos[0]), // Dientes
                        datos[1]                    // Tipo Corte
                    );
                } else if (tipo.equals("MECHA")) {
                    String[] datos = extras.split(",");
                    resultado = new Mecha(
                        rs.getString("codigo"),
                        rs.getDouble("precio_base"),
                        rs.getInt("stock"),
                        datos[0],                   // Material
                        Integer.parseInt(datos[1])  // Angulo
                    );
                }
                if (resultado != null) resultado.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar: " + e.getMessage());
        }
        return resultado;
    }
}