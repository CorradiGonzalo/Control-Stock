public abstract class Herramienta {
    protected int id;
    protected String codigo;
    protected String descripcion;
    protected Unidad unidad;      
    protected String deposito;
    protected double cantidad;    
    protected double precioBase;

    public Herramienta(String codigo, String descripcion, Unidad unidad, String deposito, double cantidadInput, double precioBase) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.unidad = unidad;
        this.deposito = deposito;
        this.precioBase = precioBase;
        
        //Convertimos automáticamente a la unidad base.
        // Si entran 1.5 Kilos, se guarda 1500.0 (Gramos)
        this.cantidad = unidad.aUnidadBase(cantidadInput);
    }

    public abstract double calcularPrecioLista();

    // GETTERS
    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public Unidad getUnidad() { return unidad; } 
    public String getDeposito() { return deposito; }
    public double getCantidad() { return cantidad; }
    public double getPrecioBase() { return precioBase; }
    public void setId(int id) { this.id = id; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Stock Base: %.3f %s | Ubic: %s", 
               codigo, descripcion, cantidad, unidad.getSimbolo(), deposito);
    }
}