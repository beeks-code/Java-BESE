import java.io.*;
import java.net.*;
import java.sql.*;


public class Server {

    static String DB_URL  = "jdbc:mysql://localhost:3306/library_db";
    static String DB_USER = "root";
    static String DB_PASS = "beekrm";


    static Connection con;

    public static void main(String[] args) throws Exception {

            Class.forName("com.mysql.cj.jdbc.Driver");   // ADD THIS LINE
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

    static String handleCommand(String cmd) {
        String[] parts = cmd.split("\\|");
        String action  = parts[0];

        try {

            // =============================================
            // STUDENT PORTAL: View my details by student ID
            // =============================================
            if (action.equals("STUDENT_VIEW")) {
                int id = Integer.parseInt(parts[1]);

                PreparedStatement ps = con.prepareStatement(
                    "SELECT s.student_id, s.student_name, s.book_id, b.book_name, " +
                    "s.issue_date, s.return_date " +
                    "FROM students s LEFT JOIN books b ON s.book_id = b.book_id " +
                    "WHERE s.student_id = ?"
                );
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    return "ERROR: No student found with ID " + id;
                }

                String name       = rs.getString("student_name");
                String bookId     = rs.getString("book_id");
                String bookName   = rs.getString("book_name");
                String issueDate  = rs.getString("issue_date");
                String returnDate = rs.getString("return_date");

                String result = "";
                result += "Student ID   : " + id + "\\n";
                result += "Student Name : " + name + "\\n";

                if (bookId == null) {
                    result += "Book Issued  : None\\n";
                } else {
                    result += "Book ID      : " + bookId + "\\n";
                    result += "Book Name    : " + bookName + "\\n";
                    result += "Issue Date   : " + issueDate + "\\n";
                    result += "Return Date  : " + (returnDate != null ? returnDate : "Not returned yet") + "\\n";
                }

                return result;
            }

            // =============================================
            // ADMIN: Add new student
            // FORMAT: ADMIN_ADD|student_id|student_name|book_id(optional)
            // =============================================
            else if (action.equals("ADMIN_ADD")) {
                int    sid  = Integer.parseInt(parts[1]);
                String name = parts[2].trim();
                String bid  = parts[3].trim(); // can be "none"

                // Check: student ID must NOT already exist
                PreparedStatement chk = con.prepareStatement("SELECT student_id FROM students WHERE student_id = ?");
                chk.setInt(1, sid);
                ResultSet rs = chk.executeQuery();
                if (rs.next()) {
                    return "ERROR: Student ID " + sid + " already exists. Use a different ID.";
                }

                if (bid.equals("none") || bid.isEmpty()) {
                    // No book assigned
                    PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO students (student_id, student_name) VALUES (?, ?)"
                    );
                    ps.setInt(1, sid);
                    ps.setString(2, name);
                    ps.executeUpdate();
                } else {
                    int bookId = Integer.parseInt(bid);

                    // Check: book must exist in library
                    PreparedStatement bchk = con.prepareStatement("SELECT book_id, available FROM books WHERE book_id = ?");
                    bchk.setInt(1, bookId);
                    ResultSet brs = bchk.executeQuery();
                    if (!brs.next()) {
                        return "ERROR: Book ID " + bookId + " does not exist in library.";
                    }
                    if (!brs.getBoolean("available")) {
                        return "ERROR: Book ID " + bookId + " is already issued to another student.";
                    }

                    // Insert student with book
                    PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO students (student_id, student_name, book_id, issue_date) VALUES (?, ?, ?, CURDATE())"
                    );
                    ps.setInt(1, sid);
                    ps.setString(2, name);
                    ps.setInt(3, bookId);
                    ps.executeUpdate();

                    // Mark book unavailable
                    PreparedStatement upd = con.prepareStatement("UPDATE books SET available = false WHERE book_id = ?");
                    upd.setInt(1, bookId);
                    upd.executeUpdate();
                }

                return "SUCCESS: Student " + name + " added with ID " + sid + ".";
            }

            // =============================================
            // ADMIN: Update student
            // FORMAT: ADMIN_UPDATE|student_id|new_name|new_book_id(or "none")
            // =============================================
            else if (action.equals("ADMIN_UPDATE")) {
                int    sid     = Integer.parseInt(parts[1]);
                String newName = parts[2].trim();
                String newBid  = parts[3].trim();

                // Check: student ID MUST exist
                PreparedStatement chk = con.prepareStatement("SELECT student_id, book_id FROM students WHERE student_id = ?");
                chk.setInt(1, sid);
                ResultSet rs = chk.executeQuery();
                if (!rs.next()) {
                    return "ERROR: Student ID " + sid + " not found. Cannot update.";
                }

                // Get old book ID so we can free it
                String oldBid = rs.getString("book_id");

                // Free old book if there was one
                if (oldBid != null) {
                    PreparedStatement free = con.prepareStatement("UPDATE books SET available = true WHERE book_id = ?");
                    free.setInt(1, Integer.parseInt(oldBid));
                    free.executeUpdate();
                }

                if (newBid.equals("none") || newBid.isEmpty()) {
                    // Update name only, clear book
                    PreparedStatement ps = con.prepareStatement(
                        "UPDATE students SET student_name = ?, book_id = NULL, issue_date = NULL, return_date = NULL WHERE student_id = ?"
                    );
                    ps.setString(1, newName);
                    ps.setInt(2, sid);
                    ps.executeUpdate();
                } else {
                    int newBookId = Integer.parseInt(newBid);

                    // Check: new book must exist
                    PreparedStatement bchk = con.prepareStatement("SELECT book_id, available FROM books WHERE book_id = ?");
                    bchk.setInt(1, newBookId);
                    ResultSet brs = bchk.executeQuery();
                    if (!brs.next()) {
                        // Restore old book since we freed it
                        if (oldBid != null) {
                            PreparedStatement restore = con.prepareStatement("UPDATE books SET available = false WHERE book_id = ?");
                            restore.setInt(1, Integer.parseInt(oldBid));
                            restore.executeUpdate();
                        }
                        return "ERROR: Book ID " + newBookId + " does not exist in library.";
                    }
                    if (!brs.getBoolean("available")) {
                        if (oldBid != null) {
                            PreparedStatement restore = con.prepareStatement("UPDATE books SET available = false WHERE book_id = ?");
                            restore.setInt(1, Integer.parseInt(oldBid));
                            restore.executeUpdate();
                        }
                        return "ERROR: Book ID " + newBookId + " is already issued to another student.";
                    }

                    // Update student with new book
                    PreparedStatement ps = con.prepareStatement(
                        "UPDATE students SET student_name = ?, book_id = ?, issue_date = CURDATE(), return_date = NULL WHERE student_id = ?"
                    );
                    ps.setString(1, newName);
                    ps.setInt(2, newBookId);
                    ps.setInt(3, sid);
                    ps.executeUpdate();

                    // Mark new book as unavailable
                    PreparedStatement mark = con.prepareStatement("UPDATE books SET available = false WHERE book_id = ?");
                    mark.setInt(1, newBookId);
                    mark.executeUpdate();
                }

                return "SUCCESS: Student ID " + sid + " updated.";
            }

            // =============================================
            // ADMIN: Delete student
            // FORMAT: ADMIN_DELETE|student_id
            // =============================================
            else if (action.equals("ADMIN_DELETE")) {
                int sid = Integer.parseInt(parts[1]);

                // Check: student ID MUST exist
                PreparedStatement chk = con.prepareStatement("SELECT student_id, book_id FROM students WHERE student_id = ?");
                chk.setInt(1, sid);
                ResultSet rs = chk.executeQuery();
                if (!rs.next()) {
                    return "ERROR: Student ID " + sid + " not found. Cannot delete.";
                }

                // Free the book if student had one
                String bid = rs.getString("book_id");
                if (bid != null) {
                    PreparedStatement free = con.prepareStatement("UPDATE books SET available = true WHERE book_id = ?");
                    free.setInt(1, Integer.parseInt(bid));
                    free.executeUpdate();
                }

                // Delete the student
                PreparedStatement ps = con.prepareStatement("DELETE FROM students WHERE student_id = ?");
                ps.setInt(1, sid);
                ps.executeUpdate();

                return "SUCCESS: Student ID " + sid + " deleted.";
            }

            // =============================================
            // ADMIN: View student by ID
            // FORMAT: ADMIN_VIEW|student_id
            // =============================================
            else if (action.equals("ADMIN_VIEW")) {
                int sid = Integer.parseInt(parts[1]);

                PreparedStatement ps = con.prepareStatement(
                    "SELECT s.student_id, s.student_name, s.book_id, b.book_name, " +
                    "s.issue_date, s.return_date " +
                    "FROM students s LEFT JOIN books b ON s.book_id = b.book_id " +
                    "WHERE s.student_id = ?"
                );
                ps.setInt(1, sid);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    return "ERROR: Student ID " + sid + " not found.";
                }

                String result = "";
                result += "Student ID   : " + rs.getInt("student_id") + "\\n";
                result += "Student Name : " + rs.getString("student_name") + "\\n";
                String bookId = rs.getString("book_id");
                if (bookId == null) {
                    result += "Book Issued  : None\\n";
                } else {
                    result += "Book ID      : " + bookId + "\\n";
                    result += "Book Name    : " + rs.getString("book_name") + "\\n";
                    result += "Issue Date   : " + rs.getString("issue_date") + "\\n";
                    result += "Return Date  : " + (rs.getString("return_date") != null ? rs.getString("return_date") : "Not returned yet") + "\\n";
                }
                return result;
            }

            // =============================================
            // ADMIN: View all students
            // =============================================
            else if (action.equals("ADMIN_VIEW_ALL")) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(
                    "SELECT s.student_id, s.student_name, s.book_id, b.book_name, s.issue_date " +
                    "FROM students s LEFT JOIN books b ON s.book_id = b.book_id ORDER BY s.student_id"
                );

                String result = "ID  | Name                | Book ID | Book Name\\n";
                result        += "----+-----------------------+---------+----------------------\\n";
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    result += String.format("%-4d| %-22s| %-8s| %s\\n",
                        rs.getInt("student_id"),
                        rs.getString("student_name"),
                        rs.getString("book_id") != null ? rs.getString("book_id") : "None",
                        rs.getString("book_name") != null ? rs.getString("book_name") : "None"
                    );
                }
                if (!any) result += "(No students registered yet)\\n";
                return result;
            }

            // =============================================
            // View all books (used by both portals)
            // =============================================
            else if (action.equals("VIEW_BOOKS")) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM books ORDER BY book_id");

                String result = "ID  | Book Name                    | Available\\n";
                result        += "----+------------------------------+----------\\n";
                while (rs.next()) {
                    result += String.format("%-4d| %-30s| %s\\n",
                        rs.getInt("book_id"),
                        rs.getString("book_name"),
                        rs.getBoolean("available") ? "Yes" : "No (Issued)"
                    );
                }
                return result;
            }

            else {
                return "ERROR: Unknown command.";
            }

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
