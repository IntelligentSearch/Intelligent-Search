package com.rest;

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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

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
					+ "\t- /favorites/\n"
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
	public Response startSearch(@PathParam("param") String search) throws JSONException {
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
		JSONObject j = new JSONObject();

		//USE TOKENS TO DO CALLS
		j.put("tokens", tokens);
			
		result += j.toString();	
		return Response.status(200).entity(result).build();
	}

	@POST
	@Path("create-user/")
	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createUser(
		@FormParam("name") String name,
		@FormParam("first") String first,
		@FormParam("last") String last,
		@FormParam("password") String password) {

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

		JSONObject j = Call.login(name, password);

        //String output = "POST:\nCreate User: " + name + " with password " + password;
		String output = j.toString();
        return Response.status(200).entity(output).build();
	}
	@POST
    @Path("get-pref/")
    //@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getPref(
        @FormParam("userID") String userID) {

        JSONObject j = Call.getUsersPref(Integer.parseInt(userID));

        String output = j.toString();
        return Response.status(200).entity(output).build();
 	}

	@POST
    @Path("update-name/")
	public Response updatename(
		@FormParam("userID") String userID,
		@FormParam("newFirst") String newFirst,
		@FormParam("newLast") String newLast){
		
		JSONObject j = Call.updateName(Integer.parseInt(userID),newFirst,newLast);
		String output = j.toString();
        return Response.status(200).entity(output).build();
	}
	
	@POST
    @Path("update-password/")
	public Response updatepass(
        @FormParam("userID") String userID,
        @FormParam("newPassword") String newPass){

        JSONObject j = Call.updatePassword(Integer.parseInt(userID),newPass);
        String output = j.toString();
        return Response.status(200).entity(output).build();
    }

	@POST
	@Path("favorite-item/")
	public Response favoriteItem(
		@FormParam("userID") String userID,
		@FormParam("itemID") String itemID,
		@FormParam("location") String loc) {
        JSONObject j = Call.favItem(Integer.parseInt(userID),itemID,loc);
        String output = j.toString();
        return Response.status(200).entity(output).build();
	}
	@POST
    @Path("unfavorite-item/")
    public Response unfavoriteItem(
        @FormParam("userID") String userID,
        @FormParam("itemID") String itemID,
        @FormParam("location") String loc) {
        JSONObject j = Call.unfavItem(Integer.parseInt(userID),itemID,loc);
        String output = j.toString();
        return Response.status(200).entity(output).build();
    }
	
	@POST
    @Path("favorites/")
	public Response getFavoriteItem(
        @FormParam("userID") String userID){
		JSONArray j = Call.getFavs(Integer.parseInt(userID));
        String output = j.toString();
        return Response.status(200).entity(output).build();
	}
}
