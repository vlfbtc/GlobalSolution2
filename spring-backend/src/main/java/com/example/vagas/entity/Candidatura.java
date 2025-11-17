package com.example.vagas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "CANDIDATURA")
public class Candidatura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_candidatura")
    @SequenceGenerator(name = "seq_candidatura", sequenceName = "seq_candidatura", allocationSize = 1)
    @Column(name = "CANDIDATURA_ID")
    private Long id;
    
    @Column(name = "CANDIDATO_ID", nullable = false)
    private Long candidatoId;
    
    @Column(name = "VAGA_ID", nullable = false)
    private Long vagaId;
    
    @Size(max = 50)
    @Column(name = "STATUS", length = 50)
    private String status = "Pendente";
    
    @Column(name = "DATA_APLICACAO")
    private LocalDate dataAplicacao = LocalDate.now();
    
    // Construtores
    public Candidatura() {}
    
    public Candidatura(Long candidatoId, Long vagaId) {
        this.candidatoId = candidatoId;
        this.vagaId = vagaId;
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
    
    public Long getVagaId() {
        return vagaId;
    }
    
    public void setVagaId(Long vagaId) {
        this.vagaId = vagaId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDate getDataAplicacao() {
        return dataAplicacao;
    }
    
    public void setDataAplicacao(LocalDate dataAplicacao) {
        this.dataAplicacao = dataAplicacao;
    }
}