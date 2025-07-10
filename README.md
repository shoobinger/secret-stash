# Secret Stash

# Running application locally
1. Build the application jar:
   ```commandline
   ./gradlew bootJar
   ```
2. Launch the app with Postgres:
   ```commandline
   docker compose up
   ```

# Running tests
Tests are performed using a temporary Postgres testcontainer instance. Ensure you have Docker running.

```commandline
./gradlew test
```
   
# Decisions and considerations

### ID
UUIDs were used as globally unique identifiers for both users and notes. With UUID it's a lot harder to brute-force
identifiers, as opposed to, for example, auto-increment integers.

### Virtual threads vs reactive
Java virtual thread support was used instead of Spring WebFlux or Kotlin coroutines. While different in terms of 
approach to concurrency, it's a good option for a simple service like this one.

### JWT library
nimbus2d was chosen as a mature library supporting EdDSA signatures, and I have more recent experience working with this
library

### Exceptions as errors
For simplicity, I did not introduce business exceptions (or result DTOs). Instead, ResponseStatusException from spring-web
was used everywhere in the service layer. A more production-ready approach would be to use business exceptions and 
translate them to responses afterward.

### Data JDBC vs JPA
Because there are no complex relations involved, I chose to use Spring Data JDBC to not overcomplicate things with 
Hibernate.

### MDC
Logback MDC is used to fill request context, so further logs do not mention IDs explicitly.

### PUT vs PATCH
For secure note update method, I chose PUT because there are not so many fields. An alternative approach would be to
provide PATCH endpoint to allow partial update.

### Spring Security
I decided not to use Spring Security, because for a simple project it brings a little bit too many abstraction layers
and unneeded components. I used a simple web interceptor to validate JWT tokens.

### 404 return code
In some cases, I return 404 when there is no permission. It's done because I want to hide information about the existence 
of a secure note as much as possible (for an unauthorized user).

### Pagination
An endpoint to fetch latest notes can further be improved with pagination, but it's not in the requirements.

### Stale rate limiters
As rate limiter objects are stored in memory, it's possible to improve this part to periodically evict stale rate
limiters using time-based cache eviction policy.
