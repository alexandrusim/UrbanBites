# UrbanBites Backend - Spring Boot Application

Backend REST API pentru sistemul de rezervări restaurant UrbanBites.

## Tehnologii utilizate

- **Spring Boot 3.5.7**
- **Java 17**
- **PostgreSQL** (baza de date)
- **Flyway** (migrări baza de date)
- **Spring Data JPA** (acces la date)
- **Lombok** (reducere cod boilerplate)
- **Maven** (management dependențe)

## Structura proiectului

```
backend/
├── src/
│   ├── main/
│   │   ├── java/gio/backend/
│   │   │   ├── BackendApplication.java        # Main application
│   │   │   ├── config/
│   │   │   │   └── CorsConfig.java            # Configurare CORS
│   │   │   ├── entity/                        # Entități JPA
│   │   │   │   ├── User.java
│   │   │   │   ├── Restaurant.java
│   │   │   │   ├── Table.java
│   │   │   │   ├── Reservation.java
│   │   │   │   ├── Menu.java
│   │   │   │   ├── MenuItem.java
│   │   │   │   ├── Payment.java
│   │   │   │   ├── Feedback.java
│   │   │   │   ├── Notification.java
│   │   │   │   └── ContactMessage.java
│   │   │   ├── repository/                    # Repository interfaces
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── RestaurantRepository.java
│   │   │   │   ├── TableRepository.java
│   │   │   │   ├── ReservationRepository.java
│   │   │   │   └── ...
│   │   │   ├── service/                       # Business logic
│   │   │   │   ├── UserService.java
│   │   │   │   ├── RestaurantService.java
│   │   │   │   ├── TableService.java
│   │   │   │   ├── ReservationService.java
│   │   │   │   └── FeedbackService.java
│   │   │   └── controller/                    # REST controllers
│   │   │       ├── UserController.java
│   │   │       ├── RestaurantController.java
│   │   │       ├── TableController.java
│   │   │       ├── ReservationController.java
│   │   │       └── FeedbackController.java
│   │   └── resources/
│   │       ├── application.properties         # Configurare aplicație
│   │       └── db/migration/                  # Script-uri Flyway
│   │           ├── V1__create_base_tables.sql
│   │           ├── V2__create_foreign_keys.sql
│   │           ├── V3__create_triggers_and_functions.sql
│   │           └── V4__create_views.sql
│   └── test/
└── pom.xml

```

## Configurare

### 1. Instalare PostgreSQL

Asigură-te că ai PostgreSQL instalat și pornit:

```bash
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql
```

### 2. Creare bază de date

```bash
sudo -u postgres psql
```

În consola PostgreSQL:

```sql
CREATE DATABASE urbanbites;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE urbanbites TO postgres;
\q
```

### 3. Configurare application.properties

Editează fișierul `src/main/resources/application.properties` și actualizează:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/urbanbites
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 4. Construire și rulare

```bash
# Din directorul backend/
./mvnw clean install
./mvnw spring-boot:run
```

Serverul va porni pe `http://localhost:8080`

## API Endpoints

### Users (`/api/users`)
- `GET /api/users` - Toate utilizatorii
- `GET /api/users/{id}` - Utilizator după ID
- `GET /api/users/email/{email}` - Utilizator după email
- `POST /api/users` - Creare utilizator nou
- `PUT /api/users/{id}` - Actualizare utilizator
- `DELETE /api/users/{id}` - Ștergere utilizator

### Restaurants (`/api/restaurants`)
- `GET /api/restaurants` - Toate restaurantele
- `GET /api/restaurants/{id}` - Restaurant după ID
- `GET /api/restaurants/city/{city}` - Restaurante după oraș
- `GET /api/restaurants/cuisine/{cuisineType}` - Restaurante după tip bucătărie
- `GET /api/restaurants/active` - Restaurante active
- `POST /api/restaurants` - Creare restaurant nou
- `PUT /api/restaurants/{id}` - Actualizare restaurant
- `DELETE /api/restaurants/{id}` - Ștergere restaurant

### Tables (`/api/tables`)
- `GET /api/tables` - Toate mesele
- `GET /api/tables/{id}` - Masă după ID
- `GET /api/tables/restaurant/{restaurantId}` - Mese după restaurant
- `GET /api/tables/restaurant/{restaurantId}/available` - Mese disponibile
- `POST /api/tables` - Creare masă nouă
- `PUT /api/tables/{id}` - Actualizare masă
- `DELETE /api/tables/{id}` - Ștergere masă

### Reservations (`/api/reservations`)
- `GET /api/reservations` - Toate rezervările
- `GET /api/reservations/{id}` - Rezervare după ID
- `GET /api/reservations/confirmation/{code}` - Rezervare după cod confirmare
- `GET /api/reservations/user/{userId}` - Rezervări după utilizator
- `GET /api/reservations/restaurant/{restaurantId}` - Rezervări după restaurant
- `GET /api/reservations/restaurant/{restaurantId}/date/{date}` - Rezervări după restaurant și dată
- `GET /api/reservations/status/{status}` - Rezervări după status
- `GET /api/reservations/table/{tableId}/date/{date}/available` - Verificare disponibilitate masă
- `POST /api/reservations` - Creare rezervare nouă
- `PUT /api/reservations/{id}` - Actualizare rezervare
- `DELETE /api/reservations/{id}` - Ștergere rezervare

### Feedback (`/api/feedback`)
- `GET /api/feedback` - Toate feedback-urile
- `GET /api/feedback/{id}` - Feedback după ID
- `GET /api/feedback/restaurant/{restaurantId}` - Feedback-uri după restaurant
- `GET /api/feedback/restaurant/{restaurantId}/visible` - Feedback-uri vizibile
- `GET /api/feedback/user/{userId}` - Feedback-uri după utilizator
- `POST /api/feedback` - Creare feedback nou
- `PUT /api/feedback/{id}` - Actualizare feedback
- `DELETE /api/feedback/{id}` - Ștergere feedback

## Exemple de cereri

### Creare rezervare

```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "restaurantId": 1,
    "tableId": 1,
    "reservationDate": "2025-12-15",
    "reservationTime": "19:00:00",
    "numberOfGuests": 4,
    "status": "PENDING"
  }'
```

### Obținere toate rezervările

```bash
curl http://localhost:8080/api/reservations
```

### Obținere restaurante active

```bash
curl http://localhost:8080/api/restaurants/active
```

## Flyway Migrations

Migrările bazei de date sunt gestionate automat de Flyway la pornirea aplicației. Script-urile sunt localizate în `src/main/resources/db/migration/`.

Ordinea migrărilor:
1. **V1** - Creare tabele de bază
2. **V2** - Adăugare chei străine
3. **V3** - Creare triggere și funcții
4. **V4** - Creare view-uri

## Debugging

Pentru a vedea SQL-ul generat:

```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

## Testing

Rulare teste:

```bash
./mvnw test
```

## Build pentru producție

```bash
./mvnw clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## Troubleshooting

### Eroare de conexiune la PostgreSQL

Verifică că PostgreSQL rulează:
```bash
sudo systemctl status postgresql
```

### Flyway checksum mismatch

Nu modifica fișierele de migrare deja aplicate. Creează migrări noi (V5, V6, etc.).

### Port 8080 deja utilizat

Schimbă portul în `application.properties`:
```properties
server.port=8081
```

## CORS Configuration

CORS este configurat pentru a permite cereri de la:
- `http://localhost:4200` (Angular dev server)
- `http://localhost:4000` (Angular SSR server)

Pentru alte origini, editează `CorsConfig.java`.

## Contribuție

1. Fork repository-ul
2. Creează un branch pentru feature (`git checkout -b feature/AmazingFeature`)
3. Commit modificările (`git commit -m 'Add some AmazingFeature'`)
4. Push la branch (`git push origin feature/AmazingFeature`)
5. Deschide un Pull Request

## Licență

Acest proiect este dezvoltat pentru scopuri educaționale.
