package com.example.vagas.repository;

import com.example.vagas.entity.Candidatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {
    
    List<Candidatura> findByCandidatoId(Long candidatoId);
    
    List<Candidatura> findByVagaId(Long vagaId);
    
    Optional<Candidatura> findByCandidatoIdAndVagaId(Long candidatoId, Long vagaId);
    
    @Query("SELECT COUNT(c) FROM Candidatura c WHERE c.candidatoId = :candidatoId")
    Long countByCandidatoId(Long candidatoId);
}