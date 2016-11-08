package com.rest;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.*;
import java.sql.*;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.ProcessBuilder;
import java.util.ArrayList;

@Path("/rest")
public class Services {
		@GET
				@Produces("application/json")
				public Response Services() throws JSONException {
						/* return page that lists different endpoints for REST API*/
						String result = "List of API Endpoints\n" 
								+ "base: http://cs307.cs.purdue.edu:8080/home/cs307/Intelligent-Search/Back-End/target/Back-End/rest\n"
								+ "\n"
								+ "\t- /search/{text}\n"
								+ "\t  GET\n"
								+ "\t  Return JSON result\n"
								+ "\n"
								+ "\t- /create-user/\n"
								+ "\t  POST\n"
								+ "\n"
								+ "\t- /login/\n"
								+ "\t  POST\n"
								+ "\n"
								+ "\t- /update-name/\n"
								+ "\t  POST\n"
								+ "\n"
								+ "\t- /update-password/\n"
								+ "\t  POST\n"
								+ "\n"
								+ "\t- /favorite-item/\n"
								+ "\t  POST\n"
								+ "\n"
								+ "\t- /unfavorite-item/\n"
								+ "\t  POST\n"
								+ "\n"
								+ "\t- /get-pref/\n"
								+ "\t  POST\n"
								+ "\n"
								+ "\t- /set-pref/\n"
								+ "\t  POST\n"
								+ "\n"
								+ "\t- /get-all-routes-stops/\n"
								+ "\t  GET\n"
								+ "\n"
								+ "\t- /get-stops/{route-id}/\n"
								+ "\t  GET\n"
								+ "\n"
								+ "\t- /get-all-routes/\n"
								+ "\t  GET\n"
								+ "\n"
								+ "\t- /start-bus/\n"
								+ "\t  GET\n"
								+ "\n"
								+ "\t- /end-bus/\n"
								+ "\t  GET\n"
								+ "\n"
								+ "\t- /live-stops/{stop-code}\n"
 								+ "\t  GET\n"
 								+ "\n"
								+ "\t- /get-close-stop-by-route/{route-id}/{lon}/{lat}/\n"
								+ "\t  GET\n"
 								+ "\n"
								+ "\t- /get-close-stop/{lon}/{lat}/\n"
                + "\t  GET\n"
                + "\n"
                + "\t- /live_buses\n"
                + "\t  GET\n"
                + "\n";

						return Response.status(200).entity(result).build();
				}

		@GET
				@Path("search/{param}")
				@Produces("application/json")
				public Response startSearch(@PathParam("param") String search) throws JSONException, ClassNotFoundException {
						int userID = 0;
						File infile = new File("/home/cs307/Intelligent-Search/files/search.txt");
						String outfile = "/home/cs307/Intelligent-Search/files/tokens.txt";
						String tokens = "";
						//write to search string to infile
						try {
								PrintWriter writer = new PrintWriter(infile);
								writer.println(search);
								writer.close();
						} catch (FileNotFoundException e) {
								tokens = e.toString();	
						}

						//call Query main function
						String[] args = {infile.getPath()};
						Query.main(args);

						try {	
								//read tokens.txt file
								BufferedReader br = new BufferedReader(new FileReader(outfile));
								tokens = br.readLine();
						} catch (IOException e) {
								tokens += e.toString();
						}

						String result = "";
						//JSONObject j = new JSONObject();

						//USE TOKENS TO DO CALLS
						//j.put("tokens", tokens);
						JSONArray j = Parsed.stringParser(tokens,userID);
						result += j.toString();	
						return Response.status(200).entity(result).build();
				}

		@GET
				@Path("menu/{location}/{date}/{uid}")
				@Produces("application/json")
				public Response getMenu(@PathParam("location") String location, @PathParam("date") String date, @PathParam("uid") String uid) throws JSONException, ClassNotFoundException {	
						String u = "https://api.hfs.purdue.edu/menus/v2/locations/" + location + "/" + date;
						String result = "";

						int id;
						try {
								id = Integer.parseInt(uid);
						} catch (NumberFormatException e) {
								result = "UID " + uid + " invalid number";
								return Response.status(200).entity(result).build();
						}
						ArrayList<String> favs = Helper.getUserFavs(id);

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
						String raw = json.toString();

						String[] pieces = raw.split("\"Name\":\"");
						StringBuilder rebuild = new StringBuilder();
						//rebuild.append(pieces[0]);
						for (int i = 0; i < pieces.length; i++) {
								String line = pieces[i];
								String[] split = line.split("\"",2);
								String item = split[0].toLowerCase();
								boolean fav = false;
								if (favs.contains(item)) {
										split[0] += "\",\"isFavorite\":true";
										fav = true;
								} else {
										//split[0] += "\",\"isFavorite\":false";
								}
								rebuild.append(split[0]);
								if (!fav)	rebuild.append("\"");
								rebuild.append(split[1]);
								if (i != pieces.length - 1) rebuild.append("\"Name\":\"");
						}
						result = rebuild.toString();
						return Response.status(200).entity(result).build();
				}

		@POST
				@Path("create-user/")
				//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
				public Response createUser(
								@FormParam("name") String name,
								@FormParam("first") String first,
								@FormParam("last") String last,
								@FormParam("password") String password) throws ClassNotFoundException {

						JSONObject j = Call.createUser(name, password, first, last, false);
						//String output = "POST:\nCreate User: " + name + " with password " + password;
						String output = j.toString();
						return Response.status(200).entity(output).build();
				}

		@POST
				@Path("login/")
				//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
				public Response login(
								@FormParam("name") String name,
								@FormParam("password") String password) {
						JSONObject jo = new JSONObject();
						JSONObject j1 = Call.login(name, password);
						int userID = -1;
						jo.put("user",j1);
						if((userID = j1.getInt("UserID")) >  0){
								j1 = Call.getUsersPref(userID);
								jo.put("prefs",j1);
								JSONArray j2 = Call.getFavs(userID);
								jo.put("favs",j2);
						}
						//String output = "POST:\nCreate User: " + name + " with password " + password;
						String output = jo.toString();
						return Response.status(200).entity(output)
								.header("Access-Control-Allow-Orgin","*")
								.header("Access-Control-Allow-Methods","GET,PUT,POST,DELETE,OPTIONS")
								.header("Access-Control-Allow-Headers","Origin, Content-Type, Content-Length, Authorization, Content-Length, X-Requested-With, Accept")
								//	.header("Access-Control-Allow-Credentials","true")
								.allow("Options").build();
				}

		@POST
				@Path("update-name/")
				public Response updatename(
								@FormParam("userID") String userID,
								@FormParam("newFirst") String newFirst,
								@FormParam("newLast") String newLast)throws ClassNotFoundException{

						JSONObject j = Call.updateName(Integer.parseInt(userID),newFirst,newLast);
						String output = j.toString();
						return Response.status(200).entity(output).build();
				}

		@POST
				@Path("update-password/")
				public Response updatepass(
								@FormParam("userID") String userID,
								@FormParam("newPassword") String newPass,
								@FormParam("oldPassword") String oldPass)throws ClassNotFoundException{

						JSONObject j = Call.updatePassword(Integer.parseInt(userID),newPass,oldPass);
						String output = j.toString();
						return Response.status(200).entity(output).build();
				}

		@POST
				@Path("favorite-item/")
				public Response favoriteItem(
								@FormParam("userID") String userID,
								@FormParam("itemID") String itemID,
								@FormParam("location") String loc)throws ClassNotFoundException {
						JSONObject j = Call.favItem(Integer.parseInt(userID),itemID,loc);
						String output = j.toString();
						return Response.status(200).entity(output).build();
				}
		@POST
				@Path("unfavorite-item/")
				public Response unfavoriteItem(
								@FormParam("userID") String userID,
								@FormParam("itemID") String itemID,
								@FormParam("location") String loc) throws ClassNotFoundException{
						JSONObject j = Call.unfavItem(Integer.parseInt(userID),itemID,loc);
						String output = j.toString();
						return Response.status(200).entity(output).build();
				}
		@POST
				@Path("set-pref/")
				public Response setPrefs(
								@FormParam("userID") String userID,
								@FormParam("prefs") String newPrefs)throws ClassNotFoundException{
						JSONObject j = Call.updateUsersPref(Integer.parseInt(userID),newPrefs);		
						String output = j.toString();
						return Response.status(200).entity(output).build();
				}
		@GET
				@Path("get-all-routes/")
				public Response getAllRoutes(){
						JSONArray j = Bus_Call.getAllRoutes();
						String output = j.toString();
						return Response.status(200).entity(output).build();
				}
		@GET
				@Path("get-all-routes-stops/")
				public Response getAllRoutesStops(){
						JSONArray j = Bus_Call.getStops();
						String output = j.toString();
						return Response.status(200).entity(output).build();		
				}
		@GET
				@Path("get-stops/{id}")
				public Response getRouteStops(@PathParam("id")String id){
						JSONArray j = Bus_Call.getRouteStops(id);
						String output = j.toString();
						return Response.status(200).entity(output).build();
				}
		@GET
				@Path("end-bus/")	
				public Response end(){
						String output ="okay";
						PreparedStatement prep_stmt = null;
						Connection conn = null;
						try{
								String query = "UPDATE reference set count = count - 1 where count > 0";
								Class.forName("com.mysql.jdbc.Driver");
								conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CITYBUS", "root", "cz002");
								prep_stmt = conn.prepareStatement(query);
								prep_stmt.executeUpdate();

						}
						catch(Exception e){
								output= e.toString();
						}
						finally{
								try{
										if(conn != null){
												conn.close();
										}
										if(prep_stmt!= null){
												prep_stmt.close();
										}
								}
								catch(Exception e){

								}
						}
						return Response.status(200).entity(output).build();
				}
		@GET
				@Path("start-bus/")
				public Response start(){
						String output ="okay";
						PreparedStatement prep_stmt = null;
						Connection conn = null;
						ResultSet res = null;
						try{
								String query = "Select count,pid from reference";
								Class.forName("com.mysql.jdbc.Driver");
								conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CITYBUS", "root", "cz002");
								prep_stmt = conn.prepareStatement(query);
								res = prep_stmt.executeQuery();
								boolean startProcess = false;
								//deals with if not there
								if(!res.next()){	
										startProcess = true;
										prep_stmt.close();
										query = "Insert INTO VALUES (0,0)";
										prep_stmt = conn.prepareStatement(query);
										prep_stmt.executeUpdate();

								}
								else{
										if(res.getInt("count") == 0 && res.getInt("pid") == -1){
												startProcess = true;	
										}
								}
								res.close();
								prep_stmt.close();
								if(!startProcess){
										query = "UPDATE reference set count = count + 1";
										prep_stmt = conn.prepareStatement(query);
										prep_stmt.executeUpdate();
								}
								else{	
										output = "start process";
										ProcessBuilder pb = new ProcessBuilder("/home/cs307/Intelligent-Search/Scripts/Vehicle/run.sh");
										pb=pb.directory(new File("/home/cs307/Intelligent-Search/Scripts/Vehicle/"));
										pb.start();
								}
						}
						catch(Exception e){
								output= e.toString();
						}
						finally{
								try{
										if(conn != null){
												conn.close();
										}
										if(prep_stmt!= null){
												prep_stmt.close();
										}
								}
								catch(Exception e){

								}
						}
						return Response.status(200).entity(output).build();
				}
		@GET
				@Path("live-stops/{stop}")
				@Produces("application/json")
				public Response live_stops(@PathParam("stop") String code){
						JSONObject jo = Stop_Handler.parseURL("http://myride.gocitybus.com/161027Purdue/Default1.aspx?pwd=cs307-102716&code="+code);
						String output = jo.toString();
						return Response.status(200).entity(output).build();	
				}
		@GET
				@Path("live-buses/")
				@Produces("application/json")
				public Response lives_busses(){
						JSONArray ja = Bus_Call.getLiveVehicles();
						String output = ja.toString();
						return Response.status(200).entity(output).build();
				}
		@GET
				@Path("get-close-stop-by-route/{route-id}/{lon}/{lat}")
				@Produces("application/json")
				public Response getClosestStopByRoute(@PathParam("route-id") String route_id , @PathParam("lon") float lon , @PathParam("lat") float lat){
						JSONObject jo = Bus_Call.getClosestStopByRouteID(lon,lat,route_id);
						String output = jo.toString();
						return Response.status(200).entity(output).build();
				}
		@GET
				@Path("get-close-stop/{lon}/{lat}")
				@Produces("application/json")
				public Response getClosestStop(@PathParam("lon") float lon,@PathParam("lat") float lat){
					JSONObject jo = Bus_Call.getClosestStop(lon,lat);
					String output = jo.toString();
					return Response.status(200).entity(output).build();
				}
}
