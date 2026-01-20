public class Fresa extends Herramienta {
    private int numeroDeDientes;
    private String tipoDeCorte;

    public Fresa(String codigo, double precioBase, int stock, int numeroDeDientes, String tipoDeCorte) {
        super(codigo, precioBase, stock);
        this.numeroDeDientes;
        this.tipoDeCorte;
    }

    @Override
    public double calcularPrecioLista() {
        return this.precioBase *  1.10;  //10% mas sobre la base
    }

    public int getNumeroDeDientes() { return numeroDeDientes; }
    public String getTipoDeCorte;() { return tipoDeCorte; }
}