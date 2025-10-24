# 🏢 Society Maintenance App

> A modern desktop application for seamless apartment/society billing, payment tracking, and administrative workflows.

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

## 📋 Table of Contents
- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Usage](#-usage)
- [Screenshots](#-screenshots)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🎯 Overview

**Society Maintenance App** is an integrated desktop solution designed for small-to-medium apartment communities to:
- 📊 Generate and track monthly maintenance bills
- 💰 Process resident payments with UTR verification
- ✅ Enable admin approval/rejection workflows
- 📧 Send automated reminders to residents
- 📅 Navigate bills by month and year
- 🔐 Manage users with role-based access (Admin/Resident)

Built with **Java Swing** for a native desktop experience and **MySQL/SQLite** for flexible deployment.

---

## ✨ Features

### 👨‍💼 Admin Dashboard
- 🗓️ **Month/Year Navigator**: Filter bills by specific periods
- 📝 **Bill Creation**: Generate monthly bills for all residents in one click
- ✅ **Payment Approval**: Review and approve resident payments
- ❌ **Payment Rejection**: Reject invalid payments and clear UTR
- 📬 **Reminder System**: Send bulk reminders for unpaid bills
- 👥 **Resident Management**: View and manage resident details

### 🏠 Resident Dashboard
- 💳 **View Bills**: See all monthly bills with status and approval state
- 📱 **QR Payment**: Pay bills via QR code and enter UTR
- 📊 **Payment Tracking**: Monitor payment status (UNPAID → PENDING → APPROVED/REJECTED)
- 🔔 **Reminders**: View unread reminder messages with counter badge

### 🔧 Core Engine
- 🛡️ **Duplicate Protection**: Prevents multiple bills for same resident/month/year
- 🔄 **Status Workflow**: UNPAID → PENDING → PAID/APPROVED or REJECTED
- 📂 **Database Abstraction**: MySQL (production) or SQLite (development)
- 🎨 **Modern UI**: Color-coded status indicators, row highlighting, and smooth UX

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Frontend/UI** | Java Swing (Nimbus Look & Feel) |
| **Backend** | Java 21, Maven |
| **Database** | MySQL 8.0 (prod), SQLite (dev) |
| **Data Access** | JDBC (mysql-connector-j 8.0.33) |
| **Build Tool** | Maven (with Shade plugin for fat JAR) |
| **Logging** | java.util.logging |
| **CI/CD** | GitHub Actions |

---

## 📁 Project Structure

```
society-maintenance-app/
│
├── .github/
│   └── workflows/
│       ├── build.yml          # CI build on push/PR
│       └── release.yml        # Auto-release on tag push
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/myapp/
│   │   │       ├── dao/                   # Data Access Objects
│   │   │       │   ├── BillDao.java
│   │   │       │   ├── MessageDao.java
│   │   │       │   └── UserDao.java
│   │   │       │
│   │   │       ├── db/                    # Database Layer
│   │   │       │   └── DatabaseConnector.java
│   │   │       │
│   │   │       ├── gui/                   # UI Components
│   │   │       │   ├── AdminDashboard.java
│   │   │       │   ├── ResidentDashboard.java
│   │   │       │   ├── LoginFrame.java
│   │   │       │   ├── SignUpFrame.java
│   │   │       │   ├── WelcomeFrame.java
│   │   │       │   ├── PaymentDialog.java
│   │   │       │   ├── MessagesDialog.java
│   │   │       │   ├── ResidentDetailsFrame.java
│   │   │       │   ├── UIUtils.java
│   │   │       │   ├── AppConstants.java
│   │   │       │   └── Main.java
│   │   │       │
│   │   │       └── model/                 # Domain Models
│   │   │           ├── Bill.java
│   │   │           ├── User.java
│   │   │           └── Message.java
│   │   │
│   │   └── resources/
│   │       ├── images/
│   │       │   └── qr-code.png
│   │       └── logging.properties
│   │
│   └── test/                              # Unit Tests (TBD)
│
├── target/                                # Build artifacts
├── .gitignore
├── pom.xml                                # Maven config
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- ☕ **Java 21+** ([Download](https://adoptium.net/))
- 📦 **Maven 3.9+** ([Download](https://maven.apache.org/download.cgi))
- 🗄️ **MySQL 8.0+** (optional, for production) ([Download](https://dev.mysql.com/downloads/))

### Installation

1️⃣ **Clone the repository**
```bash
git clone https://github.com/Praful-7723/society-maintenance-app.git
cd society-maintenance-app
```

2️⃣ **Configure Database (MySQL - Production)**

Create a database and set environment variables:

```bash
export DB_TYPE=mysql
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=society
export DB_USER=your_username
export DB_PASS=your_password
```

> 💡 **Tip**: For development, skip this step—the app will auto-create a SQLite database (`society.db`).

3️⃣ **Build the project**
```bash
mvn clean package
```

4️⃣ **Run the application**
```bash
java -jar target/society-maintenance-app-1.0-SNAPSHOT.jar
```

### 🔑 Default Credentials

On first run, a default admin account is created:

| Email | Password | Role |
|-------|----------|------|
| `admin@society.com` | `admin123` | ADMIN |

> ⚠️ **Security**: Change the default admin password after first login!

---

## 📖 Usage

### Admin Workflow

1. **Login** as admin
2. **Navigate** to desired month/year using the dropdowns
3. **Create Bills** for all residents with one click
4. **Review Payments**: Select pending payments and approve/reject
5. **Send Reminders**: Bulk-send reminders to residents with unpaid bills

### Resident Workflow

1. **Sign Up** with your details (name, email, flat number)
2. **Login** and view your monthly bills
3. **Pay Bill**: Select an unpaid bill, scan QR, and enter UTR
4. **Track Status**: Monitor approval status (PENDING → APPROVED/REJECTED)
5. **View Reminders**: Check unread messages from admin

---

## 📸 Screenshots

### Welcome Screen
![Welcome](docs/screenshots/welcome.png)
*Simple navigation: Admin Login, Resident Login, or Sign Up*

### Admin Dashboard
![Admin Dashboard](docs/screenshots/admin-dashboard.png)
*Month/year filtering, bill creation, approval/rejection, and reminders*

### Resident Dashboard
![Resident Dashboard](docs/screenshots/resident-dashboard.png)
*View bills, pay with QR/UTR, and check payment status*

### Payment Dialog
![Payment](docs/screenshots/payment-dialog.png)
*QR code display with UTR input for verification*

> 📝 **Note**: Add actual screenshots to `docs/screenshots/` folder.

---

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Praful**
- GitHub: [@Praful-7723](https://github.com/Praful-7723)

---

## 🙏 Acknowledgments

- Java Swing for the desktop UI framework
- MySQL for robust production database
- Maven Shade Plugin for single-JAR deployment

---

<div align="center">
  Made with ❤️ for better society management
</div>
