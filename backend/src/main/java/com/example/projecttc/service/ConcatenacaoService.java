package com.example.projecttc.service;

import java.io.File;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.projecttc.utils.GravarXML;
import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;
import com.example.projecttc.utils.JFFParser;

@Service
public class ConcatenacaoService {

    // Método principal para concatenar dois autômatos
    public void concatenar(MultipartFile file1, MultipartFile file2, String outputFilePath) throws Exception {
        int spacing = 200; // Define o espaçamento entre os autômatos

        // Salva os arquivos recebidos temporariamente
        File tempFile1 = File.createTempFile("automato1", ".jff");
        file1.transferTo(tempFile1);

        File tempFile2 = File.createTempFile("automato2", ".jff");
        file2.transferTo(tempFile2);

        // Parsing dos arquivos XML para criar os autômatos
        Automato automato1 = JFFParser.parse(tempFile1);
        Automato automato2 = JFFParser.parse(tempFile2);

        // ArrayLists que serão preenchidos com os dados dos autômatos
        ArrayList<Estado> lstEstados1 = (ArrayList<Estado>) automato1.getEstados();
        ArrayList<Transicao> lstTransicoes1 = (ArrayList<Transicao>) automato1.getTransicoes();
        ArrayList<Estado> lstEstados2 = (ArrayList<Estado>) automato2.getEstados();
        ArrayList<Transicao> lstTransicoes2 = (ArrayList<Transicao>) automato2.getTransicoes();

        // Lista para armazenar os estados finais do primeiro autômato
        ArrayList<Estado> finalStates = new ArrayList<>();
        // Ajusta o valor máximo de x do primeiro autômato para o posicionamento do segundo
        double maxXFirstAutomaton = getMaxXAndFinalStates(lstEstados1, finalStates);

        // Calcula o offset dos IDs dos estados para o segundo autômato
        int stateIdOffset = lstEstados1.size();

        // Adiciona os estados do segundo autômatos com IDs e posições ajustadas
        ArrayList<Estado> novosEstados = updateStates(lstEstados1, lstEstados2, stateIdOffset, maxXFirstAutomaton, spacing);

        // Atualiza as transições combinadas e retorna a lista
        ArrayList<Transicao> novasTransicoes = updateTransitions(lstTransicoes1, lstTransicoes2, novosEstados, stateIdOffset);

        // Cria as transições de lambda entre os estados finais do primeiro autômato e o estado inicial do segundo
        createLambdaTransitions(lstEstados1, lstEstados2, novosEstados, novasTransicoes, stateIdOffset);

        // Grava o arquivo concatenado
        GravarXML gravador = new GravarXML();
        gravador.gravarAutomato(novosEstados, novasTransicoes, outputFilePath);
    }

    // Método auxiliar para obter o valor máximo de X e coletar os estados finais
    private double getMaxXAndFinalStates(ArrayList<Estado> estados, ArrayList<Estado> finalStates) {
        double maxX = 0;
        for (Estado estado : estados) {
            if (estado.isFinal()) {
                finalStates.add(estado); // Armazena os estados finais
            }
            if (estado.getX() > maxX) {
                maxX = estado.getX(); // Obtém o valor máximo de X
            }
        }
        return maxX;
    }

    // Método auxiliar para atualizar os estados do segundo autômato e retornar os estados combinados
    private ArrayList<Estado> updateStates(ArrayList<Estado> estados1, ArrayList<Estado> estados2, int stateIdOffset, double maxX, int spacing) {
        // Primeiro, mantemos os estados do primeiro autômato intactos.
        ArrayList<Estado> allStates = new ArrayList<>(estados1);

        // Em seguida, criamos cópias dos estados do segundo autômato, ajustamos e adicionamos.
        for (Estado estado : estados2) {
            // Cria uma cópia do estado atual do segundo autômato
            Estado estadoCopia = new Estado(estado);

            // Ajusta o ID e o nome do estado para evitar conflitos de IDs
            estadoCopia.setId(estado.getId() + stateIdOffset);
            estadoCopia.setNome("q" + estadoCopia.getId());

            // Atualiza a posição X para afastar o segundo autômato
            estadoCopia.setX(estado.getX() + maxX + spacing);

            // Mantém a propriedade de estado final do segundo autômato
            estadoCopia.setFinal(estado.isFinal());

            // Se for estado inicial, não precisa mais ser inicial (mantemos o inicial do primeiro autômato)
            estadoCopia.setInicial(false);

            // Adiciona o estado do segundo autômato à lista de estados combinados
            allStates.add(estadoCopia);
        }

        // Retorna a lista com os estados combinados
        return allStates;
    }

    // Método auxiliar para atualizar e retornar as transições combinadas dos dois autômatos
    private ArrayList<Transicao> updateTransitions(ArrayList<Transicao> transicoes1, ArrayList<Transicao> transicoes2, ArrayList<Estado> novosEstados, int stateIdOffset) {
        // Inicializa a lista combinada com as transições do primeiro autômato
        ArrayList<Transicao> combinedTransitions = new ArrayList<>(transicoes1);

        for (Transicao transicao : transicoes2) {
            // Calcula o novo índice "de" e "para" com base no offset
            int deIndex = transicao.getOrigem().getId() + stateIdOffset;
            int paraIndex = transicao.getDestino().getId() + stateIdOffset;

            // Busca os estados ajustados nos novosEstados usando os IDs calculados
            Estado deEstado = EstadoService.getEstadoById(novosEstados, deIndex);
            Estado paraEstado = EstadoService.getEstadoById(novosEstados, paraIndex);

            // Se ambos os estados forem encontrados, cria uma nova transição
            if (deEstado != null && paraEstado != null) {
                Transicao novaTransicao = new Transicao(deEstado, paraEstado, transicao.getSimbolo());
                // Adiciona a nova transição à lista de transições combinadas
                combinedTransitions.add(novaTransicao);
            }
        }

        return combinedTransitions;
    }

    // Método auxiliar para criar as transições de lambda entre os estados finais e o inicial do segundo autômato
    private void createLambdaTransitions(ArrayList<Estado> lstEstados1, ArrayList<Estado> lstEstados2, ArrayList<Estado> novosEstados, ArrayList<Transicao> novasTransicoes, int stateIdOffset) {
        // Itera sobre os novos estados
        for (Estado estadoNovo : novosEstados) {
            // Verifica se o estado faz parte do primeiro autômato e se é um estado final
            for (Estado estado1 : lstEstados1) {
                if (estadoNovo.getId() == estado1.getId() && estado1.isFinal()) {
                    // Define o estado como não final (pois será conectado ao próximo autômato)
                    estadoNovo.setFinal(false);

                    // Itera sobre os estados do segundo autômato para encontrar o inicial
                    for (Estado estado2 : lstEstados2) {
                        if (estado2.isInicial()) {
                            // Obtem o índice do estado inicial do segundo autômato ajustado com o offset
                            int estado2Index = estado2.getId();

                            // Encontra o estado inicial ajustado no novo conjunto de estados
                            Estado estadoInicial2 = EstadoService.getEstadoById(novosEstados, estado2Index + stateIdOffset);

                            // Cria uma nova transição de lambda do estado final do primeiro autômato
                            // para o estado inicial do segundo autômato
                            Transicao lambdaTransition = new Transicao(estadoNovo, estadoInicial2, "λ");

                            // Adiciona a transição de lambda à lista de novas transições
                            novasTransicoes.add(lambdaTransition);

                            // Define o estado inicial do segundo autômato como não inicial
                            estadoInicial2.setInicial(false);
                        }
                    }
                }
            }
        }
    }
}
