#pragma once
#include <string>
/* Services.cpp will call the sql server and get the data from it, 
*  it will then, if needed pass the data to the data processing class 
*  to process the data as needed and return the data in a json format*/
//gets food for dinning court on specific day
extern std::string getFoodDin(std::string dinningCourt);
//includes date(MM/DD/YY of previous call checks if it is today or not and makes correct calls
extern std::string getFoodDin(std::string dinningCourt, std::string date);
//checks if item is being served by dinning court
extern std::string getItemDin(std::string* dinningCourt, std::string item);
extern std::string getItemDin(std::string dinningCourt, std::string item, std::string date);
//checks for item in all dinning courts
extern std::string getItem(std::string item);
extern std::string getItem(std::string item, std::string date);
//checks if user can login in or not
extern std::string login(std::string user, std::string pass);
//allows user to change preferences by given pref[] array
extern std::string changePref(std::string pref[]);
//adds to user favorite food list by pinning item
extern std::string pinFavFood(int items[]);
//adds to users favorite dinning court
extern std::string pinFavDin(std::string name);
//gets a list of saved preferences
extern std::string getSavedPref();
//sets up connections
extern void setUpConn();
