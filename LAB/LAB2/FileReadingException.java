import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileReadingException {

    public static List<String> readFile(String filePath) throws FileNotFoundException {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + e.getMessage());
        }
        return lines;
    }

    public static void processFile(String filePath) {
        System.out.println("Attempting to read: " + filePath);
        try {
            List<String> lines = readFile(filePath);
            System.out.printf("Successfully read %d line(s):%n", lines.size());
            for (int i = 0; i < lines.size(); i++) {
                System.out.printf("  Line %d: %s%n", i + 1, lines.get(i));
            }
        } catch (FileNotFoundException e) {
            System.out.println("  [FileNotFoundException] " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("  [RuntimeException] " + e.getMessage());
        } finally {
            System.out.println("  [finally] File operation attempt complete.\n");
        }
    }

    public static void main(String[] args) {

        String sampleFile = "sample_data.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(sampleFile))) {
            writer.println("Java Exception Handling");
            writer.println("Line 2: FileNotFoundException Demo");
            writer.println("Line 3: Always use try-catch-finally");
        } catch (IOException e) {
            System.out.println("Could not create sample file: " + e.getMessage());
        }

        processFile(sampleFile);

 
        processFile("nonexistent_file.txt");

        // Test 3: Path that's a directory
        processFile(".");

        // Cleanup
        new File(sampleFile).delete();
    }
}
