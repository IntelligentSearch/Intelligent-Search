package com.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.*;
//import java.sql.Connection;
//import java.sql.Statement;
//import java.sql.ResultSet;
//import java.sql.DriverManager;

@Path("/examples/sql")
public class SQL {
	@GET
		@Produces("application/json")
		public Response SQL() throws JSONException {
			int count = -1;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DINING", "root", "cz002");

				Statement stmt = conn.createStatement();
				String query = "SELECT COUNT(*) FROM Daily;";
         		System.out.println(query);
         		ResultSet rs = stmt.executeQuery(query);

         		while (rs.next()) {
            		count = rs.getInt("COUNT(*)");
         		}
         		
				rs.close();
         		stmt.close();
				conn.close();
			} catch (Exception e) {
				String result = "@Produces(\"application/json\") \nMYSQL Exception\n" + e.getMessage();
				return Response.status(200).entity(result).build();
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("Count", count);

			String result = "@Produces(\"application/json\") \n" + jsonObject;
			return Response.status(200).entity(result).build();
		}

	@Path("{f}")
		@GET
		@Produces("application/json")
		public Response JSON_test(@PathParam("f") String f) throws JSONException {

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("Input", f);

			String result = "@Produces(\"application/json\") \n" + jsonObject;
			return Response.status(200).entity(result).build();
		}
}
