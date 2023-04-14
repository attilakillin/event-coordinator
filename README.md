# Event Coordinator

## Environment settings

To make testing and deployment from a fresh `clone` easier, the following environment setting files
are committed to the repository:

* `/.env`, which sets variables used by `/docker-compose.yml`.
* `/coordinator-articles-backend/docker.properties`, which uses the values set earlier.
* `/coordinator-frontend/.env`, which sets variables used by NextJS.

Obviously, the values used in these files are by no means safe, and should be changed for proper deployment.
