import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client {

    static Socket         socket;
    static BufferedReader in;
    static PrintWriter    out;

    public static void main(String[] args) {
        connectToServer();
        showRoleChooser();
    }

    static void connectToServer() {
        try {
            socket = new Socket("localhost", 9999);
            in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out    = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Cannot connect to server!\nMake sure Server is running first.\n\n" + e.getMessage(),
                "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

   // choose role: student or admin
    static void showRoleChooser() {
        JFrame frame = new JFrame("Library Management System");
        frame.setSize(420, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Welcome to the Library", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(40, 10, 10, 10));
        frame.add(title, BorderLayout.NORTH);

        JLabel sub = new JLabel("Please select your role:", SwingConstants.CENTER);
        sub.setFont(new Font("Arial", Font.PLAIN, 14));
        frame.add(sub, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 25));

        JButton studentBtn = new JButton("Student");
        studentBtn.setFont(new Font("Arial", Font.BOLD, 15));
        studentBtn.setBackground(new Color(70, 130, 180));
        studentBtn.setForeground(Color.WHITE);
        studentBtn.setPreferredSize(new Dimension(130, 50));

        JButton adminBtn = new JButton("Admin");
        adminBtn.setFont(new Font("Arial", Font.BOLD, 15));
        adminBtn.setBackground(new Color(34, 139, 34));
        adminBtn.setForeground(Color.WHITE);
        adminBtn.setPreferredSize(new Dimension(130, 50));

        btnPanel.add(studentBtn);
        btnPanel.add(adminBtn);
        frame.add(btnPanel, BorderLayout.SOUTH);

        studentBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // close this window
                showStudentPortal(); // open student portal
            }
        });
        adminBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                showAdminPortal();
            }
        });

        frame.setVisible(true);
    }
// std portal to view details by id

    static void showStudentPortal() {
        JFrame frame = new JFrame("Student Portal");
        frame.setSize(480, 620);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JLabel title = new JLabel("Student Portal");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        topSection.add(title);
        topSection.add(Box.createVerticalStrut(15));

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        inputRow.add(new JLabel("Enter Your Student ID:"));
        JTextField idField = new JTextField(8);
        idField.setFont(new Font("Arial", Font.PLAIN, 14));
        JButton searchBtn = new JButton("View My Details");
        searchBtn.setBackground(new Color(70, 130, 180));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Arial", Font.BOLD, 13));
        inputRow.add(idField);
        inputRow.add(searchBtn);
        topSection.add(inputRow);
        frame.add(topSection, BorderLayout.NORTH);

        JTextArea output = new JTextArea();
        output.setEditable(false);
        output.setFont(new Font("Monospaced", Font.PLAIN, 13));
        output.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        output.setText("\n  Enter your Student ID above and click 'View My Details'.");
        JScrollPane scroll = new JScrollPane(output);
        scroll.setBorder(BorderFactory.createTitledBorder("Your Details"));
        frame.add(scroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backBtn = new JButton("Back to Home");
        bottomPanel.add(backBtn);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                showRoleChooser();
            }
        });

        ActionListener doSearch = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sid = idField.getText().trim();
                if (sid.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter your Student ID.");
                    return;
                }
                try { Integer.parseInt(sid); }
                catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Student ID must be a number.");
                    return;
                }
                output.setText(sendCommand("STUDENT_VIEW|" + sid));
            }
        };
        searchBtn.addActionListener(doSearch);
        idField.addActionListener(doSearch);

        frame.setVisible(true);
    }
// admin portal with CRUD and view all students

    static void showAdminPortal() {
        JFrame frame = new JFrame("Admin Portal");
        frame.setSize(580, 660);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        // Title + dropdown
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBorder(BorderFactory.createEmptyBorder(12, 15, 5, 15));

        JLabel title = new JLabel("Admin Portal — Student Management");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        topSection.add(title);
        topSection.add(Box.createVerticalStrut(10));

        JPanel dropRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        dropRow.add(new JLabel("Select Action:"));

        // ── 5 options now including View All Students ──
        String[] options = {
            "-- Select Action --",
            "New Student",
            "View Student",
            "Update Student",
            "Delete Student",
            "View All Students"       
        };
        JComboBox<String> dropdown = new JComboBox<>(options);
        dropdown.setFont(new Font("Arial", Font.PLAIN, 13));
        dropdown.setPreferredSize(new Dimension(200, 28));
        dropRow.add(dropdown);
        topSection.add(dropRow);
        frame.add(topSection, BorderLayout.NORTH);

        // Dynamic form area
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder("Form"));
        frame.add(formPanel, BorderLayout.CENTER);

        // Output + back button
        JPanel bottomSection = new JPanel(new BorderLayout());
        JTextArea output = new JTextArea(8, 50);
        output.setEditable(false);
        output.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(output);
        scroll.setBorder(BorderFactory.createTitledBorder("Result"));

        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backBtn = new JButton("Back to Home");
        backRow.add(backBtn);
        bottomSection.add(scroll, BorderLayout.CENTER);
        bottomSection.add(backRow, BorderLayout.SOUTH);
        frame.add(bottomSection, BorderLayout.SOUTH);
         // Back button clicked takes to role chooser
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                showRoleChooser();
            }
        });

        // Rebuild form when dropdown changes
        dropdown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String choice = (String) dropdown.getSelectedItem();
                formPanel.removeAll();
                output.setText("");

                // for new student, need id, name, and optional book ids to issue 
                if (choice.equals("New Student")) {
                    JTextField sidF  = addField(formPanel, "Student ID (must not already exist):");
                    JTextField nameF = addField(formPanel, "Student Name:");
                    JTextField bidF  = addField(formPanel, "Book IDs to issue — comma separated e.g. 1,3,7  (leave blank for none):");
                    addBookHint(formPanel);

                    JButton btn = styledButton("New Student", new Color(34, 139, 34));
                    formPanel.add(btn);

                    btn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            String sid  = sidF.getText().trim();
                            String name = nameF.getText().trim();
                            String bid  = bidF.getText().trim();

                            if (sid.isEmpty() || name.isEmpty()) {
                                output.setText("ERROR: Student ID and Name cannot be empty.");
                                return;
                            }
                            try { Integer.parseInt(sid); }
                            catch (NumberFormatException ex) { output.setText("ERROR: Student ID must be a number."); return; }

                            if (!bid.isEmpty()) {
                                for (String b : bid.split(",")) {
                                    b = b.trim();
                                    if (b.isEmpty()) continue;
                                    try {
                                        int bInt = Integer.parseInt(b);
                                        if (bInt < 1 || bInt > 10) { output.setText("ERROR: Book ID " + b + " must be between 1 and 10."); return; }
                                    } catch (NumberFormatException ex) { output.setText("ERROR: '" + b + "' is not a valid Book ID."); return; }
                                }
                            }

                            String bookPart = bid.isEmpty() ? "none" : bid.replaceAll("\\s", "");
                            output.setText(sendCommand("ADMIN_ADD|" + sid + "|" + name + "|" + bookPart));
                        }
                    });
                }

                // ══ READ ══
                else if (choice.equals("View Student")) {
                    JTextField sidF = addField(formPanel, "Student ID to view:");
                    JButton btn = styledButton("View Student", new Color(70, 130, 180));
                    formPanel.add(btn);

                    btn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            String sid = sidF.getText().trim();
                            if (sid.isEmpty()) { output.setText("ERROR: Please enter a Student ID."); return; }
                            try { Integer.parseInt(sid); }
                            catch (NumberFormatException ex) { output.setText("ERROR: Student ID must be a number."); return; }
                            output.setText(sendCommand("ADMIN_VIEW|" + sid));
                        }
                    });
                }

                // ══ UPDATE ══
                else if (choice.equals("Update Student")) {
                    JTextField sidF  = addField(formPanel, "Student ID (must already exist):");
                    JTextField nameF = addField(formPanel, "New Student Name:");
                    JTextField bidF  = addField(formPanel, "New Book IDs — comma separated e.g. 2,5,9  (leave blank to remove all books):");
                    addBookHint(formPanel);

                    JLabel note = new JLabel("<html>&nbsp;&nbsp;<i>Note: new book IDs will replace all currently issued books.</i></html>");
                    note.setFont(new Font("Arial", Font.PLAIN, 11));
                    note.setForeground(new Color(150, 50, 50));
                    note.setBorder(BorderFactory.createEmptyBorder(2, 8, 6, 8));
                    note.setAlignmentX(Component.LEFT_ALIGNMENT);
                    formPanel.add(note);

                    JButton btn = styledButton("Update Student", new Color(200, 130, 0));
                    formPanel.add(btn);

                    btn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            String sid  = sidF.getText().trim();
                            String name = nameF.getText().trim();
                            String bid  = bidF.getText().trim();

                            if (sid.isEmpty() || name.isEmpty()) {
                                output.setText("ERROR: Student ID and New Name cannot be empty.");
                                return;
                            }
                            try { Integer.parseInt(sid); }
                            catch (NumberFormatException ex) { output.setText("ERROR: Student ID must be a number."); return; }

                            if (!bid.isEmpty()) {
                                for (String b : bid.split(",")) {
                                    b = b.trim();
                                    if (b.isEmpty()) continue;
                                    try {
                                        int bInt = Integer.parseInt(b);
                                        if (bInt < 1 || bInt > 10) { output.setText("ERROR: Book ID " + b + " must be between 1 and 10."); return; }
                                    } catch (NumberFormatException ex) { output.setText("ERROR: '" + b + "' is not a valid Book ID."); return; }
                                }
                            }

                            String bookPart = bid.isEmpty() ? "none" : bid.replaceAll("\\s", "");
                            output.setText(sendCommand("ADMIN_UPDATE|" + sid + "|" + name + "|" + bookPart));
                        }
                    });
                }

                // ══ DELETE ══
                else if (choice.equals("Delete Student")) {
                    JTextField sidF = addField(formPanel, "Student ID to delete (must exist in database):");
                    JButton btn = styledButton("Delete Student", new Color(178, 34, 34));
                    formPanel.add(btn);

                    btn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            String sid = sidF.getText().trim();
                            if (sid.isEmpty()) { output.setText("ERROR: Please enter a Student ID."); return; }
                            try { Integer.parseInt(sid); }
                            catch (NumberFormatException ex) { output.setText("ERROR: Student ID must be a number."); return; }

                            int confirm = JOptionPane.showConfirmDialog(frame,
                                "Are you sure you want to delete Student ID " + sid + "?",
                                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                            if (confirm != JOptionPane.YES_OPTION) return;

                            output.setText(sendCommand("ADMIN_DELETE|" + sid));
                        }
                    });
                }

                // ══ VIEW ALL STUDENTS ══  (NEW)
                else if (choice.equals("View All Students")) {
                    JButton btn = styledButton("Load All Students", new Color(100, 60, 180));
                    formPanel.add(btn);

                    btn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            output.setText(sendCommand("ADMIN_VIEW_ALL"));
                        }
                    });

                    // Auto load as soon as this option is selected
                    output.setText(sendCommand("ADMIN_VIEW_ALL"));
                }

                formPanel.revalidate();
                formPanel.repaint();
            }
        });

        frame.setVisible(true);
    }

    // Command format examples:
    // sendCommand takes a command string, sends to server, and returns response string. 

    static String sendCommand(String command) {
        try {
            out.println(command);
            String response = in.readLine();
            if (response == null) return "ERROR: Server disconnected.";
            return response.replace("\\n", "\n");
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
// addField to add label and text field to a panel 
    static JTextField addField(JPanel panel, String labelText) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 8, 2, 8));
        panel.add(lbl);

        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(380, 28));
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        return field;
    }
    // addBookHint just to add books descrption in fansy css 

    static void addBookHint(JPanel panel) {
        JLabel hint = new JLabel("<html>&nbsp;&nbsp;<b>Book IDs:</b> " +
            "1=Java &nbsp;2=Data Structures &nbsp;3=OS &nbsp;4=DBMS &nbsp;5=Networks<br>" +
            "&nbsp;&nbsp;6=Discrete Maths &nbsp;7=Software Engg &nbsp;8=AI &nbsp;9=Web Tech &nbsp;10=Python</html>");
        hint.setFont(new Font("Arial", Font.PLAIN, 11));
        hint.setForeground(new Color(80, 80, 80));
        hint.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(hint);
    }
  // styledButton to create also fansy design vibecoded
    static JButton styledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 36));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        return btn;
    }
}