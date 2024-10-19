package com.example.projecttc.service;

import com.example.projecttc.model.Automato;
import com.example.projecttc.model.Estado;
import com.example.projecttc.utils.CompletarAfd;
import com.example.projecttc.utils.ValidacaoAFD;
import org.springframework.stereotype.Service;

@Service
public class ComplementoService {

    public Automato complemento(Automato automato) {
        try {
            // Verificar se é um AFD antes de aplicar o complemento
            if (!ValidacaoAFD.isAFD(automato)) {
                return null;
            }

            // Completar o AFD se necessário
            if (!CompletarAfd.isAFDCompleto(automato)) {
                CompletarAfd.deixarAFDCompleto(automato);
            }

            for(Estado t: automato.getEstados()){
                if(t.isFinal() == true){
                    t.setFinal(false);
                }else{
                    t.setFinal(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return automato;
    }
}
