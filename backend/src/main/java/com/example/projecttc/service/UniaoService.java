package com.example.projecttc.service;

import java.io.File;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;
import com.example.projecttc.utils.GravarXML;
import com.example.projecttc.utils.JFFParser;

@Service
public class UniaoService {
    public void UnirAFN(MultipartFile file1, MultipartFile file2, String outputFilePath) throws Exception {
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

        ArrayList<Estado> estadosIniciais = new ArrayList<>();
        ArrayList<Estado> estadosFinais = new ArrayList<>();

        // Determinar o menor ID existente para atribuir ao novo inicial
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

        // Ajuste das posições dos estados
        double yOffset = 200; // Deslocamento vertical para o segundo autômato
        for (Estado estado : lstEstados2) {
            estado.setY(estado.getY() + yOffset);
        }

        // Criação dos novos estados, o novo inicial e o novo final
        Estado novoInicial = new Estado(menorId, "q" + menorId, true, false, 0, 0);

        // Ajusta os IDs dos estados da lista 1 para evitar conflitos (ID + 1)
        for (Estado estado : lstEstados1) {
            estado.setId(estado.getId() + 1);
            estado.setNome("q" + estado.getId());
        }

        // Ajusta os IDs dos estados da lista 2 com base no tamanho da lista 1 + 1
        int tamanhoLista1 = lstEstados1.size();
        for (Estado estado : lstEstados2) {
            estado.setId(estado.getId() + tamanhoLista1 + 1);
            estado.setNome("q" + estado.getId());
        }

        // Encontra a posição x máxima dos estados para posicionar o novo estado final
        double maxX = Double.MIN_VALUE;
        for (Estado estado : lstEstados1) {
            if (estado.getX() > maxX) maxX = estado.getX();
        }
        for (Estado estado : lstEstados2) {
            if (estado.getX() > maxX) maxX = estado.getX();
        }

        // Criação do novo estado final com o valor de x 150 unidades à direita do estado mais à direita
        int totalEstados = tamanhoLista1 + lstEstados2.size() + 1; 
        Estado novoFinal = new Estado(totalEstados, "q" + totalEstados, false, true, maxX + 150, 0);

        // Ajusta as posições verticais do novo inicial e do novo final
        novoInicial.setY((estadosIniciais.get(0).getY() + estadosIniciais.get(1).getY()) / 2);
        novoFinal.setY(novoInicial.getY());
        
        // Transições do novo inicial para os antigos iniciais
        Transicao transicaoEpsilon1 = new Transicao(novoInicial, estadosIniciais.get(0), "");
        estadosIniciais.get(0).setInicial(false);
        Transicao transicaoEpsilon2 = new Transicao(novoInicial, estadosIniciais.get(1), "");
        estadosIniciais.get(1).setInicial(false);

        ArrayList<Transicao> transicaoParaNovoFinal = new ArrayList<>();

        // Transições dos antigos finais para o novo final
        for (Estado eF : estadosFinais) {
            transicaoParaNovoFinal.add(new Transicao(eF, novoFinal, ""));
            eF.setFinal(false);
        }

        // Criação dos arrays finais de estados e transições
        ArrayList<Estado> estados = new ArrayList<>();
        ArrayList<Transicao> transicoes = new ArrayList<>();

        // Adiciona todos os estados e transições ao array
        estados.add(novoInicial);
        estados.addAll(lstEstados1);
        estados.addAll(lstEstados2);
        estados.add(novoFinal);

        transicoes.add(transicaoEpsilon1);
        transicoes.add(transicaoEpsilon2);
        transicoes.addAll(lstTransicoes1);
        transicoes.addAll(lstTransicoes2);
        transicoes.addAll(transicaoParaNovoFinal);

        // Grava o autômato resultante em XML
        GravarXML gravador = new GravarXML();
        gravador.gravarAutomato(estados, transicoes, outputFilePath);
    }
}
