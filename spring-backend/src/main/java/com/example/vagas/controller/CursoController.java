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

import com.example.vagas.entity.Curso;
import com.example.vagas.entity.CursoCandidato;
import com.example.vagas.repository.CursoCandidatoRepository;
import com.example.vagas.repository.CursoRepository;
import com.example.vagas.repository.VagaRepository;

@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(originPatterns = "*")
public class CursoController {

    @Autowired
    private CursoRepository cursoRepository;
    
    @Autowired
    private VagaRepository vagaRepository;
    
    @Autowired
    private CursoCandidatoRepository cursoCandidatoRepository;

    @GetMapping
    public List<Curso> listarCursos() {
        return cursoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curso> buscarCurso(@PathVariable Long id) {
        return cursoRepository.findById(id)
                .map(curso -> ResponseEntity.ok().body(curso))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Curso criarCurso(@RequestBody Curso curso) {
        return cursoRepository.save(curso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Curso> atualizarCurso(@PathVariable Long id, @RequestBody Curso cursoDetails) {
        return cursoRepository.findById(id)
                .map(curso -> {
                    curso.setTitulo(cursoDetails.getTitulo());
                    curso.setCategoria(cursoDetails.getCategoria());
                    return ResponseEntity.ok(cursoRepository.save(curso));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCurso(@PathVariable Long id) {
        return cursoRepository.findById(id)
                .map(curso -> {
                    cursoRepository.delete(curso);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/estatisticas")
    public ResponseEntity<Map<String, Object>> getEstatisticasCurso(@PathVariable Long id) {
        Long totalVagas = vagaRepository.countTotalVagas();
        Long vagasComCurso = vagaRepository.countVagasWithCourse(id);
        
        double percentual = totalVagas > 0 ? (vagasComCurso * 100.0) / totalVagas : 0;
        
        Map<String, Object> estatisticas = Map.of(
            "vagasQuePedem", vagasComCurso,
            "totalVagas", totalVagas,
            "percentual", percentual
        );
        
        return ResponseEntity.ok(estatisticas);
    }

    @GetMapping("/com-progresso/{candidatoId}")
    public ResponseEntity<List<Map<String, Object>>> listarCursosComProgresso(@PathVariable Long candidatoId) {
        List<Curso> todosCursos = cursoRepository.findAll();
        List<CursoCandidato> progressos = cursoCandidatoRepository.findByCandidatoId(candidatoId);
        
        Map<Long, Integer> progressoMap = new HashMap<>();
        for (CursoCandidato cc : progressos) {
            progressoMap.put(cc.getCursoId(), cc.getProgresso());
        }
        
        List<Map<String, Object>> cursosComProgresso = todosCursos.stream().map(curso -> {
            Map<String, Object> cursoMap = new HashMap<>();
            cursoMap.put("id", curso.getId());
            cursoMap.put("titulo", curso.getTitulo());
            cursoMap.put("categoria", curso.getCategoria());
            cursoMap.put("progresso", progressoMap.getOrDefault(curso.getId(), 0));
            return cursoMap;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(cursosComProgresso);
    }
}