import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        HerramientaDAO dao = new HerramientaDAOImpl();
        dao.crearTabla();

        // Opcional: asegurar fresa de ejemplo si no existe
        if (dao.buscarPorCodigo("FRS-001") == null) {
            dao.guardar(new Fresa("FRS-001", 100.0, 10, 4, "Desbaste"));
        }

        SwingUtilities.invokeLater(() -> {
            ControlStockUI ui = new ControlStockUI();
            ui.setVisible(true);
        });
    }
} 

