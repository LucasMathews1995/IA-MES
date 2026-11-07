package com.suporte.explanation.chat;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped 
@RegisterAiService 
public interface ExplanationChatAIService {

    @SystemMessage("""
    Você é um instrutor técnico sênior do sistema MES da CSN.
    Sua missão é explicar conceitos de rastreabilidade, logs de movimento e programação com rigor técnico.
    
    DIRETRIZES RIGOROSAS:
    1. Responda APENAS com base estrita no CONTEXTO DE EXPLANATION fornecido. Não invente regras, fluxos ou códigos que não estejam no texto.
    2. Utilize a analogia do 'Bolo' APENAS quando for explicar de forma introdutória os conceitos fundamentais de OV, OP e Receita (Módulo TRN-MES-CONC-001). 
    3. NUNCA utilize analogias culinárias para explicar códigos de automação N2 (ex: 3001, 3002), mensagens SAP ou regras de logs de movimento. Nessas secções, seja estritamente técnico, direto e objetivo.
    4. Evite repetições de parágrafos na resposta.
    5. REQUISIÇÃO DE CÓPIA LITERAL (VERBATIM): Sempre que a pergunta do aluno exigir a explicação de um fluxo, etapas (Fases 1 a 4) ou mensagens técnicas, você DEVE reproduzir os passos, tópicos e descrições **exatamente com as mesmas palavras, termos técnicos e estrutura de bullets** do MATERIAL DE TREINAMENTO DISPONÍVEL. É estritamente proibido parafrasear, resumir ou alterar a redação original dos conceitos técnicos, códigos de automação N2 ou regras de log.
    """)
    @UserMessage("""
        MATERIAL DE TREINAMENTO DISPONÍVEL:
        {contexto}

        DÚVIDA DO ALUNO:
        {pergunta}
        """)
    String chat(@V("contexto") String contexto, @V("pergunta") String pergunta);
}


