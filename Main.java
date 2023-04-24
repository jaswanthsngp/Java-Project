import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;

// compile
// javac Main.java
// Run
// java -cp "mysql-connector-j-8.0.32/mysql-connector-j-8.0.32.jar" Main.java

// The Driver Code, Project Starts with Login Page
public class Main {
    public static void main(String[] args) {
        new LoginPage();
    }
}

class SQLHandler{
    // This handles all the DB side part, along with exceptions
    static Statement statement;
    static{
        try{
            // Connecting to the DB
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection= DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/alltables",
                "root" ,"1234"
                );
            statement = connection.createStatement();
        }catch(Exception x){
            System.out.println("Something Wrong Happened");
        }
    }

    public static boolean validateUser(String licence, String pass){
        // Validates the username and password, Return true if matched and false if not, used in Login form
        try{
            ResultSet result= statement.executeQuery("select password from person where licence='"+licence+"';");
            result.next();
            return pass.equals(result.getString("password"));
        }catch(SQLException sql){
            if(sql.getMessage()=="Before start of result set")
                System.out.println("User name doesnot exist, please check if it is a typo");
        }
        return false;
    }

    public static void signUp(String name, int age, String gender, String licence, long contact, long emergencyContact, String pass){
        // Add a new entry to the Customer Table, used in Signup form
        try{
            statement.executeUpdate("insert into person values('"+name+"',"+age+",'"+gender+"','"+licence+"','"+pass+"',"+contact+","+emergencyContact+");");
            System.out.println("Successfully SignedUp");
        }catch(SQLIntegrityConstraintViolationException sqli){
            System.out.println("The given licence already exists");
        }catch(SQLException sql){
            System.out.println("Some error occured, please try again");
        }
    }
    
    public static boolean addToCars(String regNo, String chasisNo, String manufacturer, String model, int seats){
        // to insert into the cars table by admin
        try{
            statement.executeUpdate("insert into car values('"+regNo+"','"+chasisNo+"','"+manufacturer+"','"+model+"',"+seats+");");
            System.out.println("Success, the car is inserted into the database");
            return true;
        }catch(Exception ex){
            System.out.println(ex);
        }
        return false;
    }
    
    public static boolean removeFromCars(String regNo){
        // to remove from cars table by admin
        try{
            // SQL to delete from table, parameter is taken in Admin Page
            statement.executeUpdate("delete from car where regNo='"+regNo+"';");
            System.out.println("Success, the car is deleted from the database");
            return true;
        }catch(Exception ex){
            System.out.println(ex);
        }
        return false;
    }
    
    public static String[][] showTable(String table){
        // to show the contents of any table, used to show in Admin Page
        try{
            // fetch data
            ResultSet result= statement.executeQuery("select count(regNo) from "+table+";");
            result.next();
            int n= result.getInt(1);
            result = statement.executeQuery("select * from "+table+";");
            ResultSetMetaData rsmd= result.getMetaData();
            int m= rsmd.getColumnCount();
            String[][] tableData = new String[n][m];
            // put it into arraylist
            int i=0;
            while(result.next()){
                String s[]= new String[m];
                for(int j=1; j<=m; j++)
                    s[j-1]= result.getString(j);
                tableData[i]= s;
                i++;
            }
            // return it
            return tableData;
        }catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    }

    public static String[] showAvailiableCars(){
        // user to show the cars that no one booked. Used in Booking Frame of Customer Page
        try{
            // show the availiable cars to the customer
            ResultSet result= statement.executeQuery("select count(regno) from car where regno not in (select regNo from rent)");
            result.next();
            int n= result.getInt(1);
            result = statement.executeQuery("select regno, model, seats from car where regno not in (select regNo from rent)");
            int m= result.getMetaData().getColumnCount();
            String[] availiableCars = new String[n];
            int i=0;
            while(result.next()){
                String s="";
                for(int j=1; j<=m; j++)
                    s+=result.getString(j)+" ";
                availiableCars[i]= s+"seater";
                i++;
            }
            return availiableCars;
        }catch(Exception x){
            System.out.println("Something went wrong, please try again");
            return null;
        }
    }

    public static String[] showBookedCars(String lic){
        // Shows the cars booked by particular customer. Used in Release Car page of customer
        try{
            // show the availiable cars to the customer
            ResultSet result= statement.executeQuery("select count(regno) from car where regno in (select regNo from rent)");
            result.next();
            int n= result.getInt(1);
            result = statement.executeQuery("select regno, model, seats from car where regno in (select regNo from rent where plicence='"+lic+"')");
            int m= result.getMetaData().getColumnCount();
            String[] bookedCars = new String[n];
            int i=0;
            while(result.next()){
                String s="";
                for(int j=1; j<=m; j++)
                    s+= result.getString(j)+" ";
                bookedCars[i]=s;
                i++;
            }
            return bookedCars;
        }catch(Exception x){
            System.out.println("Something went wrong, please try again");
            return null;
        }
    }

    public static void bookCar(String reg, String licence){
        // Function that adds Customer Booking Entry to database, used in Booking Frame
        try{
            statement.executeUpdate("insert into rent values('"+reg+"','"+licence+"',"+System.currentTimeMillis()/1000+");");
            System.out.println("Car successfully booked");
        }catch(SQLIntegrityConstraintViolationException icv){
            System.out.println("You might have entered wrong number, please enter it again");
        }catch(Exception x){
            System.out.println("Something wrong happened, please try again");
        }
    }

    public static int releaseCar(String reg, String licence){
        // Function that removes Booking Entry and gives bill to the customer
        try{
            // fetch the time for which it is taken
            ResultSet result= statement.executeQuery("select * from rent where regNo='"+reg+"';");
            result.next();
            // check if the car is booked by the same person
            String lic= result.getString("plicence");
            if(!licence.equals(lic)){
                System.out.println("Looks like you didn't book the car");
                return 0;
            }
            // calculate the time in minutes
            long time= (System.currentTimeMillis()/1000 - result.getLong("tookAt"))/60;
            // remove it from the rent DB
            statement.executeUpdate("delete from rent where regNo='"+reg+"';");
            System.out.println("The car "+reg+" has been successfully relieved");
            return (int)time;
        }catch(SQLException sql){
            if(sql.getMessage()=="Before start of result set")
                System.out.println("No such car is booked, please check if it is a typo");
        }
        return 0;
    }
}

class LoginPage extends JFrame implements ActionListener {
    // The Login Window, first one to appear

    // Components used
    private JLabel heading, userNameLabel, passwordLabel;
    private JTextField userNameField;
    private JPasswordField passwordField;
    private JButton submitButton, signupButton;

    public LoginPage(){
        // Window arrangement
        setTitle("Car Rental System with Java");
        setBounds(300, 100, 600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);


        // Component Description and Adding
        heading = new JLabel("Login");
        heading.setFont(new Font("Serif", Font.BOLD, 24));
        heading.setBounds(250, 30, 100, 30);
        add(heading);

        userNameLabel = new JLabel("Licence:");
        userNameLabel.setBounds(100, 100, 100, 30);
        add(userNameLabel);
        
        userNameField = new JTextField();
        userNameField.setBounds(220, 100, 250, 30);
        add(userNameField);

        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(100, 150, 100, 30);
        add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(220, 150, 250, 30);
        add(passwordField);

        submitButton = new JButton("Login");
        submitButton.setBounds(200, 380, 100, 30);
        add(submitButton);
        submitButton.addActionListener(this);

        signupButton = new JButton("Sign Up");
        signupButton.setBounds(320, 380, 100, 30);
        add(signupButton);
        signupButton.addActionListener(this);

        // Making the Window Visible
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource()==submitButton){
            // if submitted, read data from form
            String userName= userNameField.getText();
            String password= new String(passwordField.getPassword());
            if(SQLHandler.validateUser(userName,password)){
                // Validate the details from DB
                if(userName.equals("admin")){
                    // Go to Admin Login if admin
                    System.out.println("Admin Login Success");
                    setVisible(false);
                    new AdminWindow();
                }else{
                    // Customer Login if not
                    System.out.println("User login success");
                    setVisible(false);
                    new CustomerWindow(userName);
                }
            }
            else {
                // if Validation is not success, prompt user to enter correct details
                JOptionPane.showMessageDialog(this, "Wrong Password, please try again");
            }
        }else if(e.getSource()==signupButton){
            // If the user wants to signup, redirect him to signup page
            setVisible(false);
            new SignupPage();
        }else{
            userNameField.setText("");
            passwordField.setText("");
        }
    }
    public static void main(String[] args) {
        new LoginPage();
    }
}

class SignupPage extends JFrame implements ActionListener {
    // This page contains the signup form

    // Components used
    private JLabel headingLabel, nameLabel, ageLabel, genderLabel, licenceLabel, passwordLabel, confirmPasswordLabel, phoneLabel, emergencyPhoneLabel;
    private JTextField nameField, ageField, licenceField, phoneField, emergencyPhoneField;
    private JRadioButton maleButton, femaleButton;
    private ButtonGroup gender;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton signupButton, resetButton;
    
    public SignupPage() {
        // Window Initialisation
        setTitle("Car Rental System in Java");
        setBounds(300, 100, 600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        
        // Initialising and placing the components
        headingLabel = new JLabel("Sign Up");
        headingLabel.setFont(new Font("Serif", Font.BOLD, 24));
        headingLabel.setBounds(250, 30, 100, 30);
        add(headingLabel);
        
        nameLabel = new JLabel("Name:");
        nameLabel.setBounds(100, 100, 100, 30);
        add(nameLabel);
        
        nameField = new JTextField();
        nameField.setBounds(220, 100, 250, 30);
        add(nameField);

        ageLabel = new JLabel("Age:");
        ageLabel.setBounds(100, 150, 100, 30);
        add(ageLabel);

        ageField = new JTextField();
        ageField.setBounds(220, 150, 250, 30);
        add(ageField);

        genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(100, 200, 100, 30);
        add(genderLabel);

        maleButton= new JRadioButton();
        maleButton.setText("Male");
        maleButton.setBounds(220, 200, 100, 30);
        add(maleButton);

        femaleButton= new JRadioButton();
        femaleButton.setText("Female");
        femaleButton.setBounds(330, 200, 100, 30);
        add(femaleButton);

        gender= new ButtonGroup();
        gender.add(maleButton);
        gender.add(femaleButton);
        
        licenceLabel = new JLabel("Licence:");
        licenceLabel.setBounds(100, 250, 100, 30);
        add(licenceLabel);
        
        licenceField = new JTextField();
        licenceField.setBounds(220, 250, 250, 30);
        add(licenceField);
        
        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(100, 300, 100, 30);
        add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(220, 300, 250, 30);
        add(passwordField);
        
        confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setBounds(100, 350, 150, 30);
        add(confirmPasswordLabel);
        
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(220, 350, 250, 30);
        add(confirmPasswordField);
        
        phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setBounds(100, 400, 250, 30);
        add(phoneLabel);
        
        phoneField = new JTextField();
        phoneField.setBounds(220, 400, 250, 30);
        add(phoneField);

        emergencyPhoneLabel = new JLabel("Phone Number:");
        emergencyPhoneLabel.setBounds(100, 450, 250, 30);
        add(emergencyPhoneLabel);
        
        emergencyPhoneField = new JTextField();
        emergencyPhoneField.setBounds(220, 450, 250, 30);
        add(emergencyPhoneField);
        
        signupButton = new JButton("Sign Up");
        signupButton.setBounds(200, 500, 100, 30);
        add(signupButton);
        signupButton.addActionListener(this);
        
        resetButton = new JButton("Reset");
        resetButton.setBounds(320, 500, 100, 30);
        add(resetButton);
        resetButton.addActionListener(this);
        
        // Making page visible
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == signupButton) {
            // Get values from the form
            String name = nameField.getText();
            String age= ageField.getText();
            String gender="";
            String licence = licenceField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String phone = phoneField.getText();
            String emergencyPhone= emergencyPhoneField.getText();
            // confirm that Radio Button is selected
            if(maleButton.isSelected())
                gender= "male";
            else if(femaleButton.isSelected())
                gender= "female";
            else
                JOptionPane.showMessageDialog(this, "Please fill all the fields.");
            // confirm that all fields are filled
            if(name.isEmpty() || age.isEmpty() || licence.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty() || emergencyPhone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all the fields.");
            } else if(!password.equals(confirmPassword)) {  // Validate password
                JOptionPane.showMessageDialog(this, "Passwords do not match.");
            } else {
                // If everything is correct, add entry to the database
                try{
                    int ageInt= Integer.parseInt(age);
                    long mobile= Long.parseLong(phone);
                    long emergencyMobile= Long.parseLong(emergencyPhone);
                    SQLHandler.signUp(name, ageInt, gender, licence, mobile, emergencyMobile, password);
                    JOptionPane.showMessageDialog(this, "Sign up successful.");
                    setVisible(false);
                    // and redirect to Login Form
                    new LoginPage();
                }catch(NumberFormatException nfe){
                    // Inform the user to enter valid data in case invalid
                    JOptionPane.showMessageDialog(this, "Check whether Age, Phone and Emergency Phone are numbers only");
                }catch(Exception x){
                    System.out.println(x.getMessage());
                }
            }
        } else if(e.getSource() == resetButton) {
            nameField.setText("");
            ageField.setText("");
            maleButton.setSelected(false);
            femaleButton.setSelected(false);
            licenceField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
            phoneField.setText("");
            emergencyPhoneField.setText("");
        }
    }
    public static void main(String args[]){
        new SignupPage();
    }
}

class AdminWindow extends JFrame implements ActionListener {
    // This is only accessible to Admin, he uses it to add new cars or remove older ones

    // Components used
    private JButton addButton;
    private JButton removeButton;
    private JTextField registrationNumberField;
    private JTextField chasisNumberField;
    private JTextField manufacturerField;
    private JTextField modelNumberField;
    private JTextField numberOfSeatsField;
    private JTable table;
    private DefaultTableModel tableModel;

    public AdminWindow() {
        super("Admin Window | Car Rental System With Java");

        // components initialisation
        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
        registrationNumberField = new JTextField(10);
        chasisNumberField = new JTextField(10);
        manufacturerField = new JTextField(10);
        modelNumberField = new JTextField(10);
        numberOfSeatsField = new JTextField(10);

        // setting up table
        String[] columnNames = {"Registration Number", "Chasis Number", "Manufacturer", "Model Number", "Number of Seats"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table = new JTable(SQLHandler.showTable("car"), columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        // layout
        setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        inputPanel.add(new JLabel("Registration Number:"));
        inputPanel.add(registrationNumberField);
        inputPanel.add(new JLabel("Chasis Number:"));
        inputPanel.add(chasisNumberField);
        inputPanel.add(new JLabel("Manufacturer:"));
        inputPanel.add(manufacturerField);
        inputPanel.add(new JLabel("Model Number:"));
        inputPanel.add(modelNumberField);
        inputPanel.add(new JLabel("Number of Seats:"));
        inputPanel.add(numberOfSeatsField);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        
        addButton.addActionListener(this);
        removeButton.addActionListener(this);

        // window properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            // Adding row to the table displayed, called on pressing add button
            // Read Data
            String[] rowData = {
                    registrationNumberField.getText(),
                    chasisNumberField.getText(),
                    manufacturerField.getText(),
                    modelNumberField.getText(),
            };
            try{
                // Parse it
                int seats= Integer.parseInt(numberOfSeatsField.getText());
                // Then add entry to table
                SQLHandler.addToCars(rowData[0], rowData[1], rowData[2], rowData[3], seats);
                tableModel.addRow(rowData);
                JOptionPane.showMessageDialog(this, "Successfully Added");
                System.exit(0);
            }catch(NumberFormatException nfe){
                JOptionPane.showMessageDialog(this, "Please enter number of seats as a Number");
            }

            // Clear input fields
            registrationNumberField.setText("");
            chasisNumberField.setText("");
            manufacturerField.setText("");
            modelNumberField.setText("");
            numberOfSeatsField.setText("");
        } else if (e.getSource() == removeButton) {
            // Removing selected row from table, called on pressing Remove button
            // Read the row number
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                // Confirm that a row is selected and get the Reg. No from that row
                String regNo= table.getValueAt(selectedRow, 0).toString();
                if(SQLHandler.removeFromCars(regNo)){
                    // if success, tell admin and exit
                    JOptionPane.showMessageDialog(this, "Seccessfully Removed");
                    System.exit(0);
                }else{
                    // if not, tell him to try again
                    JOptionPane.showMessageDialog(this, "Looks like it is booked by some customer, please try later");
                }
            }
        }
    }
    public static void main(String[] args) {
        new AdminWindow();
    }
}

class CustomerWindow extends JFrame {
    // This contains the fns provided to the customer

    public CustomerWindow(String lic) {
        setTitle("Booking Page | Car Rental System in Java");
        setSize(800, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // two framesets
        BookingFrame bookingFrame = new BookingFrame(lic);
        ReleaseFrame releaseFrame = new ReleaseFrame(lic);

        //to hold the framesets
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, bookingFrame, releaseFrame);
        splitPane.setResizeWeight(0.5);
        getContentPane().add(splitPane);

        setVisible(true);
    }
    public static void main(String[] args) {
        new CustomerWindow("abcdef");
    }
}

class BookingFrame extends JPanel {
    // This fraame handles the process to book a car

    // Components used
    private JLabel label;
    private JComboBox<String> cabTypes;
    private JButton submitButton;
    private JButton resetButton;

    public BookingFrame(String lic) {
        // Initialise Frame
        setBorder(BorderFactory.createTitledBorder("Book a Car | Car Rental System in Java"));
        setLayout(new GridLayout(3, 1));


        // Initialise and add components
        label = new JLabel("Select a cab number");
        add(label);

        // Get the cars availiable to the customer from DB
        String[] cabOptions = SQLHandler.showAvailiableCars(); 
        cabTypes = new JComboBox<String>(cabOptions);
        add(cabTypes)
        ;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

        submitButton = new JButton("Submit");
        buttonPanel.add(submitButton);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // When submitted, get the reg.no. from selection
                String selectedCab = cabTypes.getSelectedItem().toString().substring(0, 10).trim();
                System.out.println("Cab type selected: " + selectedCab);
                // Add enntry to the database
                SQLHandler.bookCar(selectedCab, lic);
                JOptionPane.showMessageDialog(buttonPanel, "Car "+selectedCab+" successfully Booked");
                System.exit(0);
            }
        });

        resetButton = new JButton("Reset");
        buttonPanel.add(resetButton);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reset button action
                cabTypes.setSelectedIndex(0);
            }
        });

        add(buttonPanel);
    }
}

class ReleaseFrame extends JPanel {
    // This frame takes care of GUI relate to release of car

    private JLabel label;
    private JComboBox<String> cabNumbers;
    private JButton submitButton;
    private JButton resetButton;

    public ReleaseFrame(String lic) {
        // Initialise Frame
        setBorder(BorderFactory.createTitledBorder("Releasing a Car | Car Rental System in Java"));
        setLayout(new GridLayout(3, 1));

        label = new JLabel("Select a cab number:");
        add(label);

        // Get the cars booked by the customer from DB
        String[] cabOptions = SQLHandler.showBookedCars(lic);
        System.out.println(cabOptions==null);
        for(String x: cabOptions)
            System.out.println(x);
        cabNumbers = new JComboBox<String>(cabOptions);
        add(cabNumbers);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

        submitButton = new JButton("Submit");
        buttonPanel.add(submitButton);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // When submitted, add get the registration number from Selected Component
                String selectedCab = cabNumbers.getSelectedItem().toString().substring(0, 9).trim();
                // Remove entry from the DB and calculate the bill
                System.out.println("Cab number selected: " + selectedCab);
                int bill= SQLHandler.releaseCar(selectedCab, lic);
                // Show the bill to customer and exit
                JOptionPane.showMessageDialog(buttonPanel, "Car "+selectedCab+" successfully released, the bill is "+bill);
                System.exit(0);
            }
        });

        resetButton = new JButton("Reset");
        buttonPanel.add(resetButton);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reset button action
                cabNumbers.setSelectedIndex(0);
            }
        });

        add(buttonPanel);
    }
}