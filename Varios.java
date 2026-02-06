public class Varios extends Herramienta {
    private String tipoEspecifico; // Ej: "Inserto Triangular", "Placa", "Tornillo"
    private String especificaciones; // Ej: "TNMG 160408", "40x40mm"

    public Varios(String codigo, String descripcion, Unidad unidad, String deposito, 
                  double cantidad, double precioBase, 
                  String tipoEspecifico, String especificaciones) {
        
        super(codigo, descripcion, unidad, deposito, cantidad, precioBase);
        this.tipoEspecifico = tipoEspecifico;
        this.especificaciones = especificaciones;
    }

    @Override
    public double calcularPrecioLista() {
        return this.precioBase;
    }

    public String getTipoEspecifico() { return tipoEspecifico; }
    public String getEspecificaciones() { return especificaciones; }
}