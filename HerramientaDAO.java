public interface HerramientaDAO {
    void crearTabla(); //inicializa stock.db 
    void guardar(Herramienta herramienta); //cargar stock inicial

    Herramienta buscarPorCodigo(String codigo); //leer stock
}
