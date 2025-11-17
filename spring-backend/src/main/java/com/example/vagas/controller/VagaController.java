package com.example.vagas.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.example.vagas.entity.Vaga;
import com.example.vagas.repository.CursoRepository;
import com.example.vagas.repository.VagaRepository;

@RestController
@RequestMapping("/api/vagas")
@CrossOrigin(originPatterns = "*")
public class VagaController {

    @Autowired
    private VagaRepository vagaRepository;
    
    @Autowired
    private CursoRepository cursoRepository;

    @GetMapping("/test")
    public Map<String, Object> testConnection() {
        Map<String, Object> response = new HashMap<>();
        try {
            long count = vagaRepository.count();
            response.put("status", "success");
            response.put("totalVagas", count);
            response.put("message", "Conectado ao banco Oracle");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erro: " + e.getMessage());
        }
        return response;
    }

    @GetMapping
    public List<Map<String, Object>> listarVagas() {
        List<Vaga> vagas = vagaRepository.findAll();
        return vagas.stream().map(vaga -> {
            Map<String, Object> vagaMap = new HashMap<>();
            vagaMap.put("id", vaga.getId());
            vagaMap.put("titulo", vaga.getTitulo());
            vagaMap.put("descricao", vaga.getDescricao());
            vagaMap.put("empresaId", vaga.getEmpresaId());
            return vagaMap;
        }).collect(Collectors.toList());
    }

    @GetMapping("/completo")
    public List<Vaga> listarVagasCompleto() {
        return vagaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vaga> buscarVaga(@PathVariable Long id) {
        return vagaRepository.findById(id)
                .map(vaga -> ResponseEntity.ok().body(vaga))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criarVaga(@RequestBody Map<String, Object> vagaData) {
        try {
            Vaga vaga = new Vaga();
            vaga.setTitulo((String) vagaData.get("titulo"));
            vaga.setDescricao((String) vagaData.get("descricao"));
            vaga.setEmpresaId(((Number) vagaData.get("empresaId")).longValue());
            
            // Primeiro salvar a vaga
            Vaga vagaSalva = vagaRepository.save(vaga);
            
            // Depois processar cursos recomendados se fornecidos
            if (vagaData.containsKey("cursosRecomendados") && vagaData.get("cursosRecomendados") != null) {
                Object cursosObj = vagaData.get("cursosRecomendados");
                Set<Curso> cursos = new HashSet<>();
                
                if (cursosObj instanceof List) {
                    // Se é uma lista de objetos/maps
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> cursosData = (List<Map<String, Object>>) cursosObj;
                    cursos = cursosData.stream()
                        .map(cursoData -> {
                            Long cursoId = ((Number) cursoData.get("id")).longValue();
                            return cursoRepository.findById(cursoId).orElse(null);
                        })
                        .filter(curso -> curso != null)
                        .collect(Collectors.toSet());
                } else if (cursosObj instanceof String) {
                    // Se é uma string com IDs separados por vírgula
                    String cursosStr = (String) cursosObj;
                    if (!cursosStr.isEmpty()) {
                        String[] cursosIds = cursosStr.split(",");
                        for (String idStr : cursosIds) {
                            try {
                                Long cursoId = Long.parseLong(idStr.trim());
                                cursoRepository.findById(cursoId).ifPresent(cursos::add);
                            } catch (NumberFormatException e) {
                                // Ignorar IDs inválidos
                            }
                        }
                    }
                }
                
                if (!cursos.isEmpty()) {
                    vagaSalva.setCursosRecomendados(cursos);
                    vagaSalva = vagaRepository.save(vagaSalva);
                }
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "vagaId", vagaSalva.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Erro ao criar vaga: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vaga> atualizarVaga(@PathVariable Long id, @RequestBody Vaga vagaDetails) {
        return vagaRepository.findById(id)
                .map(vaga -> {
                    vaga.setTitulo(vagaDetails.getTitulo());
                    vaga.setDescricao(vagaDetails.getDescricao());
                    vaga.setEmpresaId(vagaDetails.getEmpresaId());
                    vaga.setCursosRecomendados(vagaDetails.getCursosRecomendados());
                    return ResponseEntity.ok(vagaRepository.save(vaga));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarVaga(@PathVariable Long id) {
        return vagaRepository.findById(id)
                .map(vaga -> {
                    vagaRepository.delete(vaga);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/empresa/{empresaId}")
    public List<Vaga> listarVagasPorEmpresa(@PathVariable Long empresaId) {
        return vagaRepository.findByEmpresaId(empresaId);
    }

    @GetMapping("/limpar-inadequadas")
    public ResponseEntity<Map<String, Object>> limparVagasInadequadas() {
        try {
            // IDs das vagas inadequadas para remover
            List<Long> idsParaRemover = List.of(1L, 2L, 22L, 26L, 30L, 34L, 36L);
            int removidas = 0;
            
            for (Long id : idsParaRemover) {
                try {
                    if (vagaRepository.existsById(id)) {
                        // Primeiro limpar relacionamentos manualmente
                        Vaga vaga = vagaRepository.findById(id).orElse(null);
                        if (vaga != null) {
                            vaga.getCursosRecomendados().clear();
                            vagaRepository.save(vaga);
                            vagaRepository.deleteById(id);
                            removidas++;
                        }
                    }
                } catch (Exception e) {
                    // Continuar mesmo se uma vaga específica der erro
                    System.err.println("Erro ao remover vaga " + id + ": " + e.getMessage());
                }
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Vagas inadequadas removidas com sucesso",
                "vagasRemovidas", removidas,
                "totalVagasRestantes", vagaRepository.count()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Erro ao remover vagas: " + e.getMessage()
            ));
        }
    }
}