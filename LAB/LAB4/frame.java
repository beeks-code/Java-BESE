import java.awt.*;
import javax.swing.*;

public class frame {

    public static void main(String[] args) {


        JFrame frame = new JFrame("GridBag Layout");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel nameLabel = new JLabel("Name:");
        JLabel classLabel = new JLabel("Class:");
        JLabel rollLabel = new JLabel("Roll:");
        JLabel dobLabel = new JLabel("Date of Birth:");

        JTextField nameField = new JTextField(15);
        JTextField classField = new JTextField(15);
        JTextField rollField = new JTextField(15);
        JTextField dobField = new JTextField(15);

        JButton findResultButton = new JButton("Find Result");

        JLabel englishLabel = new JLabel("English:");
        JLabel nepaliLabel = new JLabel("Nepali:");
        JLabel mathsLabel = new JLabel("Maths:");

        JTextField englishField = new JTextField(15);
        JTextField nepaliField = new JTextField(15);
        JTextField mathsField = new JTextField(15);

        // gbc manages the position of components in the grid
        gbc.gridx = 0; gbc.gridy = 0;
        frame.add(nameLabel, gbc);
        gbc.gridx = 1;
        frame.add(nameField, gbc);

        // do like matrix, first set the row and column, then add the component, then move to next component
        gbc.gridx = 0; gbc.gridy = 1;
        frame.add(classLabel, gbc);
        gbc.gridx = 1;
        frame.add(classField, gbc);

        
        gbc.gridx = 0; gbc.gridy = 2;
        frame.add(rollLabel, gbc);
        gbc.gridx = 1;
        frame.add(rollField, gbc);

    
        gbc.gridx = 0; gbc.gridy = 3;
        frame.add(dobLabel, gbc);
        gbc.gridx = 1;
        frame.add(dobField, gbc);

        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        frame.add(findResultButton, gbc);
        gbc.gridwidth = 1;


        gbc.gridx = 0; gbc.gridy = 5;
        frame.add(englishLabel, gbc);
        gbc.gridx = 1;
        frame.add(englishField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        frame.add(nepaliLabel, gbc);
        gbc.gridx = 1;
        frame.add(nepaliField, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        frame.add(mathsLabel, gbc);
        gbc.gridx = 1;
        frame.add(mathsField, gbc);

        frame.setVisible(true);
    }
}