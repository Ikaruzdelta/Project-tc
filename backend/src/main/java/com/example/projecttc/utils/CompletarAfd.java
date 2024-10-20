package com.example.projecttc.utils;
import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;

public class CompletarAfd {

    public static boolean isAFDCompleto(Automato automato) {
        for (Estado estado : automato.getEstados()) {

            for (String simbolo : automato.getAlfabeto()) {
                boolean temTransicaoParaSimbolo = false;
                for (Transicao transicao : estado.getTransicoes()) {

                    if (transicao.getSimbolo().equals(simbolo)) {

                        temTransicaoParaSimbolo = true;
                    }
               
                }
                if (!temTransicaoParaSimbolo) {

                    return false;
                }
            }
        }
        return true;
    }


    public static void deixarAFDCompleto(Automato automato) {
        int novoId = GerarNovoId.gerarNovoId(automato);
        Estado estadoConsumidor = new Estado(novoId, "q"+novoId, false, false, 300, 300);

        if (!automato.getEstados().contains(estadoConsumidor)) {
            automato.getEstados().add(estadoConsumidor);
        }

        for (Estado estado : automato.getEstados()) {
            for (String simbolo : automato.getAlfabeto()) {
                boolean temTransicao = false;

                for (Transicao transicao : estado.getTransicoes()) {
                    if (transicao.getSimbolo().equalsIgnoreCase(simbolo)) {
                        temTransicao = true;
                        break; 
                    }
                }

                if (!temTransicao) {
                    Transicao novaTransicao = new Transicao(estado, estadoConsumidor, simbolo);
                    estado.getTransicoes().add(novaTransicao);
                    automato.getTransicoes().add(novaTransicao);
                }
            }
        }
    }
}
