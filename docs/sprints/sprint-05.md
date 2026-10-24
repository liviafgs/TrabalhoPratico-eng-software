# Sprint 5 — Padrões de projeto aplicados ao código

- **Data de entrega:** 26/10/2026
- **Pontuação:** 2,5 pontos
- **Tag obrigatória:** `sprint-05`
- **Responsável por conferir este arquivo:** Lívia Fagundes

## 1. Pergunta que esta sprint deve responder

**Qual problema concreto e recorrente justifica a aplicação de um padrão?**

No Conexão Solidária, as verificações de autorização precisam ser realizadas de forma consistente para evitar que usuários acessem funcionalidades ou recursos sem a permissão necessária. Quando essas verificações ficam distribuídas em diferentes partes do backend, há maior possibilidade de duplicação de código e dificuldade de manutenção.

Para lidar com esse problema, foi adotado o padrão de projeto **Facade (Fachada)**, por meio da classe `AccessControl`, com o objetivo de centralizar o acesso às verificações de autorização e simplificar sua utilização pelos componentes que precisam dessas regras.

## 2. Objetivo e resultado da sprint

**Objetivo planejado:**

Aplicar e documentar um padrão de projeto pertinente ao Conexão Solidária, relacionando o problema identificado à implementação existente, descrevendo os participantes do padrão, as alternativas consideradas, os benefícios e os trade-offs da solução.

**Resultado efetivamente alcançado:**

O desenvolvimento da aplicação foi concluído, contemplando o front-end e o backend. A solução utiliza a classe `AccessControl` para centralizar as verificações de autorização, conforme a abordagem Facade documentada no projeto.

A entrega desta sprint consiste em registrar a aplicação do padrão, relacionar os arquivos envolvidos e reunir as evidências de código, execução, testes e rastreabilidade no GitHub.

## 3. Checklist do artefato central — 0,75 ponto

**Entrega esperada:** `docs/padroes/padroes-de-projeto.md`

- [x] Problema concreto descrito antes da apresentação do padrão.
- [x] Padrão Facade selecionado e contextualizado no projeto.
- [ ] Participantes do padrão relacionados aos arquivos e métodos reais.
- [x] Alternativas e justificativas registradas na documentação.
- [x] Benefícios e trade-offs explicados no contexto do projeto.
- [ ] Evidências de testes ou demonstração vinculadas ao documento.

### Links dos artefatos

| Artefato criado/atualizado | Link | O que mudou |
|---|---|---|
| Documentação dos padrões | [padroes-de-projeto.md](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/padroes/padroes-de-projeto.md) | Documentação do problema, da escolha do Facade, das alternativas e dos trade-offs. |
| Backend | [Pasta BackEnd](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd) | Código relacionado às regras de negócio e às verificações de autorização. |
| Front-end | [Pasta front-end](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/front-end) | Código da interface web e dos fluxos de interação da aplicação. |

## 4. Incremento da aplicação web — 0,75 ponto

### O que foi implementado ou evoluído

O incremento contempla a aplicação web do Conexão Solidária, composta pelo front-end e pelo backend.

No backend, o padrão Facade foi utilizado para centralizar as verificações de autorização por meio da classe `AccessControl`, facilitando a organização e a manutenção dessas regras.

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
| Requisito relacionado à autorização — confirmar identificador RF | [Backend](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd) | Inserir link do teste ou demonstração real. |
| Requisitos relacionados à interface — confirmar identificadores RF | [Front-end](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/front-end) | Inserir captura de tela ou demonstração da aplicação em funcionamento. |
| Integração entre interface e backend | [Repositório do projeto](https://github.com/liviafgs/TrabalhoPratico-eng-software) | Registrar evidência de um fluxo funcional completo. |

## 5. Scrum e gestão do trabalho — 0,50 ponto

### Sprint Backlog

| Issue | Descrição | Responsável | Critério de aceitação/conclusão | Situação |
|---|---|---|---|---|
| Issue da implementação do padrão | Aplicação do Facade para centralização das verificações de autorização. | Equipe de backend | Código integrado e comportamento verificável. | Implementação informada como concluída; confirmar Issue. |
| Issue de integração da aplicação | Verificação dos fluxos entre front-end e backend. | Equipe de desenvolvimento | Funcionalidades integradas e fluxos principais verificáveis. | Código informado como concluído; confirmar evidências. |
| Issue de documentação | Documentação do padrão aplicado e de sua relação com o código. | Lívia Fagundes dos Santos | Documento com contexto, participantes, alternativas e evidências. | Conferir versão final. |

### Acompanhamento

- **GitHub Project:** [Projetos do repositório](https://github.com/liviafgs/TrabalhoPratico-eng-software/projects)
- **Issues:** [Issues do projeto](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues)
- **Reuniões e decisões:** inserir links das atas ou registros existentes em `docs/reunioes/`.
- **Impedimentos:** nenhum impedimento informado; confirmar com a equipe.
- **Mudanças de escopo:** nenhuma mudança informada; confirmar com a equipe.

## 6. GitHub, documentação e rastreabilidade — 0,50 ponto

| Tipo de evidência | Link | O que comprova |
|---|---|---|
| Issue | [Issues do projeto](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues) | Registro das tarefas e dos critérios de conclusão. Inserir a Issue específica. |
| Pull Request | Inserir link do PR correspondente. | Revisão e integração das alterações. |
| Commit | Inserir link do commit correspondente. | Registro das alterações no código e na documentação. |
| Código do backend | [BackEnd](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd) | Localização da implementação relacionada à autorização. |
| Código do front-end | [front-end](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/front-end) | Localização da interface e dos fluxos da aplicação. |
| Documentação do padrão | [padroes-de-projeto.md](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/padroes/padroes-de-projeto.md) | Justificativa e descrição do padrão aplicado. |
| Testes ou demonstração | Inserir link da evidência executada. | Verificação do comportamento esperado da aplicação. |

### Rastreabilidade resumida

| Requisito | Issue | Artefato/modelo/decisão | Código | Teste/evidência |
|---|---|---|---|---|
| Requisito de autorização — confirmar RF | Inserir Issue específica | [Documentação do padrão](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/padroes/padroes-de-projeto.md) | [Backend](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd) | Inserir teste real. |
| Requisito de interface — confirmar RF | Inserir Issue específica | Documentação dos requisitos | [Front-end](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/front-end) | Inserir captura ou demonstração real. |
| Integração da aplicação | Inserir Issue específica | Decisão de integração entre as camadas | [Repositório](https://github.com/liviafgs/TrabalhoPratico-eng-software) | Inserir evidência de fluxo completo. |

## 7. Revisão do incremento

- **O que foi demonstrado:** aplicação web composta pelo front-end e backend, com documentação do padrão Facade associado à centralização das verificações de autorização.
- **Critérios atendidos:** desenvolvimento do código informado como concluído; padrão selecionado e documentado; alternativas, benefícios e trade-offs registrados.
- **Itens não concluídos:** preenchimento dos identificadores exatos das Issues e dos requisitos, inclusão dos links específicos de PRs e commits e registro das evidências de execução, caso ainda não estejam documentados.
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

- **Tag `sprint-05`:** inserir o link da tag após sua criação.
- **Este arquivo na tag:** inserir o link deste documento na versão marcada.
- **Observação adicional:** o código do front-end e do backend está concluído, conforme informado. A documentação final deve relacionar a implementação às evidências verificáveis da sprint.
