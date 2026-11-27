Sprint 8 — Validação final e situação do produto
Data de entrega: 23/11/2026
Pontuação: 2,5 pontos
Tag obrigatória: `sprint-08`
Responsável por conferir este arquivo: Letícia Cristhinie Alexandre
1. Pergunta que esta sprint deve responder
O que foi validado e qual a situação final do Conexão Solidária?
A Sprint 8 consolida a validação final do Conexão Solidária, retomando o plano de testes definido na Sprint 7, registrando o que já possui evidência, o que foi evoluído no código e o que ainda depende de execução e verificação manual. Os resultados históricos da Sprint 7 são identificados como históricos e não são apresentados como uma nova execução da Sprint 8.
2. Objetivo e resultado da sprint
Objetivo planejado:
Executar a validação final do sistema, corrigir defeitos identificados, verificar a integração do front-end com o back-end e a persistência PostgreSQL, e consolidar a situação dos requisitos.
Resultado documentado a partir do repositório:
Testes automatizados existentes: 66 testes, distribuídos em 9 classes.
Último resultado documentado (Sprint 7, 13/11/2026): 66 aprovados, 0 falhas, 0 erros e 0 ignorados. Esse resultado é histórico; uma nova execução na Sprint 8 não está comprovada nesta documentação.
Front-end: há uma implementação em `front-end/app.js` que usa `fetch`, a URL-base `http://localhost:8080`, e token Bearer para chamadas autenticadas. A presença do código não comprova que todos os fluxos estejam funcionando ponta a ponta.
Integração com PostgreSQL: não validada nesta documentação. A Sprint 7 registrou problema de credenciais; a configuração atual precisa ser testada com banco e credenciais reais.
Fluxo completo cadastro → oferta → solicitação → retirada com front-end, back-end e PostgreSQL reais: não há evidência de execução completa anexada.
Defeitos da Sprint 8: não há registro verificável de defeitos e retestes no material consultado. Caso tenham sido encontrados, devem ser registrados.
Requisitos: a matriz abaixo é uma avaliação preliminar baseada na existência de testes e no resultado histórico, não substitui a execução de retestes.
3. Checklist do artefato central — 1,0 ponto
Entrega esperada: `docs/testes/plano-de-testes.md` atualizado com resultados, defeitos, matriz requisito–teste, critérios de aprovação e limitações.
[ ] Resultados automatizados da Sprint 8 executados e preenchidos com a saída real.
[ ] Defeitos identificados e registrados com evidências; confirmar após execução.
[x] Matriz requisito–teste preenchida de forma preliminar.
[ ] Critérios de aprovação conferidos com execução real; PostgreSQL permanece não validado.
[ ] Evidências anexadas (saída do Maven e prints/logs).
[x] Limitações conhecidas documentadas.
Links dos artefatos
Artefato criado/atualizado	Link na tag da sprint	O que mudou
Plano de testes e resultados finais	Link da tag `sprint-08` (a criar) — `docs/testes/plano-de-testes.md`	Resultados da Sprint 8, casos manuais, defeitos e matriz requisito–teste.
Requisitos	Link da tag `sprint-08` (a criar) — `docs/requisitos/requisitos.md`	Situação final dos requisitos após conferir evidências.
Arquitetura	Link da tag `sprint-08` (a criar) — `docs/arquitetura/arquitetura.md`	Registra o estado atual da integração frontend/backend e da persistência.
Sprint 7	Link da tag `sprint-08` (a criar) — `docs/sprints/sprint-07.md`	Ajustes de nomenclatura, rastreabilidade e links.
Testes automatizados	Link da tag `sprint-08` (a criar) — `BackEnd/src/test/java/conexaosolidaria/service/`	Classes de testes já presentes; registrar alterações realizadas nesta sprint, se houver.
Evidências	Link da tag `sprint-08` (a criar) — `docs/testes/evidencias/`	Arquivos de saída dos testes e evidências manuais.
4. Incremento da aplicação web — 1,0 ponto
O que foi implementado ou evoluído
No estado do código presente no material consultado, foi identificada uma implementação de front-end em `front-end/app.js` que:
define a URL-base da API (padrão `http://localhost:8080`);
realiza requisições HTTP com `fetch`;
envia cabeçalho `Authorization: Bearer` quando existe token;
trata falhas de conexão e respostas HTTP de erro;
mantém token de autenticação no `localStorage`.
Esses elementos comprovam que existe código de integração com API, mas não comprovam a execução e aprovação de todos os fluxos integrados. A revisão da camada de integração, eventuais ajustes de mensagens de erro e conferência da interface com API simulada não foram confirmados por evidências nesta documentação; registrar somente se tiverem sido realizados e verificados no histórico.
Como executar e verificar
Na raiz do projeto:
```bash
mvn -f BackEnd/pom.xml test
```
Para iniciar o backend com PostgreSQL, confira as configurações em `BackEnd/src/main/resources/application.properties` e use as credenciais do ambiente local. Não publique senhas reais no documento ou no repositório.
Resultado de testes da Sprint 8: nova execução não confirmada nesta documentação. Não usar os números históricos da Sprint 7 como resultado da Sprint 8.
Último resultado histórico disponível (Sprint 7, 13/11/2026):
Total de testes: 66
Aprovados: 66
Falhas: 0
Erros: 0
Ignorados: 0
Situação final preliminar dos requisitos
Requisito	Situação preliminar	Teste/evidência existente	Observação
RF-01 — Cadastro de estabelecimento	Parcialmente validado	`CadastroBasicoServiceTest` (resultado histórico)	Reexecutar e conferir fluxo pela interface.
RF-06 — Controle de acesso por perfil	Parcialmente validado	`DemandaConsultaServiceTest`, `SolicitacaoStatusServiceTest` (histórico)	Retestar autorização nos endpoints.
RF-08 e RF-09 — Consulta e filtro de ofertas	Parcialmente validado	`OfertaDisponibilidadeServiceTest` (histórico)	Teste manual de filtros necessário.
RF-10 e RF-11 — Solicitação e disponibilidade	Parcialmente validado	`OfertaDisponibilidadeServiceTest` (histórico)	Revalidar bloqueio de quantidade superior ao estoque.
RF-12 e RF-13 — Estados da solicitação e retirada	Parcialmente validado	`SolicitacaoStatusServiceTest` (histórico)	Reexecutar fluxo manual completo.
RF-14 — Histórico de doações	Parcialmente validado	`HistoricoDoacaoServiceTest` (histórico)	Conferir consulta e dados exibidos na interface.
RF-16, RF-18 e RF-19 — Demandas e coerência	Parcialmente validado	`DemandaCoerenciaServiceTest` (histórico)	Reexecutar casos e conferir alertas/justificativa.
RF-20 — Consulta de demandas	Parcialmente validado	`DemandaConsultaServiceTest` (histórico)	Confirmar fluxo pela interface.
RF-21 — Compatibilidade oferta/demanda	Parcialmente validado	`OportunidadeServiceTest` (histórico)	Retestar resultado com dados de exemplo.
RF-23 — Notificações	Parcialmente validado	`NotificacaoServiceTest` (histórico)	Confirmar notificações e visualização na interface.
RNF-01 — Segurança/persistência	Não validado na Sprint 8	CT-05 pendente de nova evidência	Confirmar PostgreSQL e persistência após reiniciar.
RNF-04 — Integridade da quantidade	Parcialmente validado	`OfertaDisponibilidadeServiceTest` (histórico)	Reexecutar teste e fluxo integrado.
RF-02 a RF-05, RF-07, RF-15, RF-17, RF-22	Não validado nesta consolidação	Conferir `RequisitosNovosServiceTest` e demais testes/código	Não atribuir aprovação sem evidência por requisito.
RNF-05 a RNF-09 — Desempenho, usabilidade, acessibilidade, compatibilidade e responsividade	Não validado formalmente	Sem evidência de teste formal identificada	Registrar limitações e, se possível, executar verificações.
Legenda: Atendido, Parcialmente atendido, Não atendido, Não validado. Os estados acima são preliminares e devem ser revisados com os resultados da execução final.
5. Scrum e gestão do trabalho
Sprint Backlog
Issue	Descrição	Responsável	Critério de aceitação/conclusão	Situação
Número de Issue não confirmado	Executar testes finais e consolidar resultados	Letícia Cristhinie Alexandre	66 testes executados e resultado real salvo; confirmar execução.	Pendente de evidência
Número de Issue não confirmado	Validar integração com PostgreSQL	Equipe	Registro permanece disponível após reiniciar a aplicação.	Pendente
Número de Issue não confirmado	Corrigir e retestar defeitos identificados	Equipe	Defeitos registrados, corrigidos quando aplicável e retestados.	Pendente de confirmação
Número de Issue não confirmado	Atualizar documentação (requisitos, arquitetura e plano de testes)	Letícia Cristhinie Alexandre	Documentação atualizada e pendências/limitações explicitadas.	Pendente de confirmação
Acompanhamento
GitHub Project: https://github.com/johnatan-si/tp-eng-software (confirmar se este é o quadro correto).
Issues da Sprint 8: números não confirmados; não inventados.
Reuniões/decisões: não há ata confirmada no material consultado; inserir link se existir.
Impedimentos: validação do PostgreSQL depende de configuração de credenciais e execução em ambiente local.
Mudanças de escopo: não há mudança de escopo registrada no material consultado; confirmar com a equipe.
6. GitHub, documentação e rastreabilidade — 0,5 ponto
Tipo de evidência	Link	O que comprova
Issues da sprint	Issues específicas não confirmadas	Planejamento e acompanhamento.
Pull Request da sprint	PR não confirmado — identificar, se existir	Mudanças revisadas e integradas.
Commits relevantes	Commits não confirmados — consultar histórico	Alterações de código e documentação.
Plano e resultados de testes	Link da tag `sprint-08` após criação	Execuções, defeitos e situação dos requisitos.
Evidências de execução	Link da tag `sprint-08` após criação	Logs, prints e resultados.
Tag `sprint-08`	Link da tag após criação	Versão identificada da entrega.
Rastreabilidade resumida
Requisito	Issue	Documento	Código	Teste/evidência
RF-01, RF-06, RF-08 a RF-14, RF-16, RF-18 a RF-21, RF-23 e RNF-04	Issues não confirmadas	`docs/requisitos/requisitos.md` e `docs/testes/plano-de-testes.md`	Serviços correspondentes no backend	Classes de teste relacionadas; anexar resultados da execução final.
Integração frontend/backend e persistência	Issue não confirmada	`docs/arquitetura/arquitetura.md`	`front-end/app.js`, controllers, services, repositories e configuração do banco	Testes manuais de UI/API e CT-05; preencher com evidências.
7. Defeitos encontrados e tratamento
Não há registro verificável de defeitos identificados, corrigidos ou retestados na Sprint 8 no material consultado. Portanto, a ausência de defeitos não está confirmada.
| ID | Descrição | Severidade | Issue | Commit da correção | Reteste | Situação |
|---|---|---|---|---|---|
| — | Nenhum defeito com evidência suficiente para registro nesta consolidação. | Não definida | Não identificado | Não identificado | Execução/reteste pendentes de confirmação |
8. Revisão do incremento
O que foi demonstrado: o código do front-end possui chamadas `fetch` para a API e tratamento de autenticação por token; o backend contém testes automatizados. A aprovação de fluxos integrados depende de execução/evidências a registrar.
Critérios atendidos: há testes automatizados existentes e um resultado histórico documentado na Sprint 7. Os fluxos principais da Sprint 8 e a persistência real no PostgreSQL não estão validados nesta documentação.
Itens não concluídos ou sem comprovação: nova execução dos testes, verificação real da integração PostgreSQL, fluxo ponta a ponta e evidências, caso não tenham sido realizados.
Motivo das pendências: não há evidência anexada de execução integrada com PostgreSQL, nem nova saída do Maven nesta cópia.
Feedback recebido e ajustes: não foi localizado feedback formal no material consultado.
Limitações conhecidas da versão: PostgreSQL, fluxo ponta a ponta, desempenho, compatibilidade, acessibilidade e responsividade não podem ser declarados validados sem evidências específicas.
9. Retrospectiva
Funcionou bem: existência de um plano de testes e de 66 testes automatizados já registrados na Sprint 7, além de código de front-end com integração HTTP iniciada.
Precisa melhorar: registrar evidências reproduzíveis da execução final, ampliar testes de integração e documentar a confirmação da persistência real.
Lições para projetos futuros: manter evidências e resultados junto ao código e ao plano de testes; não confundir testes unitários com validação integrada; vincular defeitos, correções e retestes a Issues e commits.
10. Links enviados no UFLA Virtual
Tag `sprint-08`: link ainda precisa ser criado após merge na `main`.
Este arquivo na tag: link a inserir depois da criação da tag.
Observação adicional: os resultados históricos e itens não confirmados deste documento devem ser conferidos; não substituir evidências reais por suposições.