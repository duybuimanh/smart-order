# smart-order
The system creates orders instantly, then sends follow-up tasks (email, inventory, logging) to a Redis/RabbitMQ queue. Background workers process these tasks asynchronously. Each order’s status can be tracked, and an optional real-time admin dashboard shows system activity.
