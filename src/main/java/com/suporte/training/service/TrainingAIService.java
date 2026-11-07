package com.suporte.training.service;

import com.suporte.core.categoriasuporte.SuportCategory;
import com.suporte.core.dto.ChatRequestDTO;
import com.suporte.core.dto.QueryContextDTO;
import com.suporte.core.strategy.SuportAIStrategy;
import com.suporte.training.chat.TrainingChatAIService;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped 
public class TrainingAIService implements SuportAIStrategy {
  @Inject
    EmbeddingModel embeddingModel;

    @Inject
    EmbeddingStore<TextSegment> embeddingStore;

    @Inject
    TrainingChatAIService treinamentoAiClient;

    @Override
    public SuportCategory getCategory() {
        return SuportCategory.TRAINING;
    }

 @Override
    public String process(QueryContextDTO context) {
        // 1. Constrói o filtro dinâmico combinando tipo TRAINING + tags encontradas
        Filter dynamicFilter = buildFilter(context);

        // 2. Gera o vetor da pergunta (usando cleanQuery ou rawText)
        var queryEmbedding = embeddingModel.embed(context.cleanQuery()).content();

        // 3. Monta a requisição com maxResults e minScore calibrados para material de treinamento
        var searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(3)
                .minScore(0.65)
                .filter(dynamicFilter)
                .build();

        var relevantSegments = embeddingStore.search(searchRequest).matches();

        if (relevantSegments.isEmpty()) {
            return "Não encontrei material de suporte para esta dúvida nos módulos de treinamento. Tente reformular a pergunta ou verificar se o tópico está catalogado.";
        }

        // 4. Isolamento seguro: agrupa trechos associados ao módulo/curso vencedor (idWinner)
        String idWinner = relevantSegments.get(0).embedded().metadata().getString("id");
        StringBuilder contextoTreinamento = new StringBuilder();

        for (var match : relevantSegments) {
            String idCurrent = match.embedded().metadata().getString("id");
            System.out.printf("--> [TRAINING MATCH] Score: %.4f | ID: %s | Título: %s%n",
                    match.score(), idCurrent, match.embedded().metadata().getString("titulo"));

            if (idWinner != null && idWinner.equals(idCurrent)) {
                contextoTreinamento.append(match.embedded().text()).append("\n\n---\n\n");
            }
        }

     
        return treinamentoAiClient.chat(contextoTreinamento.toString(), context.rawText());
    }

   
    private Filter buildFilter(QueryContextDTO ctx) {
        Filter baseFilter = MetadataFilterBuilder.metadataKey("tipo_documento").isEqualTo("TRAINING");

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

        return baseFilter;
    }
}