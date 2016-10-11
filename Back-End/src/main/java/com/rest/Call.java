package com.rest;

import java.sql.*;
import org.json.*;

public class Call {
		public static JSONArray getFoodDining(String diningCourt){
			JSONArray ja = new JSONArray();
			try {
				diningCourt = "Wiley";
				Class.forName("com.mysql.jdbc.Driver");
				Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query = "SELECT D.ITEM_ID, D.STATION,D.BREAKFAST,D.LUNCH,D.DINNER,I.NAME,A.EGGS,A.FISH,A.GLUTEN,A.MILK,A.PEANUTS,A.Shellfish,A.SOY,A.Tree_Nuts,A.WHEAT,A.VEG "
						+	"FROM Daily AS D "
						+	"INNER JOIN Item AS I "
						+	"ON D.ITEM_ID = I.ITEM_ID "
						+	"INNER JOIN Allergen AS A "
						+	"ON D.ITEM_ID = A.ITEM_ID "
						+	"WHERE D.LOCATION = ? ";
				PreparedStatement prep_stmt = conn.prepareStatement(query);
				prep_stmt.setString(1, diningCourt);
         		ResultSet res = prep_stmt.executeQuery();
        		while (res.next()) {
        			String name = res.getString("I.NAME");
					String station = res.getString("D.STATION");
					String  id = res.getString("D.Item_ID");
					boolean breakfast = res.getBoolean("D.BREAKFAST");
					boolean lunch = res.getBoolean("D.LUNCH");;
					boolean dinner = res.getBoolean("D.DINNER");
        				boolean pref[] = { res.getBoolean("A.EGGS") , res.getBoolean("A.FISH"),
        					res.getBoolean("A.GLUTEN"),res.getBoolean("A.MILK"),
        					res.getBoolean("A.PEANUTS"),res.getBoolean("A.SHELLFISH"),
        					res.getBoolean("A.SOY"),res.getBoolean("A.TREE_NUTS"),
        					res.getBoolean("A.WHEAT"),res.getBoolean("A.VEG") };
        			Item i = new Item(name,id,station,diningCourt);
        			i.setBreakfast(breakfast);
        			i.setLunch(lunch);
        			i.setDinner(dinner);
        			i.setAllergens(pref);
        			Helper.getNutrition(id,i);
        			ja.put(i.processItem());
        		}
				res.close();
         		prep_stmt.close();
				conn.close();
			} catch (Exception e) {
				System.out.println("\n"+e.toString());
			}
			return ja;			
		}
		public static JSONArray getItemDin(String diningCourt, String item) {
			JSONArray ja = new JSONArray();
			PreparedStatement prep_stmt;
			ResultSet res;
			//sets up connection
			Connection conn;
			try {
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query = "SELECT I.Item_ID, I.NAME,D.STATION,D.BREAKFAST,D.LUNCH,D.DINNER "
					+	"FROM Daily AS D "
					+	"JOIN Item AS I "
					+	"ON D.ITEM_ID = I.Item_ID "
					+	"WHERE I.NAME = ? AND D.LOCATION = ?";
				prep_stmt = conn.prepareStatement(query);
				prep_stmt.setString(1, item);
				prep_stmt.setString(2,diningCourt);
				res = prep_stmt.executeQuery();
				if (res.next()){
					String name = res.getString("I.NAME");
					String station = res.getString("D.STATION");
					String  id = res.getString("I.Item_ID");
					boolean breakfast = res.getBoolean("D.BREAKFAST");
					boolean lunch = res.getBoolean("D.LUNCH");;
					boolean dinner = res.getBoolean("D.DINNER");
					Item i = new Item(name,id,station,diningCourt);
        				i.setBreakfast(breakfast);
        				i.setLunch(lunch);
        				i.setDinner(dinner);
        				i.setAllergens(Helper.getAllergens(id));
        				Helper.getNutrition(id,i);
        				ja.put(i.processItem());
				}
				else {
					
				}
				
				res.close();
     			prep_stmt.close();
     			conn.close();
			}
			catch (Exception e) {
				System.out.println("\n"+e.toString());
			}
			return ja;
		}
		public static JSONArray getItem(String item) {
			JSONArray ja = new JSONArray();
			Connection con;
			PreparedStatement prep_stmt;
			ResultSet res;
			//sets up connection
			try{
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query = "SELECT D.LOCATION, D.ITEM_ID, I.Item_ID,D.STATION,D.BREAKFAST,D.LUNCH,D.DINNER "
						+	"FROM Daily AS D, Item AS I "
						+	"WHERE D.ITEM_ID = (SELECT I.Item_ID  "
						+	"WHERE I.NAME = ?)";
				prep_stmt = con.prepareStatement(query);
				prep_stmt.setString(1, item);
				res = prep_stmt.executeQuery();
				while(res.next()) {
					String name = item;
					String diningCourt = res.getString("D.LOCATION");
					String station = res.getString("D.STATION");
					String id = res.getString("I.Item_ID");
					boolean breakfast = res.getBoolean("D.BREAKFAST");
					boolean lunch = res.getBoolean("D.LUNCH");;
					boolean dinner = res.getBoolean("D.DINNER");
					Item i = new Item(name,id,station,diningCourt);
        				i.setBreakfast(breakfast);
        				i.setLunch(lunch);
        				i.setDinner(dinner);
        				i.setAllergens(Helper.getAllergens(id));
        				Helper.getNutrition(id,i);
        				ja.put(i.processItem());
				}
				res.close();
     			prep_stmt.close();
     			con.close();
			}
			catch (Exception e) {
				System.out.println("\n"+e.toString());
			}
			return ja;
		}
		public static JSONObject login(String user, String pass) throws JSONException {
			Connection con;
			PreparedStatement prep_stmt;
			ResultSet res;
			JSONObject jo = new JSONObject();
			//sets up connection
			try{
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query = "SELECT User_ID "
					+	"FROM User "
					+	"WHERE USER_NAME = ? AND PASSWORD = ?";
				prep_stmt = con.prepareStatement(query);
				prep_stmt.setString(1, user);
				prep_stmt.setString(2, pass);
				res = prep_stmt.executeQuery();
				if (res.next()) {
					int userId = res.getInt("USER_ID");
					jo.put("UserID", userId);
				}
				else {
					jo.put("UserID", -1);
				}
				res.close();
     			prep_stmt.close();
     			con.close();
			}
			catch (Exception e) {
				System.out.println("\n"+e.toString());
				jo.put("UserID", -2);
			}
			return jo;
		}
		public static JSONObject getUsersPref(int userID){
			Connection con;
			PreparedStatement prep_stmt;
			ResultSet res;
			JSONObject jo = new JSONObject();
			try{
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query = "SELECT EGGS,FISH,GLUTEN,MILK,PEANUTS,SHELLFISH,SOY,TREE_NUTS,WHEAT,VEG "
						+	"FROM Preferences "
						+	"WHERE USER_ID = ?";
				prep_stmt = con.prepareStatement(query);
				prep_stmt.setInt(1, userID);
				res = prep_stmt.executeQuery();
				if (res.next()) {
					jo.put("Eggs",res.getBoolean("EGGS"));
					jo.put("Fish",res.getBoolean("FISH"));
					jo.put("Gluten", res.getBoolean("GLUTEN"));
					jo.put("Milk", res.getBoolean("MILK"));
					jo.put("Peanuts",res.getBoolean("PEANUTS"));
					jo.put("Shellfish", res.getBoolean("SHELLFISH"));
					jo.put("Soy", res.getBoolean("SOY"));
					jo.put("Tree_Nuts", res.getBoolean("TREE_NUTS"));
					jo.put("Wheat",res.getBoolean("WHEAT"));
					jo.put("Veg",res.getBoolean("VEG"));
				}
				else {
			
				}
				res.close();
     			prep_stmt.close();
     			con.close();
			}
			catch (Exception e) {
				System.out.println("\n"+e.toString());
			}
			return jo;
		}
		public static boolean updateUsersPref(int userID,JSONObject jo) throws JSONException{
			Connection con;
			PreparedStatement prep_stmt;
			try {
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query = "UPDATE Preferences "
					+	"Set Eggs = ? ,Fish = ?,Gluten = ?,Milk = ?,Peanuts = ?, "
					+	"Shellfish = ?,Soy = ?,Tree_Nuts = ?,Wheat = ?,Veg = ? "	
					+  	"Where USER_ID = ?"; 
				prep_stmt = con.prepareStatement(query);
				prep_stmt.setBoolean(1, jo.getBoolean("Eggs"));
				prep_stmt.setBoolean(2, jo.getBoolean("Fish"));
				prep_stmt.setBoolean(3, jo.getBoolean("Gluten"));
				prep_stmt.setBoolean(4, jo.getBoolean("Milk"));
				prep_stmt.setBoolean(5, jo.getBoolean("Peanuts"));
				prep_stmt.setBoolean(6, jo.getBoolean("Shellfish"));
				prep_stmt.setBoolean(7, jo.getBoolean("Soy"));
				prep_stmt.setBoolean(8, jo.getBoolean("Tree_Nuts"));
				prep_stmt.setBoolean(9, jo.getBoolean("Wheat"));
				prep_stmt.setBoolean(10, jo.getBoolean("Veg"));
				prep_stmt.setInt(11, userID);
				prep_stmt.executeUpdate();
				con.close();
				prep_stmt.close();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		public static boolean updateName(int userID,String newFirst,String newLast){
			Connection con;
			PreparedStatement prep_stmt;
			try{
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query = "Update User "
					+	"Set First_Name = ?,Last_Name = ? "
					+	"Where User_ID = ? ";
				prep_stmt = con.prepareStatement(query);
				prep_stmt.setString(1, newFirst);
				prep_stmt.setString(2, newLast);
				prep_stmt.setInt(3, userID);
				prep_stmt.executeUpdate();
				con.close();
				prep_stmt.close();
				return true;
			}
			catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		public static boolean updatePassword(int userID,String newPassword){
			Connection con;
			PreparedStatement prep_stmt;
			try{
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query = "Update User "
					+	"Set Password = ?"
					+	"Where User_ID = ? ";
				prep_stmt = con.prepareStatement(query);
				prep_stmt.setString(1, newPassword);
				prep_stmt.setInt(2, userID);
				prep_stmt.executeUpdate();
				con.close();
				prep_stmt.close();
				return true;
			}
			catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		public static boolean favItem(int userId,String itemId,String location){
			Connection con;
			PreparedStatement prep_stmt;
			try{
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query = "Insert Into Favorites "
						+	"VALUES(?,?,?)";
				prep_stmt = con.prepareStatement(query);
				prep_stmt.setInt(1, userId);
				prep_stmt.setString(2, itemId);
				prep_stmt.setString(3, location);
				prep_stmt.executeUpdate();
				con.close();
				prep_stmt.close();
				return true;
			}
			catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		public static boolean unfavItem(int userId,String itemId,String location){
			Connection con;
			PreparedStatement prep_stmt;
			try{
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query = "Delete from Favorites "
						+ 	"Where USER_ID = ? AND Item_ID = ? AND Location = ?";
				prep_stmt = con.prepareStatement(query);
				prep_stmt.setInt(1, userId);
				prep_stmt.setString(2, itemId);
				prep_stmt.setString(3, location);
				prep_stmt.executeUpdate();
				con.close();
				prep_stmt.close();
				return true;
			}
			catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		public static JSONArray getFavs(int userID){
			JSONArray ja = new JSONArray();
			Connection con;
			PreparedStatement prep_stmt;
			ResultSet res;
			try{
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query = "Select F.Item_ID,F.Location,I.Name "
					+	"From Favorites as F "
					+ 	"JOIN Item as I "
					+	"Where USER_ID = ? AND I.Item_ID = F.ITEM_ID";//THIS line takes longer then need be;
				prep_stmt = con.prepareStatement(query);
				prep_stmt.setInt(1, userID);
				res = prep_stmt.executeQuery();
				while(res.next()){
					JSONObject jo = new JSONObject();
					jo.put("Item_ID", res.getString("F.Item_ID"));
					jo.put("Location", res.getString("F.Location"));
					jo.put("Name", res.getString("I.Name"));
					ja.put(jo);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return ja;
		}
		public static JSONObject createUser(String user, String pass,String first,String last, boolean facebook) throws JSONException {
			Connection con;
			PreparedStatement prep_stmt;
			ResultSet res;
			JSONObject jo = new JSONObject();
			try{
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
				String query = "SELECT User_ID "
					+	"FROM User "
					+	"WHERE USER_NAME = ?";
				prep_stmt = con.prepareStatement(query);
				prep_stmt.setString(1, user);
				res = prep_stmt.executeQuery();
				if (res.next()) {
					jo.put("UserID", -1);
				}
				else {
					res.close();
					prep_stmt.close();
					query = "INSERT INTO User "
						+	"VALUES(?,?,?,?,?,?) ";
					prep_stmt = con.prepareStatement(query);
					prep_stmt.setInt(1, 0);
					prep_stmt.setString(4, user);
					prep_stmt.setString(2, first);
					prep_stmt.setString(3, last);
					prep_stmt.setBoolean(6, facebook);
					prep_stmt.setString(5, pass);
					prep_stmt.executeUpdate();
					prep_stmt.close();
					query = "SELECT User_ID "
						+	"FROM User "
						+  	"Where User_Name = ?";
					prep_stmt = con.prepareStatement(query);
					prep_stmt.setString(1, user);
					res = prep_stmt.executeQuery();
					if (!res.next()) {
						jo.put("UserID", -2);
					}
					else{
						int userID = res.getInt("User_ID");
						res.close();
						prep_stmt.close();
						query = "INSERT INTO Preferences "
						+" VALUES(?,FALSE,FALSE,FALSE,FALSE,FALSE,FALSE,FALSE,FALSE,FALSE,FALSE)";
						prep_stmt = con.prepareStatement(query);
						prep_stmt.setInt(1, userID);
						prep_stmt.executeUpdate();
						jo.put("UserID", userID);
						res.close();
						prep_stmt.close();
						con.close();
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				jo.put("UserID", -2);
			}
			return jo;
		}
}
