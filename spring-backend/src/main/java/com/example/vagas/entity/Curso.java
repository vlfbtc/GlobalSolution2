package com.example.vagas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "CURSO")
public class Curso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_curso")
    @SequenceGenerator(name = "seq_curso", sequenceName = "seq_curso", allocationSize = 1)
    @Column(name = "CURSO_ID")
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "TITULO", nullable = false, length = 100)
    private String titulo;
    
    @Size(max = 100)
    @Column(name = "CATEGORIA", length = 100)
    private String categoria;
    
    // Construtores
    public Curso() {}
    
    public Curso(String titulo, String categoria) {
        this.titulo = titulo;
        this.categoria = categoria;
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
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}