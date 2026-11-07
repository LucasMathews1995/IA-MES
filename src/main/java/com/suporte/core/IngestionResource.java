package com.suporte.core;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import com.suporte.core.categoriasuporte.SuportCategory;
import com.suporte.core.dto.ArquivoConfig;
import com.suporte.core.factory.IngestionFactory;
import com.suporte.core.strategy.IngestionSuportStrategy;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;
@Path("/api/ingest")
@ApplicationScoped 
public class IngestionResource {

    private static final Logger LOG = Logger.getLogger(IngestionResource.class);

  
    @Inject 
    DataSource dataSource;
   
    @Inject
    IngestionFactory ingestionFactory;

    @POST
    @Path("/todos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response carregarTodosOsArquivos() {
        
        // 1. Prepara a Base de Dados
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE embeddings;");
            LOG.info("Tabela de embeddings reiniciada para nova ingestão.");
        } catch (Exception e) {
            LOG.error("Falha ao truncar tabela de embeddings", e);
            return Response.serverError().entity("{\"erro\": \"" + e.getMessage() + "\"}").build();
        }

        // 2. Lista de Ficheiros a Processar
        List<ArquivoConfig> arquivos = List.of(
            new ArquivoConfig("training.markdown", "=== INÍCIO_TREINAMENTO ===", "=== FIM_TREINAMENTO ===", "TRAINING"),
            new ArquivoConfig("iops.markdown", "=== INÍCIO_IOP ===", "=== FIM_IOP ===", "IOP"),
            new ArquivoConfig("rdq.markdown", "=== INÍCIO_RDQ ===", "=== FIM_RDQ ===", "RDQ"),
            new ArquivoConfig("explanations.markdown", "=== INÍCIO_IOP ===", "=== FIM_IOP ===", "EXPLANATION")
        );

        // 3. Roteamento Dinâmico via Factory
        int arquivosProcessados = 0;

        for (ArquivoConfig cfg : arquivos) {
            try {
             
                SuportCategory categoria = SuportCategory.valueOf(cfg.tipo().toUpperCase());
                
            
                IngestionSuportStrategy strategy = ingestionFactory.obterServico(categoria);
                
                LOG.infov("Roteando arquivo {0} para {1}...", cfg.nomeArquivo(), strategy.getClass().getSimpleName());
                
 
                strategy.proccess(cfg); 
                arquivosProcessados++;
                
            } catch (IllegalArgumentException e) {
                // Captura a exceção lançada pela sua Factory caso a categoria não exista
                LOG.warnv("Aviso: Falha ao rotear o arquivo {0}. Motivo: {1}", cfg.nomeArquivo(), e.getMessage());
            } catch (Exception e) {
                LOG.errorv("Erro inesperado ao processar arquivo {0}: {1}", cfg.nomeArquivo(), e.getMessage());
            }
        }

        return Response.ok(String.format("{\"status\": \"sucesso\", \"arquivos_processados\": %d}", arquivosProcessados)).build();
    }
}