package com.shopping.app;

import com.shopping.model.Compra;
import com.shopping.model.CompraList;
import java.util.Scanner;

public class Principal {
    public static void main(String[] args) {

        //agregamos un objeto compralist para almacenar las compras

        CompraList compras = new CompraList();

        //el objeto compras es una instancia de la clase CompraList
        //que nos permitirá agregar y mostrar comrpas


        // ahora creamos el objeto Scanner para lee la entrada del usuario
        // El objeto scanner es una instancia de la clase Scanner que nos permitirá leer la entrada del usuario.

        Scanner scanner = new Scanner(System.in);
        // La línea double monto = scanner.nextDouble(); lee la entrada del usuario y la asigna a la variable monto

        //Ahora se crea el bucle de interacción con el usuario

        System.out.println("Cuánto monto deseas asignar a la tarjeta");
        double saldo = scanner.nextDouble();

        while (true) {

            System.out.println("Qué producto quieres comprar?");
            String descripcion = scanner.next();

            System.out.println("¿Cuánto vale el producto?");
            double precio = scanner.nextDouble();

            if (precio > saldo) {
                System.out.println(
                        "No tienes suficiente saldo para realizar esta compra");
            } else {
                Compra compra = new Compra(precio, descripcion);
                compras.agregarCompra(compra);
                saldo -= precio;
                System.out.println("Compra realizada!!:");
            }

            System.out.println(" Deseas seguir comprando? Y/N");
            String respuesta = scanner.next();

            while (!respuesta.equalsIgnoreCase("Y") && !respuesta.equalsIgnoreCase("N")) {
                System.out.println("Respuesta inválida. Por favor, ingresa Y o N.");
                respuesta = scanner.next();
            }

                if (respuesta.equalsIgnoreCase("N")) {
                    break;

                }


            }
            System.out.println("\033[33m**********************\033[0m");
            System.out.println("\033[33mResumen de compras:\033[0m ");
            System.out.println("\033[33m_________________________\033[0m");
            compras.mostrarCompras();
            System.out.println("\033[33m_________________________\033[0m");


        System.out.println("\033[33mSaldo final: $" + saldo + "\033[0m");
        System.out.println("\033[33m¡Gracias por utilizar nuestra tarjeta para sus  compras!\033[0m");


        }


    }






