
  *** Library Management System
  Simple Java Client-Server + MySQL + Swing


FILES:
  Server.java   - The server (handles all database logic)
  Client.java   - The GUI client (Swing)
  library.sql   - Database setup (10 fixed books)


SETUP (do once)


1. Install MySQL and open MySQL Workbench or terminal.

2. Run library.sql to create the database:
      source library.sql;
   This creates 10 fixed books (IDs 1 to 10).

3. Open Server.java and change these lines to your MySQL login:
      static String DB_USER = "root";
      static String DB_PASS = "password";

4. Download mysql-connector-java.jar from:
   https://dev.mysql.com/downloads/connector/j/
   Place the .jar file in the same folder as Server.java

--------------------------------------------
COMPILE
--------------------------------------------

On Windows:
   javac -cp .;mysql-connector-java.jar Server.java
   javac Client.java

On Mac/Linux:
   javac -cp .:mysql-connector-java.jar Server.java
   javac Client.java

--------------------------------------------
RUN (open TWO terminals)
--------------------------------------------

Terminal 1 - Start the server:
   Windows:   java -cp .;mysql-connector-java.jar Server
   Mac/Linux: java -cp .:mysql-connector-java.jar Server

Terminal 2 - Start the client:
   java Client

HOW TO USE

Home Screen:
  Click "Student" or "Admin"

Student Portal:
  - Type your Student ID and press Enter or click "View My Details"
  - Shows your name and the book you have issued (if any)

Admin Portal:
  - Choose an action from the dropdown:

  Add New Student:
    - Enter a NEW student ID (if ID already exists → error shown)
    - Enter student name
    - Optionally enter a Book ID from 1-10
      (if book doesn't exist → error shown)
      (if book already issued → error shown)

  Update Student:
    - Enter existing student ID (if not found → error shown)
    - Enter new name and optionally new book ID

  Delete Student:
    - Enter existing student ID (if not found → error shown)
    - A confirm popup appears before deleting

  View Student by ID:
    - Enter any student ID to see full details

  View All Students:
    - Shows a table of all registered students

  View All Books:
    - Shows all 10 library books and their availability

THE 10 BOOKS IN THE LIBRARY


ID  | Book Name
1   | Java Programming
2   | Data Structures
3   | Operating Systems
4   | Database Management
5   | Computer Networks
6   | Discrete Mathematics
7   | Software Engineering
8   | Artificial Intelligence
9   | Web Technologies
10  | Python Programming
