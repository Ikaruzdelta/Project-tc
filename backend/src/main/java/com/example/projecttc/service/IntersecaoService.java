package com.example.projecttc.service;

import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.stereotype.Service;
import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;
import com.example.projecttc.utils.CompletarAfd;
import com.example.projecttc.utils.ValidacaoAlfabeto;
@Service
public class IntersecaoService {

    public Automato intersecaoAFN(Automato automato1, Automato automato2) {
        ArrayList<Estado> estadosInterseccao = new ArrayList<>();
        ArrayList<Transicao> transicoesInterseccao = new ArrayList<>();
        HashSet<String> estadosVisitados = new HashSet<>();

        for (Estado e1 : automato1.getEstados()) {
            for (Estado e2 : automato2.getEstados()) {
                String nomeNovoEstado = e1.getNome() + "_" + e2.getNome();
    
                Estado estadoExistente = procurarEstadoInterseccao(e1, e2, estadosInterseccao);
                if (estadoExistente == null) {  
                    boolean isInicial = e1.isInicial() && e2.isInicial(); 
                    boolean isFinal = e1.isFinal() && e2.isFinal();
                    Estado novoEstado = new Estado(estadosInterseccao.size(), nomeNovoEstado, isInicial, isFinal, 0.0, 0.0); 
                    estadosInterseccao.add(novoEstado);
                    estadosVisitados.add(novoEstado.getNome());
                }

                for (Transicao t1 : automato1.getTransicoes()) {
                    if (t1.getOrigem().equals(e1)) {  
                        for (Transicao t2 : automato2.getTransicoes()) {
                            if (t2.getOrigem().equals(e2) && t1.getSimbolo().equals(t2.getSimbolo())) {  
                                Estado destino1 = t1.getDestino();
                                Estado destino2 = t2.getDestino();
    
                                if (destino1 != null && destino2 != null) {
                                    Estado novoEstadoDestino = procurarEstadoInterseccao(destino1, destino2, estadosInterseccao);
                                    if (novoEstadoDestino == null) {
                                        boolean isInicialDestino = destino1.isInicial() && destino2.isInicial(); 
                                        boolean isFinalDestino = destino1.isFinal() && destino2.isFinal(); 
                                        novoEstadoDestino = new Estado(estadosInterseccao.size(), destino1.getNome() + "_" + destino2.getNome(), isInicialDestino, isFinalDestino,0, 0); 

                                        estadosInterseccao.add(novoEstadoDestino);
                                        estadosVisitados.add(novoEstadoDestino.getNome());
                                    }

                                    Estado origemInterseccao = procurarEstadoInterseccao(t1.getOrigem(), t2.getOrigem(), estadosInterseccao);
                                    if (origemInterseccao != null) {
                                        Transicao transicaoInterseccao = new Transicao(origemInterseccao, novoEstadoDestino, t1.getSimbolo());
                                        transicoesInterseccao.add(transicaoInterseccao);
                                    } else {
                                        System.out.println("Erro: Estado de origem null na nova transição.");
                                    }
                                } else {
                                    System.out.println("Ignorando transição: Estado de destino inexistente em ambos os autômatos.");
                                }
                            }
                        }
                    }
                }
            }
        }

        Automato interseccaoAFN = new Automato("interseccaoAFN", estadosInterseccao, transicoesInterseccao);
        return removerEstadosSemTransicoes(interseccaoAFN); // Retornando o automato interseccao após limpar estados sem transições
    }

    private Automato removerEstadosSemTransicoes(Automato automato) {
        HashSet<String> estadosComTransicoes = new HashSet<>();

        for (Transicao transicao : automato.getTransicoes()) {
            estadosComTransicoes.add(transicao.getOrigem().getNome());
            estadosComTransicoes.add(transicao.getDestino().getNome());
        }

        automato.getEstados().removeIf(estado -> !estadosComTransicoes.contains(estado.getNome()));
        return automato;
    }

    private Estado procurarEstadoInterseccao(Estado e1, Estado e2, ArrayList<Estado> estadosInterseccao) {
        String nome = e1.getNome() + "_" + e2.getNome();
        for (Estado estado : estadosInterseccao) {
            if (estado.getNome().equals(nome)) {
                return estado;
            }
        }
        return null;
    }



    public Automato intersecaoAFD(Automato automato1,Automato automato2){

        if(ValidacaoAlfabeto.compararAlfabeto(automato1.getAlfabeto(),automato2.getAlfabeto())) {
            ArrayList<Estado> novosEstados = new ArrayList<Estado>();
            ArrayList<Transicao> novasTransicoes = new ArrayList<Transicao>();
            if (!CompletarAfd.isAFDCompleto(automato1)){
                CompletarAfd.deixarAFDCompleto(automato1);
            }
            if (!CompletarAfd.isAFDCompleto(automato2)) {
                CompletarAfd.deixarAFDCompleto(automato2);
           }
            // Fazer as combinacoes entre estados
            for (Estado estado1 : automato1.getEstados()) {
                for (Estado estado2 : automato2.getEstados()) {

                    if (estado1.isInicial() && estado2.isInicial()) {
                        //eh inicial e final
                        if (estado1.isFinal() && estado2.isFinal()) {
                            System.out.println(estado1.getNome());
                            System.out.println(estado2.getNome());
                            System.out.println("Adicionou inicial&final");
                            novosEstados.add(new Estado(novosEstados.size(),estado1.getNome() + ";" + estado2.getNome(),true,  true, estado1.getX(), estado2.getY()));
                        } else {
                            System.out.println(estado1.getNome());
                            System.out.println(estado2.getNome());
                            System.out.println("so eh inicial");
                            //eh inicial
                            novosEstados.add(new Estado(novosEstados.size(),estado1.getNome() + ";" + estado2.getNome(), true, false,estado1.getX(), estado2.getY()));
                        }
                    } else {
                        //eh fim
                        if (estado1.isFinal() && estado2.isFinal()) {
                            System.out.println(estado1.getNome());
                            System.out.println(estado2.getNome());
                            System.out.println("So eh final");
                            novosEstados.add(new Estado(novosEstados.size(),estado1.getNome() + ";" + estado2.getNome(), false, true, estado1.getX(), estado2.getY()));
                        } else {
                            System.out.println(estado1.getNome());
                            System.out.println(estado2.getNome());
                            //eh normal
                            System.out.println("Nao eh nada");
                            novosEstados.add(new Estado(novosEstados.size(), estado1.getNome() + ";" + estado2.getNome(),false, false, estado1.getX(), estado2.getY()));
                        }
                    }
                }
            }


            // Percorre todas as combinações dos novos estados
            for (Estado estado : novosEstados) {
                System.out.println("Combinação dos estados: " + estado.getNome());

                String[] nomes = estado.getNome().split(";"); // Dividir o nome do estado combinado para recuperar os dois estados originais

                //Pegar a referencia de cada um individualmente em suas respectivas listas
                Estado estado1 = automato1.getEstados().stream().filter(e -> e.getNome().equals(nomes[0])).findFirst().get();
                Estado estado2 = automato2.getEstados().stream().filter(e -> e.getNome().equals(nomes[1])).findFirst().get();
                System.out.println("Estado 1: " + estado1.getNome());
                System.out.println("Estado 2: " + estado1.getNome());
                //se achou
                if (estado1 != null && estado2 != null) {
                    // Pega as transições específicas de cada estado e joga num array
                    ArrayList<Transicao> transicoesEstado1 = (ArrayList<Transicao>)estado1.getTransicoes();
                    System.out.println("Transições do estado " + estado1.getNome());
                    for (Transicao t : transicoesEstado1) {
                        System.out.println(" " + t.getOrigem().getNome() + " --" + t.getSimbolo() + " --> " + t.getDestino().getNome());
                    }

                    ArrayList<Transicao> transicoesEstado2 = (ArrayList<Transicao>) estado2.getTransicoes();
                    System.out.println("Transições do estado " + estado2.getNome());
                    for (Transicao t : transicoesEstado2) {
                        System.out.println(" " + t.getOrigem().getNome() + " --" + t.getSimbolo() + " --> " + t.getDestino().getNome());
                    }
                    System.out.println("\n\n");
                    //commprara as transições entre os dois estados
                    for (Transicao transicao1 : transicoesEstado1) {
                        for (Transicao transicao2 : transicoesEstado2) {

                            System.out.println(" " + transicao1.getOrigem().getNome() + " --" + transicao1.getSimbolo() + "--> " + transicao1.getDestino().getNome());
                            System.out.println(" " + transicao2.getOrigem().getNome() + " --" + transicao2.getSimbolo() + "--> " + transicao2.getDestino().getNome());

                            //se as duas transições são iguais
                            if (transicao1.getSimbolo().equals(transicao2.getSimbolo())) {
                                System.out.println("Entrou! Símbolo: " + transicao1.getSimbolo());

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
            return new Automato("interseccaoAFD",novosEstados,novasTransicoes);
        } else{
            System.out.println("Nao eh possivel realizar a interseccao entre automatos cujos alfabetos nao sao totalmente semelhantes");
        }
        return null;
    }
    private Estado encontrarNovoEstado(ArrayList<Estado> novosEstados, Estado estado1, Estado estado2) {
        for (Estado estado : novosEstados) {
            // Verifica se o estado novo existe com base na combinação dos nomes dos dois estados originais
            if (estado.getNome().equals(estado1.getNome() + ";"+estado2.getNome())) {
                System.out.println("entrou!");
                return estado;
            }
        }
        // Retorna null se o estado não for encontrado
        return null;
    }
}

