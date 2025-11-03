# Projeto Final - API de Gestão Financeira Pessoal

Este repositório contém o código-fonte e a documentação do projeto final da disciplina de BACK-END.
O objetivo deste projeto é projetar e desenvolver uma API RESTful completa, estruturada e funcional para um sistema de gerenciamento financeiro pessoal, aplicando as boas práticas de desenvolvimento backend.

##  Tema do Projeto

O tema escolhido para o desenvolvimento da API foi o de **Finanças e Investimentos**.

##  Integrantes da Equipe

* Augusto Benedetti
* Gabriel Possamai
* Murilo Mandelli

---

## Documentação da API - Modelos e Funcionalidades

Este documento detalha os modelos de dados (entidades) e as principais funcionalidades da API de Gestão Financeira Pessoal.

### 1. Modelos de Dados (Entidades)

A API irá operar sobre três entidades centrais que se relacionam para representar os dados do domínio de finanças pessoais.

#### 1.1. Entidade `User`

Representa o usuário do sistema. Cada usuário é o "dono" de suas próprias categorias e transações, garantindo o isolamento dos dados.

| Atributo | Tipo de Dado | Descrição | Restrições / Regras |
| :--- | :--- | :--- | :--- |
| `id` | `Long` | Identificador único numérico (Chave Primária). | Gerado automaticamente. |
| `name` | `String` | Nome completo do usuário. | Obrigatório. |
| `email` | `String` | Endereço de e-mail para login e comunicação. | Obrigatório, formato de e-mail válido, único. |
| `password` | `String` | Senha de acesso do usuário. | Obrigatório, será armazenada com hash. |
| `createdAt` | `DateTime` | Data e hora do registro do usuário. | Gerado automaticamente na criação. |
| `updatedAt` | `DateTime` | Data e hora da última atualização do registro. | Atualizado automaticamente. |

#### 1.2. Entidade `Category`

Representa as categorias que um usuário pode criar para classificar suas transações (ex: Salário, Moradia, Alimentação, Lazer).

| Atributo | Tipo de Dado | Descrição | Restrições / Regras |
| :--- | :--- | :--- | :--- |
| `id` | `Long` | Identificador único numérico (Chave Primária). | Gerado automaticamente. |
| `name` | `String` | Nome da categoria (ex: "Alimentação"). | Obrigatório, único por usuário. |
| `description` | `String` | Descrição opcional para a categoria. | Opcional. |
| `type` | `Enum` | Tipo da categoria: `INCOME` (Receita) ou `EXPENSE` (Despesa). | Obrigatório. |
| `userId` | `Long` | Chave Estrangeira que referencia o `User` dono da categoria. | Obrigatório (Relação N:1 com User). |
| `accessCount` | `Long` | Contador de acessos (Req. Carta-Desafio). | Inicia com `0`. Incrementado a cada `GET /categories/{id}`. |

#### 1.3. Entidade `Transaction`

Representa uma movimentação financeira, seja uma entrada (receita) ou uma saída (despesa), realizada por um usuário e associada a uma categoria.

| Atributo | Tipo de Dado | Descrição | Restrições / Regras |
| :--- | :--- | :--- | :--- |
| `id` | `Long` | Identificador único numérico (Chave Primária). | Gerado automaticamente. |
| `description` | `String` | Descrição da transação (ex: "Almoço no restaurante X"). | Obrigatório. |
| `amount` | `Double` | Valor monetário da transação. | Obrigatório, deve ser maior que zero. |
| `date` | `Date` | Data em que a transação ocorreu. | Obrigatório. |
| `type` | `Enum` | Tipo da transação: `INCOME` (Receita) ou `EXPENSE` (Despesa). | Obrigatório. |
| `userId` | `Long` | Chave Estrangeira que referencia o `User` dono da transação. | Obrigatório (Relação N:1 com User). |
| `categoryId` | `Long` | Chave Estrangeira que referencia a `Category` da transação. | Obrigatório (Relação N:1 com Category). |

### 2. Principais Funcionalidades da API

A seguir, são descritas as funcionalidades macro que a API irá prover, agrupadas por contexto.

#### 2.1. Autenticação e Gerenciamento de Usuários

* **Cadastro de Usuários:** Permitir que um novo usuário se registre na plataforma fornecendo nome, e-mail e senha.
* **Autenticação de Usuários:** Permitir que um usuário registrado faça login para obter um token de acesso (JWT), que será usado para autorizar o acesso às demais funcionalidades.
* **Gerenciamento de Perfil:** Permitir que o usuário autenticado visualize e atualize suas próprias informações de perfil (nome, e-mail, senha).

#### 2.2. Gerenciamento de Categorias

* **Criação de Categorias:** O usuário autenticado poderá criar novas categorias de receita ou despesa.
* **Listagem de Categorias:** O usuário poderá listar todas as suas categorias cadastradas. Esta listagem permitirá a ordenação por popularidade (campo `accessCount`), conforme a Carta-Desafio.
* **Detalhe de Categoria:** O usuário poderá visualizar os detalhes de uma categoria específica. Cada chamada a esta funcionalidade incrementará o contador de popularidade (`accessCount`) da categoria.
* **Atualização de Categorias:** O usuário poderá alterar o nome ou a descrição de suas categorias.
* **Exclusão de Categorias:** O usuário poderá excluir categorias que não são mais necessárias (regras de negócio para categorias em uso deverão ser tratadas).

#### 2.3. Gerenciamento de Transações

* **Registro de Transações:** O usuário autenticado poderá registrar novas transações (receitas ou despesas), associando-as a uma de suas categorias.
* **Listagem e Filtragem de Transações:** Funcionalidade central da API, onde o usuário poderá listar todas as suas transações com suporte a:
    * **Paginação:** Para lidar com grandes volumes de dados.
    * **Ordenação:** Por data, valor, etc.
    * **Filtros:** Por período (data de início e fim), por tipo (receita/despesa) e por categoria.
* **Detalhe de Transação:** O usuário poderá visualizar os detalhes completos de uma transação específica.
* **Atualização de Transações:** O usuário poderá corrigir informações de uma transação já registrada.
* **Exclusão de Transações:** O usuário poderá remover uma transação lançada incorretamente.

---

## Arquitetura REST e Mapeamento de Funcionalidades

Este documento define a arquitetura REST da API, detalhando as rotas (endpoints), os verbos HTTP associados e os códigos de resposta esperados.

### 1. Códigos de Resposta HTTP Padrão

Para manter a consistência, os seguintes códigos de erro serão usados em toda a API:

* `200 OK`: Requisição bem-sucedida (ex: `GET`, `PUT`).
* `201 Created`: Recurso criado com sucesso (ex: `POST`).
* `204 No Content`: Requisição bem-sucedida, sem conteúdo para retornar (ex: `DELETE`).
* `400 Bad Request`: A requisição do cliente é inválida ou mal formada (ex: falha na validação de DTOs, falta de campos obrigatórios).
* `401 Unauthorized`: O cliente não está autenticado. A requisição exige um token JWT válido.
* `403 Forbidden`: O cliente está autenticado, mas não tem permissão para acessar o recurso solicitado (ex: tentar acessar dados de outro usuário).
* `404 Not Found`: O recurso solicitado não foi encontrado (ex: `GET /transactions/999` onde o ID 999 não existe).
* `409 Conflict`: A requisição não pôde ser concluída devido a um conflito com o estado atual do recurso (ex: tentar criar um usuário com um e-mail que já existe, ou deletar um recurso em uso).
* `500 Internal Server Error`: Um erro inesperado ocorreu no servidor.

### 2. Mapeamento de Rotas (Endpoints)

As rotas são agrupadas por entidade/módulo de funcionalidade. Todas as rotas, exceto `/auth/register` e `/auth/login`, são protegidas e exigem autenticação via token JWT.

#### Módulo: Autenticação e Usuários (`/auth`, `/users`)

Rotas responsáveis pelo cadastro, login e gerenciamento do perfil do usuário.

| Funcionalidade | Verbo | Rota (Endpoint) | Códigos de Sucesso | Códigos de Erro |
| :--- | :--- | :--- | :--- | :--- |
| Registrar novo usuário | `POST` | `/auth/register` | `201 Created` | `400 Bad Request`, `409 Conflict` (e-mail já existe) |
| Autenticar usuário (Login) | `POST` | `/auth/login` | `200 OK` (retorna o token JWT) | `400 Bad Request`, `401 Unauthorized` (credenciais inválidas) |
| Buscar perfil do usuário | `GET` | `/users/me` | `200 OK` | `401 Unauthorized` |
| Atualizar perfil do usuário | `PUT` | `/users/me` | `200 OK` | `400 Bad Request`, `401 Unauthorized`, `409 Conflict` |
| Deletar conta do usuário | `DELETE` | `/users/me` | `204 No Content` | `401 Unauthorized` |

#### Módulo: Categorias (`/categories`)

Rotas para o gerenciamento das categorias de transações. **Este módulo implementa a Carta-Desafio "Ranking de Popularidade".**

| Funcionalidade | Verbo | Rota (Endpoint) | Códigos de Sucesso | Códigos de Erro |
| :--- | :--- | :--- | :--- | :--- |
| Criar nova categoria | `POST` | `/categories` | `201 Created` | `400 Bad Request`, `401 Unauthorized` |
| Listar todas as categorias | `GET` | `/categories` | `200 OK` | `401 Unauthorized` |
| Buscar categoria por ID | `GET` | `/categories/{id}` | `200 OK` | `401 Unauthorized`, `403 Forbidden`, `404 Not Found` |
| Atualizar uma categoria | `PUT` | `/categories/{id}` | `200 OK` | `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found` |
| Deletar uma categoria | `DELETE` | `/categories/{id}` | `204 No Content` | `401 Unauthorized`, `403 Forbidden`, `404 Not Found`, **`409 Conflict`** |

**Detalhamento de Requisitos (Categorias)**

O endpoint de listagem `GET /categories` atende aos seguintes requisitos:

* **Paginação:** `?page=0&size=10`
* **Ordenação:** `?sort=name,asc` (ordena por nome)
* **Filtros:** `?name=Lazer&type=EXPENSE` (filtra por nome e/ou tipo)
* **Carta-Desafio (Ranking de Popularidade):** A ordenação pode ser feita pelo campo de popularidade: `?sort=accessCount,desc`

O endpoint de busca `GET /categories/{id}` atende à Carta-Desafio:

* **Carta-Desafio (Ranking de Popularidade):** Cada vez que este endpoint é chamado com sucesso, o atributo `accessCount` da categoria correspondente é **incrementado em 1**.

O endpoint de exclusão `DELETE /categories/{id}` possui uma regra de negócio específica:

* **Regra de Integridade:** Um usuário não pode excluir uma categoria que já esteja associada a uma ou mais transações. Caso isso seja tentado, a API deve retornar um erro `409 Conflict` para preservar a integridade dos dados.

#### Módulo: Transações (`/transactions`)

Rotas para o gerenciamento das transações financeiras (receitas e despesas) do usuário.

| Funcionalidade | Verbo | Rota (Endpoint) | Códigos de Sucesso | Códigos de Erro |
| :--- | :--- | :--- | :--- | :--- |
| Registrar nova transação | `POST` | `/transactions` | `201 Created` | `400 Bad Request`, `401 Unauthorized` |
| Listar todas as transações | `GET` | `/transactions` | `200 OK` | `400 Bad Request` (filtro inválido), `401 Unauthorized` |
| Buscar transação por ID | `GET` | `/transactions/{id}` | `200 OK` | `401 Unauthorized`, `403 Forbidden`, `404 Not Found` |
| Atualizar uma transação | `PUT` | `/transactions/{id}` | `200 OK` | `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found` |
| Deletar uma transação | `DELETE` | `/transactions/{id}` | `204 No Content` | `401 Unauthorized`, `403 Forbidden`, `404 Not Found` |

**Detalhamento de Requisitos (Transações)**

O endpoint de listagem `GET /transactions` atende aos seguintes requisitos obrigatórios:

* **Paginação:** `?page=0&size=20` (permite ao usuário definir o número da página e o tamanho).
* **Ordenação:** `?sort=date,desc` (permite ordenar por qualquer campo, ex: data, valor).
* **Filtros de Busca:**
    * Por período: `?startDate=2025-10-01&endDate=2025-10-31`
    * Por tipo: `?type=INCOME` ou `?type=EXPENSE`
    * Por categoria: `?categoryId=1`
    * Por descrição: `?description=Almoço`