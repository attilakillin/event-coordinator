# A Makefile with multiple targets to ease building and deployment.
# Use the various targets to rebuild and redeploy specific parts

-include .env

.PHONY: $(MAKECMDGOALS)

# Build all components
build-all: build-articles-backend build-auth-backend build-events-backend build-participants-backend \
		   build-frontend build-proxy

# Create databases
create-dbs: launch-network create-articles-db create-events-db create-participants-db

# Launch all components (databases are not recreated)
launch-all: launch-network launch-articles-db launch-events-db launch-participants-db \
			launch-articles-backend launch-auth-backend launch-events-backend launch-participants-backend \
			launch-frontend launch-proxy

# Stop all components (databases are not removed)
stop-all: stop-frontend stop-proxy \
	      stop-articles-backend stop-auth-backend stop-events-backend stop-participants-backend \
		  stop-articles-db stop-events-db stop-participants-db

# Remove databases
remove-dbs: remove-articles-db remove-events-db remove-participants-db remove-network

# Build articles backend component
build-articles-backend:
	cd coordinator-articles-backend && \
	./gradlew bootJar && \
	docker build \
		-f "Dockerfile-$(BUILD_PHASE_LOCATION)" \
		-t coordinator-articles-backend \
		--build-arg config=docker.yml \
		.

# Build auth backend component
build-auth-backend:
	cd coordinator-auth-backend && \
	./gradlew bootJar && \
	docker build \
		-f "Dockerfile-$(BUILD_PHASE_LOCATION)" \
		-t coordinator-auth-backend \
		--build-arg config=docker.yml \
		.

 # Build events backend component
build-events-backend:
	cd coordinator-events-backend && \
	./gradlew bootJar && \
	docker build \
		-f "Dockerfile-$(BUILD_PHASE_LOCATION)" \
		-t coordinator-events-backend \
		--build-arg config=docker.yml \
		.

# Build participant backend component
build-participants-backend:
	cd coordinator-participants-backend && \
	./gradlew bootJar && \
	docker build \
		-f "Dockerfile-$(BUILD_PHASE_LOCATION)" \
		-t coordinator-participants-backend \
		--build-arg config=docker.yml \
		.

# Build frontend component
build-frontend:
	cd coordinator-frontend && \
	npm run build && \
	docker build \
		-f "Dockerfile-$(BUILD_PHASE_LOCATION)" \
		-t coordinator-common-frontend \
		.

# Build proxy component
build-proxy:
	cd coordinator-proxy && \
	docker build \
		-t coordinator-common-proxy \
		.

# Create internal docker network
launch-network:
	docker network create internal || true

# Create articles db component
create-articles-db:
	docker run -d \
		--network internal \
		--name articles-db \
		--env MYSQL_DATABASE=articles \
        --env MYSQL_ROOT_PASSWORD=$(ARTICLES_DB_ROOT_PASSWORD) \
        --env MYSQL_USER=$(ARTICLES_DB_USER) \
        --env MYSQL_PASSWORD=$(ARTICLES_DB_PASS) \
		mysql:8.0

# Create events db component
create-events-db:
	docker run -d \
		--network internal \
		--name events-db \
		--env MYSQL_DATABASE=events \
        --env MYSQL_ROOT_PASSWORD=$(EVENTS_DB_ROOT_PASSWORD) \
        --env MYSQL_USER=$(EVENTS_DB_USER) \
        --env MYSQL_PASSWORD=$(EVENTS_DB_PASS) \
		mysql:8.0

# Create participants db component
create-participants-db:
	docker run -d \
		--network internal \
		--name participants-db \
		--env MYSQL_DATABASE=participants \
        --env MYSQL_ROOT_PASSWORD=$(PARTICIPANTS_DB_ROOT_PASSWORD) \
        --env MYSQL_USER=$(PARTICIPANTS_DB_USER) \
        --env MYSQL_PASSWORD=$(PARTICIPANTS_DB_PASS) \
		mysql:8.0

# Launch articles db component
launch-articles-db:
	docker start articles-db || true

# Launch events db component
launch-events-db:
	docker start events-db || true

# Launch participants db component
launch-participants-db:
	docker start participants-db || true

# Launch articles backend component
launch-articles-backend:
	docker rm -f articles-backend || true
	docker run -d \
		--network internal \
		--name articles-backend \
		coordinator-articles-backend

# Launch auth backend component
launch-auth-backend:
	docker rm -f auth-backend || true
	docker run -d \
		--network internal \
		--name auth-backend \
		coordinator-auth-backend

# Launch events backend component
launch-events-backend:
	docker rm -f events-backend || true
	docker run -d \
		--network internal \
		--name events-backend \
		coordinator-events-backend

# Launch participants backend component
launch-participants-backend:
	docker rm -f participants-backend || true
	docker run -d \
		--network internal \
		--name participants-backend \
		coordinator-participants-backend

# Launch frontend component
launch-frontend:
	docker rm -f common-frontend || true
	docker run -d \
		--network internal \
		--name common-frontend \
		coordinator-common-frontend


# Launch proxy component
launch-proxy:
	docker rm -f common-proxy || true
	docker run -d \
		--network internal \
		--name common-proxy \
		-p 80:80 \
		coordinator-common-proxy

# Stop articles db component
stop-articles-db:
	docker stop articles-db || true

# Stop events db component
stop-events-db:
	docker stop events-db || true

# Stop participants db component
stop-participants-db:
	docker stop participants-db || true


# Stop articles backend component
stop-articles-backend:
	docker stop articles-backend || true

# Stop auth backend component
stop-auth-backend:
	docker stop auth-backend || true

# Stop events backend component
stop-events-backend:
	docker stop events-backend || true

# Stop participants backend component
stop-participants-backend:
	docker stop participants-backend || true

# Stop frontend component
stop-frontend:
	docker stop common-frontend || true

# Stop proxy component
stop-proxy:
	docker stop common-proxy || true

# Remove internal docker network
remove-network:
	docker network rm internal || true

# Remove articles db content
remove-articles-db:
	docker rm -f articles-db || true

# Remove events db content
remove-events-db:
	docker rm -f events-db || true

# Remove participants db content
remove-participants-db:
	docker rm -f participants-db || true
