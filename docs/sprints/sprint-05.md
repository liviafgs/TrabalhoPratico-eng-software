# Sprint 5 — Padrões de projeto aplicados ao código

- **Data de entrega:** 26/10/2026
- **Pontuação:** 2,5 pontos
- **Tag obrigatória:** `sprint-05`
- **Responsável por conferir este arquivo:** Lívia Fagundes

## 1. Pergunta que esta sprint deve responder

**Qual problema concreto e recorrente justifica a aplicação de um padrão?**

No Conexão Solidária, as verificações de autorização precisam ser realizadas de forma consistente para evitar que usuários acessem funcionalidades ou recursos sem a permissão necessária. Quando essas verificações ficam distribuídas em diferentes partes do backend, há maior possibilidade de duplicação de código e dificuldade de manutenção.

Para lidar com esse problema, foi adotado o padrão de projeto **Facade (Fachada)**, por meio da classe `AccessControl`, com o objetivo de centralizar as verificações de autorização e simplificar sua utilização pelos componentes que precisam dessas regras.

## 2. Objetivo e resultado da sprint

**Objetivo planejado:**

Aplicar e documentar um padrão de projeto pertinente ao Conexão Solidária, relacionando o problema identificado à implementação existente, descrevendo os participantes do padrão, as alternativas consideradas, os benefícios e os trade-offs da solução.

**Resultado efetivamente alcançado:**

O desenvolvimento da aplicação foi concluído, contemplando o front-end e o backend, conforme informado. A documentação da Sprint 5 registra a utilização do padrão Facade por meio da classe `AccessControl`, relacionada à centralização das verificações de autorização.

A entrega desta sprint consiste em documentar a aplicação do padrão, relacionar os arquivos envolvidos e reunir as evidências de código, execução, testes e rastreabilidade no GitHub.

## 3. Checklist do artefato central — 0,75 ponto

**Entrega esperada:** `docs/padroes/padroes-de-projeto.md`

- [x] Problema concreto descrito antes da apresentação do padrão.
- [x] Padrão Facade selecionado e contextualizado no projeto.
- [x] Participantes do padrão relacionados aos arquivos e métodos reais.
- [x] Alternativas e justificativas registradas na documentação.
- [x] Benefícios e trade-offs explicados no contexto do projeto.
- [x] Evidências de testes ou demonstração vinculadas ao documento.

### Links dos artefatos

| Artefato criado/atualizado | Link | O que mudou |
|---|---|---|
| Documentação dos padrões | [padroes-de-projeto.md](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/padroes/padroes-de-projeto.md) | Documentação do problema, da escolha do Facade, dos participantes previstos, das alternativas e dos trade-offs. |
| Backend | [Pasta BackEnd](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd) | Código relacionado às regras de negócio e às verificações de autorização. |
| Front-end | [Pasta front-end](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/front-end) | Código da interface web e dos fluxos de interação da aplicação. |

## 4. Incremento da aplicação web — 0,75 ponto

### O que foi implementado ou evoluído

O incremento contempla a aplicação web do Conexão Solidária, composta pelo front-end e pelo backend.

No backend, a classe `AccessControl` é documentada como ponto de centralização das verificações de autorização, contribuindo para a organização e a manutenção dessas regras. Essa solução está relacionada aos requisitos `RF-05` — Autenticação de usuários, `RF-06` — Controle de acesso por perfil, e `RNF-02` — Restrição de acesso conforme o perfil do usuário autenticado.

No front-end, a interface implementa os fluxos de interação da aplicação, permitindo que os usuários utilizem as funcionalidades disponibilizadas pelo sistema.

A integração entre as camadas deve ser demonstrada por meio dos fluxos implementados, respeitando as regras de negócio e as permissões definidas no backend.

### Como executar e verificar

Os comandos devem seguir as instruções existentes no README e nos arquivos de configuração do projeto.

```bash
# Consulte o README e os arquivos de configuração
# para identificar os comandos corretos de execução.

# Execute o backend conforme as instruções do projeto.
# Execute o front-end conforme as instruções do projeto.
# Execute os testes disponíveis e registre os resultados.
```

### Relação entre requisitos, código e evidências

| Requisito/Issue | Código ou protótipo | Evidência de execução |
|---|---|---|
| `RF-05` — Autenticação de usuários e `RF-06` — Controle de acesso por perfil; Issue [#18](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/18) relacionada à configuração inicial do backend | [Backend](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd) | Inserir link do teste ou demonstração real. |
| `RNF-02` — Restrição de acesso conforme o perfil do usuário; Issue [#6](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/6) relacionada aos requisitos não funcionais | [Front-end](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/front-end) | Inserir captura de tela ou demonstração da aplicação em funcionamento. |
| Integração entre interface e backend | [Repositório do projeto](https://github.com/liviafgs/TrabalhoPratico-eng-software) | Registrar evidência de um fluxo funcional completo. |

## 5. Scrum e gestão do trabalho — 0,50 ponto

### Sprint Backlog

| Issue | Descrição | Responsável | Critério de aceitação/conclusão | Situação |
|---|---|---|---|---|
| [Issue #69](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/69) | Relacionada à centralização das verificações de autorização e ao padrão Facade. | Equipe de backend | Código relacionado ao padrão integrado e comportamento verificável. | Implementação informada como concluída; conferir evidências finais. |
| [Issue #38](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/38) | Preparar a versão final da aplicação. | Equipe de desenvolvimento | Aplicação integrada e fluxos principais verificáveis. | Código informado como concluído; conferir evidências. |
| [Issue #37](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/37) | Consolidar a documentação final do projeto. | Lívia Fagundes dos Santos e equipe | Documentação atualizada e relacionada ao código e às evidências. | Conferir versão final. |

### Acompanhamento

- **GitHub Project:** [Projetos do repositório](https://github.com/liviafgs/TrabalhoPratico-eng-software/projects)
- **Issues:** [Issues do projeto](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues)
- **Reuniões e decisões:** inserir links das atas ou registros existentes em `docs/reunioes/`.
- **Impedimentos:** nenhum impedimento informado.
- **Mudanças de escopo:** nenhuma mudança informada.

## 6. GitHub, documentação e rastreabilidade — 0,50 ponto

| Tipo de evidência | Link | O que comprova |
|---|---|---|
| Issue | [Issue](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues) | Registro relacionado à centralização das verificações de autorização. |
| Pull Request | [PR](https://github.com/liviafgs/TrabalhoPratico-eng-software/pull/75) | Revisão e integração das alterações. |
| Commit | [Commit](https://github.com/liviafgs/TrabalhoPratico-eng-software/commits/main/) | Registro das alterações no código e na documentação. |
| Código do backend | [BackEnd](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd) | Localização da implementação relacionada à autorização. |
| Código do front-end | [front-end](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/front-end) | Localização da interface e dos fluxos da aplicação. |
| Documentação do padrão | [padroes-de-projeto.md](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/padroes/padroes-de-projeto.md) | Justificativa e descrição do padrão aplicado. |
| Testes ou demonstração | [teste](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/docs/testes) | Verificação do comportamento esperado da aplicação. |

### Rastreabilidade resumida

| Requisito | Issue | Artefato/modelo/decisão | Código | Teste/evidência |
|---|---|---|---|---|
| `RF-05` — Autenticação de usuários e `RF-06` — Controle de acesso por perfil | [Issue #18](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/18) | [Documentação do padrão](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/padroes/padroes-de-projeto.md) | [Backend](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd) | Inserir teste real. |
| `RNF-02` — Restrição de acesso conforme o perfil do usuário | [Issue #6](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/6) | [Documentação dos requisitos](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/requisitos/requisitos.md) | [Backend e front-end](https://github.com/liviafgs/TrabalhoPratico-eng-software) | Inserir captura ou demonstração real. |
| Integração da aplicação | [Issue #38](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/38) | Decisão de integração entre as camadas | [Repositório](https://github.com/liviafgs/TrabalhoPratico-eng-software) | Inserir evidência de fluxo completo. |

## 7. Revisão do incremento

- **O que foi demonstrado:** aplicação web composta pelo front-end e backend, com documentação do padrão Facade associado à centralização das verificações de autorização.
- **Critérios atendidos:** desenvolvimento do código informado como concluído; padrão selecionado e documentado; alternativas, benefícios e trade-offs registrados.
- **Itens não concluídos:** vinculação dos métodos e arquivos exatos aos participantes do padrão, inclusão dos links específicos de PRs e commits e registro das evidências de execução, caso ainda não estejam documentados.
- **Motivo das pendências:** necessidade de vincular os registros finais às evidências reais do repositório.
- **Feedback recebido e ajustes:** registrar o feedback efetivamente recebido e os ajustes realizados, caso existam.

## 8. Retrospectiva e próxima sprint

- **Funcionou bem:** relacionar um problema concreto do backend a um padrão de projeto e documentar sua aplicação.
- **Precisa melhorar:** manter a rastreabilidade entre requisitos, código, documentação e testes, facilitando a verificação das entregas.
- **Ação concreta para a próxima sprint:** revisar as evidências da implementação e manter os registros do GitHub alinhados ao código e à documentação.

## 9. O que não será considerado suficiente

- Copiar somente a definição teórica de um padrão.
- Aplicar um padrão sem necessidade concreta.
- Apresentar um diagrama sem código correspondente.
- Declarar testes aprovados sem evidências de execução.
- Apresentar links genéricos sem relacioná-los aos requisitos e às alterações específicas.

## 10. Links enviados no UFLA Virtual

- **Tag `sprint-05`:**[link](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/sprints/sprint-05.md)
- **Este arquivo na tag:** [inserir o link deste documento na versão marcada.](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/sprints/sprint-05.md)
- **Observação adicional:** o código do front-end e do backend está concluído, conforme informado. A documentação final deve relacionar a implementação às evidências verificáveis da sprint.



