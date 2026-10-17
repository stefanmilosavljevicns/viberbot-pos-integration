# Development

## Build

Use JDK 17 and Maven 3.8.6 or newer. From the repository root:

```sh
mvn -f services/pom.xml test
mvn -f services/pom.xml package -DskipTests
```

The compiler is configured for Java 17 across the reactor. The source library previously targeted Java 11 while its applications targeted Java 17; this has been aligned to one documented toolchain. Maven Central access is required on a clean machine unless dependencies are already cached.

## Local services

`docker compose -f services/docker-compose.yml up --build` starts an isolated MongoDB and REST API. The Mongo database is local-only and has no authentication; do not publish this Compose stack to an untrusted network. The Viber bot is behind the `viber` profile and should only be started when a test token is configured. Webhook registration remains off unless explicitly enabled.

Set `MONGODB_URI` when running the REST API outside Compose. Set `REST_API_URL` for the bot. The bot loads menu categories on startup and therefore needs the REST API. For tests without that dependency, the bot context test disables startup menu loading.

## Debugging

- REST API startup failures: check that MongoDB is reachable at `MONGODB_URI`.
- Bot startup failures while loading categories: check that `REST_API_URL` points to a healthy REST API containing menu data.
- Viber messages are not sent: provide `VIBER_BOT_TOKEN` and inspect sanitized application logs.
- Webhook registration requires a valid token and public HTTPS callback; keep it disabled for ordinary local development.
- Docker Compose cannot run if Docker Engine or the Compose plugin is unavailable.

The repository's legacy per-service Compose files use an externally created network and are deployment-oriented. Use the root-local `services/docker-compose.yml` for local MongoDB and REST API work.

The legacy REST and bot Compose files now require credentials through environment variables. If you must use them, make a private copy of `services/restapi/.env.example` with deployment-appropriate values and pass it with `docker compose --env-file`; do not copy production credentials into the repository.
