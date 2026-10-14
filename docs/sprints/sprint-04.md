# Sprint 4 — Princípios de projeto e decisões locais

- **Data de entrega:** 13/10/2026
- **Pontuação:** 2,5 pontos
- **Tag obrigatória:** `sprint-04`
- **Responsável por conferir este arquivo:** `Equipe`

## 1. Pergunta que esta sprint deve responder

**Como responsabilidades, módulos e interfaces locais serão organizados e justificados?**

## 2. Objetivo e resultado da sprint

**Objetivo planejado:** `Organizar internamente a solução Conexão Solidária, aplicando princípios de responsabilidade, modularidade, coesão, baixo acoplamento e separação de interesses em partes concretas do código existente. A sprint trata de decisões internas e locais; a arquitetura global será formalizada na Sprint 6 e os padrões específicos, na Sprint 5.`

**Resultado efetivamente alcançado:** `O repositório passou a ter o documento de decisões de projeto, com a decomposição da solução, três decisões registradas (DP-01 a DP-03), a análise de coesão, acoplamento, responsabilidade e testabilidade e as melhorias identificadas. Duas decisões foram implementadas no código (DP-01 e DP-02), com teste e com o estado anterior e posterior documentados. Modelagem e backlog foram atualizados. A correção das declarações de package (PRJ-03) e as decisões DP-03 e PRJ-05 ficaram registradas para as próximas sprints.`

## 3. Checklist do artefato central — 0,75 ponto

**Entrega esperada:** `docs/projeto/decisoes-de-projeto.md`, decomposição e decisões relacionadas a coesão, acoplamento, responsabilidade e testabilidade.

- [x] Responsabilidades de módulos/componentes locais identificadas (seções 1 e 3 do documento).
- [x] Alternativas consideradas e decisão justificada (DP-01, DP-02 e DP-03).
- [x] Trade-offs ou consequências registrados (campo "Consequências" de cada decisão).
- [x] Código afetado linkado (seção 7 do documento).
- [x] Backlog atualizado com débitos ou tarefas descobertas (seção 9.10 do backlog: PRJ-01 a PRJ-05).

### Links dos artefatos

| Artefato criado/atualizado | Link na tag da sprint | O que mudou |
|---|---|---|
| `docs/projeto/decisoes-de-projeto.md` | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/docs/projeto/decisoes-de-projeto.md` | Criado: decomposição, decisões DP-01 a DP-03, análise de coesão, acoplamento, responsabilidade e testabilidade, e melhorias identificadas. |
| `modelaem/modelagem.md` | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/modelaem/modelagem.md` | Correspondência entre modelo e código atualizada e histórico da Sprint 4. |
| `docs/testes/backlog-produto.mb` | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/docs/testes/backlog-produto.mb` | Nova seção 9.10 com as tarefas técnicas PRJ-01 a PRJ-05 e organização da Sprint 4. |

## 4. Incremento da aplicação web — 0,75 ponto

**Incremento mínimo esperado:** Implementação/refatoração que demonstre a decisão, com comparação ou vínculo claro entre documento e código.

### O que foi implementado ou evoluído

- **DP-01 — Disponibilidade da oferta:** criação de `DisponibilidadeOfertaService`, que passa a ser o único lugar com a regra "oferta disponível = não expirada e com quantidade restante" (RF-11, RN-03, RN-04, RNF-04). Antes, a regra estava repetida em quatro lugares (`SolicitacaoOfertaService`, `OfertaService` e `OfertaController`) e `OfertaService` dependia de `SolicitacaoOfertaService` por um construtor que aceitava `null`.
- **DP-02 — Autorização por propriedade:** criação de `AccessControl.requireDonoOuAdministrador`, que substitui a verificação repetida em `confirmarRetirada` e `cancelar` (RF-06, RNF-02).
- **Teste:** `OfertaDisponibilidadeServiceTest` mantém seus três cenários e ganhou um cenário de cancelamento por usuário não autorizado e por administrador.

O comportamento observável não mudou: respostas HTTP, códigos de status e mensagens de erro são os mesmos.

**Comparação entre documento e código:**

| Estado | Commit | O que mostra |
|---|---|---|
| Anterior | `3eac19745701df62a95ae455459cde80f7872785` | Regra de disponibilidade repetida em quatro lugares; `OfertaService` com construtor aceitando `null`; verificação "dono ou administrador" duplicada em `SolicitacaoOfertaService`. |
| Posterior | `[cdc345e3ddf38c7a8882c86b3ec9a54ea02a18e0]` | Regra em `DisponibilidadeOfertaService` e autorização em `AccessControl`. |

### Como executar e verificar

```bash
git clone https://github.com/liviafgs/TrabalhoPratico-eng-software.git
cd TrabalhoPratico-eng-software/BackEnd
mvn test
mvn compile exec:java
```

O servidor inicia em `http://localhost:8080`.

> Enquanto PRJ-03 não for aplicada, `mvn test` e `mvn compile` falham por causa das declarações de `package` descritas na seção 4.2 do documento de decisões.

| Requisito/Issue | Código ou protótipo | Evidência de execução |
|---|---|---|
| `RF-11`, `RN-03`, `RN-04`, `RNF-04` / `#66` | [`DisponibilidadeOfertaService.java`](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/BackEnd/src/main/java/conexaosolidaria/service/DisponibilidadeOfertaService.java), [`OfertaService.java`](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/BackEnd/src/main/java/conexaosolidaria/service/OfertaService.java), [`SolicitacaoOfertaService.java`](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/BackEnd/src/main/java/conexaosolidaria/service/SolicitacaoOfertaService.java), [`OfertaController.java`](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/BackEnd/src/main/java/conexaosolidaria/controller/OfertaController.java) | [`OfertaDisponibilidadeServiceTest.java`](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/BackEnd/src/test/java/conexaosolidaria/service/OfertaDisponibilidadeServiceTest.java); `[print do mvn test — disponível após PRJ-03]` |
| `RF-06`, `RNF-02` / `#67` | [`AccessControl.java`](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/BackEnd/src/main/java/conexaosolidaria/security/AccessControl.java), [`SolicitacaoOfertaService.java`](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/BackEnd/src/main/java/conexaosolidaria/service/SolicitacaoOfertaService.java) | Cenário de cancelamento por usuário não autorizado (403) e por administrador em `OfertaDisponibilidadeServiceTest`. |

## 5. Scrum e gestão do trabalho — 0,50 ponto

### Sprint Backlog

| Issue | Descrição | Responsável | Critério de aceitação/conclusão | Situação |
|---|---|---|---|---|
| `#66` | PRJ-01: centralizar a regra de disponibilidade da oferta. | `Equipe` | Regra em um único lugar e usada pelos dois serviços e pelo controller. | Concluída |
| `#67` | PRJ-02: concentrar a autorização por propriedade no `AccessControl`. | `Equipe` | Verificação feita por `requireDonoOuAdministrador`, com teste. | Concluída |
| `#68` | PRJ-03: corrigir packages e imports do backend. | `Equipe` | Backend compila e o teste de ofertas é executado. | Pendente (planejada para a Sprint 5) |
| `#69` | PRJ-04: migrar a verificação de permissões dos controllers para os serviços. | `Equipe` | Controllers sem chamadas a `AccessControl`. | Pendente (Sprint 5) |
| `#70` | PRJ-05: separar rotas e composição de dependências em `ApplicationConfig`. | `Equipe` | Montagem de objetos e tabela de rotas em classes distintas. | Pendente (Sprint 6) |

### Acompanhamento

- **GitHub Project:** `https://github.com/users/liviafgs/projects/3`
- **Reuniões/decisões:** `[adicionar links para docs/reunioes/ quando houver]`
- **Impedimentos:** `Cinco arquivos do backend e o teste de ofertas declaram package fora da estrutura de diretórios, o que impede a compilação. A correção está registrada em PRJ-03 e não foi feita nesta sprint, para não alterar arquivos de outras entregas.`
- **Mudanças de escopo:** `As tarefas PRJ-03, PRJ-04 e PRJ-05 foram identificadas na análise de coesão e acoplamento e registradas no backlog para as Sprints 5 e 6.`

## 6. GitHub, documentação e rastreabilidade — 0,50 ponto

| Tipo de evidência | Link | O que comprova |
|---|---|---|
| Issue | `https://github.com/liviafgs/TrabalhoPratico-eng-software/issues` | Issues técnicas `#66` a `#70` (PRJ-01 a PRJ-05) e seus critérios de aceitação. |
| Pull Request | `[link do Pull Request]` | Revisão e integração das alterações. |
| Commit | `[3eac19745701df62a95ae455459cde80f7872785]` (anterior) e `[cdc345e3ddf38c7a8882c86b3ec9a54ea02a18e0]` (posterior); histórico: `https://github.com/liviafgs/TrabalhoPratico-eng-software/commits/main/` | Estado anterior e posterior da refatoração. |
| Código/arquivo | [`decisoes-de-projeto.md`](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/docs/projeto/decisoes-de-projeto.md) e [`DisponibilidadeOfertaService.java`](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/BackEnd/src/main/java/conexaosolidaria/service/DisponibilidadeOfertaService.java) | Decisões de projeto documentadas e implementadas. |
| Teste/captura/relatório | [`OfertaDisponibilidadeServiceTest.java`](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/BackEnd/src/test/java/conexaosolidaria/service/OfertaDisponibilidadeServiceTest.java) e `[print do resultado do mvn test]` | Cenários de disponibilidade e de autorização. |

### Rastreabilidade resumida

| Requisito | Issue | Artefato/modelo/decisão | Código | Teste/evidência |
|---|---|---|---|---|
| `RF-11`, `RN-03`, `RN-04`, `RNF-04` | `#66` (PRJ-01) | DP-01 em [`decisoes-de-projeto.md`](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/docs/projeto/decisoes-de-projeto.md) | [`DisponibilidadeOfertaService.java`](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/BackEnd/src/main/java/conexaosolidaria/service/DisponibilidadeOfertaService.java) | [`OfertaDisponibilidadeServiceTest.java`](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/BackEnd/src/test/java/conexaosolidaria/service/OfertaDisponibilidadeServiceTest.java) |
| `RF-06`, `RNF-02` | `#67` (PRJ-02) | DP-02 em [`decisoes-de-projeto.md`](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/docs/projeto/decisoes-de-projeto.md) | [`AccessControl.java`](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/BackEnd/src/main/java/conexaosolidaria/security/AccessControl.java) | Cenário de cancelamento por usuário não autorizado e por administrador |
| `RNF-10` | `#68` (PRJ-03) | Problema identificado na seção 4.2 do documento de decisões | Cinco arquivos com `package` a corrigir | Ainda não aplicável |
| `RNF-02`, `RNF-10` | `#69` (PRJ-04) | DP-03 em [`decisoes-de-projeto.md`](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/docs/projeto/decisoes-de-projeto.md) | Seis controllers que ainda verificam permissões | Ainda não aplicável (Sprint 5) |

## 7. Revisão do incremento

- **O que foi demonstrado:** `Foram apresentadas a decomposição da solução, as decisões DP-01 a DP-03, a análise de coesão, acoplamento, responsabilidade e testabilidade e a refatoração de disponibilidade da oferta e de autorização por propriedade, com o estado anterior e posterior nos commits.`
- **Critérios atendidos:** `Decomposição, três decisões registradas no formato exigido, análise em partes concretas do código, melhorias identificadas, duas decisões implementadas com evidência e artefatos atualizados.`
- **Itens não concluídos:** `Correção das declarações de package (PRJ-03), migração da verificação de permissões dos controllers para os serviços (DP-03 / PRJ-04) e separação de rotas e composição em ApplicationConfig (PRJ-05).`
- **Motivo das pendências:** `As mudanças envolvem vários arquivos de outras entregas e foram separadas para não misturar muitas alterações nesta sprint; estão registradas como PRJ-03, PRJ-04 e PRJ-05.`
- **Feedback recebido e ajustes:** `[preencher após a revisão da sprint]`

## 8. Retrospectiva e próxima sprint

- **Funcionou bem:** `A análise de coesão e acoplamento apontou problemas concretos no código existente: regra de disponibilidade repetida, autorização espalhada entre serviços e controllers e inconsistência de package que impede a compilação (registrada em PRJ-03).`
- **Precisa melhorar:** `Compilar e executar os testes antes de integrar alterações, já que a inconsistência de package só foi percebida nesta sprint.`
- **Ação concreta para a próxima sprint:** `Executar mvn test antes de abrir cada Pull Request, como exige o guia de desenvolvimento, e implementar PRJ-03 e PRJ-04.`

## 9. O que não será considerado suficiente

- Apenas listar princípios de projeto.
- Descrever arquitetura global em vez de decisões locais.
- Decisão sem alteração ou evidência no código.

## 10. Links enviados no UFLA Virtual

- **Tag `sprint-04`:** `https://github.com/liviafgs/TrabalhoPratico-eng-software/releases/tag/sprint-04`
- **Este arquivo na tag:** `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-04/docs/sprints/sprint-04.md`
- **Observação adicional:** `A Sprint 4 organiza internamente a solução, com duas decisões de projeto implementadas e documentadas, e deixa registradas as melhorias previstas para as próximas sprints.`
