# Expense Tracker

Beginner-friendly full-stack project:

- **Backend:** Java 17 + Spring Boot 3 (Maven)
- **Mobile:** Flutter (Android / iOS)
- **Database:** MySQL (`expense_db`)

This README grows with each stage. **Stage 1 (database schema) is ready to run.** Stages 2–4 (backend, Flutter, wiring) come next.

---

## Stage 1 — Database setup

### Prerequisites

- MySQL 8 installed and running (Workbench or command line is fine)
- A user that can create databases (often `root` on a local machine)

### 1. Create the schema and seed data

**PowerShell (Windows):** `<` is not allowed, so pipe the file instead. From `expense-tracker`:

```powershell
Get-Content .\schema.sql -Raw | mysql -u root -p
```

**Git Bash / Linux / macOS:**

```bash
mysql -u root -p < schema.sql
```

**MySQL client (any OS):**

```sql
SOURCE C:/Users/LENOVO/Desktop/mm/expense-tracker/schema.sql;
```

Enter your MySQL password when prompted. If `mysql` is not found, add MySQL’s `bin` folder to PATH, or use the full path (often `C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe`).

### 2. Confirm it worked

```sql
USE expense_db;
SHOW TABLES;
SELECT id, name, email FROM users;
SELECT * FROM categories;
SELECT id, title, amount, expense_date FROM expenses;
```

You should see three tables (`users`, `categories`, `expenses`) and a few demo rows.

### Demo login (for later backend tests)

| Email               | Plain password |
|---------------------|----------------|
| `alice@example.com` | `password`     |
| `bob@example.com`   | `password`     |

The `password_hash` column stores a **BCrypt** hash, not the password itself.

### Tables (interview-friendly)

| Table        | Role |
|-------------|------|
| `users`     | Accounts. `email` is unique. `password_hash` is BCrypt. |
| `categories`| Per-user labels (Food, Travel). `user_id` FK. |
| `expenses`  | Spends. `amount` is `DECIMAL(10,2)` so money is exact. |

Indexes:

- `categories(user_id)` — list a user’s categories quickly
- `expenses(user_id)` — list a user’s expenses quickly
- `expenses(expense_date)` — filter/sort by date

---

## Later stages (not in this folder yet)

- **Stage 2:** Spring Boot REST API
- **Stage 3:** Flutter app
- **Stage 4:** Connect app ↔ API

---

## API list

Added in stage 2.

---

## What I learned

Added as each stage lands. From stage 1:

- Use `DECIMAL` for money, not `FLOAT`.
- Put foreign keys on `user_id` / `category_id` so the database enforces ownership.
- Index columns you filter on (`user_id`, `expense_date`).
- Never store plain-text passwords; store a hash.
