package com.example.projecttc.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;

@Service
public class EstrelaService {
    public Automato estrela(Automato automato) throws Exception {
        ArrayList<Estado> lstEstados = (ArrayList<Estado>) automato.getEstados();
        ArrayList<Transicao> lstTransicoes = (ArrayList<Transicao>) automato.getTransicoes();

        Estado inicialColetado = null;

        double deslocamentoX = 150.0; 
        for (Estado estado : lstEstados) {
            estado.setX(estado.getX() + deslocamentoX);
            if (estado.isInicial()) {
                estado.setInicial(false);
                inicialColetado = estado;
            }
        }

        if (inicialColetado == null) {
            throw new RuntimeException("Nenhum estado inicial foi encontrado no autômato.");
        }

        ArrayList<Estado> estadosFinais = new ArrayList<>();
        for (Estado estado : lstEstados) {
            if (estado.isFinal()) {
                estado.setFinal(false);
                estadosFinais.add(estado);
            }
        }

        Estado novoInicial = new Estado(-1, "S", true, false, inicialColetado.getX() - 200.0, inicialColetado.getY());
        lstEstados.add(novoInicial);

        Estado novoFinal = new Estado(-2, "F", false, true, inicialColetado.getX() + 200.0, inicialColetado.getY());
        lstEstados.add(novoFinal);

        lstTransicoes.add(new Transicao(novoInicial, inicialColetado, "λ"));
        lstTransicoes.add(new Transicao(novoInicial, novoFinal, "λ"));

        for (Estado estadoFinal : estadosFinais) {
            Transicao transicaoFinal = new Transicao(estadoFinal, novoFinal, "λ");
            Transicao antigoInicial = new Transicao(estadoFinal, inicialColetado, "λ");
            lstTransicoes.add(transicaoFinal);
            lstTransicoes.add(antigoInicial);
        }

        return new Automato("estrela",lstEstados, lstTransicoes);
    }
}
