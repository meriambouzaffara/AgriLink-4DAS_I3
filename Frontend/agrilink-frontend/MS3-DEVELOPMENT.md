# MS3 standalone cart (development only)

Start XAMPP MariaDB, then run the backend from `Backend/order-service`:

```sh
mvn spring-boot:run -Dspring-boot.run.profiles=dev-mock
```

Run the shared Angular app from `Frontend/agrilink-frontend`:

```sh
npm start
```

Open http://localhost:4200/panier. The catalog is fetched from `http://localhost:8083/api/dev/products`; no product fixture is duplicated in Angular. Add quantities, review the estimated TND total, and click **Commander** to create a real PENDING order. The backend returns the authoritative total. Creation does not reserve stock; confirmation is a separate operation. The cart is stored locally per development customer under a versioned `agrilink.ms3.dev-cart.v1.customer-*` key, and only clears after a successful order response.

The API configuration token in `src/app/config/order-api.config.ts` has development defaults: backend URL `http://localhost:8083` and simulated customer 12. Override that token to use another local development customer. The `X-Customer-Id` header is a **development simulation, not authentication**. Angular production builds disable the simulated identity using `isDevMode()`; production customer operations require a future MS1 integration.

The existing marketplace's product and farmer-offer identifiers are strings/UUIDs, whereas this standalone MS3 API accepts numeric product IDs. The marketplace summary links to the explicitly labelled development cart, but its offers are not added to it. A verified MS2 offer, price and atomic stock-reservation contract is required before connecting real marketplace offers. Never convert UUIDs to numeric IDs or substitute development products.

The development CORS allowlist includes `http://localhost:4200` and `http://localhost:5173`. Mock stock is in memory and resets on backend restart; persisted orders remain in MariaDB. Run focused cart tests with `npm run test:ms3` and compile with `npm run build`.
