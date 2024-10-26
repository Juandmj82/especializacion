package com.desafios.biblioteca.repository;

import com.desafios.biblioteca.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    List<Libro> findByTitulo(String titulo);
    List<Libro> findByAutor(String autor);
    List<Libro> findByPaginas(int paginas);
    List<Libro> findByEvaluacion(double evaluacion);
    List<Libro> findByIsbn(String isbn);
    List<Libro> findByFechaPublicacion(String fechaPublicacion);

    @Query("SELECT l FROM Libro l WHERE LOWER(TRIM(l.autor)) = LOWER(TRIM(:autor))")
    List<Libro> findByAutorIgnoreCaseAndTrim(@Param("autor") String autor);

    @Query("SELECT l FROM Libro l WHERE LOWER(TRIM(l.autor)) LIKE LOWER(CONCAT('%', TRIM(:autor), '%'))")
    List<Libro> findByAutorContainingIgnoreCase(@Param("autor") String autor);

    @Query("SELECT l FROM Libro l WHERE LOWER(TRIM(l.titulo)) = LOWER(TRIM(:titulo))")
    List<Libro> findByTituloIgnoreCase(@Param("titulo") String titulo);

    @Query("SELECT l FROM Libro l WHERE LOWER(l.genero) = LOWER(:genero)")
    List<Libro> findByGeneroIgnoreCase(@Param("genero") String genero);

    @Query("SELECT l FROM Libro l WHERE LOWER(TRIM(l.titulo)) LIKE LOWER(CONCAT('%', TRIM(:titulo), '%'))")
    List<Libro> findByTituloContainingIgnoreCase(@Param("titulo") String titulo);

    @Query("SELECT l FROM Libro l WHERE LOWER(REPLACE(l.titulo, ' ', '')) = LOWER(REPLACE(:titulo, ' ', ''))")
List<Libro> findByTituloIgnoreCaseAndSpaces(@Param("titulo") String titulo);


}
