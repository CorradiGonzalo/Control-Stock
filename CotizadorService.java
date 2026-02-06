//En este cotizador no tocamos el stock... Lo hice aca y no reutilice el cotizador hecho con python pata que este programa tenga su propia base de datos.
public class CotizadorService {
    private HerramientaDAO dao = new HerramientaDAOImpl();

    public double generarCotizacion(String codigo, double cantidadRequerida) throws Exception {
        Herramienta h = dao.buscarPorCodigo(codigo);
        
        if (h == null) {
            throw new Exception("El producto " + codigo + " no existe en la base de datos.");
        }

        
        if (h.getCantidad() < cantidadRequerida) {
            System.out.println("⚠️  ALERTA: Pides " + cantidadRequerida + " pero hay " + 
                               h.getCantidad() + " " + h.getUnidad().getSimbolo());
        } else {
            System.out.println("✅  Stock suficiente (" + h.getCantidad() + " disponibles).");
        }

        double precioUnitario = h.calcularPrecioLista();
        return precioUnitario * cantidadRequerida;
    }
}