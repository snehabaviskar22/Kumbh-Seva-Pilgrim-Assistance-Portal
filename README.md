# 🕉️ KumbhSeva 2027 — Pilgrim Assistance Portal
> **Nashik Simhastha Kumbh Mela 2027 | Full-Stack MCA Academic Project**

---

## 📌 Overview

**KumbhSeva** is a web-based pilgrim assistance and lost-and-found management portal built for the **Nashik Simhastha Kumbh Mela 2027**. It connects **Pilgrims**, **Seva Volunteers**, and **Administrators** to report lost belongings, trace missing persons, and process claims securely.
---

## ✨ Key Features

- **Lost & Found Management:** Report lost belongings and found items with photo uploads.
- **Missing Persons Tracking:** Dedicated registration and status tracking for missing individuals.
- **Claim & Restitution Workflow:** Secure claim filing with ID proof; approved claims automatically mark items as returned.
- **4-Stage Live Tracking:** Real-time tracking from *Reported* ➔ *Under Review* ➔ *Matched* ➔ *Returned*.
- **Interactive Ghat Navigation:** Leaflet.js maps of Nashik's sacred ghats with GPS routes.
- **Admin Command Center:** Live metrics, inventory controls, and volunteer approval queue.

---

## 🛠️ Technology Stack

- **Backend:** Java 17 / 21, Spring Boot 3.2.5, Spring Data JPA, Hibernate
- **Database:** MySQL 8.x / 9.x (`kumbh_db`)
- **Frontend:** HTML5, CSS3, JavaScript (ES6+ Fetch API), Leaflet.js
- **Security:** SHA-256 password hashing, session management, role-based access control
- **AI Note:** *Frontend UI layouts and responsive styling were assisted using Generative AI tools.*

---

## 🗄️ Database Architecture

The system uses **6 relational SQL tables** with foreign key constraints:

1. **`users`** — User accounts with roles (`PILGRIM`, `VOLUNTEER`, `ADMIN`) and approval status.
2. **`lost_items`** — Lost property and missing person reports (FK ➔ `users`).
3. **`found_items`** — Found items with photo uploads and drop-off points (FK ➔ `users`).
4. **`claims`** — Claims submitted with ID proof against found items (FK ➔ `found_items`, FK ➔ `users`).
5. **`missing_persons`** — Dedicated missing person records and photo references (FK ➔ `lost_items`, FK ➔ `users`).
6. **`inquiries`** — Grievances and contact submissions.

---

## 📸 Screenshots

### 1. Home Page & Holy Ghats Navigation
![Home Page](screenshots/01_homepage.png)
*Interactive landing page featuring Kumbh Mela services and ghat locations.*

### 2. Lost & Found Portal
![Lost & Found Portal](screenshots/02_lost_found_portal.png)
*Central dashboard displaying live database statistics and reporting shortcuts.*

### 3. Report Lost Belonging / Missing Person
![Report Lost Item](screenshots/03_report_lost.png)
*Submission form with category filters, last-seen location, and image upload.*

### 4. Browse Found Items Catalog
![View Found Items](screenshots/04_view_found_items.png)
*Searchable catalog with real-time status and item detail views.*

### 5. Item Claim & Verification Form
![Claim Submission](screenshots/05_claim_item.png)
*Claim verification form requiring government ID proof.*

### 6. Admin Command Center & Volunteer Approval
![Admin Dashboard](screenshots/06_admin_dashboard.png)
*Administrative dashboard for live metrics, claim reviews, and volunteer approvals.*

---

## 🚀 Quick Start

### 1. Database Setup
1. Create a MySQL database named `kumbh_db`.
2. Configure your MySQL credentials in:
   `demo/src/main/resources/application.properties`
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/kumbh_db
   spring.datasource.username=YOUR_MYSQL_USERNAME
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```
3. Tables initialize automatically on backend startup via JPA (`ddl-auto=update`), or optionally import `kumbhsql.sql`.

### 2. Start the Backend
```powershell
cd KumbhSeva/demo
mvn spring-boot:run
```
Backend runs at `http://localhost:8080`.

### 3. Start the Frontend
Serve the frontend using any static server:
```powershell
cd KumbhSeva/frontend/kumbh-project
python -m http.server 5500
```
Open in browser: `http://localhost:5500/index.html`.

---

## 🔑 Role-Based Access for Demonstrations

The system enforces three distinct access roles:

- **Administrator:** Full access to inventory, live statistics, claim adjudications, and volunteer approvals.
- **Seva Volunteer:** Operational access to view records and verify found items.
- **Pilgrim:** Public access to submit reports, file claims, and track statuses.

---

## 📂 Project Structure

```text
KumbhSeva/
├── demo/                               # Spring Boot Backend (REST APIs & JPA Entities)
├── frontend/kumbh-project/             # Client Web Application (HTML/CSS/JS)
├── kumbhsql.sql                        # SQL Schema Script
└── README.md                           # Documentation
```
