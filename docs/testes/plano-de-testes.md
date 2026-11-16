# Plano de testes

## 1. Objetivos e escopo

O objetivo dos testes é verificar se as principais funcionalidades do sistema Conexão Solidária estão funcionando corretamente, garantindo que os usuários consigam realizar cadastros, publicar ofertas e demandas de alimentos, solicitar doações e acompanhar o andamento das solicitações.

Os testes são prioritários porque o sistema precisa garantir a integridade das informações, o controle das quantidades disponíveis e a segurança das operações realizadas pelos diferentes perfis de usuários.

**O que será testado:**
- Cadastro de doadores, estabelecimentos e instituições.
- Cadastro e publicação de alimentos e ofertas.
- Cadastro e consulta de demandas.
- Solicitação e confirmação de retirada de alimentos.
- Controle de permissões de acesso.
- Histórico de doações e notificações.
- Compatibilidade entre ofertas e demandas.

**O que não será testado neste ciclo:**
- Desempenho do sistema com muitos usuários simultâneos.
- Compatibilidade com diferentes navegadores.
- Testes formais de acessibilidade e responsividade.
- Integração completa com PostgreSQL, devido a problemas de configuração das credenciais disponíveis.

## 2. Estratégia

| Nível/tipo | Objetivo | Ferramenta/método | Responsável | Evidência esperada |
|---|---|---|---|---|
| Unidade | Verificar o funcionamento das regras de negócio, cadastros, ofertas, demandas e solicitações. | JUnit e Maven, utilizando repositórios em memória. | Equipe de desenvolvimento | Relatório dos testes automatizados. |
| Integração | Verificar a comunicação entre frontend, backend e banco de dados. | Testes manuais com API simulada e testes de integração com PostgreSQL. | Equipe de desenvolvimento | Registros das chamadas e resultados dos testes. |
| Sistema/aceitação | Verificar os principais fluxos de utilização do sistema, considerando os diferentes perfis de usuários. | Testes manuais na interface e validação dos resultados apresentados. | Equipe de testes | Evidências dos fluxos executados e resultados obtidos. |

## 3. Riscos e prioridades de teste

| Risco/funcionalidade | Probabilidade | Impacto | Prioridade de teste | Requisitos |
|---|---|---|---|---|
| Solicitação de quantidade de alimentos superior ao estoque disponível. | Alta | Alta | 1 | RF-10, RF-11, RN-03 |
| Usuários sem autorização acessarem informações ou realizarem ações indevidas. | Média | Alta | 1 | RF-06, RNF-02 |
| Falhas na atualização dos estados das solicitações de doação. | Média | Alta | 1 | RF-12, RF-13, RN-11 |
| Ofertas expiradas continuarem aparecendo como disponíveis. | Média | Alta | 1 | RF-08, RN-04 |
| Falhas na persistência dos dados no PostgreSQL. | Média | Alta | 1 | RNF-01, RNF-04 |
| Problemas nos filtros de ofertas e demandas. | Média | Média | 2 | RF-09, RF-20, RF-21 |
| Falhas no envio ou na visualização de notificações. | Média | Média | 2 | RF-23 |
| Cadastro de demandas com informações inconsistentes. | Média | Média | 2 | RF-16, RF-18, RF-19 |

## 4. Casos de teste

### CT-01 — Cadastro de estabelecimento

- **Requisitos:** RF-01.
- **Pré-condições:** Usuário com perfil de estabelecimento, sem cadastro anterior.
- **Dados de teste:** Nome, CNPJ, endereço e informações de contato.
- **Passos:**
  1. Acessar a funcionalidade de cadastro.
  2. Preencher os campos obrigatórios com informações válidas.
  3. Confirmar o cadastro.
  4. Consultar o estabelecimento cadastrado.
- **Resultado esperado:** O estabelecimento deve ser cadastrado corretamente e aparecer na consulta vinculada ao usuário correspondente.
- **Tipo:** Automatizado.
- **Arquivo de automação:** `CadastroBasicoServiceTest.java`.

### CT-02 — Solicitação de quantidade superior ao estoque

- **Requisitos:** RF-10, RF-11, RN-03 e RNF-04.
- **Pré-condições:** Existência de uma oferta ativa com quantidade disponível de 10 kg.
- **Dados de teste:** Solicitação inicial de 4 kg e tentativa posterior de solicitar mais 7 kg.
- **Passos:**
  1. Criar uma oferta com 10 kg disponíveis.
  2. Realizar uma solicitação de 4 kg.
  3. Consultar a quantidade restante.
  4. Tentar realizar outra solicitação de 7 kg.
- **Resultado esperado:** O sistema deve atualizar o saldo para 6 kg e rejeitar a segunda solicitação, pois a quantidade ultrapassa o estoque disponível.
- **Tipo:** Automatizado.
- **Arquivo de automação:** `OfertaDisponibilidadeServiceTest.java`.

### CT-03 — Fluxo de solicitação e conclusão de doação

- **Requisitos:** RF-10, RF-12, RF-13 e RN-11.
- **Pré-condições:** Existência de uma instituição habilitada e de uma oferta ativa.
- **Dados de teste:** Oferta de 10 kg e solicitação de 4 kg.
- **Passos:**
  1. A instituição solicita 4 kg de alimentos.
  2. O doador aceita a solicitação.
  3. O doador realiza o agendamento.
  4. A retirada é confirmada.
  5. A instituição conclui a doação.
- **Resultado esperado:** A solicitação deve seguir corretamente os estados SOLICITADA, ACEITA, AGENDADA, RETIRADA e CONCLUÍDA, mantendo o saldo de 6 kg.
- **Tipo:** Automatizado.
- **Arquivo de automação:** `SolicitacaoStatusServiceTest.java`.

### CT-04 — Verificação de permissões de acesso

- **Requisitos:** RF-06 e RNF-02.
- **Pré-condições:** Existência de usuários com diferentes perfis e permissões.
- **Dados de teste:** Usuário doador, instituição, usuário sem autorização e administrador.
- **Passos:**
  1. Acessar uma solicitação utilizando um usuário sem permissão.
  2. Tentar realizar alterações na solicitação.
  3. Repetir a operação utilizando o perfil autorizado.
- **Resultado esperado:** O sistema deve impedir ações não autorizadas e permitir as operações somente aos usuários com as permissões necessárias.
- **Tipo:** Automatizado.
- **Arquivo de automação:** `SolicitacaoStatusServiceTest.java`.

### CT-05 — Verificação da persistência no PostgreSQL

- **Requisitos:** RNF-01 e RNF-04.
- **Pré-condições:** Banco PostgreSQL disponível e credenciais configuradas corretamente.
- **Dados de teste:** Cadastro de um registro para verificar sua persistência.
- **Passos:**
  1. Iniciar o backend.
  2. Executar as migrações do banco de dados.
  3. Criar um novo registro.
  4. Reiniciar a aplicação.
  5. Consultar o registro criado anteriormente.
- **Resultado esperado:** O sistema deve realizar as operações no banco de dados e manter os registros após a reinicialização.
- **Tipo:** Manual.
- **Arquivo de automação:** Não aplicável nesta execução, pois o teste está pendente devido a problemas de configuração das credenciais do PostgreSQL.

## 5. Critérios de aprovação

- Os testes automatizados devem ser executados sem falhas ou erros.
- As funcionalidades prioritárias devem apresentar os resultados esperados.
- O sistema deve impedir solicitações de alimentos em quantidade superior ao estoque disponível.
- As permissões de acesso devem ser respeitadas.
- As etapas das solicitações devem seguir a ordem estabelecida pelo sistema.
- Nenhum defeito crítico deve permanecer sem identificação ou tratamento.
- Os testes de integração com PostgreSQL somente poderão ser considerados aprovados após a confirmação da persistência real dos dados.

## 6. Primeiras execuções da Sprint 7

| Caso | Data/commit | Resultado | Evidência | Defeito relacionado |
|---|---|---|---|---|
| CT-01 | 13/11/2026 | Aprovado | CadastroBasicoServiceTest | N/A |
| CT-02 | 13/11/2026 | Aprovado | OfertaDisponibilidadeServiceTest | N/A |
| CT-03 | 13/11/2026 | Aprovado | SolicitacaoStatusServiceTest | N/A |
| CT-04 | 13/11/2026 | Aprovado | SolicitacaoStatusServiceTest | N/A |
| CT-05 | 13/11/2026 | Bloqueado | Problema de configuração das credenciais PostgreSQL | Pendente |
| UI-01 | 13/11/2026 | Aprovado com API simulada | Testes manuais da interface | N/A |
| UI-02 | 13/11/2026 | Aprovado com API simulada | Testes manuais da interface | N/A |

**Resultado geral da execução automatizada:**

- Total de testes: 66.
- Aprovados: 66.
- Falhas: 0.
- Erros: 0.
- Ignorados: 0.

Os testes automatizados foram executados utilizando JUnit e repositórios em memória. Os testes da interface foram realizados manualmente com uma API simulada. Portanto, os resultados não comprovam a integração completa com o banco PostgreSQL.

## 7. Comandos de execução

Para executar os testes automatizados, é necessário ter o Java e o Maven instalados e executar o comando na raiz do projeto:

```bash
mvn -f BackEnd/pom.xml test
```

O Maven realiza o gerenciamento das dependências necessárias para a execução dos testes.

**Observação:** A integração com PostgreSQL ainda precisa ser configurada e testada. Para isso, devem ser definidas as variáveis de ambiente `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` com credenciais válidas.
