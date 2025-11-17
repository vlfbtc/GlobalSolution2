package com.example.vagas.repository;

import com.example.vagas.entity.CursoCandidato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoCandidatoRepository extends JpaRepository<CursoCandidato, Long> {
    
    List<CursoCandidato> findByCandidatoId(Long candidatoId);
    
    Optional<CursoCandidato> findByCandidatoIdAndCursoId(Long candidatoId, Long cursoId);
    
    @Query("SELECT COUNT(cc) FROM CursoCandidato cc WHERE cc.candidatoId = :candidatoId AND cc.progresso = 100")
    Long countCompletedCourses(Long candidatoId);
}