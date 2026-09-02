# Requisitos da aplicação

> **Artefato central da Sprint 2.** Não repita apenas o problema; descreva o comportamento e as condições que o sistema deverá atender.

## 1. Método de levantamento

Os requisitos foram identificados a partir da análise do problema proposto para o projeto Conexão Solidária, das discussões realizadas pela equipe e da definição das necessidades dos principais usuários da aplicação.

Foram consideradas as atividades de doação e recebimento de alimentos, o cadastro dos estabelecimentos doadores, o cadastro das instituições beneficiárias, a disponibilização e solicitação de alimentos, o gerenciamento das demandas das instituições e as condições necessárias para aumentar a segurança e a transparência das doações.

Também foram analisadas situações de uso da aplicação, buscando transformar as necessidades identificadas em comportamentos verificáveis do sistema.

## 2. Atores e perfis

| Ator/perfil | Objetivo no sistema | Permissões ou limitações principais |
|---|---|---|
| Estabelecimento doador | Cadastrar e disponibilizar alimentos excedentes para doação | Pode cadastrar ofertas, informar quantidade e período de retirada, consultar solicitações e acompanhar o histórico de doações |
| Instituição beneficiária | Encontrar alimentos disponíveis e registrar necessidades | Pode consultar ofertas, solicitar alimentos, cadastrar demandas e informar seu perfil de atendimento |
| Nutricionista responsável | Comprovar a responsabilidade técnica vinculada à instituição | Deve possuir registro profissional informado no cadastro e estar vinculado à instituição para habilitação do recebimento |
| Administrador | Gerenciar e acompanhar o funcionamento da plataforma | Pode consultar cadastros, acompanhar situações que necessitem de validação e auxiliar na manutenção da integridade dos registros |

## 3. Requisitos funcionais

| ID | Nome | Descrição verificável | Prioridade | História/Issue | Situação final |
|---|---|---|---|---|---|
| `RF-01` | Cadastro de estabelecimento | O sistema deve permitir que o estabelecimento doador realize seu cadastro informando os dados necessários para sua identificação. | Alta | `#12` | Planejado |
| `RF-02` | Cadastro de instituição | O sistema deve permitir que a instituição beneficiária realize seu cadastro informando seus dados de identificação e atendimento. | Alta | `#15` | Planejado |
| `RF-03` | Cadastro do nutricionista responsável | O sistema deve permitir o cadastro dos dados do nutricionista responsável vinculado à instituição. | Alta | `#15` | Planejado |
| `RF-04` | Habilitação da instituição | O sistema deve permitir que uma instituição seja habilitada para receber alimentos somente após o cadastro e a vinculação de um nutricionista responsável válido. | Alta | `#15` | Planejado |
| `RF-05` | Autenticação de usuários | O sistema deve permitir que usuários cadastrados realizem autenticação para acessar as funcionalidades correspondentes ao seu perfil. | Alta | `#18` | Planejado |
| `RF-06` | Controle de acesso por perfil | O sistema deve disponibilizar somente as funcionalidades compatíveis com o perfil do usuário autenticado. | Alta | `#18` | Planejado |
| `RF-07` | Cadastro de oferta de alimentos | O sistema deve permitir que um estabelecimento cadastre uma oferta informando alimento, quantidade disponível, unidade de medida e período para retirada. | Alta | `#12` | Planejado |
| `RF-08` | Consulta de ofertas | O sistema deve permitir que instituições habilitadas consultem os alimentos disponíveis para doação. | Alta | `#12` | Planejado |
| `RF-09` | Filtro de ofertas | O sistema deve permitir que a instituição filtre as ofertas disponíveis por critérios como tipo de alimento, quantidade e período de retirada. | Média | `#12` | Planejado |
| `RF-10` | Solicitação de alimento | O sistema deve permitir que uma instituição habilitada solicite uma quantidade de alimento disponível em uma oferta. | Alta | `#12` | Planejado |
| `RF-11` | Controle da quantidade disponível | O sistema deve atualizar a quantidade restante de uma oferta após a confirmação de uma solicitação. | Alta | `#12` | Planejado |
| `RF-12` | Gerenciamento da solicitação | O sistema deve permitir o acompanhamento do estado da solicitação desde seu registro até a confirmação ou cancelamento da retirada. | Alta | `#12` | Planejado |
| `RF-13` | Confirmação da retirada | O sistema deve permitir o registro da confirmação da retirada dos alimentos pela instituição. | Alta | `#12` | Planejado |
| `RF-14` | Histórico de doações | O sistema deve permitir que os usuários consultem o histórico de ofertas, solicitações e retiradas relacionadas ao seu perfil. | Média | `#12` | Planejado |
| `RF-15` | Cadastro do perfil de atendimento | O sistema deve permitir que a instituição informe sua capacidade de atendimento, número atual de pessoas atendidas, público atendido e frequência de atendimento. | Alta | `#15` | Planejado |
| `RF-16` | Cadastro de demanda | O sistema deve permitir que a instituição registre uma necessidade de alimento informando alimento, quantidade, unidade de medida, prazo, finalidade e quantidade estimada de beneficiários. | Alta | `#16` | Planejado |
| `RF-17` | Definição de prioridade da demanda | O sistema deve permitir que a instituição classifique sua demanda como alta, média ou baixa prioridade. | Média | `#16` | Planejado |
| `RF-18` | Verificação de coerência da demanda | O sistema deve comparar a quantidade solicitada com as informações de atendimento e beneficiários da demanda e apresentar um alerta quando identificar uma possível incompatibilidade. | Alta | `#17` | Planejado |
| `RF-19` | Justificativa de demanda excepcional | O sistema deve permitir que a instituição informe uma justificativa quando a quantidade solicitada estiver significativamente acima da quantidade de referência calculada. | Alta | `#17` | Planejado |
| `RF-20` | Consulta de demandas | O sistema deve permitir que estabelecimentos consultem as demandas cadastradas pelas instituições. | Média | `#16` | Planejado |
| `RF-21` | Compatibilidade entre oferta e demanda | O sistema deve permitir a identificação de ofertas compatíveis com demandas cadastradas, considerando as informações disponíveis sobre alimento, quantidade e prazo. | Média | `#12` | Planejado |
| `RF-22` | Cadastro de alimento | O sistema deve permitir que o estabelecimento cadastre os alimentos que poderão ser utilizados em ofertas de doação. | Média | `#12` | Planejado |
| `RF-23` | Notificações | O sistema deve informar aos usuários eventos relevantes relacionados a ofertas, solicitações, demandas e alterações de status. | Média | `#12` | Planejado |

## 4. Requisitos não funcionais

Evite termos vagos. Sempre que possível, inclua condição ou métrica.

| ID | Categoria | Descrição verificável | Como será avaliado | Issue |
|---|---|---|---|---|
| `RNF-01` | Segurança | O sistema não deve armazenar senhas em texto puro. | Inspeção da implementação e do banco de dados. | `#6` |
| `RNF-02` | Segurança | O sistema deve restringir o acesso às funcionalidades conforme o perfil do usuário autenticado. | Testes de acesso utilizando diferentes perfis. | `#6` |
| `RNF-03` | Privacidade | O sistema deve disponibilizar somente os dados pessoais necessários para o funcionamento das funcionalidades. | Inspeção das telas e dos dados exibidos. | `#6` |
| `RNF-04` | Integridade | O sistema não deve permitir que uma solicitação ultrapasse a quantidade disponível de uma oferta. | Teste de solicitação com quantidade superior ao estoque disponível. | `#6` |
| `RNF-05` | Desempenho | As principais operações da aplicação devem apresentar resposta em até 3 segundos no ambiente definido para os testes. | Testes de desempenho das operações principais. | `#6` |
| `RNF-06` | Usabilidade | As funcionalidades principais devem apresentar informações e mensagens de validação compreensíveis para os usuários. | Teste funcional e inspeção das interfaces. | `#6` |
| `RNF-07` | Acessibilidade | Os elementos principais da interface devem seguir os critérios de acessibilidade definidos pela equipe, incluindo navegação por teclado e identificação adequada dos campos. | Inspeção da interface e testes de navegação. | `#8` |
| `RNF-08` | Compatibilidade | A aplicação deve funcionar nos principais navegadores definidos pela equipe para a versão web. | Testes nos navegadores selecionados. | `#6` |
| `RNF-09` | Responsividade | As principais telas devem permanecer utilizáveis em diferentes tamanhos de tela. | Testes em resoluções de computador e dispositivos móveis. | `#6` |
| `RNF-10` | Manutenibilidade | O código deve ser organizado em módulos e componentes que permitam alterações sem afetar funcionalidades não relacionadas. | Inspeção do código-fonte. | `#6` |
| `RNF-11` | Rastreabilidade | Os requisitos devem possuir identificação e relação com histórias, Issues e artefatos correspondentes. | Inspeção da documentação e do GitHub Project. | `#13` |

## 5. Regras de negócio

| ID | Regra | Origem/justificativa | Requisitos afetados |
|---|---|---|---|
| `RN-01` | A instituição deverá possuir um nutricionista responsável devidamente vinculado ao seu cadastro para ser habilitada a receber alimentos. | Regra definida pelo projeto para aumentar a segurança e a responsabilidade no processo de recebimento das doações. | `RF-03`, `RF-04`, `RF-10` |
| `RN-02` | Uma instituição somente poderá solicitar alimentos de ofertas enquanto estiver habilitada para recebimento. | Necessidade de controlar quais instituições podem participar do processo de recebimento. | `RF-04`, `RF-10` |
| `RN-03` | A quantidade solicitada não poderá ser superior à quantidade disponível na oferta. | Evitar solicitações superiores ao alimento efetivamente disponibilizado pelo estabelecimento. | `RF-10`, `RF-11` |
| `RN-04` | Uma oferta não poderá receber novas solicitações após o encerramento do período definido para retirada. | Garantir que as solicitações ocorram dentro do período informado pelo estabelecimento. | `RF-07`, `RF-10` |
| `RN-05` | A instituição deverá informar seu número atual de pessoas atendidas e sua capacidade de atendimento. | Permitir que as demandas sejam analisadas considerando o perfil real de atendimento da instituição. | `RF-02`, `RF-15`, `RF-18` |
| `RN-06` | Toda demanda deverá informar a quantidade de alimento desejada, a unidade de medida, a finalidade, o prazo e a quantidade estimada de beneficiários. | Permitir a avaliação do contexto da necessidade informada pela instituição. | `RF-16` |
| `RN-07` | A quantidade de referência de uma demanda será calculada considerando o número estimado de beneficiários e o consumo per capita definido para o alimento ou contexto da solicitação. | Permitir uma análise de coerência entre a quantidade solicitada e o número de pessoas beneficiadas. | `RF-16`, `RF-18` |
| `RN-08` | Quando a quantidade solicitada apresentar diferença significativa em relação à quantidade de referência, o sistema deverá apresentar um alerta à instituição. | Aumentar a transparência e reduzir solicitações incompatíveis com o contexto informado. | `RF-18` |
| `RN-09` | A instituição poderá prosseguir com uma demanda que apresente alerta de incompatibilidade desde que informe uma justificativa para a quantidade excepcional. | Permitir situações legítimas, como eventos, campanhas ou atendimento excepcional de um número maior de pessoas. | `RF-18`, `RF-19` |
| `RN-10` | A quantidade de referência não deverá ser tratada como uma cota fixa de alimentos para a instituição. | O número de beneficiários, o tipo de alimento, a unidade, a finalidade e o contexto podem variar entre diferentes demandas. | `RF-18`, `RF-19` |
| `RN-11` | O sistema deverá manter o registro das solicitações, alterações e confirmações de retirada realizadas pelos usuários. | Garantir histórico e rastreabilidade das operações realizadas na plataforma. | `RF-12`, `RF-13`, `RF-14` |
| `RN-12` | Uma demanda deverá possuir uma prioridade entre alta, média e baixa. | Permitir que os estabelecimentos identifiquem necessidades de maior urgência. | `RF-17`, `RF-20` |
| `RN-13` | A quantidade de referência deverá ser utilizada como parâmetro de coerência, e não como uma cota fixa de recebimento para a instituição. | Permitir situações excepcionais sem impedir demandas legítimas. | `RF-18`, `RF-19` |

## 6. Histórias de usuário e critérios de aceitação

### US-01 — Cadastrar instituição

Como **instituição beneficiária**, quero **cadastrar minha instituição**, para **poder participar do processo de recebimento de alimentos**.

**Requisitos relacionados:** `RF-02`, `RF-03`, `RF-04`, `RN-01`

**Critérios de aceitação:**

1. **Dado que** uma instituição ainda não possui cadastro, **quando** preencher os dados obrigatórios e enviar o formulário, **então** o sistema deverá registrar a instituição.
2. **Dado que** a instituição não possua nutricionista responsável vinculado, **quando** tentar ser habilitada para receber alimentos, **então** o sistema deverá impedir a habilitação.

**Issue:** `#15`

### US-02 — Disponibilizar alimento para doação

Como **estabelecimento doador**, quero **cadastrar uma oferta de alimento**, para **disponibilizar meu excedente para uma instituição**.

**Requisitos relacionados:** `RF-07`, `RF-11`, `RN-03`, `RN-04`

**Critérios de aceitação:**

1. **Dado que** o estabelecimento esteja autenticado, **quando** informar alimento, quantidade, unidade e período de retirada, **então** o sistema deverá registrar a oferta.
2. **Dado que** a quantidade disponível seja 20 unidades, **quando** uma instituição solicitar 25 unidades, **então** o sistema deverá impedir a solicitação.

**Issue:** `#12`

### US-03 — Solicitar alimento

Como **instituição beneficiária habilitada**, quero **solicitar uma oferta disponível**, para **receber alimentos para atender meu público**.

**Requisitos relacionados:** `RF-08`, `RF-10`, `RF-12`, `RN-02`, `RN-03`

**Critérios de aceitação:**

1. **Dado que** a instituição esteja habilitada e exista uma oferta disponível, **quando** informar uma quantidade igual ou inferior à disponível, **então** o sistema deverá registrar a solicitação.
2. **Dado que** a instituição não esteja habilitada, **quando** tentar solicitar uma oferta, **então** o sistema deverá impedir a operação.

**Issue:** `#12`

### US-04 — Registrar necessidade de alimentos

Como **instituição beneficiária**, quero **registrar uma demanda de alimento**, para **informar aos estabelecimentos quais são minhas necessidades**.

**Requisitos relacionados:** `RF-15`, `RF-16`, `RF-17`, `RN-05`, `RN-06`

**Critérios de aceitação:**

1. **Dado que** a instituição esteja cadastrada, **quando** informar alimento, quantidade, unidade, finalidade, prazo e beneficiários estimados, **então** o sistema deverá registrar a demanda.
2. **Dado que** um dos campos obrigatórios não tenha sido informado, **quando** a instituição tentar salvar a demanda, **então** o sistema deverá solicitar o preenchimento do campo.

**Issue:** `#16`

### US-05 — Verificar coerência da quantidade solicitada

Como **instituição beneficiária**, quero **verificar se a quantidade solicitada é coerente com o número de beneficiários**, para **evitar solicitações inadequadas ou explicar necessidades excepcionais**.

**Requisitos relacionados:** `RF-15`, `RF-16`, `RF-18`, `RF-19`, `RN-07`, `RN-08`, `RN-09`

**Critérios de aceitação:**

1. **Dado que** a instituição informe o número de beneficiários e a quantidade solicitada, **quando** existir um consumo per capita definido para o contexto da demanda, **então** o sistema deverá calcular a quantidade de referência.
2. **Dado que** a quantidade solicitada esteja significativamente acima da quantidade de referência, **quando** a instituição tentar concluir a demanda, **então** o sistema deverá apresentar um alerta.
3. **Dado que** seja apresentado um alerta, **quando** a instituição informar uma justificativa para a quantidade excepcional, **então** o sistema deverá permitir o registro da demanda.
4. **Dado que** a quantidade solicitada esteja dentro da referência calculada, **quando** a instituição concluir a demanda, **então** o sistema deverá permitir seu registro sem exigir justificativa.

**Exemplo de cálculo:**

`Quantidade de referência = Número de beneficiários × Consumo per capita`

Exemplo:

`30 beneficiários × 2 unidades por pessoa = 60 unidades`

**Issue:** `#16`, `#17`

### US-06 — Consultar demandas

Como **estabelecimento doador**, quero **consultar as necessidades das instituições**, para **identificar oportunidades de doação compatíveis com os alimentos que possuo**.

**Requisitos relacionados:** `RF-20`, `RF-21`, `RN-12`

**Critérios de aceitação:**

1. **Dado que** existam demandas cadastradas, **quando** o estabelecimento acessar a área de demandas, **então** o sistema deverá apresentar as demandas disponíveis.
2. **Dado que** uma demanda possua prioridade alta, **quando** for apresentada na consulta, **então** sua prioridade deverá estar identificada.

**Issue:** `#16`

## 7. Fora do escopo

| Item | Motivo | Possível trabalho futuro |
|---|---|---|
| Pagamento ou comercialização de alimentos | O objetivo da aplicação é intermediar doações, não vendas. | Inclusão de funcionalidades comerciais em uma versão futura, caso necessário. |
| Transporte próprio dos alimentos | A aplicação não será responsável pela logística física das doações. | Integração com serviços de transporte ou logística. |
| Inspeção física dos alimentos | A verificação física das condições dos alimentos não será realizada pelo sistema. | Desenvolvimento de protocolos ou integração com profissionais responsáveis. |
| Integração automática com sistemas externos de registro profissional | A primeira versão não terá integração com bases externas. | Integração futura com serviços oficiais, caso exista API ou mecanismo autorizado. |
| Aplicativo mobile nativo | O projeto será desenvolvido inicialmente como aplicação web. | Desenvolvimento de aplicativos para Android e iOS. |
| Cálculo nutricional completo | O projeto utilizará o cálculo como referência de coerência, sem implementar inicialmente um sistema completo de avaliação nutricional. | Desenvolvimento de regras nutricionais mais específicas com participação de profissional da área. |
| Inteligência artificial para correspondência entre oferta e demanda | A primeira versão utilizará regras definidas pela aplicação. | Implementação futura de mecanismos inteligentes de recomendação. |

## 8. Histórico de alterações

| Sprint | Requisito alterado | Alteração | Motivo | Issue/commit |
|---|---|---|---|---|
| Sprint 2 | `RF-02` | Inclusão das informações de atendimento da instituição. | Permitir relacionar as necessidades ao público atendido. | `#15` |
| Sprint 2 | `RF-15` | Inclusão do perfil de atendimento da instituição. | Permitir maior coerência na análise das demandas. | `#15` |
| Sprint 2 | `RF-16` | Inclusão do número estimado de beneficiários na demanda. | Identificar o público que será beneficiado pelo alimento solicitado. | `#16` |
| Sprint 2 | `RF-18` | Inclusão da verificação de coerência da quantidade solicitada. | Evitar solicitações incompatíveis com o contexto informado. | `#17` |
| Sprint 2 | `RF-19` | Inclusão da possibilidade de justificar demandas excepcionais. | Permitir situações legítimas que ultrapassem a quantidade de referência. | `#17` |
| Sprint 2 | `RN-07` | Inclusão do cálculo de quantidade de referência. | Relacionar beneficiários e consumo per capita. | `#16` |
| Sprint 2 | `RN-08` | Inclusão de alerta para quantidade acima da referência. | Aumentar a transparência das solicitações. | `#17` |
| Sprint 2 | `RN-10` | Definição de que a referência não representa uma cota fixa. | Evitar que diferentes tipos de alimentos e situações sejam tratados da mesma forma. | `#17` |