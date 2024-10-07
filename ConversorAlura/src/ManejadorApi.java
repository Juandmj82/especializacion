import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.HashMap;

public class ManejadorApi {
    private static final String API_KEY = "da60150152a8e15f94eb9934";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static Map<String, Double> obtenerTasasDeCambio() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return parsearRespuestaJSON(response.body());
    }

    private static Map<String, Double> parsearRespuestaJSON(String jsonString) {
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");
        Map<String, Double> tasas = new HashMap<>();
        for (Map.Entry<String, com.google.gson.JsonElement> entry : conversionRates.entrySet()) {
            tasas.put(entry.getKey(), entry.getValue().getAsDouble());
        }
        return tasas;
    }

    public static double obtenerTasaDeCambio(String monedaDestino) throws IOException, InterruptedException {
        Map<String, Double> tasas = obtenerTasasDeCambio();
        return tasas.getOrDefault(monedaDestino, 1.0);
    }
}