import java.util.List;

public interface HerramientaDAO {
    void crearTabla();
    void guardar(Herramienta h);
    Herramienta buscarPorCodigo(String codigo);
    List<Herramienta> listarTodos();
    
    //  AGREGAR O QUITAR
    void actualizarStock(String codigo, double nuevaCantidad);
    void eliminar(String codigo);
}