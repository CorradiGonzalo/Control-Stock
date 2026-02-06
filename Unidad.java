public enum Unidad {
    //CATEGORIA DE CONTEO
    UNIDAD(1.0, "ud"),    
    CAJA_10(10.0, "caja_10"),

    GRAMO(1.0, "g"),
    KILO(1000.0, "kg"),
    TONELADA(1000000.0, "tn"),

    METRO(1.0, "m"),
    CENTIMETRO(0.01, "cm");

    private final double factorConversion;
    private final String simbolo;

    Unidad(double factorConversion, String simbolo) {
        this.factorConversion = factorConversion;
        this.simbolo = simbolo;
    }

    public double aUnidadBase(double cantidadInput) {
        return cantidadInput * this.factorConversion;
    }

    public String getSimbolo() {
        return simbolo;
    }
}
