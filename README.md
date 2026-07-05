# EduHub Backend

Backend REST API aplikacija za onlajn platformu za učenje — kursevi, lekcije, nastavni materijali, prijave studenata, ocenjivanje, sertifikati i notifikacije.

Projekat rađen u okviru predmeta **Softverski alati**, Fakultet organizacionih nauka.

## Tehnologije

- **Java 17**
- **Spring Boot 3.5.16** (Web, Data JPA, Security, Validation)
- **MySQL 8** + **Flyway** (seed podaci za šifarnike)
- **Spring Security + JWT** autentifikacija (stateless)
- **springdoc-openapi** (Swagger UI) — interaktivna API dokumentacija
- **Lombok**
- **Maven**
- **JUnit 5 + Mockito** — testiranje
- **Javadoc** — dokumentacija koda

## Preduslovi

- JDK 17+
- Maven 3.9+
- MySQL server 8.x (pokrenut lokalno ili u Docker kontejneru)

## Podešavanje

1. Klonirati repozitorijum i otvoriti u IntelliJ IDEA.
2. Podesiti konekciju ka bazi u `src/main/resources/application.properties` (podrazumevano: `localhost:3306`, korisnik `root`, lozinka `root` — izmeniti po potrebi):

```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/eduhub_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=root
```

   Baza `eduhub_db` se automatski kreira pri prvom pokretanju (`createDatabaseIfNotExist=true`). Hibernate (`ddl-auto=update`) kreira šemu, a Flyway potom ubacuje početne podatke u šifarničke tabele (uloge, statusi, tipovi — videti `src/main/resources/db/migration/V1__seed_lookup_data.sql`).

3. Pokrenuti aplikaciju:

```bash
   mvn spring-boot:run
```

4. Otvoriti Swagger UI: **http://localhost:8080/swagger-ui.html**

## Autentifikacija

Svaki novoregistrovani korisnik (`POST /api/auth/register`) dobija podrazumevanu ulogu **STUDENT**. Za testiranje INSTRUCTOR/ADMIN operacija, ulogu je potrebno ručno promeniti u bazi:

```sql
UPDATE app_user
SET role_id = (SELECT role_id FROM role WHERE role_name = 'INSTRUCTOR')
WHERE user_email = 'primer@eduhub.com';
```

Nakon prijave (`POST /api/auth/login`), dobijeni JWT token se koristi u `Authorization: Bearer <token>` zaglavlju za sve zaštićene endpointe (ili preko dugmeta **Authorize** u Swagger UI-ju).

## Testiranje

```bash
mvn test
```

> **Napomena:** `mvn test` uključuje i `EduhubBackendApplicationTests.contextLoads`, koji zahteva pokrenutu MySQL bazu (podiže ceo Spring kontekst). Ostali testovi (domenske klase, servisi, mapperi) su čisti JUnit/Mockito testovi bez potrebe za bazom.

## Javadoc

```bash
mvn javadoc:javadoc
```

Generisana dokumentacija: `target/site/apidocs/index.html`

## Sistemske operacije (SO1–SO29)

| SO | Opis | Endpoint |
|---|---|---|
| SO1 | Registracija korisnika | `POST /api/auth/register` |
| SO2 | Prijava korisnika | `POST /api/auth/login` |
| SO3 | Odjava korisnika | `POST /api/auth/logout` |
| SO4 | Pregled svih kurseva | `GET /api/courses` |
| SO5 | Pretraga i filtriranje kurseva | `GET /api/courses?keyword=&categoryId=&levelId=` |
| SO6 | Pregled detalja kursa | `GET /api/courses/{id}` |
| SO7 | Kreiranje kursa | `POST /api/courses` |
| SO8 | Izmena kursa | `PUT /api/courses/{id}` |
| SO9 | Brisanje/deaktivacija kursa | `DELETE /api/courses/{id}` |
| SO10 | Prijava studenta na kurs | `POST /api/enrollments` |
| SO11 | Pregled prijavljenih kurseva | `GET /api/enrollments/me` |
| SO12 | Dodavanje lekcije | `POST /api/courses/{courseId}/lessons` |
| SO13 | Izmena lekcije | `PUT /api/lessons/{id}` |
| SO14 | Brisanje lekcije | `DELETE /api/lessons/{id}` |
| SO15 | Pregled lekcija kursa | `GET /api/courses/{courseId}/lessons` |
| SO16 | Dodavanje nastavnog materijala | `POST /api/lessons/{lessonId}/materials` |
| SO17 | Pregled nastavnog materijala | `GET /api/lessons/{lessonId}/materials` |
| SO18 | Praćenje napretka studenta | `PATCH /api/enrollments/{id}/progress` |
| SO19 | Ocenjivanje kursa | `POST /api/enrollments/{enrollmentId}/review` |
| SO20 | Pregled ocena i komentara | `GET /api/courses/{courseId}/reviews` |
| SO21 | Upravljanje korisnicima | `GET /api/admin/users`, `PATCH /api/admin/users/{id}/status` |
| SO22 | Dodela uloga korisnicima | `PATCH /api/admin/users/{id}/role` |
| SO23 | Odobravanje/blokiranje kursa | `PATCH /api/admin/courses/{id}/approve`, `.../block` |
| SO24 | Statistika platforme | `GET /api/admin/statistics` |
| SO25 | Slanje notifikacije | `POST /api/notifications` |
| SO26 | Pregled notifikacija | `GET /api/notifications/me` |
| SO27 | Označavanje notifikacije kao pročitane | `PATCH /api/notifications/{id}/read` |
| SO28 | Izdavanje sertifikata | `POST /api/enrollments/{enrollmentId}/certificate` |
| SO29 | Pregled sertifikata studenta | `GET /api/certificates/me` |

## Struktura projekta
src/main/java/rs/ac/bg/fon/eduhub/
├── entity/           # domenske klase (JPA entiteti)
│   ├── impl/         # glavni entiteti (User, Course, Lesson, Material, Enrollment, Review, Certificate, Notification)
│   └── lookup/        # šifarničke klase (Role, CourseStatus, CourseLevel, CourseCategory, LessonType, MaterialType, NotificationType, EnrollmentStatus)
├── repository/       # Spring Data JPA repozitorijumi
├── dto/              # prenosni objekti (Java records)
├── mapper/           # konverzija entitet <-> DTO
├── service/          # poslovna logika
├── controller/        # REST kontroleri
├── security/         # JWT servis, filter, UserDetailsService
├── config/           # Spring Security i OpenAPI konfiguracija
└── exception/         # globalni error handler
## Domenske klase (16)

`User`, `Course`, `Lesson`, `Material`, `Enrollment`, `Review`, `Certificate`, `Notification`, `Role`, `CourseStatus`, `CourseLevel`, `CourseCategory`, `LessonType`, `MaterialType`, `NotificationType`, `EnrollmentStatus`.

## Git istorija

Projekat je razvijan vertikalno (feature po feature) kroz odvojene grane, sa merge-om u `main` i semantičkim tagovanjem svake celine (`v0.1.0`–`v0.14.0`): inicijalizacija projekta, domenski model, autentifikacija, kursevi, lekcije, materijali, prijave, ocene/sertifikati, notifikacije, admin operacije, praćenje napretka, error handling, Javadoc, JUnit testovi.

## Autor

Mihajlo Ristanovic