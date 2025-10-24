Society Maintenance App

Overview
- Desktop app for apartment/society billing: generate monthly bills, collect payments with UTR, and approve/reject in an admin workflow. Includes reminders, month/year navigation, and role-based access.

Features
- Admin: month/year filter, create monthly bills, approve/reject payments, send reminders
- Resident: view and pay bills (QR + UTR), see payment/approval status
- Billing engine: duplicate protection per resident/month/year
- Messages: unread reminders counter and viewer
- DB init: MySQL (prod) or SQLite (dev), idempotent schema

Tech stack
- Java (Swing), Maven (shaded JAR)
- JDBC: MySQL (prod), SQLite (dev)
- Logging: java.util.logging

Setup (MySQL)
1) Create a database (e.g., society) and user with privileges.
2) Set environment variables:
   - DB_TYPE=mysql
   - DB_HOST=localhost
   - DB_PORT=3306
   - DB_NAME=society
   - DB_USER=<user>
   - DB_PASS=<password>

Run
- Build and run the shaded jar:
  - mvn clean package
  - java -jar target/society-maintenance-app-1.0-SNAPSHOT.jar

Defaults
- First run creates an admin: admin@society.com / admin123

Notes
- Switch months/years from the Admin dashboard to view that period’s bills.
- Resident payments move to PENDING with UTR; admins approve (PAID/APPROVED) or reject (UNPAID/REJECTED).
