## 📚 Library Management System

A simple, user-friendly Library Management System built with **Java Swing** for the GUI, **MySQL** for database storage, and **JDBC** for database communication. The system follows proper **Object-Oriented Programming (OOP)** principles using `Book`, `Student`, and DAO classes to separate concerns.

---

## ✨ Features

- 🔍 **Search Books** – Find books by title or author.
- 📥 **Issue Books** – Issue books to students with return tracking.
- 📤 **Return Books** – Update the system when books are returned.
- 🧾 **Manage Inventory** – Add, update, or delete books.
- 🧑‍🎓 **Student Records** – Add and manage student details.
- 📊 **Dashboard** – Overview of issued, returned, and available books.

---

## 🛠️ Tech Stack

| Component | Technology |
|----------|------------|
| Language | Java |
| GUI      | Java Swing |
| Database | MySQL |
| DB Access| JDBC (Java Database Connectivity) |

---

## 🧱 Project Structure

LibraryManagementSystem/
├── src/
│ ├── gui/ # All GUI forms (e.g. MainForm, IssueBookForm)
│ ├── model/ # POJO classes (Book.java, Student.java)
│ ├── dao/ # DAO classes (BookDAO.java, StudentDAO.java)
│ └── util/ # Utility classes (DBConnection.java)
├── lib/ # JDBC driver (mysql-connector-java.jar)
└── README.md # Project documentation
