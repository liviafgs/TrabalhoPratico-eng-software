# Guia de Desenvolvimento - Conexao Solidaria

## 1. Objetivo

Este documento define os padroes e procedimentos que devem ser seguidos pela equipe durante o desenvolvimento da aplicacao Conexao Solidaria.

O objetivo e manter o projeto organizado, reduzir conflitos entre os integrantes, facilitar a revisao do codigo e garantir rastreabilidade entre Issues, branches, commits e Pull Requests.

As regras deste documento devem ser seguidas por todos os integrantes da equipe.

---

## 2. Tecnologias

### 2.1 Backend

O backend da aplicacao sera desenvolvido utilizando:

- Java
- Spring Boot
- API REST
- Banco de dados relacional

A definicao das bibliotecas complementares e demais tecnologias do backend sera registrada conforme o desenvolvimento do projeto.

### 2.2 Frontend

A tecnologia do frontend ainda nao foi definida pela equipe.

Portanto, neste momento, nao sera estabelecido um framework ou linguagem especifica para o frontend.

A definicao devera ser registrada posteriormente neste documento e nos demais artefatos do projeto quando a decisao for tomada.

---

## 3. Estrategia de branches

O projeto utilizara branches para separar o desenvolvimento das funcionalidades e evitar alteracoes diretamente na versao estavel.

A branch `release` sera considerada a branch principal de desenvolvimento integrado e devera permanecer em condicao funcional.

### 3.1 Regra principal

Toda nova branch deve obrigatoriamente ser criada a partir da branch `release`.

Exemplo:

```bash
git checkout release
git pull origin release
git checkout -b feat/cadastro-instituicao
```

Nao devem ser criadas branches novas a partir de outras branches de funcionalidades, salvo necessidade tecnica previamente acordada pela equipe.

## 4. Branches principais

### 4.1 release

A branch `release` representa a versao integrada do projeto.

Ela deve permanecer estavel e funcional.

Regras:

- Nunca realizar commit diretamente na `release`.
- Toda alteracao deve chegar a `release` por meio de Pull Request.
- O Pull Request deve passar pela revisao e aprovacao dos integrantes responsaveis.
- Uma alteracao somente podera ser mesclada apos todas as aprovacoes necessarias.
- Antes de uma integracao, a branch deve estar em condicao de execucao.

### 4.2 main

A branch `main` representa a versao principal/estavel do projeto.

A utilizacao da `main` devera ocorrer de acordo com a estrategia de versionamento e entrega definida pela equipe.

Durante o desenvolvimento das funcionalidades, a integracao cotidiana ocorrera pela `release`.

## 5. Padrao de nomenclatura das branches

Todas as branches deverao utilizar letras minusculas.

O padrao recomendado e:

```text
prefixo/nome-da-tarefa
```

Exemplos:

```text
feat/cadastro-instituicao
feat/cadastro-estabelecimento
feat/autenticacao
fix/correcao-login
docs/documento-requisitos
test/teste-cadastro
refactor/organizacao-servico
```

### 5.1 Por que utilizar somente letras minusculas?

A equipe adotara nomes de branches somente com letras minusculas para evitar problemas de compatibilidade entre sistemas operacionais e sistemas de arquivos.

Alguns sistemas diferenciam maiusculas e minusculas nos nomes dos arquivos e referencias, enquanto outros podem tratar esses nomes de maneira equivalente.

Por exemplo:

```text
feat/Cadastro
feat/cadastro
```

podem causar ambiguidades ou problemas em determinados ambientes.

Por isso, para manter o projeto consistente e facilitar o trabalho entre diferentes sistemas operacionais, a equipe devera utilizar:

- letras minusculas;
- palavras separadas por hifen;
- sem espacos;
- sem acentos;
- sem caracteres especiais desnecessarios.

Correto:

```text
feat/cadastro-instituicao
```

Incorreto:

```text
Feat/Cadastro Instituicao
```

## 6. Prefixos das branches

Os prefixos identificam o tipo de alteracao realizada.

| Prefixo | Significado | Utilizacao |
| --- | --- | --- |
| `feat` | Feature | Nova funcionalidade |
| `fix` | Fix | Correcao de erro |
| `docs` | Documentation | Alteracao ou criacao de documentacao |
| `test` | Test | Criacao ou alteracao de testes |
| `refactor` | Refactoring | Refatoracao sem mudanca de comportamento |
| `chore` | Chore | Tarefas tecnicas ou manutencao |
| `build` | Build | Alteracoes relacionadas a construcao/configuracao do projeto |
| `ci` | Continuous Integration | Configuracoes de integracao continua |
| `style` | Style | Alteracoes de formatacao ou estilo do codigo |

Exemplos:

Nova funcionalidade:

```text
feat/cadastro-instituicao
```

Correcao:

```text
fix/erro-validacao-cpf
```

Documentacao:

```text
docs/guia-desenvolvimento
```

Testes:

```text
test/teste-cadastro-instituicao
```

Refatoracao:

```text
refactor/organizar-servicos
```

Configuracao tecnica:

```text
chore/configurar-banco
```

## 7. Relacao entre Issue e Branch

Cada tarefa de desenvolvimento devera estar relacionada a uma Issue do GitHub.

O fluxo esperado e:

```text
Issue
   |
   v
Branch
   |
   v
Desenvolvimento
   |
   v
Commit
   |
   v
Push
   |
   v
Pull Request
   |
   v
Revisao
   |
   v
Aprovacoes
   |
   v
Merge na release
```

Sempre que possivel, a branch devera representar uma unica tarefa ou funcionalidade.

Exemplo:

```text
Issue #XX - Implementar cadastro de instituicao
```

Branch:

```text
feat/cadastro-instituicao
```

Isso facilita a identificacao do que esta sendo desenvolvido e permite relacionar a implementacao com a documentacao do projeto.

## 8. Fluxo obrigatorio de desenvolvimento

### 8.1 Primeiro passo - Atualizar a release

Antes de criar uma nova branch, o integrante deve atualizar sua copia local da `release`.

```bash
git checkout release
git pull origin release
```

O comando:

```bash
git checkout release
```

altera para a branch `release`.

O comando:

```bash
git pull origin release
```

baixa as alteracoes mais recentes da `release` para o computador.

## 9. Criar a branch da tarefa

Depois de atualizar a `release`, criar a branch da tarefa:

```bash
git checkout -b feat/nome-da-tarefa
```

Exemplo:

```bash
git checkout -b feat/cadastro-instituicao
```

A branch sera criada a partir da `release` atualizada.

## 10. Desenvolvimento

O desenvolvimento deve ser realizado somente dentro da branch criada para a tarefa.

Durante o desenvolvimento, o integrante deve:

- modificar apenas o que for necessario para a tarefa;
- evitar alteracoes nao relacionadas;
- manter o codigo organizado;
- realizar commits pequenos e claros;
- testar a funcionalidade antes de solicitar integracao.

## 11. Commits

Os commits devem possuir mensagens claras e objetivas.

Recomenda-se utilizar o mesmo padrao de prefixos adotado nas branches.

Exemplos:

```bash
git commit -m "feat: implementa cadastro de instituicao"
git commit -m "fix: corrige validacao do cadastro"
git commit -m "docs: atualiza guia de desenvolvimento"
git commit -m "test: adiciona testes do cadastro"
git commit -m "refactor: reorganiza camada de servico"
```

A mensagem deve explicar de forma objetiva o que foi realizado.

Evitar mensagens genericas como:

- alteracoes
- mudancas
- teste
- coisas
- final

## 12. Atualizacao da branch antes do Pull Request

Antes de abrir um Pull Request, e obrigatorio verificar se a branch `release` recebeu novas alteracoes.

O objetivo e evitar que a branch fique desatualizada e que conflitos sejam descobertos somente durante a integracao.

Primeiro:

```bash
git checkout release
git pull origin release
```

Depois, voltar para a branch da tarefa:

```bash
git checkout feat/nome-da-tarefa
```

Em seguida, integrar a `release` atualizada na branch da tarefa:

```bash
git merge release
```

Exemplo:

```bash
git checkout release
git pull origin release

git checkout feat/cadastro-instituicao
git merge release
```

Se houver conflitos, eles deverao ser corrigidos na propria branch da tarefa.

Depois da resolucao dos conflitos, testar novamente a aplicacao.

## 13. Testes antes do Pull Request

Antes de abrir um Pull Request, o integrante deve garantir que a aplicacao:

- compila;
- inicia corretamente;
- executa a funcionalidade desenvolvida;
- nao apresenta erros conhecidos introduzidos pela alteracao;
- nao possui conflitos de codigo pendentes;
- passou pelos testes necessarios para a tarefa.

E proibido abrir um Pull Request sabendo que a aplicacao esta quebrada ou que a funcionalidade nao esta funcionando.

Um Pull Request com codigo que nao executa corretamente pode introduzir problemas na `release` e dificultar o desenvolvimento dos demais integrantes.

Portanto:

**Nao abrir Pull Request com codigo quebrado, com erro conhecido ou sem realizar os testes necessarios.**

Se a tarefa ainda nao estiver pronta, ela deve continuar na branch de desenvolvimento.

## 14. Enviar a branch para o GitHub

Depois que a funcionalidade estiver desenvolvida e testada:

```bash
git push -u origin feat/nome-da-tarefa
```

Exemplo:

```bash
git push -u origin feat/cadastro-instituicao
```

O push envia a branch e seus commits para o repositorio remoto.

## 15. Pull Request

Depois do push, devera ser aberto um Pull Request no GitHub.

O Pull Request deve sempre seguir o seguinte sentido:

```text
SUA BRANCH -> release
```

Exemplo:

```text
feat/cadastro-instituicao -> release
```

Nunca abrir um Pull Request com destino diferente sem autorizacao da equipe.

### 15.1 O que nao fazer

Nao solicitar:

```text
release -> feat/cadastro-instituicao
```

Nem:

```text
feat/cadastro-instituicao -> main
```

durante o fluxo normal de desenvolvimento.

O objetivo e que as funcionalidades sejam revisadas e integradas primeiro na `release`.

## 16. Padrao de descricao do Pull Request

Todos os Pull Requests deverao utilizar um padrao minimo de descricao.

Modelo:

```markdown
## Descricao

Descreva de forma objetiva o que foi desenvolvido ou alterado.

## Issue relacionada

Closes #XX

## Alteracoes realizadas

- [ ] Alteracao 1
- [ ] Alteracao 2
- [ ] Alteracao 3

## Testes realizados

- [ ] Aplicacao inicia corretamente
- [ ] Funcionalidade principal testada
- [ ] Cenarios de erro testados
- [ ] Testes automatizados executados, quando aplicavel

## Validacao

- [ ] Codigo revisado pelo autor
- [ ] Branch atualizada com a release
- [ ] Conflitos resolvidos
- [ ] Aplicacao funcionando apos a atualizacao
- [ ] Pronto para revisao

## Observacoes

Informe qualquer informacao importante para os revisores.
```

## 17. Revisao do Pull Request

Nenhuma alteracao devera ser mesclada diretamente na `release`.

Apos a abertura do Pull Request, os demais integrantes deverao revisar a alteracao.

A revisao deve verificar, quando aplicavel:

- funcionamento da aplicacao;
- qualidade do codigo;
- organizacao;
- atendimento aos criterios de aceitacao da Issue;
- possiveis efeitos em outras funcionalidades;
- testes;
- documentacao;
- conflitos;
- aderencia aos padroes definidos pelo projeto.

## 18. Aprovacao e Merge

O Pull Request somente podera ser mesclado na `release` depois de obter todas as aprovacoes necessarias definidas pela equipe.

Fluxo:

```text
Desenvolvimento
      |
      v
Push
      |
      v
Pull Request
      |
      v
Revisao
      |
      v
Aprovacoes
      |
      v
Merge
      |
      v
release atualizada
```

O autor da tarefa nao deve considerar a tarefa concluida apenas porque realizou o push.

A tarefa somente sera considerada integrada apos o Merge do Pull Request na `release`.

## 19. Apos o Merge

Depois que o Pull Request for mesclado, a equipe deve atualizar sua `release` local:

```bash
git checkout release
git pull origin release
```

Assim, todos os integrantes passam a trabalhar sobre a versao integrada mais recente.

## 20. Comandos principais

Verificar a branch atual:

```bash
git branch
```

A branch atual sera identificada pelo simbolo `*`.

Atualizar a `release`:

```bash
git checkout release
git pull origin release
```

Criar uma branch:

```bash
git checkout -b feat/nome-da-tarefa
```

Verificar alteracoes:

```bash
git status
```

Adicionar alteracoes:

```bash
git add .
```

Ou, preferencialmente, adicionar arquivos especificos:

```bash
git add caminho/do/arquivo
```

Criar commit:

```bash
git commit -m "feat: descreve a alteracao"
```

Enviar branch para o GitHub pela primeira vez:

```bash
git push -u origin nome-da-branch
```

Nas proximas vezes:

```bash
git push
```

Atualizar a branch com a `release`:

```bash
git checkout release
git pull origin release

git checkout nome-da-sua-branch
git merge release
```

Ver historico de commits:

```bash
git log --oneline
```

## 21. Fluxo completo - exemplo

Suponha que a Issue seja:

```text
#XX - Implementar cadastro de instituicao
```

### 21.1 Atualizar a release

```bash
git checkout release
git pull origin release
```

### 21.2 Criar a branch

```bash
git checkout -b feat/cadastro-instituicao
```

### 21.3 Desenvolver

Realizar as alteracoes necessarias na aplicacao.

### 21.4 Verificar

```bash
git status
```

### 21.5 Criar commit

```bash
git add .
git commit -m "feat: implementa cadastro de instituicao"
```

### 21.6 Enviar para o GitHub

```bash
git push -u origin feat/cadastro-instituicao
```

### 21.7 Antes de abrir o Pull Request

Atualizar a branch com a `release`:

```bash
git checkout release
git pull origin release

git checkout feat/cadastro-instituicao
git merge release
```

### 21.8 Corrigir conflitos, se existirem

Se houver conflitos:

- corrigir os arquivos;
- verificar o codigo;
- executar os testes;
- criar o commit da resolucao;
- realizar o push.

```bash
git add .
git commit -m "fix: resolve conflitos com release"
git push
```

### 21.9 Abrir Pull Request

No GitHub:

```text
feat/cadastro-instituicao -> release
```

### 21.10 Revisao

Os integrantes responsaveis analisam o Pull Request.

### 21.11 Aprovacao

O Pull Request deve receber todas as aprovacoes necessarias.

### 21.12 Merge

Somente apos as aprovacoes o Pull Request podera ser mesclado na `release`.

### 21.13 Atualizar a equipe

Todos devem atualizar sua `release` local:

```bash
git checkout release
git pull origin release
```

## 22. Regra de ouro do projeto

O fluxo padrao do projeto e:

```text
release
   |
   |--> feat/tarefa-1
   |
   |--> feat/tarefa-2
   |
   |--> fix/tarefa-3
   |
   |--> docs/tarefa-4
          |
          v
       commit
          |
          v
        push
          |
          v
    Pull Request
          |
          v
       revisao
          |
          v
      aprovacoes
          |
          v
    merge na release
```

Regras obrigatorias:

- Toda branch deve ser criada a partir da `release`.
- Nao realizar commits diretamente na `release`.
- Utilizar nomes de branches em letras minusculas.
- Utilizar os prefixos definidos neste documento.
- Cada tarefa deve estar relacionada a uma Issue.
- A branch deve representar uma tarefa clara.
- Realizar commits objetivos.
- Fazer push ao finalizar a tarefa.
- Antes do Pull Request, atualizar a `release`.
- Fazer merge da `release` na propria branch de desenvolvimento.
- Corrigir todos os conflitos antes do Pull Request.
- Testar a aplicacao antes de abrir o Pull Request.
- Nao abrir Pull Request com codigo quebrado ou com erros conhecidos.
- O Pull Request deve ser sempre da branch da tarefa para `release`.
- Toda alteracao deve ser revisada.
- O Merge somente deve ocorrer apos todas as aprovacoes necessarias.
- Apos o Merge, todos devem atualizar sua `release` local.

## 23. Objetivo do processo

Esse fluxo tem como objetivo:

- evitar conflitos entre os integrantes;
- impedir alteracoes diretas na versao integrada;
- facilitar a identificacao de cada tarefa;
- manter o historico do projeto organizado;
- facilitar a revisao do codigo;
- relacionar Issues, branches, commits e Pull Requests;
- reduzir a possibilidade de codigo quebrado chegar a `release`;
- permitir que varios integrantes desenvolvam funcionalidades simultaneamente;
- manter uma versao integrada e funcional do sistema.

A organizacao do versionamento faz parte do processo de desenvolvimento do projeto e deve ser seguida durante todas as Sprints.