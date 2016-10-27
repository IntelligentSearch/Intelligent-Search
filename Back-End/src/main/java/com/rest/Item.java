//package com.rest;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

public class Item {
	String name;
	String id;
	String station;
	String diningCourt;
	String ingred;
	String date;
	int breakfast;
	int lunch;
	int lateLunch;
	int dinner;
	boolean[] allergens;
	boolean[] userPrefs;
	ArrayList<String> nameNut;
	ArrayList<String> valueNut;
	Parsed p;
	Item(String n, String i, String s,String d,String in,String date,boolean[] uP,Parsed p) {
		this.station = s;
		this.name = n;
		this.id = i;
		this.diningCourt = d;
		this.ingred = in;
		this.date = date;
		this.userPrefs = uP;
		this.p = p;
		
	}
	
	void setBreakfast(boolean b) {
		this.breakfast = (b) ? 1 : 0;
	}

	void setLunch(boolean b) {
		this.lunch = (b) ? 1 : 0;
	}

	void setLLunch(boolean b) {
		this.lateLunch = (b) ? 1 : 0;
	}

	void setDinner(boolean b) {
		this.dinner = (b) ? 1 : 0;
	}
	void setAllergens(boolean[] a){
		this.allergens = a;
	}
	void setNutrients(ArrayList<String> name,ArrayList<String> value){
		this.nameNut = name;
		this.valueNut = value;
	}
	public boolean atTime(String time){
		if(time == null){
			return true;
		}
		switch(time.toLowerCase()){
			case "breakfast":
				if(breakfast == 1){
					return true;
				}
				return false;
			case "lunch":
				if(lunch == 1){
					return true;
				}
				return false;
			case "latelunch":
				if(lateLunch == 1){
					return true;
				}
				return false;
			case "dinner":
				if(dinner == 1){
					return true;
				}
				return false;
		}
		return false;
	}
	public JSONObject processItem() throws JSONException{
		JSONObject jo = new JSONObject();
		//TODO add on to this
		if(allergens == null){
			allergens = new boolean[10];
			for(int i = 0; i < 10;i++){
				allergens[i] = false;
			}
		}
		jo.put("Calories index", this.nameNut.indexOf("Calories"));
		switch(this.p.caloriesFlag){
			case Parsed.CALORIES_EQUAL:
				if(!this.nameNut.contains("Calories") || p.getCalories() != Integer.parseInt(this.valueNut.get(this.nameNut.indexOf("Calories")))){
					return null;
				}
				break;
			case Parsed.CALORIES_GREATER:
				if(!this.nameNut.contains("Calories") || p.getCalories() < Integer.parseInt(this.valueNut.get(this.nameNut.indexOf("Calories")))){
					return null;
				}
				break;
			case Parsed.CALORIES_LESS:
				if(!this.nameNut.contains("Calories") || p.getCalories() > Integer.parseInt(this.valueNut.get(this.nameNut.indexOf("Calories")))){
					return null;
				}
				break;
		}
		if(this.userPrefs != null && !Helper.matchPrefs(this.userPrefs,this.allergens)){
			return null;
		}
		jo.put("DiningCourt",this.diningCourt);
		jo.put("FoodName",this.name);
		jo.put("Ingredients",this.ingred);
		jo.put("Date",this.date);
		jo.put("Station", this.station);
		jo.put("Food_ID", this.id);
		jo.put("Breakfast", this.breakfast);
		jo.put("Lunch", this.lunch);
		jo.put("Dinner", this.dinner);
		jo.put("LateLunch ",this.lateLunch);
		if(this.nameNut != null){
			for(int i = 0; i < this.nameNut.size();i++){
				jo.put(nameNut.get(i), valueNut.get(i));
			}
		}
		jo.put("Eggs",allergens[0]);
		jo.put("Fish",allergens[1]);
		jo.put("Gluten",allergens[2]);
		jo.put("Milk",allergens[3]);
		jo.put("Peanuts",allergens[4]);
		jo.put("Shellfish",allergens[5]);
		jo.put("Soy",allergens[6]);
		jo.put("Tree_nuts",allergens[7]);
		jo.put("Wheat",allergens[8]);
		jo.put("Veg",allergens[9]);
		return jo;	
	}
}
