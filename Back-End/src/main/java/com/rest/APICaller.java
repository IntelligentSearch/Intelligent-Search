package com.rest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class APICaller {
	public static JSONObject getJSON(String u) throws JSONException {
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
	public static JSONArray apiCall(Parsed p) throws JSONException{
		JSONArray ja = new JSONArray();
		String url = "https://api.hfs.purdue.edu/menus/v2/locations/";
		JSONObject json = getJSON(url);
		JSONArray locations = json.getJSONArray("Location");
		for (int i = 0; i < locations.length(); i++) {
			JSONObject o = locations.getJSONObject(i);
			if(p.getCourt() == null || o.get("Name").toString().toLowerCase().equals(p.getCourt().toLowerCase())){
				parseLocationData(o.get("Name").toString(),ja,p);
			}
		}		
		return ja;
	}
/*	//same as apiCallItem 
	public static JSONArray apiCallItemAtTime(Parsed p) throws JSONException{
		JSONArray ja = new JSONArray();
		String url = "https://api.hfs.purdue.edu/menus/v2/locations/";
		JSONObject json = getJSON(url);
		JSONArray locations = json.getJSONArray("Location");
		for (int i = 0; i < locations.length(); i++) {
			JSONObject o = locations.getJSONObject(i);
			parseLocationData(o.get("Name").toString(),ja,p);
		}		
		return ja;
	}
	//same as apiCallItem 
	public static JSONArray apiCallAll(Parsed p) throws JSONException{
		JSONArray ja = new JSONArray();
		String url = "https://api.hfs.purdue.edu/menus/v2/locations/";
		JSONObject json = getJSON(url);
		JSONArray locations = json.getJSONArray("Location");
		for (int i = 0; i < locations.length(); i++) {
			JSONObject o = locations.getJSONObject(i);
			parseLocationData(o.get("Name").toString(),ja,p);
		}		
		return ja;
	}
	//same as apiCallItem 
	public static JSONArray apiCallAtTime(Parsed p) throws JSONException{
		JSONArray ja = new JSONArray();
		String url = "https://api.hfs.purdue.edu/menus/v2/locations/";
		JSONObject json = getJSON(url);
		JSONArray locations = json.getJSONArray("Location");
		for (int i = 0; i < locations.length(); i++) {
			JSONObject o = locations.getJSONObject(i);
			parseLocationData(o.get("Name").toString(),ja,p);
		}		
		return ja;
	}

	public static JSONArray apiCallLocation(Parsed p) throws JSONException{
		JSONArray ja = new JSONArray();
		String url = "https://api.hfs.purdue.edu/menus/v2/locations/";
		JSONObject json = getJSON(url);
		JSONArray locations = json.getJSONArray("Location");
		for (int i = 0; i < locations.length(); i++) {
			JSONObject o = locations.getJSONObject(i);
			if(o.get("Name").toString().toLowerCase().equals(p.getCourt().toLowerCase())){
				parseLocationData(o.get("Name").toString(),ja,p);
			}
		}		
		return ja;
	}
	public static JSONArray apiCallLocAtTime(Parsed p) throws JSONException{
		JSONArray ja = new JSONArray();
		String url = "https://api.hfs.purdue.edu/menus/v2/locations/";
		JSONObject json = getJSON(url);
		JSONArray locations = json.getJSONArray("Location");
		for (int i = 0; i < locations.length(); i++) {
			JSONObject o = locations.getJSONObject(i);
			if(o.get("Name").toString().toLowerCase().equals(p.getCourt().toLowerCase())){
				parseLocationData(o.get("Name").toString(),ja,p);
			}
		}		
		return ja;
	}
	public static JSONArray apiCallLocItem(Parsed p) throws JSONException{
		JSONArray ja = new JSONArray();
		String url = "https://api.hfs.purdue.edu/menus/v2/locations/";
		JSONObject json = getJSON(url);
		JSONArray locations = json.getJSONArray("Location");
		for (int i = 0; i < locations.length(); i++) {
			JSONObject o = locations.getJSONObject(i);
			if(o.get("Name").toString().toLowerCase().equals(p.getCourt().toLowerCase())){
				parseLocationData(o.get("Name").toString(),ja,p);
			}
		}		
		return ja;
	}
	public static JSONArray apiCallLocItemAtTime(Parsed p) throws JSONException{
		JSONArray ja = new JSONArray();
		String url = "https://api.hfs.purdue.edu/menus/v2/locations/";
		JSONObject json = getJSON(url);
		JSONArray locations = json.getJSONArray("Location");
		for (int i = 0; i < locations.length(); i++) {
			JSONObject o = locations.getJSONObject(i);
			if(o.get("Name").toString().toLowerCase().equals(p.getCourt().toLowerCase())){
				parseLocationData(o.get("Name").toString(),ja,p);
			}
		}		
		return ja;
	}
*/
	public static boolean parseLocationData(String loc,JSONArray ja,Parsed p) throws JSONException {
		String url = "https://api.hfs.purdue.edu/menus/v2/locations/" + loc.replaceAll(" ", "%20") + "/" + p.getDay();
		JSONObject json = getJSON(url);
		JSONArray meals = json.getJSONArray("Meals");
		HashMap<String, Item> item_list = new HashMap<String, Item>();

		for (int i = 0; i < meals.length(); i++) {
			JSONObject m = meals.getJSONObject(i);

			//Breakfast, Lunch, Late Lunch, Dinner
			String meal = m.get("Name").toString().replaceAll(" ", "");
			//go through stations
			if (!m.has("Stations") || m.isNull("Stations"))	continue;	
			item_list = getItems(item_list, m.getJSONArray("Stations"), meal,loc,p); //get items to put into daily table
		}
		for (String s : item_list.keySet()) {
			Item i = item_list.get(s);
			

			//check if item exists, if not - get item
			if (!checkItem(i.id)) {
				System.out.println("need to add " + i.name);
				addItem(i.id);
			}
			//TODO change this to creating a json object 
			i.setAllergens(Helper.getAllergens(i.id));
			Helper.getNutrition(i.id,i);
			
			JSONObject jo = i.processItem();
			if(jo != null && i.atTime(p.getTime())){
				ja.put(jo);
			}
		}
		return true;
	}
	public static HashMap<String, Item> getItems(HashMap<String, Item> item_list, JSONArray stations, String meal,String court,Parsed p) throws JSONException {
		boolean[] userPrefs = null;
		String getItem = p.getName();
		int userID = p.getUserID();
		if(getItem != null){
			getItem = getItem.toLowerCase();
		}
		if(userID > 0){
			userPrefs = Helper.getUsersPref(userID);
		}
		for (int j = 0; j < stations.length(); j++) {
			JSONObject s = stations.getJSONObject(j);
			String station = s.getString("Name");
			if (station.equals("Potāto, Potäto"))	station = "Potato, Potäto";
			//System.out.println("Station: " + s.get("Name").toString());

			//get array of items at the station
			if (s.has("Items") && !s.isNull("Items")) {
				System.out.println(getItem);
				JSONArray items = s.getJSONArray("Items");
				for (int k = 0; k < items.length(); k++) {
					JSONObject item = items.getJSONObject(k);

					String item_name = item.getString("Name");
					if(getItem != null && !item_name.toLowerCase().matches(".*"+getItem+".*")){
						System.out.println("DOESNT MATCH "+item_name);
						continue;
					}
					Item food;
					if (item_list.containsKey(item_name)) { //item exists
						food = item_list.remove(item_name);
					} else {
						food = new Item(item_name, item.getString("ID"), station,court,Helper.getIngred(item.getString("ID")),p.getDay(),userPrefs,p);
					}
					if (meal.equals("Breakfast")){
						food.setBreakfast(true);
					}
					else if (meal.equals("Lunch")){
						food.setLunch(true);
					}
					else if (meal.equals("LateLunch")){
						food.setLLunch(true);
					}
					else if (meal.equals("Dinner")){
						food.setDinner(true);
					}
					item_list.put(item_name, food);
				}
			}
		}
		return item_list;
	}
	public static boolean checkItem(String id) {
		String query = "SELECT COUNT(*) FROM Item WHERE Item_ID=\"" + id + "\";";
		try {
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/DINING", "root", "cz002");

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
	public static void addItem(String id) throws JSONException {
		System.out.println("id is "+id);
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
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/DINING", "root", "cz002");
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
