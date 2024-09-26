package com.shopping.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CompraList {
    private List<Compra> compras;

    //Constructor

    public CompraList(){
        this.compras = new ArrayList<>();
    }

    //crear el método para agregar una nueva compra a la lista

    public void agregarCompra(Compra compra){
    this.compras.add(compra);
    }

    // creando el método para mostrar compras


    public void mostrarCompras(){
        Collections.sort(compras);

        //Collections.sort(): Es un método estático de la clase Collections que se utiliza para ordenar una lista de objetos.
        // el metodo get.Monto y getDescripcion obtiene el monto y descripcion de cada compra

        for(Compra compra: compras){
            System.out.println(compra.toString());

        }
    }


}
