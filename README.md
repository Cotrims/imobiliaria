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
| IMOBILIARIA | `vistashouse@hotmail.com` | `123456` | Casa da Vita             |

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

## 6. Rotas da aplicação

A aplicação expõe dois conjuntos de rotas:

- **Rotas web (Spring MVC + Thymeleaf):** retornam páginas HTML, exigem login
  por formulário (sessão) e respeitam as roles da seção 3.
- **Rotas REST (`/api/**`):** retornam/recebem JSON, estão liberadas no
  `WebSecurityConfig` (`permitAll`) e aceitam CORS (`@CrossOrigin`). Úteis para
  consumo por clientes externos (ex.: Postman, front-ends, scripts).

> Em todas as tabelas, `{id}` é o identificador numérico da entidade.

### 6.1. Rotas públicas / autenticação

| Método | Rota               | Acesso  | Descrição                                                       |
| ------ | ------------------ | ------- | --------------------------------------------------------------- |
| GET    | `/`                | público | Página inicial (`home`).                                        |
| GET    | `/login`           | público | Formulário de login. Campos: `email` e `password`.             |
| POST   | `/login`           | público | Autentica (processado pelo Spring Security). Sucesso → `/`.     |
| POST   | `/logout`          | logado  | Encerra a sessão. Sucesso → `/`.                                |
| GET    | `/acesso-negado`   | público | Página exibida quando falta permissão (HTTP 403).               |
| GET    | `/imoveis/catalogo`| público | Catálogo de imóveis. Filtro opcional `?cidade=NomeDaCidade`.    |

**Exemplo de login (form):**

```http
POST /login
Content-Type: application/x-www-form-urlencoded

email=admin@hotmail.com&password=123456
```

### 6.2. Rotas web — Imóveis (`/imoveis`)

| Método | Rota                     | Role        | Descrição                                              |
| ------ | ------------------------ | ----------- | ------------------------------------------------------ |
| GET    | `/imoveis/catalogo`      | público     | Catálogo público (aceita `?cidade=`).                  |
| GET    | `/imoveis/listar`        | ADMIN       | Listagem administrativa de todos os imóveis.           |
| GET    | `/imoveis/meus`          | IMOBILIARIA | Imóveis da imobiliária logada.                         |
| GET    | `/imoveis/cadastrar`     | IMOBILIARIA | Formulário de cadastro de imóvel.                      |
| POST   | `/imoveis/salvar`        | IMOBILIARIA | Salva novo imóvel. Aceita upload `novasFotos` (máx 10).|
| GET    | `/imoveis/editar/{id}`   | IMOBILIARIA | Formulário de edição (apenas imóveis próprios).        |
| POST   | `/imoveis/editar`        | IMOBILIARIA | Atualiza o imóvel. `fotosParaExcluir` remove fotos.    |
| GET    | `/imoveis/excluir/{id}`  | IMOBILIARIA | Exclui o imóvel próprio e suas fotos.                  |

> As rotas de cadastro/edição usam `multipart/form-data` por causa do upload de
> fotos (campo `novasFotos`, máx. 10 imagens de 5 MB cada).

### 6.3. Rotas web — Propostas de compra (`/propostas`)

| Método | Rota                       | Role        | Descrição                                                 |
| ------ | -------------------------- | ----------- | --------------------------------------------------------- |
| GET    | `/propostas/cadastrar`     | CLIENTE     | Formulário de proposta. Aceita `?imovelId=` para pré-seleção. |
| POST   | `/propostas/salvar`        | CLIENTE     | Cria proposta (status `ABERTO`). Bloqueia duplicadas.     |
| GET    | `/propostas/minhas`        | CLIENTE     | Propostas do cliente logado.                              |
| GET    | `/propostas/editar/{id}`   | CLIENTE     | Edita proposta própria em aberto.                         |
| POST   | `/propostas/editar`        | CLIENTE     | Atualiza valor/condições da proposta em aberto.           |
| GET    | `/propostas/excluir/{id}`  | CLIENTE     | Exclui proposta própria em aberto.                        |
| GET    | `/propostas/listar`        | ADMIN       | Listagem administrativa de todas as propostas.            |
| GET    | `/propostas/imobiliaria`   | IMOBILIARIA | Propostas recebidas nos imóveis da imobiliária.           |
| GET    | `/propostas/avaliar/{id}`  | IMOBILIARIA | Tela para avaliar uma proposta recebida.                  |
| POST   | `/propostas/decidir`       | IMOBILIARIA | Decide a proposta. Campos: `id`, `acao` (`ACEITO`/`NAO_ACEITO`), e — quando `ACEITO` — `meetingLink`, `meetingHorario`; quando `NAO_ACEITO`, contraproposta opcional `contraValor`/`contraCondicoes`. Envia e-mail ao cliente. |

### 6.4. Rotas web — Administração (somente ADMIN)

CRUD com o mesmo padrão de rotas para **Clientes** (`/clientes`), **Imobiliárias**
(`/imobiliarias`) e **Usuários** (`/usuarios`):

| Método | Rota                       | Descrição                          |
| ------ | -------------------------- | ---------------------------------- |
| GET    | `/{recurso}/listar`        | Lista todos os registros.          |
| GET    | `/{recurso}/cadastrar`     | Formulário de cadastro.            |
| POST   | `/{recurso}/salvar`        | Cria o registro.                   |
| GET    | `/{recurso}/editar/{id}`   | Formulário de edição.              |
| POST   | `/{recurso}/editar`        | Atualiza o registro (senha opcional via `novoPassword`). |
| GET    | `/{recurso}/excluir/{id}`  | Exclui o registro.                 |

Onde `{recurso}` é `clientes`, `imobiliarias` ou `usuarios`.

### 6.5. API REST (`/api/**`)

Endpoints em
[controller/api/](src/main/java/br/ufscar/dc/dsw/imobiliaria/controller/api/),
liberados (`permitAll`) e com CORS habilitado. Respostas em JSON; listas vazias
retornam **404**.

**Imóveis — `/api/imoveis`** (somente leitura):

| Método | Rota                          | Descrição                             |
| ------ | ----------------------------- | ------------------------------------- |
| GET    | `/api/imoveis`                | Lista todos os imóveis.               |
| GET    | `/api/imoveis/{id}`           | Busca um imóvel por id.               |
| GET    | `/api/imoveis/cidades/{nome}` | Imóveis de uma cidade (pelo nome).    |
| GET    | `/api/imoveis/imobiliarias/{id}` | Imóveis de uma imobiliária.        |

**Clientes — `/api/clientes`** e **Imobiliárias — `/api/imobiliarias`** (CRUD completo):

| Método | Rota                      | Descrição                                  |
| ------ | ------------------------- | ------------------------------------------ |
| GET    | `/api/{recurso}`          | Lista todos.                               |
| GET    | `/api/{recurso}/{id}`     | Busca por id.                              |
| POST   | `/api/{recurso}`          | Cria (role definida automaticamente).      |
| PUT    | `/api/{recurso}/{id}`     | Atualiza (campos enviados sobrescrevem).   |
| DELETE | `/api/{recurso}/{id}`     | Remove (retorna 204).                      |

Onde `{recurso}` é `clientes` ou `imobiliarias`.

**Exemplos de uso (cURL):**

```bash
# Listar imóveis
curl http://localhost:8080/api/imoveis

# Imóveis de uma cidade
curl http://localhost:8080/api/imoveis/cidades/Sorocaba

# Criar um cliente
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
        "nome": "Maria Silva",
        "email": "maria@exemplo.com",
        "password": "123456",
        "CPF": "12345678900",
        "sexo": "F",
        "telefone": "15999990000",
        "dataNascimento": "1990-05-20"
      }'

# Criar uma imobiliária
curl -X POST http://localhost:8080/api/imobiliarias \
  -H "Content-Type: application/json" \
  -d '{
        "nome": "Imob Central",
        "email": "central@exemplo.com",
        "password": "123456",
        "CNPJ": "12345678000199",
        "descricao": "Imobiliária de exemplo"
      }'

# Atualizar parcialmente uma imobiliária (id 5)
curl -X PUT http://localhost:8080/api/imobiliarias/5 \
  -H "Content-Type: application/json" \
  -d '{ "descricao": "Nova descrição" }'

# Remover um cliente (id 3)
curl -X DELETE http://localhost:8080/api/clientes/3
```

> Campos JSON aceitos:
> - **Cliente:** `nome`, `email`, `password`, `CPF`, `sexo`, `telefone`, `dataNascimento` (`AAAA-MM-DD`).
> - **Imobiliária:** `nome`, `email`, `password`, `CNPJ`, `descricao`.

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
