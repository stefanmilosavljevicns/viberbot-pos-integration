# Security review and remediation record

## Current tree findings

- `services/restapi/src/main/resources/application.properties` contained a committed MongoDB URI with embedded credentials and a non-local host. The URI has been replaced by `MONGODB_URI`; use local or secret-managed credentials and rotate the exposed database credential.
- `services/restapi/docker-compose.yml` contained a committed MongoDB password default. The split deployment Compose files now require `MONGODB_URI`, username, password, and Viber token values from the caller's environment; sanitized templates are provided for local editing.
- `services/nginx/certbot/` contains Certbot account private keys and TLS private-key files. These deployment files remain in the working tree because their live deployment purpose and replacement status cannot be verified here. Treat the private material as compromised, reissue affected certificates, replace account credentials where applicable, then remove deployment runtime state from source control in a planned deployment change.
- `services/bot/src/main/java/payten/bot/util/StringUtils.java` reads the Viber token from configuration. It is now environment-backed, with an empty default; do not place a live token in tracked files.
- The webhook controller and REST API do not implement authentication or webhook-signature validation. User, cart, and order data should not be exposed publicly until an authentication and authorization design is implemented.
- User records include a numeric PIN field. The source does not demonstrate hashing or access control for it.
- Request/response bodies and Viber identifiers were logged in several paths. The bot no longer logs raw webhook payloads, REST response bodies, or reminder recipient IDs.

## Git history and owner actions

The commit history includes earlier deployment layouts and configuration files in addition to the current tree. Removing a credential or key from the latest checkout does not remove it from Git history or existing clones. Do not rewrite history from this task. Repository owners should:

1. Rotate the exposed MongoDB credential and verify whether any other historical database credentials remain active.
2. Revoke/replace the Viber bot token if any historical version exposed one.
3. Reissue TLS certificates whose private keys were committed; assess the Certbot account keys and renewals independently.
4. Decide whether to perform a coordinated Git-history cleanup after preserving required audit records and notifying collaborators.
5. Review access logs for use of the affected credentials and keep the repository private until remediation is complete if exposure scope is uncertain.

The root `.gitignore` now ignores environment files, local build output, and newly added private-key files. Ignore rules do not untrack files already in Git. The tracked Certbot material must be removed from the deployment workflow in a separate reviewed change after operators confirm the replacement files and mounts.

## Local safety

The local Compose database is unauthenticated and is not published on a host port; keep its network local. Do not connect tests to production services. Webhook registration is disabled by default and requires explicit configuration. Enable it only with a dedicated test Viber bot and a test callback URL.

## Verification boundary

This review inspects the current tree and file paths in commit history. It cannot prove whether credentials remain valid, whether the public repository has been cloned, or whether the deployed Nginx service currently uses these files. Those items require owner/operator verification. No secret values are reproduced in this report.
