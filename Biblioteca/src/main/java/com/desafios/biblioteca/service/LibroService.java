package com.desafios.biblioteca.service;

import com.desafios.biblioteca.model.Libro;
import com.desafios.biblioteca.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LibroService {
    @Autowired
    private LibroRepository libroRepository;

    public Libro guardarLibro(Libro libro) {
        // Aquí puedes agregar lógica adicional si es necesario
        return libroRepository.save(libro);
    }
}
