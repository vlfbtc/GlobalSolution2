package com.example.vagas.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "VAGA")
public class Vaga {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_vaga")
    @SequenceGenerator(name = "seq_vaga", sequenceName = "seq_vaga", allocationSize = 1)
    @Column(name = "VAGA_ID")
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "TITULO", nullable = false, length = 100)
    private String titulo;
    
    @Column(name = "DESCRICAO")
    @Lob
    private String descricao;
    
    @Column(name = "EMPRESA_ID", nullable = false)
    private Long empresaId;
    
    @ManyToMany
    @JoinTable(
        name = "VAGACURSO",
        joinColumns = @JoinColumn(name = "VAGA_ID"),
        inverseJoinColumns = @JoinColumn(name = "CURSO_ID")
    )
    private Set<Curso> cursosRecomendados = new HashSet<>();
    
    // Construtores
    public Vaga() {}
    
    public Vaga(String titulo, String descricao, Long empresaId) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.empresaId = empresaId;
    }
    
    // Getters e Setters
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
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public Long getEmpresaId() {
        return empresaId;
    }
    
    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }
    
    public Set<Curso> getCursosRecomendados() {
        return cursosRecomendados;
    }
    
    public void setCursosRecomendados(Set<Curso> cursosRecomendados) {
        this.cursosRecomendados = cursosRecomendados;
    }
}