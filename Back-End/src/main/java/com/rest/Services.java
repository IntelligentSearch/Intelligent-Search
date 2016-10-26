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
	@Path("menu/{location}/{date}")
	@Produces("application/json")
	public Response getMenu(@PathParam("location") String location, @PathParam("date") String date) throws JSONException, ClassNotFoundException {	
		String u = "https://api.hfs.purdue.edu/menus/v1/locations/" + location + "/" + date;
		String result = "";

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

		result += json.toString();
/*
		String result = "";
		boolean isToday = false;
		DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		Date dateobj = new Date();
		if(df.format(dateobj).equals(date)){
			 isToday = true;;
		}

		JSONArray j = new JSONArray();
	
		if (isToday) {
			j = Call.getFoodDining(location);
		}
		else {
			j = APICaller.apiCallLocation(date,location);
		}	
		
		result += j.toString();	
*/
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
	
}
