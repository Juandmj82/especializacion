import java.util.Scanner;

public class Lectura {


public static void main(String[] args) {
    Scanner teclado = new Scanner(System.in);
    System.out.println("Escribe el nombre de tu película favorita");
    String película = teclado.nextLine();
    System.out.println("ahora escribe la fecha de lanzamiento");
    int fechaDeLanzamiento = teclado.nextInt();
    System.out.println("Por último dinos qué calificación le das a esta película");
    double nota = teclado.nextDouble();

    System.out.println(película);
    System.out.println(fechaDeLanzamiento);
    System.out.println(nota);
}

}