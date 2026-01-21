public abstract class Herramienta {
    protected int id;
    protected String codigo;
    protected double precioBase;
    protected int stock;

    public Herramienta(String codigo, double precioBase, int stock) {
        this.codigo = codigo;
        this.precioBase = precioBase;
        this.stock = stock;
    }

    //METODO ABSTRACTO PARA QUE LAS HIJAS DEFINAN
    public abstract double calcularPrecioLista();

    //Getters y Setters
    public String getCodigo() { return codigo; }
    public int getStock() { return stock; }
    public double getPrecioBase() { return precioBase; }
    public void setId(int id) { this.id = id; }
    
    @Override
    public String toString(){
        return "Cod: " + codigo + " | Stock: " + stock + " | Base: $" + precioBase;
    }
}