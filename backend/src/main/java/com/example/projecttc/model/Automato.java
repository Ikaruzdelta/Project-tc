package com.example.projecttc.model;

import java.util.ArrayList;
import java.util.List;

public class Automato {
    private String nome;
    private List<Estado> estados;
    private List<Transicao> transicoes;

    public Automato(String nome) {
        this.nome = nome;
        this.estados = new ArrayList<>();
        this.transicoes = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Estado> getEstados() {
        return estados;
    }

    public void addEstado(Estado estado) {
        this.estados.add(estado);
    }

    public List<Transicao> getTransicoes() {
        return this.transicoes;
    }

    public void addTransicao(Transicao transicao) {
        this.transicoes.add(transicao);
    }

    public Estado getEstadoPorId(int id) {
        for (Estado estado : estados) {
            if (estado.getId() == id) { 
                return estado;
            }
        }
        return null; 
    }
}
