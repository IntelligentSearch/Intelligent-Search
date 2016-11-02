
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.*;

public class Bus_Call {
	public static JSONArray getAllRoutesStops(){
		JSONArray ja = getAllRoutes();
		JSONArray ret = new JSONArray();
		for(int i =0; i < ja.length();i++){
			try {
				JSONObject jo = new JSONObject();
				JSONObject object = ja.getJSONObject(i);
				JSONArray stops = getRouteStops(object.getString("id"));
				jo.put("stops", stops);
				jo.put("route", object);
				ja.put(ret);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ja;
	}
	public static JSONArray getAllRoutes(){
		String query = "SELECT route_id, route_short_name,route_long_name,route_desc,route_type,route_color,route_text_color "
				+ "From Routes";
		JSONArray ja = new JSONArray();
		PreparedStatement prep_stmt = null;
		ResultSet res = null;
		//sets up connection
		Connection conn = null;
		try {
				JSONObject jo = new JSONObject();
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CITYBUS", "root", "cz002");
				prep_stmt = conn.prepareStatement(query);
				System.out.println(prep_stmt);
				res = prep_stmt.executeQuery();
				while(res.next()){
					jo.put("id",res.getString("route_id"));
					jo.put("short_name",res.getString("route_short_name"));
					jo.put("long_name",res.getString("route_long_name"));
					jo.put("desc",res.getString("route_desc"));
					jo.put("type",res.getString("route_type"));
					jo.put("color",res.getString("route_color"));
					jo.put("text_color",res.getString("route_text_color"));
					ja.put(jo);
				}
		}
		catch (Exception e) {
				System.out.println("\n"+e.toString());
				try {
					ja.put(new JSONObject().put("error",e.toString()));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
		}
		finally{
				System.out.println(ja);
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
	public static JSONArray getRouteStops(String route_id){
		String query = "SELECT s.stop_id,s.stop_code,s.stop_name,s.stop_desc,s.stop_lat,s.stop_lon,s.location_type "
				+ "From Trips as t "
				+ "Join "
				+ "(Select route_id From Routes where route_id = ?) as r "
				+ "on t.route_id = r.route_id "
				+ "Join Stop_Times as st "
				+ "on t.trip_id = st.trip_id "
				+ "Join Stops as s "
				+ "on st.stop_id = s.stop_id  "
				+ "Where t.service_id = ( "
				+ "Select service_id From Calendar where date = ? LIMIT 1) ";
		
		JSONArray ja = new JSONArray();
		PreparedStatement prep_stmt = null;
		ResultSet res = null;
		//sets up connection
		Connection conn = null;
		try {
				DateFormat df = new SimpleDateFormat("yyyyMMdd");
				Date dateobj = new Date();
				String date = df.format(dateobj);
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CITYBUS", "root", "cz002");
				prep_stmt = conn.prepareStatement(query);
				prep_stmt.setString(2,date);
				prep_stmt.setString(1,route_id);
				System.out.println(prep_stmt);
				res = prep_stmt.executeQuery();
				res.next();
				String id = res.getString("s.stop_id");
				boolean t = false;
				do{
					JSONObject jo = new JSONObject();
					String id_stop = res.getString("s.stop_id");
					if(t &&id_stop.equals(id)){
						break;
					}
					t = true;
					jo.put("stop_id", res.getString("s.stop_id"));
					jo.put("stop_code", res.getString("s.stop_code"));
					jo.put("stop_name", res.getString("s.stop_name"));
					jo.put("stop_desc", res.getString("s.stop_desc"));
					jo.put("stop_lat", res.getString("s.stop_lat"));
					jo.put("stop_long", res.getString("s.stop_lon"));
					jo.put("stop_lat", res.getString("s.stop_lat"));
					ja.put(jo);
				}while(res.next());
		}
		catch (Exception e) {
				System.out.println("\n"+e.toString());
				try {
					e.printStackTrace();
					ja.put(new JSONObject().put("error",e.toString()));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
		}
		finally{
				System.out.println(ja);
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
	
}
