import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Date;

public class ParseTransit {
	public static Scanner scanner;
	public static Connection conn;
	public static void main(String[] args) {
		//Time Stamp
		DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		DateFormat time = new SimpleDateFormat("HH:mm:ss aaa");
		Date d = new Date();
		System.out.println("Script run at " + time.format(d));

		connectDB();
		clearTables();
		
		getRoutes();
		getCalendar();
		getShapes();
		getStops();
		getStopTimes();
		getTrips();

		System.out.println("Script successfully completed at " + time.format(d));
	}

	public static void getRoutes() {
		File file = new File("./google_transit/routes.txt");
		try {
            scanner = new Scanner(file);
			//get mapping for first line
			String first_line = processString(scanner.nextLine());
			HashMap<Integer, String> map = getMapping(first_line);

            while (scanner.hasNextLine()) { 
				//start reading data in
				String line = scanner.nextLine().replaceAll(",,",", ,"); //blank entry for double comma
                String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); //ignore commas in quotes

				//variables needed
				String id = "";
				String short_name = "";
				String long_name = "";	
				String desc = "";
				String type = "";
				String color = "";
				String text_color = "";

				//set variables
				for (int i = 0; i < data.length; i++) {
					//get map
					String col = map.get(i);
					if (col.equals("route_id")) {
						id = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("route_short_name")) {
						short_name = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("route_long_name")) {
						long_name = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("route_desc")) {
						desc = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("route_type")) { 
						type = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("route_color")) {
						color = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("route_text_color")) {
						text_color = checkString(data[i].replaceAll("\"",""));
					}	
				}
				//insert
				//System.out.println("INSERT INTO Routes for route_id " + id);
				Statement stmt = conn.createStatement();
				PreparedStatement ps = conn.prepareStatement("INSERT INTO Routes VALUES (?,?,?,?,?,?,?)");
				ps.setString(1, id);
				ps.setString(2, short_name);
				ps.setString(3, long_name);
				ps.setString(4, desc);
				ps.setString(5, type);
				ps.setString(6, color);
				ps.setString(7, text_color);
				ps.executeUpdate();
				
				ps.close();
				stmt.close();	
            }
			scanner.close();
			System.out.println("SUCCESS: Routes");
        } catch (FileNotFoundException e) {
			System.out.println("ERROR: Cannot find routes.txt file");
        } catch (SQLException e) {
			System.out.println("ERROR: sql exception for Routes");
			e.printStackTrace();
		}
	}
	
	public static void getShapes() {
		File file = new File("./google_transit/shapes.txt");
		try {
            scanner = new Scanner(file);
			//get mapping for first line
			String first_line = processString(scanner.nextLine());
			HashMap<Integer, String> map = getMapping(first_line);

            while (scanner.hasNextLine()) { 
				//start reading data in
				String line = scanner.nextLine().replaceAll(",,",", ,"); //blank entry for double comma
                String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); //ignore commas in quotes

				//variables needed
				String id = "";
				String lat = "";
				String lon = "";
				String seq = "";
				String dist = "";

				//set variables
				for (int i = 0; i < data.length; i++) {
					//get map
					String col = map.get(i);
					if (col.equals("shape_id")) {
						id = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("shape_pt_lat")) {
						lat = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("shape_pt_lon")) {
						lon = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("shape_pt_sequence")) {
						seq = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("shape_dist_traveled")) {
						dist = checkString(data[i].replaceAll("\"",""));
					}
				}
				//insert
				//System.out.println("INSERT INTO Shapes for shape_id " + id);
				Statement stmt = conn.createStatement();
				PreparedStatement ps = conn.prepareStatement("INSERT INTO Shapes VALUES (?,?,?,?,?)");
				ps.setString(1, id);
				ps.setString(2, lat);
				ps.setString(3, lon);
				ps.setString(4, seq);
				ps.setString(5, dist);
				ps.executeUpdate();
				
				ps.close();
				stmt.close();	
            }
			scanner.close();
			System.out.println("SUCCESS: Shapes");
        } catch (FileNotFoundException e) {
			System.out.println("ERROR: Cannot find Shapes.txt file");
        } catch (SQLException e) {
			System.out.println("ERROR: SQL exception for Shapes");
			e.printStackTrace();
		}
	}
	
	public static void getStops() {
		File file = new File("./google_transit/stops.txt");
		try {
            scanner = new Scanner(file);
			//get mapping for first line
			String first_line = processString(scanner.nextLine());
			HashMap<Integer, String> map = getMapping(first_line);

            while (scanner.hasNextLine()) { 
				//start reading data in
				String line = scanner.nextLine().replaceAll(",,",", ,"); //blank entry for double comma
                String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); //ignore commas in quotes

				//variables needed
				String id = "";
				String code = "";
				String name = "";
				String desc = "";
				String lat = "";
				String lon = "";
				String type = "";

				//set variables
				for (int i = 0; i < data.length; i++) {
					//get map
					String col = map.get(i);
					if (col.equals("stop_id")) {
						id = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("stop_code")) {
						code = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("stop_name")) {
						name = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("stop_desc")) {
						desc = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("stop_lat")) {
						lat = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("stop_lon")) {
						lon = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("location_type")) {
						type = checkString(data[i].replaceAll("\"",""));
					}
				}
				//insert
				//System.out.println("INSERT INTO Stops for stop_id " + id);
				Statement stmt = conn.createStatement();
				PreparedStatement ps = conn.prepareStatement("INSERT INTO Stops VALUES (?,?,?,?,?,?,?)");
				ps.setString(1, id);
				ps.setString(2, code);
				ps.setString(3, name);
				ps.setString(4, desc);
				ps.setString(5, lat);
				ps.setString(6, lon);
				ps.setString(7, type);
				ps.executeUpdate();
				
				ps.close();
				stmt.close();	
            }
			scanner.close();
			System.out.println("SUCCESS: Stops");
        } catch (FileNotFoundException e) {
			System.out.println("ERROR: Cannot find stops.txt file");
        } catch (SQLException e) {
			System.out.println("ERROR: SQL exception for Stops");
			e.printStackTrace();
		}
	}
	
	public static void getStopTimes() {
		File file = new File("./google_transit/stop_times.txt");
		try {
            scanner = new Scanner(file);
			//get mapping for first line
			String first_line = processString(scanner.nextLine());
			HashMap<Integer, String> map = getMapping(first_line);

            while (scanner.hasNextLine()) { 
				//start reading data in
				String line = scanner.nextLine().replaceAll(",,",", ,"); //blank entry for double comma
                String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); //ignore commas in quotes

				//variables needed
				String id = "";
				String arr = "";
				String dep = "";
				String stop = "";
				String seq = "";
				String pickup = "";
				String dropoff = "";
				String dist = "";

				//set variables
				for (int i = 0; i < data.length; i++) {
					//get map
					String col = map.get(i);
					if (col.equals("trip_id")) {
						id = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("arrival_time")) {
						arr = checkString(data[i].replaceAll("\"",""));	
					} else if (col.equals("departure_time")) {
						dep = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("stop_id")) {
						stop = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("stop_sequence")) {
						seq = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("pickup_type")) {
						pickup = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("drop_off_type")) {
						dropoff = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("shape_dist_traveled")) {
						dist = checkString(data[i].replaceAll("\"",""));
					}
				}
				//insert
				//System.out.println("INSERT INTO Stop_Times for trip_id " + id + " and arr value of " + arr);
				Statement stmt = conn.createStatement();
				PreparedStatement ps = conn.prepareStatement("INSERT INTO Stop_Times VALUES (?,?,?,?,?,?,?,?)");
				ps.setString(1, id);
				ps.setString(2, arr);
				ps.setString(3, dep);
				ps.setString(4, stop);
				ps.setString(5, seq);
				ps.setString(6, pickup);
				ps.setString(7, dropoff);
				ps.setString(8, dist);
				ps.executeUpdate();
				
				ps.close();
				stmt.close();	
            }
			scanner.close();
			System.out.println("SUCCESS: Stop_Times");
        } catch (FileNotFoundException e) {
			System.out.println("ERROR: Cannot find stop_times.txt file");
        } catch (SQLException e) {
			System.out.println("ERROR: SQL exception for Stop Times");
			e.printStackTrace();
		}
	}

	public static void getTrips() {
		File file = new File("./google_transit/trips.txt");
		try {
            scanner = new Scanner(file);
			//get mapping for first line
			String first_line = processString(scanner.nextLine());
			HashMap<Integer, String> map = getMapping(first_line);

            while (scanner.hasNextLine()) { 
				//start reading data in
				String line = scanner.nextLine().replaceAll(",,",", ,"); //blank entry for double comma
                String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); //ignore commas in quotes

				//variables needed
				String route = "";
				String service = "";
				String trip = "";
				String shape = "";
				String sign = "";	
				String dir = "";
				String block = "";

				//set variables
				for (int i = 0; i < data.length; i++) {
					//get map
					String col = map.get(i);
					if (col.equals("route_id")) {
						route = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("service_id")) {
						service = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("trip_id")) {
						trip = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("shape_id")) {
						shape = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("trip_headsign")) {
						sign = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("direction_id")) {
						dir = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("block_id")) {
						block = checkString(data[i].replaceAll("\"",""));
					}
				}
				//insert
				//System.out.println("INSERT INTO Trips for route " + id);
				Statement stmt = conn.createStatement();
				PreparedStatement ps = conn.prepareStatement("INSERT INTO Trips VALUES (?,?,?,?,?,?,?)");
				ps.setString(1, route);
				ps.setString(2, service);
				ps.setString(3, trip);
				ps.setString(4, shape);
				ps.setString(5, sign);
				ps.setString(6, dir);
				ps.setString(7, block);
				ps.executeUpdate();
				
				ps.close();
				stmt.close();	
            }
			scanner.close();
			System.out.println("SUCCESS: Trips");
        } catch (FileNotFoundException e) {
			System.out.println("ERROR: Cannot find trips.txt file");
        } catch (SQLException e) {
			System.out.println("ERROR: SQL exception for Trips");
			e.printStackTrace();
		}
	}
	public static void getCalendar() {
		File file = new File("./google_transit/calendar_dates.txt");
		try {
            scanner = new Scanner(file);
			//get mapping for first line
			String first_line = processString(scanner.nextLine());
			HashMap<Integer, String> map = getMapping(first_line);

            while (scanner.hasNextLine()) { 
				//start reading data in
				String line = scanner.nextLine().replaceAll(",,",", ,"); //blank entry for double comma
                String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); //ignore commas in quotes

				//variables needed
				String id = "";
				String date = "";

				//set variables
				for (int i = 0; i < data.length; i++) {
					//get map
					String col = map.get(i);
					if (col.equals("service_id")) {
						id = checkString(data[i].replaceAll("\"",""));
					} else if (col.equals("date")) {
						date = checkString(data[i].replaceAll("\"",""));
					}	
				}
				//insert
				//System.out.println("INSERT INTO Calendar for service_id " + id);
				Statement stmt = conn.createStatement();
				PreparedStatement ps = conn.prepareStatement("INSERT INTO Calendar VALUES (?,?)");
				ps.setString(1, id);
				ps.setString(2, date);
				ps.executeUpdate();
				
				ps.close();
				stmt.close();	
            }
			scanner.close();
			System.out.println("SUCCESS: Calendar");
        } catch (FileNotFoundException e) {
			System.out.println("ERROR: Cannot find calendar_dates.txt file");
        } catch (SQLException e) {
			System.out.println("ERROR: SQL exception for Calendars");
			e.printStackTrace();
		}
	}

	public static HashMap<Integer, String> getMapping(String line) {
		String[] values = line.split(",");
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		for (int i = 0; i < values.length; i++) {
			map.put(i, values[i]);	
		}
		return map;
	}

	public static String processString(String s) {
		//remove all quotes from string
		return s.replaceAll("\"","").replaceAll(",,", ", ,"); //remove all quotes and insert space between double quote
	}

	public static String checkString(String s) {
		if (s.equals(" ") || s == null)	return "";
		else							return s;
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
