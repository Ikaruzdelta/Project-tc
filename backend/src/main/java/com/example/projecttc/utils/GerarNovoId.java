package com.example.projecttc.utils;

import com.example.projecttc.model.Estado;
import com.example.projecttc.model.Automato;

public class GerarNovoId {
    
    public static int gerarNovoId(Automato automato) {
        int maiorId = -1;
        
        // Itera sobre os estados do autômato para encontrar o maior ID
        for (Estado estado : automato.getEstados()) {
            if (estado.getId() > maiorId) {
                maiorId = estado.getId();
            }
        }
        
        // Retorna o próximo ID disponível
        return maiorId + 1;
    }
}