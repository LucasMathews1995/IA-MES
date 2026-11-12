package com.suporte.iop.service;

import com.suporte.core.categoriasuporte.SuportCategory;
import com.suporte.core.dto.QueryContextDTO;
import com.suporte.core.strategy.SuportAIStrategy;
import com.suporte.iop.chat.IopChatAIService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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


    @Override
    public SuportCategory getCategory( ) {
        return SuportCategory.IOP;
    }



    @Override
    public String process(QueryContextDTO context) {

        
        return iopAIService.chat( context.rawText());
    }
}

  
