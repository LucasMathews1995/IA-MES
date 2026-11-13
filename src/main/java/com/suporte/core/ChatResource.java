package com.suporte.core;


import com.suporte.core.dto.ChatRequestDTO;
import com.suporte.core.dto.QueryContextDTO;
import com.suporte.core.factory.SuportAIFactory;
import com.suporte.core.infraestructure.MesKeyWordExtractor;
import com.suporte.core.strategy.SuportAIStrategy;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatResource {



    @Inject
    EmbeddingModel embeddingModel;

    @Inject
    EmbeddingStore<TextSegment> embeddingStore;

    @Inject 
    SuportAIFactory  factory;

    @Inject 
    MesKeyWordExtractor extractor;

  @POST
public Response chat(@Valid ChatRequestDTO request) {
    if (request == null || request.pergunta() == null || request.pergunta().isBlank()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ChatResponse("A pergunta não pode estar vazia."))
                .build();
    }
    QueryContextDTO contextDTO = extractor.extrair(request.pergunta());


      SuportAIStrategy servico = factory.obterServico(request.categoriaSuporte());
    

    String respostaIA = servico.process(contextDTO);

    return Response.ok(new ChatResponse(respostaIA)).build();
}

   

    public static class ChatResponse {
        public String resposta;

        public ChatResponse(String resposta) {
            this.resposta = resposta;
        }
    }
}