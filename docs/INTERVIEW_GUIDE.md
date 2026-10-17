# Interview guide

## A concise project description

This is a Java 17 Maven reactor with three modules: a Viber client library, a Spring Boot bot, and a Spring Boot REST API backed by MongoDB. The bot translates Viber keyboard actions into calls to the REST API. The API stores menu, customer-cart, and reservation records. The repository also retains an Nginx/Certbot deployment setup.

## Explain the reservation flow

1. The bot loads categories from the REST API and builds Viber keyboards.
2. A user selects services; the bot stores them in the customer's cart.
3. The bot requests candidate times for the selected duration. The API filters overlapping active orders in the 07:00–17:00 window.
4. On confirmation, the bot separately checks availability, creates a `PENDING` order, and clears the cart.
5. A scheduled bot task can remind customers about next-day reservations.

The check and insert are not atomic, so two users can still reserve the same slot concurrently. A future design should make reservation creation a single transactional or conditional operation with a database-enforced conflict strategy.

## Engineering trade-offs and improvements

- The original project is split between conversation handling and persistence/API responsibilities, but the bot uses synchronous HTTP calls and a large controller.
- Configuration now takes database and Viber credentials from environment variables; webhook registration is opt-in.
- A local Compose stack avoids the deployment Compose files' externally managed network and isolates MongoDB from host ports.
- The slot listing now filters existing active orders and rejects non-positive durations. It remains a candidate-generation feature, not a concurrency guarantee.
- Sensitive request/response logging was reduced, but the wider API still needs authentication, authorization, input validation, and careful data minimization.
- Historical TLS private material remains in the checkout pending operator-led certificate replacement and deployment migration.

## Likely follow-up questions

**Why split the bot and REST API?** The bot owns Viber interactions while the API owns menu/cart/order persistence, allowing each boundary to be reasoned about independently. The current synchronous calls and duplicated assumptions still create runtime coupling.

**How do you prevent double booking?** The current implementation does not fully prevent it. Availability is checked separately from order creation. I would replace that with one atomic reservation operation and an explicit conflict response.

**How is the webhook protected?** The current code has no signature verification or authentication. A production version needs provider request verification, replay protection, rate limiting, and an authorization boundary.

**What needs immediate security work?** Rotate credentials and TLS material committed in the current or historical tree, restrict or remove unauthenticated user/order endpoints, and design secret storage before deployment.

**Why not upgrade all dependencies?** The first step was to align the build to Java 17 and make the Maven reactor explicit. Broader Spring and library upgrades need compatibility testing and a separate dependency-risk review.

**How would you evolve the system today?** I would start with authenticated APIs, a validated webhook boundary, typed configuration, transactional reservation creation, explicit time-zone handling, and integration tests against an isolated MongoDB instance.
