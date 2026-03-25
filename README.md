# ODA Automation Service
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/OpenDonationAssistant/oda-automation-service)

## Running with Docker

The service is published to GitHub Container Registry:

```bash
docker run -d \
  -p 8080:8080 \
  -e JDBC_URL=jdbc:postgresql://postgres:5432/automation \
  -e JDBC_USER=postgres \
  -e JDBC_PASSWORD=your_password \
  ghcr.io/opendonationassistant/oda-automation-service:latest
```

### Required Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `JDBC_URL` | PostgreSQL JDBC connection URL | `jdbc:postgresql://localhost/postgres?currentSchema=automation` |
| `JDBC_USER` | Database username | `postgres` |
| `JDBC_PASSWORD` | Database password | `postgres` |

### Using Docker Compose

Example `docker-compose.yml`:

```yaml
services:
  app:
    image: ghcr.io/opendonationassistant/oda-automation-service:latest
    ports:
      - "8080:8080"
    environment:
      - JDBC_URL=jdbc:postgresql://postgres:5432/automation
      - JDBC_USER=postgres
      - JDBC_PASSWORD=your_password
    depends_on:
      - postgres

  postgres:
    image: postgres:16
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=your_password
      - POSTGRES_DB=automation
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```
