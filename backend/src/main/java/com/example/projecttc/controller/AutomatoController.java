package com.example.projecttc.controller;

import com.example.projecttc.service.ConcatenacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.projecttc.model.Automato;
import com.example.projecttc.service.AutomatoService;

@RestController
@RequestMapping("/api/automato")
public class AutomatoController {

    @Autowired
    private AutomatoService automatoService;

    @Autowired
    private ConcatenacaoService concatenacaoService;

    @PostMapping("/complemento")
    public Automato complemento(@RequestParam("file") MultipartFile file) {
        try {
            Automato automato = automatoService.importarAutomato(file);
            return automatoService.aplicarComplemento(automato);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao aplicar complemento", e);
        }
    }

    @PostMapping("/concatenacao")
    public ResponseEntity<String> concatenacao(
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2,
            @RequestParam("outputPath") String outputPath) {
        try {
            // Validação dos arquivos
            if (file1 == null || file2 == null ||
                    !file1.getOriginalFilename().endsWith(".jff") ||
                    !file2.getOriginalFilename().endsWith(".jff")) {
                return ResponseEntity.badRequest().body("Por favor, envie dois arquivos .jff válidos.");
            }

            // Validação do caminho de saída
            if (!outputPath.endsWith(".jff")) {
                outputPath += ".jff";
            }

            // Realizar a concatenação dos autômatos usando o serviço
            concatenacaoService.concatenar(file1, file2, outputPath);

            // Retornar uma mensagem de sucesso com o caminho de saída
            return ResponseEntity.ok("Autômatos concatenados com sucesso! Arquivo salvo em: " + outputPath);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao concatenar os autômatos: " + e.getMessage());
        }
    }

    @PostMapping("/estrela")
    public Automato estrela(@RequestParam("file") MultipartFile file) {
        try {
            Automato automato = automatoService.importarAutomato(file);
            return automatoService.aplicarEstrela(automato);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao aplicar estrela", e);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Automato> uploadAutomato(@RequestParam("file") MultipartFile file) {
        try {
            Automato automato = automatoService.importarAutomato(file);
            return ResponseEntity.ok(automato);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/visualizar")
    public ResponseEntity<Automato> visualizarAutomato() {
        Automato automato = automatoService.getAutomato();
        if (automato == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(automato);
    }
}
