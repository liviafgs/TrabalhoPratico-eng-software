# Decisões de Projeto — Sprint 4

Projeto: Conexão Solidária

Esta sprint trata de decisões internas e locais de projeto do backend (`BackEnd/src/main/java/conexaosolidaria`). A arquitetura global será formalizada na Sprint 6 e os padrões específicos, na Sprint 5.

## 1. Decomposição da solução

O backend é organizado em pacotes, cada um com uma responsabilidade principal.

| Pacote | Responsabilidade | Principais arquivos |
|---|---|---|
| `server` | Receber requisições HTTP, rotear, aplicar CORS e converter exceções em respostas JSON. | `ApplicationServer`, `RequestUtils`, `JsonResponse`, `RouteHandler` |
| `controller` | Traduzir HTTP em chamadas de serviço e o resultado em JSON (11 controllers). | `OfertaController`, `SolicitacaoOfertaController`, `DemandaController` e demais |
| `service` | Regras de negócio e casos de uso (10 serviços, mais `OfertaFiltros` e `DisponibilidadeOfertaService`). | `OfertaService`, `SolicitacaoOfertaService`, `DisponibilidadeOfertaService` |
| `repository` | Armazenamento dos dados, hoje em memória (9 repositórios). | `OfertaRepository`, `SolicitacaoOfertaRepository` e demais |
| `model` | Entidades e enumerações do domínio. | `Oferta`, `SolicitacaoOferta`, `StatusSolicitacaoOferta`, `PerfilUsuario`, `Permissao` |
| `security` | Autenticação e autorização. | `AccessControl`, `PasswordHasher`, `TokenService` |
| `dto` e `exception` | Objetos de transferência e erro de API. | `AuthenticatedUser`, `UsuarioResponse`, `ApiException` |
| `config` | Raiz de composição: cria os objetos, liga as dependências e registra as rotas. | `ApplicationConfig` |

O diretório `front-end/` (`index.html`, `script.js`, `style.css`) concentra a interface e se comunica com o backend por HTTP.

## 2. Decisões de projeto

### DP-01 — Centralizar a regra de disponibilidade da oferta

- **Contexto:** a regra "uma oferta está disponível quando não expirou e ainda tem quantidade" estava escrita em quatro lugares: no cálculo de `SolicitacaoOfertaService.quantidadeDisponivel`, na validação `SolicitacaoOfertaService.validarOfertaDisponivel`, no filtro `OfertaService.corresponde` e no método `OfertaController.ofertaToMap`. Além disso, `OfertaService` dependia de `SolicitacaoOfertaService` por meio de um construtor que aceitava `null`; nesse caso, `quantidadeDisponivel` devolvia a quantidade total da oferta, ignorando as solicitações.
- **Alternativas:**
  1. manter a regra repetida em cada classe;
  2. fazer `OfertaService` chamar `SolicitacaoOfertaService` (situação anterior);
  3. criar um serviço pequeno, dedicado à disponibilidade, usado pelos dois serviços e pelo controller.
- **Decisão:** criar `DisponibilidadeOfertaService`, com `quantidadeDisponivel(Oferta)` e `estaDisponivel(Oferta)`. `OfertaService` e `SolicitacaoOfertaService` recebem esse serviço pelo construtor, e o construtor com `null` foi removido.
- **Justificativa:** a regra passa a ter um único dono (RF-11, RN-03, RN-04 e RNF-04). `OfertaService` deixa de depender de outro serviço de caso de uso e `SolicitacaoOfertaService` deixa de calcular algo que não é sua responsabilidade.
- **Consequências:** uma alteração na regra de disponibilidade é feita em um único arquivo. Em contrapartida, há uma classe a mais e os construtores de `OfertaService` e `SolicitacaoOfertaService` mudaram, o que exigiu ajustar `ApplicationConfig` e o teste de ofertas.
- **Status:** implementada nesta sprint.

### DP-02 — Concentrar a autorização por propriedade em `AccessControl`

- **Contexto:** `SolicitacaoOfertaService` verificava, nos métodos `confirmarRetirada` e `cancelar`, se o usuário era o dono do recurso ou administrador, com o mesmo `if` repetido e usando `conexaosolidaria.model.PerfilUsuario` com nome completo. Regras de permissão estavam, assim, divididas entre `AccessControl` e o serviço.
- **Alternativas:**
  1. manter a verificação dentro de cada serviço;
  2. criar um método em `AccessControl` para "dono ou administrador".
- **Decisão:** adicionar `AccessControl.requireDonoOuAdministrador(user, idDono, mensagem)` e usá-lo nos dois métodos.
- **Justificativa:** toda decisão sobre quem pode fazer o quê fica no pacote `security` (RNF-02), e os serviços apenas declaram qual verificação precisam.
- **Consequências:** as regras de acesso ficam em um lugar só e o código dos serviços fica mais curto. Passou a existir um teste que cobre o usuário não autorizado (403) e o administrador.
- **Status:** implementada nesta sprint.

### DP-03 — Verificar permissões nos serviços, não nos controllers

- **Contexto:** hoje a verificação de permissão acontece em lugares diferentes. `OfertaService`, `DemandaService` e `SolicitacaoOfertaService` chamam `accessControl.require` internamente. Já `EstabelecimentoController`, `InstituicaoController`, `NutricionistaController`, `AlimentoController`, `PerfilAtendimentoInstituicaoController` e `UsuarioController` fazem a verificação no próprio controller.
- **Alternativas:**
  1. manter como está;
  2. verificar sempre no controller;
  3. verificar sempre no serviço.
- **Decisão:** a verificação de permissão fica na camada de serviço, onde já está o fluxo de ofertas, demandas e solicitações. Os controllers apenas autenticam o usuário e traduzem HTTP.
- **Justificativa:** a regra de negócio e a regra de acesso ficam juntas e podem ser testadas sem subir o servidor HTTP. Os controllers ficam com uma única responsabilidade.
- **Consequências:** é necessário mover as chamadas de seis controllers para os serviços correspondentes e passar `AccessControl` a esses serviços. A migração será feita aos poucos para não misturar muitas mudanças na mesma entrega (tarefa PRJ-04).
- **Status:** decidida; migração planejada no backlog.

## 3. Análise de coesão, acoplamento e responsabilidade

| Parte do código | Antes | Depois |
|---|---|---|
| `OfertaService` | Dois construtores, um deles com `null`, e dependência de `SolicitacaoOfertaService` só para saber a quantidade restante. Comportamento diferente conforme o construtor usado. | Um único construtor, com dependência de `DisponibilidadeOfertaService`. Responsabilidade restrita a cadastrar, listar e filtrar ofertas. |
| `SolicitacaoOfertaService` | Misturava o ciclo de vida da solicitação, o cálculo de quantidade disponível e a verificação de dono ou administrador. | Cuida do ciclo de vida da solicitação (solicitar, confirmar e cancelar); delega disponibilidade a `DisponibilidadeOfertaService` e autorização a `AccessControl`. |
| `OfertaController.ofertaToMap` | Repetia a expressão de disponibilidade e consultava a quantidade disponível duas vezes por oferta, a cada chamada percorrendo todas as solicitações. | Usa `estaDisponivel`; a regra não é mais reescrita no controller. |
| `AccessControl` | Tinha as permissões por perfil, mas a regra "dono ou administrador" ficava fora dele. | Concentra as duas formas de verificação. |
| `ApplicationConfig` | Monta todos os objetos e registra todas as rotas em um único método. Baixa coesão, que cresce a cada nova funcionalidade. | Sem mudança nesta sprint; separação planejada para a Sprint 6 (PRJ-05). |
| Controllers de estabelecimento, instituição, nutricionista, alimento, perfil de atendimento e usuário | Fazem verificação de permissão, que é regra de negócio. | Sem mudança nesta sprint; migração decidida em DP-03 (PRJ-04). |

### 3.1 Testabilidade

- **Disponibilidade da oferta (DP-01):** antes, o resultado de `OfertaService.quantidadeDisponivel` dependia de qual construtor tinha sido usado (com `null`, as solicitações eram ignoradas). Agora a regra pode ser exercitada com um `DisponibilidadeOfertaService` e um `SolicitacaoOfertaRepository` em memória, sem montar `SolicitacaoOfertaService` inteiro. Os cenários de abatimento de quantidade e de oferta expirada em `OfertaDisponibilidadeServiceTest` passam por esse caminho único.
- **Autorização por propriedade (DP-02):** a regra "dono ou administrador" passa a ser verificada em `AccessControl`, sem depender do servidor HTTP. O novo cenário do teste cobre o usuário não autorizado (403) e o administrador.
- **Verificação de permissões nos controllers (DP-03):** hoje não é possível testar essas regras sem subir o servidor HTTP; a migração para os serviços (PRJ-04) é o que torna essa parte testável.

## 4. Melhorias e refatorações

### 4.1 Refatoração executada (DP-01 e DP-02)

- **Problema:** regra de disponibilidade repetida em quatro lugares, serviço dependente de outro serviço por construtor com `null` e regra de autorização repetida e fora de `AccessControl`.
- **Refatoração:** criação de `DisponibilidadeOfertaService`, criação de `AccessControl.requireDonoOuAdministrador`, ajuste dos construtores em `OfertaService` e `SolicitacaoOfertaService`, ajuste de `OfertaController` e de `ApplicationConfig`.
- **Comportamento observável:** não muda. As respostas HTTP, os códigos de status e as mensagens de erro continuam os mesmos, por isso `requisitos.md` não foi alterado.
- **Teste:** `OfertaDisponibilidadeServiceTest` mantém os três cenários anteriores (filtro de ofertas, abatimento de quantidade solicitada e oferta expirada) e ganhou um cenário novo para cancelamento por usuário não autorizado e por administrador.

### 4.2 Problema identificado e fora do escopo desta sprint (PRJ-03)

- **Problema:** cinco arquivos de `src/main/java` declaram `package main.java.conexaosolidaria...` enquanto o restante do código os importa como `conexaosolidaria...`. São eles: `SolicitacaoOferta`, `StatusSolicitacaoOferta`, `SolicitacaoOfertaRepository`, `OfertaFiltros` e `SolicitacaoOfertaService`. O teste `OfertaDisponibilidadeServiceTest` também declara `package test.java.conexaosolidaria.service`. Enquanto isso não for corrigido, o backend não compila.
- **Tratamento:** a correção foi registrada como PRJ-03 e **não foi feita nesta sprint**, para não alterar arquivos de outras entregas. As classes novas e as alterações desta sprint já seguem a estrutura de diretórios (`conexaosolidaria...`) e passam a compilar quando PRJ-03 for aplicada.

### 4.3 Melhorias pendentes

- Corrigir as declarações de `package` dos arquivos listados em 4.2 (PRJ-03).
- Migrar a verificação de permissões dos controllers para os serviços (DP-03, PRJ-04).
- Separar o registro de rotas da montagem de objetos em `ApplicationConfig` (PRJ-05).

## 5. Estado anterior e posterior

| Item | Anterior | Posterior |
|---|---|---|
| Locais com a regra de disponibilidade | 4 | 1 (`DisponibilidadeOfertaService`) |
| Construtores de `OfertaService` | 2, um aceitando `null` | 1 |
| Locais com a regra "dono ou administrador" | 2 (em `SolicitacaoOfertaService`) | 1 (`AccessControl`) |
| Cenários em `OfertaDisponibilidadeServiceTest` | 3 | 4 (novo: cancelamento por usuário não autorizado e por administrador) |

Commits de referência:

- Estado anterior: `3eac19745701df62a95ae455459cde80f7872785` (`main` antes da Sprint 4)
- Estado posterior: `[cdc345e3ddf38c7a8882c86b3ec9a54ea02a18e0]`

## 6. Artefatos atualizados

- `modelaem/modelagem.md`: correspondência entre modelo e código (seção 5), revisão de requisitos (seção 6) e histórico (seção 8).
- `docs/testes/backlog-produto.mb`: nova seção 9.10 com as tarefas PRJ-01 a PRJ-05, lista da Sprint 4 e histórico de mudanças.
- `docs/requisitos/requisitos.md`: sem alteração, pois nenhuma decisão mudou comportamento observável.

## 7. Evidências no GitHub

| Tipo | Referência |
|---|---|
| Issues técnicas | `66` (PRJ-01), `67` (PRJ-02), `68` (PRJ-03), `69` (PRJ-04), `70` (PRJ-05) |
| Código afetado | [`DisponibilidadeOfertaService.java`](../../BackEnd/src/main/java/conexaosolidaria/service/DisponibilidadeOfertaService.java), [`OfertaService.java`](../../BackEnd/src/main/java/conexaosolidaria/service/OfertaService.java), [`SolicitacaoOfertaService.java`](../../BackEnd/src/main/java/conexaosolidaria/service/SolicitacaoOfertaService.java), [`AccessControl.java`](../../BackEnd/src/main/java/conexaosolidaria/security/AccessControl.java), [`OfertaController.java`](../../BackEnd/src/main/java/conexaosolidaria/controller/OfertaController.java), [`ApplicationConfig.java`](../../BackEnd/src/main/java/conexaosolidaria/config/ApplicationConfig.java) |
| Teste | [`OfertaDisponibilidadeServiceTest.java`](../../BackEnd/src/test/java/conexaosolidaria/service/OfertaDisponibilidadeServiceTest.java) |
| Commits | listados no arquivo `docs/sprints/sprint-04.md` |
| Tag | `sprint-04` |
