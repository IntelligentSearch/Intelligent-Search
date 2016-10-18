//package com.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Parsed{
	static final String COURT = "DINING_COURT=";
	static final String DAY = "MEAL_DAY=";
	static final String TIME = "MEAL_TIME=";//mm-dd-yyyy
	static final String NAME = "ITEM_NAME=";
	public Parsed(){
		this.court = null;
		this.hascourt = false;
		this.day = null;
		this.hasday = false;
		this.time = null;
		this.hastime = false;
		this.name = null;
		this.hasname = false;
	}
	String court;
	boolean hascourt;
	public void setCourt(String court){
		hascourt = true;
		this.court = court;
	}
	public boolean hasCourt(){
		return this.hascourt;
	}
	public String getCourt(){
		return this.court;
	}
	String day;
	boolean hasday;
	public void setDay(String day){
		this.day = day;
		this.hasday = true;
	}
	public String getDay(){
		return this.day;
	}
	public boolean hasDay(){
		return this.hasday;
	}
	String time;
	boolean hastime;
	public void setTime(String time){
		this.time = time;
		this.hastime = true;
	}
	public String getTime(){
		return this.time;
	}
	public boolean hasTime(){
		return this.hastime;
	}
	String name;
	boolean hasname;
	public void setName(String name){
		this.name = name;
		this.hasname = true;
	}
	public String getName(){
		return this.name;
	}
	public boolean hasName(){
		return this.hasname;
	}
	public String toString(){
		return court+","+time+","+day+","+name+"\n";
	}
	public static JSONArray stringParser(String x, int userID) throws  ClassNotFoundException, JSONException{
		x=x+";";
		int index;
		Parsed p = new Parsed();
		while((index = x.indexOf(';')) != -1){
			
			String temp = x.substring(0,index);
			String value = temp.substring(temp.indexOf('=')+1);
			temp = temp.substring(0,temp.indexOf('=')+1);
			
			switch(temp){
				case COURT:
					p.setCourt(value);
					break;
				case DAY:
					p.setDay(value);
					break;
				case TIME:
					p.setTime(value);
					break;
				case NAME:
					p.setName(value);
					break;
			}
			x = x.substring(index+1);
		}
		return decider(p,x,userID);
	}
	public static boolean isToday(String date){
		DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		Date dateobj = new Date();
		if(df.format(dateobj).equals(date)){
			 return true;
		 }
		return false; 
	}
	
	public static JSONArray decider(Parsed p,String x,int userID) throws JSONException,ClassNotFoundException{
		JSONArray empty = new JSONArray();
		JSONObject e = new JSONObject();
		e.put("Error", "Feature not Supported");
		e.put("TOKEN",x);
		e.put("Day", p.getDay());
		e.put("Court", p.getCourt());
		e.put("name", p.getName());
		e.put("time", p.getTime());
		empty.put(e);

		if(p.hasDay()){
			//NOT CURRENT DAY
			if(!isToday(p.getDay())){
				if(!p.hasCourt() && !p.hasName() && !p.hasTime()){
					return APICaller.apiCallAll(p.getDay(),userID);
				}
				else if(p.hasCourt() && !p.hasName() && !p.hasTime()){
					return APICaller.apiCallLocation(p.getDay(),p.getCourt(),userID);
				}
				else if(p.hasCourt() && p.hasName() && !p.hasTime()){
					return APICaller.apiCallLocItem(p.getDay(),p.getCourt(),p.getName(),userID);
				}
				else if(!p.hasCourt() && p.hasName() && !p.hasTime()){
					return APICaller.apiCallItem(p.getDay(),p.getName(),userID);
				}
				else if (!p.hasCourt() && !p.hasName() && p.hasTime()){
					return APICaller.apiCallAtTime(p.getDay(), userID, p.getTime());
				}
				else if (!p.hasCourt() && p.hasName() && p.hasTime()){
					return APICaller.apiCallItemAtTime(p.getDay(),p.getName(),p.getTime(),userID);
				}
				else if (p.hasCourt() && p.hasName() && p.hasTime()){
					return APICaller.apiCallLocItemAtTime(p.getDay(),p.getCourt(),p.getName(),p.getTime(),userID);
				}
				else if (p.hasCourt() && !p.hasName() && !p.hasTime()){
					return APICaller.apiCallLocAtTime(p.getDay(),p.getCourt(),p.getTime(),userID);
				}
				return empty;
			}
		}
		//TODAY

		//searching dining court options
		//TOKEN DINING COURT, TOKEN TODAY
		if(p.hasCourt() && !p.hasName() && !p.hasTime()){
			System.out.println("Dining");
			return Call.getFoodDining(p.getCourt(),userID);
		}

		//searching item
		//TOKEN ITEM, TOKEN TODAY
		else if(!p.hasCourt() && p.hasName() && !p.hasTime()){
			System.out.println("Item");
			return Call.getItem(p.getName(),userID);
		}

		//has item and dining Court
		//TOKEN ITEM, TOKEN DINING COURT
		else if(p.hasCourt() && p.hasName() && !p.hasTime()){
			System.out.println("Item at court");
			return Call.getItemDin(p.getCourt(), p.getName(),userID);
		}
		else if(!p.hasCourt() && !p.hasName() && !p.hasTime()){
			System.out.println("just date");
			return Call.getAll(userID);
		}
		else if(!p.hasCourt() && !p.hasName() && p.hasTime()){
			System.out.println("time");
			return Call.getAtTime(p.getTime(),userID);
		}
		else if(!p.hasCourt() && p.hasName() && p.hasTime()){
			System.out.println("time name");
			return Call.getItemAtTime(p.getTime(),p.getName(),userID);
		}
		else if(p.hasCourt() && p.hasName() && p.hasTime()){
			System.out.println("time name item");
			return Call.getItemDinAtTime(p.getTime(),p.getCourt(),p.getName(),userID);
		}
		else if(p.hasCourt() && !p.hasName() && p.hasTime()){
			System.out.println("time court");
			return Call.getDinAtTime(p.getTime(),p.getCourt(),userID);
		}
		return empty;
	}
}