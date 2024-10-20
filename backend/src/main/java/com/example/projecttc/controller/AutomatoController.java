package com.example.projecttc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;
import com.example.projecttc.service.ComplementoService;
import com.example.projecttc.service.ConcatenacaoService;
import com.example.projecttc.service.DiferencaService;
import com.example.projecttc.service.EstrelaService;
import com.example.projecttc.service.HomomorfismoService;
import com.example.projecttc.service.IntersecaoService;
import com.example.projecttc.service.ReversoService;
import com.example.projecttc.service.UniaoService;
import com.example.projecttc.utils.ExibirResultado;
import com.example.projecttc.utils.GravarXML;
import com.example.projecttc.utils.JFFParser;
import com.example.projecttc.utils.ValidacaoAFD;

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

    @Autowired
    private DiferencaService diferencaService;

    @Autowired
    private ReversoService reversoService;

    @Autowired
    private IntersecaoService intersecaoService;

    @Autowired
    private HomomorfismoService homomorfismoService;

    @PostMapping({"/complemento"})
   public ResponseEntity<Resource> complemento(@RequestParam("file") MultipartFile file) {
      File tempFile = null;

      ResponseEntity var12;
      try {
         if (file != null && file.getOriginalFilename().endsWith(".jff")) {
            String fileName = (new File(file.getOriginalFilename())).getName();
            String outputPath = "resultados/complemento/C_" + fileName;
            tempFile = File.createTempFile("automato", ".jff");
            file.transferTo(tempFile);
            Automato automato = JFFParser.parse(tempFile);
            Automato complemento = this.complementoService.complemento(automato);
            GravarXML gravador = new GravarXML();
            gravador.gravarAutomato((ArrayList)complemento.getEstados(), (ArrayList)complemento.getTransicoes(), outputPath);
            File arquivoResultante = new File(outputPath);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(arquivoResultante));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + arquivoResultante.getName());
            headers.add("Content-Type", "application/xml");
            var12 = ((ResponseEntity.BodyBuilder)ResponseEntity.ok().headers(headers)).contentLength(arquivoResultante.length()).body(resource);
            return var12;
         }

         var12 = ResponseEntity.badRequest().body((Object)null);
      } catch (Exception var15) {
         var15.printStackTrace();
         var12 = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((Object)null);
         return var12;
      } finally {
         if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
         }

      }

      return var12;
   }

    @PostMapping("/estrela")
    public ResponseEntity<String> estrela(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || !file.getOriginalFilename().endsWith(".jff")) {
                return ResponseEntity.badRequest().body("Por favor, envie um arquivo .jff válido.");
            }
            String fileName = new File(file.getOriginalFilename()).getName();
            String outputPath = "resultados/estrela/E_" + fileName;
            File tempFile = File.createTempFile("automato", ".jff");
            file.transferTo(tempFile);
            Automato automato = JFFParser.parse(tempFile);
            Automato estrela = estrelaService.estrela(automato);
            GravarXML gravador = new GravarXML();
            gravador.gravarAutomato((ArrayList<Estado>) estrela.getEstados(), (ArrayList<Transicao>) estrela.getTransicoes(), outputPath);
            String resultadoFormatado = ExibirResultado.exibirResultado(estrela);
            return ResponseEntity.ok(resultadoFormatado + "\n\nOperação de estrela realizada com sucesso! Arquivo salvo em: " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao aplicar a operação de estrela no autômato: " + e.getMessage());
        }
    }

    @PostMapping("/concatenacao")
    public ResponseEntity<String> concatenacao(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2) {
        try {
            if (file1 == null || file2 == null || !file1.getOriginalFilename().endsWith(".jff") || !file2.getOriginalFilename().endsWith(".jff")) {
                return ResponseEntity.badRequest().body("Por favor, envie dois arquivos .jff válidos.");
            }
            String fileName1 = new File(file1.getOriginalFilename()).getName();
            String fileName2 = new File(file2.getOriginalFilename()).getName();
            String outputPath = "resultados/concatenacao/K_" + fileName1 + "_" + fileName2;
            File tempFile1 = File.createTempFile("automato1", ".jff");
            file1.transferTo(tempFile1);
            File tempFile2 = File.createTempFile("automato2", ".jff");
            file2.transferTo(tempFile2);
            Automato automato1 = JFFParser.parse(tempFile1);
            Automato automato2 = JFFParser.parse(tempFile2);
            Automato concatenacao = concatenacaoService.concatenacao(automato1, automato2);
            GravarXML gravador = new GravarXML();
            gravador.gravarAutomato((ArrayList<Estado>) concatenacao.getEstados(), (ArrayList<Transicao>) concatenacao.getTransicoes(), outputPath);
            String resultadoFormatado = ExibirResultado.exibirResultado(concatenacao);
            return ResponseEntity.ok(resultadoFormatado + "\n\nAutômatos concatenados com sucesso! Arquivo salvo em: " + outputPath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao concatenar os autômatos: " + e.getMessage());
        }
    }

    @PostMapping("/uniao")
    public ResponseEntity<String> uniao(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2) {
        try {
            if (file1 == null || file2 == null || !file1.getOriginalFilename().endsWith(".jff") || !file2.getOriginalFilename().endsWith(".jff")) {
                return ResponseEntity.badRequest().body("Por favor, envie dois arquivos .jff válidos.");
            }

            String fileName1 = new File(file1.getOriginalFilename()).getName();
            String fileName2 = new File(file2.getOriginalFilename()).getName();
            String outputPath = "resultados/uniao/U_" + fileName1 + "_" + fileName2;

            File tempFile1 = File.createTempFile("automato1", ".jff");
            file1.transferTo(tempFile1);
            File tempFile2 = File.createTempFile("automato2", ".jff");
            file2.transferTo(tempFile2);

            Automato automato1 = JFFParser.parse(tempFile1);
            Automato automato2 = JFFParser.parse(tempFile2);

            Automato uniao;

            // Verifica se ambos os autômatos são AFD (Determinísticos)
            if (ValidacaoAFD.isAFD(automato1) && ValidacaoAFD.isAFD(automato2)) {
                uniao = uniaoService.uniaoAFD(automato1, automato2);
            } else {
                uniao = uniaoService.uniaoAFN(automato1, automato2);
            }

            // Gravar o resultado da união
            GravarXML gravador = new GravarXML();
            gravador.gravarAutomato((ArrayList<Estado>) uniao.getEstados(), (ArrayList<Transicao>) uniao.getTransicoes(), outputPath);

            // Exibir o resultado
            String resultadoFormatado = ExibirResultado.exibirResultado(uniao);
            return ResponseEntity.ok(resultadoFormatado + "\n\nUnião dos autômatos realizada com sucesso! Arquivo salvo em: " + outputPath);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao realizar a união dos autômatos: " + e.getMessage());
        }
    }

    @PostMapping("/diferenca")
    public ResponseEntity<String> diferenca(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2) {
        try {
            if (file1 == null || file2 == null || !file1.getOriginalFilename().endsWith(".jff") || !file2.getOriginalFilename().endsWith(".jff")) {
                return ResponseEntity.badRequest().body("Por favor, envie dois arquivos .jff válidos.");
            }
            String fileName1 = new File(file1.getOriginalFilename()).getName();
            String fileName2 = new File(file2.getOriginalFilename()).getName();
            String outputPath = "resultados/diferenca/D_" + fileName1 + "_" + fileName2;
            File tempFile1 = File.createTempFile("automato1", ".jff");
            file1.transferTo(tempFile1);
            File tempFile2 = File.createTempFile("automato2", ".jff");
            file2.transferTo(tempFile2);
            Automato automato1 = JFFParser.parse(tempFile1);
            Automato automato2 = JFFParser.parse(tempFile2);
            Automato diferenca = diferencaService.diferenca(automato1, automato2);
            GravarXML gravador = new GravarXML();
            gravador.gravarAutomato((ArrayList<Estado>) diferenca.getEstados(), (ArrayList<Transicao>) diferenca.getTransicoes(), outputPath);
            String resultadoFormatado = ExibirResultado.exibirResultado(diferenca);
            return ResponseEntity.ok(resultadoFormatado + "\n\nDiferença realizada com sucesso! Arquivo salvo em: " + outputPath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao realizar a diferença: " + e.getMessage());
        }
    }

    @PostMapping("/reverso")
    public ResponseEntity<String> reverso(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || !file.getOriginalFilename().endsWith(".jff")) {
                return ResponseEntity.badRequest().body("Por favor, envie um arquivo .jff válido.");
            }
            String fileName = new File(file.getOriginalFilename()).getName();
            String outputPath = "resultados/reverso/R_" + fileName;
            File tempFile = File.createTempFile("automato", ".jff");
            file.transferTo(tempFile);
            Automato automato = JFFParser.parse(tempFile);
            Automato reverso = reversoService.reverso(automato);
            GravarXML gravador = new GravarXML();
            gravador.gravarAutomato((ArrayList<Estado>) reverso.getEstados(), (ArrayList<Transicao>) reverso.getTransicoes(), outputPath);
            String resultadoFormatado = ExibirResultado.exibirResultado(reverso);
            return ResponseEntity.ok(resultadoFormatado + "\n\nReverso realizado com sucesso! Arquivo salvo em: " + outputPath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao realizar o reverso: " + e.getMessage());
        }
    }

    @PostMapping("/intersecao")
    public ResponseEntity<String> intersecao(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2) {
        try {
            if (file1 == null || file2 == null || !file1.getOriginalFilename().endsWith(".jff") || !file2.getOriginalFilename().endsWith(".jff")) {
                return ResponseEntity.badRequest().body("Por favor, envie dois arquivos .jff válidos.");
            }

            String fileName1 = new File(file1.getOriginalFilename()).getName();
            String fileName2 = new File(file2.getOriginalFilename()).getName();
            String outputPath = "resultados/intersecao/I_" + fileName1 + "_" + fileName2;

            File tempFile1 = File.createTempFile("automato1", ".jff");
            file1.transferTo(tempFile1);
            File tempFile2 = File.createTempFile("automato2", ".jff");
            file2.transferTo(tempFile2);

            Automato automato1 = JFFParser.parse(tempFile1);
            Automato automato2 = JFFParser.parse(tempFile2);

            Automato intersecao;

            // Verifica se ambos os autômatos são AFD (Determinísticos)
            if (ValidacaoAFD.isAFD(automato1) && ValidacaoAFD.isAFD(automato2)) {
                intersecao = intersecaoService.intersecaoAFD(automato1, automato2);
            } else {
                intersecao = intersecaoService.intersecaoAFN(automato1, automato2);
            }

            // Gravar o resultado da interseção
            GravarXML gravador = new GravarXML();
            gravador.gravarAutomato((ArrayList<Estado>) intersecao.getEstados(), (ArrayList<Transicao>) intersecao.getTransicoes(), outputPath);

            // Exibir o resultado
            String resultadoFormatado = ExibirResultado.exibirResultado(intersecao);
            return ResponseEntity.ok(resultadoFormatado + "\n\nInterseção realizada com sucesso! Arquivo salvo em: " + outputPath);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao realizar a interseção: " + e.getMessage());
        }
    }


    @PostMapping("/diferenca-simetrica")
    public ResponseEntity<String> diferencaSimetrica(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2) {
        try {
            if (file1 == null || file2 == null || !file1.getOriginalFilename().endsWith(".jff") || !file2.getOriginalFilename().endsWith(".jff")) {
                return ResponseEntity.badRequest().body("Por favor, envie dois arquivos .jff válidos.");
            }
            String fileName1 = new File(file1.getOriginalFilename()).getName();
            String fileName2 = new File(file2.getOriginalFilename()).getName();
            String outputPath = "resultados/diferenca-simetrica/DS_" + fileName1 + "_" + fileName2;
            File tempFile1 = File.createTempFile("automato1", ".jff");
            file1.transferTo(tempFile1);
            File tempFile2 = File.createTempFile("automato2", ".jff");
            file2.transferTo(tempFile2);
            Automato automato1 = JFFParser.parse(tempFile1);
            Automato automato2 = JFFParser.parse(tempFile2);
            Automato diferencaSimetrica = diferencaService.diferencaSimetrica(automato1, automato2);
            GravarXML gravador = new GravarXML();
            gravador.gravarAutomato((ArrayList<Estado>) diferencaSimetrica.getEstados(), (ArrayList<Transicao>) diferencaSimetrica.getTransicoes(), outputPath);
            String resultadoFormatado = ExibirResultado.exibirResultado(diferencaSimetrica);
            return ResponseEntity.ok(resultadoFormatado + "\n\nDiferença Simétrica realizada com sucesso! Arquivo salvo em: " + outputPath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao realizar a diferença simétrica: " + e.getMessage());
        }
    }
    @PostMapping("/homomorfismo")
    public ResponseEntity<String> homomorfismo(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || !file.getOriginalFilename().endsWith(".jff")) {
                return ResponseEntity.badRequest().body("Por favor, envie um arquivo .jff válido.");
            }

            String fileName = new File(file.getOriginalFilename()).getName();
            String outputPath = "resultados/homomorfismo/H_" + fileName;

            File tempFile = File.createTempFile("automato", ".jff");
            file.transferTo(tempFile);

            Automato automato = JFFParser.parse(tempFile);
            
            Automato homomorfismo = homomorfismoService.homomorfismo(automato);

            GravarXML gravador = new GravarXML();
            gravador.gravarAutomato((ArrayList<Estado>) homomorfismo.getEstados(), (ArrayList<Transicao>) homomorfismo.getTransicoes(), outputPath);

            String resultadoFormatado = ExibirResultado.exibirResultado(homomorfismo);
            return ResponseEntity.ok(resultadoFormatado + "\n\nHomomorfismo aplicado com sucesso! Arquivo salvo em: " + outputPath);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao aplicar o homomorfismo no autômato: " + e.getMessage());
        }
    }
}
