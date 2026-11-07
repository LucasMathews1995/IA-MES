package com.suporte.core.service;

import java.util.Scanner;

import com.suporte.core.strategy.IngestionSuportStrategy;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.inject.Inject;

public abstract class AbstractIngestService implements IngestionSuportStrategy {


    @Inject
    protected EmbeddingModel embeddingModel;

    @Inject
    protected EmbeddingStore<TextSegment> embeddingStore;

    

    protected String processarBloco(String bloco, String delimitadorFim) {
        if (bloco == null || bloco.trim().isEmpty()) return "";
        if (delimitadorFim == null) return bloco.trim();
        
        int indexFim = bloco.indexOf(delimitadorFim);
        if (indexFim != -1) {
            return bloco.substring(0, indexFim).trim();
        }
        return bloco.trim();
    }

    protected String extrairTitulo(String texto, String fallbackTitulo) {
        String tituloExplicito = extrairValor(texto, "TÍTULO");
        if (!"N/A".equals(tituloExplicito)) {
            return tituloExplicito;
        }
        return fallbackTitulo;
    }

    protected String extrairValor(String texto, String chave) {
        if (texto == null || texto.trim().isEmpty()) {
            return "N/A";
        }

       
        try (Scanner scanner = new Scanner(texto)) {
            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine().trim();
                
                
                if (linha.toUpperCase().startsWith(chave.toUpperCase() + ":")) {
                    
                    return linha.substring(linha.indexOf(':') + 1).trim();
                }
            }
        }
        return "N/A";
    }

}
