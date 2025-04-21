package com.unse.proyecto.ubicua.principal.modelo;

public class OAResult {
    private int totalRespuestas;
    private int totalAciertos;
    private int totalErrores;

    public static OAResult EMPTY = new OAResult(0,0,0);

    public OAResult(int totalRespuestas, int totalAciertos, int totalErrores) {
        this.totalRespuestas = totalRespuestas;
        this.totalAciertos = totalAciertos;
        this.totalErrores = totalErrores;
    }

    public int getTotalRespuestas() {
        return totalRespuestas;
    }

    public int getTotalAciertos() {
        return totalAciertos;
    }

    public int getTotalErrores() {
        return totalErrores;
    }
}

