public class Barra extends Herramienta {
    private String material;      // "Carburo K10"
    private double largo;         // 330mm, 150mm
    private double diametro;      // ø exterior
    private boolean esPerforada;  // true/false
    private String tipoPerforacion; // "MACIZA", "RECTA", "HELICOIDAL"
    private double diametroAgujeros; // ø de los agujeritos
    private double separacionAgujeros; // Paso o separación

    public Barra(String codigo, String descripcion, Unidad unidad, String deposito, 
                 double cantidad, double precioBase, 
                 String material, double largo, double diametro, 
                 boolean esPerforada, String tipoPerforacion, 
                 double diametroAgujeros, double separacionAgujeros) {
        
        super(codigo, descripcion, unidad, deposito, cantidad, precioBase);
        this.material = material;
        this.largo = largo;
        this.diametro = diametro;
        this.esPerforada = esPerforada;
        this.tipoPerforacion = tipoPerforacion;
        this.diametroAgujeros = diametroAgujeros;
        this.separacionAgujeros = separacionAgujeros;
    }

    @Override
    public double calcularPrecioLista() {
        // Las barras perforadas suelen ser más caras (+20% ejemplo)
        if (esPerforada) return this.precioBase * 1.20;
        return this.precioBase;
    }

    // Getters
    public String getMaterial() { return material; }
    public double getLargo() { return largo; }
    public double getDiametro() { return diametro; }
    public boolean isEsPerforada() { return esPerforada; }
    public String getTipoPerforacion() { return tipoPerforacion; }
    public double getDiametroAgujeros() { return diametroAgujeros; }
    public double getSeparacionAgujeros() { return separacionAgujeros; }
}