public class Mecha extends Herramienta {
    private String material;
    private int anguloPunta;

    public Mecha(String codigo, double precioBase, int stock, String material, int anguloPunta) {
        super(codigo, precioBase, stock);
        this.material = material;      
        this.anguloPunta = anguloPunta; 
    }

    @Override
    public double calcularPrecioLista() {
        return this.precioBase * 1.05; // +5%
    }

    public String getMaterial() { return material; }
    public int getAnguloPunta() { return anguloPunta; }
}
