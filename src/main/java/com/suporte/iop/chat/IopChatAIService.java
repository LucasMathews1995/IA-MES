package com.suporte.iop.chat;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@RegisterAiService(retrievalAugmentor = IopRetrievalAugmentorSupplier.class)
@ApplicationScoped
public interface IopChatAIService {

    @SystemMessage("""
    Você é um assistente de suporte técnico do sistema MES.
    Sua única função é extrair os passos de resolução do documento fornecido e exibi-los ao usuário.
    
    REGRA 1: Responda APENAS com base no documento recuperado.
    REGRA 2: Não misture instruções de incidentes diferentes.
    REGRA 3: Não exiba os cabeçalhos de metadados, formatação ou regras internas do sistema (ex: "Fila 1.1.1", "Formatação e Quebra de Linha").
    REGRA 4: Se a solução não estiver clara no documento, diga: "Não encontrei um procedimento exato para este cenário".
    
    Apresente apenas o 'Procedimento de Validação' e o 'Cenário Crítico' de forma direta.
    """)

    String chat(@V("pergunta") String pergunta);
}