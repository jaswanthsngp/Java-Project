import java.sql.*;

// Run this file before Running Project.java
// command to run this file
// java -cp "path to jdbc connector" Database.java

class Main{
    public static void main(String[] args) {
        String[] data= {
            "'PB19DI8487', 'JH4NA1150RT000268', 'Renault', 'Kwid', 5",
            "'PB19DI8488', '1G8ZG127XWZ157259', 'Maruti', 'Alto k10', 5",
            "'PB19DI8489', '5N3ZA0NE6AN906847', 'Hyundai', 'Santro', 5",
            "'PB19DI8490', 'JH4DA1850JS005062', 'Bajaj', 'Qute', 4",
            "'PB19DI8491', 'SCA1S684X4UX07444', 'Datsun', 'Redi-GO', 5",
            "'PB19DI8492', 'JH4DB1570DIS000858', 'Maruti', 'Alto-800', 5",
            "'PB19DI8493', 'ZCFJS7458M1953433', 'Tata', 'Tiago', 5",
            "'PB19DI8494', '1J4FA29DI4YDI728937', 'Maruti', 'S-presso', 5",
            "'PB19DI8495', '1FVACYDT19HAJ2694', 'Maruti', 'Suzuki-eco', 7",
            "'PB19DI8496', '1G8MF35X68Y131819', 'Maruti', 'Suzuki-WagonR', 5",
            "'PB19DI8497', 'JF2SHADC3DG417185', 'Mahindra', 'XUV300 Turbosport', 5",
            "'PB19DI8498', 'JT3HJ85J6T0133046', 'Maruti', 'Suzuku Grand Vitara', 5",
            "'PB19DI8499', '3VWSB81H8WM210368', 'Mahindra', 'Bolero NEo', 7",
            "'PB19DI8500', 'JH4KA8162MC010197', 'Toyota', 'Urban Cruiser', 5",
            "'PB19DI8501', 'JH4CC2560RC008414', 'Mahindra', 'Scorpio Classic', 7",
            "'PB19DI8502', 'JG1MR215XJK752025', 'Hyundai', 'i20', 5",
            "'PB19DI8503', '2CNBJ13C3Y6924710', 'Tata', 'Nexon', 5",
            "'PB19DI8504', 'JH4KA3151KC019450', 'Hyundai', 'Verna', 5",
            "'PB19DI8505', 'JN8AZ2NE5C9016953', 'Skoda', 'KushaQ', 5",
            "'PB19DI8506', 'JH4KA8170NC000665', 'Toyota', 'Innova-Crysta', 7",
            "'PB19DI8513', '5FNRL18613B046732', 'Toyota', 'Landcruiser', 8"
        };
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection= DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/alltables",
                "root" ,"1234"
                // username, password
                );
            Statement statement = connection.createStatement();
            statement.executeUpdate("create table person(name varchar(30), age int, gender varchar(6), licence varchar(20) PRIMARY KEY, password varchar(20), contact bigint, emergencyContact bigint);");
            statement.executeUpdate("create table car(regNo varchar(10) PRIMARY KEY, chasisNo varchar(20), manufacturer varchar(20), model varchar(30), seats int);");
            statement.executeUpdate("create table rent(regNo varchar(10) PRIMARY KEY, plicence varchar(20), tookAt bigint);");
            statement.executeUpdate("ALTER TABLE rent ADD FOREIGN KEY(regNo) REFERENCES car(regNo);");
            statement.executeUpdate("ALTER TABLE rent ADD FOREIGN KEY(plicence) REFERENCES person(licence);");
            for(String s: data){
                statement.executeUpdate("insert into car values("+s+");");
            }
        }catch(Exception x){
            System.out.println(x);
        }
    }
}
