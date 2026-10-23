# Levantamento de problemas técnicos --- Conexão Solidária

> **Finalidade:** inventário técnico para priorização e acompanhamento.
> Este documento registra os achados informados pelo Codex; as
> evidências devem ser conferidas no repositório e, quando possível,
> reproduzidas antes de considerar cada item definitivamente validado.
>
> **Escopo desta versão:** levantamento e recomendações. Nenhuma
> correção de código foi aplicada por meio deste documento.

## 1. Resumo executivo

O levantamento reportou **14 problemas ou riscos**: 1 crítico, 2 altos,
10 médios e 1 baixo.

  Categoria                          Quantidade informada
  -------------------------------- ----------------------
  Segurança                                             5
  Compilação                                            1
  Regra de negócio                                      1
  Validação                                             1
  Requisitos                                            1
  Front-end                                             1
  Documentação e rastreabilidade                        1
  Arquitetura                                           1
  Integridade de dados                                  1
  Concorrência                                          1
  **Total**                                        **14**

### Prioridades imediatas

1.  **PROB-001 --- Compilação quebrada:** impede executar a suíte de
    testes e validar alterações.
2.  **PROB-002 --- Cadastro público de administradores:** risco crítico
    de elevação indevida de privilégios.
3.  **PROB-003 --- Habilitação de instituição sem verificar responsável
    técnico:** pode permitir operações em desacordo com as regras de
    negócio.
4.  **PROB-005 --- Dados de demanda incompletos:** divergência entre
    requisitos, modelo, serviço e interface.
5.  **PROB-008 e PROB-013 --- Integridade e concorrência:** verificar
    unicidade de CNPJ nas atualizações e atomicidade da reserva de
    quantidade.

> **Nota de validação:** os resultados abaixo foram transcritos e
> organizados a partir do relatório do Codex fornecido pela equipe. Não
> representam uma nova execução independente dos comandos ou uma
> auditoria independente de cada arquivo.

## 2. Problemas confirmados por diagnóstico

### PROB-001 --- Pacotes incompatíveis impedem a compilação

-   **Categoria:** Compilação
-   **Severidade:** Alta
-   **Confiança informada:** Confirmada
-   **Localização informada:**
    -   `BackEnd/src/main/java/conexaosolidaria/model/SolicitacaoOferta.java`
    -   `BackEnd/src/main/java/conexaosolidaria/model/StatusSolicitacaoOferta.java`
    -   `BackEnd/src/main/java/conexaosolidaria/repository/SolicitacaoOfertaRepository.java`
    -   `BackEnd/src/main/java/conexaosolidaria/service/OfertaFiltros.java`
    -   `BackEnd/src/main/java/conexaosolidaria/service/SolicitacaoOfertaService.java`
    -   `BackEnd/src/test/java/.../OfertaDisponibilidadeServiceTest.java`
        (caminho exato a confirmar)
-   **Evidência informada:** `mvn test` falhou durante a compilação
    principal com 9 erros; `mvn package` falhou na compilação dos testes
    com 14 erros. O relatório aponta declarações de pacote
    `main.java.conexaosolidaria...` em fontes e
    `test.java.conexaosolidaria...` no teste, divergentes dos pacotes
    usados pelos consumidores.
-   **Descrição e impacto:** declarações de pacote incompatíveis com a
    estrutura padrão Maven impedem a resolução de classes e símbolos.
    Parte dos erros pode ser consequência da mesma causa, não defeitos
    independentes.
-   **Causa provável:** declarações de pacote incompatíveis com os
    diretórios e imports.
-   **Recomendação:** tratar primeiro a Issue #68 / PRJ-03, conferir
    declarações de pacote e imports e repetir a compilação e os testes.
    Limpar os artefatos de `target` se necessário para distinguir
    resultados atuais de artefatos antigos.
-   **Relação com itens anteriores:** Issue #68 --- PRJ-03;
    `docs/projeto/decisoes-de-projeto.md`.
-   **Estado:** Pendente de correção e validação.

## 3. Problemas confirmados pela inspeção do código, conforme o relatório

### PROB-002 --- Cadastro público permite criar administradores

-   **Categoria:** Segurança e autorização
-   **Severidade:** Crítica
-   **Confiança informada:** Confirmada
-   **Localização informada:** `UsuarioController.java:27`,
    `UsuarioService.java:22`, `PerfilUsuario.java:5`.
-   **Evidência informada:** `POST /usuarios` não autentica nem verifica
    autorização; o serviço aceita o perfil informado e a enumeração
    inclui `ADMINISTRADOR`.
-   **Descrição e impacto:** uma pessoa sem conta poderia registrar um
    usuário administrativo e obter privilégios após autenticar.
-   **Causa provável:** o cadastro público aceita perfis privilegiados
    sem separar o fluxo de provisionamento administrativo.
-   **Recomendação:** impedir a criação de perfis privilegiados pelo
    endpoint público e definir um processo administrativo protegido.
-   **Relação com itens anteriores:** RF-05/RF-06, RNF-02 e Issue #18,
    conforme o relatório.
-   **Estado:** Pendente de validação e correção.

### PROB-003 --- Instituição pode habilitar a si própria sem verificar nutricionista

-   **Categoria:** Regra de negócio e autorização
-   **Severidade:** Alta
-   **Confiança informada:** Confirmada
-   **Localização informada:** `InstituicaoService.java:78`,
    `SolicitacaoOfertaService.java:30`,
    `RequisitosNovosServiceTest.java:19`.
-   **Evidência informada:** o fluxo de habilitação não verifica vínculo
    com nutricionista; a solicitação verifica o perfil, mas não o estado
    habilitado da instituição. O teste citado habilita a instituição sem
    cadastrar nutricionista.
-   **Descrição e impacto:** uma instituição sem responsável técnico
    poderia ser habilitada e solicitar ofertas, contrariando a regra
    documentada.
-   **Causa provável:** ausência de verificação do vínculo e do estado
    da instituição nos fluxos correspondentes.
-   **Recomendação:** verificar o vínculo antes da habilitação e validar
    a elegibilidade no momento de solicitar uma oferta.
-   **Relação com itens anteriores:** RN-01/RN-02, RF-03/RF-04/RF-10 e
    Issue #15, conforme o relatório.
-   **Estado:** Pendente de validação e correção.

### PROB-004 --- Valores não finitos passam pela validação numérica

-   **Categoria:** Validação
-   **Severidade:** Média
-   **Confiança informada:** Confirmada
-   **Localização informada:** `RequestUtils.java:32`,
    `OfertaService.java:110`, `DemandaService.java:48`.
-   **Evidência informada:** os valores são convertidos para `double` e
    a validação rejeita apenas valores menores ou iguais a zero; `NaN` e
    infinito não são barrados por essa condição.
-   **Descrição e impacto:** entradas não finitas podem gerar
    quantidades incoerentes ou respostas JSON inválidas.
-   **Causa provável:** validação incompleta de valores numéricos.
-   **Recomendação:** rejeitar explicitamente valores não finitos e
    validar quantidades positivas e finitas.
-   **Relação com itens anteriores:** RN-03/RNF-04, conforme o
    relatório.
-   **Estado:** Pendente de teste e correção.

### PROB-005 --- Demanda não armazena campos exigidos nos requisitos

-   **Categoria:** Requisitos e regra de negócio
-   **Severidade:** Média
-   **Confiança informada:** Confirmada
-   **Localização informada:** `Demanda.java`, `DemandaService.java:26`,
    `front-end/script.js:437`; `docs` de modelagem, linha 248, conforme
    o relatório.
-   **Evidência informada:** modelo e serviço não incluem beneficiários
    estimados, finalidade, consumo per capita ou justificativa; a
    interface lê a justificativa, mas não a inclui no objeto enviado.
-   **Descrição e impacto:** informações necessárias para justificar ou
    avaliar quantidades excepcionais não ficam registradas no backend. A
    documentação de modelagem afirma que RF-16 a RF-20 estão
    implementados, embora o relatório identifique cobertura incompleta.
-   **Causa provável:** divergência entre o contrato de requisitos, o
    modelo, o serviço e a interface.
-   **Recomendação:** alinhar modelo, serviço, controller e interface
    aos requisitos; testar persistência e consulta dos campos.
-   **Relação com itens anteriores:** RF-15/RF-16/RF-18/RF-19,
    RN-05/RN-07/RN-09 e Issues #16/#17, conforme o relatório.
-   **Estado:** Pendente de validação e correção.

### PROB-006 --- Hash de senha usa SHA-256 direto

-   **Categoria:** Segurança
-   **Severidade:** Média
-   **Confiança informada:** Confirmada
-   **Localização informada:** `PasswordHasher.java:13`.
-   **Evidência informada:** o código usa salt, mas calcula hash
    diretamente com SHA-256, sem uma função de derivação de senha com
    custo configurável.
-   **Descrição e impacto:** se os hashes forem expostos, senhas fracas
    podem ser testadas offline com rapidez.
-   **Causa provável:** uso de hash criptográfico geral para
    armazenamento de senhas.
-   **Recomendação:** adotar uma função apropriada para armazenamento de
    senhas, com parâmetros de custo documentados.
-   **Relação com itens anteriores:** RNF-01 e TEST-01, conforme o
    relatório.
-   **Estado:** Pendente de avaliação e correção.

### PROB-007 --- Protótipo confirma a solicitação errada

-   **Categoria:** Front-end e regra de negócio
-   **Severidade:** Média
-   **Confiança informada:** Confirmada
-   **Localização informada:** `front-end/script.js:562` e
    `front-end/script.js:581`.
-   **Evidência informada:** o botão passa `this`, mas a função ignora o
    argumento e encontra a primeira solicitação pendente.
-   **Descrição e impacto:** quando há várias solicitações pendentes, a
    ação pode alterar uma solicitação diferente daquela selecionada.
-   **Causa provável:** ação não vinculada ao identificador específico
    do item.
-   **Recomendação:** associar a ação ao ID da solicitação clicada e
    atualizar somente o registro correspondente.
-   **Relação com itens anteriores:** RF-12/RF-13 e US-03, conforme o
    relatório.
-   **Estado:** Pendente de validação e correção.

### PROB-008 --- Atualizações cadastrais não preservam a unicidade do CNPJ

-   **Categoria:** Integridade de dados
-   **Severidade:** Média
-   **Confiança informada:** Confirmada
-   **Localização informada:** `EstabelecimentoService.java:55`,
    `InstituicaoService.java:56`, `EstabelecimentoRepository.java:18`,
    `InstituicaoRepository.java:18`.
-   **Evidência informada:** os métodos de atualização não verificam se
    o novo CNPJ pertence a outro cadastro; os repositórios adicionam a
    nova chave ao índice sem remover a anterior.
-   **Descrição e impacto:** podem ocorrer CNPJs duplicados e
    associações antigas remanescentes nos índices.
-   **Causa provável:** atualização sem validação de unicidade e sem
    sincronização completa do índice.
-   **Recomendação:** verificar unicidade antes da atualização e manter
    os índices coerentes ao alterar o CNPJ.
-   **Relação com itens anteriores:** RF-01/RF-02, conforme o relatório.
-   **Estado:** Pendente de teste e correção.

### PROB-009 --- Autorização dividida entre controllers e serviços

-   **Categoria:** Arquitetura e testes
-   **Severidade:** Baixa
-   **Confiança informada:** Confirmada
-   **Localização informada:** controllers de usuário, estabelecimento,
    instituição, nutricionista, alimento e perfil de atendimento;
    `docs/projeto/decisoes-de-projeto.md`.
-   **Evidência informada:** parte das verificações de `AccessControl`
    ocorre nos controllers; a decisão DP-03 registra a migração para
    serviços como pendente.
-   **Descrição e impacto:** regras de autorização distribuídas
    dificultam testes sem HTTP e deixam a responsabilidade dividida
    entre camadas.
-   **Causa provável:** migração de responsabilidades ainda não
    concluída.
-   **Recomendação:** seguir a decisão existente e padronizar a
    localização das verificações, acompanhada de testes de permissão.
-   **Relação com itens anteriores:** DP-03 e Issue #69 --- PRJ-04,
    conforme o relatório.
-   **Estado:** Pendente conforme planejamento anterior.

### PROB-010 --- Tokens não têm expiração ou revogação

-   **Categoria:** Segurança
-   **Severidade:** Média
-   **Confiança informada:** Provável
-   **Localização informada:** `TokenService.java:12`.
-   **Evidência informada:** tokens são mantidos em mapa em memória; o
    relatório não identificou expiração, revogação ou fluxo de logout.
-   **Descrição e impacto:** um token capturado pode continuar válido
    enquanto a instância do servidor estiver ativa; o risco depende do
    contexto de implantação.
-   **Causa provável:** ciclo de vida de sessão não implementado ou não
    identificado na inspeção.
-   **Recomendação:** definir e implementar ciclo de vida da sessão,
    incluindo expiração e encerramento, e testar esses comportamentos.
-   **Relação com itens anteriores:** RF-05/RNF-02; nenhuma Issue
    específica foi identificada no relatório.
-   **Estado:** Requer confirmação adicional.

### PROB-011 --- Configuração de banco contém senha em texto claro, mas não está ativa

-   **Categoria:** Configuração e segurança
-   **Severidade:** Média
-   **Confiança informada:** Suspeita quanto à exposição real; presença
    da propriedade foi reportada como confirmada
-   **Localização informada:** `application.properties:1` e
    `pom.xml:18`.
-   **Evidência informada:** o arquivo contém configuração de PostgreSQL
    e senha em texto claro; o relatório não reproduziu o valor. O POM
    não declara Spring Boot, driver PostgreSQL ou Liquibase, e o backend
    usa repositórios em memória.
-   **Descrição e impacto:** não há evidência de que as propriedades
    sejam utilizadas pelo backend atual. Se o valor for uma credencial
    real ou reutilizada, existe risco de exposição.
-   **Causa provável:** configuração remanescente ou não integrada à
    aplicação atual.
-   **Recomendação:** verificar se o valor é apenas exemplo; se for
    credencial real, rotacioná-la e removê-la do arquivo. Documentar a
    configuração de banco quando a persistência for implementada.
-   **Relação com itens anteriores:** persistência em banco registrada
    como pendência na modelagem, conforme o relatório.
-   **Estado:** Requer confirmação; não expor credenciais no registro.

### PROB-012 --- Dados dinâmicos são inseridos diretamente em HTML

-   **Categoria:** Segurança
-   **Severidade:** Média
-   **Confiança informada:** Suspeita
-   **Localização informada:** `front-end/script.js:137`,
    `front-end/script.js:529`, `front-end/script.js:622`.
-   **Evidência informada:** dados de oferta, solicitação e demanda são
    interpolados em `innerHTML`. O protótipo atual usa dados locais; não
    foi confirmado fluxo remoto explorável entre usuários.
-   **Descrição e impacto:** caso os dados passem a vir de usuários ou
    da API, conteúdo poderá ser interpretado como marcação ou script.
-   **Causa provável:** renderização de conteúdo dinâmico por
    interpolação de HTML.
-   **Recomendação:** preferir inserir conteúdo como texto e sanitizar
    entradas nos casos em que HTML seja necessário.
-   **Relação com itens anteriores:** RNF-02/RNF-03, conforme o
    relatório.
-   **Estado:** Requer validação de fluxo e origem dos dados.

### PROB-013 --- Verificação de estoque e salvamento não são atômicos

-   **Categoria:** Concorrência e integridade
-   **Severidade:** Média
-   **Confiança informada:** Provável
-   **Localização informada:** `SolicitacaoOfertaService.java:30`,
    `DisponibilidadeOfertaService.java:19`,
    `SolicitacaoOfertaRepository.java:16`.
-   **Evidência informada:** a quantidade é lida e validada antes de a
    solicitação ser salva; operações individuais no mapa são
    thread-safe, mas a sequência verificar-calcular-salvar não é
    atômica.
-   **Descrição e impacto:** solicitações concorrentes podem observar o
    mesmo saldo e, juntas, ultrapassar a quantidade disponível.
-   **Causa provável:** operação composta sem sincronização ou mecanismo
    atômico.
-   **Recomendação:** garantir atomicidade na reserva de quantidade e
    criar teste concorrente para validar o comportamento.
-   **Relação com itens anteriores:** RN-03/RNF-04 e Issue #66/PRJ-01,
    conforme o relatório. A centralização da regra de disponibilidade
    não garante, por si só, atomicidade.
-   **Estado:** Requer reprodução com teste concorrente.

### PROB-014 --- Status das Issues diverge da documentação da Sprint 4

-   **Categoria:** Documentação e rastreabilidade
-   **Severidade:** Média
-   **Confiança informada:** Confirmada
-   **Localização informada:** `docs/sprints/sprint-04.md` e
    `docs/testes/backlog-produto.mb`.
-   **Evidência informada:** a Sprint 4 registra PRJ-01/#66 e PRJ-02/#67
    como concluídas, mas o relatório indica que as Issues públicas #66 e
    #67 permaneciam abertas e no Backlog. As Issues #68--#70 também
    aparecem abertas, em concordância com as pendências documentadas.
-   **Descrição e impacto:** a rastreabilidade entre entregas, Issues e
    documentação fica inconsistente.
-   **Causa provável:** estados não sincronizados entre GitHub e
    documentação local.
-   **Recomendação:** verificar o estado real de cada Issue e atualizar
    a documentação ou o GitHub para refletir o histórico verdadeiro.
-   **Relação com itens anteriores:** Issues #66, #67, #68, #69 e #70,
    conforme o relatório.
-   **Estado:** Pendente de conferência.

## 4. Melhorias recomendadas sem defeito atual comprovado

Os itens abaixo foram classificados pelo relatório como melhorias ou
limitações, não como defeitos inesperados confirmados.

  ---------------------------------------------------------------------------
  ID                Tema                  Observação        Próximo passo
                                                            sugerido
  ----------------- --------------------- ----------------- -----------------
  MEL-001           Persistência          Repositórios em   Manter como
                                          memória perdem    limitação
                                          dados após        conhecida e
                                          reiniciar; a      seguir o
                                          modelagem já      planejamento de
                                          registra isso     persistência.
                                          como pendência.   

  MEL-002           Integração da         O front-end usa   Registrar
                    interface             dados locais e    dependência e
                                          não chama o       critério de
                                          backend; a        integração no
                                          integração está   backlog.
                                          prevista para     
                                          etapa posterior.  

  MEL-003           Cobertura de testes   O relatório       Após corrigir a
                                          menciona três     compilação,
                                          classes de teste  ampliar os testes
                                          e ausência de     por risco e
                                          cobertura visível requisito.
                                          para endpoints,   
                                          cadastro          
                                          administrativo,   
                                          elegibilidade     
                                          institucional,    
                                          concorrência e    
                                          integração.       

  MEL-004           Configuração de       O backend usa     Documentar a
                    framework e banco     `HttpServer`; o   arquitetura
                                          POM não declara   executada e
                                          dependências de   evitar assumir
                                          Spring Boot,      que a
                                          driver PostgreSQL persistência está
                                          ou Liquibase. As  integrada.
                                          propriedades      
                                          `spring.*` não    
                                          comprovam conexão 
                                          ativa.            

  MEL-005           Organização de        O método          Respeitar o
                    `ApplicationConfig`   concentra         planejamento e
                                          composição de     não antecipar
                                          objetos e         refatoração sem
                                          registro de       decisão da
                                          rotas. A          equipe.
                                          separação está    
                                          planejada para    
                                          PRJ-05/Sprint 6.  

  MEL-006           Acessibilidade        Alguns campos     Avaliar a
                                          podem não ter     interface contra
                                          rótulos           RNF-07 com
                                          associados por    verificação
                                          `for`/`id`; não   sistemática.
                                          houve auditoria   
                                          completa.         

  MEL-007           Documentação de       A Sprint 1 contém Conferir cada
                    sprint                placeholders para afirmação contra
                                          comandos e        código, testes e
                                          evidências; a     evidências
                                          modelagem pode    atuais.
                                          declarar          
                                          requisitos como   
                                          implementados sem 
                                          cobertura         
                                          integral.         
  ---------------------------------------------------------------------------

## 5. Diagnósticos e limitações informados

  ------------------------------------------------------------------------
  Comando ou verificação               Resultado informado
  ------------------------------------ -----------------------------------
  `mvn -f pom.xml validate`            `BUILD SUCCESS`; validou o POM, mas
                                       não compilou o código.

  `mvn -f pom.xml test`                `BUILD FAILURE` na compilação
                                       principal, com 9 erros reportados.
                                       Nenhum teste foi executado.

  `mvn -f pom.xml package`             `BUILD FAILURE` na compilação dos
                                       testes, com 14 erros de
                                       símbolos/pacotes reportados. Não
                                       devem ser tratados como 14 defeitos
                                       independentes sem análise.

  `node --check front-end/script.js`   Nenhuma saída; o relatório
                                       interpreta isso como sintaxe
                                       JavaScript válida.

  Java Language Server                 Reportou divergências de pacote,
                                       problemas de símbolos/filtros e
                                       import não utilizado em teste.

  Java disponível                      Temurin 25; o POM configura alvo
                                       17. Foi informado aviso relacionado
                                       a `source`/`target` sem
                                       `--release`.

  PostgreSQL                           Não testado. O relatório afirma que
                                       o backend atual não inicia conexão
                                       com banco e usa repositórios em
                                       memória.

  Issues do GitHub                     O relatório indica consulta às
                                       Issues #66--#70; o estado atual
                                       deve ser conferido novamente antes
                                       de alterar documentação.

  Git                                  `git status --short` indicou
                                       arquivos e diretórios como não
                                       rastreados; não foi possível
                                       estabelecer baseline limpo
                                       confiável.
  ------------------------------------------------------------------------

**Limitação:** estes resultados foram recebidos do relatório do Codex e
não foram reproduzidos independentemente durante a elaboração deste
arquivo. Os comandos Maven podem criar ou atualizar artefatos em
`target`; o relatório original afirma que não houve edição manual de
fontes, documentação, branches ou commits.

## 6. Ordem sugerida de tratamento

1.  **Restabelecer compilação:** resolver PROB-001 e repetir a
    compilação/testes.
2.  **Tratar segurança e autorização:** validar e corrigir PROB-002 e
    PROB-003.
3.  **Alinhar regras e requisitos:** revisar PROB-005 e validar os
    campos e fluxos relacionados.
4.  **Preservar integridade:** reproduzir e tratar PROB-008 e PROB-013;
    validar também PROB-004.
5.  **Revisar armazenamento de credenciais e sessões:** avaliar
    PROB-006, PROB-010 e PROB-011.
6.  **Corrigir o comportamento do protótipo:** reproduzir PROB-007 e
    avaliar PROB-012.
7.  **Sincronizar rastreabilidade e arquitetura:** conferir PROB-009 e
    PROB-014 e organizar as melhorias MEL-001 a MEL-007.

A ordem definitiva deve ser aprovada pela equipe e conciliada com o
backlog, as dependências e o escopo de cada sprint.

## 7. Questões a investigar para a Sprint 5 --- Padrões de projeto

Os problemas a seguir podem servir como ponto de partida para
investigação, mas **não justificam automaticamente um padrão de
projeto**.

  -----------------------------------------------------------------------
  Questão                 Problema relacionado    O que verificar antes
                                                  de escolher um padrão
  ----------------------- ----------------------- -----------------------
  A reserva de quantidade PROB-013                Verificar se a solução
  está distribuída ou                             é uma operação atômica,
  apenas não é atômica?                           sincronização,
                                                  transação ou uma
                                                  mudança de design. Não
                                                  assumir que Strategy,
                                                  State ou outro padrão
                                                  resolve concorrência.

  As regras de            PROB-009                Identificar duplicação
  autorização estão                               concreta e avaliar o
  duplicadas ou apenas                            custo de centralização;
  distribuídas entre                              a migração já está
  camadas?                                        registrada em decisão
                                                  anterior.

  Existem regras de       PROB-004/PROB-005       Procurar regras
  validação que variam                            alternativas
  por tipo de operação?                           recorrentes no código
                                                  real. Considerar uma
                                                  solução simples se não
                                                  houver variação
                                                  suficiente para
                                                  justificar um padrão.

  A composição de         MEL-005                 Considerar o
  serviços e rotas é                              planejamento da Sprint
  difícil de manter por                           6 antes de propor
  causa de                                        mudança de escopo.
  responsabilidades                               
  misturadas?                                     
  -----------------------------------------------------------------------

**Critério de decisão:** descrever primeiro o problema concreto
observado no código, comparar alternativas (inclusive não aplicar
padrão), escolher somente se houver benefício demonstrável e vincular a
implementação a testes, Issue/PR e commits reais.

## 8. Controle de atualização deste inventário

Ao revisar cada item, atualizar seu estado usando uma destas opções:

-   **Pendente de validação**
-   **Confirmado**
-   **Em correção**
-   **Corrigido, aguardando teste**
-   **Validado**
-   **Descartado após investigação**

Não marcar um item como corrigido apenas porque o código foi alterado:
registrar também o teste ou a evidência que comprova o resultado. Manter
os IDs `PROB-001` a `PROB-014` estáveis para facilitar a rastreabilidade
entre documentação, Issues, PRs e sprints.
