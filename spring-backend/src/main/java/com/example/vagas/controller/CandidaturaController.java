package com.example.vagas.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vagas.entity.Candidatura;
import com.example.vagas.entity.Vaga;
import com.example.vagas.repository.CandidaturaRepository;
import com.example.vagas.repository.VagaRepository;

@RestController
@RequestMapping("/api/candidaturas")
@CrossOrigin(originPatterns = "*")
public class CandidaturaController {

    @Autowired
    private CandidaturaRepository candidaturaRepository;
    
    @Autowired
    private VagaRepository vagaRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> criarCandidatura(@RequestBody Map<String, Object> candidaturaData) {
        try {
            Long vagaId = ((Number) candidaturaData.get("vagaId")).longValue();
            Long candidatoId = ((Number) candidaturaData.get("candidatoId")).longValue();
            
            // Verificar se já existe candidatura
            if (candidaturaRepository.findByCandidatoIdAndVagaId(candidatoId, vagaId).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Candidato já se candidatou a esta vaga"
                ));
            }
            
            Candidatura candidatura = new Candidatura(candidatoId, vagaId);
            candidaturaRepository.save(candidatura);
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Erro ao criar candidatura: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/candidato/{candidatoId}")
    public List<Map<String, Object>> listarCandidaturasPorCandidato(@PathVariable Long candidatoId) {
        List<Candidatura> candidaturas = candidaturaRepository.findByCandidatoId(candidatoId);
        
        return candidaturas.stream()
            .map(candidatura -> {
                Vaga vaga = vagaRepository.findById(candidatura.getVagaId()).orElse(null);
                Map<String, Object> response = new HashMap<>();
                response.put("vagaTitulo", vaga != null ? vaga.getTitulo() : "Vaga não encontrada");
                response.put("status", candidatura.getStatus());
                response.put("dataAplicacao", candidatura.getDataAplicacao().toString());
                return response;
            })
            .collect(Collectors.toList());
    }

    @GetMapping("/vaga/{vagaId}")
    public List<Candidatura> listarCandidaturasPorVaga(@PathVariable Long vagaId) {
        return candidaturaRepository.findByVagaId(vagaId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidatura> atualizarStatusCandidatura(
            @PathVariable Long id, 
            @RequestBody Map<String, String> statusData) {
        
        return candidaturaRepository.findById(id)
                .map(candidatura -> {
                    candidatura.setStatus(statusData.get("status"));
                    return ResponseEntity.ok(candidaturaRepository.save(candidatura));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCandidatura(@PathVariable Long id) {
        return candidaturaRepository.findById(id)
                .map(candidatura -> {
                    candidaturaRepository.delete(candidatura);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}