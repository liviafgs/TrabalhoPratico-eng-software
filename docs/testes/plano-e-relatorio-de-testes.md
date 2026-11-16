# Plano e relatório de testes

**Projeto:** Conexão Solidária  
**Data da execução:** 13/11/2026  
**Build avaliado:** `mvn -f BackEnd/pom.xml test`  
**Resultado da suíte automatizada:** 66 aprovados, 0 falhas, 0 erros, 0 ignorados.

## 1. Objetivo e escopo

Verificar regras dos serviços de cadastro, ofertas, demandas, solicitações, histórico, oportunidades e notificações; registrar os resultados e a relação com os requisitos funcionais e não funcionais.

**Incluído:** testes unitários de serviço com repositórios em memória; verificações manuais da interface contra uma API mock para cadastro de doador/organização/oferta, envio de demanda e solicitação de oferta.

**Fora da execução desta rodada:** autenticação contra o backend real, API integrada ao PostgreSQL, persistência após reinicialização, desempenho, compatibilidade entre navegadores e avaliação formal de acessibilidade/responsividade. A conexão PostgreSQL local recusou as credenciais disponíveis; portanto, nenhum teste abaixo é apresentado como integração real com banco.

## 2. Níveis e tipos

| Nível/tipo | Abrangência | Situação |
|---|---|---|
| Unitário de serviço | Regras de negócio, autorização, filtros, transições e notificações, usando repositórios em memória. | Executado: 66 aprovados. |
| Interface funcional com API simulada | Formulários e chamadas HTTP do frontend para fluxos representativos. | Executado manualmente; não verifica a API nem o banco real. |
| Integração API/PostgreSQL | Inicialização, migrações, operações CRUD e recuperação após reinício. | Bloqueado por credenciais; pendente. |
| Não funcional | Desempenho, navegadores, acessibilidade e responsividade. | Não executado nesta rodada. |

## 3. Funcionalidades e riscos prioritários

| Prioridade | Risco/funcionalidade | Motivo | Cobertura nesta rodada |
|---|---|---|---|
| Alta | Quantidade solicitada exceder o estoque; oferta expirada. | Pode comprometer a disponibilidade e a integridade da doação (`RN-03`, `RN-04`, `RNF-04`). | Coberto por testes unitários. |
| Alta | Perfil não autorizado executar uma ação ou consultar dados alheios. | Exposição ou alteração indevida (`RF-06`, `RNF-02`). | Coberto em serviços; endpoints HTTP não foram testados com servidor real. |
| Alta | Estado inválido no ciclo de solicitação. | Pode registrar retirada ou conclusão fora de ordem (`RF-12`, `RF-13`, `RN-11`). | Coberto por testes unitários. |
| Alta | Persistência ou migração falhar no PostgreSQL. | Cadastros podem não sobreviver à reinicialização. | Não coberto; integração bloqueada por credenciais. |
| Média | Erro em filtros, compatibilidade e visibilidade de demandas/histórico. | Pode ocultar ou exibir itens incorretos (`RF-09`, `RF-14`, `RF-20`, `RF-21`). | Coberto em serviços. |
| Média | Quantidade de demanda incompatível ou sem justificativa. | Pode prejudicar a avaliação de necessidade (`RF-18`, `RF-19`). | Coberto em serviços; fluxo HTTP não foi integrado ao banco. |
| Média | Notificação ausente, duplicada ou visível ao usuário errado. | Afeta comunicação e privacidade (`RF-23`). | Coberto em serviços. |

## 4. Casos de teste

Os identificadores `TC-*` são atribuídos neste relatório. Os passos resumem o Arrange/Act/Assert implementado nos testes JUnit; não existia anteriormente um catálogo formal com esses IDs.

| ID / resultado | Pré-condição | Passos e dados | Resultado esperado | Requisito / evidência |
|---|---|---|---|---|
| TC-01 — PASS | Usuário do perfil estabelecimento, sem cadastro prévio. | Cadastrar estabelecimento com nome, CNPJ, endereço e contato; consultar pelo usuário. | Cadastro é salvo e encontrado pelo usuário correto. | `RF-01`; `CadastroBasicoServiceTest.deveCadastrarEConsultarEstabelecimentoDoUsuario`. |
| TC-02 — PASS | Usuário de instituição e repositórios vazios. | Cadastrar instituição e responsável técnico com dados válidos. | Instituição e vínculo responsável são registrados. | `RF-02`, `RF-03`, `RF-04`; `CadastroBasicoServiceTest.deveCadastrarInstituicaoEResponsavelTecnico`. |
| TC-03 — PASS | Instituição cadastrada. | Habilitar instituição e registrar perfil de atendimento. | Estado de habilitação e perfil de atendimento ficam disponíveis para consulta. | `RF-04`, `RF-15`; `RequisitosNovosServiceTest.deveHabilitarInstituicaoERegistrarPerfilDeAtendimento`. |
| TC-04 — PASS | Usuário doador autorizado. | Cadastrar alimento com nome, categoria e unidade; consultá-lo. | Alimento é criado e retornado na consulta. | `RF-22`; `RequisitosNovosServiceTest.deveCadastrarAlimentoEConsultaLo`. |
| TC-05 — PASS | Instituição apta a cadastrar demanda. | Tentar salvar demanda sem finalidade ou sem número de beneficiários. | Operação é rejeitada com erro de validação. | `RF-16`, `RN-06`; `DemandaCoerenciaServiceTest.deveRecusarDemandaSemFinalidadeOuSemBeneficiarios`. |
| TC-06 — PASS | Demanda com quantidade, beneficiários e consumo per capita válidos. | Calcular referência com base em beneficiários × consumo per capita. | Referência calculada corretamente e coerência avaliada. | `RF-18`, `RN-07`; `DemandaCoerenciaServiceTest.deveCalcularQuantidadeDeReferenciaComConsumoInformadoNaDemanda`. |
| TC-07 — PASS | Demanda excede o limite de coerência. | Tentar salvar sem justificativa e depois com justificativa. | Sem justificativa, rejeitar; com justificativa, permitir. | `RF-19`, `RN-08`, `RN-09`; `DemandaCoerenciaServiceTest.deveExigirJustificativaQuandoQuantidadeEstaAcimaDaReferencia` e `devePermitirDemandaExcepcionalQuandoHaJustificativa`. |
| TC-08 — PASS | Existem demandas de mais de uma instituição e perfis com permissões distintas. | Consultar como instituição, doador e perfil sem permissão; aplicar filtros de alimento, prioridade, prazo e localização. | Respeitar visibilidade por perfil, combinar filtros e rejeitar filtros inválidos. | `RF-06`, `RF-20`, `RNF-02`; `DemandaConsultaServiceTest`. |
| TC-09 — PASS | Existem ofertas com diferentes alimentos, categorias, locais e datas. | Consultar com filtros combinados. | Retornar somente a oferta que corresponde aos filtros. | `RF-08`, `RF-09`; `OfertaDisponibilidadeServiceTest.deveFiltrarOfertasPorAlimentoCategoriaLocalEData`. |
| TC-10 — PASS | Oferta ativa de 10 kg e instituição habilitada. | Solicitar 4 kg, verificar saldo; tentar solicitar 7 kg; confirmar a retirada da primeira solicitação. | Saldo reservado é 6 kg; quantidade acima do saldo é rejeitada; confirmar retirada não duplica o abatimento. | `RF-10`, `RF-11`, `RN-03`, `RNF-04`; `OfertaDisponibilidadeServiceTest.deveAbaterQuantidadeSolicitadaEConfirmada`. |
| TC-11 — PASS | Oferta com prazo de retirada expirado. | Consultar ofertas disponíveis. | Oferta expirada não aparece como disponível. | `RF-08`, `RN-04`; `OfertaDisponibilidadeServiceTest.naoDeveConsultarOfertaExpiradaComoDisponivel`. |
| TC-12 — PASS | Instituição habilitada e oferta ativa de 10 kg. | Solicitar 4 kg; doador aceita e agenda; confirmar retirada; instituição conclui. | Estados seguem `SOLICITADA → ACEITA → AGENDADA → RETIRADA → CONCLUIDA`; saldo final é 6 kg. | `RF-10` a `RF-13`, `RN-11`; `SolicitacaoStatusServiceTest.deveSeguirOFluxoCompletoAteConcluirADoacao`. |
| TC-13 — PASS | Solicitação existente, com doador, instituição solicitante, outro usuário e administrador. | Tentar responder/concluir como perfis não proprietários; repetir como administrador. | Ações não autorizadas retornam 403; proprietário ou administrador permitido conforme regra. | `RF-06`, `RF-12`, `RNF-02`; `SolicitacaoStatusServiceTest.somenteODoadorDaOfertaOuOAdministradorRespondeASolicitacao` e `somenteAInstituicaoSolicitanteOuOAdministradorConcluiADoacao`. |
| TC-14 — PASS | Solicitações de vários usuários em estados diferentes. | Consultar histórico e linha do tempo com perfis distintos; tentar ver solicitação alheia. | Cada perfil vê somente dados permitidos; mudanças de status aparecem em ordem. | `RF-14`, `RN-11`, `RNF-02`; `HistoricoDoacaoServiceTest.cadaPerfilVeApenasOQueLheDizRespeito` e `deveRegistrarCadaMudancaDeStatusNaLinhaDoTempo`. |
| TC-15 — PASS | Oferta, demanda e solicitações com eventos associados a vários usuários. | Gerar eventos, listar notificações, filtrar lidas e marcar uma/todas como lidas. | Notificar destinatários pertinentes; não expor a terceiros; leitura é idempotente. | `RF-23`, `RNF-02`; `NotificacaoServiceTest`. |
| TC-16 — PASS | Ofertas e demandas com alimento, unidade, quantidade, prazo e instituição em estados variados. | Consultar compatibilidade como doador e como instituição. | Retornar somente combinações compatíveis e excluir oferta esgotada/expirada ou demanda vencida/inabilitada. | `RF-21`; `OportunidadeServiceTest`. |
| UI-01 — PASS com mock | Página aberta; API mock responde ao cadastro e às consultas. | Criar conta de doador, cadastrar estabelecimento, criar alimento e publicar oferta. | Formulários enviam chamadas HTTP e a interface confirma publicação. | Verificação manual do frontend; não comprova gravação em PostgreSQL. |
| UI-02 — PASS com mock | Página aberta; mock fornece perfil institucional e oferta. | Criar conta institucional, enviar solicitação de oferta e cadastrar demanda com justificativa. | Interface atualiza a lista/contador e exibe confirmação. | Verificação manual do frontend; o cadastro de instituição não foi exercitado neste cenário e não comprova gravação em PostgreSQL. |
| IT-DB-01 — BLOQUEADO | PostgreSQL disponível com credenciais válidas. | Iniciar backend; aplicar Liquibase; criar registro; reiniciar aplicação; consultar registro. | Migração conclui e registro persiste após reinício. | `RNF-01`, `RNF-04`; não executado: `DB_PASSWORD` não estava configurada e a senha local foi rejeitada. |

## 5. Execução automatizada

Comando executado na raiz do projeto:

```bash
mvn -f BackEnd/pom.xml test
```

| Suíte | Testes | Falhas | Erros | Resultado |
|---|---:|---:|---:|---|
| `OfertaDisponibilidadeServiceTest` | 4 | 0 | 0 | Aprovado |
| `RequisitosNovosServiceTest` | 2 | 0 | 0 | Aprovado |
| `NotificacaoServiceTest` | 10 | 0 | 0 | Aprovado |
| `SolicitacaoStatusServiceTest` | 9 | 0 | 0 | Aprovado |
| `DemandaCoerenciaServiceTest` | 12 | 0 | 0 | Aprovado |
| `DemandaConsultaServiceTest` | 11 | 0 | 0 | Aprovado |
| `CadastroBasicoServiceTest` | 2 | 0 | 0 | Aprovado |
| `HistoricoDoacaoServiceTest` | 8 | 0 | 0 | Aprovado |
| `OportunidadeServiceTest` | 8 | 0 | 0 | Aprovado |
| **Total** | **66** | **0** | **0** | **Aprovado** |

Os testes JUnit usam repositórios em memória. Portanto, o resultado não valida a conexão JDBC, as migrações Liquibase nem a persistência real.

## 6. Matriz de rastreabilidade

| Requisito/regra | Casos relacionados | Situação da evidência |
|---|---|---|
| `RF-01` | TC-01, UI-01 | JUnit e UI mock; sem persistência real. |
| `RF-02`, `RF-03`, `RF-04`, `RF-15`, `RN-01` | TC-02, TC-03 | JUnit aprovado. |
| `RF-05` | UI-01, UI-02 | Apenas UI mock; endpoint de autenticação sem teste JUnit nesta suíte. |
| `RF-06`, `RNF-02` | TC-08, TC-13, TC-14 | JUnit aprovado para regras de serviço. |
| `RF-07`, `RF-22` | TC-04, UI-01 | JUnit e UI mock; sem gravação real em banco validada. |
| `RF-08`, `RF-09`, `RN-04` | TC-09, TC-11 | JUnit aprovado. |
| `RF-10`, `RF-11`, `RN-02`, `RN-03`, `RNF-04` | TC-10, TC-11, TC-12 | JUnit aprovado. |
| `RF-12`, `RF-13`, `RN-11` | TC-12, TC-13, TC-14 | JUnit aprovado. |
| `RF-14` | TC-14 | JUnit aprovado. |
| `RF-16`, `RF-17`, `RF-18`, `RF-19`, `RN-05` a `RN-09`, `RN-12` | TC-05, TC-06, TC-07, TC-08, UI-02 | JUnit aprovado; UI mock parcial. |
| `RF-20` | TC-08 | JUnit aprovado. |
| `RF-21` | TC-16 | JUnit aprovado. |
| `RF-23` | TC-15 | JUnit aprovado. |
| `RNF-01` | — | Inspeção de hash existente, mas sem teste automatizado de persistência nesta suíte. |
| `RNF-03`, `RNF-05` a `RNF-09` | — | Sem execução formal nesta rodada. |
| `RNF-10`, `RNF-11` | Matriz deste relatório e inspeção de código | Evidência documental; não equivale a teste funcional. |
| Persistência PostgreSQL | IT-DB-01 | Bloqueado; pendente de credenciais e nova execução. |

## 7. Critérios de aprovação e reprovação

**Caso aprovado:** resultado observado corresponde ao esperado e todas as asserções passam.  
**Caso reprovado:** qualquer resultado divergente, exceção inesperada ou asserção falha.  
**Suíte unitária aprovada:** execução termina com 0 falhas e 0 erros. Este critério foi atendido: 66/66.  
**Integração aprovada:** só após migrações, criação/leitura e persistência após reinício serem verificadas no PostgreSQL real. Situação atual: bloqueada, não aprovada.  
**Incremento global aprovado:** exige os testes unitários aprovados e os casos de integração críticos sem bloqueios. Situação atual: parcial; não declarar aprovação ponta a ponta.

## 8. Pendências para fechar o relatório

- Configurar `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` válidas e executar `IT-DB-01`.
- Automatizar testes HTTP para cadastro, login, controle de perfil e respostas de erro.
- Repetir os fluxos do frontend contra o backend real, incluindo cadastro institucional, oferta, demanda, solicitação e histórico.
- Definir navegadores/resoluções suportados e executar testes de desempenho, acessibilidade e responsividade.