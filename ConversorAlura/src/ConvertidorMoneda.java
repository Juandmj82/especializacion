import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConvertidorMoneda {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<String> historial = new ArrayList<>();
    private static final DecimalFormat df = new DecimalFormat("#,##0.00");
    private static final List<String> monedasDisponibles = Arrays.asList("USD", "EUR", "ARS", "BRL", "JPY", "COP", "GBP", "CAD");

    public static void main(String[] args) {
        System.out.println("Bienvenido al convertidor de moneda");

        while (true) {
            mostrarMenu();
            int opcion = obtenerOpcion();

            switch (opcion) {
                case 1:
                    procesarConversion();
                    break;
                case 2:
                    mostrarHistorial();
                    break;
                case 3:
                    System.out.println("Gracias por usar el convertidor de moneda. ¡Hasta luego!");
                    return;
                default:
                    System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
        }
    }

    private static void mostrarMenu() {
        String linea = "═══════════════════════════════════════════════════════════════════";
        String titulo = "Convertidor de Monedas";

        System.out.println(ANSI_CYAN + linea + ANSI_RESET);
        System.out.println(ANSI_YELLOW + centrarTexto(titulo, linea.length()) + ANSI_RESET);
        System.out.println(ANSI_CYAN + linea + ANSI_RESET);

        System.out.println(ANSI_GREEN + "Pares disponibles: " + ANSI_RESET + String.join(", ", monedasDisponibles));
        System.out.println();
        System.out.println(ANSI_PURPLE + "1. " + ANSI_RESET + "Convertir");
        System.out.println(ANSI_PURPLE + "2. " + ANSI_RESET + "Ver historial");
        System.out.println(ANSI_PURPLE + "3. " + ANSI_RESET + "Salir");
        System.out.println();
        System.out.println(ANSI_CYAN + linea + ANSI_RESET);
    }

    private static String centrarTexto(String texto, int ancho) {
        int espacios = (ancho - texto.length()) / 2;
        return " ".repeat(espacios) + texto;
    }

    private static int obtenerOpcion() {
        System.out.print("Seleccione una opción: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Por favor, ingrese un número válido.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static void procesarConversion() {
        scanner.nextLine(); // Limpiar el buffer
        System.out.print("Ingrese la moneda de origen: ");
        String monedaOrigen = scanner.nextLine().toUpperCase();
        System.out.print("Ingrese la moneda de destino: ");
        String monedaDestino = scanner.nextLine().toUpperCase();

        if (!monedasDisponibles.contains(monedaOrigen) || !monedasDisponibles.contains(monedaDestino)) {
            System.out.println(ANSI_RED + "Una o ambas monedas no están disponibles. Por favor, intente de nuevo." + ANSI_RESET);
            return;
        }

        System.out.print("Ingrese la cantidad a convertir: ");
        double cantidad = scanner.nextDouble();
        scanner.nextLine(); // Limpiar el buffer

        try {
            double resultado = convertir(cantidad, monedaOrigen, monedaDestino);
            mostrarResultado(cantidad, monedaOrigen, resultado, monedaDestino);
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Error al realizar la conversión: " + e.getMessage() + ANSI_RESET);
        }
    }

    private static void mostrarResultado(double cantidad, String monedaOrigen, double resultado, String monedaDestino) {
        String linea = "─────────────────────────────────────────────────────────────────────";
        System.out.println("\n" + ANSI_CYAN + linea + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "                     Resultado de la Conversión" + ANSI_RESET);
        System.out.println(ANSI_CYAN + linea + ANSI_RESET);
        System.out.printf(ANSI_GREEN + " %-15s %20s %-10s\n" + ANSI_RESET, "Cantidad Original:", df.format(cantidad), monedaOrigen);
        System.out.printf(ANSI_GREEN + " %-15s %20s %-10s\n" + ANSI_RESET, "Cantidad Convertida:", df.format(resultado), monedaDestino);
        System.out.println(ANSI_CYAN + linea + ANSI_RESET);

        // Agregar al historial
        String mensajeHistorial = String.format("%.2f %s = %.2f %s", cantidad, monedaOrigen, resultado, monedaDestino);
        historial.add(mensajeHistorial);
    }

    private static double convertir(double cantidad, String monedaOrigen, String monedaDestino) throws IOException, InterruptedException {
        double tasaOrigen = ManejadorApi.obtenerTasaDeCambio(monedaOrigen);
        double tasaDestino = ManejadorApi.obtenerTasaDeCambio(monedaDestino);
        return cantidad * (tasaDestino / tasaOrigen);
    }

    private static void mostrarHistorial() {
        String linea = "─────────────────────────────────────────────────────────────────────";
        System.out.println("\n" + ANSI_CYAN + linea + ANSI_RESET);
        System.out.println(ANSI_YELLOW + centrarTexto("Historial de Conversiones", linea.length()) + ANSI_RESET);
        System.out.println(ANSI_CYAN + linea + ANSI_RESET);

        if (historial.isEmpty()) {
            System.out.println(ANSI_RED + centrarTexto("Aún no se han realizado conversiones.", linea.length()) + ANSI_RESET);
        } else {
            System.out.printf(ANSI_GREEN + " %-5s %-20s %-20s %-15s\n" + ANSI_RESET, "No.", "Cantidad Original", "Cantidad Convertida", "Fecha/Hora");
            System.out.println(ANSI_CYAN + linea + ANSI_RESET);

            for (int i = 0; i < historial.size(); i++) {
                String[] partes = historial.get(i).split(" = ");
                String fechaHora = obtenerFechaHoraActual();
                System.out.printf(ANSI_PURPLE + " %-5d" + ANSI_RESET + " %-20s %-20s %-15s\n",
                        i + 1, partes[0], partes[1], fechaHora);
            }
        }
        System.out.println(ANSI_CYAN + linea + ANSI_RESET);
    }

    private static String obtenerFechaHoraActual() {
        return java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"));
    }

    private static String obtenerNombreMoneda(String codigo) {
        switch (codigo) {
            case "USD": return "Dólares estadounidenses";
            case "EUR": return "Euros";
            case "ARS": return "Pesos argentinos";
            case "BRL": return "Reales brasileños";
            case "JPY": return "Yenes japoneses";
            case "COP": return "Pesos colombianos";
            case "GBP": return "Libras esterlinas";
            case "CAD": return "Dólares canadienses";
            default: return codigo;
        }
    }

    // Constantes para colores ANSI
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_RED = "\u001B[31m";
}
