# Sprint 4 — Princípios de projeto e decisões locais de solução

* **Data de entrega:** `13/10/2026`/
* **Pontuação:** 2,5 pontos
* **Tag obrigatória:** `sprint-04`
* **Responsável por conferir este arquivo:** `Equipe`

## 1. Pergunta que esta sprint deve responder

**Como a solução está organizada internamente e quais decisões de projeto sustentam essa organização?**

## 2. Objetivo e resultado da sprint

**Objetivo planejado:**

Organizar internamente a solução Conexão Solidária, aplicando princípios de responsabilidade, modularidade, coesão, baixo acoplamento e separação de interesses em partes concretas do código existente. Esta sprint trata de decisões internas e locais de projeto; a arquitetura global será formalizada na Sprint 6 e os padrões específicos, na Sprint 5.

**Resultado esperado:**

Ao final da sprint, o repositório deverá possuir o documento de decisões de projeto, com a decomposição da solução, pelo menos três decisões registradas, a análise de coesão, acoplamento e responsabilidade e a identificação de melhorias. Deverá haver também ao menos uma decisão implementada no código, com evidência do estado anterior e posterior, e os modelos e o backlog atualizados.

## 3. Checklist do artefato central

**Entrega esperada:** `docs/projeto/decisoes-de-projeto.md`.

* [x] Decomposição da solução em módulos, classes, serviços e responsabilidades (seção 1 do documento).
* [x] Três decisões de projeto registradas com contexto, alternativas, decisão, justificativa e consequências (DP-01, DP-02 e DP-03).
* [x] Análise de coesão, acoplamento e responsabilidade em partes concretas da solução (seção 3).
* [x] Identificação de melhorias e refatorações necessárias (seção 4).

### Artefatos atualizados

* [x] Modelo estrutural/correspondência com o código: `modelaem/modelagem.md`.
* [x] Backlog com tarefas técnicas e refatorações: `docs/testes/backlog-produto.mb`.
* [x] Requisitos: sem alteração, pois nenhuma decisão mudou comportamento observável.

### Links dos artefatos

| Artefato criado/atualizado | Link | O que deve comprovar |
|---|---|---|
| `docs/projeto/decisoes-de-projeto.md` | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/projeto/decisoes-de-projeto.md` | Decomposição, decisões de projeto, análise de coesão e acoplamento e melhorias identificadas. |
| `modelaem/modelagem.md` | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/modelaem/modelagem.md` | Correspondência atualizada entre modelo e código e histórico da Sprint 4. |
| `docs/testes/backlog-produto.mb` | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/testes/backlog-produto.mb` | Tarefas técnicas PRJ-01 a PRJ-05 e organização da Sprint 4. |

## 4. Incremento da aplicação web

**Incremento mínimo esperado:** implementação de pelo menos uma decisão documentada, refatoração com justificativa e evidência do estado anterior e posterior.

### O que foi implementado ou evoluído

* **DP-01 — Disponibilidade da oferta:** criação de `DisponibilidadeOfertaService`, que passa a ser o único lugar com a regra "oferta disponível = não expirada e com quantidade restante". Antes, a regra estava repetida em quatro lugares (`SolicitacaoOfertaService`, `OfertaService` e `OfertaController`), e `OfertaService` dependia de `SolicitacaoOfertaService` por um construtor que aceitava `null`.
* **DP-02 — Autorização por propriedade:** criação de `AccessControl.requireDonoOuAdministrador`, que substitui a verificação repetida em `confirmarRetirada` e `cancelar`.
* **Correção estrutural:** cinco arquivos declaravam `package main.java.conexaosolidaria...` e não eram encontrados pelo restante do código, o que impedia a compilação do backend. As declarações foram alinhadas à estrutura de diretórios.
* **Teste:** `OfertaDisponibilidadeServiceTest` mantém seus três cenários e ganhou um cenário de cancelamento por usuário não autorizado e por administrador.

O comportamento observável não mudou: respostas HTTP, códigos de status e mensagens de erro são os mesmos.

### Evidência do estado anterior e posterior

| Estado | Commit | O que mostra |
|---|---|---|
| Anterior | `103a097125ff4f4de85ccd8bfd345fb8b4132037]` | Backend sem compilar; regra de disponibilidade repetida em quatro lugares. |
| Posterior | `8bbb7abbefbcb09c8b4829c368ae2b5351b5b1a3` | Backend compilando; regra em `DisponibilidadeOfertaService` e autorização em `AccessControl`. |

### Como executar e verificar

```bash
git clone https://github.com/liviafgs/TrabalhoPratico-eng-software.git
cd TrabalhoPratico-eng-software/BackEnd
mvn test
mvn compile exec:java
```

O servidor inicia em `http://localhost:8080`.

| Requisito/Issue | Código ou artefato | Evidência esperada |
|---|---|---|
| `#66` / PRJ-01 | `DisponibilidadeOfertaService.java`, `OfertaService.java`, `SolicitacaoOfertaService.java`, `OfertaController.java` | Regra de disponibilidade em um único lugar; teste de ofertas passando. |
| `#67` / PRJ-02 | `AccessControl.java`, `SolicitacaoOfertaService.java` | Teste de cancelamento por usuário não autorizado (403) e por administrador. |
| `#68` / PRJ-03 | `SolicitacaoOferta.java`, `StatusSolicitacaoOferta.java`, `SolicitacaoOfertaRepository.java`, `OfertaFiltros.java`, `SolicitacaoOfertaService.java` | Backend compila. |

## 5. Sprint Backlog

| Issue | Descrição | Responsável | Critério de aceitação/conclusão | Situação |
|---|---|---|---|---|
| `#66` | PRJ-01: centralizar a regra de disponibilidade da oferta. | `Equipe` | Regra em um único lugar e usada pelos dois serviços e pelo controller. | Concluída |
| `#67` | PRJ-02: concentrar a autorização por propriedade no `AccessControl`. | `Equipe` | Verificação feita por `requireDonoOuAdministrador`, com teste. | Concluída |
| `#68` | PRJ-03: corrigir packages e imports do backend. | `Equipe` | Backend compila e o teste de ofertas é executado. | Concluída |
| `#69` | PRJ-04: migrar a verificação de permissões dos controllers para os serviços. | `Equipe` | Controllers sem chamadas a `AccessControl`. | Planejada (Sprint 5) |
| `#70` | PRJ-05: separar rotas e composição de dependências em `ApplicationConfig`. | `Equipe` | Montagem de objetos e tabela de rotas em classes distintas. | Planejada (Sprint 6) |

### Acompanhamento

* **GitHub Project:** `https://github.com/users/liviafgs/projects/3`
* **Issues da Sprint 4:** `https://github.com/liviafgs/TrabalhoPratico-eng-software/issues`
* **Reuniões/decisões:** `[adicionar links para docs/reunioes/ quando houver]`
* **Impedimentos:** `O backend não compilava por inconsistência de package e import; o problema foi tratado em PRJ-03.`
* **Mudanças de escopo:** `As tarefas PRJ-04 e PRJ-05 foram identificadas na análise de coesão e acoplamento e registradas no backlog para as Sprints 5 e 6.`

## 6. GitHub, documentação e rastreabilidade

| Tipo de evidência | Link | O que comprova |
|---|---|---|
| Issues | `https://github.com/liviafgs/TrabalhoPratico-eng-software/issues` | Issues técnicas PRJ-01 a PRJ-05 e seus critérios de aceitação. |
| Documento de decisões | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/projeto/decisoes-de-projeto.md` | Decisões de projeto, análise e melhorias. |
| Modelagem | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/modelaem/modelagem.md` | Correspondência atualizada entre modelo e código. |
| Backlog | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/testes/backlog-produto.mb` | Tarefas técnicas e organização da sprint. |
| Pull Request | `[link do Pull Request]` | Revisão e integração das alterações. |
| Commits | `[hash do commit anterior]` (anterior) e `[hash do commit da refatoração]` (posterior); histórico: `https://github.com/liviafgs/TrabalhoPratico-eng-software/commits/main/` | Estado anterior e posterior da refatoração. |
| Teste | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/BackEnd/src/test/java/conexaosolidaria/service/OfertaDisponibilidadeServiceTest.java` e `[print do resultado do mvn test]` | Execução dos cenários de disponibilidade e de autorização. |

### Rastreabilidade resumida

| Requisito | Issue | Decisão | Código | Teste/evidência |
|---|---|---|---|---|
| `RF-11`, `RN-03`, `RN-04`, `RNF-04` | `#66` (PRJ-01) | DP-01 | `DisponibilidadeOfertaService.java`, `OfertaService.java`, `SolicitacaoOfertaService.java`, `OfertaController.java` | `OfertaDisponibilidadeServiceTest` |
| `RF-06`, `RNF-02` | `#67` (PRJ-02) | DP-02 | `AccessControl.java`, `SolicitacaoOfertaService.java` | Teste de cancelamento por usuário não autorizado e por administrador |
| `RNF-10` | `#68` (PRJ-03) | Correção estrutural | Cinco arquivos com `package` corrigido (seção 4) | Compilação do backend |
| `RNF-02`, `RNF-10` | `#69` (PRJ-04) | DP-03 | Seis controllers que ainda verificam permissões | Planejado para a Sprint 5 |

## 7. Revisão do incremento

* **O que foi demonstrado:** `Foram apresentadas a decomposição da solução, as decisões DP-01 a DP-03, a análise de coesão e acoplamento e a refatoração de disponibilidade da oferta e de autorização por propriedade, com o estado anterior e posterior nos commits.`
* **Critérios atendidos:** `Decomposição, três decisões registradas no formato exigido, análise em partes concretas do código, melhorias identificadas, duas decisões implementadas com evidência e artefatos atualizados.`
* **Itens não concluídos:** `Migração da verificação de permissões dos controllers para os serviços (DP-03) e separação de rotas e composição em ApplicationConfig.`
* **Motivo das pendências:** `As mudanças envolvem vários controllers e serviços e foram separadas para não misturar muitas alterações nesta entrega; estão registradas como PRJ-04 e PRJ-05.`
* **Feedback recebido e ajustes:** `[preencher após a revisão da sprint]`

## 8. Retrospectiva e próximo sprint

* **Funcionou bem:** `A análise de coesão e acoplamento apontou problemas concretos no código existente: regra de disponibilidade repetida, autorização espalhada entre serviços e controllers e erro de package que impedia a compilação.`
* **Precisa melhorar:** `Compilar e executar os testes antes de integrar alterações, já que o erro de package só foi percebido nesta sprint.`
* **Ação concreta para o próximo sprint:** `Executar mvn test antes de abrir cada Pull Request, como exige o guia de desenvolvimento, e implementar PRJ-04.`

## 9. O que não será considerado suficiente

* Copiar definições teóricas de coesão, acoplamento ou SOLID.
* Citar princípios sem mostrar onde foram aplicados.
* Apresentar apenas uma lista de tecnologias.
* Antecipar um diagrama arquitetural sem analisar decisões internas.

## 10. Links enviados no UFLA Virtual

* **Tag `sprint-04`:** `https://github.com/liviafgs/TrabalhoPratico-eng-software/releases/tag/sprint-04`
* **Este arquivo na branch `main`:** `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/sprints/sprint-04.md`
* **Observação adicional:** `A Sprint 4 organiza internamente a solução, com decisões de projeto implementadas e documentadas, e deixa registradas as melhorias previstas para as próximas sprints.`
