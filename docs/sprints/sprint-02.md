# Sprint 2 — Requisitos e escopo validável da solução

- **Data de entrega:** 14/09/2026
- **Pontuação:** 2,5 pontos
- **Tag obrigatória:** `sprint-02`
- **Responsável por conferir este arquivo:** `Lívia Fagundes`

## 1. Pergunta que esta sprint deve responder

**O que o sistema deverá fazer e quais condições verificáveis deverá atender?**

Nesta sprint, o grupo irá detalhar o funcionamento do Conexão Solidária, partindo da visão definida na Sprint 1. Serão identificados os principais usuários do sistema e as funcionalidades necessárias para atender às suas necessidades.

Também serão definidos os requisitos funcionais e não funcionais, as histórias de usuário e os critérios de aceitação, tornando as funcionalidades mais claras e verificáveis. Além disso, será estabelecido o escopo inicial da solução, definindo o que será desenvolvido e o que ficará fora do projeto neste momento.

## 2. Objetivo e resultado da sprint

**Objetivo planejado:** `Detalhar os requisitos funcionais e não funcionais do Conexão Solidária, identificar os principais atores do sistema, definir histórias de usuário e critérios de aceitação, estabelecer o escopo incluído e excluído e refinar o backlog para orientar o desenvolvimento da aplicação.`

**Resultado efetivamente alcançado:** `Foi elaborado o documento central de requisitos da aplicação, contendo a identificação de quatro perfis de usuários, 23 requisitos funcionais, 11 requisitos não funcionais, 13 regras de negócio e 6 histórias de usuário com critérios de aceitação. Também foi definido o que ficará fora do escopo da primeira versão da aplicação. O documento passou a servir como referência para a organização das próximas etapas de desenvolvimento e testes.`

## 3. Checklist do artefato central — 0,75 ponto

**Entrega esperada:** `docs/requisitos/requisitos.md`, histórias/casos, critérios de aceitação, escopo excluído e backlog refinado.

- [x] Atores/perfis identificados.
- [x] Requisitos funcionais com IDs e prioridade.
- [x] Requisitos não funcionais verificáveis.
- [x] Histórias/casos vinculados aos requisitos.
- [x] Critérios de aceitação nas Issues ou em links diretos.

### Links dos artefatos

| Artefato criado/atualizado | Link na tag da sprint | O que mudou |
|---|---|---|
| `docs/requisitos/requisitos.md` | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/requisitos/requisitos.md` | `Documento central da Sprint 2, reunindo atores, requisitos funcionais e não funcionais, regras de negócio, histórias de usuário, critérios de aceitação e definição de escopo.` |
| `front-end/` | `https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/front-end` | `Desenvolvimento da estrutura e das funcionalidades da camada de apresentação da aplicação.` |
| `BackEnd/` | `https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd/src/main/java` | `Desenvolvimento da estrutura responsável pelo processamento e funcionamento da aplicação.` |
| `GitHub Project` | `https://github.com/users/liviafgs/projects/3` | `Será utilizado para acompanhar as tarefas da Sprint 2.` |

## 4. Incremento da aplicação web — 0,75 ponto

**Incremento mínimo esperado:** Implementação de pelo menos um fluxo prioritário ou protótipo navegável ligado a requisitos e critérios de aceitação.

### O que foi implementado ou evoluído

Durante a Sprint 2, foram desenvolvidas partes do front-end e do back-end da aplicação de forma integrada, avançando na construção da estrutura técnica do Conexão Solidária.

No front-end, foi desenvolvida a estrutura inicial das páginas e componentes da aplicação, utilizando HTML, CSS e JavaScript, estabelecendo a base visual e de interação que será utilizada nos fluxos definidos nos requisitos.

No back-end, foram desenvolvidas estruturas relacionadas ao funcionamento da aplicação e ao gerenciamento dos dados, incluindo a preparação da comunicação com o banco de dados e funcionalidades necessárias para dar suporte aos fluxos definidos durante a especificação dos requisitos.

Dessa forma, a Sprint 2 não ficou restrita à documentação: houve evolução tanto na camada de apresentação quanto na camada de processamento da aplicação. O desenvolvimento foi realizado considerando os requisitos funcionais, requisitos não funcionais e histórias de usuário definidos no documento de requisitos.

### Como executar e verificar

```bash
git clone https://github.com/liviafgs/TrabalhoPratico-eng-software.git
cd TrabalhoPratico-eng-software
```

| Requisito/Issue | Código ou protótipo | Evidência de execução |
|---|---|---|
| `RF-01 a RF-23` | `front-end/ e BackEnd/` | `Funcionalidades e estruturas desenvolvidas de acordo com os requisitos definidos` |
| `US-01 a US-06` | `docs/requisitos/requisitos.md` | `Conferência das histórias de usuário e respectivos critérios de aceitação.` |
| `RNF-01 a RNF-11` | `front-end/ e BackEnd/` | `Verificação das características e condições definidas para a aplicação.` |
| `Requisitos e critérios de aceitação` | `docs/requisitos/requisitos.md` | `Conferência dos requisitos, prioridades, histórias e critérios definidos para orientar a implementação.` |

## 5. Scrum e gestão do trabalho — 0,50 ponto

### Sprint Backlog

| Issue | Descrição | Responsável | Critério de aceitação/conclusão | Situação |
|---|---|---|---|---|
| `#5` | `Levantar requisitos funcionais` | `@raissafernandesdesouza` | `Requisitos funcionais identificados e numerados` | Concluída |
| `#6` | `Levantar requisitos não funcionais` | `@raissafernandesdesouza` | `Requisitos não funcionais definidos e verificáveis` | Concluída |
| `#7` | `Criar histórias de usuário/casos` | `@raissafernadesdesouza @liiafag` | `Histórias vinculadas aos requisitos` | Concluído |
| `#8` | `Definir critério de aceitação das funcionalidades` | `Todos` | `Critérios objetivos definidos para as funcionalidades` | Concluído |
| `#9` | `Refinar backlog do produto` | `@leticia-Cristhinie` | `Itens detalhados e priorizados` | Concluído |
| `#10` | `Definir funcionalidades da aplicação web` | `@raissafernandesdesouza @liviafag` | `Escopo definido` | Concluído |
| `#11` | `Criar Documentação da sprint-02` | `@liviafag` | `Arquivo da sprint preenchido e versionado` | Concluído |
| `#12` | `Ata de Reunião` | `@rawanymendonca` | `Arquivo da sprint preenchido e versionado` | Concluído |

### Acompanhamento

- **GitHub Project:** `https://github.com/users/liviafgs/projects/3`
- **Reuniões/decisões:** `https://meet.google.com/zve-vpem-xer`
- **Impedimentos:** `Nenhum impedimento significativo registrado.`
- **Mudanças de escopo:** `O escopo foi refinado a partir da definição dos requisitos e da identificação das funcionalidades que serão contempladas na aplicação.`

## 6. GitHub, documentação e rastreabilidade — 0,50 ponto

| Tipo de evidência       | Link                                                                                                                                                                        | O que comprova                                                                                                                                         |
| ----------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Issues                  | `https://github.com/liviafgs/TrabalhoPratico-eng-software/issues`                                                                                                           | Levantamento, definição e organização dos requisitos e atividades da sprint.                                                                           |
| Documento de requisitos | `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/requisitos/requisitos.md`                                                                          | Registro dos atores, 23 requisitos funcionais, 11 requisitos não funcionais, regras de negócio, histórias de usuário, critérios de aceitação e escopo. |
| Pull Requests           | `https://github.com/liviafgs/TrabalhoPratico-eng-software/pulls`                                                                                                            | Registro das alterações realizadas no desenvolvimento da aplicação.                                                                                    |
| Commits                 | `https://github.com/liviafgs/TrabalhoPratico-eng-software/commits/main/`                                                                                                    | Histórico das alterações realizadas no projeto.                                                                                                        |
| Código                  | `https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/front-end` / `https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd/src/main/java` | Desenvolvimento das camadas de front-end e back-end da aplicação.                                                                                      |

### Rastreabilidade resumida

| Requisito                              | Issue              | Artefato/modelo/decisão         | Código                           | Teste/evidência                  |
| -------------------------------------- | ------------------ | ------------------------------- | -------------------------------- | -------------------------------- |
| Requisitos funcionais e não funcionais | Issues da Sprint 2 | `docs/requisitos/requisitos.md` | Front-end e back-end             | Critérios de aceitação definidos |
| Histórias de usuário                   | Issues da Sprint 2 | `docs/requisitos/requisitos.md` | Funcionalidades relacionadas     | Critérios de aceitação           |
| Regras de negócio                      | Issues da Sprint 2 | `docs/requisitos/requisitos.md` | A implementar conforme os fluxos | Regras verificáveis              |
| Escopo da aplicação                    | Issues da Sprint 2 | `docs/requisitos/requisitos.md` | Front-end e back-end             | Verificação conforme requisitos  |


## 7. Revisão do incremento

- **O que foi demonstrado:** `Foram apresentados os requisitos definidos para o sistema e a evolução da aplicação por meio do desenvolvimento das estruturas de front-end e back-end.`
- **Critérios atendidos:** `Foram identificados os principais usuários, definidos os requisitos funcionais e não funcionais, elaboradas histórias de usuário e critérios de aceitação e iniciado o desenvolvimento das duas principais camadas da aplicação.`
- **Itens não concluídos:** `Algumas tarefas de organização permaneceram pendentes no GitHub.`
- **Motivo das pendências:** `Parte das atividades foi desenvolvida paralelamente à implementação, sendo necessário continuar a organização das tarefas nas próximas etapas do projeto.`
- **Feedback recebido e ajustes:** `A partir da revisão do trabalho, os requisitos e a estrutura da aplicação foram utilizados como referência para organizar as próximas atividades de desenvolvimento.`

## 8. Retrospectiva e próxima sprint

- **Funcionou bem:** `A definição dos requisitos junto com o desenvolvimento do front-end e do back-end permitiu que a equipe tivesse uma visão mais clara de como as funcionalidades deveriam ser estruturadas.`
- **Precisa melhorar:** `É necessário manter uma atualização mais constante do GitHub Project e das Issues, garantindo que o status das atividades acompanhe o desenvolvimento realizado pela equipe.`
- **Ação concreta para a próxima sprint:** `Continuar o desenvolvimento das funcionalidades, atualizar o backlog de acordo com o que já foi implementado e manter a relação entre requisitos, Issues, código e evidências de teste.`

## 9. O que não será considerado suficiente

- Repetir a descrição do problema da Sprint 1.
- Listar funcionalidades sem identificadores ou critérios.
- Apresentar telas sem relacioná-las a requisitos.

## 10. Links enviados no UFLA Virtual

- **Tag `sprint-02`:** `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/sprints/sprint-02.md`
- **Este arquivo na tag:** `https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/docs/sprints`
- **Observação adicional:** `Durante a Sprint 2, além da especificação dos requisitos, foram desenvolvidas partes do front-end e do back-end da aplicação, utilizando os requisitos definidos como base para a implementação.`



