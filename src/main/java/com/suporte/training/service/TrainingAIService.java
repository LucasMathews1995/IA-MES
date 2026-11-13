package com.suporte.training.service;

import com.suporte.core.categoriasuporte.SuportCategory;
import com.suporte.core.dto.ChatRequestDTO;
import com.suporte.core.dto.QueryContextDTO;
import com.suporte.core.strategy.SuportAIStrategy;
import com.suporte.iop.chat.IopHybridRetriever;
import com.suporte.training.chat.TrainingChatAIService;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;


@ApplicationScoped 
public class TrainingAIService implements SuportAIStrategy {


     @Inject
     TrainingChatAIService trainingChatAIService;

    @Inject
    IopHybridRetriever iopHybridRetriever;

    @Override
    public SuportCategory getCategory() {
        return SuportCategory.TRAINING;
    }

 @Override
    public String process(QueryContextDTO context) {

        String ask = context.rawText();
        List<Content> content = iopHybridRetriever.retrieve(Query.from(ask));

        if(content.isEmpty()){
           return"Nenhuma Training correspondente foi encontrada na base de conhecimento para este cenário.";
        }

     String retrievedContent = content.stream()
             .map(c -> c.textSegment().text())
             .collect(Collectors.joining("\n\n"));

        return trainingChatAIService.chat(retrievedContent, context.rawText());

}
}