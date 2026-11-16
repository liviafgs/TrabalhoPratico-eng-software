# Sprint 7 — Estratégia de testes e primeira execução verificável

- **Data de entrega:** 16/11/2026
- **Pontuação:** 2,5 pontos
- **Tag obrigatória:** `sprint-07`
- **Responsável por conferir este arquivo:** Lívia Fagundes

## 1. Pergunta que esta sprint deve responder

**Quais riscos e funcionalidades serão testados, como e com quais resultados iniciais?**

A Sprint 7 tem como objetivo identificar os principais riscos do sistema Conexão Solidária e definir estratégias para verificar o funcionamento das funcionalidades prioritárias da aplicação.

Foram considerados testes relacionados aos cadastros, ofertas de alimentos, demandas, solicitações de doação, controle de disponibilidade, permissões de acesso, histórico e notificações.

A estratégia envolve testes automatizados no backend, testes manuais na interface e planejamento dos testes de integração com o banco de dados.

## 2. Objetivo e resultado da sprint

**Objetivo planejado:**

Elaborar um plano de testes para o Conexão Solidária, identificando os principais riscos do sistema, definindo os casos de teste, critérios de aprovação e estratégias de validação. Também realizar as primeiras execuções verificáveis, registrando os resultados e possíveis problemas encontrados.

**Resultado efetivamente alcançado:**

Foi elaborado o planejamento dos testes, contemplando objetivos, escopo, níveis de teste, riscos, prioridades, casos de teste e critérios de aprovação.

Na primeira execução, foram realizados 66 testes automatizados utilizando JUnit, todos aprovados, sem falhas ou erros. Também foram realizados testes manuais na interface utilizando uma API simulada, verificando fluxos de cadastro, publicação de ofertas, envio de demandas e solicitações.

A integração completa com PostgreSQL permaneceu pendente devido a problemas na configuração das credenciais de acesso ao banco de dados.

Dessa forma, a sprint apresentou resultados positivos na validação das regras de negócio, mas ainda existem pontos de integração que precisam ser verificados.

## 3. Checklist do artefato central — 0,75 ponto

**Entrega esperada:** `docs/testes/plano-de-testes.md`, casos de teste completos, critérios de aprovação e matriz requisito–teste.

- [x] Objetivo, escopo, tipos e níveis de teste definidos.
- [x] Casos com pré-condição, dados, passos e resultado esperado.
- [x] Primeiros resultados registrados.
- [x] Código de testes automatizados existente no backend.
- [x] Defeitos e ao menos uma correção/reteste, caso encontrados.

**Observação:** Os testes automatizados foram executados com sucesso. Entretanto, não há registro de uma correção de defeito acompanhada de reteste nesta rodada. A integração com PostgreSQL permanece pendente.

### Links dos artefatos

| Artefato criado/atualizado | Link na tag da sprint | O que mudou |
|---|---|---|
| Plano e relatório de testes | https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/docs/testes | Documentação dos objetivos, escopo, estratégias, riscos, casos de teste e resultados. |
| Testes automatizados do backend | - | Execução e registro de 66 testes automatizados. |
| Evidências dos testes | - | Registro dos resultados das primeiras execuções. |

## 4. Incremento da aplicação web — 0,75 ponto

### O que foi implementado ou evoluído

Nesta sprint, o foco foi a validação das funcionalidades já desenvolvidas no Conexão Solidária.

Foram executados testes automatizados para verificar as regras de negócio relacionadas a:

- Cadastro de usuários e instituições.
- Cadastro e consulta de alimentos.
- Cadastro e validação de demandas.
- Consulta e filtragem de ofertas.
- Controle da quantidade disponível de alimentos.
- Solicitação e conclusão de doações.
- Controle de permissões de acesso.
- Histórico de doações.
- Notificações.
- Compatibilidade entre ofertas e demandas.

Também foram realizados testes manuais na interface utilizando uma API simulada, verificando o comportamento dos formulários e das chamadas HTTP.

Os testes automatizados apresentaram 66 aprovações, sem falhas ou erros. A integração completa com PostgreSQL ainda precisa ser validada.

### Como executar e verificar

Na raiz do projeto, executar:

```bash
mvn -f BackEnd/pom.xml test
```

O comando executa os testes automatizados do backend utilizando Maven.

**Resultado registrado:**
- Total de testes: 66.
- Aprovados: 66.
- Falhas: 0.
- Erros: 0.
- Ignorados: 0.

| Requisito/Issue | Código ou protótipo | Evidência de execução |
|---|---|---|
| RF-01 — Cadastro de estabelecimento | `CadastroBasicoServiceTest` | Teste automatizado aprovado. |
| RF-06 — Controle de permissões | `DemandaConsultaServiceTest` e `SolicitacaoStatusServiceTest` | Testes automatizados aprovados. |
| RF-08 e RF-09 — Consulta de ofertas | `OfertaDisponibilidadeServiceTest` | Testes automatizados aprovados. |
| RF-10 e RF-11 — Solicitação e disponibilidade | `OfertaDisponibilidadeServiceTest` | Teste automatizado aprovado. |
| RF-12 e RF-13 — Estados das solicitações | `SolicitacaoStatusServiceTest` | Testes automatizados aprovados. |
| RF-23 — Notificações | `NotificacaoServiceTest` | Testes automatizados aprovados. |

## 5. Scrum e gestão do trabalho — 0,50 ponto

### Sprint Backlog

| Issue | Descrição | Responsável | Critério de aceitação/conclusão | Situação |
|---|---|---|---|---|
| #30 | Elaborar plano de testes | Não atribuído no GitHub | Plano contendo estratégia, fluxos, critérios e resultados esperados. | Aberta (Backlog) |
| #31 | Implementar testes automatizados | Não atribuído no GitHub | Testes implementados e resultados registrados. | Aberta (Backlog) |
| #32 | Executar testes dos principais fluxos | Não atribuído no GitHub | Resultados aprovados, reprovados e não executados documentados. | Aberta (Backlog) |
| #33 | Registrar evidências dos testes | Não atribuído no GitHub | Evidências de execução documentadas. | Aberta (Backlog) |

### Acompanhamento

- **GitHub Project:** https://github.com/liviafgs/TrabalhoPratico-eng-software
- **Issues da Sprint 7:** 
  - #30 — Elaborar plano de testes.
  - #31 — Implementar testes automatizados.
  - #32 — Executar testes dos principais fluxos.
  - #33 — Registrar evidências dos testes.
- **Reuniões/decisões:** Consultar os registros disponíveis em `docs/reunioes/`.
- **Impedimentos:** Problemas na configuração das credenciais do PostgreSQL, impedindo a validação completa da integração com o banco de dados.
- **Mudanças de escopo:** Não foram registradas mudanças de escopo nesta documentação.

## 6. GitHub, documentação e rastreabilidade — 0,50 ponto

| Tipo de evidência | Link | O que comprova |
|---|---|---|
| Issue #30 | https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/30 | Planejamento dos testes. |
| Issue #31 | https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/31 | Implementação dos testes automatizados. |
| Issue #32 | https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/32 | Execução dos principais fluxos. |
| Issue #33 | https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/33 | Registro das evidências dos testes. |
| Pull Requests | https://github.com/liviafgs/TrabalhoPratico-eng-software/pulls?q=is%3Apr+state%3Aclosed | Histórico de PRs encerrados do projeto. |
| Repositório | https://github.com/liviafgs/TrabalhoPratico-eng-software | Código-fonte e documentação do projeto. |
| Plano e relatório | https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/testes/plano-e-relatorio-de-testes.md | Planejamento, casos de teste e resultados das execuções. |

### Rastreabilidade resumida

| Requisito | Issue | Artefato/modelo/decisão | Código | Teste/evidência |
|---|---|---|---|---|
| Cadastro de usuários e estabelecimentos | #30 e #31 | Plano de testes | `CadastroBasicoServiceTest` | TC-01 e TC-02 |
| Cadastro e validação de demandas | #31 e #32 | Plano de testes | `DemandaCoerenciaServiceTest` | TC-05, TC-06 e TC-07 |
| Consulta e filtragem de ofertas | #31 e #32 | Plano de testes | `OfertaDisponibilidadeServiceTest` | TC-09 e TC-11 |
| Controle de disponibilidade dos alimentos | #31 e #32 | Plano de testes | `OfertaDisponibilidadeServiceTest` | TC-10 |
| Solicitações e conclusão de doações | #31 e #32 | Plano de testes | `SolicitacaoStatusServiceTest` | TC-12 e TC-13 |
| Histórico e notificações | #31 e #32 | Plano de testes | `HistoricoDoacaoServiceTest` e `NotificacaoServiceTest` | TC-14 e TC-15 |
| Compatibilidade entre ofertas e demandas | #31 e #32 | Plano de testes | `OportunidadeServiceTest` | TC-16 |
| Registro dos resultados | #33 | Plano e relatório de testes | Não aplicável | Relatório com resultados das execuções |

## 7. Revisão do incremento

- **O que foi demonstrado:** A execução de 66 testes automatizados aprovados e a realização de testes manuais na interface utilizando uma API simulada.
- **Critérios atendidos:** Planejamento dos testes, definição dos riscos, documentação dos casos de teste, definição dos resultados esperados e registro das primeiras execuções.
- **Itens não concluídos:** Validação completa da integração com PostgreSQL, testes de desempenho, compatibilidade entre navegadores e avaliação formal de acessibilidade e responsividade.
- **Motivo das pendências:** Problemas na configuração das credenciais necessárias para conexão com PostgreSQL e limitações do escopo desta rodada de testes.
- **Feedback recebido e ajustes:** Os resultados foram registrados no relatório de testes, diferenciando os testes aprovados daqueles que permaneceram bloqueados ou não foram executados.

## 8. Retrospectiva e próxima sprint

- **Funcionou bem:** A organização dos casos de teste, a definição das prioridades e a execução dos testes automatizados, que apresentaram resultados positivos.
- **Precisa melhorar:** A configuração do ambiente de integração com PostgreSQL e a ampliação das evidências dos testes realizados.
- **Ação concreta para a próxima sprint:** Resolver os problemas de conexão com o banco de dados, ampliar os testes de integração e continuar validando os requisitos da aplicação.

## 9. O que não será considerado suficiente

- Listar tipos de teste sem aplicá-los.
- Casos sem resultado esperado.
- Declarar que todos passaram sem evidência.

## 10. Links enviados no UFLA Virtual

- **Tag `sprint-07`:** [COLAR LINK DA TAG]
- **Este arquivo na tag:** [COLAR LINK DO ARQUIVO]
- **Observação adicional:** A Sprint 7 contemplou o planejamento e as primeiras execuções verificáveis dos testes do Conexão Solidária. Foram registrados 66 testes automatizados aprovados e testes manuais com API simulada. A integração completa com PostgreSQL permanece pendente.
