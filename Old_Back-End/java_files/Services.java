import java.sql.*;
import java.util.ArrayList;
import org.json.*;

public class Services {
				
		public static JSONArray getFoodDin(String diningCourt){
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
        			i.setAllergens(getAllergens(id));
        			getNutrition(id,i);
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
		public static void getItemDin(String diningCourt, String item) {
			Connection con;
			PreparedStatement prep_stmt;
			Statement stmt;
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
		}
		public static void getItem(String item) {
			Connection con;
			PreparedStatement prep_stmt;
			Statement stmt;
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
				}
				res.close();
     			prep_stmt.close();
     			con.close();
			}
			catch (Exception e) {
				System.out.println("\n"+e.toString());
			}
		}
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
		public static void main(String[] args){
			//getFoodDin("");
			getItem("Oatmeal");
			getNutrition("6ea795d3-67d5-4a39-9e81-d2ee5e9e64aa");
			getAllergens("6ea795d3-67d5-4a39-9e81-d2ee5e9e64aa");
			
		}
}
