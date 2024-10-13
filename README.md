# BugBee: Social Media and Forum for Indus University
BugBee is a social media and forum platform exclusively for Indus University students, providing a space for discussion, collaboration, and community building.

## Under Development
This Project is currently in progress.

## License
This project is licensed under the MIT License - see the [LICENSE](https://github.com/RickinShah/BugBee/blob/main/LICENSE) file for details.

## Features
- Restricted access to Indus University students only, verified through university email domain ([indusuni.ac.in](https://indusuni.ac.in/))
- Secure login system using JWT Token for authentication and authorization
- Social media features for connecting with fellow students
- AI-powered content moderation for a safe and respectful community
- All media files are securely encrypted before being stored. Ensuring sensitive content remains confidential and protected from unautorized access

## Tech Stack

### Backend
- Language: Java 21, Python 3.7+
- Framework: Spring Webflux, FastAPI
- Database: PostgreSQL
- Build Tool: Maven
- Security: Spring Security
- Web Server: Netty, Uvicorn

### Frontend
- Build Tool: Vite, Npm
- UI Library: React
- CSS Framework: Tailwind

### Infrastructure
- Load Balancer/Reverse Proxy: Nginx
- Containerization: Docker

### AI/ML
- Deep Learning Framework/ML Library: Hugging Face Transformers

## Getting Started
### Prerequisites

Here's what you need to be able to run BugBee:

- Docker ([Installation Guide](https://docs.docker.com/get-started/get-docker/))
- Docker Compose ([Installation Guide](https://docs.docker.com/compose/install/))

> **Note**: You do **not** need to install Java, Python, npm, or any other dependencies manually. Docker will automatically handle everything via pre-configured containers.

### 1. Clone the repository
```shell
git clone https://github.com/RickinShah/BugBee.git
cd BugBee
```

### 2. Rename the .env.example file
Docker requires .env file for environment variables
```shell
cp .env.example .env
```

### 3. Build the project
```shell
docker-compose build
```

> [!TIP]
> You can speed up development by using the `docker-compose up --build` command. This automatically rebuilds the application when changes are made.

### 4. Run the project
```shell
docker-compose up
```

> [!WARNING]
> Ensure that you have sufficient disk space and system resources available. Docker images and containers can consume a significant amount of storage and memory.

Access the application at http://localhost:80/

## Configuration
### Directory Structure for Posts
The following directory structure is used to organize the media files uploaded by users. You can customize the location of this storage by changing the `$STORAGE` environment variable in your `.env` file

```shell
|- / (root) - Path described in '$STORAGE'
  |- posts/
    |- images/      # Supported formats: .jpg, .jpeg, .png
    |- videos/      # Supported formats: .mp4, .webm
    |- audios/      # Supported formats: .mp3, .wav, .ogg
    |- documents/   # Supported formats: .pdf
```
