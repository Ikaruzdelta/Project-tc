package com.example.projecttc.service;

import java.io.File;
import java.util.ArrayList;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;
import com.example.projecttc.utils.CompletarAfd;
import com.example.projecttc.utils.GravarXML;
import com.example.projecttc.utils.JFFParser;
import com.example.projecttc.utils.ValidacaoAFD;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ComplementoService {

    public ResponseEntity<String> aplicarComplemento(MultipartFile file, String outputFilePath) {
        try {
            // Salva o arquivo recebido temporariamente
            File tempFile = File.createTempFile("automato", ".jff");
            file.transferTo(tempFile);

            // Parsing do arquivo XML para criar o autômato
            Automato automato = JFFParser.parse(tempFile);

            // Verificar se é um AFD antes de aplicar o complemento
            if (!ValidacaoAFD.isAFD(automato)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("O autômato fornecido não é um AFD. Não é possível aplicar o complemento a um AFN.");
            }

            // Completar o AFD se necessário
            if (!CompletarAfd.isAFDCompleto(automato)) {
                CompletarAfd.deixarAFDCompleto(automato);
            }

            for(Estado t: automato.getEstados()){
                if(t.isFinal() == true){
                    t.setFinal(false);
                }else{
                    t.setFinal(true);
                }
            }

            // Grava o autômato complementado no caminho de saída especificado
            GravarXML gravador = new GravarXML();
            gravador.gravarAutomato((ArrayList<Estado>) automato.getEstados(), (ArrayList<Transicao>) automato.getTransicoes(), outputFilePath);

            // Retornar uma mensagem de sucesso com o caminho de saída
            return ResponseEntity.ok("Complemento aplicado com sucesso! Arquivo salvo em: " + outputFilePath);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao aplicar complemento: " + e.getMessage());
        }
    }
}
