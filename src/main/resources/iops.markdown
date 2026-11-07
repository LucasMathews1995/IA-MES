=== INÍCIO_IOP ===
ID: IOP-MES-RDQ-001
TÍTULO: Consulta Geral e Fluxo de Decisões de Qualidade (RDQ)
MÓDULO: Qualidade / Integração SAP e MES

### 1. Consulta de Lotes em Qualidade
Quando um lote entra em qualidade, a monitorização deve ser feita através da tela **Acabamento – Consulta Geral de Lotes em RDQ**. Ao pesquisar pelo número do lote, o sistema retorna os seguintes campos de identificação:
* **Data de Entrada:** Data em que o lote entrou em qualidade (Interface de envio: 2.3.1 QMRDQ).
* **Defeito:** Indica o tipo de anomalia identificada no lote.
* **Equipamento:** Máquina/recurso responsável por direcionar o lote para a Qualidade (RDQ).
* **Matrícula RDQ:** Identificação do inspetor ou usuário responsável pela análise e liberação.
* **Data de Saída:** Representa a data da decisão de saída da RDQ, retornada pela interface 2.1.1 (campo QMRDQ).

### 2. Solução RDQ e Interfaces de Comunicação
A "Solução RDQ" corresponde à decisão processada e devolvida pelo SAP após a análise do lote.
* **Envio da Colocação (MES -> SAP):** Tela 2.3.1 
* **Retorno da Decisão (SAP -> MES):** Tela 2.1.1 

### 3. Tipos de Decisão SAP e Ações no MES
* **`NO_RDQ_SAME_OV`**: Remove o lote da RDQ e mantém a Ordem de Venda (OV), alterando apenas a sua versão.
* **`NO_RDQ_RETRABALHO_MESMA_OV`**: Remove o lote da RDQ e mantém a mesma OV com nova versão. **Ação:** Apaga o programa do equipamento e o programa de embalagem (se houver).
* **`NO_RDQ_ESTOQUE_LIVRE`**: Remove o lote da RDQ sem efetuar outras alterações.
* **`NO_RDQ_RETRABALHO_OUTRA_OV`**: Remove o lote da RDQ e altera a Ordem de Venda (OV). **Ação:** Apaga o programa do equipamento e o programa de embalagem (se houver).
* **`NO_RDQ_SUCATA`**: Indica o descarte do lote. **Ação:** Apaga o programa do equipamento e o programa de embalagem (se houver).
* **`CHANGE_BATCH` (Exclusivo Galvasud)**: Apenas atualiza as informações do lote no sistema. Não realiza inclusão nem retirada de RDQ.

### 4. Lógica de Redirecionamento (Trabalhar em Outra OV)
Caso um lote em análise apresente desvios e não atenda aos requisitos da OV original, a Qualidade pode determinar que ele não seja sucateado, mas sim redirecionado. 
* **Aplicação:** O lote é transferido para uma nova Ordem de Venda cujos requisitos sejam compatíveis com suas características atuais (ex.: um produto com desvio que pode ser reaproveitado para produção de latas de refrigerante), evitando desperdício de material.

### 5. Rastreabilidade de Retrabalho (Banco de Dados)
A gestão do status de retrabalho ocorre a nível de base de dados utilizando flags específicas:
* **`CSN_PROGRAMA_BRONER`**: Tabela no MES que serve de espelho para a tabela de programas do SAP. Possui o campo/flag **`ST_OP_RETRAB`**.
* **Trâmite de Retrabalho:** Quando o lote é consumido na produção e recebe uma decisão de qualidade para retrabalho, o valor do lote é transferido da tabela **`XXX_LOTE_CONSUMIDO`** para o campo **`LOTE_RETRABALHO`**, mantendo o histórico de que a peça foi interrompida e enviada para reprocessamento.
=== FIM_IOP ===

=== INÍCIO_IOP ===
ID: IOP-MES-ERR-001
TÍTULO: Diagnóstico - Repetição de OP e Conflito de OV no Abastecimento
MÓDULO: Abastecimento e Consistência de Programação

### 📌 Tema
**Regra de Repetição de OP (Ordem de Produção) e Validação de Ordem de Venda (OV)**

### ⚠️ Sintoma
* **No processo produtivo:** O sistema identifica que o programa de um lote possui exatamente a mesma OP do programa imediatamente anterior.
* **No sistema (Tela de Abastecimento):** Ao tentar abastecer um equipamento (ex: recurso LRC 2), o sistema Heimdall dispara um *pop-up* de alerta amarelo com a mensagem: *"O lote [Nº do Lote Atual] abastecido após o lote [Nº do Lote Anterior] é de uma OV diferente!"*.
* **Impacto:** Este cenário gera risco de ocorrência indevida e afeta diretamente a rastreabilidade tanto do lote consumido quanto do lote produzido por aquela OP.

### 🛠️ Solução (Plano de Ação)
1. **Análise de Continuidade:** Sempre que houver programas consecutivos associados à mesma OP, analise rigorosamente os dados do material consumido e do produto gerado.
2. **Validação do Alerta de OV:** O alerta do Heimdall atua como uma trava de segurança visual. Verifique se a mudança de Ordem de Venda (OV) no meio da execução da mesma Ordem de Produção (OP) é um cenário planejado pelo PCP (ex: aproveitamento de setup) ou um erro de sequenciamento físico das bobinas.
3. **Correção:** 
   * Se for um **erro de sequenciamento**, remova/desabasteça o lote incorreto e respeite a fila do programa original.
   * Se for um **cenário planejado**, valide se os parâmetros do equipamento (Receita) estão adequados para a nova OV antes de confirmar a gravação na tela de abastecimento.
=== FIM_IOP ===

=== INÍCIO_IOP ===
ID: IOP-MES-EST-001
TÍTULO: Procedimento de Estorno de Bobinas
MÓDULO: Operação Heimdall / Gestão de Lotes

### Objetivo
Padronizar o passo a passo para acessar os menus de estorno de bobinas no sistema MES (Heimdall Cliente), definindo o caminho correto dependendo do status de aprovação do lote.

### Passo a Passo Geral
1. Acesse o sistema **Heimdall Cliente** na sua estação de trabalho.
2. Na barra de navegação superior, clique no menu **Ferramentas**.

### Cenário A: Estorno de Bobinas Ainda Não Aprovadas
Para efetuar o estorno de bobinas que ainda não passaram pela aprovação de qualidade:
1. Com o menu **Ferramentas** aberto, posicione o mouse sobre a opção **Estorno Usuário**.
2. No submenu lateral que será aberto, clique em **Estornar Bobinas**.

### Cenário B: Estorno de Bobinas Já Aprovadas / Resolução de Problemas
Para identificar problemas relacionados a estornos ou manusear bobinas que já foram aprovadas:
1. Com o menu **Ferramentas** aberto, posicione o mouse sobre a opção **Monitoração**.
2. No submenu lateral que será aberto, clique na opção **Estornar Bobinas**.
=== FIM_IOP ===

=== INÍCIO_IOP ===
ID: IOP-MES-ERR-002
TÍTULO: Diagnóstico e Escalonamento - Duplicidade de Programas Ativos
MÓDULO: Programação e Integração (MES / SAP)

### 📌 Cenário e Sintoma
* **Sintoma:** O usuário depara-se com uma mensagem de erro no sistema ao tentar manusear um lote. *(Nota: Esta mensagem pode ocorrer em diversas telas caso o lote se encontre duplicado no sistema).*
* **Causa Provável:** O lote possui mais de um programa ativo no MES para o mesmo equipamento, sem que nenhum deles esteja classificado como operação de retrabalho.

### 🛠️ Procedimento de Análise (Passo a Passo)

**1. Consulta Inicial (Tela 3.1)**
* Acesse a **Tela 3.1 (Consulta de Programa dos Lotes)**.
* Pesquise o número do lote e valide quantos programas foram recebidos e estão atualmente ativos no MES.
* **Gatilho de Análise:** Se identificar **2 programas para o mesmo equipamento** e **nenhum** estiver marcado como "Retrabalho", avance para o passo seguinte.

**2. Verificação do Status de Produção (MES)**
É necessário confirmar se o lote já foi fisicamente produzido. Esta validação pode ser feita através de três opções:
* **Na própria Tela 3.1:** Analise os campos de quantidades produzidas e status de aprovação.
* **Na Tela 5741:** Desmarque a opção "Pendentes" e informe a data de produção (caso o usuário já tenha fornecido essa informação no relato).
* **Na Tela 4.1 (Genealogia):** Consulte a árvore de rastreabilidade do lote para confirmar os apontamentos.

**3. Confirmação de Integração (SAP)**
* Acesse o sistema SAP e utilize a transação **MB51**.
* Consulte o histórico do lote para confirmar se o movimento de produção já se encontra devidamente atualizado no lado do SAP.

**4. Escalonamento e Registro**
* Documente todas as evidências coletadas (se já foi produzido no MES e se já subiu para o SAP) no ticket do incidente.
* Encaminhe imediatamente o chamado para a equipe especialista de **GSO Manutenção MES** para que procedam com a correção sistêmica da duplicidade.
=== FIM_IOP ===

=== INÍCIO_IOP ===
ID: IOP-MES-ERR-003
TÍTULO: Diagnóstico e Sincronização de Mensagens RDQ (MES vs SAP)
MÓDULO: Integração de Qualidade (SAP/MES)

### 📌 Objetivo
Orientar a análise de divergências ou bloqueios no processamento de mensagens de Qualidade (RDQ), garantindo que as informações entre o MES e o SAP estão sincronizadas antes do escalonamento.

### 🛠️ Procedimento de Análise (Passo a Passo)

**1. Identificação da Mensagem e do Lote (MES)**
* Acesse a tela **Monitoração - 2.1.1 Detalhes Instruções SAP -> MES**.
* Consulte o ID específico da mensagem que apresenta anomalia.
* No corpo da mensagem, localize o campo **`PPPI_BATCH`** (apresentado na coluna *Característica*).
* A partir desse campo, identifique o número do lote correspondente (na coluna de valor/linha).

**2. Verificação de Histórico no SAP**
* Acesse o sistema SAP e abra a transação **MB51**.
* Pesquise pelo lote identificado no passo anterior.
* Identifique e anote as datas exatas em que o lote entrou e saiu do status de RDQ no SAP.

**3. Comparação de Registros (MES)**
* Retorne ao sistema MES e abra a tela **Acabamento - Consulta Geral de Lotes em RDQ**.
* Compare o último registro de RDQ presente nesta tela com as datas de movimentação obtidas na transação MB51 do SAP.

**4. Escalonamento e Resolução**
* Adicione todas as evidências e informações coletadas no chamado de suporte.
* Notifique imediatamente a **equipe SAP** para que procedam com a análise.
* A equipe SAP informará se a mensagem presa deve ser forçada a processar ou se pode ser descartada.
* Em caso de orientação para descarte, após o retorno do chamado, a equipe (ou o N2) deverá realizar a tratativa final para liberar o fluxo.
=== FIM_IOP ===

=== INÍCIO_IOP ===
ID: IOP-MES-ERR-004
TÍTULO: Diagnóstico - Lote em RDQ no MES mas não no SAP
MÓDULO: Integração de Qualidade (MES VR / SAP)

### 📌 Cenário e Sintoma
* **Sistema:** MES VR (Volta Redonda).
* **Sintoma:** O lote encontra-se retido em RDQ no sistema MES, mas não consta com status de RDQ no sistema SAP.

### 🛠️ Procedimento de Análise (Passo a Passo)

**1. Verificação de Decisão Manual no SAP (MB51)**
* Acesse a transação **MB51** no SAP.
* Verifique a data em que ocorreu a decisão de qualidade para o lote.
* Confirme se a decisão foi realizada de forma manual: analise o campo "Usuário" e valide se este contém uma matrícula de usuário (em vez do usuário de sistema `JOBMES`).
* *Regra de Negócio:* Nesta condição (decisão manual), o SAP não envia a mensagem automática para retirar o lote de RDQ no MES.

**2. Verificação de Falha de Envio no Monitor MES (Tela 2.3.1)**
* Caso o lote não possua registro de entrada de RDQ no SAP, a falha pode ter ocorrido no envio da mensagem inicial (`RDQ_MES`) do MES para o SAP.
* Acesse a tela **2.3.1 Detalhes MES -> SAP**.
* Confirme se a mensagem ainda se encontra retida no monitor e se apresenta status de erro.

### 🔄 Matriz de Escalonamento (Direcionamento de Chamado)
Após a análise, o encaminhamento do incidente deve seguir esta regra estrita:
* **Para a Equipe SAP:** Se identificar que a mensagem se encontra com erro no monitor do MES (Tela 2.3.1).
* **Para a Equipe MES:** Se identificar que a decisão de qualidade ocorreu de forma manual no SAP, ou caso não consiga localizar a mensagem de integração no monitor do MES.
=== FIM_IOP ===

=== INÍCIO_IOP ===
ID: IOP-MES-ERR-005
TÍTULO: Diagnóstico - Lote Programado Desapareceu do Sistema
MÓDULO: Programação e Rastreabilidade (MES VR)

### 📌 Cenário e Sintoma
* **Sistema:** MES VR (Volta Redonda).
* **Sintoma:** Um lote que estava programado no MES desapareceu (sumiu) subitamente do sistema, não estando visível para o usuário no equipamento esperado.

### 🛠️ Procedimento de Análise (Passo a Passo)

**1. Verificação de Programa Ativo (SAP e MES)**
* Valide se o lote possui, de fato, um programa ativo para o equipamento informado pelo usuário.
* **No SAP:** Utilize a transação **SE16N** e consulte as tabelas **ZPTPP_CUSTOM_TAB** ou **ZPPP166**.
* **No MES:** Consulte a **Tela 3.1** (Consulta de Programa dos Lotes).

**2. Verificação nas Telas de Programação (Fila e Rascunho)**
* **Tela 4I11:** Confirme se o lote não se encontra oculto nesta tela, verificando especificamente as abas/campos de **Retrabalho** ou **Não Sequenciados**.
* **Tela 4J11:** Consulte esta tela para confirmar se o lote não se encontra retido no estado de "rascunho da programação".

**3. Verificação de Genealogia e Regra de Qualidade (RDQ)**
* Acesse a **Tela 4.1** (Genealogia) e verifique o histórico do lote no MES.
* **Análise de RDQ:** Observe se o lote passou pela Qualidade.
  * *Condição Afirmativa (Passou em RDQ):* A Ordem de Produção (OP) do programa enviado após a decisão de RDQ tem de ser obrigatoriamente diferente da OP utilizada no programa original.
  * *Condição Negativa (Não passou em RDQ):* Ainda assim, é necessário consultar a sua OP. Selecione a OP na Tela 3.1 e, em seguida, consulte a **Tela 5.4**, informando a OP e o recurso (equipamento).

**4. Escalonamento**
* Após a realização de todas as validações e coleta de evidências (se o lote consta nas tabelas do SAP, se está como não sequenciado ou se há erro na OP após RDQ), encaminhe o chamado devidamente documentado para a **equipe N3** prosseguir com a correção avançada.
=== FIM_IOP ===

=== INÍCIO_IOP ===
ID: IOP-MES-ERR-006
TÍTULO: Diagnóstico - Lote Produzido Reaparecendo na Tela de Abastecimento
MÓDULO: Abastecimento e Programação (MES VR)

### 📌 Cenário e Sintoma
* **Sistema:** MES VR (Volta Redonda).
* **Sintoma:** Um lote específico já foi totalmente produzido no sistema, mas volta a aparecer indevidamente como pendente na tela de Abastecimento.

### 🛠️ Procedimento de Análise (Passo a Passo)

**1. Acompanhamento e Coleta de Evidências**
* Entre em contato direto com o usuário que reportou a falha.
* Acompanhe o procedimento passo a passo e faça capturas de tela (prints) com o erro exato que está sendo apresentado.

**2. Verificação de Status de Produção (Tela 3.1)**
* No sistema MES VR, navegue pelo caminho: **Ferramentas > Monitoração > Consulta de sistemas > Tela 3.1 (Consulta de Programa dos Lotes)**.
* Pesquise o lote afetado para confirmar se o mesmo já foi, de fato, produzido e em qual equipamento isso ocorreu.
* *Validação Prática:* Confirme se as flags de status do equipamento correspondente (Prog, Em Prod, Aprovado, Env. SAP) se encontram preenchidas (ex: todas com "SIM"), comprovando que o ciclo foi concluído e integrado com o SAP.

### 🔄 Matriz de Escalonamento (Direcionamento de Chamado)
Após confirmar que o lote reapareceu indevidamente, o encaminhamento do incidente deve seguir este critério:
* **Fluxo Padrão:** Direcione o chamado para a equipe **MANUTENCAO - MES**, anexando todas as informações verificadas e os registros visuais (capturas de tela) do erro.
* **Cenário Crítico:** Caso o usuário classifique o problema como crítico (ex.: bloqueio na operação), é estritamente necessário acionar de imediato a **Equipe N3** para uma verificação urgente e resolução do bloqueio.
=== FIM_IOP ===


=== INÍCIO_IOP ===
ID: IOP-MES-ERR-007
TÍTULO: Diagnóstico - Falha na Aprovação de Corridas (Integração Gusa)
MÓDULO: Aciaria e Integração (MES VR / MES Redução)

### 📌 Cenário e Sintoma
* **Sistema:** MES VR (Volta Redonda).
* **Sintoma:** O usuário reporta que não está conseguindo realizar a aprovação (finalização) de corridas no sistema MES VR.

### 🛠️ Procedimento de Análise (Passo a Passo)

**1. Validação das Regras de Finalização (Tela 235_2)**
Para que uma corrida possa ser finalizada, o sistema valida três condições obrigatórias. Verifique se todas estão sendo cumpridas:
* **Apontamento de Gusa:** É estritamente necessário existir apontamento de gusa na corrida. As viradas de gusa efetuadas no *MES Redução* já devem constar como exportadas para o MES.
* **Quantidade Mínima Parametrizada:** A quantidade de gusa apontada precisa ser superior ao valor configurado no parâmetro `VALIDACAO_QUANT_GUSA_DIST` (consulta disponível no SIC via caminho: *Cadastro >> Heimdall VR >> Cadastro AAA >> Parâmetro ACI*).
* **Quantidade Mínima Fixa:** A quantidade de gusa apontada tem de ser superior a **100 toneladas** (regra fixa implementada na procedure do Heimdall: `SP_CSN_SEL_CHECK_CORRIDA_GUSA`).

**2. Verificação de Bloqueios e Integração (JOB)**
* **Bloqueio de Tela:** A **Tela 212 – Apontamento de viradas de Gusa** encontra-se desabilitada para inserção manual por parte dos operadores.
* **Integração Obrigatória:** A finalização depende exclusivamente do recebimento automático das viradas. Verifique a execução e integridade do JOB responsável pela captura dos dados: **`CSN_IMPORT_VIRADAS_REDUCAO`**.

**3. Confirmação na Origem (MES Redução Gusa)**
Caso os dados não constem no MES VR, valide a situação da corrida diretamente no sistema de origem:
* Acesse o portal `http://mesreducaogusa/` e efetue o login.
* Navegue até **Menu Aciaria >> Tela 1512 – Alteração de Corrida** para certificar-se do status dos apontamentos antes de escalar o chamado.
=== FIM_IOP ===

=== INÍCIO_IOP ===
ID: IOP-MES-ERR-008
TÍTULO: Diagnóstico - Produção de Placas não Refletida no SAP (Falta de OP/Receita)
MÓDULO: Integração (MES VR / SAP) e Máquinas de Corrida Contínua (MCC)

### 📌 Cenário e Sintoma
* **Sistema:** MES VR (Volta Redonda).
* **Sintoma:** A produção de placas efetuada no sistema MES VR não foi refletida (enviada) para o SAP.
* **Causa Raiz Comum:** Este problema ocorre quando não existe no MES a Ordem de Produção (OP) referente ao grau do material utilizado no período de produção.

### 🛠️ Procedimento de Análise (Passo a Passo)

**1. Verificação da Receita no MES (Tela 5.4)**
* É necessário confirmar se o MES possui a Receita criada para o mês de produção e para o grau específico da placa.
* Acesse **Monitoração - 5.4 Consulta das Tabelas XXX_RECEITA**.
* Preencha os campos de pesquisa de acordo com a seguinte regra de nomenclatura:
  * **Campo Prefixo:** Insira `MCC` (faz com que o sistema procure receitas associadas às Máquinas de Corrida Contínua: MCC2, MCC3 e MCC4).
  * **Campo OP:** Deve ser composto por: `Letra P` + `MÊS e ANO (2 dígitos cada)` + `Letra G` + `Grau do Material` + `ID da Máquina (2, 3 ou 4)`.
  * *Exemplo:* Para o mês 04 de 2016 e grau 2926, a OP pesquisada será `P0416G2926`.
* Se, ao clicar em "BUSCAR", for exibida a mensagem *"Não há dados para esta consulta"*, confirma-se a inexistência da receita no MES.

**2. Identificação do Material da Placa**
* Acesse o procedimento **Geral - Consulta de Placas**.
* Preencha o número da estampa e identifique no resultado da consulta a descrição correta do **Material** (exemplo: `PLC_INT_2926`).

**3. Validação de Ordem em Aberto no SAP (Transação COIO)**
* Uma vez confirmado que a receita não existe no MES, a equipe deve acessar o sistema SAP para verificar a origem do problema.
* Abra a transação **COIO**.
* Utilize o código do **material** identificado no passo anterior e o perfil geral como parâmetros de pesquisa.
* O objetivo desta consulta é validar se existe, de fato, uma Ordem de Produção (OP) em aberto no SAP para aquele grau e período específico (Mês).
=== FIM_IOP ===


=== INÍCIO_IOP ===
ID: IOP-MES-ERR-009
TÍTULO: Diagnóstico - Erro de Chave Duplicada na Inserção de Placas (PK_MCC_POSICAO)
MÓDULO: Gestão de Placas e Máquinas de Corrida Contínua (MES VR)

### 📌 Cenário e Sintoma
* **Sistema:** MES VR (Volta Redonda).
* **Sintoma:** O usuário não consegue inserir uma placa (exemplo: P54235) em determinada corrida (exemplo: corrida 6B1708, Máquina 4).
* **Erro Crítico Apresentado:** O sistema exibe a mensagem: *"Erro ao tentar salvar registro. Violation of PRIMARY KEY constraint 'PK_MCC_POSICAO'. Cannot insert duplicate key in object 'dbo.MCC_POSICAO'"*.

### 🛠️ Procedimento de Análise (Passo a Passo)

Para identificar a existência da placa em conflito no MES (Heimdall), siga as etapas de verificação:

**1. Consulta da Placa no Sistema**
* Acesse o menu principal do MES e siga o caminho: **FERRAMENTAS --> MONITORACAO --> CONSULTA DE SISTEMAS --> CONSULTA DE PLACAS** (Procedimento: *Geral - Consulta de Placas*).
* No campo **ESTAMPA**, informe o código da placa reportada com erro pelo usuário (exemplo: P54235) e execute a pesquisa.

**2. Interpretação do Resultado e Resolução**
* **Se a pesquisa retornar resultados:** Isto indica que a placa ainda consta como existente no estoque. O erro de chave duplicada ocorre porque já existem registros ativos para essa placa nas tabelas `CSN_PLACA` e `CSN_POSICAO` do banco de dados.
* **Ação Corretiva:** É obrigatório acionar o responsável da área de negócio. O responsável deverá acessar o SAP, efetuar a renomeação da placa em causa e enviar a nova identificação para o MES.
* **Validação Final:** Após a renomeação da placa (exemplo prático: a placa P54235 foi renomeada para L04206), o usuário conseguirá incluir a placa na corrida com sucesso, utilizando a nova estampa.
=== FIM_IOP ===


=== INÍCIO_IOP ===
ID: IOP-MES-ERR-010
TÍTULO: Diagnóstico e Correção - Placa com Grau Zero ou Incorreto
MÓDULO: Qualidade e Liberação (MES VR / LIMS VR)

### 📌 Cenário e Sintoma
* **Sistema:** MES VR (Volta Redonda).
* **Sintoma:** Uma placa específica (produzida em determinada corrida e máquina) apresenta grau de aço com valor zero ou com o grau incorreto no sistema.

### 🧠 Contexto e Regras de Negócio (Análise Química)
Para compreender a origem da divergência, deve-se considerar as seguintes regras de negócio do sistema:
* **Integração LIMS:** O grau do aço da placa produzida é determinado pelo apontamento da análise química, cujos dados são transmitidos do sistema LIMS VR para o MES VR (consultáveis na tela **232 - Apontamento de Análises Químicas**). O grau resultante dessa análise pode divergir do que foi inicialmente planejado na programação.
* **Amostragem nas MCCs:** Nas Máquinas de Corrida Contínua (MCCs), são coletadas até 3 análises por corrida em metragens distintas.
* **Grau Padrão (Fallback):** A ausência do apontamento de análise química não bloqueia a produção física da placa na corrida. Entretanto, nessa ocorrência, o MES VR classifica automaticamente a placa com o **grau padrão 2000**.

### 🛠️ Procedimento de Correção (Alteração de Grau Incorreto)
A correção do grau da placa deve ser efetuada durante a rotina de aprovação e finalização da corrida:

1. Acessar a tela **Liberação de Placas para o SAP (235)**.
2. Localizar a corrida afetada e clicar no botão **"Finalizar"** (esta ação desbloqueará os campos da grade para edição).
3. Com os campos liberados, realizar as seguintes alterações:
   * Marcar a flag **"Aprov."**.
   * Alterar manualmente o valor do campo para o grau correto especificado.
4. Clicar no botão **"Salvar"** para registrar a atualização no sistema.
=== FIM_IOP ===


=== INÍCIO_IOP ===
ID: IOP-MES-ERR-011
TÍTULO: Diagnóstico - Falha Silenciosa ao Desabastecer Lote (Bobina Cortada)
MÓDULO: Abastecimento e Movimentação (MES VR)

### 📌 Cenário e Sintoma
* **Sistema:** MES VR (Volta Redonda).
* **Sintoma:** O usuário tenta desabastecer um lote em determinado equipamento, desmarca a flag "Abastecer" e clica em salvar, mas o sistema não grava a alteração. A falha ocorre de forma silenciosa, sem apresentar qualquer aviso ou mensagem de erro na tela.
* **Causa Raiz:** O bloqueio sistêmico ocorre quando a bobina já sofreu evento de corte no equipamento, impedindo o fluxo padrão de desabastecimento direto pela interface.

### 🛠️ Procedimento de Confirmação (Passo a Passo)

Para confirmar tecnicamente se a bobina já foi cortada na linha, utilize uma das abordagens abaixo:

**Método A: Consulta de Movimento**
* Acesse a tela **Consulta de Movimento** (Consultar Log) no MES VR.
* Pesquise pelo número do lote e examine os eventos listados.
* Verifique se consta o registro de corte (exemplo: motivo constando *"CORTADO NO RECURSO..."*).

**Método B: Consulta de Tabelas de Produção (Tela 5.2)**
* Acesse a tela **Monitoração - 5.2 Consulta das Tabelas XXX_LOTE_PRODUZIDO**.
* Preencha os filtros de consulta: Prefixo do equipamento (onde a bobina está alocada), Item e Bobina.
* Execute a busca e valide o conteúdo da coluna **`PASSOU_NO_EQUIPAMENTO`**.
* Se o campo estiver com **valor 1**, confirma-se que a bobina já passou por corte.

### 🔄 Resolução e Orientação Operacional
* Confirmado o corte da bobina por qualquer um dos métodos, o desabastecimento não deve ser forçado.
* Instruir o operador a **finalizar a produção** do material no equipamento ou executar o fluxo específico de **retorno de bobina** previsto no sistema.
=== FIM_IOP ===

=== INÍCIO_IOP ===
ID: IOP-MES-ERR-012
TÍTULO: Diagnóstico Completo - Programa de Lote Não Exibido na Tela do  Equipamento
MÓDULO: Programação e Integração (MES / SAP)

### 📌 Cenário e Sintoma
* O usuário reporta que um lote deveria estar disponível para produção em determinado equipamento, mas o seu programa não é exibido nas telas operacionais do MES.
* Este procedimento detalha o roteiro de validações obrigatórias para identificar em qual ponto o fluxo de dados foi interrompido.

### 🛠️ Procedimento de Análise (Checklist de Validação)

**1. Validação de Existência do Programa (Tela 3.1)**
* Acesse **Monitoração - 3.1 Consulta de Programa dos Lotes**.
* Pesquise pelo lote e confirme se o recurso (equipamento) reclamado está correto.
* Verifique as *flags* obrigatórias: **R/V** deve ser igual a "R" (Real), **Prog** deve ser "SIM" e **Em Prod** deve ser "SIM".
* ⚠️ *Escalonamento:* Se a consulta não retornar linhas (ausência de programa) ou se foi enviado como virtual ("V"), encaminhe para a **Equipe SAP** para reenvio ou análise.

**2. Validação da Receita (Tela 5.4)**
* Acesse **Monitoração - 5.4 Consulta das Tabelas XXX_RECEITA** e pesquise pela OP identificada no passo anterior.
* ⚠️ *Escalonamento:* Se não existir receita no MES para o programa do lote, encaminhe para a **Equipe SAP** (anote a OV/Item para a etapa seguinte).

**3. Validação da Ordem de Venda / Item (Tela 5.5)**
* Acesse **Monitoração - 5.5 Consulta de CSN_ORDEM_VENDA**.
* ⚠️ *Escalonamento:* Caso não sejam retornadas linhas para o item da OV (ou a versão não seja válida), o incidente deve ser encaminhado para a **Equipe SAP**.

**4. Validação de Lote Consumido e Divergências (Tela 5.1)**
* Acesse **Monitoração - 5.1 Consulta das Tabelas XXX_LOTE_CONSUMIDO**.
* Confirme se o número da receita visualizado é idêntico ao do passo 2.
* A *flag* de RDQ precisa estar em zero (indicando que não está retido pela Qualidade).
* ⚠️ *Escalonamento:* Caso a versão da OV ou o ID da receita estejam divergentes, encaminhe para a equipe de **TI INDUSTRIAL – MANUTENÇÃO MES**.

**5. Validação de Status de Qualidade (RDQ)**
* O lote NÃO pode estar em RDQ no MES. Verifique na tela **Acabamento - Consulta Geral de Lotes em RDQ**.
* Se constar em RDQ, verifique no log de movimentos se a movimentação mais recente foi de "retirada de RDQ".
* ⚠️ *Escalonamento:*
  * Se existir movimento de retirada no log, encaminhe para **TI INDUSTRIAL – MANUTENÇÃO MES**.
  * Se não existir, verifique a Tela 2.1.1 à procura de uma mensagem `QMRDQ` pendente com erro. Se não houver mensagem pendente nem movimentação registrada, encaminhe para a **Equipe SAP**.

**6. Validação de Genealogia (Tela 4.1)**
* Acesse **Monitoração - 4.1 Consulta de Genealogia dos Lotes**.
* Deve ser exibida ao menos uma linha regist



=== INÍCIO_IOP ===
ID: IOP-MES-ERR-013
TÍTULO: Diagnóstico - Lotes Produzidos no LDC Não Aparecem no LTF
MÓDULO: Integração de Fluxo Produtivo (MES VR / SAP)

### 📌 Cenário e Sintoma
* **Sistema:** MES VR (Volta Redonda).
* **Sintoma:** O usuário relata que os lotes já foram totalmente produzidos no equipamento LDC, contudo não aparecem disponíveis para produção no equipamento subsequente (LTF).

### 🛠️ Procedimento de Análise (Passo a Passo)

**1. Confirmação de Produção e Depósito no SAP (MB51)**
* Acessar a transação **MB51** no SAP.
* Confirmar se a produção da decapagem foi devidamente lançada (movimento 101).
* Validar se o material está alocado em um dos depósitos válidos: **E02, LTF1, LTF2 ou LTF3**.
* *Nota de Validação:* Para que o lote conste na transação ZPOT11, é mandatório que o material **não** esteja retido pelo controle de qualidade (RDQ).

**2. Validação da Programação (Transações ZPPP166 e ZPPP109)**
* Acessar a transação **ZPPP166** (*custom table*) no SAP para checar a existência de programa para o equipamento.
* **Se não houver programa criado:** Acessar a transação **ZPPP109** para verificar a situação do lote. Constatado o status *"Aguardando programação"*, orientar o usuário a acionar diretamente a equipe de Programação.

**3. Análise do Status de Envio e Mensageria**
Existindo o programa na transação ZPPP166, validar a coluna **"Status de Envio"**:

* **Status "N":** Procedimento de programação do lote pendente no SAP. *Ação:* Acionar a equipe de Programação.
* **Status "L":** Pendente de execução do JOB `PP_CRIAR_AMATSP1` (rotina encarregada de gerar a mensagem AMATSP1 para envio ao MES).
* **Status "S":** Programa transmitido pelo SAP.
  * *Ação subsequente:* Acessar a transação **CO54** e verificar se a mensagem `AMATSP1` foi processada com sucesso.
  * *Escalonamento:* Se a mensagem constar cancelada ou mantiver o status *"a enviar"* por mais de 20 minutos na CO54, acionar a equipe **SAP-PP-QM-VR** para averiguação de fila de comunicação ou falhas de JOB.
=== FIM_IOP ===



=== INÍCIO_IOP ===
ID: IOP-MES-ERR-014
TÍTULO: Diagnóstico - Erro de Locação de Placas (Mensagem em Processamento)
MÓDULO: Locação de Placas e Integração (MES VR)

### 📌 Cenário e Sintoma
* **Sistema:** MES VR (Volta Redonda).
* **Sintoma:** Ao tentar realizar a locação de placas no sistema, o usuário se depara com a seguinte mensagem de bloqueio: *"SM Placa X está sendo processada. Não é possível processar dados agora"*.
* **Causa Raiz:** O erro ocorre porque existe uma mensagem de integração pendente de finalização em segundo plano (background) no monitor do sistema.

### 🛠️ Procedimento de Verificação (Passo a Passo)

Para confirmar se a mensagem está de fato em trânsito operacional regular:

**1. Acesso ao Monitor de Integração**
* Abra o sistema MES VR.
* Na barra de menus superior, acesse **Monitoração >> Consulta de Sistemas**.

**2. Consulta da Mensagem (Tela 1.1.1)**
* Na tela de consultas, utilize o seletor "Seleção do procedimento" e selecione a opção **Monitoração - 1.1.1 Instruções SAP -> MES**.
* Na listagem exibida, localize na coluna "Mensagem" os registros com a nomenclatura **SMPLACAX** e valide se constam em processamento ativo.

### 🔄 Ação e Resolução
* Se a mensagem for localizada na consulta em fila de processamento, não há necessidade de abertura imediata de incidente nem de acionamento de outras equipes.
* **Ação Corretiva:** Instruir o operador a aguardar a conclusão do processamento da fila. Por padrão, a rotina automática é rápida e libera a placa para locação logo em seguida.
=== FIM_IOP ===


=== INÍCIO_IOP ===
ID: IOP-MES-ERR-015
TÍTULO: Diagnóstico - Bloqueio na Aferição de Instrumentos (Status NOK)
MÓDULO: Inspeção e Qualidade (MES VR)

### 📌 Cenário e Sintoma
* **Sistema:** MES VR (Volta Redonda).
* **Sintoma:** O usuário não consegue efetuar a aferição de instrumentos no sistema. Ao tentar realizá-la, o sistema exibe uma janela de alerta com a mensagem: *"Os instrumentos: [Nome do Instrumento] serão bloqueados Favor contactar a equipe de qualidade"* (ex: Tela 4NU1).

### 🧠 Causa Raiz (Regra de Negócio)
Este bloqueio não representa uma falha técnica do sistema, tratando-se de uma trava de processo vinculada a controle de qualidade por tempo limite:
* Ocorre quando um instrumento (ex: Ondulômetro) foi aferido como **NOK** em determinado turno e a área operacional não corrigiu o apontamento para **OK** dentro do intervalo máximo parametrizado.
* Uma vez acionado o bloqueio, a aferição do equipamento permanece inativa durante todo o turno corrente, liberando nova verificação apenas no **turno seguinte**.

### 🛠️ Procedimento de Diagnóstico (Passo a Passo)

Para identificar com precisão qual instrumento disparou a trava, realize a seguinte verificação:

**1. Acesso à Tela de Consulta**
* Na árvore de navegação do MES VR, expanda o módulo **Inspeção**.
* Localize e expanda a pasta do equipamento reportado (exemplo: pasta **LCR**).
* Clique na opção **Consulta de Verificação de Instrumentos**.

**2. Filtragem e Análise**
* Defina os parâmetros de pesquisa informando a **Data** e o **Turno** da ocorrência.
* Clique no ícone de pesquisa (Lupa / Buscar).
* Localize o item divergente na tabela, confirmando qual instrumento permanece com status "NOK" ou pendente de regularização.

### ℹ️ Informações de Configuração (SIC VR)
Para equipes de sustentação e suporte N3:
* **Parâmetro Controlado:** `TEMPO_BLOQUEIO_MIN`.
* **Caminho no SIC VR:** Acessar *Cadastro >> Cadastro de Parâmetros no Sistema* (o acesso depende do nível de privilégio cadastrado no perfil).
* **Regra Sistêmica:** Determina a tolerância (ex: 60 minutos) para alteração de status de NOK para OK antes da ativação do bloqueio compulsório até a virada de turno.
=== FIM_IOP ===


=== INÍCIO_IOP ===
ID: IOP-MES-ERR-016
TÍTULO: Diagnóstico - Lote Produzido no MES Não Foi Enviado ao SAP
MÓDULO: Integração e Mensageria (MES / SAP)

### 📌 Cenário e Sintoma
* **Sistema:** MES (Aplicável a Volta Redonda, Porto Real, Prada e Paraná).
* **Sintoma:** O usuário reporta que um lote já foi produzido no MES, mas o mesmo não foi enviado para o SAP (o lote criado não existe na transação MB51).

### 🛠️ Procedimento de Análise (Passo a Passo)

**1. Coleta Inicial de Dados**
* Solicitar ao usuário o número da **Tela** em que o lote foi produzido e a **identificação exata do lote**.
* Confirmar os dados informados na tela de produção do MES, anotando o horário exato em que o lote foi produzido.

**2. Verificação de Saída no MES (Tela 2.3.1)**
* Acessar a tela monitor de mensagens MES x SAP (**Tela 2.3.1**).
* Verificar se as mensagens do tipo `XX_PROD` e `XX_LOTE` (exemplo no MES VR: `PI_PROD` e `CSN_LOTE`) foram enviadas com sucesso (Status `S`).
* ⚠️ *Escalonamento (Falha no MES):* Caso as mensagens não tenham sido enviadas, verificar se houve problema na execução do JOB (IMC Windows). Em caso de erro neste ponto, o chamado (crítico) deve ser acionado para a **equipe MES**.

**3. Validação de Entrada no SAP (Transação CO54)**
Se não houver erro no JOB e as mensagens estiverem com status de envio correto (`S`) no MES, a falha está na camada SAP:
* Acessar a transação **CO54** no SAP.
* Informar o Centro correspondente à unidade fabril:
  * Volta Redonda: `01`
  * Porto Real: `53`
  * Prada: `1801`
  * Paraná: `22`
* Informar um intervalo de horário aproximado ao da produção do lote e clicar no botão **Renovar** (Atualizar).

**4. Análise de Mensagens Canceladas**
* Após a atualização dos dados na CO54, clicar no campo/botão correspondente às mensagens **Canceladas** (ícone com círculo vermelho) para filtrar apenas as ocorrências com erro.
* Abrir as mensagens listadas e checar as características (ex: `CSN_LOTE`) até localizar o lote reclamado.

**5. Extração do Log de Erro e Escalonamento**
* Ao localizar a mensagem do lote, retornar à tela anterior, selecionar a linha correspondente e clicar no botão **Log** (Informações Técnicas).
* O SAP exibirá o protocolo de erro detalhado (ex: "Erro no lançamento de saída de mercadoria").
* ⚠️ *Escalonamento (Falha no SAP):* Comprovada a falha no processamento de entrada, o chamado deve ser encaminhado para a **equipe SAP**.
* *Plantão:* Se o incidente ocorrer fora do horário comercial/administrativo, validar a criticidade com a operação para acionamento de plantão.
=== FIM_IOP ===




=== INÍCIO_IOP ===
ID: IOP-MES-ERR-017
TÍTULO: Diagnóstico - Falha Silenciosa ao Salvar Apontamento de Produção
MÓDULO: Apontamento de Produção (MES)

### 📌 Cenário e Sintoma
* **Sintoma:** O usuário acessa a tela de "Apontar Produção", preenche os dados e clica no botão "Salvar", contudo o sistema não grava as informações e não exibe qualquer mensagem de erro na interface.
* **Causa Raiz:** Este bloqueio silencioso ocorre devido à ausência de registro adequado de DLL no sistema operacional da estação do usuário.

### 🛠️ Procedimento de Correção (Passo a Passo)
Sendo um problema na máquina local do usuário, a correção é realizada via reinstalação e registro da dependência:

1. **Acessar o Catálogo de Aplicativos:** Orientar o usuário a acessar a área de trabalho do Windows e abrir o **Software Center Web** (Catálogo de Aplicativos), disponível corporativamente.
2. **Localizar o Pacote de Correção:** Na listagem de softwares disponíveis no catálogo, localizar e selecionar o item denominado **"CORREÇÃO MES CSN"**.
3. **Executar Instalação:** Clicar no botão **"INSTALAR"** e aguardar a finalização completa da rotina de registro.
4. **Validação Operacional:** Após a conclusão, solicitar ao usuário que reabra a tela de apontamento de produção no MES, insira os dados do lote/ordem e valide se a operação de salvar conclui com sucesso.

### 🔄 Escalonamento
* Se após a execução do procedimento de correção a tela continuar sem salvar ou passar a exibir uma mensagem de erro explícita no sistema, encaminhar o incidente imediatamente para a equipe de **N3**.
=== FIM_IOP ===






=== INÍCIO_IOP ===
ID: IOP-MES-ERR-018
TÍTULO: Diagnóstico - Erro de Chave Duplicada ao Inserir Placa em Veio (PK_MCC_POSICAO)
MÓDULO: Apontamento e Gestão de Placas (MES VR)

### 📌 Cenário e Sintoma
* **Sistema:** MES VR (Volta Redonda).
* **Sintoma:** Ao tentar inserir uma placa em um veio específico de uma corrida (exemplo: corrida 5A4333), o sistema bloqueia a ação e apresenta erro de banco de dados.
* **Erro Apresentado (Heimdall):** *"Erro ao tentar salvar registros. Violation of PRIMARY KEY constraint 'PK_MCC_POSICAO'. Cannot insert duplicate key in object 'dbo.MCC_POSICAO'"*.

### 🛠️ Procedimento de Coleta de Dados
O operador de suporte deve coletar imediatamente as evidências antes do escalonamento:
1. **Contato Direto:** Entrar em contato com o usuário que reportou a falha.
2. **Identificação do Registro:** Solicitar ao usuário a informação exata de qual é o **veio** e qual é a **placa** que ele está tentando inserir no momento da falha.
3. **Evidência Visual:** Obter print screen da tela onde ocorre o erro para anexar ao chamado.

### 🚨 Escalonamento e Nível de Urgência
* **Urgência Crítica:** Os problemas referentes a esta tela de apontamento são classificados, de forma geral, como sendo de **grande urgência**.
* **Análise de Impacto:** Verificar o tempo de frente para resolução do problema (tempo disponível antes de impactar a produção ou parar a linha).
* **Ação Obrigatória:** Acionar imediatamente o **plantão N3** para proceder à verificação e correção do conflito de chave duplicada no banco de dados.
=== FIM_IOP ===




=== INÍCIO_IOP ===
ID: IOP-MES-ERR-019
TÍTULO: Diagnóstico - Flag de Finalização Não Marcada Após Aprovação
MÓDULO: Liberação de Placas (MES VR)

### 📌 Cenário e Sintoma
* **Sistema:** MES VR (Volta Redonda).
* **Sintoma:** Uma corrida de uma determinada máquina teve as suas placas aprovadas e foi dada como finalizada pelo usuário, contudo, a *flag* "Finalizado" não ficou visivelmente marcada no sistema.
* **Causa Raiz:** Este erro na interface ocorre quando o processo de finalização da corrida é executado, mas o campo `FINALIZADO_MCC` permanece indevidamente com o valor `0` na tabela `ACI_CORRIDA` do banco de dados.

### 🛠️ Procedimento de Validação (Passo a Passo)

Para confirmar se o bloqueio se trata efetivamente desta anomalia de banco de dados, execute os seguintes passos de diagnóstico:

**1. Consulta de Status na Tela Principal**
* Acesse a tela **Liberação de Placas para o SAP (235)**.
* Localize a corrida reportada pelo usuário (exemplo demonstrativo: corrida `6A1802`).
* Verifique a divergência nas *flags*: confirme se a linha apresenta a *flag* desmarcada na coluna **"Finalizado"**, mas possui a *flag* marcada na coluna **"Placas Aprovadas"**.

**2. Verificação de Botões de Ação**
* Na linha correspondente a essa corrida, clique no botão **"Visualizar"**.
* Na tela de detalhes da corrida, observe o estado dos controles superiores: confirme se os botões **"Reverter Finalização"** e **"Finalizar"** se encontram totalmente desativados (inativos/cinzas).

### 🔄 Escalonamento
* **Ação Corretiva:** Caso a verificação seja positiva (divergência nas *flags* associada à inativação dos botões de ação), o operador de suporte não conseguirá resolver o problema via interface.
* É estritamente necessário documentar o chamado com estas evidências e acionar de imediato a equipe de **TI INDUSTRIAL - MANUTENCAO MES** para que seja efetuada a correção no banco de dados.
=== FIM_IOP ===



=== INÍCIO_IOP ===
ID: IOP-MES-ERR-020
TÍTULO: Diagnóstico - Erro ao Realizar Desabastecimento de Item (Bloqueio de Frente)
MÓDULO: Abastecimento de Itens e Pátio de Placas (MES VR)

### 📌 Cenário e Sintoma
* **Sistema:** MES VR (Volta Redonda).
* **Sintoma:** Ao tentar efetuar o desabastecimento de um item (por exemplo, na tela **271 – Abastecer Itens – Pátio de Placas**), o sistema não permite concluir a ação.
* **Erro Apresentado (Heimdall):** É exibida uma janela de alerta com a crítica: *"Não é permitido desabastecimento, esta retirando frente!"*.

### 🧠 Causa Raiz e Regra de Negócio
* Após a análise, verifica-se que este comportamento não constitui uma falha técnica, mas sim uma **regra de segurança do sistema**.
* **Objetivo da Regra:** O bloqueio foi implementado para impedir a retirada do material que está na "frente" da fila do equipamento. Esta medida visa garantir a alimentação contínua e evitar uma possível parada (falta de material) na máquina.
* **Origem da Parametrização:** Esta regra é alimentada e controlada de forma dinâmica por um parâmetro existente no sistema **SIC (Cadastro - Heimdall-VR)**.

### 🛠️ Procedimento e Orientação
1. **Esclarecimento Operacional:** O operador de suporte deve informar ao usuário que o bloqueio é intencional e serve para proteger a máquina contra paradas indevidas. O sistema não permitirá o desabastecimento direto daquele item específico enquanto ele for a "frente" de abastecimento.
2. **Gestão de Parâmetros (Escalonamento):** Caso exista uma diretriz de produção que exija contornar esta regra, a alteração do comportamento sistêmico dependerá de um ajuste de parâmetros efetuado no portal do SIC (através do endereço `http://mescadastro`). Esta ação requer validação e aprovação das equipes responsáveis pela configuração de processos.
=== FIM_IOP ===










