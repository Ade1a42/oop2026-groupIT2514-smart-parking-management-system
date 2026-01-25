IT-2514: Amanov Alikhan, Assel Nurzhankyzy
Topic_4: Smart Parking Management System
Smart Parking Management System is a Java console application for managing parking.
The system allows you to reserve parking spots, monitor availability, and calculate parking fees.
This project was completed as part of an assignment on OOP / SOLID / JDBC / Exceptions.

Database Design:

```sql
CREATE TABLE parking_spots (
    id SERIAL PRIMARY KEY,
    spot_number VARCHAR(10) UNIQUE NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('STANDARD', 'ELECTRIC', 'DISABLED')),
    status VARCHAR(20) DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'RESERVED', 'OCCUPIED')),
    zone VARCHAR(10) NOT NULL
);
```
```sql
CREATE TABLE vehicles (
    id SERIAL PRIMARY KEY,
    plate_number VARCHAR(20) UNIQUE NOT NULL,
    owner_name VARCHAR(100) NOT NULL
);
```
```sql
CREATE TABLE tariffs (
    id SERIAL PRIMARY KEY,
    spot_type VARCHAR(20) UNIQUE NOT NULL CHECK (spot_type IN ('STANDARD', 'ELECTRIC', 'DISABLED')),
    hourly_rate DECIMAL(5,2) NOT NULL CHECK (hourly_rate > 0)
);
```
```sql
CREATE TABLE reservations (
    id SERIAL PRIMARY KEY,
    vehicle_id INTEGER NOT NULL REFERENCES vehicles(id),
    spot_id INTEGER NOT NULL REFERENCES parking_spots(id),
    start_time TIMESTAMP DEFAULT NOW(),
    end_time TIMESTAMP,
    total_cost DECIMAL(10,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED'))
);
```
This project is not intended for commercial use. It was developed purely for educational purposes as part of an OOP assignment.

During this project, we learned and practiced:

Object-Oriented Programming concepts in Java

SOLID principles for clean and maintainable code

JDBC database connections and abstraction

Handling custom exceptions

Using GitHub for team collaboration

Designing and managing a simple database schema in PostgreSQL / Supabase
