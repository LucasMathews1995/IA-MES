package com.suporte.iop.service;

import com.suporte.core.categoriasuporte.SuportCategory;
import com.suporte.core.dto.ChatRequestDTO;
import com.suporte.core.dto.QueryContextDTO;
import com.suporte.core.infraestructure.MesKeyWordExtractor;
import com.suporte.core.strategy.SuportAIStrategy;
import com.suporte.iop.chat.IOPChatAIService;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class IopAIService implements SuportAIStrategy {



    @Inject
    EmbeddingModel embeddingModel;

    @Inject
    EmbeddingStore<TextSegment> embeddingStore;

    @Inject
    IOPChatAIService iopAIService;

    @Inject
    MesKeyWordExtractor extractor;


    @Override
    public SuportCategory getCategory( ) {
        return SuportCategory.IOP;
    }
@Override
    public String process(QueryContextDTO context) {
      
        Filter dynamicFilter = buildFilter(context);

        
        var queryEmbedding = embeddingModel.embed(context.cleanQuery()).content();

       
        var searchRequestBuilder = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(2)
                .minScore(0.65)
                .filter(dynamicFilter);

        var searchResult = embeddingStore.search(searchRequestBuilder.build());
        var relevantSegments = searchResult.matches();

        if (relevantSegments.isEmpty()) {
            return "Não encontrei nenhuma Instrução Operacional (IOP) compatível com o cenário relatado. Verifique os dados informados ou acione o suporte N3.";
        }

       
        String idWinner = relevantSegments.get(0).embedded().metadata().getString("id");
        StringBuilder contextoAcumulado = new StringBuilder();

        for (var match : relevantSegments) {
            String idCurrent = match.embedded().metadata().getString("id");
            System.out.printf("--> [RAG MATCH] Score: %.4f | ID: %s | Título: %s%n",
                    match.score(), idCurrent, match.embedded().metadata().getString("titulo"));

           
            if (idWinner != null && idWinner.equals(idCurrent)) {
                contextoAcumulado.append(match.embedded().text()).append("\n\n---\n\n");
            }
        }

        
        return iopAIService.chat(contextoAcumulado.toString(), context.rawText());
    }

  
    private Filter buildFilter(QueryContextDTO ctx) {
        Filter baseFilter = MetadataFilterBuilder.metadataKey("tipo_documento").isEqualTo("IOP");

        if (!ctx.hasFilters()) { 
            return baseFilter;
        }

      
        if (ctx.telas() != null && !ctx.telas().isEmpty()) {
            String tela = ctx.telas().iterator().next(); 
            Filter telaFilter = MetadataFilterBuilder.metadataKey("tela").isEqualTo(tela);
            baseFilter = baseFilter.and(telaFilter);
        }

        
        if (ctx.equipamentos() != null && !ctx.equipamentos().isEmpty()) {
            String eq = ctx.equipamentos().iterator().next();
            Filter eqFilter = MetadataFilterBuilder.metadataKey("equipamento").isEqualTo(eq);
            baseFilter = baseFilter.and(eqFilter);
        }
        
       
        if (ctx.procedures() != null && !ctx.procedures().isEmpty()) {
            String proc = ctx.procedures().iterator().next();
            Filter procFilter = MetadataFilterBuilder.metadataKey("procedure").isEqualTo(proc);
            baseFilter = baseFilter.and(procFilter);
        }

        return baseFilter;
    }
}