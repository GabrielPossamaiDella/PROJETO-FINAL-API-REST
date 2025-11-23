#  Projeto Final - API de Gestão Financeira Pessoal



Este repositório contém o código-fonte e a documentação do projeto final da disciplina de BACK-END. A aplicação é uma API RESTful completa para controle de finanças pessoais, permitindo aos usuários gerenciar suas receitas e despesas de forma segura.

##  Integrantes da Equipe

* **Augusto Benedetti**
* **Gabriel Possamai**
* **Murilo Mandelli**

---

##  Tema do Projeto

O tema escolhido para o desenvolvimento da API foi o de **Finanças e Investimentos**. A solução proposta é uma API que permite a um usuário controlar suas receitas e despesas, categorizá-las e obter insights sobre sua vida financeira.

##  Tecnologias Utilizadas

Este projeto foi desenvolvido utilizando a seguinte stack:

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3.5.7
    * **Spring Data JPA:** Para persistência de dados.
    * **Spring Web:** Para a construção dos endpoints RESTful.
    * **Spring Security:** Autenticação e Autorização robusta.
    * **JWT (JSON Web Token):** Para segurança stateless via tokens.
    * **Spring Boot Validation:** Validação de dados (@NotNull, @NotBlank).
* **Banco de Dados:** PostgreSQL
* **Documentação:** Springdoc OpenAPI (Swagger UI)
* **Utilitários:** Lombok

---

##  Como Executar o Projeto

Para rodar a aplicação na sua máquina, siga os passos abaixo:

### Pré-requisitos
* Java 21 instalado.
* PostgreSQL instalado e rodando (com um banco de dados chamado `financas_db`).

### Passo a Passo

1.  **Clonar o repositório:**
    ```bash
    git clone https://github.com/GabrielPossamaiDella/PROJETO-FINAL-API-REST
    cd financas
    ```

2.  **Configurar o Banco de Dados:**
    * Abra o arquivo `src/main/resources/application.properties`.
    * Verifique se as credenciais (`username` e `password`) do PostgreSQL correspondem às da sua máquina.

3.  **Executar a Aplicação:**
    * No terminal, dentro da pasta do projeto, execute:
    ```bash
    ./mvnw spring-boot:run
    ```

4.  **Acessar a Documentação (Swagger):**
    * Com a aplicação rodando, acesse no seu navegador:
    *  **http://localhost:8080/swagger-ui.html**

---

##  Documentação da API e Rotas

Abaixo estão descritos os principais endpoints. **Nota:** A maioria das rotas exige autenticação via Token Bearer (obtido no login).

###  Autenticação (`/auth`)

| Método | Rota | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/register` | Registra um novo usuário. | Público |
| `POST` | `/auth/login` | Autentica o usuário e retorna o **Token JWT**. | Público |

###  Categorias (`/categories`)

| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `POST` | `/categories` | Cria uma nova categoria para o usuário logado. |
| `GET` | `/categories` | Lista todas as categorias do usuário logado. |
| `GET` | `/categories/{id}` | Busca uma categoria específica. **(Incrementa o contador de popularidade)**. |
| `PUT` | `/categories/{id}` | Atualiza nome ou descrição da categoria. |
| `DELETE` | `/categories/{id}` | Remove uma categoria (se não estiver em uso). |

###  Transações (`/transactions`)

| Método | Rota | Descrição |
| :--- | :--- | :--- |
| `POST` | `/transactions` | Cria uma nova receita ou despesa. |
| `GET` | `/transactions` | Lista transações com suporte a **filtros** (data, tipo, categoria). |
| `PUT` | `/transactions/{id}` | Atualiza uma transação existente. |
| `DELETE` | `/transactions/{id}` | Remove uma transação. |

---

##  Implementação da Carta-Desafio

**Desafio Sorteado:** "Ranking de Popularidade"

> *Requisito: Uma entidade deve possuir uma propriedade de número de acessos. Para cada GET, a entidade deve ser incrementada em 1.*

**Solução Implementada:**
1.  Alteramos a entidade **`Category`** adicionando o campo `accessCount` (inicia em 0).
2.  No `CategoryService`, toda vez que o método `getOneCategory(id)` é chamado (via `GET /categories/{id}`), o sistema:
    * Verifica se a categoria pertence ao usuário.
    * Incrementa o `accessCount` em +1.
    * Salva a alteração no banco automaticamente.
3.  Isso permite identificar quais categorias são mais consultadas pelo usuário.

---

##  Segurança e Regras de Negócio

O projeto implementa regras estritas de segurança:

1.  **Isolamento de Dados:** Um usuário **jamais** consegue ver, editar ou excluir categorias e transações de outro usuário. O sistema filtra tudo com base no ID do usuário logado.
2.  **Proteção de Rotas:** Apenas as rotas de Login e Registro são públicas. Todas as outras exigem um Token JWT válido no Header `Authorization`.
3.  **Tratamento de Erros:** A API possui um manipulador global de exceções (`GlobalExceptionHandler`) que retorna mensagens JSON claras e códigos HTTP corretos (404, 403, 400) em vez de erros genéricos.