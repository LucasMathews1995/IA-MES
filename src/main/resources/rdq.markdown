# Orientação para Análises de Decisão de Qualidade (RDQ)

Esta diretriz auxilia nas análises que envolvem decisões de qualidade. A consulta pode ser realizada através do monitor **Consulta Geral de lotes em RDQ** ou ao verificar os detalhes da mensagem **QMRDQ** para o lote em questão.

> **Nota:** Quando a mensagem QMRDQ apresenta o Texto-opção **RDQ**, trata-se de uma retirada de RDQ enviada pelo SAP.

## Tipos de Decisão SAP e Ações no MES

Abaixo estão os tipos de decisão enviados pelo SAP e o comportamento correspondente no sistema MES:

* **`NO_RDQ_SAME_OV`**
  * **Ação MES:** Retira o lote de RDQ, mantendo-o na mesma OV e alterando apenas a versão da OV.

* **`NO_RDQ_RETRABALHO_MESMA_OV`**
  * **Ação MES:** Retira o lote de RDQ, mantendo-o na mesma OV e alterando apenas a versão da OV. Apaga o programa do equipamento e o eventual programa de embalagem.

* **`NO_RDQ_ESTOQUE_LIVRE`**
  * **Ação MES:** Apenas retira o lote de RDQ.

* **`NO_RDQ_RETRABALHO_OUTRA_OV`**
  * **Ação MES:** Retira o lote de RDQ e altera o lote de OV. Apaga o programa do equipamento e o eventual programa de embalagem.

* **`NO_RDQ_SUCATA`**
  * **Ação MES:** Apaga o programa do equipamento e o eventual programa de embalagem.

* **`CHANGE_BATCH`** *(Exclusivo Galvasud)*
  * **Ação MES:** Atualiza as informações do lote sem colocá-lo ou retirá-lo de RDQ (ação de atualização, não de decisão de qualidade).

## Programação de Lotes de Inclusão

Quando o SAP envia um lote como inclusão (ex.: lotes devolvidos pelo cliente ou lotes que tiveram os dados de produção apagados pelo encerramento da OV e retornam para novo processamento), o processo ocorre geralmente via qualidade (envio de QMRDQ).

**Fluxo de Processamento:**
1. Inicialmente, as QMRDQ’s recebem o status `I`. O programa processa e cria uma linha de genealogia com a informação: *"Lote incluído no sistema"*.
2. Na rodada seguinte do JOB, a QMRDQ de colocação é processada, inserindo o lote em qualidade.
3. Quando a QMRDQ de retirada for processada, o sistema verifica se o lote é de inclusão.
   * **Exceção de Apagamento:** Se for um lote de inclusão, mesmo que a decisão de qualidade exija o apagamento do programa (`NO_RDQ_RETRABALHO_MESMA_OV` ou `NO_RDQ_RETRABALHO_OUTRA_OV`), o programa **não é apagado**. Isso evita que o lote fique sem programa no MES.
   * **Atenção:** Caso o lote ainda possua genealogia ativa no MES por algum outro motivo, ele não entrará como inclusão e o programa será apagado *(Incidente de referência: 300-300438)*.

---
**Observação:** Informações adicionais sobre os tipos de retrabalho podem ser consultadas na IOP correspondente: **Retrabalho - ST_OP_RETRAB**.
