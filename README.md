# 📌 ProjetoSpringFullStack - ToDo List com Spring Boot

Aplicação web fullstack de lista de tarefas desenvolvida com **Spring Boot** e **Spring Security**, que permite o cadastro e autenticação de usuários com gerenciamento individual de tarefas. O projeto foi estruturado em camadas e utiliza banco de dados MySQL hospedado na plataforma **Neon**.

---

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot**
- **Spring Security**
- **Spring Data JPA**
- **Thymeleaf**
- **MySQL** (via Neon)
- **Maven**

---

## 🔐 Funcionalidades

- Registro e login de usuários com autenticação CSRF
- Criptografia de senhas com **BCrypt**
- Operações de **CRUD** de tarefas
- Interface web com **Thymeleaf**
- Proteção de rotas e recursos com Spring Security

---

## 🛠️ Pré-requisitos

Certifique-se de ter instalado:

- [Java JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Maven](https://maven.apache.org/)
- [MySQL](https://www.mysql.com/) (ou conexão ativa com Neon)

---

## 🔒 Segurança
- **Autenticação baseada em sessão e formulário com token CSRF**

- **Criptografia de senhas com BCrypt**

- **Rotas protegidas com Spring Security**

- **Configuração de CORS**

---

## ⚙️ Configuração do Banco de Dados

No MySQL (local ou Neon), execute:

sql

``CREATE SCHEMA `projeto`;``

## 🖥️ Como executar o projeto

bash

``git clone https://github.com/heitorfilizzola/ProjetoSpringFullStack.git``

``cd ProjetoSpringFullStack``

``mvn spring-boot:run``

## 🆙 Evoluções desde a N1

- Implementação de autenticação com CSRF e Spring Security
- Criptografia com BCrypt
- Organização em camadas
- Deploy na AWS EC2
- Integração com banco externo (Neon)
