package com.suporte.core.factory;

import com.suporte.core.categoriasuporte.SuportCategory;
import com.suporte.core.strategy.IngestionSuportStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;


@ApplicationScoped 
public class IngestionFactory {

    @Inject
    Instance<IngestionSuportStrategy> servicos;

    public IngestionSuportStrategy obterServico(SuportCategory categoria) {
        for (IngestionSuportStrategy servico : servicos) {
            if (servico.getCategory() == categoria) {
                return servico;
            }
        }
        throw new IllegalArgumentException("Categoria de suporte não implementada: " + categoria);
    }
}
