# Mel Spiritual Portal - Core API Gateway & Rate Limiter

A high-performance, production-ready API Gateway system built using Java Spring Boot and MongoDB Atlas. This microservice architecture manages traffic routing, member identity context tracking, and non-blocking asynchronous analytical auditing logs.

## 🚀 Live Environment Urls
* **Public Dashboard UI Web Address:** <PASTE_YOUR_GENERATED_RAILWAY_URL_HERE>
* **Guarded Target Core Resource Endpoint:** `<PASTE_YOUR_GENERATED_RAILWAY_URL_HERE>/api/v1/meditation/session`

## 🛠️ Infrastructure Tech Stack
* **Runtime Language Engine:** Java (Targeting 17/21 bytecode compilation)
* **Framework Layer:** Spring Boot 3.4.x / Web MVC Core Architecture
* **Analytical Persistence Core:** MongoDB Atlas Cloud Instance (Implicitly created schemas)

## 📌 Gateway Core Endpoint Documentation

### 1. Spiritual Meditation Resource Processing Node
* **URL Destination Path:** `/api/v1/meditation/session`
* **Network Method:** `GET`
* **Required Security Header Key:** `X-Member-Token: <Your_Unique_Member_ID_String>`
* **Status Action Outcomes:**
  * `200 OK`: Successful authorization verification. Member transaction approved, 1 token credit deducted.
  * `401 Unauthorized`: Denied entry request. Missing or empty member identification token context.
  * `429 Too Many Requests`: Structural protection ceiling tripped. The profile has consumed its allocated 100 request token balance allowance.

## 🧠 System Design Choices & Trade-offs
1. **Token Bucket Algorithmic Strategy**: Chosen over Fixed Window tracking because it smoothly authorizes necessary customer burst traffic periods without allowing high traffic peaks to overload down-stream database nodes. Primitives consume minimal database allocation space.
2. **Lazy Refill Computation**: The gateway does not execute resource-heavy background continuous loops to increment balances. Refill calculations run lazily and reactively *on-demand* exactly when a user hits an endpoint, keeping CPU utilization incredibly lean.
3. **Decoupled Asynchronous Persistence Logs**: To keep client transaction speeds lightning fast, logging data mapping is handed off via Spring's `@Async` architecture to an isolated multi-threaded background worker pool. This ensures network writing delays never slow down the customer request lifecycle.
