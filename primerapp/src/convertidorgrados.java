import java.util.Scanner;
public class convertidorgrados {
    public static  void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Por favor , ingresa una temperatura en grados Celsius");
        double temperaturaCelsius = scanner.nextDouble();
        double temperaturaFarenheit = (temperaturaCelsius * 1.8) +32;
        System.out.println("Latemperatura en Farenheit es: " + temperaturaFarenheit);
    }
}
