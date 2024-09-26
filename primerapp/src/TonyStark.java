
import java.util.Scanner;

public class TonyStark {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double saldo = 50000;
        int opcion = 0; //variable para almacenar la opción seleccionada

        System.out.println("Bienvenido al banco Avengers señor Stark");


        while (opcion != 4) {
            System.out.println("Seleccione una de las siguientes opciones:");
            System.out.println("1. Consulta de saldo");
            System.out.println("2. Retirar");
            System.out.println("3. Depositar");
            System.out.println("4. Salir");

            opcion = scanner.nextInt(); //leer la opción seleccionada

            switch (opcion) {
                case 1:
                    System.out.println("SU saldo actual es:$" + saldo);

                    break;
                case 2:
                    System.out.println("¿Cuánto desea retirar?");
                    double retiro = scanner.nextDouble();
                    if (retiro > saldo) {
                        System.out.println("Fondos insuficientes.No puede retirar más de lo que tiene");
                    } else {
                        saldo -= retiro;
                        System.out.println(" Has retirado $" + retiro + ". Tu saldo actual es: $" + saldo);
                    }
                    break;
                case 3:
                    System.out.println("Cuánto desea depositar?");
                    double deposito = scanner.nextDouble();

                    if (deposito > 0) {
                        saldo += deposito;
                        System.out.println("Has depositado $" + deposito + ". Tu saldo actual es: $" + saldo);
                    } else {
                        System.out.println("El monto a depositar debe ser mayo que 0.");
                    }


                    break;

                case 4:
                    System.out.println("Gracias por usar el banco Avengers");

                    break;
                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción válida");
            }
        }
    }
}
