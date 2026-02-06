public class Polvo extends Herramienta {
    private String material; // Ej: "Carburo de Tungsteno", "Cobalto"

    public Polvo(String codigo, String descripcion, Unidad unidad, String deposito, 
                 double cantidad, double precioBase, String material) {
        super(codigo, descripcion, unidad, deposito, cantidad, precioBase);
        this.material = material;
    }

    @Override
    public double calcularPrecioLista() {
        return this.precioBase; // El precio del polvo suele ser directo por peso
    }

    public String getMaterial() { return material; }
}