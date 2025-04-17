package com.unse.proyecto.ubicua.principal.modelo;

public class Sentencia {

    private int tipo;
    private String movimiento, url;
    private int img;
    private int repeat;

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public Sentencia(int tipo) {
        this.tipo = tipo;
    }

    public Sentencia(int tipo, String movimiento, int img) {
        this.tipo = tipo;
        this.movimiento = movimiento;
        this.img = img;
        this.repeat = 0;
        this.url = "";
    }

    public Sentencia(int tipo, String movimiento, int img, int repeat) {
        this.tipo = tipo;
        this.movimiento = movimiento;
        this.img = img;
        this.repeat = repeat;
        this.url = "";
    }

    public Sentencia(int tipo, String movimiento, String img) {
        this.tipo = tipo;
        this.movimiento = movimiento;
        this.url = img;
        this.repeat = 0;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
