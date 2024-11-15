import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class RegistrationForm extends JFrame {
    private JTextField nameField, mobileField;
    private JRadioButton maleRadio, femaleRadio;
    private JComboBox<String> dayBox, monthBox, yearBox;
    private JTextArea addressField;
    private JCheckBox termsBox;
    private JButton submitButton, resetButton;
    private JTable table;
    private DefaultTableModel tableModel;

    public RegistrationForm() {
        setTitle("Registration Form");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));


        nameField = new JTextField();
        mobileField = new JTextField();

        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);

        dayBox = new JComboBox<>();
        for (int i = 1; i <= 31; i++) {
            dayBox.addItem(String.valueOf(i));
        }

        monthBox = new JComboBox<>(new String[]{
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        });

        yearBox = new JComboBox<>();
        for (int i = 1990; i <= 2024; i++) {
            yearBox.addItem(String.valueOf(i));
        }

        addressField = new JTextArea();
        termsBox = new JCheckBox("Accept Terms and Conditions");

        submitButton = new JButton("Submit");
        resetButton = new JButton("Reset");


        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Mobile:"));
        formPanel.add(mobileField);
        formPanel.add(new JLabel("Gender:"));
        formPanel.add(maleRadio);
        formPanel.add(femaleRadio);
        formPanel.add(new JLabel("DOB:"));
        formPanel.add(dayBox);
        formPanel.add(monthBox);
        formPanel.add(yearBox);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(new JScrollPane(addressField));
        formPanel.add(termsBox);
        formPanel.add(submitButton);
        formPanel.add(resetButton);


        String[] columnNames = {"Name", "Mobile", "Gender", "DOB", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);


        add(formPanel, BorderLayout.WEST);
        add(tableScrollPane, BorderLayout.CENTER);


        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (termsBox.isSelected()) {
                    saveToDatabase();
                } else {
                    JOptionPane.showMessageDialog(null, "You must accept the terms and conditions.");
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });

        setVisible(true);
    }

    private void saveToDatabase() {
        String name = nameField.getText();
        String mobile = mobileField.getText();
        String gender = maleRadio.isSelected() ? "Male" : "Female";


        String day = (String) dayBox.getSelectedItem();
        String month = String.valueOf(monthBox.getSelectedIndex() + 1);
        String year = (String) yearBox.getSelectedItem();
        String dob = year + "-" + (month.length() == 1 ? "0" + month : month) + "-" + (day.length() == 1 ? "0" + day : day);

        String address = addressField.getText();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/registration_db", "root", "");
            String query = "INSERT INTO registrations (name, mobile, gender, dob, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, name);
            pst.setString(2, mobile);
            pst.setString(3, gender);
            pst.setString(4, dob);
            pst.setString(5, address);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
                addRowToTable(name, mobile, gender, dob, address); // Add data to table
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed.");
            }
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void addRowToTable(String name, String mobile, String gender, String dob, String address) {
        tableModel.addRow(new Object[]{name, mobile, gender, dob, address});
    }

    private void resetForm() {
        nameField.setText("");
        mobileField.setText("");
        maleRadio.setSelected(false);
        femaleRadio.setSelected(false);
        dayBox.setSelectedIndex(0);
        monthBox.setSelectedIndex(0);
        yearBox.setSelectedIndex(0);
        addressField.setText("");
        termsBox.setSelected(false);
    }

    public static void main(String[] args) {
        new RegistrationForm();
    }
}
