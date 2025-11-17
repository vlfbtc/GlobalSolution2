package com.example.vagas.repository;

import com.example.vagas.entity.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VagaRepository extends JpaRepository<Vaga, Long> {
    
    List<Vaga> findByEmpresaId(Long empresaId);
    
    List<Vaga> findByTituloContainingIgnoreCase(String titulo);
    
    @Query("SELECT v FROM Vaga v JOIN v.cursosRecomendados c WHERE c.id = :cursoId")
    List<Vaga> findByRecommendedCourse(Long cursoId);
    
    @Query("SELECT COUNT(v) FROM Vaga v")
    Long countTotalVagas();
    
    @Query("SELECT COUNT(v) FROM Vaga v JOIN v.cursosRecomendados c WHERE c.id = :cursoId")
    Long countVagasWithCourse(Long cursoId);
}