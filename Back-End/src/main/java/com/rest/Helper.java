package com.rest;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import org.json.*;

public class Helper {
				
		public static void getNutrition(String id, Item item) {
			Connection con = null;
			PreparedStatement prep_stmt = null;
			ResultSet res = null;
			try{
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query = "SELECT NAME, VALUE "
					+ 	"FROM Nutrition "
					+	"WHERE ITEM_ID = ?";
				prep_stmt = con.prepareStatement(query);
				prep_stmt.setString(1, id);
				res = prep_stmt.executeQuery();
				ArrayList<String> name = new ArrayList<String>();
				ArrayList<String> value = new ArrayList<String>();
				while(res.next()){
					name.add(res.getString("NAME"));
					value.add(res.getString("VALUE"));
				}
     			item.setNutrients(name,value);
			}
			catch (Exception e) {
				System.out.println("\n"+e.toString());
			}
			finally{
				try{
					if(con != null){
						con.close();
					}
					if(res != null){
						res.close();
					}
					if(prep_stmt != null){
						prep_stmt.close();
					}
				}	
				catch(Exception e){
				}
			}
		}
		public static boolean[] getAllergens(String id) {
			Connection con = null;
			PreparedStatement prep_stmt = null;
			ResultSet res = null;
			boolean[] pref = null;
			try{
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query = "SELECT EGGS,FISH,GLUTEN,MILK,PEANUTS,SHELLFISH,SOY,TREE_NUTS,WHEAT,VEG "
						+	"FROM Allergen "
						+	"WHERE ITEM_ID = ?";
				prep_stmt = con.prepareStatement(query);
				prep_stmt.setString(1, id);
				res = prep_stmt.executeQuery();
				if (res.next()) {
					boolean[] temp = { res.getBoolean("EGGS") , res.getBoolean("FISH"),
							res.getBoolean("GLUTEN"),res.getBoolean("MILK"),
							res.getBoolean("PEANUTS"),res.getBoolean("SHELLFISH"),
							res.getBoolean("SOY"),res.getBoolean("TREE_NUTS"),
							res.getBoolean("WHEAT"),res.getBoolean("VEG") };
					pref = temp;
				}
				else {
					//return "ERROR";
				}
			}
			catch (Exception e) {
				System.out.println("\n"+e.toString());
			}
			finally{
				try{
					if(con != null){
						con.close();
					}
					if(res != null){
						res.close();
					}
					if(prep_stmt != null){
						prep_stmt.close();
					}
				}	
				catch(Exception e){
				}
			}
			return pref;
		}
		public static String getIngred(String id){
			Connection con = null;
			PreparedStatement prep_stmt = null;
			ResultSet res = null;
			String ingred = null;
			try{
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query =  "SELECT Ingredients "
					+	"FROM Item "
					+	"WHERE ITEM_ID = ?";
				prep_stmt = con.prepareStatement(query);
				prep_stmt.setString(1, id);
				res = prep_stmt.executeQuery();
				if (res.next()) {
					ingred = res.getString("Ingredients");
				}
				else{
					//ERROR
				}
			}
			catch (Exception e) {
				System.out.println("\n"+e.toString());
			}
			finally{
				try{
					if(con != null){
						con.close();
					}
					if(res != null){
						res.close();
					}
					if(prep_stmt != null){
						prep_stmt.close();
					}
				}	
				catch(Exception e){
				}
			}
			return ingred;
		}

		public static ArrayList<String> getUserFavs(int userID) {
			Connection con = null;
			PreparedStatement prep_stmt = null;
			ResultSet res = null;
			ArrayList<String> names = new ArrayList<String>();
			try{
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query = "select Item.Name from Item inner join Favorites on Item.Item_ID = Favorites.Item_ID where Favorites.User_ID = ?";
				prep_stmt = con.prepareStatement(query);
				prep_stmt.setInt(1, userID);
				res = prep_stmt.executeQuery();
				while (res.next()) {
					String item = res.getString("Item.Name");	
					item = item.toLowerCase();
					names.add(item);
				}
			}
			catch (Exception e) {
				System.out.println("\n"+e.toString());
			}
			finally{
				try{
					if(con != null){
						con.close();
					}
					if(res != null){
						res.close();
					}
					if(prep_stmt != null){
						prep_stmt.close();
					}
				}	
				catch(Exception e){
				}
			}
			return names;	
		}
		public static boolean[] getUsersPref(int userID){
			Connection con = null;
			PreparedStatement prep_stmt = null;
			ResultSet res = null;
			boolean[] pref = null;
			try{
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query = "SELECT EGGS,FISH,GLUTEN,MILK,PEANUTS,SHELLFISH,SOY,TREE_NUTS,WHEAT,VEG "
						+	"FROM Preferences "
						+	"WHERE USER_ID = ?";
				prep_stmt = con.prepareStatement(query);
				prep_stmt.setInt(1, userID);
				res = prep_stmt.executeQuery();
				if (res.next()) {
					boolean[] temp = { res.getBoolean("EGGS") , res.getBoolean("FISH"),
							res.getBoolean("GLUTEN"),res.getBoolean("MILK"),
							res.getBoolean("PEANUTS"),res.getBoolean("SHELLFISH"),
							res.getBoolean("SOY"),res.getBoolean("TREE_NUTS"),
							res.getBoolean("WHEAT"),res.getBoolean("VEG") };
					pref = temp;
				}
				else {
					//return "ERROR";
				}
			}
			catch (Exception e) {
				System.out.println("\n"+e.toString());
			}
			finally{
				try{
					if(con != null){
						con.close();
					}
					if(res != null){
						res.close();
					}
					if(prep_stmt != null){
						prep_stmt.close();
					}
				}	
				catch(Exception e){
				}
			}
			return pref;
		}
		public static boolean matchPrefs(boolean[] user,boolean[] food){
			for(int i = 0; i<user.length;i++){
				if(user[i] && !food[i]){
					return false;
				}
			}
			return true;
		}
		public static void readItems(ResultSet res,JSONArray ja, int userID,Parsed p) throws SQLException, JSONException{
			boolean[] userPrefs = null; 
			if(userID > 0){
				userPrefs = Helper.getUsersPref(userID);
			}
			while (res.next()){
				String name = res.getString("I.NAME");
				//ja.put(new JSONObject().put("Name",name));
				//System.out.println(name);
				String diningCourt = res.getString("D.LOCATION");
				String station = res.getString("D.STATION");
				String  id = res.getString("I.Item_ID");
				boolean breakfast = res.getBoolean("D.BREAKFAST");
				boolean lunch = res.getBoolean("D.LUNCH");;
				boolean dinner = res.getBoolean("D.DINNER");
				boolean llunch = res.getBoolean("D.LateLunch");
				String ingred = res.getString("I.Ingredients");
				DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
				Date dateobj = new Date();
				Item i = new Item(name,id,station,diningCourt,ingred,df.format(dateobj),userPrefs,p);
				i.setBreakfast(breakfast);
				i.setLunch(lunch);
				i.setDinner(dinner);
				i.setLLunch(llunch);
				i.setAllergens(Helper.getAllergens(id));
				Helper.getNutrition(id,i);
				JSONObject jo = i.processItem();
				if(jo != null){
					ja.put(jo);
				}
				else{
					//ja.put(new JSONObject().put("object","eror object as it is null"));
				}
		}
		}
		public static String date(String date){
			Date dateGiven = null;
			try {
				dateGiven = new SimpleDateFormat("MM-dd-yyyy").parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "error";
			}
			Calendar c = Calendar.getInstance();
			c.setTime(dateGiven);
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			Calendar c2 = Calendar.getInstance();
			int actualDayOfWeek = c2.get(Calendar.DAY_OF_WEEK);
			if(dayOfWeek == actualDayOfWeek){
				return "Today";
			}
			else if((actualDayOfWeek+1) == (dayOfWeek)){
				return "Tomorrow";
			}
			else if( actualDayOfWeek == 7 && dayOfWeek == 1){
				return "Tomorrow";
			}
			else{
				SimpleDateFormat dateFormat = new SimpleDateFormat("EEEEE");
				//return ((actualDayOfWeek+1)%7)+" " +(dayOfWeek);
				return dateFormat.format(c.getTime());
			}
		}
}
