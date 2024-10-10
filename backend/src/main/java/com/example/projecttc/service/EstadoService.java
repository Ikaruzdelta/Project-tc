package com.example.projecttc.service;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EstadoService {

    // Método para buscar um estado pela sua id
    public static Estado getEstadoById(ArrayList<Estado> estados, int id) {
        for (Estado estado : estados) {
            if (estado.getId() == id) {
                return estado;
            }
        }
        return null; // Retorna null se nenhum estado com o ID for encontrado
    }

    public static void carregarTransicoesEstado(Automato automato) {
        for (Estado estado : automato.getEstados()) {
            estado.getTransicoes().clear();
            for (Transicao transicao : automato.getTransicoes()) {
                if (transicao.getOrigem().equals(estado)) {
                    estado.getTransicoes().add(transicao);
                }
            }
        }
    }

}
