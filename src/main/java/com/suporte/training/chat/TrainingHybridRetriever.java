package com.suporte.training.chat;

import com.suporte.core.strategy.retriever.AbstractHybridRetriever;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TrainingHybridRetriever extends AbstractHybridRetriever {


    @Override
    protected String getTipoDocumento() {
        return "TRAINING";
    }
}
