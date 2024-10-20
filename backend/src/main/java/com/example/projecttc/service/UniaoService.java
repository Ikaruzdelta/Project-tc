package com.example.projecttc.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;
import com.example.projecttc.utils.CompletarAfd;
import com.example.projecttc.utils.ValidacaoAlfabeto;

@Service
public class UniaoService {
    public Automato uniaoAFN(Automato automato1, Automato automato2) throws Exception {
        // ArrayLists que serão preenchidos com os dados dos autômatos
        ArrayList<Estado> lstEstados1 = (ArrayList<Estado>) automato1.getEstados();
        ArrayList<Transicao> lstTransicoes1 = (ArrayList<Transicao>) automato1.getTransicoes();
        ArrayList<Estado> lstEstados2 = (ArrayList<Estado>) automato2.getEstados();
        ArrayList<Transicao> lstTransicoes2 = (ArrayList<Transicao>) automato2.getTransicoes();

        ArrayList<Estado> estadosIniciais = new ArrayList<>();
        ArrayList<Estado> estadosFinais = new ArrayList<>();

        int menorId = Integer.MAX_VALUE;

        for (Estado lE1 : lstEstados1) {
            if (lE1.getId() < menorId) menorId = lE1.getId();
            if (lE1.isInicial()) estadosIniciais.add(lE1);
            if (lE1.isFinal()) estadosFinais.add(lE1);
        }

        for (Estado lE2 : lstEstados2) {
            if (lE2.getId() < menorId) menorId = lE2.getId();
            if (lE2.isInicial()) estadosIniciais.add(lE2);
            if (lE2.isFinal()) estadosFinais.add(lE2);
        }

        double yOffset = 200; 
        for (Estado estado : lstEstados2) {
            estado.setY(estado.getY() + yOffset);
        }

        Estado novoInicial = new Estado(menorId, "q" + menorId, true, false, 0, 0);

        for (Estado estado : lstEstados1) {
            estado.setId(estado.getId() + 1);
            estado.setNome("q" + estado.getId());
        }

        int tamanhoLista1 = lstEstados1.size();
        for (Estado estado : lstEstados2) {
            estado.setId(estado.getId() + tamanhoLista1 + 1);
            estado.setNome("q" + estado.getId());
        }

        double maxX = Double.MIN_VALUE;
        for (Estado estado : lstEstados1) {
            if (estado.getX() > maxX) maxX = estado.getX();
        }
        for (Estado estado : lstEstados2) {
            if (estado.getX() > maxX) maxX = estado.getX();
        }

        int totalEstados = tamanhoLista1 + lstEstados2.size() + 1; 
        Estado novoFinal = new Estado(totalEstados, "q" + totalEstados, false, true, maxX + 150, 0);

        novoInicial.setY((estadosIniciais.get(0).getY() + estadosIniciais.get(1).getY()) / 2);
        novoFinal.setY(novoInicial.getY());

        Transicao transicaoEpsilon1 = new Transicao(novoInicial, estadosIniciais.get(0), "");
        estadosIniciais.get(0).setInicial(false);
        Transicao transicaoEpsilon2 = new Transicao(novoInicial, estadosIniciais.get(1), "");
        estadosIniciais.get(1).setInicial(false);

        ArrayList<Transicao> transicaoParaNovoFinal = new ArrayList<>();

        for (Estado eF : estadosFinais) {
            transicaoParaNovoFinal.add(new Transicao(eF, novoFinal, ""));
            eF.setFinal(false);
        }

        ArrayList<Estado> estados = new ArrayList<>();
        ArrayList<Transicao> transicoes = new ArrayList<>();

        estados.add(novoInicial);
        estados.addAll(lstEstados1);
        estados.addAll(lstEstados2);
        estados.add(novoFinal);

        transicoes.add(transicaoEpsilon1);
        transicoes.add(transicaoEpsilon2);
        transicoes.addAll(lstTransicoes1);
        transicoes.addAll(lstTransicoes2);
        transicoes.addAll(transicaoParaNovoFinal);
        return new Automato("UniaoAFN",estados,transicoes);
    }
    public Automato uniaoAFD(Automato automato1, Automato automato2) {

        if(ValidacaoAlfabeto.compararAlfabeto(automato1.getAlfabeto(),automato2.getAlfabeto())) {
            ArrayList<Estado> novosEstados = new ArrayList<Estado>();
            ArrayList<Transicao> novasTransicoes = new ArrayList<Transicao>();
            if(!CompletarAfd.isAFDCompleto(automato1)){
                CompletarAfd.deixarAFDCompleto(automato1);
            }
            if(!CompletarAfd.isAFDCompleto(automato2)){
                CompletarAfd.deixarAFDCompleto(automato2);
            }
            for (Estado estado1 : automato1.getEstados()) {
                for (Estado estado2 : automato2.getEstados()) {
                    if (!estado1.isInicial() || !estado2.isInicial()) {
                        if (estado1.isFinal() || estado2.isFinal()) {
                            novosEstados.add(new Estado(novosEstados.size(), estado1.getNome() + ";" + estado2.getNome(),  false, true, estado1.getX(), estado1.getY()));
                        } else {
                            novosEstados.add(new Estado(novosEstados.size(),estado1.getNome() + ";" + estado2.getNome(), false, false, estado1.getX(), estado1.getY()));
                        }
                    } else {
                        if (estado1.isFinal() || estado2.isFinal()) {
                            novosEstados.add(new Estado(novosEstados.size(), estado1.getNome() + ";" + estado2.getNome(), true, true, estado1.getX(), estado1.getY()));
                        } else {
                            novosEstados.add(new Estado(novosEstados.size(), estado1.getNome() + ";" + estado2.getNome(), true, false, estado1.getX(), estado1.getY()));
                        }
                    }
                }
            }

            // Percorre todas as combinações dos novos estados
            for (Estado estado : novosEstados) {

                String[] nomes = estado.getNome().split(";"); // Dividir o nome do estado combinado para recuperar os dois estados originais
                //Pegar a referencia de cada um individualmente em suas respectivas listas
                Estado estado1 = automato1.getEstados().stream().filter(e -> e.getNome().equals(nomes[0])).findFirst().get();
                Estado estado2 = automato2.getEstados().stream().filter(e -> e.getNome().equals(nomes[1])).findFirst().get();
            
                //se achou
                if (estado1 != null && estado2 != null) {
                    // Pega as transições específicas de cada estado e joga num array para saber se está atualizado
                    ArrayList<Transicao> transicoesEstado1 = (ArrayList<Transicao>) estado1.getTransicoes();
                    ArrayList<Transicao> transicoesEstado2 =(ArrayList<Transicao>) estado2.getTransicoes();

                    //commprara as transições entre os dois estados
                    for (Transicao transicao1 : transicoesEstado1) {
                        for (Transicao transicao2 : transicoesEstado2) {
                            //se as duas transições são iguais
                            if (transicao1.getSimbolo().equals(transicao2.getSimbolo())) {
                                //vejo pra onde vai individualmente e retornar a combinação a partir das combinações existentes
                                Estado estadoDestino = encontrarNovoEstado(novosEstados, transicao1.getDestino(), transicao2.getDestino());
                                if (estadoDestino != null) {
                                    novasTransicoes.add(new Transicao(estado, estadoDestino, transicao1.getSimbolo()));
                                }
                            }
                        }
                    }
                }
            }
        return new Automato("UniaoAFD",novosEstados,novasTransicoes);
        }
        else{
            System.out.println("Nao eh possivel realizar a uniao entre automatos cujos alfabetos nao sao totalmente semelhantes");
        }
        return null;
    }
    private Estado encontrarNovoEstado(ArrayList<Estado> novosEstados, Estado estado1, Estado estado2) {
        for (Estado estado : novosEstados) {
            // Verifica se o estado novo existe com base na combinação dos nomes dos dois estados originais
            if (estado.getNome().equals(estado1.getNome() + ";"+estado2.getNome())) {
                return estado;
            }
        }
        // Retorna null se o estado não for encontrado
        return null;
    }
}
