import java.io.*;
import java.net.*;
import java.sql.*;

/*
    Compile:  javac -cp ".;mysql-connector-j-9.6.0.jar" Server.java
     Run:      java  -cp ".;mysql-connector-j-9.6.0.jar" Server
*/

public class Server {

    static String DB_URL  = "jdbc:mysql://localhost:3306/library_std";
    static String DB_USER = "root";
    static String DB_PASS = "beekrm";

    static Connection con;

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        System.out.println("Connected to database.");

        ServerSocket ss = new ServerSocket(9999);
        System.out.println("Server started on port 9999. Waiting for client...");

        while (true) {
            Socket client = ss.accept();
            System.out.println("Client connected!");

            BufferedReader in  = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter    out = new PrintWriter(client.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Command: " + line);
                String response = handleCommand(line);
                out.println(response);
            }

            client.close();
            System.out.println("Client disconnected.");
        }
    }
    // Handle commands takes cmd string, processes it, and returns response string which is sent back to client. All database interactions happen here.

    static String handleCommand(String cmd) {
        String[] parts  = cmd.split("\\|");  // regex to split by | character
        String   action = parts[0];

        try {

            
            // STUDENT_VIEW — student sees their own details
            // FORMAT: STUDENT_VIEW|student_id
            
            if (action.equals("STUDENT_VIEW")) {
                int sid = Integer.parseInt(parts[1]);

                PreparedStatement ps = con.prepareStatement(
                    "SELECT student_id, student_name FROM students WHERE student_id = ?"
                );
                ps.setInt(1, sid);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) return "ERROR: No student found with ID " + sid;

                String result = "";
                result += "Student ID   : " + rs.getInt("student_id") + "\\n";
                result += "Student Name : " + rs.getString("student_name") + "\\n\\n";

                PreparedStatement ps2 = con.prepareStatement(
                    "SELECT b.book_id, b.book_name, sb.issue_date, sb.return_date " +
                    "FROM student_books sb " +
                    "JOIN books b ON sb.book_id = b.book_id " +
                    "WHERE sb.student_id = ? ORDER BY sb.issue_date"
                );
                ps2.setInt(1, sid);
                ResultSet rs2 = ps2.executeQuery();

                result += "Books Issued :\\n";
                result += "--------------------------------------------------\\n";
                boolean hasBooks = false;
                while (rs2.next()) {
                    hasBooks = true;
                    String ret = rs2.getString("return_date");
                    result += "  Book ID    : " + rs2.getInt("book_id") + "\\n";
                    result += "  Book Name  : " + rs2.getString("book_name") + "\\n";
                    result += "  Issue Date : " + rs2.getString("issue_date") + "\\n";
                    result += "  Returned   : " + (ret != null ? ret : "Not returned yet") + "\\n";
                    result += "  --\\n";
                }
                if (!hasBooks) result += "  No books currently issued.\\n";

                return result;
            }

            
            // FORMAT: ADMIN_ADD|student_id|student_name|book_id1,book_id2 (or "none")
            
            else if (action.equals("ADMIN_ADD")) {
                int    sid      = Integer.parseInt(parts[1]);
                String name     = parts[2].trim();
                String bookPart = parts[3].trim();

                // Student ID must NOT already exist
                PreparedStatement chk = con.prepareStatement(
                    "SELECT student_id FROM students WHERE student_id = ?"
                );
                chk.setInt(1, sid);
                if (chk.executeQuery().next()) return "ERROR: Student ID " + sid + " already exists.";

                // Insert the student
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO students (student_id, student_name) VALUES (?, ?)"
                );
                ps.setInt(1, sid);
                ps.setString(2, name);
                ps.executeUpdate();

                // Issue books if any were provided
                if (!bookPart.equals("none") && !bookPart.isEmpty()) {
                    String[] bookIds = bookPart.split(",");
                    for (String bid : bookIds) {
                        bid = bid.trim();
                        if (bid.isEmpty()) continue;
                        String err = issueBookToStudent(sid, Integer.parseInt(bid));
                        if (err != null) return "Student created but book error: " + err;
                    }
                }

                return "SUCCESS: Student " + name + " created with ID " + sid + ".";
            }

            // =============================================
            // ADMIN_VIEW — view one student by ID
            // FORMAT: ADMIN_VIEW|student_id
            // =============================================
            else if (action.equals("ADMIN_VIEW")) {
                int sid = Integer.parseInt(parts[1]);

                PreparedStatement ps = con.prepareStatement(
                    "SELECT student_id, student_name FROM students WHERE student_id = ?"
                );
                ps.setInt(1, sid);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) return "ERROR: Student ID " + sid + " not found.";

                String result = "";
                result += "Student ID   : " + rs.getInt("student_id") + "\\n";
                result += "Student Name : " + rs.getString("student_name") + "\\n\\n";

                PreparedStatement ps2 = con.prepareStatement(
                    "SELECT b.book_id, b.book_name, sb.issue_date, sb.return_date " +
                    "FROM student_books sb " +
                    "JOIN books b ON sb.book_id = b.book_id " +
                    "WHERE sb.student_id = ? ORDER BY sb.issue_date"
                );
                ps2.setInt(1, sid);
                ResultSet rs2 = ps2.executeQuery();

                result += "Books Issued:\\n";
                result += "--------------------------------------------------\\n";
                boolean any = false;
                while (rs2.next()) {
                    any = true;
                    String ret = rs2.getString("return_date");
                    result += "  [" + rs2.getInt("book_id") + "] " + rs2.getString("book_name") +
                              " | Issued: " + rs2.getString("issue_date") +
                              " | Returned: " + (ret != null ? ret : "Not yet") + "\\n";
                }
                if (!any) result += "  No books issued.\\n";

                return result;
            }

            // =============================================
            // ADMIN_VIEW_ALL — view every student with their books
            // FORMAT: ADMIN_VIEW_ALL
            // =============================================
            else if (action.equals("ADMIN_VIEW_ALL")) {

                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(
                    "SELECT student_id, student_name FROM students ORDER BY student_id"
                );

                String result = "";
                boolean anyStudent = false;

                while (rs.next()) {
                    anyStudent = true;
                    int    sid  = rs.getInt("student_id");
                    String sname = rs.getString("student_name");

                    result += "================================\\n";
                    result += "Student ID   : " + sid + "\\n";
                    result += "Student Name : " + sname + "\\n";

                    // Fetch all books for this student
                    PreparedStatement ps2 = con.prepareStatement(
                        "SELECT b.book_id, b.book_name, sb.issue_date, sb.return_date " +
                        "FROM student_books sb " +
                        "JOIN books b ON sb.book_id = b.book_id " +
                        "WHERE sb.student_id = ? ORDER BY sb.issue_date"
                    );
                    ps2.setInt(1, sid);
                    ResultSet rs2 = ps2.executeQuery();

                    boolean anyBook = false;
                    while (rs2.next()) {
                        anyBook = true;
                        String ret = rs2.getString("return_date");
                        result += "  Book      : [" + rs2.getInt("book_id") + "] " + rs2.getString("book_name") + "\\n";
                        result += "  Issued    : " + rs2.getString("issue_date") + "\\n";
                        result += "  Returned  : " + (ret != null ? ret : "Not returned yet") + "\\n";
                    }

                    if (!anyBook) {
                        result += "  Books     : No books issued\\n";
                    }

                    result += "\\n";
                }

                if (!anyStudent) return "No students registered yet.";

                return result;
            }

            // ADMIN_UPDATE — update student name and books
            // FORMAT: ADMIN_UPDATE|student_id|new_name|book_id1,book_id2 (or "none")
            
            else if (action.equals("ADMIN_UPDATE")) {
                int    sid      = Integer.parseInt(parts[1]);
                String newName  = parts[2].trim();
                String bookPart = parts[3].trim();

                // Student ID MUST exist
                PreparedStatement chk = con.prepareStatement(
                    "SELECT student_id FROM students WHERE student_id = ?"
                );
                chk.setInt(1, sid);
                if (!chk.executeQuery().next()) return "ERROR: Student ID " + sid + " not found.";

                // Update name
                PreparedStatement upd = con.prepareStatement(
                    "UPDATE students SET student_name = ? WHERE student_id = ?"
                );
                upd.setString(1, newName);
                upd.setInt(2, sid);
                upd.executeUpdate();

                // Free all books this student currently has
                PreparedStatement getBooks = con.prepareStatement(
                    "SELECT book_id FROM student_books WHERE student_id = ?"
                );
                getBooks.setInt(1, sid);
                ResultSet brs = getBooks.executeQuery();
                while (brs.next()) {
                    PreparedStatement freeBook = con.prepareStatement(
                        "UPDATE books SET available = TRUE WHERE book_id = ?"
                    );
                    freeBook.setInt(1, brs.getInt("book_id"));
                    freeBook.executeUpdate();
                }

                // Delete all old book assignments
                PreparedStatement del = con.prepareStatement(
                    "DELETE FROM student_books WHERE student_id = ?"
                );
                del.setInt(1, sid);
                del.executeUpdate();

                // Assign new books if any
                if (!bookPart.equals("none") && !bookPart.isEmpty()) {
                    String[] bookIds = bookPart.split(",");
                    for (String bid : bookIds) {
                        bid = bid.trim();
                        if (bid.isEmpty()) continue;
                        String err = issueBookToStudent(sid, Integer.parseInt(bid));
                        if (err != null) return "Student updated but book error: " + err;
                    }
                }

                return "SUCCESS: Student ID " + sid + " updated.";
            }

            // ADMIN_DELETE — delete a student by ID
            // FORMAT: ADMIN_DELETE|student_id
           
            else if (action.equals("ADMIN_DELETE")) {
                int sid = Integer.parseInt(parts[1]);

                // Student MUST exist
                PreparedStatement chk = con.prepareStatement(
                    "SELECT student_id FROM students WHERE student_id = ?"
                );
                chk.setInt(1, sid);
                if (!chk.executeQuery().next()) return "ERROR: Student ID " + sid + " not found.";

                // Free all books this student has
                PreparedStatement getBooks = con.prepareStatement(
                    "SELECT book_id FROM student_books WHERE student_id = ?"
                );
                getBooks.setInt(1, sid);
                ResultSet brs = getBooks.executeQuery();
                while (brs.next()) {
                    PreparedStatement freeBook = con.prepareStatement(
                        "UPDATE books SET available = TRUE WHERE book_id = ?"
                    );
                    freeBook.setInt(1, brs.getInt("book_id"));
                    freeBook.executeUpdate();
                }

                // Delete from student_books first (foreign key)
                PreparedStatement delBooks = con.prepareStatement(
                    "DELETE FROM student_books WHERE student_id = ?"
                );
                delBooks.setInt(1, sid);
                delBooks.executeUpdate();

                // Now delete the student
                PreparedStatement delStudent = con.prepareStatement(
                    "DELETE FROM students WHERE student_id = ?"
                );
                delStudent.setInt(1, sid);
                delStudent.executeUpdate();

                return "SUCCESS: Student ID " + sid + " deleted.";
            }

            else {
                return "ERROR: Unknown command.";
            }

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    // =============================================
    // Helper: issue one book to a student
    // Returns null on success, error string on failure
   
    static String issueBookToStudent(int studentId, int bookId) throws SQLException {

        // Check book exists and is available
        PreparedStatement bchk = con.prepareStatement(
            "SELECT book_id, available FROM books WHERE book_id = ?"
        );
        bchk.setInt(1, bookId);
        ResultSet brs = bchk.executeQuery();
        if (!brs.next()) return "Book ID " + bookId + " does not exist in library.";
        if (!brs.getBoolean("available")) return "Book ID " + bookId + " is already issued to another student.";

        // Check student does not already have this same book
        PreparedStatement dupChk = con.prepareStatement(
            "SELECT id FROM student_books WHERE student_id = ? AND book_id = ? AND return_date IS NULL"
        );
        dupChk.setInt(1, studentId);
        dupChk.setInt(2, bookId);
        if (dupChk.executeQuery().next()) return "Book ID " + bookId + " is already issued to this student.";

        // Insert into student_books
        PreparedStatement ins = con.prepareStatement(
            "INSERT INTO student_books (student_id, book_id, issue_date) VALUES (?, ?, CURDATE())"
        );
        ins.setInt(1, studentId);
        ins.setInt(2, bookId);
        ins.executeUpdate();

        // Mark book as unavailable
        PreparedStatement mark = con.prepareStatement(
            "UPDATE books SET available = FALSE WHERE book_id = ?"
        );
        mark.setInt(1, bookId);
        mark.executeUpdate();

        return null; // success
    }
}