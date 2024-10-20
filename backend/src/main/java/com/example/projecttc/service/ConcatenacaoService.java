package com.example.projecttc.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;

@Service
public class ConcatenacaoService {

    public Automato concatenacao(Automato automato1, Automato automato2) throws Exception {
        int spacing = 200; 

        ArrayList<Estado> lstEstados1 = (ArrayList<Estado>) automato1.getEstados();
        ArrayList<Transicao> lstTransicoes1 = (ArrayList<Transicao>) automato1.getTransicoes();
        ArrayList<Estado> lstEstados2 = (ArrayList<Estado>) automato2.getEstados();
        ArrayList<Transicao> lstTransicoes2 = (ArrayList<Transicao>) automato2.getTransicoes();

        ArrayList<Estado> finalStates = new ArrayList<>();

        double maxXFirstAutomaton = getMaxXAndFinalStates(lstEstados1, finalStates);

        int stateIdOffset = lstEstados1.size();

        ArrayList<Estado> novosEstados = updateStates(lstEstados1, lstEstados2, stateIdOffset, maxXFirstAutomaton, spacing);

        ArrayList<Transicao> novasTransicoes = updateTransitions(lstTransicoes1, lstTransicoes2, novosEstados, stateIdOffset);

        createLambdaTransitions(lstEstados1, lstEstados2, novosEstados, novasTransicoes, stateIdOffset);

        return new Automato("concatenacao",novosEstados,novasTransicoes);
    }

    private double getMaxXAndFinalStates(ArrayList<Estado> estados, ArrayList<Estado> finalStates) {
        double maxX = 0;
        for (Estado estado : estados) {
            if (estado.isFinal()) {
                finalStates.add(estado); 
            }
            if (estado.getX() > maxX) {
                maxX = estado.getX(); 
            }
        }
        return maxX;
    }

    private ArrayList<Estado> updateStates(ArrayList<Estado> estados1, ArrayList<Estado> estados2, int stateIdOffset, double maxX, int spacing) {
        ArrayList<Estado> allStates = new ArrayList<>(estados1);

        for (Estado estado : estados2) {
            Estado estadoCopia = new Estado(estado);

            estadoCopia.setId(estado.getId() + stateIdOffset);
            estadoCopia.setNome("q" + estadoCopia.getId());

            estadoCopia.setX(estado.getX() + maxX + spacing);

            estadoCopia.setFinal(estado.isFinal());

            estadoCopia.setInicial(false);

            allStates.add(estadoCopia);
        }

        return allStates;
    }

    private ArrayList<Transicao> updateTransitions(ArrayList<Transicao> transicoes1, ArrayList<Transicao> transicoes2, ArrayList<Estado> novosEstados, int stateIdOffset) {
        ArrayList<Transicao> combinedTransitions = new ArrayList<>(transicoes1);

        for (Transicao transicao : transicoes2) {
            int deIndex = transicao.getOrigem().getId() + stateIdOffset;
            int paraIndex = transicao.getDestino().getId() + stateIdOffset;

            Estado deEstado = EstadoService.getEstadoById(novosEstados, deIndex);
            Estado paraEstado = EstadoService.getEstadoById(novosEstados, paraIndex);

            if (deEstado != null && paraEstado != null) {
                Transicao novaTransicao = new Transicao(deEstado, paraEstado, transicao.getSimbolo());
                combinedTransitions.add(novaTransicao);
            }
        }

        return combinedTransitions;
    }

    private void createLambdaTransitions(ArrayList<Estado> lstEstados1, ArrayList<Estado> lstEstados2, ArrayList<Estado> novosEstados, ArrayList<Transicao> novasTransicoes, int stateIdOffset) {
        
        for (Estado estadoNovo : novosEstados) {
            for (Estado estado1 : lstEstados1) {
                if (estadoNovo.getId() == estado1.getId() && estado1.isFinal()) {
                    estadoNovo.setFinal(false);

                    for (Estado estado2 : lstEstados2) {
                        if (estado2.isInicial()) {            
                            int estado2Index = estado2.getId();

                            Estado estadoInicial2 = EstadoService.getEstadoById(novosEstados, estado2Index + stateIdOffset);

                            Transicao lambdaTransition = new Transicao(estadoNovo, estadoInicial2, "λ");
                            novasTransicoes.add(lambdaTransition);
                            estadoInicial2.setInicial(false);
                        }
                    }
                }
            }
        }
    }
}
