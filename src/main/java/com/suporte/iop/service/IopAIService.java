package com.suporte.iop.service;

import com.suporte.core.categoriasuporte.SuportCategory;
import com.suporte.core.dto.QueryContextDTO;
import com.suporte.core.strategy.SuportAIStrategy;
import com.suporte.iop.chat.IopChatAIService;
import com.suporte.iop.chat.IopHybridRetriever;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.query.Query;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

/*
* Trade-off
* pode parecer que é um sinkhole , mas conforme o projeto for crescendo verá que pode ter dados e busca dinâmicas mais profundas nisso
* Melhor conter o roteamento no back-end do que no front-end , pois eu pensei em separar os controller(resource) em cada categoria
* Mas se eu criasse mais categorias , meu front-end quem iría fazer o direcionamento enorme de cada peça
*
*
* */
@ApplicationScoped
public class IopAIService implements SuportAIStrategy {




    @Inject
    IopChatAIService iopAIService;
    @Inject
    IopHybridRetriever iopHybridRetriever;


    @Override
    public SuportCategory getCategory( ) {
        return SuportCategory.IOP;
    }



    @Override
    public String process(QueryContextDTO context) {

        String pergunta = context.rawText();

        List<Content> conteudos = iopHybridRetriever.retrieve(Query.from(pergunta));


        if (conteudos.isEmpty()) {
            return "Nenhuma IOP correspondente foi encontrada na base de conhecimento para este cenário.";
        }
        String contextoRecuperado = conteudos.stream()
                .map(c -> c.textSegment().text())
                .collect(Collectors.joining("\n\n"));

        return iopAIService.chat(contextoRecuperado,context.rawText());
    }
}

  
