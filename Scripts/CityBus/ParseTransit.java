import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.sql.*;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.Statement;
//import java.sql.SQLException;
import java.net.*;
import java.util.*;

public class ParseTransit {
	public static Scanner scanner;
	public static Connection conn;
	public static void main(String[] args) {
		connectDB();
		clearTables();
		
		//getRoutes();
	}

	public static void getRoutes() {
		File file = new File("./google_transit/routes.txt");
		try {
            Scanner scanner = new Scanner(file);
			//skip first line
			scanner.nextLine();
            while (scanner.hasNextLine()) { 
				//start reading data in
                String[] data = scanner.nextLine().split("\"");
            }
        } catch (FileNotFoundException e) {
			System.out.println("ERROR: Cannot find routes.txt file");
        }
	}

	public static String parseString(String s) {
		//remove all quotes from string
		return s.replaceAll("\"","");
	}
	
	public static void connectDB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/CITYBUS", "root", "cz002");	
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Class driver not found");
			System.exit(1);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Cannot connect to DB");
			System.exit(1);
		}
	}

	//delete table data - Calendar, Routes, Shapes, Stop_Times, Stops, Trips
	public static void clearTables() {
		try {
			Statement stmt = conn.createStatement();

			System.out.println("DELETE FROM Trips;");
			stmt.execute("DELETE FROM Trips;");

			System.out.println("DELETE FROM Stop_Times;");
			stmt.execute("DELETE FROM Stop_Times;");

			System.out.println("DELETE FROM Routes;");
			stmt.execute("DELETE FROM Routes;");
		
			System.out.println("DELETE FROM Stops;");
			stmt.execute("DELETE FROM Stops;");

			System.out.println("DELETE FROM Calendar;");
			stmt.execute("DELETE FROM Calendar;");
		
			System.out.println("DELETE FROM Shapes;");
			stmt.execute("DELETE FROM Shapes;");

			stmt.close(); 
		} catch (SQLException e) {
			System.out.println("SQL exception when clearing tables");
			System.exit(1);
		}
	}
}
