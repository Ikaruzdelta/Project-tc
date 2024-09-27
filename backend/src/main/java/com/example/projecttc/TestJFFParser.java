package com.example.projecttc;

import java.io.File;

import com.example.projecttc.model.Automato;
import com.example.projecttc.utils.JFFParser;

public class TestJFFParser {

    public static void main(String[] args) {
        try {
            File file = new File("/Users/ikaruz/Downloads/i.jff");

            Automato automato = JFFParser.parse(file);
            System.out.println("Estados:");
            automato.getEstados().forEach(estado -> {
                System.out.println("ID: " + estado.getId() + ", Nome: " + estado.getNome() + ", Inicial: " + estado.isInicial() + ", Final: " + estado.isFinal());
            });

            System.out.println("\nTransições:");
            automato.getTransicoes().forEach(transicao -> {
                System.out.println("De: " + transicao.getOrigem().getNome() + ", Para: " + transicao.getDestino().getNome() + ", Símbolo: " + transicao.getSimbolo());
            });

        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
}
