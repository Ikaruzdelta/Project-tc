package com.example.projecttc.service;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class EstrelaService {
    public Automato aplicarEstrela(Automato automato) throws Exception {
        // ArrayLists que serão preenchidos com os dados dos autômatos
        ArrayList<Estado> lstEstados = (ArrayList<Estado>) automato.getEstados();
        ArrayList<Transicao> lstTransicoes = (ArrayList<Transicao>) automato.getTransicoes();

        Estado inicialColetado = null;

        // Ajuste de posição para os estados à direita
        double deslocamentoX = 150.0; // Define o deslocamento à direita para todos os estados
        for (Estado estado : lstEstados) {
            estado.setX(estado.getX() + deslocamentoX);
            if (estado.isInicial()) {
                estado.setInicial(false);
                inicialColetado = estado;
            }
        }

        // Verificar se o estado inicial foi coletado
        if (inicialColetado == null) {
            throw new RuntimeException("Nenhum estado inicial foi encontrado no autômato.");
        }

        // Coletar estados finais
        ArrayList<Estado> estadosFinais = new ArrayList<>();
        for (Estado estado : lstEstados) {
            if (estado.isFinal()) {
                estadosFinais.add(estado);
            }
        }

        // Criar o novo estado inicial, mais à esquerda do antigo inicial
        Estado novoInicial = new Estado(-1, "S", true, false, inicialColetado.getX() - 200.0, inicialColetado.getY());
        lstEstados.add(novoInicial);

        // Criar transições de lambda do novo estado inicial para o antigo inicial
        lstTransicoes.add(new Transicao(novoInicial, inicialColetado, "λ"));

        // Criar transições de lambda dos estados finais para o antigo inicial
        for (Estado estadoFinal : estadosFinais) {
            Transicao transicaoFinal = new Transicao(estadoFinal, inicialColetado, "λ");
            lstTransicoes.add(transicaoFinal);
        }

        return new Automato("estrela",lstEstados, lstTransicoes);
    }
}
