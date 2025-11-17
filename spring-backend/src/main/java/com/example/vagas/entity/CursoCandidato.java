package com.example.vagas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "CURSOCANDIDATO")
public class CursoCandidato {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_curso_cand")
    @SequenceGenerator(name = "seq_curso_cand", sequenceName = "seq_curso_cand", allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    
    @Column(name = "CANDIDATO_ID", nullable = false)
    private Long candidatoId;
    
    @Column(name = "CURSO_ID", nullable = false)
    private Long cursoId;
    
    @Column(name = "PROGRESSO")
    private Integer progresso = 0;
    
    // Construtores
    public CursoCandidato() {}
    
    public CursoCandidato(Long candidatoId, Long cursoId, Integer progresso) {
        this.candidatoId = candidatoId;
        this.cursoId = cursoId;
        this.progresso = progresso;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCandidatoId() {
        return candidatoId;
    }
    
    public void setCandidatoId(Long candidatoId) {
        this.candidatoId = candidatoId;
    }
    
    public Long getCursoId() {
        return cursoId;
    }
    
    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }
    
    public Integer getProgresso() {
        return progresso;
    }
    
    public void setProgresso(Integer progresso) {
        this.progresso = progresso;
    }
}