package com.desafios.biblioteca.service;

import com.desafios.biblioteca.exception.BibliotecaException;
import com.desafios.biblioteca.model.Libro;
import com.desafios.biblioteca.repository.LibroRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * Servicio para gestionar operaciones relacionadas con libros utilizando la API de Google Books
 * y el almacenamiento local en base de datos.
 */
@Service
public class GoogleBooksService {
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleBooksService.class);
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("GOOGLE_BOOKS_API_KEY");
    private static final String URL_BASE = "https://www.googleapis.com/books/v1/volumes";

    @Autowired
    private LibroRepository libroRepository;

    /**
     * Busca un libro por su título utilizando la API de Google Books.
     * 
     * @param titulo El título del libro a buscar
     * @return Libro encontrado o null si no se encuentra
     * @throws BibliotecaException Si ocurre algún error durante la búsqueda
     */
    public Libro buscarLibroPorTitulo(String titulo) throws BibliotecaException {
        try {
            validarEntrada(titulo);
            
            String tituloLimpiado = limpiarTitulo(titulo);
            String tituloCodificado = URLEncoder.encode("intitle:" + tituloLimpiado, StandardCharsets.UTF_8.toString());
            String urlString = URL_BASE + "?q=" + tituloCodificado + "&key=" + API_KEY;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                return procesarRespuestaAPI(response.toString());
            }
        } catch (Exception e) {
            logger.error("Error al buscar libro con título: {}", titulo, e);
            throw new BibliotecaException("Error al buscar el libro", e);
        }
    }

    /**
     * Elimina un libro por su título de la base de datos.
     * 
     * @param titulo El título del libro a eliminar
     * @return true si se eliminó al menos un libro, false si no se encontró ninguno
     * @throws BibliotecaException Si ocurre algún error durante la eliminación
     */
    @Transactional
    public boolean eliminarLibroPorTitulo(String titulo) throws BibliotecaException {
        try {
            validarEntrada(titulo);
            
            // Usamos findByTituloContainingIgnoreCase en lugar de findByTituloIgnoreCase
            List<Libro> libros = libroRepository.findByTituloContainingIgnoreCase(titulo);
            
            if (!libros.isEmpty()) {
                // Mostrar los libros encontrados antes de eliminar
                System.out.println("Se encontraron los siguientes libros:");
                for (Libro libro : libros) {
                    System.out.println("- " + libro.getTitulo());
                }
                
                // Pedir confirmación
                System.out.println("¿Desea eliminar estos libros? (S/N)");
                try (Scanner scanner = new Scanner(System.in)) { // Usar try-with-resources para cerrar el scanner
                    String respuesta = scanner.nextLine(); // Crear el scanner aquí
                    
                    if (respuesta.equalsIgnoreCase("S")) {
                        for (Libro libro : libros) {
                            libroRepository.delete(libro);
                            System.out.println("Libro '" + libro.getTitulo() + "' eliminado de la base de datos.");
                        }
                        return true;
                    } else {
                        System.out.println("Operación cancelada.");
                        return false;
                    }
                }
            } else {
                System.out.println("No se encontraron libros con el título: " + titulo);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error al eliminar libro con título: {}", titulo, e);
            throw new BibliotecaException("Error al eliminar el libro", e);
        }
    }

    /**
     * Busca libros por título en la base de datos.
     * 
     * @param titulo El título de los libros a buscar
     * @return Lista de libros encontrados
     * @throws BibliotecaException Si ocurre algún error durante la búsqueda
     */
    @Transactional(readOnly = true)
    public List<Libro> buscarLibrosPorTitulo(String titulo) throws BibliotecaException {
        try {
            validarEntrada(titulo);
            return libroRepository.findByTituloContainingIgnoreCase(titulo);
        } catch (Exception e) {
            logger.error("Error al buscar libros por título: {}", titulo, e);
            throw new BibliotecaException("Error al buscar libros por título", e);
        }
    }

    /**
     * Busca libros por género en la base de datos.
     * 
     * @param genero El género de los libros a buscar
     * @return Lista de libros encontrados
     * @throws BibliotecaException Si ocurre algún error durante la búsqueda
     */
    @Transactional(readOnly = true)
    public List<Libro> buscarLibrosPorGenero(String genero) throws BibliotecaException {
        try {
            validarEntrada(genero);
            
            String generoNormalizado = normalizarEntrada(genero);
            String generoTraducido = traducirGenero(generoNormalizado);
            
            List<Libro> libros = libroRepository.findByGeneroIgnoreCase(generoTraducido);
            
            if (libros.isEmpty()) {
                logger.info("No se encontraron libros para el género: {}", genero);
            } else {
                logger.info("Se encontraron {} libros para el género: {}", libros.size(), genero);
            }
            
            return libros;
        } catch (Exception e) {
            logger.error("Error al buscar libros por género: {}", genero, e);
            throw new BibliotecaException("Error al buscar libros por género", e);
        }
    }

    /**
     * Procesa la respuesta JSON de la API de Google Books.
     */
    private Libro procesarRespuestaAPI(String jsonResponse) {
        JSONObject response = new JSONObject(jsonResponse);
        if (response.getInt("totalItems") > 0) {
            JSONObject libroJson = response.getJSONArray("items")
                                         .getJSONObject(0)
                                         .getJSONObject("volumeInfo");
            
            return construirLibroDesdeJSON(libroJson);
        }
        return null;
    }

    /**
     * Construye un objeto Libro a partir de la información JSON.
     */
    private Libro construirLibroDesdeJSON(JSONObject libroJson) {
        String titulo = libroJson.getString("title");
        String autor = libroJson.optJSONArray("authors") != null ? 
                      libroJson.getJSONArray("authors").getString(0) : 
                      "Autor no disponible";
        String editorial = libroJson.optString("publisher", "Editorial no disponible");
        String isbn = extraerISBN(libroJson);
        String genero = libroJson.optJSONArray("categories") != null ? 
                       libroJson.getJSONArray("categories").getString(0) : 
                       "";
        int paginas = libroJson.optInt("pageCount", 0);
        double evaluacion = libroJson.optDouble("averageRating", 0.0);
        String fechaPublicacion = libroJson.optString("publishedDate", "Fecha no disponible");
        String caratulaUrl = libroJson.optJSONObject("imageLinks") != null ? 
                            libroJson.getJSONObject("imageLinks").getString("thumbnail") : 
                            "";

        return new Libro(titulo, autor, genero, paginas, evaluacion, isbn, 
                        editorial, fechaPublicacion, caratulaUrl);
    }

    /**
     * Extrae el ISBN-13 de la información JSON del libro.
     */
    private String extraerISBN(JSONObject libroJson) {
        if (libroJson.has("industryIdentifiers")) {
            JSONArray identifiers = libroJson.getJSONArray("industryIdentifiers");
            for (int i = 0; i < identifiers.length(); i++) {
                JSONObject identifier = identifiers.getJSONObject(i);
                if ("ISBN_13".equals(identifier.getString("type"))) {
                    return identifier.getString("identifier");
                }
            }
        }
        return "";
    }

    /**
     * Normaliza una cadena de entrada eliminando espacios extra y convirtiendo a minúsculas.
     */
    private String normalizarEntrada(String entrada) {
        return entrada.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    /**
     * Limpia un título eliminando espacios extra.
     */
    private String limpiarTitulo(String titulo) {
        return titulo.trim().replaceAll("\\s+", " ");
    }

    /**
     * Traduce géneros del español al inglés.
     */
    private String traducirGenero(String genero) {
        return switch (genero.toLowerCase()) {
            case "ficcion" -> "fiction";
            case "no ficción" -> "non-fiction";
            case "fantasía" -> "fantasy";
            case "ciencia ficción" -> "science fiction";
            default -> genero;
        };
    }

    /**
     * Valida que la entrada no sea null ni esté vacía.
     */
    private void validarEntrada(String entrada) throws BibliotecaException {
        if (!StringUtils.hasText(entrada)) {
            throw new BibliotecaException("La entrada no puede estar vacía");
        }
    }
}
