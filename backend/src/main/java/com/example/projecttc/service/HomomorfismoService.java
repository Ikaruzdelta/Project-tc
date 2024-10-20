package com.example.projecttc.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;

@Service
public class HomomorfismoService {

    public Automato homomorfismo(Automato automato) {
        Map<String, String> funcaoHomomorfismo = new HashMap<>();
        funcaoHomomorfismo.put("0", "ab");  
        funcaoHomomorfismo.put("1", "ε");  

        Automato novoAutomato = new Automato("Automato Transformado");

        for (Estado estado : automato.getEstados()) {
            novoAutomato.addEstado(new Estado(estado));
        }

        for (Estado estado : automato.getEstados()) {
            for (Transicao transicao : estado.getTransicoes()) {
                String simboloOriginal = transicao.getSimbolo();
                String simboloTransformado = funcaoHomomorfismo.getOrDefault(simboloOriginal, simboloOriginal); // Usa o símbolo original se não houver substituição

                Transicao novaTransicao = new Transicao(transicao.getOrigem(), transicao.getDestino(), simboloTransformado);

                novoAutomato.addTransicao(novaTransicao);
            }
        }

        return novoAutomato; 
    }
}
