# Assistente Inteligente RAG para Suporte MES (rag-suporte-backend)

Este projeto implementa um assistente inteligente baseado em RAG (Retrieval-Augmented Generation) para centralizar, dinamizar e democratizar o acesso ao conhecimento técnico corporativo no chão de fábrica. 

O sistema resolve a dificuldade de rápida assimilação de termos técnicos, manuais operacionais extensos e regras de negócio complexas inerentes ao sistema MES (Manufacturing Execution System), reduzindo o MTTR (Mean Time to Repair) e a dependência de suporte especializado de terceiro nível.

---

## 🏗️ Visão Geral da Arquitetura

A arquitetura on-premise conecta componentes modernos de desenvolvimento backend em Java com inteligência artificial executada localmente, garantindo zero exposição de dados industriais sensíveis.

*   **Camada de Apresentação / API:** Microsserviços desenvolvidos em Java (Quarkus) expostos via REST.
*   **Camada de Orquestração de IA:** LangChain4j gerenciando o fluxo de RAG, embeddings e engenharia de prompts.
*   **Camada de Persistência Vetorial:** PostgreSQL com extensão `pgvector` armazenando os chunks e embeddings.
*   **Motor de Inferência Local:** Ollama executando modelos open-source (ex: Llama 3) de forma isolada e segura.
*   **Autenticação:** Gerenciamento de acessos via Keycloak (OIDC).

---

## ✨ Funcionalidades Principais

*   **Consulta Textual em Linguagem Natural:** Submissão de perguntas sobre falhas de processos, IOPs e regras de negócio do MES.
*   **Filtragem Contextual por Metadados:** Aplicação de filtros prévios baseados em código do equipamento, setor da fábrica e módulo do MES antes da busca vetorial.
*   **Recuperação de Contexto Vetorial:** Uso de similaridade de cosseno via `pgvector` para resgatar os trechos mais relevantes.
*   **Geração de Respostas Aumentadas:** Síntese de respostas precisas e contextualizadas, mitigando alucinações técnicas e sem tomada de decisão autônoma ou comandos automáticos nas máquinas.

---

## 🚀 Como Executar o Projeto

O projeto utiliza o framework Quarkus. Certifique-se de ter o Docker, Docker Compose e o JDK 17+ instalados.

### 1. Subindo a Infraestrutura (Banco de Dados, Ollama e Keycloak)
Inicie os serviços auxiliares em background:
```bash
docker-compose up -d
