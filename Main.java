import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HerramientaDAO dao = new HerramientaDAOImpl();
            dao.crearTabla(); // Se asegura que exista la tabla PRO
            
            ControlStockUI ui = new ControlStockUI();
            ui.setVisible(true);
        });
    }
}
