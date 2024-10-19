package com.example.projecttc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.projecttc.model.Automato;
import com.example.projecttc.utils.CompletarAfd;
import com.example.projecttc.utils.ValidacaoAFD;
import com.example.projecttc.utils.ValidacaoAlfabeto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DiferencaService {

    private static final Logger logger = LoggerFactory.getLogger(DiferencaService.class);

    @Autowired
    private ComplementoService complementoService;

    @Autowired
    private IntersecaoService intersecaoService;

    @Autowired
    private UniaoService uniaoService;

    public Automato diferenca(Automato automato1, Automato automato2) {
        if (ValidacaoAFD.isAFD(automato1) && ValidacaoAFD.isAFD(automato2)) {
            Automato complemento = complementoService.complemento(automato2);
            return intersecaoService.intersecaoAFD(automato1, complemento);
        } else {
            logger.error("Não é possível fazer a diferença entre AFN e AFD. Converta para AFD.");
            throw new IllegalArgumentException("Não é possível fazer a diferença entre AFN e AFD. Converta para AFD.");
        }
    }

    public Automato diferencaSimetrica(Automato automato1, Automato automato2) {
        // Verifica se os alfabetos são compatíveis
        if (!ValidacaoAlfabeto.compararAlfabeto(automato1.getAlfabeto(), automato2.getAlfabeto())) {
            logger.error("Os alfabetos dos AFDs são diferentes. A operação de diferença simétrica não pode ser realizada.");
            throw new IllegalArgumentException("Os alfabetos dos AFDs são diferentes.");
        }

        // Completar os AFDs se necessário
        if (!CompletarAfd.isAFDCompleto(automato1)) {
            CompletarAfd.deixarAFDCompleto(automato1);
            logger.info("AFD 1 completado automaticamente.");
        }
        if (!CompletarAfd.isAFDCompleto(automato2)) {
            CompletarAfd.deixarAFDCompleto(automato2);
            logger.info("AFD 2 completado automaticamente.");
        }

        // Realizar a diferença
        Automato automato3 = diferenca(automato1, automato2);
        Automato automato4 = diferenca(complementoService.complemento(automato2), automato1);

        // Verificar novamente os alfabetos
        if (!ValidacaoAlfabeto.compararAlfabeto(automato3.getAlfabeto(), automato4.getAlfabeto())) {
            logger.error("Os alfabetos dos autômatos resultantes da diferença são diferentes. Operação abortada.");
            throw new IllegalArgumentException("Os alfabetos dos autômatos resultantes da diferença são diferentes.");
        }

        // Completar novamente, se necessário
        if (!CompletarAfd.isAFDCompleto(automato3)) {
            CompletarAfd.deixarAFDCompleto(automato3);
            logger.info("AFD 3 completado automaticamente.");
        }
        if (!CompletarAfd.isAFDCompleto(automato4)) {
            CompletarAfd.deixarAFDCompleto(automato4);
            logger.info("AFD 4 completado automaticamente.");
        }

        // Realizar a união dos resultados das duas diferenças
        Automato resultadoFinal = uniaoService.uniaoAFD(automato3, automato4);
        logger.info("Diferença simétrica realizada com sucesso.");
        return resultadoFinal;
    }
}
