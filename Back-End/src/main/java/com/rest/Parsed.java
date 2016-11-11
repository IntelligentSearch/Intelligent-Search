package com.rest;

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
	static final String CALORIE_FLAG = "MODIFIER=";
	static final String CALORIES = "CALORIES=";
	static final int CALORIES_LESS = 1;
	static final int CALORIES_EQUAL = 2;
	static final int CALORIES_GREATER = 3;
	int userID = 0;
	public Parsed(int userID){
		this.court = null;
		this.userID = userID;
		this.hascourt = false;
		this.day = null;
		this.hasday = false;
		this.time = null;
		this.hastime = false;
		this.name = null;
		this.hasname = false;
		this.numCalories =0;
		this.caloriesFlag = CALORIES_LESS;
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
	int caloriesFlag;
	int numCalories;
	public void setCalories(int numCalories){
		
		this.numCalories = numCalories;
	}
	public void setCalorieFlag(int flag){
		this.caloriesFlag = flag;
	}
	public int getCalories(){
		return this.numCalories;
	}
	public int getCaloriesFlag(){
		return this.caloriesFlag;
	}
	public int getUserID(){
		return this.userID;
	}
	public String toString(){
		String type;
		switch(this.caloriesFlag){
			case CALORIES_LESS:
				type = "less than";
				break;
			case CALORIES_EQUAL:
				type = "equal";
				break;
			case CALORIES_GREATER:
				type = "greater than";
				break;
			default:
				type = "none";
				break;
		}
		return court+","+time+","+day+","+name+","+type+" "+numCalories+"Calories\n";
	}
	public static JSONArray stringParser(String x, int userID) throws  ClassNotFoundException, JSONException{
		x=x+";";
		int index;
		Parsed p = new Parsed(userID);
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
				case CALORIE_FLAG:
					int flag = 0;
					switch(value){
						case "<":
							flag = CALORIES_LESS;
						case ">":
							flag = CALORIES_GREATER;
						case "=":
							flag = CALORIES_EQUALS;
					}
					p.setCalorieFlag(flag);
				case CALORIES:
					
					//p.setName(value);
					p.setCalories(Integer.parseInt(value));
					
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
		e.put("calorieflag", p.getCaloriesFlag());
		e.put("calories", p.getCalories());
		empty.put(e);

		if(p.hasDay()){
			//NOT CURRENT DAY
			if(!isToday(p.getDay())){
				return APICaller.apiCall(p);
				/*if(!p.hasCourt() && !p.hasName() && !p.hasTime()){
					return APICaller.apiCallAll(p);
				}
				else if(p.hasCourt() && !p.hasName() && !p.hasTime()){
					return APICaller.apiCallLocation(p);
				}
				else if(p.hasCourt() && p.hasName() && !p.hasTime()){
					return APICaller.apiCallLocItem(p);
				}
				else if(!p.hasCourt() && p.hasName() && !p.hasTime()){
					return APICaller.apiCallItem(p);
				}
				else if (!p.hasCourt() && !p.hasName() && p.hasTime()){
					return APICaller.apiCallAtTime(p);
				}
				else if (!p.hasCourt() && p.hasName() && p.hasTime()){
					return APICaller.apiCallItemAtTime(p);
				}
				else if (p.hasCourt() && p.hasName() && p.hasTime()){
					return APICaller.apiCallLocItemAtTime(p);
				}
				else if (p.hasCourt() && !p.hasName() && p.hasTime()){
					return APICaller.apiCallLocAtTime(p);
				}
				return empty;
				*/
			}
		}
		//TODAY

		//searching dining court options
		//TOKEN DINING COURT, TOKEN TODAY
		if(p.hasCourt() && !p.hasName() && !p.hasTime()){
			System.out.println("Dining");
			return Call.getFoodDining(p.getCourt(),userID,p);
		}

		//searching item
		//TOKEN ITEM, TOKEN TODAY
		else if(!p.hasCourt() && p.hasName() && !p.hasTime()){
			System.out.println("Item");
			return Call.getItem(p.getName(),userID,p);
		}

		//has item and dining Court
		//TOKEN ITEM, TOKEN DINING COURT
		else if(p.hasCourt() && p.hasName() && !p.hasTime()){
			System.out.println("Item at court");
			return Call.getItemDin(p.getCourt(), p.getName(),userID,p);
		}
		else if(!p.hasCourt() && !p.hasName() && !p.hasTime()){
			System.out.println("just date");
			return Call.getAll(userID,p);
		}
		else if(!p.hasCourt() && !p.hasName() && p.hasTime()){
			System.out.println("time");
			return Call.getAtTime(p.getTime(),userID,p);
		}
		else if(!p.hasCourt() && p.hasName() && p.hasTime()){
			System.out.println("time name");
			return Call.getItemAtTime(p.getTime(),p.getName(),userID,p);
		}
		else if(p.hasCourt() && p.hasName() && p.hasTime()){
			System.out.println("time name item");
			return Call.getItemDinAtTime(p.getTime(),p.getCourt(),p.getName(),userID,p);
		}
		else if(p.hasCourt() && !p.hasName() && p.hasTime()){
			System.out.println("time court");
			return Call.getDinAtTime(p.getTime(),p.getCourt(),userID,p );
		}
		return empty;
	}
}
