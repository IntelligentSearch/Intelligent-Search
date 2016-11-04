package com.rest;

import java.sql.*;
import org.json.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Call {
		/*TODO USER STORIES 
		 *  test calorie count
		 */
		//give userID as greater than 0 if you dont have a userID
		public static JSONArray getAll(int userID,Parsed p) throws JSONException{
			JSONArray ja = new JSONArray();
			PreparedStatement prep_stmt = null;
			ResultSet res = null;
			//sets up connection
			Connection conn = null;
			try {
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
					String query = "SELECT D.ITEM_ID,D.LOCATION,D.LateLunch,I.Ingredients,I.Item_ID, I.NAME,D.STATION,D.BREAKFAST,D.LUNCH,D.DINNER "
							+	"FROM Daily AS D "
							+	"JOIN Item AS I "
							+	"ON D.ITEM_ID = I.Item_ID ";
					prep_stmt = conn.prepareStatement(query);
					res = prep_stmt.executeQuery();
					Helper.readItems(res,ja,userID,p);
			}
			catch (Exception e) {
					System.out.println("\n"+e.toString());
					ja.put(new JSONObject().put("error",e.toString()));
			}
			finally{
					try{
							if(conn != null){
									conn.close();
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
			return ja;
		}
		
		public static JSONArray getFoodDining(String diningCourt, int userID,Parsed p) throws JSONException{
				JSONArray ja = new JSONArray();
				Connection conn = null;
				ResultSet res = null;
				PreparedStatement prep_stmt = null;
				try {
						Class.forName("com.mysql.jdbc.Driver");
						conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
						String query = "SELECT D.LateLunch,D.LOCATION,I.ITEM_ID, D.STATION,I.Ingredients,D.BREAKFAST,D.LUNCH,D.DINNER,I.NAME "
								+	"FROM Daily AS D "
								+	"INNER JOIN Item AS I "
								+	"ON D.ITEM_ID = I.ITEM_ID "
								+	"WHERE D.LOCATION LIKE ? ";
						prep_stmt = conn.prepareStatement(query);
						prep_stmt.setString(1, "%"+diningCourt+"%");
						res = prep_stmt.executeQuery();
						Helper.readItems(res,ja,userID,p);
						// ja.put(new JSONObject().put("loc",diningCourt));
				} catch (Exception e) {
						System.out.println("\n"+e.toString());
						ja.put(new JSONObject().put("error",e.toString()));
				}
				finally{
						try{
								if(conn != null){
										conn.close();
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
				return ja;			
		}
		public static JSONArray getItemDin(String diningCourt, String item,int userID,Parsed p ) throws JSONException {
				JSONArray ja = new JSONArray();
				PreparedStatement prep_stmt = null;
				ResultSet res = null;
				//sets up connection
				Connection conn = null;
				try {
						Class.forName("com.mysql.jdbc.Driver");
						conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
						String query = "SELECT D.ITEM_ID,D.LOCATION,D.LateLunch,I.Ingredients,I.Item_ID, I.NAME,D.STATION,D.BREAKFAST,D.LUNCH,D.DINNER "
								+	"FROM Daily AS D "
								+	"JOIN Item AS I "
								+	"ON D.ITEM_ID = I.Item_ID "
								+	"WHERE I.NAME LIKE ? AND D.LOCATION LIKE ?";
						prep_stmt = conn.prepareStatement(query);
						prep_stmt.setString(1, "%"+item+"%");
						prep_stmt.setString(2,"%"+diningCourt+"%");
						/*JSONObject jo = new JSONObject();
						  jo.put("Query",prep_stmt.toString());
						  ja.put(jo);*/
						res = prep_stmt.executeQuery();
						Helper.readItems(res,ja,userID,p);
				}
				catch (Exception e) {
						System.out.println("\n"+e.toString());
						ja.put(new JSONObject().put("error",e.toString()));
				}
				finally{
						try{
								if(conn != null){
										conn.close();
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
				return ja;
		}
		public static JSONArray getItem(String item,int userID,Parsed p) throws ClassNotFoundException, JSONException{
			JSONArray ja = new JSONArray();
			Connection con = null;
			PreparedStatement prep_stmt = null;
			ResultSet res = null;
			//sets up connection
				try{
						Class.forName("com.mysql.jdbc.Driver");
						con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
						String query = "SELECT I.Name,D.LOCATION, I.Ingredients,D.LateLunch,D.ITEM_ID, I.Item_ID,D.STATION,D.BREAKFAST,D.LUNCH,D.DINNER "
								+	"FROM Daily AS D, Item AS I "
								+	"WHERE D.ITEM_ID = (SELECT I.Item_ID  "
								+	"WHERE I.NAME LIKE ?)";
						prep_stmt = con.prepareStatement(query);
						prep_stmt.setString(1,"%"+item+"%");
						res = prep_stmt.executeQuery();
						Helper.readItems(res,ja,userID,p);
				}
				catch (Exception e) {
						System.out.println("\n"+e.toString());
						ja.put(new JSONObject().put("error",e.toString()));
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
				return ja;
		}
		public static JSONArray getAtTime(String time,int userID,Parsed p) throws JSONException{
			JSONArray ja = new JSONArray();
			PreparedStatement prep_stmt = null;
			ResultSet res = null;
			//sets up connection
			Connection conn = null;
			try {
					
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
					String query = "SELECT D.ITEM_ID,D.LOCATION,D.LateLunch,I.Ingredients,I.Item_ID, I.NAME,D.STATION,D.BREAKFAST,D.LUNCH,D.DINNER "
							+	"FROM Daily AS D "
							+	"JOIN Item AS I "
							+	"ON D.ITEM_ID = I.Item_ID "
							+	"WHERE D."+time +"= true";
					prep_stmt = conn.prepareStatement(query);
					//prep_stmt.setString(1, "I."+time);
					System.out.println(prep_stmt);
					res = prep_stmt.executeQuery();
					Helper.readItems(res,ja, userID,p);
			}
			catch (Exception e) {
					System.out.println("\n"+e.toString());
					ja.put(new JSONObject().put("error",e.toString()));
			}
			finally{
					try{
							if(conn != null){
									conn.close();
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
			return ja;
		}
		public static JSONArray getItemAtTime(String time,String item,int userID,Parsed p) throws JSONException{
			JSONArray ja = new JSONArray();
			PreparedStatement prep_stmt = null;
			ResultSet res = null;
			//sets up connection
			Connection conn = null;
			try {
					
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
					String query = "SELECT D.ITEM_ID,D.LOCATION,D.LateLunch,I.Ingredients,I.Item_ID, I.NAME,D.STATION,D.BREAKFAST,D.LUNCH,D.DINNER "
							+	"FROM Daily AS D "
							+	"JOIN Item AS I "
							+	"ON D.ITEM_ID = I.Item_ID "
							+	"WHERE D."+time +"= true AND I.NAME LIKE ?";
					prep_stmt = conn.prepareStatement(query);
					prep_stmt.setString(1,"%"+item+"%");
					//prep_stmt.setString(1, "I."+time);
					System.out.println(prep_stmt);
					res = prep_stmt.executeQuery();
					Helper.readItems(res,ja, userID,p);
			}
			catch (Exception e) {
					System.out.println("\n"+e.toString());
					ja.put(new JSONObject().put("error",e.toString()));
			}
			finally{
					try{
							if(conn != null){
									conn.close();
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
			return ja;
		}
		public static JSONArray getItemDinAtTime(String time,String diningCourt,String item, int userID,Parsed p) throws JSONException{
			JSONArray ja = new JSONArray();
			PreparedStatement prep_stmt = null;
			ResultSet res = null;
			//sets up connection
			Connection conn = null;
			try {
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
					String query = "SELECT D.ITEM_ID,D.LOCATION,D.LateLunch,I.Ingredients,I.Item_ID, I.NAME,D.STATION,D.BREAKFAST,D.LUNCH,D.DINNER "
							+	"FROM Daily AS D "
							+	"JOIN Item AS I "
							+	"ON D.ITEM_ID = I.Item_ID "
							+	"WHERE D."+time +"= true AND I.NAME LIKE ? AND D.LOCATION LIKE ?";
					prep_stmt = conn.prepareStatement(query);
					prep_stmt.setString(1, "%"+item+"%");
					prep_stmt.setString(2,"%"+diningCourt+"%");
					/*JSONObject jo = new JSONObject();
					  jo.put("Query",prep_stmt.toString());
					  ja.put(jo);*/
					res = prep_stmt.executeQuery();
					Helper.readItems(res,ja,userID,p);
			}
			catch (Exception e) {
					System.out.println("\n"+e.toString());
					ja.put(new JSONObject().put("error",e.toString()));
			}
			finally{
					try{
							if(conn != null){
									conn.close();
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
			return ja;
		}
		public static JSONArray getDinAtTime(String time,String diningCourt, int userID,Parsed p) throws JSONException{
			JSONArray ja = new JSONArray();
			PreparedStatement prep_stmt = null;
			ResultSet res = null;
			//sets up connection
			Connection conn = null;
			try {
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
					String query = "SELECT D.ITEM_ID,D.LOCATION,D.LateLunch,I.Ingredients,I.Item_ID, I.NAME,D.STATION,D.BREAKFAST,D.LUNCH,D.DINNER "
							+	"FROM Daily AS D "
							+	"JOIN Item AS I "
							+	"ON D.ITEM_ID = I.Item_ID "
							+	"WHERE D."+time +"= true AND D.LOCATION LIKE ?";
					prep_stmt = conn.prepareStatement(query);
					prep_stmt.setString(1,"%"+diningCourt+"%");
					/*JSONObject jo = new JSONObject();
					  jo.put("Query",prep_stmt.toString());
					  ja.put(jo);*/
					res = prep_stmt.executeQuery();
					Helper.readItems(res,ja,userID,p);
			}
			catch (Exception e) {
					System.out.println("\n"+e.toString());
					ja.put(new JSONObject().put("error",e.toString()));
			}
			finally{
					try{
							if(conn != null){
									conn.close();
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
			return ja;
		}
		public static JSONObject login(String user, String pass) throws JSONException {
				Connection con = null;
				PreparedStatement prep_stmt = null;
				ResultSet res = null;
				JSONObject jo = new JSONObject();
				jo.put("UserName",user);
				//jo.put("password:",pass);
				try{
						Class.forName("com.mysql.jdbc.Driver");
						con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
						String query = "SELECT User_ID,First_Name,Last_Name "
								+	"FROM User "
								+	"WHERE User_Name = ? AND Password = ?";
						prep_stmt = con.prepareStatement(query);
						prep_stmt.setString(1, user);
						prep_stmt.setString(2, pass);
						res = prep_stmt.executeQuery();
						if (res.next()) {
								int userId = res.getInt("USER_ID");
								jo.put("UserID", userId);
								jo.put("FirstName",res.getString("First_Name"));
								jo.put("LastName",res.getString("Last_Name"));
						}
						else {
								jo.put("UserID", -1);
						}
				}
				catch (Exception e) {
						System.out.println("\n"+e.toString());
						jo.put("UserID", -2);
						jo.put("Exception", e.toString());
						jo.put("query",prep_stmt.toString());
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
				return jo;
		}

		public static JSONObject getUsersPref(int userID){
				Connection con = null;
				PreparedStatement prep_stmt = null;
				ResultSet res = null;
				JSONObject jo = new JSONObject();
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

				return jo;
		}
		public static JSONObject updateUsersPref(int userID,String update) throws JSONException,ClassNotFoundException{
				JSONObject jo1 = new JSONObject();
				Connection con = null;
				PreparedStatement prep_stmt = null;
				try {
						Class.forName("com.mysql.jdbc.Driver");
						con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
						String query = "UPDATE Preferences SET "+update
								+ 	" WHERE USER_ID = ?";
						prep_stmt = con.prepareStatement(query);
						/*prep_stmt.setBoolean(1, jo.getBoolean("Eggs"));
						  prep_stmt.setBoolean(2, jo.getBoolean("Fish"));
						  prep_stmt.setBoolean(3, jo.getBoolean("Gluten"));
						  prep_stmt.setBoolean(4, jo.getBoolean("Milk"));
						  prep_stmt.setBoolean(5, jo.getBoolean("Peanuts"));
						  prep_stmt.setBoolean(6, jo.getBoolean("Shellfish"));
						  prep_stmt.setBoolean(7, jo.getBoolean("Soy"));
						  prep_stmt.setBoolean(8, jo.getBoolean("Tree_Nuts"));
						  prep_stmt.setBoolean(9, jo.getBoolean("Wheat"));
						  prep_stmt.setBoolean(10, jo.getBoolean("Veg"));*/
						prep_stmt.setInt(1, userID);
						prep_stmt.executeUpdate();
						jo1.put("success",1);
				} catch (SQLException e) {
						e.printStackTrace();
						jo1.put("success",e.toString());
						jo1.put("query",prep_stmt.toString());
				}
				finally{
						try{
								if(con != null){
										con.close();
								}
								if(prep_stmt != null){
										prep_stmt.close();
								}
						}	
						catch(Exception e){
						}
				}

				return jo1;
		}
		public static JSONObject updateName(int userID,String newFirst,String newLast) throws ClassNotFoundException, JSONException{
				Connection con = null;
				PreparedStatement prep_stmt = null;
				JSONObject jo = new JSONObject();
				try{
						Class.forName("com.mysql.jdbc.Driver");
						con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
						String query = "Update User "
								+	"Set First_Name = ?,Last_Name = ? "
								+	"Where User_ID = ? ";
						prep_stmt = con.prepareStatement(query);
						prep_stmt.setString(1, newFirst);
						prep_stmt.setString(2, newLast);
						prep_stmt.setInt(3, userID);
						prep_stmt.executeUpdate();
						jo.put("success",1);
				}
				catch (SQLException e) {
						e.printStackTrace();
						jo.put("success",-1);
				}
				finally{
						try{
								if(con != null){
										con.close();
								}
								if(prep_stmt != null){
										prep_stmt.close();
								}
						}	
						catch(Exception e){
						}
				}

				return jo;
		}
		public static JSONObject updatePassword(int userID,String newPassword,String oldPassword)throws ClassNotFoundException, JSONException{
				Connection con = null;
				PreparedStatement prep_stmt = null;
				JSONObject jo = new JSONObject();
				try{
						Class.forName("com.mysql.jdbc.Driver");
						con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
						String query = "Update User "
								+	"Set Password = ?"
								+	"Where User_ID = ? AND Password = ?";
						prep_stmt = con.prepareStatement(query);
						prep_stmt.setString(1, newPassword);
						prep_stmt.setInt(2, userID);
						prep_stmt.setString(3, oldPassword);
						prep_stmt.executeUpdate();
						jo.put("success",1);
				}
				catch (SQLException e) {
						e.printStackTrace();
						jo.put("success",e.toString());
				}
				finally{
						try{
								if(con != null){
										con.close();
								}
								if(prep_stmt != null){
										prep_stmt.close();
								}
						}	
						catch(Exception e){
						}
				}
				return jo;
		}
		public static JSONObject favItem(int userId,String itemId,String location) throws ClassNotFoundException, JSONException{
				JSONObject jo = new JSONObject();
				if((itemId != null && !itemId.isEmpty()) && (location != null && !location.isEmpty())){
						jo.put("success",-1);
						return jo;
				}
				Connection con = null;
				PreparedStatement prep_stmt = null;
				try{
						Class.forName("com.mysql.jdbc.Driver");
						con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
						String query = "Insert Into Favorites "
								+	"VALUES(?,?,?)";
						prep_stmt = con.prepareStatement(query);
						prep_stmt.setInt(1, userId);
						if(itemId != null && !itemId.isEmpty()){
								prep_stmt.setString(2, itemId);
						}
						else{
								prep_stmt.setString(2,null);
						}
						if(location != null && !location.isEmpty()){
								prep_stmt.setString(3, location);
						}
						else{
								prep_stmt.setString(3,null);
						}

						prep_stmt.executeUpdate();
						jo.put("success",1);
				}
				catch (SQLException e) {
						e.printStackTrace();
						jo.put("success",e.toString());
				}
				finally{
						try{
								if(con != null){
										con.close();
								}
								if(prep_stmt != null){
										prep_stmt.close();
								}
						}	
						catch(Exception e){
						}
				}
				return jo;
		}
		public static JSONObject unfavItem(int userId,String itemID,String location) throws ClassNotFoundException, JSONException{
				Connection con = null;
				JSONObject jo = new JSONObject();
				PreparedStatement prep_stmt = null;

				boolean itemFlag = true;
				boolean locationFlag = true;

				if (itemID == null || itemID.equals("")) {
						itemFlag = false;
				}

				if (location == null || location.equals("")) {
						locationFlag = false;
				}
				try{
						Class.forName("com.mysql.jdbc.Driver");
						con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
						String query;
						if (itemFlag) {
								query = "Delete from Favorites "
										+  "Where USER_ID = ? AND Item_ID = ?";
								prep_stmt = con.prepareStatement(query);
								prep_stmt.setInt(1, userId);
								prep_stmt.setString(2, itemID);
						} else {
								query = "Delete from Favorites "
										+  "Where USER_ID = ? AND Location = ?";
								prep_stmt = con.prepareStatement(query);
								prep_stmt.setInt(1, userId);
								prep_stmt.setString(2, location);
						}
						prep_stmt.executeUpdate();
						jo.put("success",1);
				}
				catch (SQLException e) {
						e.printStackTrace();
						jo.put("success",-1);
				}
				finally{
						try{
								if(con != null){
										con.close();
								}
								if(prep_stmt != null){
										prep_stmt.close();
								}
						}	
						catch(Exception e){
						}
				}
				return jo;
		}
		public static JSONArray getFavs(int userID){
				JSONArray ja = new JSONArray();
				Connection con = null;
				PreparedStatement prep_stmt = null;
				ResultSet res = null;
				try{
						Class.forName("com.mysql.jdbc.Driver");
						con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");
						String query = "Select Item_ID,Location "
								+	"From Favorites "
								+	"Where USER_ID = ?"; 
						prep_stmt = con.prepareStatement(query);
						prep_stmt.setInt(1, userID);
						res = prep_stmt.executeQuery();
						while(res.next()) {
								JSONObject jo = new JSONObject();
								String item_id = res.getString("Item_ID");
								jo.put("Food_ID", item_id);
								String loc = res.getString("Location");
								jo.put("Location", loc );
								String name = "";
								if(item_id != null){
										PreparedStatement prep_stmt2 = null;
										ResultSet res2 = null;

										query = "Select Name FROM Item Where Item_ID = ?";
										prep_stmt2 = con.prepareStatement(query);
										prep_stmt2.setString(1, item_id);
										res2 = prep_stmt2.executeQuery();
										if(!res2.next()){
												jo.put(name,"food not found");
												prep_stmt2.close();
												res2.close();
												continue;
										}
										name = res2.getString("Name");
										jo.put("Name", name);
										prep_stmt2.close();
										res2.close();
								}
								/*if(item_id != null && loc != null){
										jo.put("Item Cards", getItemDin(loc,name,userID,null));
								}
								else if(item_id != null){
										jo.put("Item Cards", getItem(name,userID,null));
								}
								else if(loc != null){
										//jo.put("location is being checked",loc);
										jo.put("Item Cards",getFoodDining(loc,userID,null));
								}*/
								ja.put(jo);
						}
				}
				catch (Exception e) {
						e.printStackTrace();
						try{
							ja.put(new JSONObject().put("error",e.toString()));
						}
						catch(Exception e1){
							
						}
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
				return ja;
		}
		public static JSONObject createUser(String user, String pass,String first,String last, boolean facebook) throws JSONException {
				Connection con = null;
				PreparedStatement prep_stmt = null;
				ResultSet res = null;
				JSONObject jo = new JSONObject();
				try{
						Class.forName("com.mysql.jdbc.Driver");
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
								}
						}
				}
				catch (Exception e) {
						e.printStackTrace();
						jo.put("UserID",-1);
						//jo.put("name ",first+" " +last);
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

				return jo;
		}
}
