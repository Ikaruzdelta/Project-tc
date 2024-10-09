package com.example.projecttc.model;

import java.util.ArrayList;
import java.util.List;

public class Estado {
    private int id;
    private String nome;
    private boolean isInicial;
    private boolean isFinal;
    private double y;
    private double x;
    private List<Transicao> transicoes = new ArrayList<>();

    public Estado(int id, String nome, boolean isInicial, boolean isFinal, double x, double y) {
        this.id = id;
        this.nome = nome;
        this.isInicial = isInicial;
        this.isFinal = isFinal;
        this.x = x;
        this.y = y;
    }

    public Estado(Estado estado) {
        this.nome = estado.getNome();
        this.id = estado.getId();
        this.x = estado.getX();
        this.y = estado.getY();
        this.isFinal = estado.isFinal();
        this.isFinal = estado.isInicial();
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

    public double getX(){
        return x;
    }

    public void setX(double x){
        this.x = x;
    }

    public double getY(){
        return y;
    }

    public void setY(double y){
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

    // Método para buscar um estado pela sua id
    public static Estado getEstadoById(ArrayList<Estado> estados, int id) {
        for (Estado estado : estados) {
            if (estado.getId() == id) {
                return estado;
            }
        }
        return null; // Retorna null se nenhum estado com o ID for encontrado
    }
}

