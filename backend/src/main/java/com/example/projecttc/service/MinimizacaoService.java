package com.example.projecttc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;

@Service
public class MinimizacaoService {
    public Automato minimizacao(Automato automato) {
        try {
            eliminarEstadosMortos(automato);
            HashMap<String, CombinedState> paresEstados = geraTabela((ArrayList<Estado>) automato.getEstados());
            marcarTrivialmenteNaoEquivalente(paresEstados, (ArrayList<Estado>) automato.getEstados());
            marcarNaoEquivalente(paresEstados, automato);
            ArrayList<Estado> novosEstados = unificarEstados(paresEstados, (ArrayList<Estado>) automato.getEstados());
            otimizarEstados(novosEstados);

            // Atualizar as transições com base nos novos estados
            ArrayList<Transicao> novasTransicoes = unificarTransicoes(novosEstados, (ArrayList<Transicao>) automato.getTransicoes(),(ArrayList<Estado>)automato.getEstados());

            return new Automato("minimizacao", novosEstados, novasTransicoes);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Passo 1: Gerar tabela de pares de estados
    public HashMap<String, CombinedState> geraTabela(ArrayList<Estado> estados) {
        HashMap<String, CombinedState> paresEstados = new HashMap<>();
        for (int i = 0; i < estados.size(); i++) {
            for (int j = i + 1; j < estados.size(); j++) {
                paresEstados.put(estados.get(i).getId() + "," + estados.get(j).getId(),
                        new CombinedState(estados.get(i), estados.get(j)));
            }
        }
        return paresEstados;
    }

    // Passo 2: Marcar estados trivialmente não equivalentes
    public void marcarTrivialmenteNaoEquivalente(HashMap<String, CombinedState> paresEstados, ArrayList<Estado> estados) {
        for (CombinedState pair : paresEstados.values()) {
            if (pair.getQu().isFinal() != pair.getQv().isFinal()) {
                pair.setEquivalent(false);
            }
        }
    }

    // Passo 3: Marcar estados não equivalentes
    public void marcarNaoEquivalente(HashMap<String, CombinedState> paresEstados, Automato automato) {
        boolean mudou;
        do {
            mudou = false;
            for (CombinedState pair : paresEstados.values()) {
                if (!pair.isEquivalent())
                    continue;

                for (String simbolo : automato.getAlfabeto()) {
                    Estado pu = obterEstadoDestino(pair.getQu(), simbolo, (ArrayList<Transicao>) automato.getTransicoes(),
                            (ArrayList<Estado>) automato.getEstados());
                    Estado pv = obterEstadoDestino(pair.getQv(), simbolo, (ArrayList<Transicao>) automato.getTransicoes(),
                            (ArrayList<Estado>) automato.getEstados());

                    if (pu == null || pv == null)
                        continue;

                    String chave = (pu.getId() < pv.getId()) ? pu.getId() + "," + pv.getId() : pv.getId() + "," + pu.getId();
                    CombinedState parPuPv = paresEstados.get(chave);

                    if (parPuPv != null && !parPuPv.isEquivalent()) {
                        pair.setEquivalent(false);
                        mudou = true;
                        break;
                    }
                }
            }
        } while (mudou);
    }

    public Estado obterEstadoDestino(Estado estado, String simbolo, ArrayList<Transicao> transicoes, ArrayList<Estado> estados) {
        for (Transicao transicao : transicoes) {
            if (transicao.getOrigem().getId() == estado.getId() && transicao.getSimbolo().equals(simbolo)) {
                for (Estado e : estados) {
                    if (e.getId() == transicao.getDestino().getId()) {
                        return e;
                    }
                }
            }
        }
        return null;
    }

    // Passo 4: Unificar estados equivalentes
    public ArrayList<Estado> unificarEstados(HashMap<String, CombinedState> paresEstados, ArrayList<Estado> estados) {
        Map<Integer, Estado> mapaEstados = new HashMap<>();
        for (Estado estado : estados) {
            mapaEstados.put(estado.getId(), estado);
        }

        for (CombinedState pair : paresEstados.values()) {
            if (pair.isEquivalent()) {
                Estado qu = mapaEstados.get(pair.getQu().getId());
                Estado qv = mapaEstados.get(pair.getQv().getId());

                if (qu == null || qv == null) {
                    System.out.println("Erro: Estado nulo detectado ao tentar combinar " + pair.getQu() + " e " + pair.getQv());
                    continue;
                }

                qu.setNome(qu.getNome() + "_" + qv.getNome());

                mapaEstados.remove(qv.getId());
            }
        }

        return new ArrayList<>(mapaEstados.values());
    }

    public void otimizarEstados(ArrayList<Estado> novosEstados) {
        // Pode ser implementada lógica adicional se necessário
    }

    public ArrayList<Transicao> unificarTransicoes(ArrayList<Estado> novosEstados, ArrayList<Transicao> transicoes, ArrayList<Estado> estadosAntigos) {
        // Mapa para armazenar o mapeamento entre o ID dos estados antigos e os novos estados unificados
        Map<Integer, Estado> mapaEstado = new HashMap<>();

        // Para cada novo estado, associamos os IDs dos estados antigos que foram unificados nele
        for (Estado estado : novosEstados) {
            String[] ids = String.valueOf(estado.getId()).split(" ");
            for (String idStr : ids) {
                int id = Integer.parseInt(idStr); // Converte o ID para inteiro
                mapaEstado.put(id, estado); // Mapeia o ID antigo para o novo estado
            }
        }

        

        ArrayList<Transicao> novasTransicoes = new ArrayList<>();
        ArrayList<Transicao> novasTransicoes1 = new ArrayList<>();
        // Para cada transição, atualizamos os estados de origem e destino com base no mapeamento dos novos estados
        for (Transicao transicao : transicoes) {
            Estado novoDe = mapaEstado.getOrDefault(transicao.getOrigem().getId(),getEstadoPorId(transicao.getOrigem().getId(), novosEstados));
            Estado novoPara = mapaEstado.getOrDefault(transicao.getDestino().getId(),getEstadoPorId(transicao.getDestino().getId(), novosEstados));

            // Cria uma nova transição com os novos estados, se aplicável
            Transicao novaTransicao = new Transicao(novoDe, novoPara, transicao.getSimbolo());

            // Evita duplicação de transições
            if (!novasTransicoes.contains(novaTransicao)) {
                novasTransicoes.add(novaTransicao);
            }
        }
        for (Transicao transicao: novasTransicoes){
            if ((transicao.getOrigem()!=null)&&(transicao.getDestino()!=null)){
                novasTransicoes1.add(transicao);
            }
        }
        return novasTransicoes1;
    }

    // Método auxiliar para obter um estado pelo ID na lista de estados antigos
    private Estado getEstadoPorId(int id, ArrayList<Estado> estados) {
        for (Estado estado : estados) {
            if (estado.getId() == id) {
                return estado;
            }
        }
        return null; // Retorna null se o estado não for encontrado
    }

    public void eliminarEstadosMortos(Automato automato) {
        Estado estadoInicial = null; // Inicializa a variável
        ArrayList<Estado> estadosFinais = new ArrayList<>(); // Inicializa a lista

        // Identificar o estado inicial
        for (Estado estado : automato.getEstados()) {
            if (estado.isInicial()) {
                estadoInicial = estado;
            }
        }

        // Identificar os estados finais
        for (Estado estado : automato.getEstados()) {
            if (estado.isFinal()) {
                estadosFinais.add(estado);
            }
        }

        if (estadoInicial == null || estadosFinais.isEmpty()) {
            System.out.println("Erro: Estado inicial ou final não definido.");
            return; // Evita a continuação sem estados válidos
        }

        // Identificar estados acessíveis a partir do estado inicial
        ArrayList<Integer> estadoAcessiveis = new ArrayList<>();
        identificarEstadosAcessiveis(estadoInicial, estadoAcessiveis, automato);

        // Identificar estados que podem alcançar os estados finais
        ArrayList<Integer> estadosMortos = new ArrayList<>();
        for (Estado estado : estadosFinais) {
            identificarEstadosQueAlcancamFinal(estado, estadosMortos, automato);
        }

        // Identificar os estados a serem removidos
        ArrayList<Estado> estadosRemover = new ArrayList<>();
        for (Estado estado : automato.getEstados()) {
            if (!estadoAcessiveis.contains(estado.getId())) {
                estadosRemover.add(estado);
            }
        }

        for (Estado estado : automato.getEstados()) {
            if (!estadosMortos.contains(estado.getId())) {
                estadosRemover.add(estado);
            }
        }
        if (estadosRemover.size()>0){
            // Remover os estados mortos
            automato.getEstados().removeAll(estadosRemover);
            // Remover as transições associadas aos estados removidos
            todasTransicoesMortas(estadosRemover, automato);
        }
    }

    public void todasTransicoesMortas(ArrayList<Estado> estadosMortos, Automato automato) {
        ArrayList<Transicao> transicoesMortas = new ArrayList<>();
        for (Transicao transicao : automato.getTransicoes()) {
            for (Estado estado : estadosMortos) {
                if (estado.getId() == transicao.getOrigem().getId() || estado.getId() == transicao.getDestino().getId()) {
                    transicoesMortas.add(transicao);
                }
            }
        }
        automato.getTransicoes().removeAll(transicoesMortas);
    }

    public void identificarEstadosAcessiveis(Estado estado, ArrayList<Integer> estadosAcessiveis, Automato automato) {
        if (estadosAcessiveis.contains(estado.getId())) {
            return;
        }

        estadosAcessiveis.add(estado.getId());

        for (Transicao transicao : automato.getTransicoes()) {
            if (transicao.getOrigem().getId() == estado.getId()) {
                Estado destino = transicao.getDestino();

                if (destino != null) {
                    identificarEstadosAcessiveis(destino, estadosAcessiveis, automato);
                }

            }
        }
    }

    public void identificarEstadosQueAlcancamFinal(Estado estado, ArrayList<Integer> estadosQueAlcancamFinal,
            Automato automato) {
        if (estadosQueAlcancamFinal.contains(estado.getId())) {
            return;
        }

        estadosQueAlcancamFinal.add(estado.getId());

        for (Transicao transicao : automato.getTransicoes()) {
            if (transicao.getOrigem().getId() == estado.getId()) {
                Estado origem = transicao.getDestino();
                if (origem != null) {
                    identificarEstadosQueAlcancamFinal(origem, estadosQueAlcancamFinal, automato);
                }
            }
        }
    }

    public class CombinedState {
        private Estado qu;
        private Estado qv;
        private boolean equivalent = true;
        private List<CombinedState> listaEstados = new ArrayList<>();

        public CombinedState(Estado qu, Estado qv) {
            this.qu = qu;
            this.qv = qv;
        }

        public Estado getQu() {
            return qu;
        }

        public Estado getQv() {
            return qv;
        }

        public boolean isEquivalent() {
            return equivalent;
        }

        public void setEquivalent(boolean equivalent) {
            this.equivalent = equivalent;
        }

        public List<CombinedState> getListaEstados() {
            return listaEstados;
        }

        public void setListaEstados(CombinedState combinedState) {
            this.listaEstados.add(combinedState);
        }

    }
}
