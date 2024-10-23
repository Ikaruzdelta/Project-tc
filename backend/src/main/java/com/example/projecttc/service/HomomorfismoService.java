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
        // Função de homomorfismo mapeando os símbolos de entrada para os novos símbolos
        Map<String, String> funcaoHomomorfismo = new HashMap<>();
        funcaoHomomorfismo.put("0", "a");  
        funcaoHomomorfismo.put("1", "b");  

        // Criação do novo autômato transformado
        Automato novoAutomato = new Automato("Automato Transformado");

        // Mapeia os estados originais para novos estados (necessário para evitar duplicação)
        Map<Estado, Estado> estadoMap = new HashMap<>();

        // Copiando os estados para o novo autômato
        for (Estado estadoOriginal : automato.getEstados()) {
            Estado novoEstado = new Estado(estadoOriginal.getId(), estadoOriginal.getNome(), estadoOriginal.isInicial(), estadoOriginal.isFinal(),estadoOriginal.getX(),estadoOriginal.getY());

            // Adiciona o estado no novo autômato
            novoAutomato.addEstado(novoEstado);

            // Mapeia o estado original para o novo estado
            estadoMap.put(estadoOriginal, novoEstado);
        }

        // Processando as transições do autômato original e aplicando o homomorfismo
        for (Estado estadoOriginal : automato.getEstados()) {
            for (Transicao transicao : estadoOriginal.getTransicoes()) {
                String simboloOriginal = transicao.getSimbolo();
                // Aplica o homomorfismo aos símbolos
                String simboloTransformado = funcaoHomomorfismo.getOrDefault(simboloOriginal, simboloOriginal); 

                // Encontra os estados de origem e destino no novo autômato
                Estado origemTransformada = estadoMap.get(transicao.getOrigem());
                Estado destinoTransformado = estadoMap.get(transicao.getDestino());

                // Cria uma nova transição com o símbolo transformado
                Transicao novaTransicao = new Transicao(origemTransformada, destinoTransformado, simboloTransformado);

                // Adiciona a nova transição ao novo autômato
                novoAutomato.addTransicao(novaTransicao);
            }
        }

        return novoAutomato; 
    }
}

