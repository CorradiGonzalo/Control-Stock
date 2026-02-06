public class Buje extends Herramienta {
    private String material;
    private double diametroExterior;
    private double diametroInterior;
    private double largo;

    public Buje(String codigo, String descripcion, Unidad unidad, String deposito, 
                double cantidad, double precioBase, 
                String material, double diamExt, double diamInt, double largo) {
        
        super(codigo, descripcion, unidad, deposito, cantidad, precioBase);
        this.material = material;
        this.diametroExterior = diamExt;
        this.diametroInterior = diamInt;
        this.largo = largo;
    }

    @Override
    public double calcularPrecioLista() {
        return this.precioBase; 
    }

    // Getters
    public String getMaterial() { return material; }
    public double getDiametroExterior() { return diametroExterior; }
    public double getDiametroInterior() { return diametroInterior; }
    public double getLargo() { return largo; }
}