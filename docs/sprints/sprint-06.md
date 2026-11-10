# Sprint 6 --- Arquitetura e organização global da aplicação

-   **Data de entrega:** 09/11/2026
-   **Pontuação:** 2,5 pontos
-   **Tag obrigatória:** `sprint-06`
-   **Responsável por conferir este arquivo:** Raíssa Fernandes

## 1. Pergunta que esta sprint deve responder

**Como as partes principais da aplicação são organizadas, comunicam-se,
executam e são implantadas?**

A sprint registra a organização global do Conexão Solidária, as
responsabilidades dos componentes existentes e as formas de execução.
Também identifica as limitações atuais: a comunicação entre frontend e
backend ainda não foi concluída e os repositórios analisados utilizam
armazenamento em memória.

## 2. Objetivo e resultado da sprint

**Objetivo planejado:**

Definir e documentar a arquitetura global da aplicação, identificando
estilo arquitetural, componentes, responsabilidades, tecnologias,
dependências e formas de execução. Relacionar as decisões aos requisitos
não funcionais e à estrutura real do código. Evoluir a organização do
backend e implementar a comunicação entre frontend, API e banco de
dados, validando ao menos um fluxo funcional completo.

**Resultado efetivamente alcançado:**

Foi elaborado e versionado o documento
`docs/arquitetura/arquitetura.md`, com visão geral, diagramas de
contexto e componentes, responsabilidades, tecnologias, decisões
arquiteturais, relação com requisitos não funcionais, implantação,
riscos e correspondência entre arquitetura e código. O documento foi
submetido pelo [Pull Request
#79](https://github.com/liviafgs/TrabalhoPratico-eng-software/pull/79).

A análise do repositório identificou frontend estático em HTML, CSS e
JavaScript; backend Java 17 organizado em camadas; servidor HTTP na
porta 8080; composição de dependências em `ApplicationConfig`; e
repositórios que utilizam estruturas em memória. Existem configurações
de PostgreSQL e changelogs do Liquibase, mas a integração efetiva com o
banco não foi confirmada na implementação analisada.

**A integração funcional entre frontend e backend não foi concluída
nesta sprint.** Não foi demonstrado um fluxo ponta a ponta passando pela
interface, API e banco de dados. A separação entre composição de
dependências e registro de rotas, prevista na Issue #70, permanece
pendente de conclusão/validação. Portanto, o principal resultado
alcançado foi a documentação arquitetural baseada no código existente, e
não a integração completa da aplicação.

## 3. Checklist do artefato central --- 0,75 ponto

**Entrega esperada:** `docs/arquitetura/arquitetura.md`, diagramas,
componentes, responsabilidades, tecnologias, decisões e implantação.

-   [x] Estilo arquitetural justificado.
-   [x] Componentes ligados a diretórios/serviços reais.
-   [x] Comunicação e dependências explicadas, com a ausência de
    integração frontend-backend explicitamente registrada.
-   [x] Requisitos não funcionais relacionados a decisões arquiteturais.
-   [x] Execução local e situação do ambiente publicado documentadas;
    não há URL pública confirmada.

### Links dos artefatos

  -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  Artefato                Link na tag da sprint                                                                                                                           O que mudou
  criado/atualizado                                                                                                                                                       
  ----------------------- ----------------------------------------------------------------------------------------------------------------------------------------------- -----------------------
  Documento de            [docs/arquitetura/arquitetura.md](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/arquitetura/arquitetura.md) ---       Documento com estilo
  arquitetura             versão atual na `main`; substituir `main` por `sprint-06` após criação/verificação da tag                                                       arquitetural,
                                                                                                                                                                          diagramas, componentes
                                                                                                                                                                          reais,
                                                                                                                                                                          responsabilidades,
                                                                                                                                                                          decisões DA-01 a DA-05,
                                                                                                                                                                          requisitos não
                                                                                                                                                                          funcionais,
                                                                                                                                                                          implantação, riscos e
                                                                                                                                                                          rastreabilidade.

  Documento de requisitos [docs/requisitos/requisitos.md](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/requisitos/requisitos.md)               Referência para
                                                                                                                                                                          relacionar requisitos
                                                                                                                                                                          funcionais e não
                                                                                                                                                                          funcionais às decisões
                                                                                                                                                                          arquiteturais.

  Decisões de projeto     [docs/projeto/decisoes-de-projeto.md](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/projeto/decisoes-de-projeto.md)   Referência para
                                                                                                                                                                          decisões existentes
                                                                                                                                                                          sobre organização do
                                                                                                                                                                          backend e autorização.

  Backend                 [BackEnd/](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd)                                                          Estrutura analisada
                                                                                                                                                                          para identificar
                                                                                                                                                                          inicialização, servidor
                                                                                                                                                                          HTTP, configuração,
                                                                                                                                                                          controllers, serviços,
                                                                                                                                                                          segurança, modelos e
                                                                                                                                                                          repositórios.

  Frontend                [front-end/](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/front-end)                                                      Estrutura HTML, CSS e
                                                                                                                                                                          JavaScript analisada;
                                                                                                                                                                          não foi identificada
                                                                                                                                                                          integração HTTP com o
                                                                                                                                                                          backend no `script.js`
                                                                                                                                                                          consultado.
  -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## 4. Incremento da aplicação web --- 0,75 ponto

**Incremento mínimo esperado:** Código organizado conforme a
arquitetura, comunicação funcional entre partes e configuração de
execução/dependências.

### O que foi implementado ou evoluído

O incremento efetivamente concluído nesta parte da sprint foi a
documentação da arquitetura global, elaborada a partir da inspeção do
código existente.

O frontend está organizado em `front-end/index.html`,
`front-end/style.css` e `front-end/script.js`, utilizando HTML, CSS e
JavaScript. O backend utiliza Java 17 e Maven e está organizado nos
pacotes `config`, `server`, `controller`, `service`, `repository`,
`model`, `security`, `dto` e `exception`.

A inicialização ocorre pela classe `Main`, que utiliza
`ApplicationConfig` para instanciar componentes e configurar o servidor
HTTP. O servidor utiliza `com.sun.net.httpserver.HttpServer` e
disponibiliza a aplicação na porta 8080. Os serviços concentram regras
de negócio e utilizam repositórios para consultar e armazenar dados. Nos
repositórios verificados, o armazenamento é realizado em estruturas em
memória, como `ConcurrentHashMap`/`ConcurrentMap`.

Foram documentadas decisões relacionadas à organização em camadas,
composição manual das dependências, servidor HTTP nativo, armazenamento
atual em memória e frontend estático. Essas decisões foram relacionadas
aos requisitos não funcionais, principalmente `RNF-02` (segurança e
controle de acesso), `RNF-10` (manutenibilidade) e `RNF-11`
(rastreabilidade), além dos requisitos de usabilidade, acessibilidade,
compatibilidade e responsividade aplicáveis ao frontend.

**Limitação importante:** a integração entre frontend e backend ainda
não está implementada/validada. Não foi identificada chamada HTTP do
frontend para a API no `front-end/script.js` analisado. Também não foi
comprovada a persistência efetiva no PostgreSQL, apesar da existência de
propriedades de configuração e changelogs do Liquibase. Assim, não há
fluxo ponta a ponta para demonstrar nesta entrega.

### Como executar e verificar

Backend --- a partir da pasta `BackEnd/`:

``` bash
mvn compile exec:java
```

O comando utiliza Maven e o plugin de execução configurado no `pom.xml`.
O backend inicia o servidor HTTP na porta 8080. É necessário ter Java 17
e Maven instalados.

Frontend --- a partir da raiz do repositório:

``` bash
# Abrir front-end/index.html no navegador
```

O frontend pode ser visualizado localmente como página estática. Essa
verificação não representa integração com o backend.

Para conferir o estado local do repositório:

``` bash
git status
git log --oneline -5
```

Não registrar como aprovado um teste de integração ponta a ponta: esse
fluxo permanece pendente.

  ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  Requisito/Issue                                                            Código ou protótipo                                                                                                                                                 Evidência de execução
  -------------------------------------------------------------------------- ------------------------------------------------------------------------------------------------------------------------------------------------------------------- -------------------------------------------------------------------------
  `RNF-10` --- Manutenibilidade e `RNF-11` --- Rastreabilidade; [Issue       [Documento de arquitetura](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/arquitetura/arquitetura.md) e                                    [PR
  #28](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/28)   [backend](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd)                                                                               #79](https://github.com/liviafgs/TrabalhoPratico-eng-software/pull/79),
                                                                                                                                                                                                                                                 que registra a submissão da documentação.

  `RNF-02` --- Restrição de acesso conforme o perfil; referência [Issue      [Pacote security](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd/src/main/java/conexaosolidaria/security) e [pacote                     Código disponível para inspeção; não foi anexada nova evidência de teste
  #6](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/6)     service](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd/src/main/java/conexaosolidaria/service)                                         ponta a ponta nesta sprint.

  Integração entre frontend, API e banco; [Issue                             [Frontend](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/front-end) e                                                                          **Pendente:** não há evidência de fluxo funcional completo; integração
  #29](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/29)   [backend](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd)                                                                               não concluída.

  Separar composição de dependências e registro de rotas; [Issue             [ApplicationConfig.java](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/BackEnd/src/main/java/conexaosolidaria/config/ApplicationConfig.java)   **Pendente de confirmação/conclusão:** Issue #70 permanece aberta no
  #70](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/70)                                                                                                                                                                       GitHub.
  ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## 5. Scrum e gestão do trabalho --- 0,50 ponto

### Sprint Backlog

  ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  Issue                                                                       Descrição              Responsável               Critério de           Situação
                                                                                                                               aceitação/conclusão   
  --------------------------------------------------------------------------- ---------------------- ------------------------- --------------------- ------------------------
  [#28](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/28)   Definir e documentar a @raissafernandesdesouza   Documento representa  Documentação elaborada e
                                                                              arquitetura global da                            os principais         PR #79 fechado; Issue
                                                                              aplicação.                                       componentes           #28 ainda aparece aberta
                                                                                                                               existentes, suas      no GitHub.
                                                                                                                               relações,             
                                                                                                                               responsabilidades e   
                                                                                                                               tecnologias, sendo    
                                                                                                                               compatível com a      
                                                                                                                               implementação.        

  [#29](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/29)   Implementar integração @raissafernandesdesouza   Um fluxo funcional    Pendente; integração e
                                                                              entre frontend, API e                            deve percorrer as     persistência ponta a
                                                                              banco de dados.                                  camadas e apresentar  ponta não concluídas.
                                                                                                                               os dados              
                                                                                                                               corretamente.         

  [#70](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/70)   Separar a montagem dos @rawanymendonca           Montagem dos objetos  Pendente de
                                                                              objetos e o registro                             e tabela de rotas     confirmação/conclusão;
                                                                              das rotas atualmente                             organizadas em        Issue permanece aberta
                                                                              concentrados em                                  classes diferentes.   no GitHub.
                                                                              `ApplicationConfig`.                                                   
  ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------

### Acompanhamento

-   **GitHub Project:** [Projetos do
    repositório](https://github.com/liviafgs/TrabalhoPratico-eng-software/projects)
    --- consultar a visão da Sprint 6 no quadro da equipe.
-   **Reuniões/decisões:** [Pasta de
    reuniões](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/docs/reunioes)
    e [decisões de
    projeto](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/projeto/decisoes-de-projeto.md).
    Não foi identificado link específico de ata da Sprint 6.
-   **Impedimentos:** integração entre frontend, API e banco não
    concluída; por isso, não foi possível demonstrar um fluxo completo.
    A configuração de banco existente não comprova persistência ativa.
-   **Mudanças de escopo:** não foi localizado registro de mudança
    formal de escopo. A documentação foi concluída sem declarar como
    implementadas as integrações pendentes.

## 6. GitHub, documentação e rastreabilidade --- 0,50 ponto

  ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  Tipo de evidência         Link                                                                                                                                    O que comprova
  ------------------------- --------------------------------------------------------------------------------------------------------------------------------------- ------------------------------------
  Issue                     [Issue #28 --- Definir arquitetura da aplicação](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/28)                    Escopo e critério de aceitação do
                                                                                                                                                                    artefato central. A Issue ainda
                                                                                                                                                                    aparece aberta.

  Issue                     [Issue #29 --- Integração entre frontend, API e banco](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/29)              Escopo da integração ponta a ponta,
                                                                                                                                                                    que permanece pendente.

  Issue                     [Issue #70 --- Separar rotas e composição de dependências](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/70)          Atividade de organização do backend
                                                                                                                                                                    prevista para a Sprint 6; permanece
                                                                                                                                                                    aberta.

  Pull Request              [PR #79 --- sprint-6-documento-arquitetura](https://github.com/liviafgs/TrabalhoPratico-eng-software/pull/79)                           Submissão da documentação da
                                                                                                                                                                    arquitetura; PR fechado em
                                                                                                                                                                    07/11/2026.

  Commit                    [Histórico de commits do documento de                                                                                                   Histórico de alterações do arquivo
                            arquitetura](https://github.com/liviafgs/TrabalhoPratico-eng-software/commits/main/docs/arquitetura/arquitetura.md)                     `docs/arquitetura/arquitetura.md`.

  Código/arquivo            [docs/arquitetura/arquitetura.md](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/arquitetura/arquitetura.md)   Documento com diagramas, decisões,
                                                                                                                                                                    responsabilidades, implantação,
                                                                                                                                                                    riscos e correspondência com o
                                                                                                                                                                    código.

  Código/arquivo            [BackEnd/](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd) e                                                Estruturas de código analisadas para
                            [front-end/](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/front-end)                                              produzir a arquitetura.

  Teste/captura/relatório   [Pasta de testes](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/docs/testes)                                       Localização dos registros de teste
                                                                                                                                                                    existentes. Não foi localizada
                                                                                                                                                                    evidência de teste integrado
                                                                                                                                                                    frontend-backend-banco para esta
                                                                                                                                                                    sprint.
  ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

### Rastreabilidade resumida

  -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  Requisito           Issue                                                                       Artefato/modelo/decisão                                                                                            Código                                                                                                                                 Teste/evidência
  ------------------- --------------------------------------------------------------------------- ------------------------------------------------------------------------------------------------------------------ -------------------------------------------------------------------------------------------------------------------------------------- -------------------------------------------------------------------------
  `RNF-02` ---        [#6](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/6)     [Arquitetura ---                                                                                                   [security/](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd/src/main/java/conexaosolidaria/security) e      Código documentado; não foi anexada evidência nova de teste de integração
  Segurança:                                                                                      DA-01](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/arquitetura/arquitetura.md) e       [service/](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd/src/main/java/conexaosolidaria/service)          nesta sprint.
  restringir                                                                                      [decisões de                                                                                                                                                                                                                                              
  funcionalidades                                                                                 projeto](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/projeto/decisoes-de-projeto.md)                                                                                                                                          
  conforme o perfil                                                                                                                                                                                                                                                                                                                                         
  autenticado                                                                                                                                                                                                                                                                                                                                               

  `RNF-10` ---        [#28](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/28)   [Arquitetura --- DA-01 e                                                                                           [Estrutura do backend](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd/src/main/java/conexaosolidaria)      [PR
  Manutenibilidade:                                                                               DA-02](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/arquitetura/arquitetura.md)                                                                                                                                                #79](https://github.com/liviafgs/TrabalhoPratico-eng-software/pull/79) e
  organização em                                                                                                                                                                                                                                                                                                                                            inspeção dos diretórios reais.
  módulos e                                                                                                                                                                                                                                                                                                                                                 
  componentes                                                                                                                                                                                                                                                                                                                                               

  `RNF-11` ---        [#28](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/28)   [Arquitetura --- seção                                                                                             [Backend](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd) e                                                [PR
  Rastreabilidade                                                                                 8](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/arquitetura/arquitetura.md)             [frontend](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/front-end)                                               #79](https://github.com/liviafgs/TrabalhoPratico-eng-software/pull/79).
  entre requisitos,                                                                                                                                                                                                                                                                                                                                         
  histórias, Issues e                                                                                                                                                                                                                                                                                                                                       
  artefatos                                                                                                                                                                                                                                                                                                                                                 

  Integração da       [#29](https://github.com/liviafgs/TrabalhoPratico-eng-software/issues/29)   [Arquitetura --- diagramas e                                                                                       [Frontend](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/front-end),                                              **Ainda não aplicável:** integração ponta a ponta não concluída; Issue
  interface com a API                                                                             DA-04/DA-05](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/docs/arquitetura/arquitetura.md)   [backend](https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/main/BackEnd) e                                                permanece pendente.
  e o banco                                                                                                                                                                                                          [configuração](https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/main/BackEnd/src/main/resources/application.properties)   
  -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## 7. Revisão do incremento

-   **O que foi demonstrado:** documentação da arquitetura global do
    Conexão Solidária, com diagramas compatíveis com a estrutura
    consultada, identificação das camadas do backend, descrição do
    frontend, responsabilidades, dependências, decisões arquiteturais,
    requisitos não funcionais e riscos.
-   **Critérios atendidos:** estilo arquitetural justificado;
    componentes relacionados a diretórios e arquivos reais; dependências
    internas do backend descritas; requisitos não funcionais
    relacionados às decisões; execução local documentada. O documento
    foi submetido no PR #79.
-   **Itens não concluídos:** integração funcional entre frontend e
    backend; fluxo ponta a ponta envolvendo API e banco; confirmação da
    persistência efetiva no PostgreSQL; conclusão/validação da separação
    entre montagem de dependências e registro de rotas da Issue #70; URL
    pública de implantação; criação/verificação da tag `sprint-06`.
-   **Motivo das pendências:** a comunicação entre a interface e a API
    ainda não foi implementada/validada. Os repositórios analisados usam
    armazenamento em memória e a configuração PostgreSQL/Liquibase não
    comprova conexão ativa. A Issue #70 permanece aberta. A publicação
    não possui URL confirmada no README consultado.
-   **Feedback recebido e ajustes:** o documento foi ajustado para
    seguir o modelo de arquitetura definido pelo professor e refletir a
    implementação real, identificando explicitamente as integrações e
    funcionalidades pendentes. Não foi localizado registro de feedback
    formal adicional.

## 8. Retrospectiva e próxima sprint

-   **Funcionou bem:** a análise direta do repositório permitiu
    documentar a arquitetura de acordo com o código existente, evitando
    diagramas genéricos ou componentes apresentados incorretamente como
    implementados. A organização por decisões arquiteturais e
    correspondência arquitetura × código facilitou a rastreabilidade.
-   **Precisa melhorar:** antecipar a integração entre frontend, API e
    banco; validar a persistência real; manter as Issues atualizadas
    conforme a conclusão das tarefas; e registrar evidências de execução
    dos fluxos completos.
-   **Ação concreta para a próxima sprint:** priorizar a conclusão da
    Issue #29, implementando e testando ao menos um fluxo ponta a ponta
    entre frontend, API e persistência; revisar a Issue #70; atualizar a
    arquitetura após as mudanças; e registrar testes, commits e Pull
    Requests correspondentes.

## 9. O que não será considerado suficiente

-   Somente lista de frameworks.
-   Diagrama que não corresponde ao código.
-   Confundir arquitetura global com padrão de projeto local.

## 10. Links enviados no UFLA Virtual

-   **Tag `sprint-06`:** pendente de criação/verificação. Após criar a
    tag, inserir o link:
    `https://github.com/liviafgs/TrabalhoPratico-eng-software/tree/sprint-06`
-   **Este arquivo na tag:** após criar e enviar
    `docs/sprints/sprint-06.md` e criar/verificar a tag, inserir:
    `https://github.com/liviafgs/TrabalhoPratico-eng-software/blob/sprint-06/docs/sprints/sprint-06.md`
-   **Observação adicional:** a documentação arquitetural foi elaborada
    e submetida no PR #79. A integração frontend-backend e o fluxo com
    banco de dados permanecem pendentes e estão registrados na Issue
    #29. Os links usam a branch `main` como referência provisória; devem
    ser conferidos e substituídos pelos links da tag `sprint-06` antes
    do envio no UFLA Virtual.
