package com.suporte.core.factory;

import com.suporte.core.categoriasuporte.SuportCategory;
import com.suporte.core.exceptions.NotFoundIngestionSuportStrategy;
import com.suporte.core.strategy.IngestionSuportStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

/*
 * =====================================================================================
 * NOTA ARQUITETURAL: DECISÃO DE DESIGN (STRATEGY VS DUPLICAÇÃO)
 * =====================================================================================
 *
 * 1. Camada de Ingestão (IngestionFactory e IngestionSuportStrategy):
 * Aplicamos o padrão Strategy orquestrado por esta Factory para centralizar e
 * simplificar o roteamento da entrada de dados com base na SuportCategory.
 * Como a ingestão atua como um mecanismo de infraestrutura/core, o Strategy mantém
 * o ponto de entrada limpo, escalável e tira proveito da injeção dinâmica do CDI.
 *
 * 2. Camada de Domínio/Serviços (ex: TrainingService e IOPResources):
 * Optamos por NÃO aplicar o padrão base/strategy para o processamento interno e
 * mantivemos a lógica de extração (parsing de markdown) duplicada intencionalmente.
 *
 * MOTIVO: Os contextos de Treinamento e IOP representam domínios de negócio distintos.
 * No cenário de uma futura migração para microsserviços, essa separação inicial
 * garante que os domínios não possuam acoplamento de código (library coupling).
 * Preferimos tolerar a duplicação nos serviços para garantir independência de
 * manutenção e deploy, enquanto usamos a abstração (Strategy) apenas aqui no roteamento.
 * =====================================================================================
 */
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
        throw new NotFoundIngestionSuportStrategy("Categoria de suporte não implementada: " + categoria);
    }
}



