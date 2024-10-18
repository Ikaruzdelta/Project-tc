package com.example.projecttc.controller;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;
import com.example.projecttc.service.ComplementoService;
import com.example.projecttc.service.ConcatenacaoService;
import com.example.projecttc.service.EstrelaService;
import com.example.projecttc.service.UniaoService;
import com.example.projecttc.utils.GravarXML;
import com.example.projecttc.utils.JFFParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;

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

            // Salva os arquivos recebidos temporariamente
            File tempFile = File.createTempFile("automato", ".jff");
            file.transferTo(tempFile);
            // Parsing dos arquivos XML para criar os autômatos
            Automato automato = JFFParser.parse(tempFile);
            // Aplicar o complemento
            Automato complemento = complementoService.aplicarComplemento(automato);

            // Grava o autômato complementado no caminho de saída especificado
            GravarXML gravador = new GravarXML();
            gravador.gravarAutomato((ArrayList<Estado>) complemento.getEstados(), (ArrayList<Transicao>) complemento.getTransicoes(), outputPath);

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

            // Salva os arquivos recebidos temporariamente
            File tempFile = File.createTempFile("automato", ".jff");
            file.transferTo(tempFile);
            // Parsing dos arquivos XML para criar os autômatos
            Automato automato = JFFParser.parse(tempFile);

            // Aplicar a operação de estrela no autômato
            Automato estrela = estrelaService.aplicarEstrela(automato);

            // Grava o autômato complementado no caminho de saída especificado
            GravarXML gravador = new GravarXML();
            gravador.gravarAutomato((ArrayList<Estado>) estrela.getEstados(), (ArrayList<Transicao>) estrela.getTransicoes(), outputPath);


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

            // Salva os arquivos recebidos temporariamente
            File tempFile1 = File.createTempFile("automato1", ".jff");
            file1.transferTo(tempFile1);
            File tempFile2 = File.createTempFile("automato2", ".jff");
            file2.transferTo(tempFile2);
            // Parsing dos arquivos XML para criar os autômatos
            Automato automato1 = JFFParser.parse(tempFile1);
            Automato automato2 = JFFParser.parse(tempFile2);

            // Realizar a concatenação dos autômatos usando o serviço
            Automato concatenacao = concatenacaoService.concatenar(automato1,automato2);

            // Grava o autômato complementado no caminho de saída especificado
            GravarXML gravador = new GravarXML();
            gravador.gravarAutomato((ArrayList<Estado>) concatenacao.getEstados(), (ArrayList<Transicao>) concatenacao.getTransicoes(), outputPath);
 
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

            // Salva os arquivos recebidos temporariamente
            File tempFile1 = File.createTempFile("automato1", ".jff");
            file1.transferTo(tempFile1);
            File tempFile2 = File.createTempFile("automato2", ".jff");
            file2.transferTo(tempFile2);
            // Parsing dos arquivos XML para criar os autômatos
            Automato automato1 = JFFParser.parse(tempFile1);
            Automato automato2 = JFFParser.parse(tempFile2);

            // Realizar a união dos autômatos usando o serviço
            Automato uniao = uniaoService.UnirAFN(automato1,automato2);
            // Grava o autômato complementado no caminho de saída especificado
            GravarXML gravador = new GravarXML();
            gravador.gravarAutomato((ArrayList<Estado>) uniao.getEstados(), (ArrayList<Transicao>) uniao.getTransicoes(), outputPath);

            // Retornar uma mensagem de sucesso com o caminho de saída
            return ResponseEntity.ok("União dos autômatos realizada com sucesso! Arquivo salvo em: " + outputPath);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao realizar a união dos autômatos: " + e.getMessage());
        }
    }
}
