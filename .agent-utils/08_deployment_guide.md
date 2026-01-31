# Deployment Guide

> **AI Agent Instruction**: Deployment configuration for Restaurant Ordering System.

---

## 1. Architecture Overview

```
Single VPS Deployment (Cost-Optimized)
┌─────────────────────────────────────────┐
│                  VPS                    │
│  ┌────────────────────────────────┐     │
│  │   Spring Boot JAR              │     │
│  │   ├─ REST API                  │     │
│  │   ├─ WebSocket STOMP           │     │
│  │   └─ Static React (/static)    │     │
│  └────────────────────────────────┘     │
│              ↓                          │
│  ┌────────────────────────────────┐     │
│  │   PostgreSQL                   │     │
│  └────────────────────────────────┘     │
└─────────────────────────────────────────┘
```

---

## 2. Environment Profiles

### Development (H2)
```properties
# application-dev.properties
spring.datasource.url=jdbc:h2:file:./data/restaurant_dev
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true

# JWT
jwt.secret=dev-secret-key-change-in-prod
jwt.expiration=86400000
```

### Production (PostgreSQL)
```properties
# application-prod.properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# Payment callbacks
momo.partner-code=${MOMO_PARTNER_CODE}
momo.access-key=${MOMO_ACCESS_KEY}
momo.secret-key=${MOMO_SECRET_KEY}
zalopay.app-id=${ZALOPAY_APP_ID}
zalopay.key1=${ZALOPAY_KEY1}
zalopay.key2=${ZALOPAY_KEY2}
```

---

## 3. Build Commands

### Local Development
```bash
# Run with H2
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Access
# API: http://localhost:8080
# H2 Console: http://localhost:8080/h2-console
# Swagger: http://localhost:8080/swagger-ui.html
```

### Production Build
```bash
# Build JAR (skip tests for faster build)
./mvnw clean package -DskipTests

# Output: target/restaurant-backend-0.0.1-SNAPSHOT.jar

# Run with prod profile
java -jar target/restaurant-backend-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod
```

---

## 4. Environment Variables (Production)

| Variable | Description | Example |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/restaurant` |
| `DATABASE_USER` | Database username | `restaurant_user` |
| `DATABASE_PASSWORD` | Database password | `secure_password` |
| `JWT_SECRET` | Secret key for JWT | `your-256-bit-secret` |
| `MOMO_PARTNER_CODE` | MoMo partner code | `MOMOXXX` |
| `MOMO_ACCESS_KEY` | MoMo access key | `abc123` |
| `MOMO_SECRET_KEY` | MoMo secret key | `xyz789` |
| `ZALOPAY_APP_ID` | ZaloPay app ID | `123` |
| `ZALOPAY_KEY1` | ZaloPay key 1 | `abc` |
| `ZALOPAY_KEY2` | ZaloPay key 2 | `xyz` |

---

## 5. Database Setup

### PostgreSQL Installation (Ubuntu)
```bash
# Install PostgreSQL
sudo apt update
sudo apt install postgresql postgresql-contrib

# Create database and user
sudo -u postgres psql
CREATE DATABASE restaurant;
CREATE USER restaurant_user WITH ENCRYPTED PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE restaurant TO restaurant_user;
\q
```

### Run Migrations
```bash
# Flyway runs automatically on startup
# Or manually:
./mvnw flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/restaurant \
  -Dflyway.user=restaurant_user -Dflyway.password=secure_password
```

---

## 6. Systemd Service (Linux)

### Create service file
```ini
# /etc/systemd/system/restaurant.service
[Unit]
Description=Restaurant Ordering Backend
After=network.target postgresql.service

[Service]
User=restaurant
WorkingDirectory=/opt/restaurant
ExecStart=/usr/bin/java -jar restaurant-backend.jar --spring.profiles.active=prod
SuccessExitStatus=143
Restart=always
RestartSec=10

Environment=DATABASE_URL=jdbc:postgresql://localhost:5432/restaurant
Environment=DATABASE_USER=restaurant_user
Environment=DATABASE_PASSWORD=secure_password
Environment=JWT_SECRET=your-256-bit-secret

[Install]
WantedBy=multi-user.target
```

### Commands
```bash
# Enable and start
sudo systemctl enable restaurant
sudo systemctl start restaurant

# Check status
sudo systemctl status restaurant

# View logs
journalctl -u restaurant -f
```

---

## 7. Nginx Reverse Proxy (Optional)

```nginx
# /etc/nginx/sites-available/restaurant
server {
    listen 80;
    server_name your-domain.com;

    # WebSocket upgrade
    location /ws {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
    }

    # API and static files
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

## 8. Health Check

```bash
# Actuator health endpoint
curl http://localhost:8080/actuator/health

# Expected response
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

---

## 9. Deployment Checklist

### Pre-deployment
- [ ] All tests pass (`./mvnw test`)
- [ ] Build succeeds (`./mvnw clean package`)
- [ ] Environment variables configured
- [ ] Database created and accessible
- [ ] JWT_SECRET is strong (256-bit)

### Deployment
- [ ] Upload JAR to server
- [ ] Run migrations (automatic on startup)
- [ ] Start service
- [ ] Verify health check returns UP
- [ ] Test login endpoint
- [ ] Test WebSocket connection

### Post-deployment
- [ ] Monitor logs for errors
- [ ] Verify payment callbacks accessible
- [ ] Test full order flow

---

## 10. Troubleshooting

| Issue | Solution |
|-------|----------|
| Database connection failed | Check DATABASE_URL, user, password |
| JWT validation failed | Verify JWT_SECRET matches |
| WebSocket not connecting | Check Nginx WebSocket config |
| Migrations failed | Check Flyway migration files |
| 403 on endpoints | Verify role guards and JWT |
