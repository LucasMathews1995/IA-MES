package com.suporte.core.strategy.retriever;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHybridRetriever implements ContentRetriever {

    @Inject
    protected EmbeddingModel embeddingModel;

    @Inject
    protected EntityManager em;



    protected abstract String getTipoDocumento();

    protected int getMaxResults() {
        return 1;
    }


    @Override
    public List<Content> retrieve(Query query) {
        String textoUsuario = query.text();

        String termosOr = textoUsuario.trim()
                .replaceAll("[^\\p{L}\\p{N}\\s]", " ")
                .trim()
                .replaceAll("\\s+", " | ");

        Embedding vetorPergunta = embeddingModel.embed(textoUsuario).content();
        String vetorString = vetorPergunta.vectorAsList().toString();
        String tipoDoc = getTipoDocumento();

        String sql = """
            WITH semantic_search AS (
                SELECT embedding_id, text, metadata,
                       ROW_NUMBER() OVER (ORDER BY embedding <=> CAST(:vetor AS vector)) AS rank
                FROM embeddings
                WHERE metadata->>'tipo_documento' = :tipoDoc
                LIMIT 10
            ),
            keyword_search AS (
                SELECT embedding_id, text, metadata,
                       ROW_NUMBER() OVER (ORDER BY ts_rank_cd(to_tsvector('portuguese', text), to_tsquery('portuguese', :termosOr)) DESC) AS rank
                FROM embeddings
                WHERE metadata->>'tipo_documento' = :tipoDoc
                  AND to_tsvector('portuguese', text) @@ to_tsquery('portuguese', :termosOr)
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
            LIMIT :limite
        """;

        @SuppressWarnings("unchecked")
        List<Object[]> resultados = em.createNativeQuery(sql)
                .setParameter("termosOr", termosOr)
                .setParameter("vetor", vetorString)
                .setParameter("tipoDoc", tipoDoc)
                .setParameter("limite", getMaxResults())
                .getResultList();

        List<Content> conteudosFinais = new ArrayList<>();

        for (Object[] linha : resultados) {
            String id = (String) linha[0];
            String texto = (String) linha[1];
            Double score = ((Number) linha[2]).doubleValue();
            String titulo = (String) linha[3];

            System.out.printf("--> [HYBRID MATCH - %s] Score: %.4f | ID: %s | Título: %s%n",
                    tipoDoc, score, id, titulo);

            conteudosFinais.add(Content.from(texto));
        }

        return conteudosFinais;
    }
}



