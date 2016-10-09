package com.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;

@Path("/examples/json")
public class JSON {
		@GET
				@Produces("application/json")
				public Response JSON_test() throws JSONException {

						JSONObject jsonObject = new JSONObject();
						jsonObject.put("Key1", "Value1");
						jsonObject.put("Key2", "Value2");

						String result = "@Produces(\"application/json\") \n\n" + jsonObject;
						return Response.status(200).entity(result).build();
				}

		@Path("{f}")
				@GET
				@Produces("application/json")
				public Response JSON_test(@PathParam("f") String f) throws JSONException {

						JSONObject jsonObject = new JSONObject();
						jsonObject.put("Value", f);
						
						String result = "@Produces(\"application/json\") \n\n" + jsonObject;
						return Response.status(200).entity(result).build();
				}
}
