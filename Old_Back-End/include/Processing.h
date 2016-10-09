#pragma once
#include <string>
//takes an array of size 10 and sees if the preferences given matched the users preferences.
bool matchesPref(bool pref[]);
//takes an item from dinning and sets it up to be returned in json format to the front end
std::string processItem(std::string dinningCourt, std::string name, std::string station, int id,bool breakfast, bool lunch, bool dinner);
//logs the user out
int logout();
//logs the user in and sets up a new user object with its pref and favorites
int login(std::string user,int userid);
std::string processNutrition(std::string name[],int value[], int size);
std::string processPref(bool pref[]);