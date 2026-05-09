# castellanos-post1-u12

Laboratorio de la Unidad 12, modo WEB, orientado a contenedorización y despliegue en Railway.

## Objetivo

Contenedorizar una aplicación Spring Boot con Docker multi-stage, separar los ambientes de desarrollo y producción mediante perfiles, orquestar localmente con Docker Compose y dejar el proyecto listo para despliegue en Railway con PostgreSQL y endpoints REST funcionales.

## Arquitectura

```text
Cliente HTTP
	|
	v
Controller REST
	|
	v
Service
	|
	v
Repository JPA
	|
	v
Base de datos
	|-- Dev: H2 en memoria
	'-- Prod: PostgreSQL
```

## Tecnologías y versiones

| Componente     | Versión     |
| -------------- | ----------- |
| Java           | 21          |
| Spring Boot    | 3.4.5       |
| Maven          | 3.9.12      |
| PostgreSQL     | 16-alpine   |
| H2             | 2.x         |
| Docker         | Multi-stage |
| Docker Compose | 3.9         |

## Estructura del proyecto

```text
.
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── README.md
├── src
│   ├── main
│   │   ├── java/co/edu/udes/castellanos/post1u12
│   │   │   ├── domain
│   │   │   ├── repository
│   │   │   ├── service
│   │   │   └── web
│   │   └── resources
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       ├── data.sql
│   │       └── db/migration
│   └── test
└── .dockerignore
```

## Rúbrica y evidencia

| Criterio                     | Evidencia en el repositorio                                                                                                                                                              |
| ---------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Dockerfile multi-stage       | [Dockerfile](Dockerfile) con etapa builder y etapa JRE                                                                                                                                   |
| Usuario no root              | `USER spring` en [Dockerfile](Dockerfile)                                                                                                                                                |
| Limpieza del contexto Docker | [.dockerignore](.dockerignore)                                                                                                                                                           |
| Orquestación local           | [docker-compose.yml](docker-compose.yml) con app y PostgreSQL                                                                                                                            |
| Perfil de producción         | [application-prod.properties](src/main/resources/application-prod.properties)                                                                                                            |
| API REST                     | [ProductoController](src/main/java/co/edu/udes/castellanos/post1u12/web/ProductoController.java)                                                                                         |
| Persistencia y validación    | [ProductoServiceImpl](src/main/java/co/edu/udes/castellanos/post1u12/service/ProductoServiceImpl.java) y [Producto](src/main/java/co/edu/udes/castellanos/post1u12/domain/Producto.java) |
| Pruebas                      | [Post1U12ApplicationTests](src/test/java/co/edu/udes/castellanos/post1u12/Post1U12ApplicationTests.java)                                                                                 |

## Decisiones técnicas

- Se usó una arquitectura por capas para separar controladores, lógica de negocio y acceso a datos.
- El perfil `dev` usa H2 para pruebas locales rápidas, con carga de datos iniciales mediante `data.sql`.
- El perfil `prod` usa PostgreSQL con `ddl-auto=update` y Flyway deshabilitado en Railway para evitar el bloqueo con PostgreSQL 18.3.
- Las migraciones SQL de producción quedan como referencia en `src/main/resources/db/migration`; en Railway el esquema lo crea JPA al arrancar.
- El Dockerfile copia primero `pom.xml` para aprovechar la caché de capas y reduce la imagen final a una base JRE.

## Prerrequisitos

- Java 21.
- Maven 3.9.12 o superior.
- Docker Desktop en ejecución.
- Git.
- Cuenta de Railway vinculada a GitHub.

## Ejecución local sin Docker

1. Ejecutar las pruebas:

```bash
mvn test
```

2. Iniciar la aplicación en desarrollo:

```bash
mvn spring-boot:run
```

3. Verificar el healthcheck:

```bash
curl http://localhost:8080/actuator/health
```

Respuesta esperada:

```json
{ "status": "UP" }
```

## Ejecución con Docker

1. Construir la imagen:

```bash
docker build -t castellanos-post1-u12:local .
```

2. Levantar el stack completo:

```bash
docker compose up -d --build
```

3. Revisar el estado de los contenedores:

```bash
docker compose ps
```

4. Validar el healthcheck:

```bash
curl http://localhost:8080/actuator/health
```

5. Probar los endpoints REST:

```bash
curl http://localhost:8080/api/productos
curl http://localhost:8080/api/productos/1
curl -X POST http://localhost:8080/api/productos -H "Content-Type: application/json" -d "{\"nombre\":\"Tablet 10 pulgadas\",\"descripcion\":\"Tablet para estudio y consumo multimedia\",\"precio\":1299900.00,\"stock\":4}"
```

## Endpoints principales

| Método | Ruta                | Descripción                         |
| ------ | ------------------- | ----------------------------------- |
| GET    | /api/productos      | Lista todos los productos           |
| GET    | /api/productos/{id} | Obtiene un producto por id          |
| POST   | /api/productos      | Crea un producto                    |
| PUT    | /api/productos/{id} | Actualiza un producto               |
| DELETE | /api/productos/{id} | Elimina un producto                 |
| GET    | /actuator/health    | Verifica el estado de la aplicación |

## Despliegue en Railway

1. Conectar el repositorio de GitHub en Railway y permitir el despliegue automático desde el `Dockerfile`.
2. Agregar un servicio PostgreSQL en el proyecto de Railway.
3. Configurar estas variables en el servicio de la aplicación:

| Variable               | Valor esperado                                                                                         |
| ---------------------- | ------------------------------------------------------------------------------------------------------ |
| SPRING_PROFILES_ACTIVE | prod                                                                                                   |
| DATABASE_URL           | `jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}?sslmode=require` |
| DB_USER                | `${{Postgres.PGUSER}}`                                                                                 |
| DB_PASS                | `${{Postgres.PGPASSWORD}}`                                                                             |

Importante: sustituye `Postgres` por el nombre exacto del servicio de PostgreSQL que ves en Railway. El nombre debe coincidir carácter por carácter con la tarjeta del servicio. No copies la variable `DATABASE_URL` que muestra el servicio de PostgreSQL tal como viene, porque esa es para conexión interna y no tiene formato JDBC; en la app usa la referencia construida arriba.

En el panel de Railway, pega cada dato en dos campos separados: `Key` y `Value`. En `Value` pega solo el contenido, sin repetir el nombre de la variable. Por ejemplo, en `DATABASE_URL` el valor debe empezar directamente con `jdbc:postgresql://`, no con `DATABASE_URL=`.

4. Generar el dominio público desde Networking.
5. Verificar:

```bash
curl https://tu-dominio.up.railway.app/actuator/health
curl https://tu-dominio.up.railway.app/api/productos
```

## Pruebas ejecutadas

- `mvn test` ejecutado correctamente.
- Se validó carga de contexto Spring Boot.
- Se validó listado de productos sembrados.
- Se validó consulta por id de un producto sembrado.
- Se validó creación de un nuevo producto.

## Problemas frecuentes

- Si `/actuator/health` no responde, revisar que `SPRING_PROFILES_ACTIVE=prod` esté configurado y que la base PostgreSQL esté disponible.
- Si la aplicación no encuentra `productos`, revisar que las migraciones o la inicialización de datos hayan corrido correctamente.
- Si Docker Compose no levanta, revisar que Docker Desktop esté en ejecución.

## Evidencia pendiente para la entrega final

- Agregar la URL pública real de Railway en el informe final.
- Guardar capturas del panel de Railway, del healthcheck y de al menos tres endpoints en la carpeta `capturas/` antes de generar el PDF.
