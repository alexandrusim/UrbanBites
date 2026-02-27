# UrbanBites Database Migrations

This directory contains Flyway-compatible database migration scripts for the UrbanBites restaurant reservation system.

## Directory Structure

```
db/
└── migration/
    ├── V1__create_base_tables.sql
    ├── V2__create_foreign_keys.sql
    ├── V3__create_triggers_and_functions.sql
    └── V4__create_views.sql
```

## Migration Files

### V1__create_base_tables.sql
- Creates PostgreSQL extensions (`uuid-ossp`, `pgcrypto`)
- Creates all core tables:
  - `user` - System users (clients, restaurant admins, system admins)
  - `restaurant` - Restaurant information with JSONB opening hours
  - `table` - Restaurant tables with layout coordinates
  - `menu` - Restaurant menus with time availability
  - `menu_item` - Menu items with dietary information
  - `reservation` - Table reservations with confirmation codes
  - `payment` - Payment transactions
  - `feedback` - Restaurant reviews and ratings
  - `notification` - User notifications
  - `contact_message` - Contact form messages
- Creates all indexes for performance optimization

### V2__create_foreign_keys.sql
- Adds the circular foreign key from `user.restaurant_id` to `restaurant.restaurant_id`
- This is separated to avoid circular dependency issues

### V3__create_triggers_and_functions.sql
- **Functions:**
  - `update_updated_at_column()` - Auto-updates `updated_at` timestamps
  - `update_restaurant_rating()` - Recalculates restaurant ratings from feedback
  - `generate_confirmation_code()` - Generates unique 8-character reservation codes
  - `set_confirmation_code()` - Auto-assigns confirmation codes to reservations
  - `check_table_availability()` - Checks if a table is available for a time slot

- **Triggers:**
  - `updated_at` triggers on 9 tables (user, restaurant, table, menu, menu_item, reservation, payment, feedback, notification)
  - `update_restaurant_rating_trigger` - Auto-updates ratings when feedback is added
  - `set_reservation_confirmation_code` - Auto-generates confirmation codes

### V4__create_views.sql
- **active_reservations** - Current and upcoming confirmed/pending reservations
- **restaurant_statistics** - Aggregated restaurant metrics (reservations, ratings, revenue)
- **full_menu_view** - Complete menu with all item details

## For Backend Developers

### Prerequisites
1. PostgreSQL database server running
2. Database `urbanbites` created manually (Flyway doesn't create databases)
3. Database user with appropriate permissions

### Spring Boot Setup

**1. Add Flyway dependency to `pom.xml`:**
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

**2. Configure `application.properties`:**
```properties
# Database connection
spring.datasource.url=jdbc:postgresql://localhost:5432/urbanbites
spring.datasource.username=postgres
spring.datasource.password=your_password

# Disable Hibernate auto DDL
spring.jpa.hibernate.ddl-auto=none

# Flyway configuration (optional, these are defaults)
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration
```

**3. Copy migration files:**
Copy the entire `db/` directory to your Spring Boot project at:
```
src/main/resources/db/migration/
```

Your project structure should look like:
```
src/
└── main/
    └── resources/
        ├── application.properties
        └── db/
            └── migration/
                ├── V1__create_base_tables.sql
                ├── V2__create_foreign_keys.sql
                ├── V3__create_triggers_and_functions.sql
                └── V4__create_views.sql
```

**4. Run your Spring Boot application:**
```bash
mvn spring-boot:run
```

Flyway will automatically:
- Create the `flyway_schema_history` table
- Apply all pending migrations in order
- Track which migrations have been applied

### Important Notes

⚠️ **Flyway Rules:**
- **Never modify applied migrations** - Flyway tracks checksums
- **Always create new migrations** - Increment version numbers (V5, V6, etc.)
- **No automatic rollbacks** - Create new migrations to undo changes
- **Database must exist** - Create the `urbanbites` database before running

📝 **Naming Convention:**
- Format: `V{version}__{description}.sql`
- Version: Sequential number (V1, V2, V3...)
- Two underscores after version
- Description: lowercase with underscores
- Example: `V5__add_user_preferences.sql`

### Creating New Migrations

When you need to modify the schema:

```bash
# Create a new migration file
touch src/main/resources/db/migration/V5__add_new_feature.sql
```

Example content:
```sql
-- V5__add_user_preferences.sql
ALTER TABLE "user" ADD COLUMN preferences JSONB;
CREATE INDEX idx_user_preferences ON "user" USING GIN (preferences);
```

### Troubleshooting

**Migration checksum mismatch:**
```
Solution: Never modify applied migrations. Create a new migration instead.
```

**Baseline migration:**
If you have an existing database:
```properties
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=0
```

**Skip specific migrations:**
```properties
spring.flyway.ignore-migration-patterns=*:missing,*:future
```

**View migration history:**
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

## Testing Migrations

Before deploying to production or committing to repository:

```bash
# Test migrations with provided script
python3 test_migrations.py
```

This will:
1. Create a test database (`urbanbites_test_migrations`)
2. Apply all migrations in order
3. Run 8 verification tests
4. Report any issues

## Database Features

### Auto-Generated Fields
- `user_id`, `restaurant_id`, etc. - Auto-increment primary keys
- `confirmation_code` - Auto-generated 8-character codes for reservations
- `updated_at` - Auto-updated on record changes
- `rating_average`, `rating_count` - Auto-calculated from feedback

### Constraints
- Foreign keys with CASCADE on restaurant/menu deletions
- CHECK constraints on ratings (1-5), prices (>= 0), etc.
- UNIQUE constraints on emails, confirmation codes
- Role enums for user types

### Performance
- Indexes on frequently queried columns
- Composite indexes for common query patterns
- GIN indexes for JSONB columns (if added)

## Support

For issues or questions:
1. Check the test script output
2. Verify PostgreSQL version compatibility (11+)
3. Ensure all extensions are available
4. Check database user permissions

## Migration History

| Version | Description | Applied |
|---------|-------------|---------|
| V1 | Create base tables and indexes | Pending |
| V2 | Create foreign key constraints | Pending |
| V3 | Create triggers and functions | Pending |
| V4 | Create views | Pending |

---

Generated for UrbanBites Restaurant Reservation System
