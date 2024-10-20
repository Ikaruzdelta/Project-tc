package com.example.projecttc.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;

@Service
public class ReversoService {

    public Automato reverso(Automato automato) {
        ArrayList<Estado> estadosReverso = new ArrayList<>();
        ArrayList<Transicao> transicoesReverso = new ArrayList<>();
        ArrayList<Estado> estadosOriginais = (ArrayList<Estado>) automato.getEstados(); // Obtenção dos estados originais
        ArrayList<Transicao> transicoesOriginais = (ArrayList<Transicao>) automato.getTransicoes(); // Obtenção das transições originais

        // Cria uma cópia dos estados originais para o autômato reverso, invertendo os estados iniciais e finais
        for (Estado e : estadosOriginais) {
            Estado novoEstado = new Estado(e.getId(), e.getNome(), e.isFinal(), e.isInicial(), e.getX(), e.getY()); // Inverter inicial e final
            estadosReverso.add(novoEstado);
        }

        // Inverte as transições
        for (Transicao t : transicoesOriginais) {
            Estado origem = procurarEstadoReverso(t.getDestino().getId(), estadosReverso);
            Estado destino = procurarEstadoReverso(t.getOrigem().getId(), estadosReverso);

            Transicao transicaoInvertida = new Transicao(origem, destino, t.getSimbolo());
            transicoesReverso.add(transicaoInvertida);
        }

        // Ajusta os estados iniciais e finais no autômato reverso
        ajustarEstadosIniciaisEFinais(estadosOriginais, estadosReverso);

        // Verifica se há mais de um estado final e ajusta se necessário
        adicionarEstadoInicialComTransicoesLambda(estadosOriginais,estadosReverso, transicoesReverso);

        return new Automato("reverso", estadosReverso, transicoesReverso);
    }

    private Estado procurarEstadoReverso(int id, ArrayList<Estado> estadosReverso) {
        for (Estado e : estadosReverso) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    private void ajustarEstadosIniciaisEFinais(ArrayList<Estado> estadosOriginais, ArrayList<Estado> estadosReverso) {
        for (Estado e : estadosReverso) {
            Estado estadoOriginal = procurarEstadoOriginal(e.getId(), estadosOriginais);

            if (estadoOriginal.isFinal()) {
                e.setInicial(true);  
            }

            if (estadoOriginal.isInicial()) {
                e.setFinal(true);  
            } else {
                e.setInicial(false); 
            }
        }
    }

    private Estado procurarEstadoOriginal(int id, ArrayList<Estado> estadosOriginais) {
        for (Estado e : estadosOriginais) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    private void adicionarEstadoInicialComTransicoesLambda(ArrayList<Estado> estadosOriginais,ArrayList<Estado> estadosReverso, ArrayList<Transicao> transicoesReverso) {
        int numFinais = 0;
        ArrayList<Estado> estadosFinais = new ArrayList<>();
        Estado estadoInicialOriginal = null;

        // Identifica os estados finais e o estado inicial
        for (Estado e : estadosOriginais) {
            if (e.isFinal()) {
                numFinais++;
                estadosFinais.add(e); 
            }
            if (e.isInicial()) {
                estadoInicialOriginal = e;
            }
        }

        // Se houver mais de um estado final, cria um novo estado inicial
        if (numFinais > 1 && estadoInicialOriginal != null) {
            Estado novoEstadoInicial = new Estado(estadosReverso.size(), "qNovo", true, false, 50, 50); // qNovo como inicial
            estadosReverso.add(novoEstadoInicial);

            // Adiciona transições λ do novo estado para os estados finais existentes
            for (Estado estadoFinal : estadosFinais) {
                Transicao transicaoLambda = new Transicao(novoEstadoInicial, estadoFinal, "λ");
                transicoesReverso.add(transicaoLambda);
                estadoFinal.setFinal(false); 
            }

            // Marca o estado inicial original como final
            estadoInicialOriginal.setFinal(true);
            estadoInicialOriginal.setInicial(false);
        }
    }
}