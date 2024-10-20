package com.example.projecttc.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Transicao;

@Service
public class EstadoService {

    public static Estado getEstadoById(ArrayList<Estado> estados, int id) {
        for (Estado estado : estados) {
            if (estado.getId() == id) {
                return estado;
            }
        }
        return null;
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
