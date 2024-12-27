# Multi Currency Wallet

Multi currency wallet is project simulating features a multi-currency wallet (like Wise) using event driven microservices with Kotlin + Spring Boot + GraphQL + Kafka

### Modules

* common-library: A library to be shared among all services, to simplify development
* events-library: A library for publishing events using outboxing pattern to make events consistent
* customers: Domain to manage customers features
* multi-accounts: TBD


### Customers

The user identity is managed by Auth0, so please create an (free) Auth0 account and configure the following environment properties:

```sh
AUTH0.CLIENT.ID=<auth0-client-id>;
AUTH0.CLIENT.SECRET=<auth0-client-secret>;
AUTH0.DOMAIN=<auth0-domain>
```