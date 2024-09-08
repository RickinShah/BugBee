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
- Nginx - Reverse Proxy
- Docker - Container
- Vite - Frontend Build Tool
- Maven - Backend Build Tool
- React - Frontend Library
- Tailwind - CSS

## Getting Started

### Prerequisites

Here's what you need to be able to run BugBee:

- Docker

### 1. Clone the repository

```shell
git clone https://github.com/RickinShah/BugBee.git
cd BugBee
```

### 2. Rename the .env.example file

- Docker requires .env file for environment variables

```shell
cp .env.example .env
```

### 3. Install npm dependencies

```shell
npm install --prefix src/main/frontend
```

### 4. Build the project

```shell
sh build.sh
```

### 5. Run the project

```shell
docker-compose up
```