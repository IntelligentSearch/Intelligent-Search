package com.rest;

import java.sql.*;
import java.util.ArrayList;
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
					String  id = res.getString("I.Item_ID");
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
        			i.setAllergens(Helper.getAllergens(id));
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
}
