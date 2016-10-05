import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.*;
import java.sql.*;

class Item {
	String name;
	String id;
	String station;
	int breakfast;
	int lunch;
	int lateLunch;
	int dinner;

	Item(String n, String i, String s) {
		this.station = s;
		this.name = n;
		this.id = i;
		this.breakfast = 0;
		this.lunch = 0;
		this.dinner = 0;
	}

	void setBreakfast(boolean b) {
		this.breakfast = (b) ? 1 : 0;
	}

	void setLunch(boolean b) {
		this.lunch = (b) ? 1 : 0;
	}

	void setLLunch(boolean b) {
		this.lateLunch = (b) ? 1 : 0;
	}

	void setDinner(boolean b) {
		this.dinner = (b) ? 1 : 0;
	}

	public String toString(String loc) {
		return "INSERT INTO Daily (Location, Item_ID, Station, Breakfast, Lunch, Dinner, LateLunch) VALUES (\""
				+ loc + "\", \"" + id + "\", \"" + station + "\", \"" + breakfast + "\", \"" + lunch + "\", \""
				+ dinner + "\", \"" + lateLunch + "\");";
	}
}

public class DiningScript {

	static Connection conn;

	public static void main (String[] args) throws IOException {
		//open sql connection
		try {
			DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
			DateFormat time = new SimpleDateFormat("HH:mm:ss aaa");
			Date d = new Date();
			System.out.println("Script run at " + time.format(d));
			String date = df.format(d);
			
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/DINING", "root", "cz002");

			Statement stmt = conn.createStatement();
			System.out.println("DELETE FROM Daily;");
			stmt.execute("DELETE FROM Daily;");
			stmt.close();

			String url = "https://api.hfs.purdue.edu/menus/v2/locations/";
			JSONObject json = getJSON(url);
			JSONArray locations = json.getJSONArray("Location");

			for (int i = 0; i < locations.length(); i++) {
				JSONObject o = locations.getJSONObject(i);
				if (parseLocationData(o.get("Name").toString(), date)) {
					System.out.print(o.get("Name").toString() + " successfully parsed!\n");
				} else {
					System.out.print(o.get("Name").toString() + " not parsed.\n");
				}
			}		

			//close
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static JSONObject getJSON(String u) {
		JSONObject json = new JSONObject();
		try {
			URL url = new URL(u);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestProperty("Accept", "application/json");
			try {
				InputStream in = new BufferedInputStream(conn.getInputStream());
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				json = new JSONObject(sb.toString());			
			} finally {
				conn.disconnect();
			}
		}
		catch (IOException e) {
			System.err.println("Exception getting url " + u);
			e.printStackTrace();
		}
		return json;
	}

	public static void updateLocationTable(ArrayList<String> columns, ArrayList<String> values, String loc) {
		//make sure required meals are filled in
		String col = columns.toString();
		String val = values.toString();
		String[] required = {"Breakfast", "Lunch", "Dinner"};
		for (int i = 0; i < required.length; i++) {
			if (!col.contains(required[i])) {
				columns.add(required[i]);
				values.add("Closed");
			}
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < columns.size(); i++) {
			sb.append(columns.get(i) + "=\"" + values.get(i) + "\"");
			if (i != columns.size()-1)	sb.append(", ");
		}

		//run against sql database
		String loc_update = "UPDATE Location"
				+ " SET " + sb.toString()
				+ " WHERE Name=\"" + loc + "\";";

		try {
			Statement stmt = conn.createStatement();
			System.out.println(loc_update);
			stmt.executeUpdate(loc_update);
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static HashMap<String, Item> getItems(HashMap<String, Item> item_list, JSONArray stations, String meal) {
		for (int j = 0; j < stations.length(); j++) {
			JSONObject s = stations.getJSONObject(j);
			String station = s.getString("Name");
			if (station.equals("Potāto, Potäto"))	station = "Potato, Potäto";
			//System.out.println("Station: " + s.get("Name").toString());

			//get array of items at the station
			if (s.has("Items") && !s.isNull("Items")) {
				JSONArray items = s.getJSONArray("Items");
				for (int k = 0; k < items.length(); k++) {
					JSONObject item = items.getJSONObject(k);

					String item_name = item.getString("Name");
					Item food;
					if (item_list.containsKey(item_name)) { //item exists
						food = item_list.remove(item_name);
					} else {
						food = new Item(item_name, item.getString("ID"), station);
					}
					if (meal.equals("Breakfast"))		food.setBreakfast(true);
					else if (meal.equals("Lunch"))		food.setLunch(true);
					else if (meal.equals("LateLunch"))	food.setLLunch(true);
					else if (meal.equals("Dinner"))		food.setDinner(true);

					item_list.put(item_name, food);
				}
			}
		}
		return item_list;
	}

	/*
	 * Add data to location table for today\"s hours
	 * Add items to Daily table
	 * Check if item exists in Item table, if it does not - call addItem() function
	 * return true if successful, false otherwise
	 */
	public static boolean parseLocationData(String loc, String date) throws IOException {
		String url = "https://api.hfs.purdue.edu/menus/v2/locations/" + loc.replaceAll(" ", "%20") + "/" + date;
		JSONObject json = getJSON(url);
		JSONArray meals = json.getJSONArray("Meals");

		ArrayList<String> loc_columns = new ArrayList<String>();
		ArrayList<String> loc_values = new ArrayList<String>();

		HashMap<String, Item> item_list = new HashMap<String, Item>();

		for (int i = 0; i < meals.length(); i++) {
			JSONObject m = meals.getJSONObject(i);

			//Breakfast, Lunch, Late Lunch, Dinner
			String meal = m.get("Name").toString().replaceAll(" ", "");

			//get daily hours of operation
			String hours;
			if (m.isNull("Hours")) {
				hours = "Closed";
			} else {
				hours = m.getJSONObject("Hours").get("StartTime").toString() 
						+ "-"
						+ m.getJSONObject("Hours").get("EndTime").toString();
			}

			loc_columns.add(meal);
			loc_values.add(hours);

			//go through stations
			if (!m.has("Stations") || m.isNull("Stations"))	continue;
			item_list = getItems(item_list, m.getJSONArray("Stations"), meal); //get items to put into daily table
		}

		Statement stmt = null;
		for (String s : item_list.keySet()) {
			Item i = item_list.get(s);

			//check if item exists, if not - get item
			if (!checkItem(i.id)) {
				System.out.println("need to add " + i.name);
				addItem(i.id);
			}

			try { //insert into daily table
				stmt = conn.createStatement();
				System.out.println(i.toString(loc));
				stmt.execute(i.toString(loc));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {}
		}

		updateLocationTable(loc_columns, loc_values, loc);

		return true;
	}

	/*
	 * check if item exists in database
	 * return true if yes
	 * return false if no
	 */
	public static boolean checkItem(String id) {
		String query = "SELECT COUNT(*) FROM Item WHERE Item_ID=\"" + id + "\";";
		try {
			Statement stmt = conn.createStatement();
			System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				int count = rs.getInt("COUNT(*)");
				rs.close();
				stmt.close();
				if (count == 0)	return false;
				else			return true;
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * Adds item to database
	 * return true if success
	 * return false if error
	 */
	public static void addItem(String id) {
		//https://api.hfs.purdue.edu/menus/v2/items/{item-id}/
		String url = "https://api.hfs.purdue.edu/menus/v2/items/" + id + "/";
		JSONObject json = getJSON(url);

		String ingr;
		if (json.has("Ingredients")) 	ingr = json.getString("Ingredients").replace("\"", "\\\""); //escape quotes
		else							ingr = null;

		String item = "INSERT INTO Item (Item_ID, Name, Ingredients) VALUES ("
				+ "\"" + id + "\", \"" + json.getString("Name") + "\", \"" + ingr + "\");";
		StringBuilder col = new StringBuilder();
		StringBuilder val = new StringBuilder();
		col.append("Item_ID");
		val.append("\"" + id + "\"");

		try {
			Statement stmt = conn.createStatement();
			System.out.println(item);
			stmt.execute(item); //insert into Item to get primary key going

			//get allergens
			if (json.has("Allergens")) {
				JSONArray allergens = json.getJSONArray("Allergens");
				for (int i = 0; i < allergens.length(); i++) {
					JSONObject a = allergens.getJSONObject(i);
					String name = a.getString("Name").replaceAll(" ", "_");
					col.append(", " + name);

					if (a.getBoolean("Value")) 	val.append(", \"1\"");
					else						val.append(", \"0\"");
				}

				col.append(", Veg");
				if (json.getBoolean("IsVegetarian")) 	val.append(", 1");
				else									val.append(", 0");

				String allergen = "INSERT INTO Allergen (" + col.toString() + ") VALUES (" + val.toString() + ");";
				System.out.println(allergen);
				stmt.execute(allergen);
			}
			if (json.has("Nutrition")) {
				//get nutrition information
				JSONArray nutrition = json.getJSONArray("Nutrition");
				for (int i = 0; i < nutrition.length(); i++) {
					JSONObject n = nutrition.getJSONObject(i);

					String nu;
					if (n.getString("Name").equals("Serving Size")) {
						nu = "INSERT INTO Nutrition (Item_ID, Name, Value, Ordinal) VALUES (\""
								+ id + "\", \"" + n.getString("Name") + "\", \"" + n.getString("LabelValue") + "\", \""
								+ n.getInt("Ordinal") + "\");";
					} else {
						String value;
						if (n.has("Value")) value = Double.toString(n.getDouble("Value"));
						else				value = Integer.toString(n.getInt("LabelValue"));
						nu = "INSERT INTO Nutrition (Item_ID, Name, Value, Ordinal) VALUES (\""
								+ id + "\", \"" + n.getString("Name") + "\", \"" + value + "\", \""
								+ n.getInt("Ordinal") + "\");";
					}
					System.out.println(nu);
					stmt.execute(nu);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
