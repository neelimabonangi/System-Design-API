# Pick-a-Spot API — High-Traffic System Design

## Project Overview
This is a **Spring Boot** based backend system designed to handle **heavy traffic** from cranes and yard apps calling the `/pickSpot` API simultaneously.  
The system is optimized to stay **fast**, **resilient**, **scalable**, and **observable** under peak loads.

## System Architecture
                   Internet
                       |
               +-------▼--------+
               |      Nginx      |  (TLS Termination & Load Balancer)
               |  Reverse Proxy  |
               +-------▲--------+
                       |
             +---------┼----------+
             |         |          |
         +---▼---+   +---▼---+   +---▼---+
         |  API  |   |  API  |   |  API  |  (Stateless Spring Boot Pods)
         |   #1  |   |   #2  |   |   #3  |  (Add more pods as needed)
         +-------+   +-------+   +-------+
             |         |
   +-------------------+-------------------+
   |      In-Memory Cache (Yard Map)       |
   |   - HashMap with 10s refresh interval  |
   +---------------------------------------+

### Key Components
| Component             | Technology                     | Reason                                           |
|------------------------|---------------------------------|--------------------------------------------------|
| Load Balancer          | Nginx                           | TLS termination and round-robin load balancing.  |
| API Layer              | Spring Boot (Fat JARs)          | Lightweight, stateless, fast REST APIs.          |
| Cache                  | In-memory HashMap               | Fast O(1) lookups, refreshed every 10 seconds.   |
| Observability          | Prometheus + Grafana            | Metrics collection and dashboard visualization. |
| Deployment             | Docker + Kubernetes             | Easy scaling and blue-green deployments.         |

---

## How It Handles Heavy Traffic

| Scenario         | RPS  | Strategy |
|------------------|------|----------|
| Normal Day       | 100  | 3 API pods handle (~40 rps each) |
| Peak Monsoon     | 500  | Auto-scale to 5 API pods |

- **Stateless API**: Each request is independent — no session dependency.
- **Hot path optimization**: No DB lookup during request; cached yard map used.
- **Quick Scaling**: Auto-scale on CPU > 70%.

---

## Concurrency Model

- **Nginx** distributes load evenly (Round-robin).
- **Each API pod** can handle ~100–120 rps.
- **Kubernetes HPA** (Horizontal Pod Autoscaler) adds more pods if CPU usage > 70%.

---

## Failure Handling

- **Pod Crash**: Nginx automatically stops routing to dead pods. Other pods pick up the load.
- **Redis/Cache Issue**: API falls back to the last good map. Error is logged and admin alerted.
- **Node Crash**: Kubernetes automatically reschedules pods.

---

## Scaling Strategy

- **Horizontal Scaling**: Add pods when CPU > 70%.
- **Blue-Green Deployment**: Deploy new version without downtime — shift traffic gradually after validation.

---

## Metrics and Alerts

| Metric | Threshold | Action |
|--------|-----------|--------|
| P95 Latency | > 300ms | Alert and scale up |
| Error Rate | > 1% | Alert and investigate |
| CPU Usage | > 70% | Auto-scale |

Example Alert:  
> **If P95 latency exceeds 400 ms for 5 minutes, trigger PagerDuty alert.**

---

## Running the Project Locally

1. **Clone the Repo**  
   ```bash
   git clone <your-repo-link>
   cd pick-a-spot-api
Run with Maven

mvn spring-boot:run

Access the API

POST http://localhost:8080/pickSpot

Monitor:    

Metrics available at /actuator/prometheus for Prometheus scraping.

Future Improvements:    

Add Redis for distributed caching.

Add Kafka for async retries and audit logging.

Implement rolling updates for smoother deploys.



