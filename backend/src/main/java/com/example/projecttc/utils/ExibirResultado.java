package com.example.projecttc.utils;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;

public class ExibirResultado {

    public static String exibirResultado(Automato automato) {
        StringBuilder resultado = new StringBuilder();
        resultado.append("Resultado\n\n");

        // Exibir os estados
        resultado.append("Estados:\n");
        for (Estado estado : automato.getEstados()) {
            resultado.append("ID: ").append(estado.getId())
                    .append(", Nome: ").append(estado.getNome())
                    .append(", Inicial: ").append(estado.isInicial())
                    .append(", Final: ").append(estado.isFinal()).append("\n");
        }

        // Exibir as transições
        resultado.append("\nTransições:\n");
        for (Transicao transicao : automato.getTransicoes()) {
            resultado.append("De: ").append(transicao.getOrigem().getNome())
                    .append(", Para: ").append(transicao.getDestino().getNome())
                    .append(", Símbolo: ").append(transicao.getSimbolo()).append("\n");
        }

        return resultado.toString();
    }
 
}