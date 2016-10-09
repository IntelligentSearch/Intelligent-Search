package com.rest;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

public class Item {
	String name;
	String id;
	String station;
	String diningCourt;
	int breakfast;
	int lunch;
	int lateLunch;
	int dinner;
	boolean[] allergens;
	ArrayList<String> nameNut;
	ArrayList<String> valueNut;
	
	Item(String n, String i, String s,String d) {
		this.station = s;
		this.name = n;
		this.id = i;
		this.diningCourt = d;
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
	public JSONObject processItem() throws JSONException{
		JSONObject jo = new JSONObject();
		//TODO add on to this
		jo.put("DiningCourt",this.diningCourt);
		jo.put("FoodName",this.name);
		jo.put("Station", this.station);
		jo.put("Food_ID", this.id);
		jo.put("Breakfast", this.breakfast);
		jo.put("Lunch", this.lunch);
		jo.put("Dinner", this.dinner);
		for(int i = 0; i < this.nameNut.size();i++){
			jo.put(nameNut.get(i), valueNut.get(i));
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
