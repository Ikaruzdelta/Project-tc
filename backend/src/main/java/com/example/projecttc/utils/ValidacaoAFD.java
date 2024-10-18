package com.example.projecttc.utils;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Transicao;

import java.util.HashSet;
import java.util.Set;

public class ValidacaoAFD {

    public static boolean isAFD(Automato automato) {
        // Usando HashSet para verificar transições repetidas
        Set<String> transicoesVerificadas = new HashSet<>();
        boolean temTransicaoEpsilon = false;
        boolean transicaoRepetida = false;

        // Iterar sobre as transições do autômato
        for (Transicao transicao : automato.getTransicoes()) {
            int estadoDeId = transicao.getOrigem().getId();
            String simbolo = transicao.getSimbolo();

            // Verificar se é uma transição epsilon
            if (simbolo.equals("\u03b5") || simbolo.equals("")) {
                temTransicaoEpsilon = true;
            } else {
                // Criar uma chave única para a transição (estado-origem + símbolo)
                String transicaoKey = estadoDeId + "-" + simbolo;

                // Verificar se a transição já existe
                if (!transicoesVerificadas.add(transicaoKey)) {
                    // Se não conseguiu adicionar, significa que a transição é repetida
                    transicaoRepetida = true;
                }
            }
        }

        // Retornar true somente se não houver transições epsilon nem repetidas
        return !temTransicaoEpsilon && !transicaoRepetida;
    }
}
