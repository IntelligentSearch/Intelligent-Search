#pragma once
#include <string.h>
#include <string>
#include <stdlib.h>
#include <stdio.h> 
class User {
	//where if true the user does not want{Eggs,Fish,Gluten,Milk,Peanuts,Shellfish,Soy,Tree Nuts,Wheat,Veg}
	bool pref[10];
	std::string user;
	int userId;
};