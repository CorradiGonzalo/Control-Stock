import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                resultado = fromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar: " + e.getMessage());
        }
        return resultado;
    }

    @Override
    public List<Herramienta> listarTodos() {
        String sql = "SELECT * FROM inventario ORDER BY codigo";
        List<Herramienta> lista = new ArrayList<>();

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Herramienta h = fromResultSet(rs);
                if (h != null) lista.add(h);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar: " + e.getMessage());
        }
        return lista;
    }

    private Herramienta fromResultSet(ResultSet rs) throws SQLException {
        String tipo = rs.getString("discriminador");
        String extras = rs.getString("datos_extra");
        Herramienta h = null;

        if ("FRESA".equals(tipo)) {
            String[] datos = extras.split(",");
            h = new Fresa(
                rs.getString("codigo"),
                rs.getDouble("precio_base"),
                rs.getInt("stock"),
                Integer.parseInt(datos[0]),
                datos[1]
            );
        } else if ("MECHA".equals(tipo)) {
            String[] datos = extras.split(",");
            h = new Mecha(
                rs.getString("codigo"),
                rs.getDouble("precio_base"),
                rs.getInt("stock"),
                datos[0],
                Integer.parseInt(datos[1])
            );
        }
        if (h != null) h.setId(rs.getInt("id"));
        return h;
    }
}