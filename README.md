# Imobiliária — Sistema de Compra e Venda de Imóveis

Trabalho de Desenvolvimento Web (DC/UFSCar). Aplicação Spring Boot com Spring MVC,
Spring Security, JPA/Hibernate e Thymeleaf para gestão de imóveis, clientes,
imobiliárias e propostas de compra.

---

## 1. Tecnologias

| Item      | Versão / Detalhe                                                                    |
| --------- | ----------------------------------------------------------------------------------- |
| Linguagem | Java **17**                                                                         |
| Framework | Spring Boot **4.0.6** (Spring MVC, Security, Data JPA, Thymeleaf, Mail, Validation) |
| Build     | Maven (wrapper `mvnw` / `mvnw.cmd` incluso)                                         |
| **SGBD**  | **MySQL 8.x** (driver `mysql-connector-j`)                                          |
| Front-end | Thymeleaf + Tailwind CSS, jQuery                                                    |
| Servidor  | Tomcat embarcado (porta padrão **8080**)                                            |

---

## 2. Banco de dados

- **SGBD utilizado:** MySQL 8.
- **Nome do banco:** `Imobiliaria`
- **Criação automática do banco:** a URL de conexão usa
  `createDatabaseIfNotExist=true`, ou seja, **o banco é criado automaticamente**
  na primeira execução caso não exista.
- **Criação automática do schema (tabelas):** a propriedade
  `spring.jpa.hibernate.ddl-auto=create` faz o Hibernate **recriar todas as
  tabelas a cada inicialização** a partir das entidades JPA.

> ⚠️ **Não há scripts SQL manuais para executar.** O schema (tabelas) e a carga
> inicial de dados são gerados automaticamente pela aplicação ao subir. Como
> `ddl-auto=create`, **os dados são apagados e recriados a cada execução**.

### Configuração da conexão

Em [src/main/resources/application.properties](src/main/resources/application.properties):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/Imobiliaria?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=1234
```

Ajuste `username` e `password` conforme o seu MySQL local antes de executar.

### Modelo de dados (resumo)

Herança JPA `JOINED` a partir de `Usuario`:

- **Usuario** (tabela base) — `email`, `password` (BCrypt), `role`, `enabled`.
- **Cliente** _(estende Usuario)_ — CPF, nome, telefone, sexo, data de nascimento.
- **Imobiliaria** _(estende Usuario)_ — CNPJ, nome, descrição.
- **Imovel** — endereço, cidade, descrição, preço, imobiliária dona; possui fotos.
- **FotoImovel**, **Cidade**, **PropostaCompra** (proposta de um cliente para um imóvel).

---

## 3. Papéis (roles) e permissões

As roles são definidas no enum `Role` e aplicadas em
[WebSecurityConfig.java](src/main/java/br/ufscar/dc/dsw/imobiliaria/config/WebSecurityConfig.java):

| Papel              | Permissões principais                                                                           |
| ------------------ | ----------------------------------------------------------------------------------------------- |
| `ROLE_ADMIN`       | CRUD de clientes, imobiliárias e usuários; listagens administrativas de imóveis e propostas.    |
| `ROLE_IMOBILIARIA` | Cadastra/edita/exclui os próprios imóveis (`/imoveis/meus`); avalia propostas dos seus imóveis. |
| `ROLE_CLIENTE`     | Cria, lista e altera apenas as suas próprias propostas em aberto.                               |
| _(público)_        | Catálogo de imóveis (`/imoveis/catalogo`), login, recursos estáticos.                           |

Autenticação por formulário em `/login` (campo de usuário = **email**). Senhas
armazenadas com **BCrypt**.

---

## 4. Usuários populados (seed)

A carga inicial é feita por um `CommandLineRunner` em
[ImobiliariaApplication.java](src/main/java/br/ufscar/dc/dsw/imobiliaria/ImobiliariaApplication.java),
executado automaticamente a cada inicialização. **Todos têm a senha `123456`.**

| Papel       | E-mail (login)            | Senha    | Observação               |
| ----------- | ------------------------- | -------- | ------------------------ |
| ADMIN       | `admin@hotmail.com`       | `123456` | Administrador do sistema |
| CLIENTE     | `julio@hotmail.com`       | `123456` | Julio Moya               |
| CLIENTE     | `marina@hotmail.com`      | `123456` | Marina Pena              |
| CLIENTE     | `jader@hotmail.com`       | `123456` | Jader Vinícius           |
| IMOBILIARIA | `cardinali@hotmail.com`   | `123456` | Cardinali                |
| IMOBILIARIA | `vistashouse@hotmail.com` | `123456` | Cada da Vita             |

Além dos usuários, o seed cria **3 cidades**, **10 imóveis** e **10 propostas de
compra** de exemplo.

---

## 5. Roteiro de execução

### Pré-requisitos

- JDK 17 instalado (`java -version`).
- MySQL 8 em execução em `localhost:3306` com usuário/senha conferindo com o
  `application.properties` (padrão `root` / `1234`).
- Não é preciso criar o banco manualmente — ele é criado na primeira execução.

### Passos

1. Clonar o repositório e entrar na pasta:

   ```bash
   git clone <url-do-repositorio>
   cd imobiliaria
   ```

2. (Opcional) Ajustar `username`/`password` do MySQL em
   `src/main/resources/application.properties`.

3. Executar a aplicação com o Maven Wrapper:

   **Windows (PowerShell / CMD):**

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   **Linux / macOS:**

   ```bash
   ./mvnw spring-boot:run
   ```

4. Aguardar o log `===== INSERÇÕES FINALIZADAS COM SUCESSO =====`, indicando que
   o banco foi populado.

5. Acessar no navegador: **http://localhost:8080**
   - Catálogo público: http://localhost:8080/imoveis/catalogo
   - Login: http://localhost:8080/login (use um dos usuários da seção 4).

### Gerar o JAR (opcional)

```bash
./mvnw clean package
java -jar target/imobiliaria-0.0.1-SNAPSHOT.jar
```

---

## 6. API REST

Há endpoints REST em
[controller/api/](src/main/java/br/ufscar/dc/dsw/imobiliaria/controller/api/)
(`/api/**`) para clientes, imobiliárias e imóveis.

---

## 7. Observações

- **`ddl-auto=create`** recria as tabelas e reexecuta a seed a cada start —
  ideal para avaliação/desenvolvimento, mas **apaga dados a cada execução**.
  Para preservar dados entre execuções, troque para `update` em
  `application.properties`.
- Upload de fotos de imóveis é salvo na pasta `uploads/` (máx. 10 imagens, 5 MB
  cada), servida em `/uploads/**`.
- O envio de e-mail (SMTP) está comentado no `application.properties`; preencha
  as credenciais para habilitar.
