package com.suporte.iop.service;

import com.suporte.core.IngestionResource;
import com.suporte.core.categoriasuporte.SuportCategory;
import com.suporte.core.dto.ArquivoConfig;
import com.suporte.core.service.AbstractIngestService;
import com.suporte.core.strategy.IngestionSuportStrategy;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jboss.logging.Logger;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;


@ApplicationScoped 
public class IopIngestionService extends AbstractIngestService{

     private static final Logger LOG = Logger.getLogger(IngestionResource.class);


   
    @Override
    public SuportCategory getCategory() {
       return  SuportCategory.IOP;
    }

   @Override
    public Response proccess(ArquivoConfig cfg) {
        List<TextSegment> todosSegmentos = new ArrayList<>();

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(cfg.nomeArquivo())) {
            if (is == null) {
                LOG.warnv("Arquivo {0} não encontrado, pulando...", cfg.nomeArquivo());
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"erro\": \"Arquivo " + cfg.nomeArquivo() + " não encontrado\"}")
                        .build();
            }

            String conteudo = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            if (conteudo.isBlank()) {
                return Response.ok("{\"status\": \"vazio\", \"total_chunks\": 0}").build();
            }

            // O corte exato: divide pelo delimitador de início garantindo que não perde partes da IOP
            String[] blocos = conteudo.split(Pattern.quote(cfg.delimitadorInicio()));

            // Splitter configurado com limite ALTO (6000) para garantir que uma IOP inteira nunca seja cortada ao meio!
            DocumentSplitter splitter = DocumentSplitters.recursive(6000, 200);

            for (String bloco : blocos) {
                // Remove espaços em branco e a tag de fim (=== FIM_IOP ===)
                String blocoLimpo = processarBloco(bloco, cfg.delimitadorFim());
                if (blocoLimpo.isBlank()) continue;

                // Extração precisa ignorando quebras de linha de Windows (\r\n) ou Linux (\n)
                String id = extrairValor(blocoLimpo, "ID:");
                String titulo = extrairTitulo(blocoLimpo,"TÍTULO:");
                String modulo = extrairValor(blocoLimpo, "MÓDULO:");

                Metadata metadata = Metadata.from("arquivo", cfg.nomeArquivo())
                        .put("tipo_documento", cfg.tipo())
                        .put("id", id)
                        .put("titulo", titulo)
                        .put("modulo", modulo);

                Document doc = Document.from(blocoLimpo, metadata);
                List<TextSegment> subChunks = splitter.split(doc);

                // Cabeçalho otimizado para Diagnóstico/IOP: Ensina à IA do que se trata antes dela ler os passos
                String cabecalhoContexto = String.format("[GUIA DE DIAGNÓSTICO IOP | MÓDULO: %s | ID: %s | TÍTULO: %s]\n", 
                        modulo, id, titulo);

                for (TextSegment seg : subChunks) {
                    String textoEnriquecido = seg.text().startsWith("[GUIA DE DIAGNÓSTICO") 
                            ? seg.text() 
                            : cabecalhoContexto + seg.text();
                    todosSegmentos.add(TextSegment.from(textoEnriquecido, seg.metadata()));
                }
            }

            if (!todosSegmentos.isEmpty()) {
                LOG.infov("Gerando embeddings para {0} IOPs...", todosSegmentos.size());
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