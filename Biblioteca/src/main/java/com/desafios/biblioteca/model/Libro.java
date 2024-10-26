package com.desafios.biblioteca.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Asegúrate de que este campo sea único

    @NotNull
    @Size(min = 1, max = 255)
    private String titulo;

    @NotNull
    @Size(min = 1, max = 255)
    private String autor;

    @NotNull
    @Size(min = 1, max = 100)
    private String genero;

    private int paginas;

    private double evaluacion;

    @NotNull
    @Size(min = 10, max = 13)
    private String isbn;

    private String editorial;

    private String fechaPublicacion;

    private String caratulaUrl; // Nuevo campo para la URL de la carátula

    // Constructor por defecto
    public Libro() {
    }

    // Constructor
    public Libro(String titulo, String autor, String genero, int paginas, double evaluacion, String isbn, String editorial, String fechaPublicacion, String caratulaUrl) {
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.paginas = paginas;
        this.evaluacion = evaluacion;
        this.isbn = isbn;
        this.editorial = editorial;
        this.fechaPublicacion = fechaPublicacion;
        this.caratulaUrl = caratulaUrl; // Inicializar el nuevo campo
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getPaginas() {
        return paginas;
    }

    public void setPaginas(int paginas) {
        this.paginas = paginas;
    }

    public double getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(double evaluacion) {
        this.evaluacion = evaluacion;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getCaratulaUrl() {
        return caratulaUrl;
    }

    public void setCaratulaUrl(String caratulaUrl) {
        this.caratulaUrl = caratulaUrl;
    }
}
