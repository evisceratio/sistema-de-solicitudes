# Sistema de Solicitudes de Retiro — Microservicios Spring Boot

> **Evaluación Parcial N.º 2 (JVY0101)** — Java: Diseño y Construcción de Soluciones Nativas en Nube.  
> Implementación local de cuatro microservicios REST, con persistencia independiente en Docker, Java 21 y Maven.

## 1. Alcance y estado

El proyecto permite gestionar **usuarios, rutas de retiro, solicitudes y flota de vehículos**. Se ha organizado siguiendo las capas `model`, `repository`, `service` y `controller`.

| Microservicio | Responsabilidad | Persistencia | Puerto |
| --- | --- | --- | --- |
| `ms-auth` | CRUD de usuarios y login con JWT | PostgreSQL (`auth_db`) | `8081` |
| `ms-rutas` | CRUD de rutas con paradas | PostgreSQL (`rutas_db`) | `8082` |
| `ms-solicitudes` | CRUD de solicitudes de retiro | MongoDB (`solicitudes_db`) | `8083` |
| `ms-flota` | CRUD de vehículos y movimientos | Oracle Database Free (`FREEPDB1`) | `8084` |

**Alcance de esta entrega:** las pruebas descritas utilizan los **puertos directos** de los cuatro microservicios. **Eureka Server y API Gateway no forman parte del recorrido funcional documentado**, porque su integración está pendiente. RabbitMQ se incluye en el entorno Docker; la publicación y el consumo de eventos solo deben demostrarse si efectivamente se implementaron y verificaron.

> Este README describe el diseño y las pruebas previstas para el código trabajado en el proyecto. Antes de entregar, ejecutar cada prueba y ajustar cualquier nombre de clase, ruta o configuración que sea distinto en el repositorio real. Los resultados HTTP que se indican son **esperados**, no una certificación de ejecución automática.

## 2. Requisitos previos

- **JDK 21** instalado y seleccionado en IntelliJ IDEA (`Project SDK`).
- **Maven** disponible como `mvn` o Maven Wrapper (`mvnw.cmd`).
- **Docker Desktop** con Docker Compose.
- **IntelliJ IDEA** u otro IDE compatible con Java/Spring Boot.
- **Postman** o una herramienta similar para peticiones HTTP.
- **Git** para clonar y versionar el proyecto.

Comprobar las herramientas:

```bash
java -version
mvn -version
docker compose version
git --version
```

## 3. Estructura de directorios

Estructura de referencia para un repositorio que agrupe los microservicios:

```text
sistema-retiros/
├── README.md
├── docker-compose.yml
├── ms-auth/
│   ├── pom.xml
│   └── src/main/java/cl/demo/auth/
│       ├── model/
│       ├── repository/
│       ├── service/
│       ├── controller/
│       ├── dto/
│       └── config/
├── ms-rutas/
│   ├── pom.xml
│   └── src/main/java/cl/demo/rutas/
│       ├── model/
│       ├── repository/
│       ├── service/
│       └── controller/
├── ms-solicitudes/
│   ├── pom.xml
│   └── src/main/java/cl/demo/solicitudes/
│       ├── model/
│       ├── repository/
│       ├── service/
│       └── controller/
└── ms-flota/
    ├── pom.xml
    └── src/main/java/cl/demo/flota/
        ├── model/
        ├── repository/
        ├── service/
        ├── controller/
        └── dto/
```

Si cada microservicio se entrega en **un repositorio diferente**, conservar las instrucciones propias de cada servicio y agregar sus enlaces en el documento de entrega. La pauta académica solicita los enlaces a los repositorios desarrollados.

### Separación de capas

- **`model`**: entidades JPA (`@Entity`) o documentos MongoDB (`@Document`).
- **`repository`**: interfaces Spring Data (`JpaRepository` / `MongoRepository`).
- **`service`**: operaciones de negocio y validaciones.
- **`controller`**: endpoints REST y respuestas HTTP.
- **`dto`**, cuando corresponde: datos de entrada/salida para evitar exponer información interna, como hashes de contraseña.

Relaciones relacionales utilizadas en el diseño: `Ruta` → `Parada` (`@OneToMany` / `@ManyToOne`) y `Movimiento` → `Vehiculo` (`@ManyToOne`). La solicitud de MongoDB conserva los identificadores de las otras entidades, **sin crear claves foráneas entre bases de datos de distintos microservicios**.

## 4. Clonar y ejecutar desde cero

### 4.1. Clonar

Reemplazar la URL por el enlace real del repositorio:

```bash
git clone <URL_DEL_REPOSITORIO>
cd sistema-retiros
```

Si los servicios están en repositorios separados, clonar cada uno y ubicar `docker-compose.yml` en una carpeta de trabajo común o ajustar los comandos a su ubicación real.

### 4.2. Iniciar las bases de datos

Desde la carpeta que contiene `docker-compose.yml`:

```bash
docker compose up -d
docker compose ps
```

Servicios y conexiones locales del entorno de desarrollo:

| Contenedor | Host | Puerto externo | Base / servicio | Usuario de ejemplo |
| --- | --- | --- | --- | --- |
| PostgreSQL Auth | `localhost` | `5433` | `auth_db` | `postgres` |
| PostgreSQL Rutas | `localhost` | `5434` | `rutas_db` | `postgres` |
| MongoDB | `localhost` | `27017` | `solicitudes_db` | según Compose (sin autenticación en este prototipo) |
| Oracle Free | `localhost` | `1521` | `FREEPDB1` | `flota` |
| RabbitMQ (opcional) | `localhost` | `5672` / `15672` (panel) | cola de eventos | según Compose |

Los usuarios, contraseñas y nombres de bases deben coincidir entre `docker-compose.yml` y `application.properties` de cada servicio. Las contraseñas de ejemplo son **solo para desarrollo local**: no subir credenciales reales ni archivos `.env` privados a GitHub.

> Oracle puede tardar varios minutos en quedar listo aunque Docker muestre el contenedor encendido. Revisar `docker compose logs oracle --tail=50` antes de iniciar `ms-flota`.

### 4.3. Compilar y descargar dependencias Maven

Entrar en **cada** carpeta que contenga un `pom.xml` y ejecutar:

```bash
mvn clean install
```

Este comando descarga las dependencias, compila, ejecuta las pruebas Maven configuradas e instala el artefacto en el repositorio local. Si todavía hay pruebas de ejemplo que fallan, **corregirlas**; no omitirlas en la evidencia final.

Alternativa en Windows cuando el proyecto trae Maven Wrapper:

```powershell
.\mvnw.cmd clean install
```

### 4.4. Iniciar cada microservicio

Abrir cuatro terminales (una por proyecto) o ejecutar las cuatro clases `*Application.java` desde IntelliJ. En sus respectivas carpetas:

```bash
# Terminal 1: ms-auth
mvn spring-boot:run

# Terminal 2: ms-rutas
mvn spring-boot:run

# Terminal 3: ms-solicitudes
mvn spring-boot:run

# Terminal 4: ms-flota
mvn spring-boot:run
```

Esperar los mensajes `Started ...Application` en IntelliJ y verificar con `GET` desde Postman. **No es necesario iniciar Eureka ni Gateway para estas pruebas.**

### 4.5. Propiedades de conexión (referencia)

Fragmentos de `application.properties` por servicio (además de `spring.application.name` y `server.port`):

```properties
# ms-auth
spring.datasource.url=jdbc:postgresql://localhost:5433/auth_db
spring.datasource.username=postgres
spring.datasource.password=${AUTH_DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
```

```properties
# ms-rutas
spring.datasource.url=jdbc:postgresql://localhost:5434/rutas_db
spring.datasource.username=postgres
spring.datasource.password=${RUTAS_DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
```

```properties
# ms-solicitudes
spring.data.mongodb.uri=mongodb://localhost:27017/solicitudes_db
```

```properties
# ms-flota
spring.datasource.url=jdbc:oracle:thin:@//localhost:1521/FREEPDB1
spring.datasource.username=flota
spring.datasource.password=${FLOTA_DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
```

Establecer las variables de entorno de contraseña con los valores **locales** del Compose, o utilizar la configuración equivalente que ya funciona en cada proyecto. Las variables mostradas aquí son una **recomendación de seguridad**, no una afirmación de que ya estén implementadas. `ddl-auto=update` se usa únicamente para el prototipo académico; en producción se recomiendan migraciones versionadas.

## 5. Cómo ejecutar las pruebas HTTP

- En Postman seleccionar el **método** indicado y la URL absoluta.
- Para `POST` y `PUT` seleccionar **Body → raw → JSON** y `Content-Type: application/json`.
- Guardar los ID devueltos por las operaciones `POST`; **no asumir que serán `1`**.
- Los códigos HTTP que aparecen a continuación son **resultados esperados** con los controladores descritos. Si la implementación real difiere, actualizar el README y/o el código antes de entregar.
- En este recorrido de pruebas **el CRUD se consulta directamente** y no requiere un JWT; la validación por Gateway está pendiente. El endpoint de login se prueba por separado.

Variables recomendadas en Postman:

| Variable | Valor inicial |
| --- | --- |
| `auth_url` | `http://localhost:8081` |
| `rutas_url` | `http://localhost:8082` |
| `solicitudes_url` | `http://localhost:8083` |
| `flota_url` | `http://localhost:8084` |
| `usuario_id`, `ruta_id`, `solicitud_id`, `vehiculo_id`, `movimiento_id` | Completar después de cada `POST` |

### 5.1. MS Auth — usuarios y autenticación (`8081`)

**A1. Crear usuario** — `POST {{auth_url}}/api/usuarios` → **201 Created**

```json
{
  "nombre": "Juan Pérez",
  "correo": "juan.ep2@example.com",
  "clave": "ClaveDemo123"
}
```

**Verificación:** la respuesta contiene `id`, `nombre`, `correo`, `rol`; **no** debe exponer `claveHash`. Guardar `id` como `usuario_id`.

**A2. Listar usuarios** — `GET {{auth_url}}/api/usuarios` → **200 OK**; debe incluir al usuario creado.

**A3. Buscar por ID** — `GET {{auth_url}}/api/usuarios/{{usuario_id}}` → **200 OK**.

**A4. Actualizar** — `PUT {{auth_url}}/api/usuarios/{{usuario_id}}` → **200 OK**

```json
{
  "nombre": "Juan Pérez Actualizado",
  "correo": "juan.ep2@example.com",
  "clave": "ClaveNueva123"
}
```

**A5. Iniciar sesión** — `POST {{auth_url}}/api/auth/login` → **200 OK**

```json
{
  "correo": "juan.ep2@example.com",
  "clave": "ClaveNueva123"
}
```

**Verificación:** la respuesta contiene un campo `token` (JWT).

**A6. Error de credenciales** — repetir A5 con clave incorrecta → **401 Unauthorized**.

**A7. Error por correo duplicado** — repetir A1 con el mismo correo → **409 Conflict**.

**A8. Eliminar (al finalizar todas las pruebas que usan el usuario)** — `DELETE {{auth_url}}/api/usuarios/{{usuario_id}}` → **204 No Content**.

**A9. Buscar eliminado** — repetir A3 → **404 Not Found**.

> Si ya existe `juan.ep2@example.com`, cambia el correo antes de A1. Para el video conviene ejecutar A1 → A2 → A4 → A5 → A6 → A7 y dejar A8 → A9 para el cierre, después de crear las solicitudes que referencian al vecino.

### 5.2. MS Rutas — ruta con paradas (`8082`)

**R1. Crear ruta** — `POST {{rutas_url}}/api/rutas` → **201 Created**

```json
{
  "nombre": "Ruta Centro",
  "origen": "Terminal",
  "destino": "Sector Alto",
  "paradas": [
    {"nombre": "Plaza de Armas", "orden": 1},
    {"nombre": "Municipalidad", "orden": 2}
  ]
}
```

Guardar el `id` de respuesta en `ruta_id`.

**R2. Listar rutas** — `GET {{rutas_url}}/api/rutas` → **200 OK**.

**R3. Consultar ID** — `GET {{rutas_url}}/api/rutas/{{ruta_id}}` → **200 OK**; verificar el arreglo `paradas`.

**R4. Actualizar ruta y sus paradas** — `PUT {{rutas_url}}/api/rutas/{{ruta_id}}` → **200 OK**

```json
{
  "nombre": "Ruta Centro Modificada",
  "origen": "Terminal Norte",
  "destino": "Sector Alto",
  "paradas": [
    {"nombre": "Plaza de Armas", "orden": 1},
    {"nombre": "Hospital", "orden": 2}
  ]
}
```

**R5. Consultar un ID inexistente** — `GET {{rutas_url}}/api/rutas/99999999` → **404 Not Found**.

**R6. Validación (si está activada con `@Valid`)** — `POST {{rutas_url}}/api/rutas` con `"nombre": ""` y los demás campos válidos → **400 Bad Request**.

**R7. Eliminar** — `DELETE {{rutas_url}}/api/rutas/{{ruta_id}}` → **204 No Content**. Ejecutar **al final**, después de las pruebas de Solicitudes.

**R8. Verificar eliminación** — repetir R3 → **404 Not Found**.

**Persistencia:** abrir `rutas_db → public → tables → ruta` y `parada`. Verificar relación `parada.ruta_id = ruta.id` antes de R7. En la operación PUT del diseño de referencia se reemplazan las paradas existentes por las nuevas.

### 5.3. MS Flota — vehículos (`8084`)

**F1. Crear vehículo** — `POST {{flota_url}}/api/flota` → **201 Created**

```json
{
  "patente": "ABCD12",
  "modelo": "Camión recolector",
  "estado": "DISPONIBLE"
}
```

Guardar `id` en `vehiculo_id`.

**F2. Listar vehículos** — `GET {{flota_url}}/api/flota` → **200 OK**.

**F3. Buscar vehículo** — `GET {{flota_url}}/api/flota/{{vehiculo_id}}` → **200 OK**.

**F4. Actualizar vehículo** — `PUT {{flota_url}}/api/flota/{{vehiculo_id}}` → **200 OK**

```json
{
  "patente": "ABCD12",
  "modelo": "Camión recolector 2026",
  "estado": "EN_RUTA"
}
```

**F5. Error por patente duplicada** — intentar crear otro vehículo con `"patente":"ABCD12"` → **409 Conflict**.

**F6. ID inexistente** — `GET {{flota_url}}/api/flota/99999999` → **404 Not Found**.

**F7. Eliminar vehículo** — `DELETE {{flota_url}}/api/flota/{{vehiculo_id}}` → **204 No Content** **solo cuando se hayan eliminado antes sus movimientos**.

**F8. Buscar eliminado** — repetir F3 → **404 Not Found**.

**Persistencia:** revisar la tabla `FLOTA.VEHICULOS` en Oracle antes de F7.

### 5.4. MS Solicitudes — MongoDB (`8083`)

> Ejecutar S1 **después de R1 y F1** para usar `ruta_id` y `vehiculo_id` reales. Crear usuario primero si se quiere usar un `vecinoId` válido.

**S1. Crear solicitud** — `POST {{solicitudes_url}}/api/solicitudes` → **201 Created**

```json
{
  "vecinoId": 1,
  "rutaId": 1,
  "vehiculoId": 1,
  "direccion": "Av. Presidente Ibáñez 1200"
}
```

**Sustituir** los tres identificadores numéricos del ejemplo por los ID reales obtenidos. Guardar el `id` devuelto por MongoDB en `solicitud_id`. El `estado` inicial esperado es `PENDIENTE`.

**S2. Listar solicitudes** — `GET {{solicitudes_url}}/api/solicitudes` → **200 OK**.

**S3. Consultar solicitud** — `GET {{solicitudes_url}}/api/solicitudes/{{solicitud_id}}` → **200 OK**.

**S4. Actualizar solicitud** — `PUT {{solicitudes_url}}/api/solicitudes/{{solicitud_id}}` → **200 OK**

```json
{
  "vecinoId": 1,
  "rutaId": 1,
  "vehiculoId": 1,
  "direccion": "Av. Presidente Ibáñez 1250",
  "estado": "EN_PROCESO"
}
```

Nuevamente, reemplazar los ID numéricos por los reales.

**S5. ID inexistente** — `GET {{solicitudes_url}}/api/solicitudes/000000000000000000000000` → **404 Not Found**, si dicho documento no existe.

**S6. Datos inválidos (si `@Valid` está activo)** — intentar crear una solicitud sin `direccion`, pero con los ID válidos → **400 Bad Request**.

**S7. Eliminar** — `DELETE {{solicitudes_url}}/api/solicitudes/{{solicitud_id}}` → **204 No Content**.

**S8. Buscar eliminado** — repetir S3 → **404 Not Found**.

**Persistencia:** inspeccionar la colección `solicitudes` de `solicitudes_db` en MongoDB Compass, IntelliJ Database o `mongosh`.

### 5.5. MS Flota — movimientos (`8084`)

**M1. Crear movimiento** — `POST {{flota_url}}/api/flota/movimientos` → **201 Created**

```json
{
  "solicitudId": "REEMPLAZAR_CON_ID_DE_MONGO",
  "rutaId": 1,
  "vehiculoId": 1
}
```

Sustituir `solicitudId`, `rutaId` y `vehiculoId` por los ID reales. Guardar `id` en `movimiento_id`.

**M2. Listar movimientos** — `GET {{flota_url}}/api/flota/movimientos` → **200 OK**.

**M3. Buscar movimiento** — `GET {{flota_url}}/api/flota/movimientos/{{movimiento_id}}` → **200 OK**.

**M4. Actualizar movimiento** — `PUT {{flota_url}}/api/flota/movimientos/{{movimiento_id}}` → **200 OK**

```json
{
  "solicitudId": "REEMPLAZAR_CON_ID_DE_MONGO",
  "rutaId": 1,
  "vehiculoId": 1
}
```

Reemplazar los ID por los reales; para evidenciar un cambio visible puede crearse previamente una segunda ruta y actualizar `rutaId` a esa nueva ruta.

**M5. ID de vehículo inexistente** — `POST {{flota_url}}/api/flota/movimientos` con un `vehiculoId` que no exista (por ejemplo `99999999`) → **404 Not Found**.

**M6. Eliminar movimiento** — `DELETE {{flota_url}}/api/flota/movimientos/{{movimiento_id}}` → **204 No Content**.

**M7. Consultar eliminado** — repetir M3 → **404 Not Found**.

**Persistencia:** revisar `FLOTA.MOVIMIENTOS`, comprobando el campo `VEHICULO_ID` que vincula el movimiento con el vehículo.

### 5.6. Orden sugerido para evitar errores de referencias

1. Crear usuario **A1**; obtener `usuario_id`.
2. Crear ruta **R1**; obtener `ruta_id`.
3. Crear vehículo **F1**; obtener `vehiculo_id`.
4. Crear solicitud **S1** usando esos ID; obtener `solicitud_id`.
5. Crear movimiento **M1** usando los ID creados (si está habilitado el consumidor automático RabbitMQ, comprobar antes que no se generó uno por evento).
6. Ejecutar `GET`, `PUT`, manejo de errores y verificar persistencia en cada motor.
7. Eliminar primero **M6**, luego **S7**; eliminar **F7**, **R7** y **A8** al terminar.

> **Nota sobre RabbitMQ:** si el consumidor de `ms-flota` está activo, `POST /api/solicitudes` puede crear el movimiento **automáticamente**. En ese caso, mostrar ese registro automático y **no ejecutar M1 para esa misma solicitud**; usar una solicitud separada o desactivar temporalmente el consumidor para la prueba CRUD manual. No afirmar que la integración está funcionando sin verificarlo.

## 6. Consultar datos directamente en Docker

Estos comandos permiten comprobar que los registros no están solo en la respuesta HTTP, sino almacenados en las bases de datos.

```bash
# Auth (PostgreSQL)
docker compose exec postgres-auth psql -U postgres -d auth_db -c 'SELECT id,nombre,correo,rol FROM usuarios;'

# Rutas (PostgreSQL)
docker compose exec postgres-rutas psql -U postgres -d rutas_db -c 'SELECT * FROM ruta;'
docker compose exec postgres-rutas psql -U postgres -d rutas_db -c 'SELECT * FROM parada;'

# Solicitudes (MongoDB)
docker compose exec mongo mongosh solicitudes_db --eval 'db.solicitudes.find().pretty()'

# Oracle (conexión interactiva)
docker compose exec oracle sqlplus flota@//localhost:1521/FREEPDB1
```

En Oracle, ingresar la contraseña local del usuario `flota` al solicitarla; luego ejecutar:

```sql
SELECT * FROM VEHICULOS;
SELECT * FROM MOVIMIENTOS;
```

Alternativamente, visualizar las tablas desde **IntelliJ → Database**. Los nombres de las tablas y servicios se basan en las clases propuestas; verificar que coincidan con el código y el Compose reales.

## 7. Pruebas de Maven y artefactos ejecutables

Desde cada microservicio:

```bash
mvn clean
mvn test
mvn package
mvn install
```

Si todas las fases finalizan correctamente, buscar el artefacto en `target/`:

```text
target/ms-rutas-0.0.1-SNAPSHOT.jar
```

El nombre depende del `artifactId` y la versión real de cada `pom.xml`. Para demostrar ejecución del JAR desde la carpeta de ese microservicio (con sus bases disponibles):

```bash
java -jar target/ms-rutas-0.0.1-SNAPSHOT.jar
```

**Evidencia recomendada:** mostrar en pantalla la salida `BUILD SUCCESS`, la carpeta `target` y el arranque del `.jar`. Si no hay pruebas automatizadas de negocio configuradas, indicarlo con transparencia: `mvn test` puede terminar correctamente sin probar todo el CRUD; las pruebas REST de Postman constituyen evidencia adicional.

## 8. Uso de Git y estrategia de ramas

La pauta recomienda `main`, `develop` y `feature/*` con commits descriptivos. Una estrategia simple:

```text
main                 → versión estable de entrega
develop              → integración de cambios
feature/ms-auth      → desarrollo de autenticación
feature/ms-rutas     → desarrollo de rutas
feature/ms-solicitudes
feature/ms-flota
```

Ejemplos de commits:

```bash
git add .
git commit -m "feat(rutas): implementar CRUD y relacion con paradas"
git commit -m "feat(flota): implementar persistencia Oracle"
git commit -m "test: documentar casos de prueba en Postman"
git commit -m "docs: agregar instrucciones de ejecucion en README"
```

Antes de publicar:

```bash
git status
git branch -a
git log --oneline -10
```

No crear commits ficticios ni afirmar que existen ramas que no hayan sido creadas realmente. Asegurarse de excluir `target/`, `.idea/`, contraseñas reales y archivos `.env` privados mediante `.gitignore`.

## 9. Problemas frecuentes

| Problema | Comprobación |
| --- | --- |
| `Connection refused` | `docker compose ps`, puerto, nombre de base y estado del contenedor. |
| PostgreSQL responde pero no hay tablas | Ejecutar el microservicio, revisar `@Entity`, JPA, `ddl-auto=update` y refrescar IntelliJ Database. |
| MongoDB no muestra la colección | Insertar un documento con `POST` y actualizar la vista. |
| Oracle no inicia | Esperar la inicialización y consultar `docker compose logs oracle`. |
| `400 Bad Request` | Revisar el JSON y las anotaciones de validación. |
| `404 Not Found` | Revisar la URL, el ID y si el registro fue eliminado. |
| `409 Conflict` | Revisar un correo o patente duplicados. |
| Error de Lombok | Recargar Maven y verificar `Annotation Processing` en IntelliJ. |
| Maven falla | Confirmar JDK 21, versiones de dependencias y el error inicial de la consola. |

## 10. Presentación y evidencias para la evaluación

De acuerdo con la pauta **EP2 JVY0101**, el video debe mostrar:

- Endpoints funcionando en Postman, incluyendo **respuestas correctas y manejo de errores**.
- Configuración de persistencia en `application.properties` y dependencias en `pom.xml`.
- Explicación breve de las capas y las relaciones JPA; CRUD con evidencia de datos guardados.
- Maven (`clean`, `test`, `package` / `install`) y un `.jar` generado.
- Proceso reproducible: clonar, preparar dependencias, ejecutar Docker e iniciar los servicios siguiendo este README.
- Video **entre 3 y 8 minutos**, con **voz clara de los integrantes**, orden técnico y archivo MP4 o similar.

**Entregables:** enlaces a GitHub en AVA y copia al correo del docente; video en AVA y copia al correo del docente, conforme al calendario indicado por el curso.

## 11. Limitaciones conocidas y próximos pasos

- Eureka Server y API Gateway/JWT centralizado: **integración pendiente**; el CRUD actual se prueba directamente.
- Gestión completa de roles vecino/conductor: **pendiente**. El login básico emite JWT, pero el recorrido documentado no implica protección de todos los endpoints.
- Validaciones cruzadas de ID entre microservicios y eventos con reintentos/idempotencia: **pendiente o sujeto a verificación**.
- Las credenciales de ejemplo y `ddl-auto=update` son configuraciones académicas, no adecuadas para producción.

---

**Asignatura:** JVY0101 — Java: Diseño y Construcción de Soluciones Nativas en Nube  
**Tecnologías:** Java 21 · Spring Boot · Spring Data JPA · MongoDB · PostgreSQL · Oracle Free · Docker Compose · Maven · Postman
