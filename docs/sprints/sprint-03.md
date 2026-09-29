# Sprint 3 — Modelagem do sistema e rastreabilidade dos requisitos

* **Data de entrega:** 28/09/2026
* **Pontuação:** 2,5 pontos
* **Tag obrigatória:** `sprint-03`
* **Responsável por conferir este arquivo:** `Equipe`

## 1. Pergunta que esta sprint deve responder

**Como a estrutura e os principais fluxos do sistema são representados e como os requisitos se relacionam com os modelos e com o código existente?**

## 2. Objetivo e resultado da sprint

**Objetivo planejado:**

Realizar a modelagem do sistema Conexão Solidária, representando seus principais fluxos e sua estrutura, além de estabelecer a relação entre os requisitos definidos nas etapas anteriores, os elementos dos modelos e a implementação existente no projeto.

**Resultado esperado:**

Ao final da sprint, o repositório deverá possuir modelos comportamentais e estruturais versionados, descrições das decisões de modelagem, vínculos entre requisitos e elementos dos modelos e uma relação explícita entre os modelos e o código existente. Também deverá ser registrada qualquer alteração necessária no backlog a partir da modelagem.

## 3. Checklist do artefato central — 0,75 ponto

**Entrega esperada:** `modelaem/modelagem.md`, contendo ao menos um modelo comportamental e um modelo estrutural, suas descrições e o vínculo com os requisitos.

* [ ] Modelos legíveis e versionados no repositório.
* [ ] Descrição textual da finalidade e das decisões de cada modelo.
* [ ] Requisitos ligados aos elementos dos modelos.
* [ ] Backlog e requisitos revisados quando a modelagem revelar mudanças.
* [ ] Elementos modelados relacionados ao código existente.

### Links dos artefatos

| Artefato criado/atualizado       | Link                                                                                                | O que deve comprovar                                                                                                             |
| -------------------------------- | --------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------- |
| `modelaem/modelagem.md`          | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/modelaem/modelagem.md`          | Modelagem comportamental e estrutural, relação entre requisitos e modelos, correspondência com o código e decisões de modelagem. |
| `docs/requisitos/requisitos.md`  | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/requisitos/requisitos.md`  | Requisitos que serão relacionados aos elementos modelados.                                                                       |
| `docs/testes/backlog-produto.mb` | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/testes/backlog-produto.mb` | Refinamento do backlog e registro das tarefas da Sprint 3.                                                                       |

## 4. Incremento da aplicação web — 0,75 ponto

**Incremento mínimo esperado:** Evolução de um fluxo funcional ou protótipo com base na modelagem, mantendo coerência entre o comportamento representado, a estrutura do sistema e o código existente.

### O que será implementado ou evoluído

A Sprint 3 utilizará a estrutura já existente do projeto como base para modelar e relacionar os principais elementos do Conexão Solidária.

O trabalho será organizado em três frentes:

* **Modelagem comportamental:** representar os principais fluxos do sistema por meio de diagramas de sequência, incluindo cadastro de usuário, cadastro de oferta e solicitação de alimento.
* **Modelagem estrutural:** representar as principais entidades e relacionamentos do sistema, incluindo estabelecimento, instituição, oferta, solicitação, demanda e alimento.
* **Rastreabilidade:** relacionar os requisitos funcionais aos elementos dos modelos e identificar a correspondência entre os elementos modelados e os arquivos existentes no `front-end` e no `BackEnd`.

Após a modelagem, um fluxo funcional será selecionado para evolução com base nos comportamentos representados nos modelos.

### Como executar e verificar

```bash
git clone https://github.com/liviafgs/TrabalhoPratico-eng-software.git
cd TrabalhoPratico-eng-software
```

**Repositório:** https://github.com/liviafgs/TrabalhoPratico-eng-software

| Requisito/Issue                                | Código ou artefato           | Evidência esperada                                                            |
| ---------------------------------------------- | ---------------------------- | ----------------------------------------------------------------------------- |
| `#20` / modelagem comportamental               | `modelaem/modelagem.md`      | Diagramas de sequência relacionados aos requisitos correspondentes.           |
| `#22` / modelagem estrutural e rastreabilidade | `modelaem/modelagem.md`      | Modelo estrutural, relação requisito → modelo e correspondência com o código. |
| `#21` / evolução do fluxo funcional            | `front-end/` e/ou `BackEnd/` | Evidência de execução do fluxo evoluído de acordo com a modelagem.            |

### Pendências herdadas da Sprint 2

As Issues `#18` e `#19` permanecem como pendências da Sprint 2 enquanto seus critérios de aceitação não forem confirmados no GitHub Project. Elas não devem ser marcadas como concluídas apenas pela existência de código no repositório.

## 5. Sprint Backlog

| Issue | Descrição                                                                                 | Responsável | Critério de aceitação/conclusão                                                                                               | Situação |
| ----- | ----------------------------------------------------------------------------------------- | ----------- | ----------------------------------------------------------------------------------------------------------------------------- | -------- |
| `#20` | Elaborar a modelagem comportamental dos principais fluxos do sistema.                     | `Equipe`    | Os diagramas representam os principais fluxos e estão relacionados aos requisitos correspondentes.                            | Pendente |
| `#22` | Elaborar a modelagem estrutural e relacionar os requisitos aos modelos.                   | `Equipe`    | O modelo estrutural representa os principais elementos do sistema e os requisitos possuem vínculo com os elementos modelados. | Pendente |
| `#21` | Evoluir um fluxo funcional da aplicação utilizando como referência os modelos elaborados. | `Equipe`    | O fluxo implementado corresponde aos comportamentos representados nos modelos e possui evidência de execução.                 | Pendente |

> **Organização das Issues:** a Issue `#22`, que atualmente possui o mesmo título da `#21`, deve ser editada no GitHub para representar a modelagem estrutural e o vínculo entre requisitos e modelos. Assim, a Sprint 3 fica organizada em três atividades complementares: modelagem comportamental (`#20`), modelagem estrutural e rastreabilidade (`#22`) e evolução funcional (`#21`).

### Acompanhamento

* **GitHub Project:** `https://github.com/users/liviafgs/projects/3`
* **Issues da Sprint 3:** `https://github.com/liviafgs/TrabalhoPratico-eng-software/issues`
* **Reuniões/decisões:** `[adicionar links para docs/reunioes/ quando houver]`
* **Impedimentos:** `Nenhum impedimento registrado até o momento.`
* **Mudanças de escopo:** `A Sprint 3 foi organizada em torno da modelagem comportamental, modelagem estrutural, rastreabilidade entre requisitos e modelos e evolução de um fluxo funcional.`

## 6. GitHub, documentação e rastreabilidade — 0,50 ponto

| Tipo de evidência       | Link                                                                                                | O que comprova                                                                                |
| ----------------------- | --------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------- |
| Issues                  | `https://github.com/liviafgs/TrabalhoPratico-eng-software/issues`                                   | Organização das atividades da Sprint 3 e critérios de aceitação.                              |
| Documento de requisitos | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/requisitos/requisitos.md`  | Fonte dos requisitos relacionados aos modelos.                                                |
| Modelagem               | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/modelaem/modelagem.md`          | Modelos comportamentais e estruturais e a rastreabilidade entre requisitos, modelos e código. |
| Backlog                 | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/testes/backlog-produto.mb` | Tarefas refinadas e organização da Sprint 3.                                                  |
| Pull Request            | `Será preenchido após a abertura do PR.`                                                            | Registro da revisão e integração das alterações.                                              |
| Commit                  | `Será preenchido após o commit.`                                                                    | Registro da alteração realizada na branch.                                                    |
| Teste/captura/relatório | `Será preenchido após a execução.`                                                                  | Evidência do fluxo funcional evoluído.                                                        |

### Rastreabilidade resumida

| Requisito               | Issue         | Artefato/modelo/decisão                                         | Código                                                                                             | Teste/evidência                                             |
| ----------------------- | ------------- | --------------------------------------------------------------- | -------------------------------------------------------------------------------------------------- | ----------------------------------------------------------- |
| `RF-01 a RF-05`         | `#20` e `#22` | Cadastro de usuário / modelo estrutural                         | `BackEnd/src/main/java/conexaosolidaria/controller/UsuarioController.java` e serviços relacionados | Evidência a ser registrada na revisão da sprint.            |
| `RF-07 e RF-09`         | `#20` e `#22` | Cadastro de oferta / modelo estrutural                          | `OfertaController.java`, `OfertaService.java` e `OfertaRepository.java`                            | Evidência a ser registrada na revisão da sprint.            |
| `RF-08 e RF-10 a RF-13` | `#20` e `#22` | Solicitação de alimento / `SOLICITACAO`                         | Fluxo modelado; implementação correspondente ainda não concluída                                   | Evidência a ser registrada quando o fluxo for implementado. |
| `RF-16 a RF-20`         | `#22`         | `DEMANDA` no modelo estrutural                                  | `DemandaController.java`, `DemandaService.java` e `DemandaRepository.java`                         | Evidência a ser registrada na revisão da sprint.            |
| `RF-21 a RF-23`         | `#22`         | Relações `OFERTA`, `DEMANDA`, `ALIMENTO` e operações do sistema | Correspondências existentes e pendências registradas no artefato de modelagem                      | Evidência a ser registrada após a revisão.                  |

## 7. Revisão do incremento

* **O que foi demonstrado:** `Será preenchido após a revisão da Sprint 3.`
* **Critérios atendidos:** `Será preenchido após a verificação dos modelos, vínculos e fluxo funcional.`
* **Itens não concluídos:** `Será preenchido ao final da sprint.`
* **Motivo das pendências:** `Será preenchido ao final da sprint, caso existam pendências.`
* **Feedback recebido e ajustes:** `Será preenchido após a revisão do incremento.`

## 8. Retrospectiva e próxima sprint

* **Funcionou bem:** `Será preenchido após a retrospectiva da Sprint 3.`
* **Precisa melhorar:** `Será preenchido após a retrospectiva da Sprint 3.`
* **Ação concreta para a próxima sprint:** `Será definida após a revisão da Sprint 3 e utilizada para alimentar o backlog da Sprint 4.`

## 9. O que não será considerado suficiente

* Diagramas sem explicação da finalidade e das decisões tomadas.
* Imagens externas sem versão correspondente no repositório.
* Modelo genérico que não corresponde aos requisitos ou ao código.
* Relações entre requisitos e modelos sem identificação clara dos elementos correspondentes.
* Evolução de fluxo funcional sem evidência de execução.

## 10. Links enviados no UFLA Virtual

* **Tag `sprint-03`:** `Será preenchido após a criação da tag.`
* **Este arquivo na tag:** `Será preenchido após a criação da tag.`
* **Observação adicional:** `A Sprint 3 concentra a modelagem comportamental e estrutural do Conexão Solidária, o vínculo entre requisitos e modelos e a evolução de um fluxo funcional com base na modelagem.`
