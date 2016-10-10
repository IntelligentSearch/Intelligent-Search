package com.rest;

import java.sql.*;
import java.util.ArrayList;
import org.json.*;

public class Helper {
				
		public static void getNutrition(String id, Item item) {
			Connection con;
			PreparedStatement prep_stmt;
			ResultSet res;
			try{
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
				res.close();
     			prep_stmt.close();
     			con.close();
     			item.setNutrients(name,value);
			}
			catch (Exception e) {
				System.out.println("\n"+e.toString());
			}
			
		}
		public static boolean[] getAllergens(String id) {
			Connection con;
			PreparedStatement prep_stmt;
			ResultSet res;
			boolean[] pref = null;
			try{
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
				res.close();
     			prep_stmt.close();
     			con.close();
			}
			catch (Exception e) {
				System.out.println("\n"+e.toString());
			}
			return pref;
		}
<<<<<<< HEAD
=======
/*
		public static void main(String[] args){
			//getFoodDin("");
			getItem("Oatmeal");
			getNutrition("6ea795d3-67d5-4a39-9e81-d2ee5e9e64aa");
			getAllergens("6ea795d3-67d5-4a39-9e81-d2ee5e9e64aa");
			
		}
*/
>>>>>>> origin/master
}
