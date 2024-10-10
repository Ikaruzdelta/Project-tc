package com.example.projecttc.utils;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Transicao;

import java.util.HashSet;
import java.util.Set;

public class ValidacaoAFD {

    public static boolean isAFD(Automato automato) {
        // Conjunto para armazenar pares únicos de estado e símbolo
        Set<String> transicoesVerificadas = new HashSet<>();
        boolean temTransicaoEpsilon = false;

        // Iterar sobre todas as transições do autômato
        for (Transicao transicao : automato.getTransicoes()) {
            int estadoDeId = transicao.getOrigem().getId();
            String simbolo = transicao.getSimbolo();

            // Verifica se é uma transição epsilon
            if (simbolo.equals("ε")) {
                temTransicaoEpsilon = true; // Encontra uma transição epsilon
                break; // Se encontrar um epsilon, já pode parar a verificação
            } else {
                // Cria uma representação única do par estado-símbolo
                String transicaoKey = estadoDeId + "-" + simbolo;

                // Verifica se já existe uma transição para esse par estado-símbolo
                if (transicoesVerificadas.contains(transicaoKey)) {
                    return false; // Se encontrar uma transição repetida, não é um AFD
                }

                // Adiciona a transição ao conjunto de transições verificadas
                transicoesVerificadas.add(transicaoKey);
            }
        }

        // Se não encontrarmos transições epsilon e nem transições repetidas, é um AFD
        return !temTransicaoEpsilon;
    }
}
