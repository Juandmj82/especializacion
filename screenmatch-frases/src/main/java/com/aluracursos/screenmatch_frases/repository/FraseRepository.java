package com.aluracursos.screenmatch_frases.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aluracursos.screenmatch_frases.model.Frase;

@Repository
public interface FraseRepository extends JpaRepository<Frase, Long> {
    @Query(value = "SELECT * FROM frases ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Frase obtenerFraseAleatoria();
}