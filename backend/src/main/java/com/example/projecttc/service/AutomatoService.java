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

    public Automato completarAFD(Automato automato) {
        int novoId = gerarNovoId(automato);
        String nomeEstadoPoço = gerarNomeEstado(automato);
        Estado estadoEspecial = new Estado(novoId, nomeEstadoPoço, false, false, "0", "0");
        automato.addEstado(estadoEspecial);
        for (Estado estado : automato.getEstados()) {
            if (!estado.equals(estadoEspecial)) {
                for (String simbolo : automato.getAlfabeto()) {
                    if (!estado.temTransicao(simbolo)) {
                        automato.addTransicao(new Transicao(estado, estadoEspecial, simbolo));
                    }
                }
            }
        }
        for (String simbolo : automato.getAlfabeto()) {
            if (!estadoEspecial.temTransicao(simbolo)) {
                automato.addTransicao(new Transicao(estadoEspecial, estadoEspecial, simbolo));
            }
        }
        return automato;
    }

    public Automato aplicarComplemento(Automato automato) {
        automato = completarAFD(automato);
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

    public int gerarNovoId(Automato automato) {
        int maiorId = -1;
        for (Estado estado : automato.getEstados()) {
            if (estado.getId() > maiorId) {
                maiorId = estado.getId();
            }
        }
        return maiorId + 1;
    }

    public String gerarNomeEstado(Automato automato) {
        int maxNum = -1;
        for (Estado estado : automato.getEstados()) {
            String nome = estado.getNome();
            if (nome.startsWith("q")) {
                try {
                    int num = Integer.parseInt(nome.substring(1));
                    if (num > maxNum) {
                        maxNum = num;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        return "q" + (maxNum + 1);
    }
}
