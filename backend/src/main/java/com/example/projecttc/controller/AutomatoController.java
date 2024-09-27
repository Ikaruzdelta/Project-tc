package com.example.projecttc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.projecttc.model.Automato;
import com.example.projecttc.service.AutomatoService;

@RestController
@RequestMapping("/api/automato")
public class AutomatoController {

    @Autowired
    private AutomatoService automatoService;

    @PostMapping("/complemento")
    public Automato complemento(@RequestParam("file") MultipartFile file) {
        try {
            Automato automato = automatoService.importarAutomato(file);
            return automatoService.aplicarComplemento(automato);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao aplicar complemento", e);
        }
    }

    @PostMapping("/estrela")
    public Automato estrela(@RequestParam("file") MultipartFile file) {
        try {
            Automato automato = automatoService.importarAutomato(file);
            return automatoService.aplicarEstrela(automato);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao aplicar estrela", e);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Automato> uploadAutomato(@RequestParam("file") MultipartFile file) {
    try {
        Automato automato = automatoService.importarAutomato(file);
        return ResponseEntity.ok(automato);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}


    @GetMapping("/visualizar")
    public ResponseEntity<Automato> visualizarAutomato() {
    Automato automato = automatoService.getAutomato(); 
    if (automato == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); 
    }
    return ResponseEntity.ok(automato);
    }
}
