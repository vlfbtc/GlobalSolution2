package com.example.vagas.controller;

import com.example.vagas.entity.CursoCandidato;
import com.example.vagas.repository.CursoCandidatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/progresso")
@CrossOrigin(originPatterns = "*")
public class ProgressoController {

    @Autowired
    private CursoCandidatoRepository cursoCandidatoRepository;

    @PostMapping("/iniciar")
    public ResponseEntity<Map<String, Object>> iniciarCurso(@RequestBody Map<String, Object> dados) {
        try {
            Long cursoId = ((Number) dados.get("cursoId")).longValue();
            Long candidatoId = ((Number) dados.get("candidatoId")).longValue();
            
            Optional<CursoCandidato> existing = cursoCandidatoRepository.findByCandidatoIdAndCursoId(candidatoId, cursoId);
            
            if (existing.isEmpty()) {
                CursoCandidato cursoCandidato = new CursoCandidato(candidatoId, cursoId, 0);
                cursoCandidatoRepository.save(cursoCandidato);
            }
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Erro ao iniciar curso: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/atualizar")
    public ResponseEntity<Map<String, Object>> atualizarProgresso(@RequestBody Map<String, Object> dados) {
        try {
            Long cursoId = ((Number) dados.get("cursoId")).longValue();
            Long candidatoId = ((Number) dados.get("candidatoId")).longValue();
            Integer progresso = ((Number) dados.get("progresso")).intValue();
            
            Optional<CursoCandidato> existing = cursoCandidatoRepository.findByCandidatoIdAndCursoId(candidatoId, cursoId);
            
            CursoCandidato cursoCandidato;
            if (existing.isPresent()) {
                cursoCandidato = existing.get();
                cursoCandidato.setProgresso(progresso);
            } else {
                cursoCandidato = new CursoCandidato(candidatoId, cursoId, progresso);
            }
            
            cursoCandidatoRepository.save(cursoCandidato);
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Erro ao atualizar progresso: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{candidatoId}/{cursoId}")
    public ResponseEntity<Map<String, Object>> buscarProgresso(
            @PathVariable Long candidatoId, 
            @PathVariable Long cursoId) {
        
        Optional<CursoCandidato> cursoCandidato = cursoCandidatoRepository.findByCandidatoIdAndCursoId(candidatoId, cursoId);
        
        Integer progresso = cursoCandidato.map(CursoCandidato::getProgresso).orElse(0);
        
        return ResponseEntity.ok(Map.of("progresso", progresso));
    }

    @GetMapping("/candidato/{candidatoId}")
    public List<CursoCandidato> listarProgressoPorCandidato(@PathVariable Long candidatoId) {
        return cursoCandidatoRepository.findByCandidatoId(candidatoId);
    }
}