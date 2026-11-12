package com.suporte.training.service;


import com.suporte.training.dto.TrainingRequestDTO;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TrainingService {

public List<TrainingRequestDTO> searchTraining(){

    List<TrainingRequestDTO> lista = new ArrayList<TrainingRequestDTO>();

    try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("training.markdown")) {
        if (inputStream == null) return lista;

        String conteudo = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        String[] blocos = conteudo.split("=== INÍCIO_TREINAMENTO ===");

        for (String bloco : blocos) {
            String iopLimpa = bloco.replace("=== FIM_TREINAMENTO ===", "").trim();
            if (iopLimpa.isBlank()) continue;

            String id = extrairLinha(iopLimpa, "ID:");
            String titulo = extrairLinha(iopLimpa, "TÍTULO:");
            String modulo = extrairLinha(iopLimpa, "MÓDULO:");

            lista.add(new TrainingRequestDTO(id, titulo, modulo, iopLimpa));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return lista;
}

    private String extrairLinha(String texto, String prefixo) {
        for (String linha : texto.split("\n")) {
            if (linha.trim().startsWith(prefixo)) {
                return linha.replace(prefixo, "").trim();
            }
        }
        return "Desconhecido";
    }
}
