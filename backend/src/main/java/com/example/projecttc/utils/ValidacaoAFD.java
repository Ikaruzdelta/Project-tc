package com.example.projecttc.utils;

import java.util.HashSet;
import java.util.Set;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Transicao;

public class ValidacaoAFD {

    public static boolean isAFD(Automato automato) {
        Set<String> transicoesVerificadas = new HashSet<>();
        boolean temTransicaoEpsilon = false;
        boolean transicaoRepetida = false;

        for (Transicao transicao : automato.getTransicoes()) {
            int estadoDeId = transicao.getOrigem().getId();
            String simbolo = transicao.getSimbolo();

            if (simbolo.equals("\u03b5") || simbolo.equals("")) {
                temTransicaoEpsilon = true;
            } else {
                String transicaoKey = estadoDeId + "-" + simbolo;

                if (!transicoesVerificadas.add(transicaoKey)) {
                    transicaoRepetida = true;
                }
            }
        }
        return !temTransicaoEpsilon && !transicaoRepetida;
    }
}
