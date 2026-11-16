# **Trabalho Final de Engenharia de Software - Conexão Solidária**

##  **1. Identificação do Projeto:**
| Campo | Informações |
|---|---|
|Nome do projeto | Conexão Solidária - destinando excedentes alimentares à doação.
|Problema escolhido | Dificuldade na destinação de alimentos excedentes de comércios para instituições de caridade.
|Turma/semestre | Engenharia de Software — 2026/2.
|Professor | Prof. Johnatan Oliveira.
|Link para o projeto no GitHub | https://github.com/liviafgs/TrabalhoPratico-eng-software  |
|Link da aplicação publicada | [em andamento]
|Link do vídeo final | [em andamento]

## **Integrantes:**
|Nome completo | Usuário no GitHub | Responsabilidade principal | Outras contribuições |
|---|---|---|---|
|Letícia Cristhinie Alexandre | @Letícia-Cristhinie | Front-end | ---
|Lívia Fagundes dos Santos | @liviafgs | Front-end | Documentações
|Raíssa Fernandes | @raissafernandesdesouza | Back-end | Documentações
|Rawany Mendonça | @rawanymendonca | Back-end | ---

## **2. Resumo da solução:**
**Problema:** Comércios como supermercados, restaurantes e padarias podem possuir alimentos excedentes que ainda estão próprios para consumo, mas que não serão comercializados. Ao mesmo tempo, instituições de caridade precisam de alimentos para atender pessoas em situação de vulnerabilidade. A falta de uma forma organizada de conectar esses dois públicos pode dificultar as doações e contribuir para o desperdício de alimentos.

**proposta de solução:** Será desenvolvida uma aplicação web que conecta comércios que possuem alimentos excedentes a instituições de caridade interessadas em recebê-los. Os estabelecimentos poderão cadastrar os alimentos disponíveis, enquanto as instituições poderão consultar as doações e demonstrar interesse. A plataforma organizará esse processo, facilitando a comunicação e a destinação dos alimentos.

**Público principal:** Comércios que possuem alimentos excedentes e instituições de caridade que realizam ações de assistência e distribuição de alimentos.

**Funcionalidades prioritárias:**
- Cadastro de comércios e instituições de caridade.
- Cadastro e consulta de alimentos disponíveis para doação.
- Manifestação de interesse e conexão entre instituições e estabelecimentos doadores.
- Acompanhamento do status das doações.

## 3. Execução do backend

O backend requer Java 17, Maven e PostgreSQL. Na inicialização, o Liquibase aplica as migrações e os repositórios persistem os dados no banco `conexao_solidaria`. Configure as credenciais do PostgreSQL no ambiente antes de iniciar:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/conexao_solidaria
export DB_USERNAME=conexao_solidaria
export DB_PASSWORD='sua-senha-do-postgres'
mvn -f BackEnd/pom.xml clean compile exec:java
```

`DB_URL`, `DB_USERNAME` e `DB_PASSWORD` substituem os valores padrão de `BackEnd/src/main/resources/application.properties`. O servidor HTTP inicia na porta 8080 somente após conectar ao banco e executar as migrações.


