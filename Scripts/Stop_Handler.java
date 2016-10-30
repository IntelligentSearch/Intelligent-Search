//package com.rest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class Stop_Handler extends DefaultHandler{
	boolean bus = false;
	boolean name = false;
	boolean time = false;
	boolean key = false;
	public class Stop{
		String bus;
		String name;
		String time;
		String key;
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
			return "Stop [bus=" + bus + ", name=" + name + ", time=" + time + ", key=" + key + "]";
		}
	}
	Stop s;
	public Stop_Handler(){
		super();
		this.s = new Stop();
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
    		case "bus":
    			bus = true;
    			break;
    		case "routename":
    			this.name = true;
    			break;
    		case "timetillarrival":
    			time = true;
    			break;
    		//TODO add any more keys here
    	}
    }
    public void endElement (String uri, String name, String qName) {
    	switch(qName.toLowerCase()){
    		case "bus":
    			bus = false;
    			//TODO process Data
    			System.out.println(s);
    			s = new Stop();
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
    }
    
	 public static void pasreURL(String url){
			try{
				ContentHandler handler = (ContentHandler) new Stop_Handler();
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
		}

}
