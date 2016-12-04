package com.rest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class Stop_Handler extends DefaultHandler{
	boolean bus = false;
	boolean name = false;
	boolean time = false;
	boolean key = false;
	boolean stop_key = false;
	JSONObject jo;
	public class Stop{
		String bus;
		String name;
		String time;
		String key;
		JSONArray ja;
		public Stop(JSONArray ja){
			this.ja = ja;
		}
		public String getBus() {
			return bus;
		}
		public void setBus(String bus) {
			this.bus = bus;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		@Override
		public String toString() {
			JSONObject jo1 = new JSONObject();
			jo1.put("RouteName",name);
			jo1.put("TimeTilArrival",time);
			jo1.put("Name",key);
			ja.put(jo1);
			return "Stop [bus=" + bus + ", name=" + name + ", time=" + time + ", key=" + key + "]";
		}
	}
	Stop s;
	public Stop_Handler(JSONObject  jo){
		super();
		this.jo = jo;
		this.jo.put("stops",new JSONArray());
		this.s = new Stop(this.jo.getJSONArray("stops"));
	}

    public void startDocument (){
    	System.out.println("Start document");
    }

    public void endDocument (){
    	System.out.println("End document");
    }
    public void startElement (String uri, String name,
		String qName, Attributes atts){
    	switch(qName.toLowerCase()){
    		case "vehicle":
    			bus = true;
    			break;
    		case "routename":
    			this.name = true;
    			break;
    		case "timetillarrival":
    			time = true;
    			break;
			case "name":
				this.key = true;
			case "stopkey":
				this.stop_key = true;
    		//TODO add any more keys herei
    	}
    }
    public void endElement (String uri, String name, String qName) {
    	switch(qName.toLowerCase()){
    		case "vehicle":
    			bus = false;
    			System.out.println(s);
    			s = new Stop(this.jo.getJSONArray("stops"));
    	}
    }
    public void characters (char ch[], int start, int length)
    {
    	String value = new String(ch,start,length); 
    	if(this.name){
    		this.name = false;
    		s.setName(value);
    	}
    	else if(this.time){
    		this.time = false;
    		s.setTime(value);
    	}
		else if(this.key){
			this.key = false;
			s.setKey(value);
		}
		else if(this.stop_key){
			this.stop_key = false;
			this.jo.put("stop_key",value);
		}
    }
    
	 public static JSONObject parseURL(String url){
			JSONObject jo = new JSONObject();
			try{
				ContentHandler handler = (ContentHandler) new Stop_Handler(jo);
				XMLReader myReader = XMLReaderFactory.createXMLReader();

				myReader.setContentHandler(handler);
				//String temp = getXML(url);
				//System.out.println(temp);
				//temp = temp.replaceAll("[^\\x20-\\x7e]", "");
				HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
	    		conn.setRequestProperty("Accept", "application/xml");
				myReader.parse(new InputSource( conn.getInputStream()));
			}
			catch(SAXException e){
				e.printStackTrace();
			}
			catch(MalformedURLException e){
				e.printStackTrace();
			}
			catch(IOException e){
				e.printStackTrace();
			}
			return jo;
		}
}
