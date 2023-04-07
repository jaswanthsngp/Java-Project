import java.util.*;
import java.sql.*;

// "C:\Program Files\Java\mysql-connector-j-8.0.32\mysql-connector-j-8.0.32.jar"

class Main{
    public static void main(String[] args) {
        Scanner sc= new Scanner(System.in);
        int costPerMinute= 1;   // in rupees, let's say
        System.out.println("Enter ");
        System.out.println("1 for admin rights");
        System.out.println("2 for customer rights");
        System.out.println("0 to exit");
        int c= sc.nextInt();
        if(c==0){
            System.out.println("Program Terminated");
            sc.close();
            return;
        }else{
            if(c==1){
                // admin login
                System.out.print("Enter Password: ");
                if(!"admin".equals(sc.next())){
                    System.out.println("Wrong Password");
                    sc.close();
                    return;
                }
                // admin's choice
                System.out.println("Enter");
                System.out.println("1 to add a new car");
                System.out.println("2 to remove a car");
                System.out.println("0 to exit");
                c= sc.nextInt();
                if(c==0){
                    System.out.println("Program Terminated");
                    sc.close();
                        return;
                }else if(c==1){
                    // add a new car
                    String regNo, chasisNo, manufacturer, model;
                    int seats;
                    // taking input for values to insert to table
                    System.out.println("Enter the Registration Number(without spaces)");
                    regNo= sc.next();
                    System.out.println("Enter the Chasis Number(without spaces)");
                    chasisNo= sc.next();
                    System.out.println("Enter the Manufacturer Company");
                    sc.nextLine();
                    manufacturer= sc.nextLine();
                    System.out.println("Enter the Model");
                    model= sc.nextLine();
                    System.out.println("Enter the Number of seats");
                    seats= sc.nextInt();
                    System.out.println(regNo+" "+chasisNo+" "+" "+manufacturer+" "+model+" "+seats);
                    SQLHandler.addToCars(regNo, chasisNo, manufacturer, model, seats);
                    // System.out.println("Press any key to see all the cars");
                    // sc.next();
                    // printTable(SQLHandler.showTable("car"));
                }else if(c==2){
                    // remove a car
                    System.out.println("You have the following cars");
                    printTable(SQLHandler.showTable("car"));
                    System.out.println("Enter the Registration Number(without spaces)");
                    String rm= sc.next();
                    SQLHandler.removeFromCars(rm);
                    // System.out.println("Press any key to see all the cars");
                    // sc.next();
                    // printTable(SQLHandler.showTable("car"));
                }else{
                    System.out.println("Invalid Input");
                }
            }else if(c==2){
                // customer login
                System.out.println("Enter");
                System.out.println("1 if Existing Customer (Login)");
                System.out.println("2 if New Customer (Signup)");
                c= sc.nextInt();
                String licence="";
                if(c==0){
                    System.out.println("Program Terminated");
                    sc.close();
                    return;
                }else if(c==1){
                    // login form
                    String pass;
                    System.out.println("Enter the Licence");
                    licence= sc.next();
                    System.out.println("Enter the Password");
                    pass= sc.next();
                    if(!SQLHandler.validUser(licence, pass)){
                        System.out.println("Wrong Password, please try again");
                        sc.close();
                        return;
                    }
                }else if(c==2){
                    // sign-up form
                    // name, age, gender, licence, contact no, emergency contact no, password
                    System.out.println("Enter Your Name");
                    sc.nextLine();
                    String name= sc.nextLine();
                    System.out.println("Enter your Age");
                    int age= sc.nextInt();
                    System.out.println("Enter your Gender(MALE/FEMALE)");
                    String gender= sc.next();
                    System.out.println("Enter your Licence");
                    licence= sc.next();
                    System.out.println("Enter your Contact Number");
                    long contact= sc.nextLong();
                    System.out.println("Enter your Emergency Contact Number");
                    long emergencyContact= sc.nextLong();
                    System.out.println("Enter your Password");
                    String pass= sc.next();
                    System.out.println("Re-Enter the Password");
                    String dupPass= sc.next();
                    if(pass.equals(dupPass)){
                        SQLHandler.signUp(name, age, gender, licence, contact, emergencyContact, pass);
                    }else{
                        System.out.println("Passwords didn't match, please try again");
                        sc.close();
                        return;
                    }
                }
                // book/relieve cars
                System.out.println("Enter");
                System.out.println("1 to book a car");
                System.out.println("2 to releive a car");
                System.out.println("0 to exit");
                System.out.println("The Rent Per minute is "+costPerMinute+" Rupees");
                c= sc.nextInt();
                if(c==0){
                    System.out.println("Program Terminated");
                    sc.close();
                    return;
                }else if(c==1){
                    // book a car
                    printTable(SQLHandler.showAvailiableCars());
                    System.out.println("Enter the Reg.No of car you want to rent");
                    String reg= sc.next();
                    SQLHandler.addToRent(reg, licence);
                }else if(c==2){
                    // relive a car
                    System.out.println("Enter the Registration number of car you want to relieve");
                    String reg= sc.next();
                    long bill= (long)costPerMinute * SQLHandler.releiveCar(reg, licence);
                    System.out.println("Bill you have to pay is "+bill);
                }else{
                    System.out.println("Invalid Input");
                }
            }else{
                System.out.println("Invalid Input");
            }
        }
        System.out.println("Thanks for using our service");
        sc.close();
    }
    static void printTable(ArrayList<String> table){
        int n= table.size();
        for(int i=0; i<n; i++){
            System.out.println(table.get(i));
        }
    }
}

class SQLHandler{
    // This handles all the DB side part, along with exceptions
    static Scanner sc= new Scanner(System.in);
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
    // to insert into the cars table by admin
    public static void addToCars(String regNo, String chasisNo, String manufacturer, String model, int seats){
        try{
            statement.executeUpdate("insert into car values('"+regNo+"','"+chasisNo+"','"+manufacturer+"','"+model+"',"+seats+");");
            System.out.println("Success, the car is inserted into the database");
        }catch(Exception ex){
            System.out.println(ex);
        }
    }
    // to remove from cars table by admin
    public static void removeFromCars(String regNo){
        try{
            // SQL to delete from table, parameter is taken in Main
            statement.executeUpdate("delete from car where regNo='"+regNo+"';");
            System.out.println("Success, the car is deleted from the database");
        }catch(Exception ex){
            System.out.println(ex);
        }
    }
    // to show the contents of any table
    public static ArrayList<String> showTable(String table){
        try{
            // fetch data
            ResultSet result = statement.executeQuery("select * from "+table+";");
            ResultSetMetaData rsmd= result.getMetaData();
            int n= rsmd.getColumnCount();
            ArrayList<String> tableData = new ArrayList<String>();
            // put it into arraylist
            while(result.next()){
                String s="";
                for(int i=1; i<=n; i++){
                    s+=result.getString(i)+" ";
                }
                tableData.add(s);
            }
            // return it
            return tableData;
        }catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    }
    public static ArrayList<String> showAvailiableCars(){
        try{
            // show the availiable cars to the customer
            ArrayList<String> availiableCars = new ArrayList<String>();
            ResultSet result = statement.executeQuery("select regno, model, seats from car where regno not in (select regNo from rent)");
            int n= result.getMetaData().getColumnCount();
            while(result.next()){
                String s="";
                for(int i=1; i<=n; i++){
                    s+=result.getString(i)+" ";
                }
                availiableCars.add(s);
            }
            return availiableCars;
        }catch(Exception x){
            System.out.println("Something went wrong, please try again");
            return null;
        }
    }
    public static void addToRent(String reg, String licence){
        try{
            statement.executeUpdate("insert into rent values('"+reg+"','"+licence+"',"+System.currentTimeMillis()/1000+");");
            System.out.println("Car successfully booked");
        }catch(SQLIntegrityConstraintViolationException icv){
            System.out.println("You might have entered wrong number, please enter it again");
        }catch(Exception x){
            System.out.println("Something wrong happened, please try again");
        }
    }
    public static long releiveCar(String reg, String licence){
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
            return time;
        }catch(SQLException sql){
            if(sql.getMessage()=="Before start of result set")
                System.out.println("No such car is booked, please check if it is a typo");
        }
        return 0;
    }
    public static boolean validUser(String licence, String pass){
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
        try{
            statement.executeUpdate("insert into person values('"+name+"',"+age+",'"+gender+"','"+licence+"','"+pass+"',"+contact+","+emergencyContact+");");
            System.out.println("Successfully SignedUp");
        }catch(SQLIntegrityConstraintViolationException sqli){
            System.out.println("The given licence already exists");
        }catch(SQLException sql){
            System.out.println("Some error occured, please try again");
        }
    }
}