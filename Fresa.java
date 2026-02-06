public class Fresa extends Herramienta {
    private int numeroDeDientes;
    private String tipoDeCorte;

    public Fresa(String codigo, String descripcion, Unidad unidad, String deposito, 
                 double cantidad, double precioBase, int dientes, String corte) {
        super(codigo, descripcion, unidad, deposito, cantidad, precioBase);
        this.numeroDeDientes = dientes;
        this.tipoDeCorte = corte;
    }

    @Override
    public double calcularPrecioLista() {
        return this.precioBase * 1.10; 
    }
    public int getNumeroDeDientes() { return numeroDeDientes; }
    public String getTipoDeCorte() { return tipoDeCorte; }
}