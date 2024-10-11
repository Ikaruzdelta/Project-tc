package com.example.projecttc.controller;

import com.example.projecttc.service.ComplementoService;
import com.example.projecttc.service.ConcatenacaoService;
import com.example.projecttc.service.EstrelaService;
import com.example.projecttc.service.UniaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/automato")
public class AutomatoController {

    @Autowired
    private ConcatenacaoService concatenacaoService;

    @Autowired
    private ComplementoService complementoService;

    @Autowired
    private EstrelaService estrelaService;

    @Autowired
    private UniaoService uniaoService;

    @PostMapping("/complemento")
    public ResponseEntity<String> complemento(@RequestParam("file") MultipartFile file) {
        try {
            // Validação do arquivo recebido
            if (file == null || !file.getOriginalFilename().endsWith(".jff")) {
                return ResponseEntity.badRequest().body("Por favor, envie um arquivo .jff válido.");
            }

            // Gerar o caminho de saída automático
            String fileName = new File(file.getOriginalFilename()).getName();
            String outputPath = "resultados/complemento/C_" + fileName;

            // Aplicar o complemento
            complementoService.aplicarComplemento(file, outputPath);

            // Retornar uma mensagem de sucesso com o caminho de saída
            return ResponseEntity.ok("Complemento de Autômato realizado com sucesso! Arquivo salvo em: " + outputPath);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao complementar o autômato: " + e.getMessage());
        }
    }

    @PostMapping("/estrela")
    public ResponseEntity<String> estrela(@RequestParam("file") MultipartFile file) {
        try {
            // Validação do arquivo recebido
            if (file == null || !file.getOriginalFilename().endsWith(".jff")) {
                return ResponseEntity.badRequest().body("Por favor, envie um arquivo .jff válido.");
            }

            // Gerar o caminho de saída automático
            String fileName = new File(file.getOriginalFilename()).getName();
            String outputPath = "resultados/estrela/E_" + fileName;

            // Aplicar a operação de estrela no autômato
            estrelaService.aplicarEstrela(file, outputPath);

            // Retornar uma mensagem de sucesso com o caminho de saída
            return ResponseEntity.ok("Operação de estrela realizada com sucesso! Arquivo salvo em: " + outputPath);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao aplicar a operação de estrela no autômato: " + e.getMessage());
        }
    }

    @PostMapping("/concatenacao")
    public ResponseEntity<String> concatenacao(
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2) {
        try {
            // Validação dos arquivos
            if (file1 == null || file2 == null ||
                    !file1.getOriginalFilename().endsWith(".jff") ||
                    !file2.getOriginalFilename().endsWith(".jff")) {
                return ResponseEntity.badRequest().body("Por favor, envie dois arquivos .jff válidos.");
            }

            // Gerar o caminho de saída automático
            String fileName1 = new File(file1.getOriginalFilename()).getName();
            String fileName2 = new File(file2.getOriginalFilename()).getName();
            String outputPath = "resultados/concatenacao/K_" + fileName1 + "_" + fileName2;

            // Realizar a concatenação dos autômatos usando o serviço
            concatenacaoService.concatenar(file1, file2, outputPath);

            // Retornar uma mensagem de sucesso com o caminho de saída
            return ResponseEntity.ok("Autômatos concatenados com sucesso! Arquivo salvo em: " + outputPath);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao concatenar os autômatos: " + e.getMessage());
        }
    }

    @PostMapping("/uniao")
    public ResponseEntity<String> uniao(
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2) {
        try {
            // Validação dos arquivos
            if (file1 == null || file2 == null ||
                    !file1.getOriginalFilename().endsWith(".jff") ||
                    !file2.getOriginalFilename().endsWith(".jff")) {
                return ResponseEntity.badRequest().body("Por favor, envie dois arquivos .jff válidos.");
            }

            // Gerar o caminho de saída automático
            String fileName1 = new File(file1.getOriginalFilename()).getName();
            String fileName2 = new File(file2.getOriginalFilename()).getName();
            String outputPath = "resultados/uniao/U_" + fileName1 + "_" + fileName2;

            // Realizar a união dos autômatos usando o serviço
            uniaoService.UnirAFN(file1, file2, outputPath);

            // Retornar uma mensagem de sucesso com o caminho de saída
            return ResponseEntity.ok("União dos autômatos realizada com sucesso! Arquivo salvo em: " + outputPath);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao realizar a união dos autômatos: " + e.getMessage());
        }
    }
}
