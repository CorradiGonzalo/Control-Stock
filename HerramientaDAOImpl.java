import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HerramientaDAOImpl implements HerramientaDAO {

    @Override
    public void crearTabla() {
        // La estructura es la misma, solo cambiamos lo que guardamos dentro de 'datos_extra'
        String sql = "CREATE TABLE IF NOT EXISTS inventario (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "codigo TEXT UNIQUE, " +
                     "descripcion TEXT, " +
                     "unidad TEXT, " +
                     "deposito TEXT, " +
                     "cantidad REAL, " +
                     "precio_base REAL, " +
                     "tipo TEXT, " + 
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
        String sql = "INSERT INTO inventario(codigo, descripcion, unidad, deposito, cantidad, precio_base, tipo, datos_extra) VALUES(?,?,?,?,?,?,?,?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, h.getCodigo());
            pstmt.setString(2, h.getDescripcion());
            pstmt.setString(3, h.getUnidad().name());
            pstmt.setString(4, h.getDeposito());
            pstmt.setDouble(5, h.getCantidad());
            pstmt.setDouble(6, h.getPrecioBase());

            if (h instanceof Fresa) {
                Fresa f = (Fresa) h;
                pstmt.setString(7, "FRESA");
                pstmt.setString(8, f.getNumeroDeDientes() + "," + f.getTipoDeCorte());
            } else if (h instanceof Mecha) {
                Mecha m = (Mecha) h;
                pstmt.setString(7, "MECHA");
                pstmt.setString(8, m.getMaterial() + "," + m.getAnguloPunta());
            } else if (h instanceof Polvo) {
                Polvo p = (Polvo) h;
                pstmt.setString(7, "POLVO");
                pstmt.setString(8, p.getMaterial());
            } else if (h instanceof Barra) {
                Barra b = (Barra) h;
                pstmt.setString(7, "BARRA");
                String extra = b.getMaterial() + "," + b.getLargo() + "," + b.getDiametro() + "," +
                               b.isEsPerforada() + "," + b.getTipoPerforacion() + "," + 
                               b.getDiametroAgujeros() + "," + b.getSeparacionAgujeros();
                pstmt.setString(8, extra);
            } else if (h instanceof Buje) {
                Buje b = (Buje) h;
                pstmt.setString(7, "BUJE");
                // Guardamos: Material, OD, ID, Largo
                pstmt.setString(8, b.getMaterial() + "," + b.getDiametroExterior() + "," + 
                                   b.getDiametroInterior() + "," + b.getLargo());
            } else if (h instanceof Varios) {
                Varios v = (Varios) h;
                pstmt.setString(7, "VARIOS");
                // Guardamos: TipoEspecifico, Especificaciones
                pstmt.setString(8, v.getTipoEspecifico() + "," + v.getEspecificaciones());
            }

            pstmt.executeUpdate();
            System.out.println("✅ Guardado: " + h.getCodigo());

        } catch (SQLException e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
    }

    @Override
    public Herramienta buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM inventario WHERE codigo = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) { 
            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return fromResultSet(rs);
        } catch (SQLException e) { }
        return null;
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
        } catch (SQLException e) { }
        return lista;
    }

    @Override
    public void actualizarStock(String codigo, double nuevaCantidad) {
        String sql = "UPDATE inventario SET cantidad = ? WHERE codigo = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, nuevaCantidad);
            pstmt.setString(2, codigo);
            pstmt.executeUpdate();
            System.out.println("Stock actualizado para: " + codigo);
            
        } catch (SQLException e) {
            System.out.println("Error al actualizar stock: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(String codigo) {
        String sql = "DELETE FROM inventario WHERE codigo = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigo);
            pstmt.executeUpdate();
            System.out.println("Eliminado: " + codigo);
            
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }

    private Herramienta fromResultSet(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo");
        String extras = rs.getString("datos_extra");
        
        String codigo = rs.getString("codigo");
        String desc = rs.getString("descripcion");
        String depo = rs.getString("deposito");
        double cant = rs.getDouble("cantidad");
        double prec = rs.getDouble("precio_base");
        
        Unidad uni;
        try { uni = Unidad.valueOf(rs.getString("unidad")); } 
        catch (Exception e) { uni = Unidad.UNIDAD; }

        double cantidadOriginal = cant / uni.aUnidadBase(1.0);
        String[] datos = extras.split(",");

        switch (tipo) {
            case "FRESA":
                return new Fresa(codigo, desc, uni, depo, cantidadOriginal, prec, Integer.parseInt(datos[0]), datos[1]);
            case "MECHA":
                return new Mecha(codigo, desc, uni, depo, cantidadOriginal, prec, datos[0], Integer.parseInt(datos[1]));
            case "POLVO":
                return new Polvo(codigo, desc, uni, depo, cantidadOriginal, prec, datos[0]);
            case "BARRA":
                return new Barra(codigo, desc, uni, depo, cantidadOriginal, prec,
                    datos[0], Double.parseDouble(datos[1]), Double.parseDouble(datos[2]), 
                    Boolean.parseBoolean(datos[3]), datos[4], Double.parseDouble(datos[5]), Double.parseDouble(datos[6]));
            case "BUJE":
                return new Buje(codigo, desc, uni, depo, cantidadOriginal, prec,
                    datos[0], // Material
                    Double.parseDouble(datos[1]), // OD
                    Double.parseDouble(datos[2]), // ID
                    Double.parseDouble(datos[3])); // Largo
            case "VARIOS":
                return new Varios(codigo, desc, uni, depo, cantidadOriginal, prec,
                    datos[0], // Tipo Especifico
                    datos[1]); // Specs
            default:
                return null;
        }
    }
}