package com.example.projecttc.utils;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;

public class GerarNovoId {
    
    public static int gerarNovoId(Automato automato) {
        int maiorId = -1;
        
        for (Estado estado : automato.getEstados()) {
            if (estado.getId() > maiorId) {
                maiorId = estado.getId();
            }
        }
        return maiorId + 1;
    }
}