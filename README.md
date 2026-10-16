# 📋 ProjetoSpringFullStack — Gerenciador de Tarefas Empresarial

Aplicação web fullstack de gerenciamento de tarefas com suporte a **empresas e funcionários**, desenvolvida com **Spring Boot 3** e **Spring Security**. Permite o cadastro de empresas, gerenciamento de usuários com controle de acesso por papéis (administrador/usuário) e operações completas de CRUD de tarefas por usuário.

---

## 🚀 Tecnologias Utilizadas

| Tecnologia | Versão / Detalhe |
|---|---|
| Java | 17 |
| Spring Boot | 3.4.3 |
| Spring Security | Form Login + CSRF |
| Spring Data JPA | Hibernate |
| Thymeleaf | + Extras Security 6 |
| MySQL | Driver via `mysql-connector-j` |
| PostgreSQL | Driver disponível (comentado) |
| Spring Mail | SMTP via Mailtrap |
| Spring Actuator | Monitoramento |
| Bean Validation | Jakarta Validation |
| Maven | Build tool |

---

## 🏗️ Arquitetura do Projeto

O projeto segue a arquitetura em camadas padrão do Spring Boot:

```
src/main/java/com/example/demo/
├── config/             # Configurações de segurança (SecurityConfig, WebSecurityConfig)
├── controllers/        # Controladores MVC (Login, Register, Task, Enterprise, Email, ForgotPassword...)
├── dto/                # Data Transfer Objects para requisições e formulários
├── models/             # Entidades JPA (User, Task, Enterprise, RolesUser, StatusTask)
├── repositories/       # Interfaces Spring Data JPA
└── services/           # Serviços (EmailService)

src/main/resources/
├── templates/          # Views Thymeleaf por página
│   ├── Login_Page/
│   ├── Register_Page/
│   ├── ForgotPassword_Page/
│   ├── ResetPassword_Page/
│   ├── Enterprise_Page/
│   ├── CreateEnterprise_Page/
│   ├── CreateEmployee_Page/
│   ├── EditEmployee_Page/
│   ├── Edit_Page/
│   └── error/          # Páginas de erro (404, 500)
├── static/
│   ├── css/            # Estilos por página
│   └── js/             # Scripts por página
└── application.properties
```

---

## ✅ Funcionalidades

### 👤 Usuários
- Registro e login com autenticação por formulário
- Criptografia de senhas com **BCrypt**
- Recuperação de senha por e-mail (token com expiração)
- Controle de acesso por papéis: `administrator` e `user`
- Proteção de rotas com Spring Security + tokens CSRF

### 🏢 Empresas
- Criação de empresas por administradores
- Associação de funcionários (usuários) a uma empresa
- Edição e gerenciamento de funcionários

### 📝 Tarefas
- CRUD completo de tarefas por usuário
- Status da tarefa: `FAZER` (a fazer) e `FEITO` (concluída)
- Cada tarefa possui título e conteúdo

### 📧 E-mail
- Envio de e-mail para recuperação de senha via SMTP (Mailtrap por padrão)

---

## 🗂️ Modelo de Dados

```
Enterprise  1 ──── N  User  1 ──── N  Task
```

- **Enterprise**: `id (UUID)`, `name (único)`
- **User**: `id (UUID)`, `name`, `email (único)`, `password (BCrypt)`, `rolesUser`, `enterprise_id (FK)`
- **Task**: `idTask`, `title`, `content`, `statusTask`, `user_id (FK)`

---

## 🛠️ Pré-requisitos

- [Java JDK 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Maven 3.8+](https://maven.apache.org/)
- [MySQL 8+](https://www.mysql.com/) (local ou hospedado, ex.: [Neon](https://neon.tech/))

---

## ⚙️ Configuração

### 1. Banco de Dados

Crie o schema no MySQL:

```sql
CREATE SCHEMA `projeto`;
```

### 2. Variáveis / `application.properties`

Configure as credenciais em `src/main/resources/application.properties`:

```properties
# Banco de dados
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/projeto
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA

# E-mail (SMTP)
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=SEU_USERNAME_MAILTRAP
spring.mail.password=SUA_SENHA_MAILTRAP
```

> **Dica:** Em produção, prefira variáveis de ambiente (`${SPRING_DATASOURCE_URL}`, etc.) em vez de credenciais em texto no arquivo de propriedades.

---

## ▶️ Como Executar

```bash
# Clone o repositório
git clone https://github.com/heitorfilizzola/ProjetoSpringFullStack.git
cd ProjetoSpringFullStack

# Execute com Maven Wrapper
./mvnw spring-boot:run
```

A aplicação estará disponível em: **http://localhost:8080**

---

## 🔐 Segurança

- Autenticação baseada em **sessão** com formulário de login
- Proteção **CSRF** habilitada em todos os formulários
- Senhas criptografadas com **BCrypt**
- Controle de acesso por **roles** (`administrator` / `user`)
- **Thymeleaf Security Extras** para renderização condicional de elementos na view
- Token de redefinição de senha com **prazo de expiração**

---

## 🗺️ Rotas Principais

| Rota | Método | Descrição |
|---|---|---|
| `/` | GET | Página home (lista de tarefas) |
| `/login` | GET / POST | Login de usuário |
| `/register` | GET / POST | Registro de novo usuário |
| `/forgot-password` | GET / POST | Solicitar redefinição de senha |
| `/reset-password` | GET / POST | Redefinir senha via token |
| `/newTask` | GET / POST | Criar nova tarefa |
| `/edit/{id}` | GET / POST | Editar tarefa |
| `/enterprise` | GET | Painel da empresa (admin) |
| `/enterprise/create` | GET / POST | Criar empresa |
| `/enterprise/employee/create` | GET / POST | Adicionar funcionário |
| `/enterprise/employee/edit/{id}` | GET / POST | Editar funcionário |

---

## 📦 Build para Produção

```bash
./mvnw clean package -DskipTests
java -jar target/demo-1.0.0.jar
```

---

## 🆕 Evoluções Recentes

- ✅ Implementação de autenticação com Spring Security e CSRF
- ✅ Criptografia de senhas com BCrypt
- ✅ Sistema de recuperação de senha por e-mail com token expirável
- ✅ Suporte a empresas e gerenciamento de funcionários com controle de papéis
- ✅ Páginas de erro customizadas (404 e 500)
- ✅ Organização em camadas (Controller → Service → Repository)
- ✅ Deploy na AWS EC2 com banco externo (Neon)

---

## 👤 Autor

**Heitor Filizzola**  
[GitHub](https://github.com/heitorfilizzola)
