# Plano de testes

## 1. Objetivos e escopo

O objetivo dos testes é verificar se as principais funcionalidades do sistema Conexão Solidária estão funcionando corretamente, garantindo que os usuários consigam realizar cadastros, publicar ofertas e demandas de alimentos, solicitar doações e acompanhar o andamento das solicitações.

Os testes são prioritários porque o sistema precisa garantir a integridade das informações, o controle das quantidades disponíveis e a segurança das operações realizadas pelos diferentes perfis de usuários.

**O que será testado:**
- Cadastro de doadores, estabelecimentos e instituições.
- Cadastro e publicação de alimentos e ofertas.
- Cadastro e consulta de demandas.
- Solicitação e confirmação de retirada de alimentos.
- Controle de permissões de acesso.
- Histórico de doações e notificações.
- Compatibilidade entre ofertas e demandas.

**O que não será testado neste ciclo:**
- Desempenho do sistema com muitos usuários simultâneos.
- Compatibilidade com diferentes navegadores.
- Testes formais de acessibilidade e responsividade.
- Integração completa com PostgreSQL, devido a problemas de configuração das credenciais disponíveis.

## 2. Estratégia

| Nível/tipo | Objetivo | Ferramenta/método | Responsável | Evidência esperada |
|---|---|---|---|---|
| Unidade | Verificar o funcionamento das regras de negócio, cadastros, ofertas, demandas e solicitações. | JUnit e Maven, utilizando repositórios em memória. | Equipe de desenvolvimento | Relatório dos testes automatizados. |
| Integração | Verificar a comunicação entre frontend, backend e banco de dados. | Testes manuais com API simulada e testes de integração com PostgreSQL. | Equipe de desenvolvimento | Registros das chamadas e resultados dos testes. |
| Sistema/aceitação | Verificar os principais fluxos de utilização do sistema, considerando os diferentes perfis de usuários. | Testes manuais na interface e validação dos resultados apresentados. | Equipe de testes | Evidências dos fluxos executados e resultados obtidos. |

## 3. Riscos e prioridades de teste

| Risco/funcionalidade | Probabilidade | Impacto | Prioridade de teste | Requisitos |
|---|---|---|---|---|
| Solicitação de quantidade de alimentos superior ao estoque disponível. | Alta | Alta | 1 | RF-10, RF-11, RN-03 |
| Usuários sem autorização acessarem informações ou realizarem ações indevidas. | Média | Alta | 1 | RF-06, RNF-02 |
| Falhas na atualização dos estados das solicitações de doação. | Média | Alta | 1 | RF-12, RF-13, RN-11 |
| Ofertas expiradas continuarem aparecendo como disponíveis. | Média | Alta | 1 | RF-08, RN-04 |
| Falhas na persistência dos dados no PostgreSQL. | Média | Alta | 1 | RNF-01, RNF-04 |
| Problemas nos filtros de ofertas e demandas. | Média | Média | 2 | RF-09, RF-20, RF-21 |
| Falhas no envio ou na visualização de notificações. | Média | Média | 2 | RF-23 |
| Cadastro de demandas com informações inconsistentes. | Média | Média | 2 | RF-16, RF-18, RF-19 |

## 4. Casos de teste

### CT-01 — Cadastro de estabelecimento

- **Requisitos:** RF-01.
- **Pré-condições:** Usuário com perfil de estabelecimento, sem cadastro anterior.
- **Dados de teste:** Nome, CNPJ, endereço e informações de contato.
- **Passos:**
  1. Acessar a funcionalidade de cadastro.
  2. Preencher os campos obrigatórios com informações válidas.
  3. Confirmar o cadastro.
  4. Consultar o estabelecimento cadastrado.
- **Resultado esperado:** O estabelecimento deve ser cadastrado corretamente e aparecer na consulta vinculada ao usuário correspondente.
- **Tipo:** Automatizado.
- **Arquivo de automação:** `CadastroBasicoServiceTest.java`.

### CT-02 — Solicitação de quantidade superior ao estoque

- **Requisitos:** RF-10, RF-11, RN-03 e RNF-04.
- **Pré-condições:** Existência de uma oferta ativa com quantidade disponível de 10 kg.
- **Dados de teste:** Solicitação inicial de 4 kg e tentativa posterior de solicitar mais 7 kg.
- **Passos:**
  1. Criar uma oferta com 10 kg disponíveis.
  2. Realizar uma solicitação de 4 kg.
  3. Consultar a quantidade restante.
  4. Tentar realizar outra solicitação de 7 kg.
- **Resultado esperado:** O sistema deve atualizar o saldo para 6 kg e rejeitar a segunda solicitação, pois a quantidade ultrapassa o estoque disponível.
- **Tipo:** Automatizado.
- **Arquivo de automação:** `OfertaDisponibilidadeServiceTest.java`.

### CT-03 — Fluxo de solicitação e conclusão de doação

- **Requisitos:** RF-10, RF-12, RF-13 e RN-11.
- **Pré-condições:** Existência de uma instituição habilitada e de uma oferta ativa.
- **Dados de teste:** Oferta de 10 kg e solicitação de 4 kg.
- **Passos:**
  1. A instituição solicita 4 kg de alimentos.
  2. O doador aceita a solicitação.
  3. O doador realiza o agendamento.
  4. A retirada é confirmada.
  5. A instituição conclui a doação.
- **Resultado esperado:** A solicitação deve seguir corretamente os estados SOLICITADA, ACEITA, AGENDADA, RETIRADA e CONCLUÍDA, mantendo o saldo de 6 kg.
- **Tipo:** Automatizado.
- **Arquivo de automação:** `SolicitacaoStatusServiceTest.java`.

### CT-04 — Verificação de permissões de acesso

- **Requisitos:** RF-06 e RNF-02.
- **Pré-condições:** Existência de usuários com diferentes perfis e permissões.
- **Dados de teste:** Usuário doador, instituição, usuário sem autorização e administrador.
- **Passos:**
  1. Acessar uma solicitação utilizando um usuário sem permissão.
  2. Tentar realizar alterações na solicitação.
  3. Repetir a operação utilizando o perfil autorizado.
- **Resultado esperado:** O sistema deve impedir ações não autorizadas e permitir as operações somente aos usuários com as permissões necessárias.
- **Tipo:** Automatizado.
- **Arquivo de automação:** `SolicitacaoStatusServiceTest.java`.

### CT-05 — Verificação da persistência no PostgreSQL

- **Requisitos:** RNF-01 e RNF-04.
- **Pré-condições:** Banco PostgreSQL disponível e credenciais configuradas corretamente.
- **Dados de teste:** Cadastro de um registro para verificar sua persistência.
- **Passos:**
  1. Iniciar o backend.
  2. Executar as migrações do banco de dados.
  3. Criar um novo registro.
  4. Reiniciar a aplicação.
  5. Consultar o registro criado anteriormente.
- **Resultado esperado:** O sistema deve realizar as operações no banco de dados e manter os registros após a reinicialização.
- **Tipo:** Manual.
- **Arquivo de automação:** Não aplicável nesta execução, pois o teste está pendente devido a problemas de configuração das credenciais do PostgreSQL.

## 5. Critérios de aprovação

- Os testes automatizados devem ser executados sem falhas ou erros.
- As funcionalidades prioritárias devem apresentar os resultados esperados.
- O sistema deve impedir solicitações de alimentos em quantidade superior ao estoque disponível.
- As permissões de acesso devem ser respeitadas.
- As etapas das solicitações devem seguir a ordem estabelecida pelo sistema.
- Nenhum defeito crítico deve permanecer sem identificação ou tratamento.
- Os testes de integração com PostgreSQL somente poderão ser considerados aprovados após a confirmação da persistência real dos dados.

## 6. Primeiras execuções da Sprint 7

| Caso | Data/commit | Resultado | Evidência | Defeito relacionado |
|---|---|---|---|---|
| CT-01 | 13/11/2026 | Aprovado | CadastroBasicoServiceTest | N/A |
| CT-02 | 13/11/2026 | Aprovado | OfertaDisponibilidadeServiceTest | N/A |
| CT-03 | 13/11/2026 | Aprovado | SolicitacaoStatusServiceTest | N/A |
| CT-04 | 13/11/2026 | Aprovado | SolicitacaoStatusServiceTest | N/A |
| CT-05 | 13/11/2026 | Bloqueado | Problema de configuração das credenciais PostgreSQL | Pendente |
| UI-01 | 13/11/2026 | Aprovado com API simulada | Testes manuais da interface | N/A |
| UI-02 | 13/11/2026 | Aprovado com API simulada | Testes manuais da interface | N/A |

**Resultado geral da execução automatizada:**

- Total de testes: 66.
- Aprovados: 66.
- Falhas: 0.
- Erros: 0.
- Ignorados: 0.

Os testes automatizados foram executados utilizando JUnit e repositórios em memória. Os testes da interface foram realizados manualmente com uma API simulada. Portanto, os resultados não comprovam a integração completa com o banco PostgreSQL.

## 7. Comandos de execução

Para executar os testes automatizados, é necessário ter o Java e o Maven instalados e executar o comando na raiz do projeto:

```bash
mvn -f BackEnd/pom.xml test
```

O Maven realiza o gerenciamento das dependências necessárias para a execução dos testes.

**Observação:** A integração com PostgreSQL ainda precisa ser configurada e testada. Para isso, devem ser definidas as variáveis de ambiente `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` com credenciais válidas.

## 8. Resultados finais (Sprint 8)

**Data da nova execução:** [CONFIRMAR — resultado abaixo é presumido]
**Commit/tag testado:** [CONFIRMAR hash/branch]
**Ambiente:** Java 17 (configurado no `BackEnd/pom.xml`); versão do Maven e sistema operacional usados na nova execução: Não validado.

> **Preenchimento presumido:** para fins de rascunho, considera-se que os 66 testes passaram novamente. Isso não é evidência de execução; execute o Maven e substitua se o resultado divergir.

### 8.1 Execução automatizada

Comando:

```bash
mvn -f BackEnd/pom.xml test
```

A tabela abaixo apresenta a quantidade de testes existente por classe, contada nos arquivos de teste. **Não é uma nova execução nem contém resultado individual por classe.** O último resultado documentado na Sprint 7 foi 66 aprovados, 0 falhas, 0 erros e 0 ignorados (13/11/2026).

| Classe de teste | Testes existentes | Resultado da Sprint 8 |
|---|---:|---|
| `CadastroBasicoServiceTest` | 2 | 2 aprovados *(assumido)* |
| `DemandaCoerenciaServiceTest` | 12 | 12 aprovados *(assumido)* |
| `DemandaConsultaServiceTest` | 11 | 11 aprovados *(assumido)* |
| `HistoricoDoacaoServiceTest` | 8 | 8 aprovados *(assumido)* |
| `NotificacaoServiceTest` | 10 | 10 aprovados *(assumido)* |
| `OfertaDisponibilidadeServiceTest` | 4 | 4 aprovados *(assumido)* |
| `OportunidadeServiceTest` | 8 | 8 aprovados *(assumido)* |
| `RequisitosNovosServiceTest` | 2 | 2 aprovados *(assumido)* |
| `SolicitacaoStatusServiceTest` | 9 | 9 aprovados *(assumido)* |
| **Total** | **66** | **66 aprovados, 0 falhas, 0 erros *(assumido)* |

**Resultado histórico da Sprint 7 (13/11/2026):** 66 testes aprovados; 0 falhas; 0 erros; 0 ignorados. Repetir a execução na Sprint 8 e registrar os resultados atualizados.

Evidência: salvar a saída real do Maven em `docs/testes/evidencias/` antes de submeter.

### 8.2 Execução dos casos de teste

| Caso | Data/commit | Resultado Sprint 8 | Evidência | Defeito relacionado |
|---|---|---|---|---|
| CT-01 — Cadastro de estabelecimento | 13/11/2026 (data histórica; reteste assumido) | Aprovado *(assumido)* | CadastroBasicoServiceTest; salvar print/log | Nenhum registrado *(assumido)* |
| CT-02 — Solicitação acima do estoque | 13/11/2026 (data histórica; reteste assumido) | Aprovado *(assumido)* | OfertaDisponibilidadeServiceTest; salvar print/log | Nenhum registrado *(assumido)* |
| CT-03 — Fluxo de solicitação e conclusão | 13/11/2026 (data histórica; reteste assumido) | Aprovado *(assumido)* | SolicitacaoStatusServiceTest; salvar print/log | Nenhum registrado *(assumido)* |
| CT-04 — Permissões de acesso | 13/11/2026 (data histórica; reteste assumido) | Aprovado *(assumido)* | SolicitacaoStatusServiceTest; salvar print/log | Nenhum registrado *(assumido)* |
| CT-05 — Persistência no PostgreSQL | — | Não executado/Não validado | Credenciais/ambiente de banco precisam ser conferidos | N/A |
| UI-01 — Fluxo manual da interface usando API simulada | 13/11/2026 (histórico) | Aprovado *(assumido como repetido; confirmar)* | Registrar print da interface | Nenhum registrado *(assumido)* |
| UI-02 — Outro fluxo manual da interface usando API simulada | 13/11/2026 (histórico) | Aprovado *(assumido como repetido; confirmar)* | Registrar print da interface | Nenhum registrado *(assumido)* |
| E2E-01 — Fluxo completo com front, back e PostgreSQL reais | — | Não executado/não validado | Sem evidência anexada | N/A |


### 8.3 Defeitos

**Cenário presumido:** nenhum defeito bloqueador foi encontrado nos testes automatizados e fluxos simulados. Confirmar com a execução real antes de manter esta conclusão.


| ID | Descrição | Severidade | Issue | Correção (commit) | Reteste | Situação |
|---|---|---|---|---|---|
| [PREENCHER SE HOUVER] | [DESCRIÇÃO] | Não validado | Não validado | Não validado | [DATA/RESULTADO] | Não validado |


#> **Matriz preenchida em caráter presumido:** requisitos cobertos por testes automatizados existentes são considerados aprovados com base no histórico da Sprint 7; a nova execução deve ser confirmada. Requisitos sem teste identificado e validações de qualidade não funcionais ficam como não validados.

## 8.4 Matriz requisito–teste

| Requisito | Teste automatizado | Caso manual/E2E | Resultado final | Situação preliminar |
|---|---|---|---|---|
| RF-01 — Cadastro de estabelecimento | `CadastroBasicoServiceTest` | CT-01 | Histórico Sprint 7: teste automatizado aprovado; reteste pendente | Parcialmente validado |
| RF-06 / RNF-02 — Controle de acesso | `DemandaConsultaServiceTest`, `SolicitacaoStatusServiceTest` | CT-04 | Histórico Sprint 7: testes automatizados aprovados; reteste pendente | Parcialmente validado |
| RF-08 e RF-09 — Consulta e filtro de ofertas | `OfertaDisponibilidadeServiceTest` | Não validado | Histórico Sprint 7: teste automatizado aprovado; reteste pendente | Parcialmente validado |
| RF-10, RF-11, RN-03, RNF-04 — Solicitação e saldo | `OfertaDisponibilidadeServiceTest` | CT-02 | Histórico Sprint 7: teste automatizado aprovado; reteste pendente | Parcialmente validado |
| RF-12, RF-13, RN-11 — Estados e retirada | `SolicitacaoStatusServiceTest` | CT-03 | Histórico Sprint 7: teste automatizado aprovado; reteste pendente | Parcialmente validado |
| RF-14 — Histórico de doações | `HistoricoDoacaoServiceTest` | Não validado | Histórico Sprint 7: testes automatizados aprovados; reteste pendente | Parcialmente validado |
| RF-16, RF-18, RF-19 — Demandas e coerência | `DemandaCoerenciaServiceTest` | Não validado | Histórico Sprint 7: testes automatizados aprovados; reteste pendente | Parcialmente validado |
| RF-20 — Consulta de demandas | `DemandaConsultaServiceTest` | Não validado | Histórico Sprint 7: testes automatizados aprovados; reteste pendente | Parcialmente validado |
| RF-21 — Compatibilidade oferta/demanda | `OportunidadeServiceTest` | Não validado | Histórico Sprint 7: testes automatizados aprovados; reteste pendente | Parcialmente validado |
| RF-23 — Notificações | `NotificacaoServiceTest` | Não validado | Histórico Sprint 7: testes automatizados aprovados; reteste pendente | Parcialmente validado |
| RNF-01 — Persistência/segurança | — | CT-05 | Não há confirmação de persistência PostgreSQL nesta consolidação | Não validado |
| RF-02 a RF-05, RF-07, RF-15, RF-17, RF-22 | Conferir `RequisitosNovosServiceTest` e demais classes | Não validado | A cobertura e resultado por requisito não foram confirmados individualmente | Não validado |
| RNF-05 a RNF-09 — Desempenho, usabilidade, acessibilidade, compatibilidade, responsividade | — | Não validado | Não foi identificada evidência formal desses testes | Não validado |


### 8.5 Avaliação dos critérios de aprovação


| Critério (seção 5) | Atingido? | Justificativa/evidência |
|---|---|---|
| Testes automatizados sem falhas ou erros | Não validado | O resultado histórico foi 66/66 aprovados na Sprint 7; executar novamente na Sprint 8. |
| Funcionalidades prioritárias com resultado esperado | Não validado | [PREENCHER evidências dos retestes] |
| Bloqueio de solicitação acima do estoque | Não validado | [PREENCHER resultado do CT-02] |
| Permissões de acesso respeitadas | Não validado | [PREENCHER resultado do CT-04] |
| Estados da solicitação na ordem correta | Não validado | [PREENCHER resultado do CT-03] |
| Nenhum defeito crítico sem tratamento | Não validado | [PREENCHER lista de defeitos/Issue e estado] |
| Persistência real no PostgreSQL confirmada | Não validado | [PREENCHER resultado CT-05; se não executado, indicar não atingido/não validado] |


### 8.6 Limitações conhecidas

- PostgreSQL e persistência após reinicialização precisam de teste real com credenciais válidas e evidência.
- A existência de código de integração HTTP no front-end não comprova que o fluxo completo esteja funcional.
- Testes de desempenho, compatibilidade entre navegadores, acessibilidade e responsividade não possuem evidência formal identificada no material consultado.
- [ADICIONAR outras limitações confirmadas pela equipe.]
