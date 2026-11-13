package com.suporte.iop.chat;

import com.suporte.core.strategy.retriever.AbstractHybridRetriever;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;


/*
* Preferi utilizar a consulta por SQL porque é melhor no âmbito de ter maior precisão semântica do que a busca dinamica no momento
* por conta de alucinações entre as palavras como sumir e não aparecer. Perdi no contexto que eu irei retornar na classe IOPAIService mais tarde
*Mas por enquanto por mim , isso faz sentido , por isso o sinkhole no meu
* */
@ApplicationScoped
public class IopHybridRetriever extends AbstractHybridRetriever {

    @Override
    protected String getTipoDocumento() {
        return "TRAINING";
    }

        }