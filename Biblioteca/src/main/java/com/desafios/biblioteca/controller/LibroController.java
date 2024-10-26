package com.desafios.biblioteca.controller;

import com.desafios.biblioteca.model.Libro;
import com.desafios.biblioteca.service.GoogleBooksService;
import com.desafios.biblioteca.exception.BibliotecaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    @Autowired
    private GoogleBooksService googleBooksService;

    @GetMapping("/libros/titulo/{titulo}")
    public ResponseEntity<List<Libro>> buscarLibrosPorTitulo(@PathVariable String titulo) {
        try {
            List<Libro> libros = googleBooksService.buscarLibrosPorTitulo(titulo);
            return ResponseEntity.ok(libros);
        } catch (BibliotecaException e) {
            // Manejo de la excepción
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null); // O puedes devolver un mensaje de error
        }
    }

    // Otros métodos para manejar diferentes rutas
}
