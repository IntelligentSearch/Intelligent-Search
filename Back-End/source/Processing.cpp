#include "Processing.h"
#include "string"
#include "Services.h"
#include "User.h"
using namespace std;
//takes an array of size 10 and sees if the preferences given matched the users preferences.
bool matchesPref(bool pref[]) {
	return false;
}
//takes an item from dinning and sets it up to be returned in json format to the front end
string processItem(string dinningCourt,string name, string station, int id,bool breakfast, bool lunch, bool dinner){
	//TODO put info in json format
	return "hi";
}
int logout() {
	return 0;
}
int login(string user, int userid){
	return -1;
}
string processNutrition(string name[], int value[], int size) {
	return NULL;
}
string processPref(bool pref[]) {
	return NULL;
}