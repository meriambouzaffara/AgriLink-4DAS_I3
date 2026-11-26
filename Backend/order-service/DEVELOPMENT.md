# MS3 local stock simulation

Start locally with `mvn spring-boot:run -Dspring-boot.run.profiles=dev-mock`.
Without `dev-mock`, no `ProductClient` bean is configured; startup intentionally fails until an MS2 integration is supplied. No MS2 HTTP contract has been confirmed.

| ID | Fictional product | Unit price (TND) | Initial stock (kg) |
|---|---|---:|---:|
| 101 | Tomates | 5.000 | 100.000 |
| 102 | Pommes de terre | 3.000 | 150.000 |
| 103 | Carottes | 2.500 | 80.000 |
| 104 | Oignons | 4.000 | 120.000 |
| 105 | Pommes | 6.000 | 60.000 |

Mock stock and reservation records are in memory and reset on restart. A persisted confirmed order whose reservation disappeared after restart cannot be cancelled through the mock: release returns false and the order stays confirmed, preventing an artificial stock increase.

Orders start `PENDING`. Creation checks aggregate availability but does not reserve. Confirmation reserves all lines under a database order-row lock, then changes status. Cancellation releases a matching reservation only for `CONFIRMED`; `PENDING` cancellation releases nothing. Cancellation after preparation begins is rejected. This cancellation policy awaits team confirmation.

Amounts use `DECIMAL(15,3)` with `HALF_UP` on each subtotal. Overflow is rejected. Unit prices are captured when the order is created.

For this local mock, a failed local confirmation transaction releases its reservation, and a failed cancellation transaction attempts to restore it. These compensations are best effort. A real MS2 integration needs an agreed idempotent reservation protocol, failure recovery, and a distributed consistency strategy. MS4 delivery updates also need caller authorization before a production endpoint is exposed.

## REST API demonstration

Run from this directory: `mvn spring-boot:run -Dspring-boot.run.profiles=dev-mock`.
The `dev-mock` profile is required for the in-memory product client, simulated customer identity, product catalog endpoint, delivery simulation endpoint, and local CORS policy. No real MS1 authentication, MS2 HTTP integration, or MS4 authorization is implemented.

Customer requests require `X-Customer-Id: 12`. This header is **only a development simulation, not authentication**. Missing IDs return 401; malformed or non-positive IDs return 400. Outside `dev-mock`, the header never authorizes a customer request. The order body never accepts a customer ID as identity. The service checks ownership on individual order operations; another customer's history returns 404.

| Method | Endpoint | Result |
|---|---|---|
| POST | `/api/orders` | 201, order JSON, `Location: /api/orders/{id}` |
| GET | `/api/orders/{id}` | 200, owned order JSON |
| GET | `/api/orders/customer/{id}` | 200, newest-first JSON array |
| PATCH | `/api/orders/{id}/confirm` | 200, confirmed order JSON |
| PATCH | `/api/orders/{id}/cancel` | 200, cancelled order JSON |
| GET | `/api/dev/products` | 200, current mock product list; development only |
| PATCH | `/api/dev/orders/{id}/status` | 200, simulated delivery update; development only |

Create request example:

```http
POST /api/orders
Content-Type: application/json
X-Customer-Id: 12

{"lignes":[{"produitId":101,"quantite":10},{"produitId":102,"quantite":5}]}
```

The response has `id`, `clientId`, `dateCommande`, `montantTotal` (65.000 TND), `statut` (`PENDING`), and `lignes` with `id`, `produitId`, `quantite`, `prixUnitaire`, and `sousTotal`. The creation response also supplies a `Location` header. Error responses contain `timestamp`, `status`, `error`, `message`, `path`, and `fieldErrors`. Use [requests.http](requests.http) in IntelliJ to run the full workflow with generated IDs.

Example creation response (IDs and timestamp vary):

```json
{
  "id": 1,
  "clientId": 12,
  "dateCommande": "2026-11-26T12:00:00",
  "montantTotal": 65.000,
  "statut": "PENDING",
  "lignes": [
    {"id": 1, "produitId": 101, "quantite": 10.000, "prixUnitaire": 5.000, "sousTotal": 50.000},
    {"id": 2, "produitId": 102, "quantite": 5.000, "prixUnitaire": 3.000, "sousTotal": 15.000}
  ]
}
```

Delivery simulation accepts `{"statut":"IN_PREPARATION"}`, then `SHIPPED`, then `DELIVERED`, only after confirmation. It is an open local demonstration endpoint under `dev-mock`; do not expose this profile outside a trusted local environment.

Development CORS allows `http://localhost:5173` by default, with GET, POST, PATCH, OPTIONS and `Content-Type`, `X-Customer-Id` headers. Credentials are disabled. Override the comma-separated `agrilink.dev.allowed-origins` property to list other trusted local origins. No production CORS policy is configured.
