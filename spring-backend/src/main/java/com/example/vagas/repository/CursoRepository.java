package com.example.vagas.repository;

import com.example.vagas.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    
    List<Curso> findByTituloContainingIgnoreCase(String titulo);
    
    List<Curso> findByCategoria(String categoria);
    
    @Query("SELECT c FROM Curso c ORDER BY c.titulo")
    List<Curso> findAllOrderByTitulo();
}