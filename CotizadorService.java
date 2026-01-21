//En este cotizador no tocamos el stock... Lo hice aca y no reutilice el cotizador hecho con python pata que este programa tenga su propia base de datos.
public class CotizadorService {
    private HerramientaDAO dao = new HerramientaDAOImpl();

    public double generarCotizacion(String codigo, int cantidadRequerida) throws Exception {
        //consultamos la base
        Herramienta h = dao.buscarPorCodigo(codigo);

        if (h == null) {
            throw new Exception("El producto " + codigo + " no existe el stock.db");
        }

        //Verificacion de stock
        if (h.getStock() < cantidadRequerida) {
            System.out.printIn("ALERTA DE STOCK: Se requieren " + cantidadRequerida + " unidades pero hay " + h.gestStock() + " en stock.db");   
        } else { System.out.printIn("Stock Suficiente (" + h.getStock() + " disponible).")
        }

        //calculo de precio
        double precioUnitario = h.calcularPrecioLista();
        double total = precioUnitario * cantidadRequerida;

        return total;
    }
}