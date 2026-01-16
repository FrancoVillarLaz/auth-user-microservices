# auth-user-microservices

Arquitectura de microservicios basada en Spring Boot y Quarkus que proporciona autenticación y gestión de usuarios como servicios independientes y escalables. Incluye un API Gateway centralizado para enrutamiento y un servidor Eureka para descubrimiento dinámico de servicios. Diseñado para ser la base de sistemas distribuidos modernos con soporte para Redis, MySQL y mensajería con Kafka.

## 🏗️ Arquitectura

```
┌─────────────────┐
│   API Gateway   │  Puerto 8100
│ (Spring Cloud)  │
└────────┬────────┘
         │
    ┌────┴─────────────────────────┐
    │                               │
┌───▼──────────┐          ┌────────▼────────┐
│ Auth Service │          │  User Service   │
│  (Quarkus)   │          │ (Spring Boot)   │
│  Puerto 8102 │          │  Puerto 8083    │
└──────────────┘          └─────────────────┘
         │                         │
         └────────┬────────────────┘
                  │
         ┌────────▼────────┐
         │ Eureka Server   │  Puerto 8761
         │ (Spring Cloud)  │
         └─────────────────┘
```

## 🚀 Tecnologías

### Servicios
- **Auth Service**: Quarkus 3.29.0 (compilación nativa) + JWT
- **User Service**: Spring Boot 3.x
- **Gateway**: Spring Cloud Gateway
- **Eureka Server**: Spring Cloud Netflix Eureka

### Infraestructura
- **Base de datos**: MySQL 8.0
- **Cache**: Redis 7
- **Mensajería**: Apache Kafka + Zookeeper
- **Monitoreo**: Kafka UI, Adminer
- **Orquestación**: Docker Compose

### Stack tecnológico
- Java 21
- Maven
- Docker & Docker Compose
- Hibernate ORM / Panache
- Reactive WebFlux

## 📋 Prerrequisitos

- Docker y Docker Compose instalados
- Java 21 (para desarrollo local)
- Maven 3.9+ (para desarrollo local)
- GraalVM (opcional, para compilación nativa de Quarkus)

## 🔧 Configuración

### Variables de entorno

Crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```bash
# Base de datos
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_password_seguro
DB_ROOT_PASSWORD=root_password_seguro

# Redis
REDIS_PASSWORD=redis_password_seguro

# JWT (solo para auth-service)
JWT_SECRET=tu_clave_secreta_jwt_muy_larga_y_segura
```

### Puertos utilizados

| Servicio          | Puerto |
|-------------------|--------|
| Gateway           | 8100   |
| Eureka Server     | 8761   |
| Auth Service      | 8102   |
| User Service      | 8083   |
| MySQL             | 3306   |
| Redis             | 6379   |
| Kafka             | 9092   |
| Kafka (externo)   | 29092  |
| Kafka UI          | 8082   |
| Adminer           | 8081   |
| Zookeeper         | 2181   |

## 🚀 Inicio rápido

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/auth-user-microservices.git
cd auth-user-microservices
```

### 2. Configurar variables de entorno

```bash
cp .env.example .env
# Edita el archivo .env con tus credenciales
```

### 3. Levantar todos los servicios

```bash
docker-compose up -d
```

### 4. Verificar estado de servicios

```bash
# Ver logs
docker-compose logs -f

# Verificar contenedores
docker-compose ps
```

### 5. Acceder a las interfaces

- **Eureka Dashboard**: http://localhost:8761
- **Kafka UI**: http://localhost:8082
- **Adminer** (MySQL UI): http://localhost:8081
- **Gateway**: http://localhost:8100

## 📡 Endpoints principales

### Auth Service (vía Gateway)

```bash
# Registro de usuario
POST http://localhost:8100/api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123"
}

# Login
POST http://localhost:8100/api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123"
}

# Refresh token
POST http://localhost:8100/api/auth/refresh
Authorization: Bearer {refresh_token}
```

### User Service (vía Gateway)

```bash
# Obtener perfil de usuario
GET http://localhost:8100/api/users/me
Authorization: Bearer {access_token}

# Actualizar perfil
PUT http://localhost:8100/api/users/me
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "name": "Nombre Actualizado"
}
```

## 🔨 Desarrollo

### Compilar servicios individualmente

```bash
# Auth Service (Quarkus)
cd auth-service
./mvnw clean package

# Gateway
cd Gateway
./mvnw clean package

# User Service
cd modulo_user
./mvnw clean package

# Eureka Server
cd EurekaServer
./mvnw clean package
```

### Ejecutar localmente (sin Docker)

1. Asegúrate de tener MySQL y Redis corriendo
2. Configura las variables de entorno en `application.properties` de cada servicio
3. Ejecuta en este orden:

```bash
# 1. Eureka Server
cd EurekaServer && ./mvnw spring-boot:run

# 2. Gateway
cd Gateway && ./mvnw spring-boot:run

# 3. Auth Service
cd auth-service && ./mvnw quarkus:dev

# 4. User Service
cd modulo_user && ./mvnw spring-boot:run
```

### Modo desarrollo con hot-reload

```bash
# Quarkus (Auth Service)
cd auth-service
./mvnw quarkus:dev

# Spring Boot DevTools ya está incluido en los otros servicios
```

## 🧪 Testing

```bash
# Ejecutar tests de un servicio
cd auth-service
./mvnw test

# Ejecutar tests de todos los servicios
./mvnw test -f auth-service/pom.xml
./mvnw test -f Gateway/pom.xml
./mvnw test -f modulo_user/pom.xml
./mvnw test -f EurekaServer/pom.xml
```

## 📦 Compilación nativa (Quarkus)

El Auth Service puede compilarse a ejecutable nativo para mayor rendimiento:

```bash
cd auth-service
./mvnw package -Pnative

# Con Docker
docker build -f Dockerfile.native -t auth-service:native .
```

## 🔒 Seguridad

- ✅ Autenticación basada en JWT
- ✅ Tokens de acceso y refresh
- ✅ Passwords hasheados con BCrypt
- ✅ Variables de entorno para secretos
- ✅ CORS configurable
- ⚠️ **IMPORTANTE**: Cambia todos los valores placeholder en `.env` antes de producción

## 🐳 Docker

### Construir imágenes individuales

```bash
# Auth Service
docker build -t auth-service:latest -f auth-service/Dockerfile.native auth-service/

# Gateway
docker build -t gateway:latest Gateway/

# User Service
docker build -t user-service:latest modulo_user/

# Eureka Server
docker build -t eureka-server:latest EurekaServer/
```

### Comandos útiles

```bash
# Detener todos los servicios
docker-compose down

# Eliminar volúmenes (⚠️ borra los datos)
docker-compose down -v

# Reconstruir servicios
docker-compose up -d --build

# Ver logs de un servicio específico
docker-compose logs -f auth-service
```

## 🗄️ Base de datos

### Acceder a MySQL con Adminer

1. Abre http://localhost:8081
2. Usa las credenciales configuradas en `.env`:
   - Sistema: MySQL
   - Servidor: mysql
   - Usuario: tu `DB_USERNAME`
   - Contraseña: tu `DB_PASSWORD`
   - Base de datos: auth_service_db

### Backup de base de datos

```bash
docker exec mysql mysqldump -u root -p${DB_ROOT_PASSWORD} auth_service_db > backup.sql
```

### Restaurar base de datos

```bash
docker exec -i mysql mysql -u root -p${DB_ROOT_PASSWORD} auth_service_db < backup.sql
```

## 📊 Monitoreo

### Kafka UI

Accede a http://localhost:8082 para:
- Visualizar topics
- Monitorear mensajes
- Ver consumidores y productores
- Configurar topics

### Health checks

```bash
# Eureka
curl http://localhost:8761/actuator/health

# Gateway (si actuator está habilitado)
curl http://localhost:8100/actuator/health
```

## 🛠️ Troubleshooting

### Los servicios no se registran en Eureka

- Verifica que Eureka Server esté corriendo: `docker-compose ps eureka-server`
- Revisa logs: `docker-compose logs eureka-server`
- Espera ~30 segundos para el registro inicial

### Errores de conexión a base de datos

- Verifica que MySQL esté saludable: `docker-compose ps mysql`
- Comprueba credenciales en `.env`
- Revisa logs: `docker-compose logs mysql`

### Puerto ya en uso

```bash
# Encuentra el proceso usando el puerto
sudo lsof -i :8100

# O mata todos los contenedores
docker-compose down
```

### Kafka no se conecta

- Verifica que Zookeeper esté corriendo primero
- Espera a que Kafka esté completamente iniciado (~30 segundos)
- Revisa logs: `docker-compose logs kafka`

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'feat: add amazing feature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 👥 Autores

- Franco - Desarrollo inicial

## 📚 Recursos adicionales

- [Documentación de Quarkus](https://quarkus.io/guides/)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Netflix Eureka](https://github.com/Netflix/eureka/wiki)
- [Apache Kafka](https://kafka.apache.org/documentation/)
