package com.desafios.biblioteca;

import com.desafios.biblioteca.model.Libro;
import com.desafios.biblioteca.repository.LibroRepository;
import com.desafios.biblioteca.service.GoogleBooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class BibliotecaMenu implements CommandLineRunner {

    @Autowired
    private GoogleBooksService googleBooksService;

    @Autowired
    private LibroRepository libroRepository;

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("Menú:");
            System.out.println("1. Buscar libro por título");
            System.out.println("2. Buscar libro por autor");
            System.out.println("3. Buscar libro por género");
            System.out.println("4. Buscar libro por ISBN");
            System.out.println("5. Eliminar libro por título");
            System.out.println("6. Salir");
            System.out.print("Elige una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            switch (opcion) {
                case 1:
                    System.out.print("Introduce el título del libro: ");
                    String titulo = scanner.nextLine();
                    // Llamar al método buscarYGuardarLibroPorTitulo
                    buscarYGuardarLibroPorTitulo(titulo);
                    break;
                case 2:
                    System.out.print("Introduce el autor del libro: ");
                    String autor = scanner.nextLine();
                    buscarYGuardarLibroPorAutor(autor); // Asegúrate de que se llame a este método
                    break;
                case 3:
                    System.out.print("Introduce el género del libro: ");
                    String genero = scanner.nextLine();
                    buscarYGuardarLibroPorGenero(genero); // Asegúrate de que se llame a este método
                    break;
                case 4:
                    System.out.print("Introduce el ISBN del libro: ");
                    String isbn = scanner.nextLine();
                    buscarYGuardarLibroPorIsbn(isbn);
                    break;
                case 5:
                    System.out.print("Introduce el título del libro a eliminar: ");
                    String tituloEliminar = scanner.nextLine().trim(); // Normaliza el título
                    eliminarLibroPorTitulo(tituloEliminar); // Llama al método para eliminar
                    break;
                case 6:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida. Intenta de nuevo.");
            }
        } while (opcion != 6);
        scanner.close();
    }

    private void buscarYGuardarLibroPorTitulo(String titulo) {
        try {
            Libro libro = googleBooksService.buscarLibroPorTitulo(titulo);
            if (libro != null) {
                System.out.println("Libro encontrado: " + libro.getTitulo());
                libroRepository.save(libro); // Guardar el libro en la base de datos
                System.out.println("Libro guardado en la base de datos.");
            } else {
                System.out.println("No se encontró el libro.");
            }
        } catch (Exception e) {
            System.out.println("Error al buscar el libro: " + e.getMessage());
        }
    }

    private void buscarYGuardarLibroPorAutor(String autor) {
        String autorNormalizado = autor.trim(); // Eliminar espacios en blanco
        List<Libro> libros = libroRepository.findByAutorContainingIgnoreCase(autorNormalizado); // Usar el método que busca coincidencias
        if (!libros.isEmpty()) {
            for (Libro libro : libros) {
                System.out.println("Libro encontrado: " + libro.getTitulo());
            }
        } else {
            System.out.println("No se encontraron libros por el autor: " + autor);
        }
    }

    private void buscarYGuardarLibroPorGenero(String genero) {
        String generoIngles = traducirGenero(genero); // Llamar al método para traducir el género
        List<Libro> libros = libroRepository.findByGeneroIgnoreCase(generoIngles); // Usar el género traducido
        if (!libros.isEmpty()) {
            for (Libro libro : libros) {
                System.out.println("Libro encontrado: " + libro.getTitulo());
            }
        } else {
            System.out.println("No se encontraron libros del género: " + genero);
        }
    }

    private void buscarYGuardarLibroPorIsbn(String isbn) {
        List<Libro> libros = libroRepository.findByIsbn(isbn);
        if (!libros.isEmpty()) {
            for (Libro libro : libros) {
                System.out.println("Libro encontrado: " + libro.getTitulo());
                libroRepository.save(libro);
                System.out.println("Libro guardado en la base de datos.");
            }
        } else {
            System.out.println("No se encontraron libros con el ISBN: " + isbn);
        }
    }

    private String traducirGenero(String genero) {
        switch (genero.toLowerCase()) {
            case "ficcion":
                return "fiction"; // Mapeo de español a inglés
            case "no ficción":
                return "non-fiction";
            case "fantasía":
                return "fantasy";
            case "ciencia ficción":
                return "science fiction";
            // Agrega más géneros según sea necesario
            default:
                return genero; // Retorna el género original si no hay mapeo
        }
    }

    private void eliminarLibroPorTitulo(String titulo) {
        String tituloNormalizado = titulo.trim(); // Eliminar espacios en blanco
        List<Libro> libros = libroRepository.findByTituloIgnoreCase(tituloNormalizado); // Usar el método que busca por título

        if (!libros.isEmpty()) {
            for (Libro libro : libros) {
                libroRepository.delete(libro); // Eliminar el libro
                System.out.println("Libro eliminado: " + libro.getTitulo());
            }
        } else {
            System.out.println("No se encontraron libros con el título: " + titulo);
        }
    }
}
