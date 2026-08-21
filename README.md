# Mel Spiritual Portal - Core API Gateway & Rate Limiter

## 1. Project Overview
Mel Portal is a high-performance backend infrastructure microservice designed explicitly for spiritual organizations to manage member access traffic securely. Acting as a central entry point (API Gateway), the application shields downstream services by intercepting incoming requests to perform token-based user authentication, evaluate dynamic per-profile rate limiting, and execute decoupled, non-blocking telemetry logging. The entire system runs inside an optimized Multi-Stage Docker environment.

---

## 2. Tech Stack Used
* **Backend Framework Core:** Spring Boot 3.4.2 / Web MVC
* **Runtime Language Engine:** Java 21 / 26 targeting stable JVM 21 bytecode configurations
* **Containerization Framework:** Docker (Eclipse Temurin JRE Alpine baseline)
* **Analytical Database Core:** MongoDB Atlas Cloud Cluster Instance
* **Frontend Web Interface:** Decoupled Vanilla HTML5 / JavaScript / TailwindCSS
* **Cloud Hosting Provider:** Railway Platform Core Engines

---

## 3. Setup Instructions (Step-by-Step)

### Prerequisites
* Ensure you have **Java 21** (or higher) and **Maven** installed locally.
* A running **MongoDB Atlas** cloud cluster with network access set to `0.0.0.0/0` (Allow access from anywhere).

### Local Installation & Startup Execution
1. **Clone the Repository:**
   ```bash
   git clone https://github.com
   cd GUVI
   ```

2. **Configure Local Environment variables:**
   Wrap your MongoDB cloud access credentials inside single quotes to prevent PowerShell parsing drive conflicts:
   ```powershell
   \$env:SPRING_DATA_MONGODB_URI = 'mongodb+srv://yourUsername:yourSafePassword@hue.cluster.mongodb.net/mel_db?retryWrites=true&w=majority'
   ```

3. **Clean Cache and Compile Binary Assets:**
   ```powershell
   .\mvnw clean compile
   ```

4. **Boot Up the Application Server Local Host Environment:**
   ```powershell
   .\mvnw spring-boot:run
   ```
5. **Access the Local Web Workspace:** Open your browser and navigate directly to: `http://localhost:8081`

---

## 4. API Documentation (Endpoints & Sample Requests)

### Target Spiritual Core Resource Node
* **URL Destination Path:** `/api/v1/meditation/session`
* **Network Communication Method:** `GET`
* **Mandatory Security Custom Header Key:** `X-Member-Token`
* **Header Value Parameter Format:** Plain text string tracking the member identification profile (e.g., `HUE-MEMBER-777`)

### Sample Network Execution Sequence (Using cURL / Postman)

#### Scenario A: Successful Authorized Request (HTTP 200 OK)
```bash
curl -X GET https://railway.app \
  -H "X-Member-Token: HUE-MEMBER-777" \
  -H "Content-Type: application/json"
```
**Sample JSON Response Payload:**
```json
{
    "status": "success",
    "title": "Morning Mindfulness Flow",
    "durationMinutes": 20,
    "instructor": "Acharya Achal",
    "message": "Welcome to your sacred portal space. Your request passed the Gateway successfully."
}
```

#### Scenario B: Denied Missing Identity Authorization Check (HTTP 401 Unauthorized)
```bash
curl -X GET https://railway.app
```
**Sample JSON Response Payload:**
```json
{
    "error": "Unauthorized",
    "message": "Missing spiritual portal authorization token. Please provide your Member ID."
}
```

#### Scenario C: Blocked Rate Limiter Burst Ceiling Tripped (HTTP 429 Too Many Requests)
*Triggered automatically when a member account profile fires network calls faster than the configured allocation parameters.*
```json
{
    "error": "Too Many Requests",
    "message": "Your spiritual portal user account has exceeded its traffic allowance. Please wait a moment."
}
```

---

## 5. Design Decisions & Trade-Offs

### Token Bucket vs. Alternative Strategies
* **Token Bucket (Chosen Framework):** This approach permits clean, temporary **burst traffic handling capacity** (up to a max-tokens cap of 100.0) while smoothly restricting long-term usage to an average steady refill rate. Primitives consume minimal database allocation space (`tokens` and `timestamp`).
* **Fixed Window (Rejected):** Highly vulnerable to a major traffic flaw called **Edge Bursts**, where a bot user can dump double their allocation capacity right at the borders of the transition minute clock boundaries, potentially crashing downstream APIs.
* **Sliding Window Log (Rejected):** Requires tracking a growing collection list of exact epoch timestamp variables for every click a user makes. Under high enterprise load conditions, this rapidly inflates database memory consumption, leading to scaling friction.

### High-Traffic Performance Controls
1. **Lazy Evaluation Math:** To avoid resource-heavy background continuous worker loops that drain server CPU cores, token regeneration math runs lazily and reactively *on-demand* exactly when a user hits an endpoint.
2. **Decoupled Asynchronous Telemetry Logs:** Writing log rows to a database introduces network latency. To keep member API responses lightning fast, the gateway captures request metadata and instantly pushes it to an isolated background thread worker pool via Spring's `@Async` architecture. The database write happens in the background, entirely unlinked from the user's web thread.

---

## 6. Deployment URL (Very Important)
* **Live Production Application Gateway UI Location Link:** [https://railway.app](https://guvi-production-5912.up.railway.app/)
