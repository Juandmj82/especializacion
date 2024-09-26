package com.shopping.model;

public class Compra implements Comparable<Compra>{
    private double monto;
    private String descripcion;

    //constructor

    public Compra(double monto, String descripcion) {

        this.monto = monto;
        this.descripcion = descripcion;
    }

    public double getMonto() {
        return monto;
    }



    public String getDescripcion() {
        return descripcion;
    }


    @Override
    public int compareTo(Compra otraCompra) {
        return Double.valueOf(this.monto).compareTo(Double.valueOf(otraCompra.getMonto()));
    }
    @Override
    public String toString() {
        return "Compra:'" + descripcion + "', Monto= $:"+ String.format("%10.2f", monto);
    }

}
