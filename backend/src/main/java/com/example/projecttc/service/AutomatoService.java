package com.example.projecttc.service;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;
import com.example.projecttc.utils.JFFParser;

@Service
public class AutomatoService {

    private Automato automatoAtual;

    public Automato importarAutomato(MultipartFile file) throws Exception {
        try {
            File tempFile = File.createTempFile("automato", ".jff");
            file.transferTo(tempFile);
            automatoAtual = JFFParser.parse(tempFile);
            return automatoAtual;
    
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao importar autômato: problema de IO", e);
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao importar autômato: problema de parsing XML", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro inesperado ao importar autômato", e);
        }
    }
    
    
    public Automato aplicarComplemento(Automato automato) {
        for (Estado estado : automato.getEstados()) {
            estado.setFinal(!estado.isFinal());
        }
        return automato;
    }

    public Automato aplicarEstrela(Automato automato) {
        Estado estadoInicial = automato.getEstados().get(0);
        
        for (Estado estado : automato.getEstados()) {
            if (estado.isFinal()) {
                Transicao novaTransicao = new Transicao(estado, estadoInicial, "ε");
                automato.getTransicoes().add(novaTransicao);
            }
        }

        Transicao loopInicial = new Transicao(estadoInicial, estadoInicial, "ε");
        automato.getTransicoes().add(loopInicial);

        return automato;
    }

    public Automato getAutomato() {
        return automatoAtual;
    }
}
