package com.suporte.explanation.service;

import com.suporte.core.categoriasuporte.SuportCategory;
import com.suporte.core.dto.ArquivoConfig;
import com.suporte.core.service.AbstractIngestService;
import com.suporte.core.strategy.IngestionSuportStrategy;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.agroal.pool.DataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jboss.logging.Logger;

@ApplicationScoped 
public class ExplanationIngestionService extends AbstractIngestService{

    private static final Logger LOG = Logger.getLogger(ExplanationIngestionService.class);


    private final DocumentSplitter splitter = DocumentSplitters.recursive(6000, 200);

    @Override
    public SuportCategory getCategory() {
        return  SuportCategory.EXPLANATION;
    }

    @Override
    public Response proccess(ArquivoConfig cfg) {
        List<TextSegment> todosSegmentos = new ArrayList<>();

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(cfg.nomeArquivo())) {
            if (is == null) {
                LOG.warnv("Arquivo {0} não encontrado, saltando...", cfg.nomeArquivo());
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"erro\": \"Arquivo " + cfg.nomeArquivo() + " não encontrado\"}")
                        .build();
            }

            String conteudo = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            if (conteudo.isBlank()) {
                return Response.ok("{\"status\": \"vazio\", \"total_chunks\": 0}").build();
            }

            // O corte divide dinamicamente pelo delimitador de início configurado no ArquivoConfig
            String[] blocos = conteudo.split(Pattern.quote(cfg.delimitadorInicio()));

            for (String bloco : blocos) {
                String blocoLimpo = processarBloco(bloco, cfg.delimitadorFim());
                if (blocoLimpo.isBlank()) continue;

                // Extração dos metadados (resiliente a quebras de linha Windows e Unix)
                String id = extrairValor(blocoLimpo, "ID:");
                String titulo = extrairTitulo(blocoLimpo, "Sem Título");
                String modulo = extrairValor(blocoLimpo, "MÓDULO:");

                Metadata metadata = Metadata.from("arquivo", cfg.nomeArquivo())
                        .put("tipo_documento", cfg.tipo())
                        .put("id", id)
                        .put("titulo", titulo)
                        .put("modulo", modulo);

                Document doc = Document.from(blocoLimpo, metadata);
                List<TextSegment> subChunks = splitter.split(doc);

                // Cabeçalho otimizado para Explicações: Ancoragem semântica para perguntas de "O que é?" ou "Como funciona?"
                String cabecalhoContexto = String.format("[EXPLICAÇÃO DE FLUXO/CONCEITO | MÓDULO: %s | ID: %s | TÍTULO: %s]\n", 
                        modulo, id, titulo);

                for (TextSegment seg : subChunks) {
                    String textoEnriquecido = seg.text().startsWith("[EXPLICAÇÃO") 
                            ? seg.text() 
                            : cabecalhoContexto + seg.text();
                    todosSegmentos.add(TextSegment.from(textoEnriquecido, seg.metadata()));
                }
            }

            if (!todosSegmentos.isEmpty()) {
                LOG.infov("A gerar embeddings para {0} blocos de Explicação...", todosSegmentos.size());
                var embeddings = embeddingModel.embedAll(todosSegmentos).content();
                embeddingStore.addAll(embeddings, todosSegmentos);
            }

            LOG.infov("Arquivo {0} preparado e ingerido com sucesso.", cfg.nomeArquivo());
            return Response.ok("{\"status\": \"sucesso\", \"total_chunks\": " + todosSegmentos.size() + "}").build();

        } catch (Exception e) {
            LOG.errorv("Erro ao processar arquivo {0}: {1}", cfg.nomeArquivo(), e.getMessage());
            return Response.serverError().entity("{\"erro\": \"Falha ao ler " + cfg.nomeArquivo() + "\"}").build();
        }
    }

   
}

