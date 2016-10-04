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

import org.json.*;

public class DiningScript {

	public static void main (String[] args) throws IOException {
		DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		Date d = new Date();
		String date = df.format(d);

		String url = "https://api.hfs.purdue.edu/menus/v2/locations/";
		JSONObject json = getJSON(url);
		JSONArray locations = json.getJSONArray("Location");

		for (int i = 0; i < locations.length(); i++) {
			JSONObject o = locations.getJSONObject(i);
			if (parseLocationData(o.get("Name").toString(), date)) {
				//System.out.print(o.get("Name").toString() + " successfully parsed!\n");
			} else {
				//System.out.print(o.get("Name").toString() + " not parsed.\n");
			}
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
			sb.append(columns.get(i) + "='" + values.get(i) + "'");
			if (i != columns.size()-1)	sb.append(", ");
		}
		
		//run against sql database
		String loc_update = "UPDATE Location"
				+ " SET " + sb.toString()
				+ " WHERE Name='" + loc + "';";
		System.out.println(loc_update);
	}
	
	/*
	 * Add data to location table for today's hours
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
			JSONArray stations = m.getJSONArray("Stations");
			for (int j = 0; j < stations.length(); j++) {
				JSONObject s = stations.getJSONObject(j);
				//System.out.println("Station: " + s.get("Name").toString());
				
				//get array of items at the station
				if (s.has("Items") && !s.isNull("Items")) {
					JSONArray items = s.getJSONArray("Items");
					for (int k = 0; k < items.length(); k++) {
						
					}
				}
			}
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

		return false;
	}

	/*
	 * Adds item to database
	 * return true if success
	 * return false if error
	 */
	public static boolean addItem(String id) {
		//https://api.hfs.purdue.edu/menus/v2/items/{item-id}/

		return true;
	}


}
