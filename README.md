# Warehouse Management Application

## Project Description
This school project is part of the **Software Architecture course (2024-2025)**. The **Warehouse Management Application** tracks inventory levels, manages storage operations, and calculates daily costs and commissions for Krystal distributie Groep (KdG).

## Technologies Used
- **Backend**: Spring Boot (Java/Kotlin)
- **Database**: PostgreSQL (Warehouse schema)
- **Queue System**: RabbitMQ
- **Identity Provider**: Keycloak
- **Build Tool**: Maven/Gradle
- **Logging**: Integrated with Spring Boot logging

## Features
- Inventory tracking for multiple mineral types per warehouse.
- Automated daily storage cost calculations.
- Commission tracking for fulfilled Purchase Orders.
- Integration with land and water logistics systems.
- RESTful API endpoints secured with OAuth2.

## Related Projects
This project is part of a larger system for managing the logistics of Krystal distributie Groep (KdG). The following related projects provide additional functionality:
- **[Land Logistics Application](https://github.com/softtagz-sys/sa-land)**: Manages truck-based deliveries and inbound logistics.
- **[Water Logistics Application](https://github.com/softtagz-sys/sa-water)**: Handles ship docking, inspections, bunkering, and loading operations.

## Contributors
- [Nicolas Verachtert](https://github.com/NicolasVerachtert)
- [Kobe Ponet](https://github.com/softtagz-sys)
