public class Main {
    public static void main(String[] args) { 
        System.out.println("--- INICIANDO SISTEMA ---");

        // Simulamos datos
        HerramientaDAO dao = new HerramientaDAOImpl();
        dao.crearTabla(); // Creamos si no existe.

        // Prueba
        try {
             Herramienta f1 = new Fresa("FRS-001", 100.0, 10, 4, "Desbaste");
             dao.guardar(f1);
        } catch (Exception e) {
             System.out.println("El producto ya existía, seguimos...");
        }

        // Cotizamos
        CotizadorService cotizador = new CotizadorService();

        try {
            System.out.println("\n--- NUEVA COTIZACIÓN ---");
            String producto = "FRS-001";
            int cantidad = 5;

            double precioFinal = cotizador.generarCotizacion(producto, cantidad);

            System.out.println("Precio Final Cotizado: $" + precioFinal);
            System.out.println("Nota: El inventario en stock.db no ha sido modificado.");

            // Verificación
            Herramienta control = dao.buscarPorCodigo("FRS-001");
            
            System.out.println("\n Auditoria: El stock fisico sigue siendo: " + control.getStock());   
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
} 

