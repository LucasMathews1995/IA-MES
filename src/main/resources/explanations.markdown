=== INÍCIO_IOP ===
ID: IOP-MES-INV-001
TÍTULO: Fluxo de Lotes INV e Apontamento de Produção na MCB
MÓDULO: Integração SAP e Chão de Fábrica

### 1. Definição e Fluxo Base
* **Lote INV:** Nomenclatura utilizada para lotes que consomem uma placa oriunda do estoque.
* **Programação:** Através do SAP (tela **2.1.1**), o sistema envia uma programação para a produção de lotes/blocos com as iniciais **PS**, que serão processados/produzidos na MCB.

### 2. Tratamento de Quebra de Lote (Produção Inferior à Programada)
Caso a placa INV seja consumida e gere uma quantidade de blocos menor do que a programada originalmente, o sistema MES divide a comunicação de retorno ao SAP em duas frentes:

* **Blocos Não Gerados / Sucateados:** A diferença de blocos que não foi produzida retorna para o SAP através da mensagem **MCB_SUC**.
  * *Verificação e Consulta:* O fluxo dessa mensagem de sucata/perda pode ser consultado no monitor através das telas **1.3.1** e **2.3.1**.
* **Blocos Efetivamente Produzidos:** A quantidade restante, que foi realmente gerada e concluída com sucesso na MCB, retorna ao SAP através da interface padrão de produção **PI_PROD**.
=== FIM_IOP ===
