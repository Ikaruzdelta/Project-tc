package com.example.projecttc.utils;
import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;

public class CompletarAfd {

    public static boolean isAFDCompleto(Automato automato) {
        for (Estado estado : automato.getEstados()) {

            // Verifica se para cada símbolo do alfabeto, há uma transição correspondente
            for (String simbolo : automato.getAlfabeto()) {
                boolean temTransicaoParaSimbolo = false;
                // Procura uma transição que corresponda ao símbolo
                for (Transicao transicao : estado.getTransicoes()) {

                    if (transicao.getSimbolo().equals(simbolo)) {
<<<<<<< HEAD
                        temTransicaoParaSimbolo = true;
                    }
                }
                // Se não houver transição para algum símbolo do alfabeto, o AFD não é completo
=======
                        temTransicaoParaSimbolo = true;;
                    }
                }
>>>>>>> origin/main
                if (!temTransicaoParaSimbolo) {

                    return false;
                }
            }
        }

        // Se todas as verificações passarem, o AFD é completo
        return true;
    }


    public static void deixarAFDCompleto(Automato automato) {
        // Criar o estado consumidor, garantindo que ele não tem transições saindo
        int novoId = GerarNovoId.gerarNovoId(automato);
        Estado estadoConsumidor = new Estado(novoId, "q"+novoId, false, false, 300, 300);

        // Adicionar o estado consumidor à lista de estados, se ainda não estiver presente
        if (!automato.getEstados().contains(estadoConsumidor)) {
            automato.getEstados().add(estadoConsumidor);
        }

        // Iterar sobre todos os estados
        for (Estado estado : automato.getEstados()) {
            // Iterar sobre cada símbolo do alfabeto
            for (String simbolo : automato.getAlfabeto()) {
                boolean temTransicao = false;

                // Verificar se já existe uma transição para o símbolo atual
                for (Transicao transicao : estado.getTransicoes()) {
                    if (transicao.getSimbolo().equalsIgnoreCase(simbolo)) {
                        temTransicao = true;
                        break; // Já tem transição para o símbolo, não precisa adicionar
                    }
                }

                // Se não houver transição para o símbolo atual, criamos uma nova transição para
                // o estado consumidor
                if (!temTransicao) {
                    Transicao novaTransicao = new Transicao(estado, estadoConsumidor, simbolo);
                    estado.getTransicoes().add(novaTransicao);
                    automato.getTransicoes().add(novaTransicao);
                }
            }
        }
    }
}
