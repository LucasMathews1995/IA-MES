=== INÍCIO_IOP ===
ID: IOP-MES-REL-001
TÍTULO: Relacionamentos das Entidades do Sistema
MÓDULO: Programação e MES

Esta IOP tem o intuito de mostrar como as entidades se relacionam para o melhor entendimento de como as programações e o MES são feitos.

### Diagrama de Relacionamento

```mermaid
graph TD
    OV[Ordem Venda] -- "1 : N" --> OP[Ordem de Produção]
    OP -- "1 : 1" --> EQ[Equipamento]
    L[Lote] -- "N : 1" --> OP
    R[Receita] --> OV
    R --> OP
=== FIM_IOP === 

s