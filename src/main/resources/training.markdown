=== INÍCIO_TREINAMENTO ===
ID: TRN-MES-LOG-001
TÍTULO: Guia de Análise do Log de Movimentos de Lotes
MÓDULO: Rastreabilidade e Log de Produção

### Introdução à Consulta de Movimentos
Para analisar a trajetória e o histórico de um lote na tela de **Consulta de Movimento (Log de Movimentos)**, é obrigatório informar o número do lote na pesquisa. (Nota: A tela 3.1 é exclusiva para validação de programação/abastecimento, não sendo a tela do Log de Movimentos). O ciclo de movimentação no log é sequencial e repete-se (Programado > Abastecido > Apontado > Aprovado) para cada equipamento pelo qual o material passa, dividido nas 4 fases abaixo:

### Fase 1: Programação e Gestão de Estoque
* **LOTE INCLUIDO NA CSN_PROGRAMA_BRONER:** O lote foi inserido na tabela de programação. Esta informação pode ser validada na tela 3.1(tela de programa de lote).
* **ALTERAÇÃO NO ESTOQUE:** Registro de movimentação do material no estoque.
* **PROGRAMADO NO RECURSO:** O PCP efetuou a programação. Na tela 3.1, a flag de "Programado" passa para SIM, fazendo com que o lote fique disponível na tela de abastecimento do equipamento (ex: LTF3).

### Fase 2: Integração com a Automação (N2)
O sistema MES comunica com a automação (N2) através de mensagens assíncronas de envio e reconhecimento (ACK).
* **3001 (DADOS DE PDI ENVIADO):** O MES envia as informações do lote para o N2.
* **3002 (ACK DADOS DE PDI):** O N2 responde com "STATUS = OK", confirmando a receção dos dados.
* **3101 (SEQUENCIA DE PRODUCAO ENVIADA):** O MES envia a ordem/fila correta das bobinas para o N2.
* **3102 (ACK SEQUENCIA DE PRODUCAO):** O N2 confirma a receção da lista de sequência.
* **Alerta Operacional:** Mensagens como *"NÃO FOI POSSÍVEL ABASTECER A BOBINA"* indicam falha ou informação incompleta. Requer alinhamento com o operador que executou a ação ou escalonamento para o Analista MES.

### Fase 3: Execução e Apontamento (Chão de Fábrica)
* **ABASTECIDO NO RECURSO:** O material foi fisicamente posicionado. Na tela 3.1, a flag "ABAST." muda para SIM.
* **TOTALIZADO NO RECURSO (PASSOU_NO_EQUIPAMENTO):** Indica que o lote foi totalmente consumido no equipamento. Se a capacidade da máquina for inferior ao tamanho do lote, este será repartido em pedaços, gerando lotes filhos.
* **ALTERAÇÃO NO ESTOQUE:** Nova atualização após o consumo do material.
* **ATUALIZADO O BATCH_VIRTUAL_PROD:** Atualização do lote virtual (conceito detalhado na IOP específica de Lotes Virtuais).
* **PRODUZIDO NO RECURSO:** O utilizador efetuou o apontamento da produção diretamente na tela do equipamento.

### Fase 4: Qualidade e Ciclo de Encerramento
* **APROVADO NA TELA DE QUALIDADE:** A equipa da Qualidade avalia e aprova o lote. Esta ação despoleta a subida de mensagens de integração para o SAP (`PI_PROD` para produção, `PI_CONS` para consumo, ou `QMRDQ` para qualidade).
* **3300 (APONTAMENTO DE PRODUCAO E QUALIDADE):** O MES notifica o N2 de que a produção e a qualidade foram apontadas.
* **3301 (ACK APONTAMENTO):** O N2 confirma a receção da mensagem 3300.
* **Novo Ciclo:** Uma nova entrada *"LOTE INCLUIDO NA CSN_PROGRAMA_BRONER"* indica que o lote já foi programado para a próxima etapa, reiniciando todo o fluxo no equipamento seguinte.
=== FIM_TREINAMENTO ===




=== INÍCIO_TREINAMENTO ===
ID: TRN-MES-CONC-001
TÍTULO: Nomenclaturas e Hierarquia de Produção (OV, OP, Receita e Lote)
MÓDULO: Conceitos Fundamentais MES

### Visão Geral e Analogia Prática (O Bolo)
A relação entre Ordem de Venda (OV), Ordem de Produção (OP), Receita e Lote de Produção pode ser explicada utilizando a analogia do "Bolo":
* **Lote (O Bolo):** Representa o lote de produção, que é o foco central e a entidade física principal do sistema MES.
* **Receita (A Ficha Técnica):** Define os ingredientes, as quantidades e as características esperadas do bolo (o padrão).
* **Ordem de Produção - OP (O Modo de Preparo):** São as instruções detalhadas de como o operador e o equipamento atual vão misturar e processar essa receita para obter o resultado final.
* **Ordem de Venda - OV (A Demanda do Cliente):** É o pedido final do cliente que originou a necessidade de fabricação.

### 1. Ordem de Venda (OV)
A Ordem de Venda representa o pedido final do cliente da CSN (ex.: componentes automotivos, latas para leite em pó).
* **Abrangência:** A OV engloba e conecta toda a cadeia produtiva necessária para o atendimento da demanda.
* **Relação com OPs:** Uma única OV contém múltiplas Ordens de Produção (OPs). Isto acontece porque a fabricação exige etapas sequenciais em diferentes equipamentos e plantas (desde a produção de gusa no MES Redução até a embalagem nos sistemas MES Volta Redonda, Paraná ou Galvasud).

### 2. Ordem de Produção (OP)
A OP dita as instruções exatas de como um determinado lote deve ser processado num equipamento específico.
* **Função Operacional:** Funciona como o guia prático de fabricação na máquina.
* **Ajustes Físicos:** É através da OP que o operador visualiza as especificações técnicas (ex.: atingir uma espessura exata em milímetros) e os ajustes necessários no equipamento (calibrações, níveis, degraus) para garantir a conformidade com a OV.

### 3. Receita
A Receita atua como o elo estrutural entre os requisitos do cliente (OV) e a execução na máquina (OP).
* **Parâmetros:** Traduz as exigências do cliente em configurações operacionais diretas, definindo dimensões obrigatórias como espessura, comprimento e largura.

### Tabela Resumo do Ciclo
| Operação / Conceito | Descrição / Papel no Sistema MES |
| :--- | :--- |
| **OV (Ordem de Venda)** | Pedido final do cliente (Origem da demanda global). |
| **OP (Ordem de Produção)** | Instruções de processamento e ajustes físicos no equipamento. |
| **Receita** | Definição técnica dos parâmetros, dimensões e padrões de fabricação. |
| **Lote** | Entidade física central rastreada ao longo de todo o ciclo produtivo. |

Em suma, a OP é a etapa física de fabricação, a OV é a representação da demanda do cliente, a Receita é a definição dos parâmetros e o Lote é o foco central do sistema MES.
=== FIM_TREINAMENTO ===



=== INÍCIO_TREINAMENTO ===
ID: TRN-MES-PROG-001
TÍTULO: Guia de Programação e Dicionário da Tela 3.1
MÓDULO: Programação e Controle de Chão de Fábrica

### 1. Programação e Controle de Fluxo
A programação é o coração do controle do lote no processo produtivo. Através dela, o operador consegue identificar em tempo real:
* A localização atual do lote na fábrica.
* O seu status exato de produção.
* O próximo destino na cadeia de fabricação.

### 2. Regra de Consistência (Trava de Duplicidade)
Para manter a base de dados consistente, o sistema possui uma trava de segurança rigorosa: **não é permitida a duplicidade na combinação OP + Lote + Recurso (Equipamento)**.
* **Regra de Ouro:** Um lote (ou os seus lotes filhos) só pode passar **uma única vez** pelo mesmo equipamento dentro de uma mesma OP.
* **Exceção de Retrabalho (RDQ):** Se o lote reprovar na qualidade e precisar de regressar ao mesmo equipamento, o sistema exige a criação de uma **nova OP** com um processo diferente ou ajustado.
* **Dica de Diagnóstico:** Se deparar com um erro de duplicação no ecrã, consulte a tabela `XXX_LOTE_PRODUZIDO` na base de dados para confirmar se o registro já existe.

### 3. Lote Virtual
O conceito de Lote Virtual serve para definir previamente a identificação do próximo lote no fluxo (frequentemente utilizado quando a etapa seguinte é a embalagem).
* **Mecânica:** O sistema SAP envia dois programas em simultâneo (um para a equipamento atual e outro para o equipamento seguinte(Embalagem)). Ao finalizar a etapa atual, o MES transita a identificação do lote automaticamente.
* **Objetivo:** Garantir a continuidade do fluxo contínuo, evitar perdas de tempo com reidentificações manuais e assegurar a rastreabilidade total.

### 4. Dicionário de Campos: Tela 3.1 (Consulta de Programa dos Lotes)
A Tela 3.1 é o principal monitor de acompanhamento. Abaixo estão os significados dos campos apresentados:

**Campos de Materiais e Quantidades:**
* **Lote Cons / Mat Cons:** O lote e o código do material que estão a ser consumidos na etapa atual.
* **Lote Prod / Mat Prod:** O lote gerado (original ou filho) e o código do material produzido após o processamento.
* **Q Cons / Q Prod:** As quantidades exatas consumidas e efetivamente produzidas.
* **B V Cons / B V Prod:** Identificações da Bobina Virtual consumida e produzida.
* **Item Lam:** A sequência do item dentro do processo de laminação.

**Campos de Roteamento e Equipamento:**
* **Eqpto:** A sigla do equipamento onde o lote está a ser processado no momento.
* **Prox Eq:** O equipamento de destino após a conclusão da etapa atual.
* **Linha:** A linha de produção física onde o lote se encontra.

**Campos de Ordem e Status (Acompanhamento):**
* **OP (Ordem de Produção):** O código que define o processo produtivo a ser seguido.
* **OP Retrab.:** Quando preenchido, sinaliza que o lote sofreu uma anormalidade e está associado a uma OP de retrabalho.
* **OV (Ordem de Venda):** O pedido do cliente associado àquela produção.
* **R/V:** Indica se a condição do programa é Real (R) ou Virtual (V).
* **Nº Prog:** O identificador numérico único daquela programação.
* **Prog. (Programado):** Status (SIM/NÃO) que indica se o lote já tem a programação definida.
* **Em Prod.:** Status (SIM/NÃO) que indica se a máquina está a executar o lote naquele preciso momento.
* **Aprovado:** Status que confirma se o lote passou nos critérios da Qualidade.
* **Env. SAP:** Status que confirma se os dados já foram integrados com sucesso no SAP.

**Campos de Rastreabilidade e Integração (Datas):**
* **Id:** O identificador único do registo interno da base de dados.
* **Data SAP:** A data e hora oficiais do registo ou envio ao sistema SAP.
* **Data Download:** O momento em que os dados desceram do SAP para o MES local.
* **Data Proq:** O momento em que a programação foi processada pelo sistema e ficou disponível para a operação na máquina.
=== FIM_TREINAMENTO ===

=== INÍCIO_TREINAMENTO ===
ID: TRN-MES-LOG-003
TÍTULO: Fluxo Básico de Mensagens
MÓDULO: Rastreabilidade e Log de Produção

### Introdução à Consulta de Movimentos
Para analisar a trajetória e o histórico de um lote no sistema, é obrigatório informar o número do lote na pesquisa. O ciclo de movimentação é sequencial e repete-se (Programado > Abastecido > Apontado > Aprovado) para cada equipamento pelo qual o material passa.

### Fase 1: Programação e Gestão de Estoque
* **LOTE INCLUIDO NA CSN_PROGRAMA_BRONER:** O lote foi inserido na tabela de programação. Esta informação pode ser validada na tela 3.1.
* **ALTERAÇÃO NO ESTOQUE:** Registro de movimentação do material no estoque.
* **PROGRAMADO NO RECURSO:** O PCP efetuou a programação. Na tela 3.1, a flag de "Programado" passa para SIM, fazendo com que o lote fique disponível na tela de abastecimento do equipamento (ex: LTF3).

### Fase 2: Integração com a Automação (N2)
O sistema MES comunica com a automação (N2) através de mensagens assíncronas de envio e reconhecimento (ACK).
* **3001 (DADOS DE PDI ENVIADO):** O MES envia as informações do lote para o N2.
* **3002 (ACK DADOS DE PDI):** O N2 responde com "STATUS = OK", confirmando a receção dos dados.
* **3101 (SEQUENCIA DE PRODUCAO ENVIADA):** O MES envia a ordem/fila correta das bobinas para o N2.
* **3102 (ACK SEQUENCIA DE PRODUCAO):** O N2 confirma a receção da lista de sequência.
* **Alerta Operacional:** Mensagens como *"NÃO FOI POSSÍVEL ABASTECER A BOBINA"* indicam falha ou informação incompleta. Requer alinhamento com o operador que executou a ação ou escalonamento para o Analista MES.

### Fase 3: Execução e Apontamento (Chão de Fábrica)
* **ABASTECIDO NO RECURSO:** O material foi fisicamente posicionado. Na tela 3.1, a flag "ABAST." muda para SIM.
* **TOTALIZADO NO RECURSO (PASSOU_NO_EQUIPAMENTO):** Indica que o lote foi totalmente consumido no equipamento. Se a capacidade da máquina for inferior ao tamanho do lote, este será repartido em pedaços, gerando lotes filhos.
* **ALTERAÇÃO NO ESTOQUE:** Nova atualização após o consumo do material.
* **ATUALIZADO O BATCH_VIRTUAL_PROD:** Atualização do lote virtual (conceito detalhado na IOP específica de Lotes Virtuais).
* **PRODUZIDO NO RECURSO:** O utilizador efetuou o apontamento da produção diretamente na tela do equipamento.

### Fase 4: Qualidade e Ciclo de Encerramento
* **APROVADO NA TELA DE QUALIDADE:** A equipa da Qualidade avalia e aprova o lote. Esta ação despoleta a subida de mensagens de integração para o SAP (`PI_PROD` para produção, `PI_CONS` para consumo, ou `QMRDQ` para qualidade).
* **3300 (APONTAMENTO DE PRODUCAO E QUALIDADE):** O MES notifica o N2 de que a produção e a qualidade foram apontadas.
* **3301 (ACK APONTAMENTO):** O N2 confirma a receção da mensagem 3300.
* **Novo Ciclo:** Uma nova entrada *"LOTE INCLUIDO NA CSN_PROGRAMA_BRONER"* indica que o lote já foi programado para a próxima etapa, reiniciando todo o fluxo no equipamento seguinte.
=== FIM_TREINAMENTO ===

=== INÍCIO_TREINAMENTO ===
ID: TRN-MES-OPR-004
TÍTULO: Fluxo Operacional de Programação, Apontamento e Integração SAP
MÓDULO: PCP, Produção, Qualidade e Integração SAP

### Introdução ao Fluxo Operacional de Telas
O ciclo operacional de um lote no sistema MES inicia-se com o recebimento das diretrizes do SAP, passando pelo planejamento do PCP, execução no chão de fábrica e encerramento na Qualidade, culminando no retorno das informações ao SAP. Caso não ocorram desvios, este ciclo repete-se sequencialmente para cada equipamento da rota produtiva.

### Fase 1: Recebimento de Mensagens do SAP (Tela 2.1.1)
- **MENSAGENS DE PROGRAMAÇÃO E DECISÃO DE RDQ:** As informações de programação e as decisões de Relatório de Desvio de Qualidade (RDQ) são enviadas diretamente pelo SAP e recepcionadas no MES através da tela **2.1.1**.
- **ATRIBUIÇÃO DO PROGRAMA:** Nesta etapa de entrada, o lote recebe o programa produtivo correspondente para iniciar o seu fluxo na fábrica.

### Fase 2: Planejamento e Sequenciamento pelo PCP (Telas 4j11 e 4i11)
- **RASCUNHO DE PROGRAMAÇÃO (TELA 4j11):** Após receber o programa na tela 2.1.1, o lote passa a ser exibido para a equipe de Planejamento e Controle da Produção (PCP) na tela **4j11**, que funciona como uma área de rascunho da programação.
- **LOTES RESEQUENCIADOS (TELA 4i11):** O PCP transfere o lote da tela 4j11 para a tela **4i11**, que corresponde à fila de lotes resequenciados (equivalente à fila da tela de abastecimento) do equipamento selecionado.
- **ORDENAÇÃO DE FILA:** Através da tela 4i11, o PCP gerencia a fila produtiva, definindo a ordenação dos lotes e a sequência exata em que serão abastecidos na máquina.

### Fase 3: Abastecimento e Apontamento de Produção (Chão de Fábrica)
- **ENTRADA EM PRODUÇÃO (TELA DE ABASTECIMENTO):** Na interface de operação do equipamento, o operador visualiza o lote programado na fila e aciona a flag **"Abastecido"**, registrando no sistema que o material entrou efetivamente em produção.
- **CONCLUSÃO DO PROCESSAMENTO (TELA DE APONTAR PRODUÇÃO):** Assim que o lote é processado e sai fisicamente do equipamento, ele fica disponível na tela de **Apontar Produção**.
- **CONFIRMAÇÃO DE PRODUÇÃO:** Nessa tela, o operador aciona a flag **"Produzido"**, confirmando o término da etapa produtiva na máquina.

### Fase 4: Qualidade e Auditoria de Subida para o SAP (Tela 2.3.1)
- **APROVAÇÃO NA QUALIDADE (TELA DE APONTAMENTO DE QUALIDADE):** Após ser apontado como produzido, o lote é direcionado para a tela de **Apontamento de Qualidade**. O responsável avalia o material, marca o lote como **"Aprovado"** e dispara o envio das informações para o SAP.
- **VERIFICAÇÃO DE INTEGRAÇÃO MES → SAP (TELA 2.3.1):** Para auditar e confirmar se a mensagem do lote subiu corretamente do MES para o SAP, realiza-se a consulta na tela **2.3.1** inserindo o identificador (nome) do lote no campo **"LINHA"**.
- **Continuidade do Ciclo:** Confirmada a subida da mensagem para o SAP sem intercorrências, o ciclo naquele recurso é encerrado e todo o fluxo se repetirá normalmente nos equipamentos subsequentes da linha.
=== FIM_TREINAMENTO ===