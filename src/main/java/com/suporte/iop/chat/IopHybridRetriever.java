package com.suporte.iop.chat;

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
public class IopHybridRetriever implements ContentRetriever {

    @Inject
    EmbeddingModel embeddingModel; 

    @Inject
    EntityManager em;

    @Override
    public List<Content> retrieve(Query query) {
        String textoUsuario = query.text();

        Embedding vetorPergunta = embeddingModel.embed(textoUsuario).content();


        String sql = """
            WITH semantic_search AS (
                SELECT embedding_id, text, metadata,
                       ROW_NUMBER() OVER (ORDER BY embedding <=> CAST(:vetor AS vector)) AS rank
                FROM embeddings
                LIMIT 10
            ),
            keyword_search AS (
                SELECT embedding_id, text, metadata,
                       ROW_NUMBER() OVER (ORDER BY ts_rank_cd(to_tsvector('portuguese', text), websearch_to_tsquery('portuguese', :texto)) DESC) AS rank
                FROM embeddings
                WHERE to_tsvector('portuguese', text) @@ websearch_to_tsquery('portuguese', :texto)
                LIMIT 10
            )
            SELECT 
                CAST(COALESCE(s.embedding_id, k.embedding_id) AS VARCHAR) AS id,
                COALESCE(s.text, k.text) AS texto,
                (COALESCE(1.0 / (60 + s.rank), 0.0)) + (COALESCE(1.0 / (60 + k.rank), 0.0)) AS score,
                COALESCE(s.metadata->>'titulo', k.metadata->>'titulo') AS titulo
            FROM semantic_search s
            FULL OUTER JOIN keyword_search k ON s.embedding_id = k.embedding_id
            ORDER BY score DESC
            LIMIT 1
        """;

        String vetorString = vetorPergunta.vectorAsList().toString();

        @SuppressWarnings("unchecked")
        List<Object[]> resultados = em.createNativeQuery(sql)
                .setParameter("texto", textoUsuario)
                .setParameter("vetor", vetorString)
                .getResultList();

        List<Content> conteudosFinais = new java.util.ArrayList<>();


        for (Object[] linha : resultados) {
            String id = (String) linha[0];
            String texto = (String) linha[1];
            Double score = ((Number) linha[2]).doubleValue();
            String titulo = (String) linha[3];

            // Printa no console igual você fazia antes!
            System.out.printf("--> [HYBRID MATCH] Score: %.4f | ID: %s | Título: %s%n", score, id, titulo);

            // Adiciona o texto na lista que o LangChain4j vai mandar para a IA
            conteudosFinais.add(Content.from(texto));
        }

        return conteudosFinais;
    }}