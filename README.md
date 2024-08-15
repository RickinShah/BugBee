# BugBee

Under Development
================

This Project is currently in progress.

## Tech Stack
- Java - Language
- Spring Boot - Framework
- Spring Webflux - Reactive Framework
- PostgreSQL - Database
- Spring Security - Authentication

## Getting Started

### Prerequisites

Here's what you need to be able to run Papermark:

- JDK 21
- PostgreSQL

### 1. Clone the repository
```shell
git clone https://github.com/RickinShah/BugBee.git
cd BugBee
```

### 2. Configure Database (PostgreSQL)

- Make changes in src/main/resources/application.yml
```shell
spring:
  r2dbc:
    url: r2dbc:postgresql://your_host:5432/your_database
    username: your_username
    password: your_password
```

### 3. Compile the project

#### For Windows:
```shell
mvnw.cmd clean install
```
#### For Linux/MacOS:
```shell
./mvnw clean install
```

### 4. Run the project
```shell
java -jar target/BugBee-0.0.1-SNAPSHOT.jar
```