package com.example.projecttc.model;

import java.util.ArrayList;
import java.util.List;

public class Estado {
    private int id;
    private String nome;
    private boolean isInicial;
    private boolean isFinal;
    private String y;
    private String x;
    private List<Transicao> transicoes = new ArrayList<>();

    public Estado(int id, String nome, boolean isInicial, boolean isFinal, String x, String y) {
        this.id = id;
        this.nome = nome;
        this.isInicial = isInicial;
        this.isFinal = isFinal;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isInicial() {
        return isInicial;
    }

    public void setInicial(boolean isInicial) {
        this.isInicial = isInicial;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public String getX(){
        return x;
    }

    public void setX(String x){
        this.x = x;
    }

    public String getY(){
        return y;
    }

    public void setY(String y){
        this.y = y;
    }

    public boolean temTransicao(String simbolo) {
        for (Transicao transicao : this.getTransicoes()) {
            if (transicao.getSimbolo().equals(simbolo)) {
                return true;
            }
        }
        return false;
    }

    public List<Transicao> getTransicoes() {
        return transicoes;
    }
}

