import java.util.List;

public interface HerramientaDAO {
    void crearTabla(); //inicializa stock.db 
    void guardar(Herramienta herramienta); //cargar stock inicial

    Herramienta buscarPorCodigo(String codigo); //leer stock
    List<Herramienta> listarTodos(); //listar inventario para la UI
}
