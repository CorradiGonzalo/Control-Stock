public class Mecha extends Herramienta {
    private String material;
    private int anguloPunta;

    public Mecha(String codigo, String descripcion, Unidad unidad, String deposito, 
                 double cantidad, double precioBase, String material, int angulo) {
        super(codigo, descripcion, unidad, deposito, cantidad, precioBase);
        this.material = material;
        this.anguloPunta = angulo;
    }

    @Override
    public double calcularPrecioLista() {
        return this.precioBase * 1.05; 
    }
    public String getMaterial() { return material; }
    public int getAnguloPunta() { return anguloPunta; }
}