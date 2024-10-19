package com.example.projecttc.service;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class HomomorfismoService {

    public Automato homomorfismo(Automato automato) {
        // Definir a função de homomorfismo internamente
        Map<String, String> funcaoHomomorfismo = new HashMap<>();
        funcaoHomomorfismo.put("0", "ab");  // Exemplo: substitui '0' por 'ab'
        funcaoHomomorfismo.put("1", "ε");   // Exemplo: substitui '1' por string vazia (ε)

        // Criar um novo autômato para armazenar o resultado
        Automato novoAutomato = new Automato("Automato Transformado");

        // Copiar os estados do autômato original para o novo autômato
        for (Estado estado : automato.getEstados()) {
            novoAutomato.addEstado(new Estado(estado)); // Copia os estados
        }

        // Processar todas as transições do autômato original
        for (Estado estado : automato.getEstados()) {
            for (Transicao transicao : estado.getTransicoes()) {
                // Substituir o símbolo da transição com base na função de homomorfismo
                String simboloOriginal = transicao.getSimbolo();
                String simboloTransformado = funcaoHomomorfismo.getOrDefault(simboloOriginal, simboloOriginal); // Usa o símbolo original se não houver substituição

                // Criar nova transição com o símbolo transformado
                Transicao novaTransicao = new Transicao(transicao.getOrigem(), transicao.getDestino(), simboloTransformado);
                
                // Adicionar a nova transição ao novo autômato
                novoAutomato.addTransicao(novaTransicao);
            }
        }

        return novoAutomato; // Retorna o autômato com o alfabeto substituído
    }
}
