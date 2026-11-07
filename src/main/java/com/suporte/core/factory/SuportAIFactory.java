package com.suporte.core.factory;

import com.suporte.core.categoriasuporte.SuportCategory;
import com.suporte.core.strategy.SuportAIStrategy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class SuportAIFactory {

    @Inject
    Instance<SuportAIStrategy> servicos;

    public SuportAIStrategy obterServico(SuportCategory categoria) {
        for (SuportAIStrategy servico : servicos) {
            if (servico.getCategory() == categoria) {
                return servico;
            }
        }
        throw new IllegalArgumentException("Categoria de suporte não implementada: " + categoria);
    }
}