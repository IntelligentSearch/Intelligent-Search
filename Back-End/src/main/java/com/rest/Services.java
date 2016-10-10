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
					+ "\t- /favorite-item/\n"
					+ "\t  POST\n"
					+ "\n";
			return Response.status(200).entity(result).build();
		}

	@GET
	@Path("search/{param}")
	@Produces("application/json")
	public Response startSearch(@PathParam("param") String search) throws JSONException {
		String result = "";
		JSONObject j = new JSONObject();
		j.put("Input", search);
		
		result += "Outside if conditional\n";
		if (search.equals("Wiley")) { //get wiley menu
			result += "Test Wiley query\n";
			JSONArray ja = Call.getFoodDining(search);
			result += "array: " + ja.toString();
			j.put("Menu", ja);
		}
			
		result += j.toString();	
		return Response.status(200).entity(result).build();
	}

	@POST
	@Path("create-user/")
	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createUser(
		@FormParam("name") String name,
		@FormParam("password") String password) {

        String output = "POST:\nCreate User: " + name + " with password " + password;
        return Response.status(200).entity(output).build();
	}

	@POST
	@Path("favorite-item/")
	public Response favoriteItem(String msg) {
        String output = "POST:\nFavorite Item: " + msg;
        return Response.status(200).entity(output).build();
	}
}
