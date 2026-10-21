# AI-Powered Developer Productivity & Code Review Platform

An enterprise-ready, full-stack developer productivity and automated code review platform. Built with **Spring Boot 3 (Java 21)** and **React 19 (Vite + Tailwind CSS)**, the platform connects to GitHub repositories, inspects Pull Requests, and leverages **Groq Cloud LLMs (Llama 3.3 70B)** to provide instant, objective architectural analysis, security vulnerability detection, bug identification, and code quality scoring.

Designed as a **clean modular monolith** with a modern, high-contrast **white SaaS aesthetic** suitable for production deployment and software engineering interview walkthroughs.

---

## 1. Architecture Overview

```text
               +-------------------------------------------+
               |           React 19 / Vite Client          |
               | (Tailwind CSS, React Router, Toastify)    |
               +-------------------------------------------+
                                     |
                         HTTPS / REST API (JWT)
                                     v
               +-------------------------------------------+
               |        Spring Boot 3.3 (Java 21)          |
               |              REST API Layer               |
               +-------------------------------------------+
                /         |             |             \
               /          |             |              \
              v           v             v               v
    +-------------+  +----------+  +----------+  +-------------+
    | Spring Data |  | GitHub   |  | Groq AI  |  |  Security   |
    | JPA Engine  |  | API/Diff |  | LLM API  |  | Filter Chain|
    +-------------+  +----------+  +----------+  +-------------+
          |
          v
    +-------------+       +-------------------+
    | PostgreSQL  | <---- |      pgAdmin      |
    | Database    |       | (Database Studio) |
    +-------------+       +-------------------+
```

---

## 2. Tech Stack

### Frontend
- **Framework**: React 19 + Vite
- **Language**: JavaScript (ES6+)
- **Styling**: Tailwind CSS (Clean White SaaS Design System)
- **Routing**: React Router DOM v7
- **HTTP Client**: Axios (Centralized API Layer with JWT Interceptor)
- **Icons**: React Icons (Feather Icons)
- **Notifications**: React Toastify (Non-intrusive toasts)

### Backend
- **Language & Runtime**: Java 21 LTS
- **Framework**: Spring Boot 3.3.4
- **Security**: Spring Security 6 + JJWT (Stateless JWT Bearer Auth)
- **Password Hashing**: BCrypt
- **Persistence**: Spring Data JPA / Hibernate
- **Database**: PostgreSQL 16
- **Integrations**: Spring `RestClient` for GitHub REST API & Groq AI Chat Completions
- **Validation**: Jakarta Bean Validation (`@Valid`, `@NotBlank`, `@Email`, etc.)

### Infrastructure
- **Containerization**: Docker & Docker Compose
- **Database Administration**: pgAdmin 4 Container
- **Configuration**: Root `.env` file passed dynamically to services

---

## 3. Key Capabilities & Features

1. **Clean White SaaS Dashboard**:
   - Total repositories, pull request count, issues detected, and average code quality scorecard (1-10).
   - Real-time audit activity feed tracking developer registrations, GitHub syncs, and review runs.
   - High/critical severity issue counter.

2. **GitHub Account & Repository Integration**:
   - Connect via GitHub Personal Access Token (PAT) or simulated enterprise token.
   - Automatic sync of repositories, branches, visibility (Public/Private), and PR counts.
   - Manual repository addition with custom configuration.

3. **Pull Request Lifecycle & AI Review Engine**:
   - Comprehensive pull request listing across all user repositories with search and status filtering (Open/Closed).
   - One-click **Analyze Pull Request** button powered by **Groq Llama 3.3 70B**.
   - Review findings categorized by:
     - `BUG` (Null pointer risks, logic flaws, race conditions)
     - `SECURITY` (Injection vulnerabilities, auth flaws, unsanitized inputs)
     - `PERFORMANCE` (N+1 queries, unclosed resources, memory overhead)
     - `MAINTAINABILITY` (Code duplication, tight coupling, missing abstractions)
     - `CODE_QUALITY` (Anti-patterns, conventions)
   - Severity levels: `CRITICAL`, `HIGH`, `MEDIUM`, `LOW`.
   - Actionable code remediation snippets and file/line references.
   - Executive AI summary with strengths and recommended improvements.
   - Historical review versioning: track score improvements across iterations.

4. **Team Collaboration & Governance**:
   - Create squads and assign members by email.
   - Role assignments: `OWNER`, `LEAD`, `MEMBER`.

5. **Security & Role-Based Access Control (RBAC)**:
   - User roles: `ADMIN`, `DEVELOPER`, `REVIEWER`.
   - Protected endpoints using Spring Security `@EnableMethodSecurity`.
   - JWT token storage with automatic client-side route guards and redirection.

---

## 4. Project Structure

```text
p2/
├── .env                     # Single root environment configuration
├── .env.example             # Template with placeholder credentials
├── docker-compose.yml       # Multi-container orchestration (Postgres, pgAdmin, Backend, Frontend)
├── README.md                # Technical documentation
│
├── backend/
│   ├── Dockerfile           # Multi-stage Java 21 build
│   ├── pom.xml              # Maven dependencies & plugins
│   └── src/
│       ├── main/
│       │   ├── java/com/example/codereview/
│       │   │   ├── config/          # SecurityConfig, DataInitializer
│       │   │   ├── controller/      # REST API Controllers (Auth, Repos, PRs, Reviews, Teams, Dashboard)
│       │   │   ├── dto/             # Request & Response DTOs
│       │   │   ├── entity/          # JPA Entities (User, Repo, PR, Review, Finding, Team, Activity)
│       │   │   ├── enums/           # RoleType, FindingType, FindingSeverity, Status enums
│       │   │   ├── exception/       # Centralized GlobalExceptionHandler & custom exceptions
│       │   │   ├── integration/     # GitHubClient & GroqClient + GroqPromptBuilder
│       │   │   ├── mapper/          # Entity <-> DTO Mappers
│       │   │   ├── repository/      # Spring Data JPA Repositories
│       │   │   ├── security/        # JwtUtils, JwtFilter, CustomUserDetails
│       │   │   ├── service/         # Business services
│       │   │   └── CodeReviewApplication.java
│       │   └── resources/
│       │       ├── application.yml        # PostgreSQL production config
│       │       └── application-local.yml  # Zero-friction local dev profile
│       └── test/
│
└── frontend/
    ├── Dockerfile           # Node.js container for frontend
    ├── package.json         # React 19 dependencies & scripts
    ├── vite.config.js       # Vite + Tailwind CSS plugin
    ├── index.html           # HTML template & Inter typography
    └── src/
        ├── components/
        │   ├── common/      # Button, Input, Select, Modal, Card, Badge, StatCard, Loader, EmptyState
        │   └── layout/      # Navbar, Sidebar (with mobile drawer), Layout
        ├── context/         # AuthContext (JWT & state management)
        ├── pages/           # Dashboard, Repositories, PullRequests, Reviews, Team, Profile, Settings
        ├── routes/          # AppRoutes, ProtectedRoute
        ├── services/        # Centralized Axios client & API service modules
        ├── App.jsx          # Root application wrapper
        └── index.css        # Clean White SaaS styling
```

---

## 5. Database Schema & Entities

The relational database in PostgreSQL contains 9 core entities:

| Entity | Table | Description |
|---|---|---|
| `User` | `users` | Accounts, hashed BCrypt credentials, roles (`ADMIN`, `DEVELOPER`, `REVIEWER`) |
| `GithubAccount` | `github_accounts` | Linked GitHub profile, avatar, username, and token |
| `RepositoryEntity` | `repositories` | Connected repos, clone URL, default branch, privacy flag |
| `PullRequest` | `pull_requests` | PR number, title, author, branch refs, status (`OPEN`, `CLOSED`, `MERGED`) |
| `CodeReview` | `code_reviews` | Review score (1-10), executive summary, strengths, improvements |
| `ReviewFinding` | `review_findings` | Issues with type, severity (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`), remediation suggestion |
| `Team` | `teams` | Engineering squads created by users |
| `TeamMember` | `team_members` | Team membership mapping with squad roles |
| `Activity` | `activities` | Real-time audit log of actions |

---

## 6. Standardized API Response Structure

Every REST endpoint returns a uniform `ApiResponse<T>` envelope:

**Success Response (200 OK / 201 Created):**
```json
{
  "success": true,
  "statusCode": 200,
  "message": "AI code review completed successfully",
  "data": {
    "id": 1,
    "score": 8,
    "summary": "AI-generated analysis: Clear separation of concerns...",
    "findings": [ ... ]
  }
}
```

**Error Response (400 / 401 / 404 / 409):**
```json
{
  "success": false,
  "statusCode": 404,
  "message": "Pull Request not found with id: 99",
  "data": null
}
```

---

## 7. Environment Variables (`.env`)

Configure the single `.env` at the root directory:

```env
# PostgreSQL
POSTGRES_DB=code_review_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_PORT=5432

# JWT Security
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION_MS=86400000

# GitHub Integration
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret

# Groq Cloud AI
GROQ_API_KEY=your_groq_api_key
GROQ_MODEL=llama-3.3-70b-versatile

# Ports
FRONTEND_PORT=5173
BACKEND_PORT=8080
PGADMIN_PORT=5050

# pgAdmin
PGADMIN_DEFAULT_EMAIL=admin@codereview.ai
PGADMIN_DEFAULT_PASSWORD=admin
```

---

## 8. Quick Start with Docker Compose

To build and run all 4 containers (`frontend`, `backend`, `postgres`, `pgadmin`):

```bash
docker compose up --build
```

### Accessing Services:
- **Frontend Web UI**: [http://localhost:5173](http://localhost:5173)
- **Backend API**: [http://localhost:8080/api/health](http://localhost:8080/api/health)
- **pgAdmin 4**: [http://localhost:5050](http://localhost:5050)
  - Login: `admin@codereview.ai` / `admin`
  - Connect Server: Host `postgres`, Port `5432`, Database `code_review_db`, User `postgres`, Password `postgres`

### Stopping Containers:
```bash
docker compose down
```

---

## 9. Local Development (Without Docker)

### 1. Backend:
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```
*Note: The `local` profile uses an embedded in-memory database with automatic demo seeding, requiring zero setup!*

### 2. Frontend:
```bash
cd frontend
npm install
npm run dev
```
Open [http://localhost:5173](http://localhost:5173).

---

## 10. Default Demo Accounts

Out-of-the-box seeded credentials:

| Role | Email | Password |
|---|---|---|
| **Developer** | `developer@codereview.ai` | `dev123` |
| **Admin** | `admin@codereview.ai` | `admin123` |

You can also register any new account instantly from the UI.
