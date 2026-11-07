package com.suporte.training.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import com.suporte.core.categoriasuporte.SuportCategory;
import com.suporte.core.dto.ArquivoConfig;
import com.suporte.core.service.AbstractIngestService;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@ApplicationScoped 
public class TrainingIngestionService extends AbstractIngestService {

    private static final Logger LOG = Logger.getLogger(TrainingIngestionService.class);


    
    private final DocumentSplitter splitter = DocumentSplitters.recursive(6000, 200);

    @Override
    public SuportCategory getCategory() {
        return SuportCategory.TRAINING;
    }

    @Override
    public Response proccess(ArquivoConfig cfg) {
        List<TextSegment> todosSegmentos = new ArrayList<>();

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(cfg.nomeArquivo())) {
            if (is == null) {
                LOG.warnv("Arquivo {0} não encontrado, a saltar...", cfg.nomeArquivo());
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"erro\": \"Arquivo " + cfg.nomeArquivo() + " não encontrado\"}")
                        .build();
            }

            String conteudo = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            if (conteudo.isBlank()) {
                return Response.ok("{\"status\": \"vazio\", \"total_chunks\": 0}").build();
            }

          
            String[] blocos = conteudo.split(Pattern.quote(cfg.delimitadorInicio()));

            for (String bloco : blocos) {
             
                String blocoLimpo = processarBloco(bloco, cfg.delimitadorFim());
                if (blocoLimpo.isBlank()) continue;

             
                String id = extrairValor(blocoLimpo, "ID"); 
                String titulo = extrairTitulo(blocoLimpo, "Módulo de Formação MES");
                String modulo = extrairValor(blocoLimpo, "MÓDULO");

                Metadata metadata = Metadata.from("arquivo", cfg.nomeArquivo())
                        .put("tipo_documento", cfg.tipo())
                        .put("id", id)
                        .put("titulo", titulo)
                        .put("modulo", modulo);

                Document doc = Document.from(blocoLimpo, metadata);
                List<TextSegment> subChunks = splitter.split(doc);

        
                String cabecalhoContexto = String.format("[MATERIAL DE TREINAMENTO | MÓDULO: %s | ID: %s | TÍTULO: %s]\n", 
                        modulo, id, titulo);

                for (TextSegment seg : subChunks) {
                    String textoEnriquecido = seg.text().startsWith("[MATERIAL DE TREINAMENTO") 
                            ? seg.text() 
                            : cabecalhoContexto + seg.text();
                    todosSegmentos.add(TextSegment.from(textoEnriquecido, seg.metadata()));
                }
            }

            if (!todosSegmentos.isEmpty()) {
                LOG.infov("A processar e a ingerir {0} chunks de Treinamento...", todosSegmentos.size());
                var embeddings = embeddingModel.embedAll(todosSegmentos).content();
                embeddingStore.addAll(embeddings, todosSegmentos);
            }

            LOG.infov("Ficheiro de Treinamento {0} concluído.", cfg.nomeArquivo());
            return Response.ok("{\"status\": \"sucesso\", \"total_chunks\": " + todosSegmentos.size() + "}").build();

        } catch (Exception e) {
            LOG.error("Falha ao ingerir ficheiro de Treinamento: " + cfg.nomeArquivo(), e);
            return Response.serverError().entity("{\"erro\": \"" + e.getMessage() + "\"}").build();
        }
    }
}