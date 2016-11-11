package com.rest;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.*;

public class Bus_Call {
	public static JSONArray scheduleStaticOneRoute(String time,String day,String stop1,String stop2){
		JSONArray ja = new JSONArray();
		PreparedStatement prep_stmt = null;
		ResultSet res = null;
		//sets up connection
		Connection conn = null;
		String query = "Select r.route_short_name from stops as s"
				+ "Join(Select * from Stop_Times where departure_time > ? as st "//AND departure_time < ?) as st "
				+ "on st.stop_id = s.stop_id "
				+ "Join trips as t "
				+ "on t.trip_id = st.trip_id"
				+ "Join (Select * from Calendar where date = ?) as c "
				+ "on c.service_id = t.service_id "
				+ "Join Routes as r "
				+ "on r.route_id = t.route_id"
				+ "where stop_code = ?)";
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CITYBUS", "root", "cz002");
			prep_stmt = conn.prepareStatement(query);
			prep_stmt.setString(1, time);
			prep_stmt.setString(2, day);
			prep_stmt.setString(3, stop1);
			//prep_stmt.setString(4, stop2);
			res = prep_stmt.executeQuery();
			System.out.println(prep_stmt);
			while(res.next()){
				System.out.println(res.getString("r.route_short_name"));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(conn != null){
					conn.close();
				}
				if(prep_stmt != null){
					prep_stmt.close();
				}
				if(res != null){
					res.close();
				}
			}
			catch(Exception e){
				
			}
		}
		
		return ja;
	}
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
				ret.put(jo);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
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
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CITYBUS", "root", "cz002");
				prep_stmt = conn.prepareStatement(query);
				System.out.println(prep_stmt);
				res = prep_stmt.executeQuery();
				while(res.next()){
					JSONObject jo = new JSONObject();
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
				String id = null;
				if(res.next()){
					id = res.getString("s.stop_id");
				}
				else{
					return ja.put(new JSONObject().put("stop_id","error"));
				}
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
	public static class routeStops{
		JSONArray Stops0;
		JSONArray Stops1;
		JSONObject Route;
		HashMap<String,String> Stop;
		String id;
		public routeStops(String id){
			this.id = id;
			Stops0 = new JSONArray();
			Route = new JSONObject();
			Stop = new HashMap<String,String>();
			Stops1 = new JSONArray();
		}
		public JSONArray getStopsByID(int id) {
			if(id == 0){
				return Stops0;
			}
			return Stops1;
		}
		public JSONArray getStops(){
			try{
				for(int i = 0; i < Stops1.length(); i++){
					Stops0.put(Stops1.get(i));
				}
				return Stops0;
			}
			catch(Exception e){
				return null;
			}
		}
		public JSONObject getRoute() {
			return Route;
		}
		public HashMap<String,String> getStopsLists() {
			return Stop;
		}
		public void addStop(String s){
			Stop.put(s, s);
		}
		public boolean stopExist(String s){
			return Stop.containsKey(s);
		}
		@Override
		public String toString(){
			return this.id;
		}
		@Override
		public boolean equals(Object o){
			return ((String)o).equals(id);
		}
		
	}
	public static int indexItem(String o,ArrayList<routeStops> r){
		int i = 0;
		for(routeStops x:r ){
			if(x.equals(o)){
				return i;
			}
			i++;
		}
		return -1;
	}
	public static JSONArray getStops(){
		ArrayList<routeStops> a = new ArrayList<routeStops>();
		String query = "SELECT t.direction_id,st.stop_id,st.stop_sequence,t.route_id, r.route_short_name,r.route_long_name,r.route_desc,r.route_type,r.route_color,r.route_text_color,s.stop_id,s.stop_code,s.stop_name,s.stop_desc,s.stop_lat,s.stop_lon,s.location_type "
				+ "From Trips as t "
				+ "Join Routes as r "
				+ "on r.route_id = t.route_id "
				+ "Join Stop_Times as st "
				+ "on t.trip_id = st.trip_id "
				+ "Join Stops as s "
				+ "on st.stop_id = s.stop_id  "
				+ "Where t.service_id = ( "
				+ "Select service_id From Calendar where date = ? LIMIT 1) "
				+ "Group By t.direction_id,t.route_id,s.stop_id,st.stop_id "
				+ "Order By st.stop_sequence, t.route_id";
		JSONArray ja = new JSONArray();
		PreparedStatement prep_stmt = null;
		ResultSet res = null;
		long mil = System.currentTimeMillis();
		//sets up connection
		Connection conn = null;
		//list of all routes to make sure only use once
		//ArrayList<String> routes_used = new ArrayList<String>();
		int count = 0;
		try {
				DateFormat df = new SimpleDateFormat("yyyyMMdd");
				Date dateobj = new Date();
				String date = df.format(dateobj);
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CITYBUS", "root", "cz002");
				prep_stmt = conn.prepareStatement(query);
				prep_stmt.setString(1,date);
				System.err.println(prep_stmt);
				res = prep_stmt.executeQuery();
				System.err.println("time it takes to run "+(System.currentTimeMillis()-mil));
				mil = System.currentTimeMillis();
				String s_id = "-567";
				String r_id = "-467";
				boolean newRoute = true;
				int index = -1;
				while(res.next()){
					String id_route = res.getString("t.route_id");
					String id_stop = res.getString("s.stop_id");
					if(id_stop.equals(s_id) && r_id.equals(res.getString("t.route_id"))){
						continue;
					}
					s_id = id_stop;
					if(!r_id.equals(id_route)){
						//route_id
						if((index = indexItem(id_route,a)) < 0){
							routeStops routeStop = new routeStops(res.getString("t.route_id"));
							r_id = id_route;		
							routeStop.getRoute().put("id",id_route);
							routeStop.getRoute().put("short_name",res.getString("r.route_short_name"));
							routeStop.getRoute().put("long_name",res.getString("r.route_long_name"));
							routeStop.getRoute().put("desc",res.getString("r.route_desc"));
							routeStop.getRoute().put("type",res.getString("r.route_type"));
							routeStop.getRoute().put("color",res.getString("r.route_color"));
							routeStop.getRoute().put("text_color",res.getString("r.route_text_color"));
							s_id = res.getString("s.stop_id");
							a.add(routeStop);
							index = indexItem(id_route,a);
						}
						newRoute = true;
					}
					newRoute = false;
					routeStops rs = a.get(index);
					if(!rs.stopExist(res.getString("s.stop_id"))){
						JSONObject jo = new JSONObject();
						jo.put("stop_count", res.getString("st.stop_sequence"));
						jo.put("stop_id", res.getString("s.stop_id"));
						jo.put("stop_code", res.getString("s.stop_code"));
						jo.put("stop_name", res.getString("s.stop_name"));
						jo.put("stop_desc", res.getString("s.stop_desc"));
						jo.put("stop_lat", res.getString("s.stop_lat"));
						jo.put("stop_long", res.getString("s.stop_lon"));
						jo.put("stop_lat", res.getString("s.stop_lat"));
						jo.put("travel_dir",res.getString("t.direction_id"));
						rs.getStopsByID(res.getInt("t.direction_id")).put(jo);
						rs.addStop(res.getString("s.stop_id"));
					}
				}
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
				for(routeStops routes: a ){
					if(routes != null){
						try {
							ja.put(new JSONObject().put("routes",routes.getRoute()).put("stops",routes.getStops()));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				System.err.println("time it takes to run "+(System.currentTimeMillis()-mil));
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
	public static JSONObject getClosestStopByRouteID(float lon,float lat,String routeID){
		String query = "select DISTINCT s.stop_code,s.stop_id,s.stop_name,s.stop_desc,s.stop_lat,s.stop_lon,s.location_type from Stops as s "
				+	"JOIN Stop_Times as st on st.stop_id = s.stop_id "
				+ 	"JOIN Trips as t on st.trip_id = t.trip_id "
				+	"JOIN Calendar as c on c.service_id = t.service_id "
				+ 	"where t.route_id = ? and c.date = ? "
				+	"order by SQRT(power((?-stop_lon),2)+power((?-stop_lat),2)) LIMIT 1";
		
		JSONObject jo = new JSONObject();
		PreparedStatement prep_stmt = null;
		Connection conn = null;
		ResultSet res = null;
		try{
			DateFormat df = new SimpleDateFormat("yyyyMMdd");
			Date dateobj = new Date();
	
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CITYBUS", "root", "cz002");
			prep_stmt = conn.prepareStatement(query);
			
			prep_stmt.setString(1,routeID);
			prep_stmt.setString(2,df.format(dateobj));
			prep_stmt.setFloat(3,lon);
			prep_stmt.setFloat(4,lat);
			
			res = prep_stmt.executeQuery();
			if(!res.next()){
				jo.put("error", "no stops near by");
			}
			else{
				jo.put("stop_id", res.getString("s.stop_id"));
				jo.put("stop_code",res.getString("s.stop_code"));
				jo.put("stop_name",res.getString("s.stop_name"));
				jo.put("stop_desc",res.getString("s.stop_desc"));
				jo.put("stop_lat",res.getFloat("s.stop_lat"));
				jo.put("stop_lon",res.getFloat("s.stop_lon"));
				jo.put("location_type",res.getString("s.location_type"));
				jo.put("stop_times",Stop_Handler.parseURL("http://myride.gocitybus.com/161027Purdue/Default1.aspx?pwd=cs307-102716&code="+res.getString("s.stop_code")));
			}
			
		}
		catch(Exception e){
			jo.put("error",e.toString());
		}
		finally{
			try{
				if(conn != null){
					conn.close();
				}
				if(prep_stmt != null){
					prep_stmt.close();
				}
				if(res != null){
					res.close();
				}
			}
			catch(Exception e){
				
			}
		}
		return jo;
	}
	public static JSONObject getClosestStop(float lon, float lat){
		String query = "select stop_id,stop_code,stop_name,stop_desc,stop_lat,stop_lon,location_type from Stops "
				+ "order by SQRT(power((?-stop_lon),2)+power((?-stop_lat),2)) LIMIT 1";
		JSONObject jo = new JSONObject();
		PreparedStatement prep_stmt = null;
		Connection conn = null;
		ResultSet res = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CITYBUS", "root", "cz002");
			prep_stmt = conn.prepareStatement(query);
			prep_stmt.setFloat(1,lon);
			prep_stmt.setFloat(2,lat);
			res = prep_stmt.executeQuery();
			if(!res.next()){
				jo.put("error", "no stops near by");
			}
			else{
				jo.put("stop_id", res.getString("stop_id"));
				jo.put("stop_code",res.getString("stop_code"));
				jo.put("stop_name",res.getString("stop_name"));
				jo.put("stop_desc",res.getString("stop_desc"));
				jo.put("stop_lat",res.getFloat("stop_lat"));
				jo.put("stop_lon",res.getFloat("stop_lon"));
				jo.put("location_type",res.getString("location_type"));
				jo.put("stop_times",Stop_Handler.parseURL("http://myride.gocitybus.com/161027Purdue/Default1.aspx?pwd=cs307-102716&code="+res.getString("stop_code")));
			}
			
		}
		catch(Exception e){
			jo.put("error",e.toString());
		}
		finally{
			try{
				if(conn != null){
					conn.close();
				}
				if(prep_stmt != null){
					prep_stmt.close();
				}
				if(res != null){
					res.close();
				}
			}
			catch(Exception e){
				
			}
		}
		return jo;
	}
	public static JSONArray getHardRoute(String name) {
		JSONArray ja = new JSONArray();
		PreparedStatement prep_stmt = null;
		Connection conn = null;
		ResultSet res = null;
		String query = "select * from Hard_Route where Route=? ORDER BY Ord";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CITYBUS", "root", "cz002");
			prep_stmt = conn.prepareStatement(query);
			prep_stmt.setString(1,name);
			res = prep_stmt.executeQuery();
			while (res.next()) {
				JSONObject jo = new JSONObject();
				jo.put("Latitude", res.getLong("Lat"));
				jo.put("Longitude", res.getLong("Lon"));
				jo.put("Order", res.getInt("Ord"));
				ja.put(jo);
			}
}
				catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(conn != null){
					conn.close();
				}
				if(prep_stmt != null){
					prep_stmt.close();
				}
				if(res != null){
					res.close();
				}
			}
			catch(Exception e){
				
			}
			
		}
		return ja;
	}
	public static JSONArray getLiveVehicles(){
		JSONArray ja = new JSONArray();
		PreparedStatement prep_stmt = null;
		Connection conn = null;
		ResultSet res = null;
		String query = "Select dateTime, lat,vlong,spd,dir,sched_status,sched_delta,route_status,route_key,route_name,pattern_key,pattern_name,trip_key,trip_name,name from live_data";
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CITYBUS", "root", "cz002");
			prep_stmt = conn.prepareStatement(query);
			res = prep_stmt.executeQuery();
			while(res.next()){
				JSONObject jo = new JSONObject();
				jo.put("Name",res.getString("name"));
				jo.put("dateTime",res.getString("dateTime"));
				jo.put("Lat",res.getString("lat"));
				jo.put("Long",res.getString("vlong"));
				jo.put("Spd",res.getString("spd"));
				jo.put("Dir",res.getString("dir"));
				jo.put("SchedStatus",res.getString("sched_status"));
				jo.put("SchedDelta",res.getString("sched_delta"));
				jo.put("RouteStatus",res.getString("route_status"));
				jo.put("Route_Key",res.getString("route_key"));
				jo.put("Route_Name",res.getString("route_name"));
				jo.put("Trip_Key",res.getString("pattern_key"));
				jo.put("Trip_Name",res.getString("pattern_name"));
				jo.put("Pattern_Key",res.getString("trip_key"));
				jo.put("Pattern_Name",res.getString("trip_name"));
				ja.put(jo);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(conn != null){
					conn.close();
				}
				if(prep_stmt != null){
					prep_stmt.close();
				}
				if(res != null){
					res.close();
				}
			}
			catch(Exception e){
				
			}
			
		}
		return ja; 
	}
}
