
#include <string.h>
#include <string>
#include <stdlib.h>
#include <stdio.h>
#include <ctime>
#include <iostream>
#include <sstream>
#include <memory>
#include <stdexcept>
#include "Services.h"
#include "mysql_driver.h"
#include "mysql_connection.h"
#include <cppconn/driver.h>
#include <cppconn/exception.h>
#include <cppconn/resultset.h>
#include <cppconn/statement.h>
#include <cppconn/prepared_statement.h>
#include <cppconn/resultset.h>
#include <cppconn/metadata.h>
#include <cppconn/resultset_metadata.h>
#include <cppconn/exception.h>
#include <cppconn/warning.h>
#include "Processing.h"

//uses the date(month/day/year) to see if it is today
#define HOST		"cs307.cs.purdue.edu" 
#define USERNAME	"root"
#define PASSWORD	"cz002"
#define DINN_DB		"DINING"
using namespace std;
struct sql_connection{
	sql::mysql::MySQL_Driver *driver;
}sql_call;

int checkDate(string date);

extern void setUpConn() {
	//this call is not thread safe
	sql_call.driver = sql::mysql::get_driver_instance();
}
//gets food for dinning court on specific day
string getFoodDin(string dinningCourt) {
	try {
		sql::Connection *con;
		sql::PreparedStatement *prep_stmt;
		sql::Statement *stmt;
		sql::ResultSet *res;
		//sets up connection
		cout << "does it connect\n";
		con = sql_call.driver->connect(HOST, USERNAME, PASSWORD);
		cout << "never connects";
		//sees if it connects succesfully
		if (con->isValid()) {
			cout << "connectio is closed";
		}
		else {
			cout << "connection is not valid";
			return "ERROR";
		}
		//sets correct database to be used
		stmt = con->createStatement();
		stmt->execute("USE " DINN_DB);
		string query = "SELECT D.ITEM_ID D.STATION,D.BREAKFAST,D.LUNCH,D.DINNER,I.NAME,A.EGGS,A.FISH,A.GLUTEN,A.MILK,A.PEANUNTS,A.SHELFISH,A.SOY,A.TREENUTS,A.WHEAT,A.VEGETARIAN"
			"FROM DAILY_TABLE AS D"
			"INNER JOIN ITEM_TABLE AS I"
			"ON D.ITEM_ID = I.ITEM_ID"
			"INNER JOIN ALLERGEN_TABLE AS N"
			"ON D.ITEM_ID = N.ITEM_ID"
			"WHERE D.LOCATION = ?";
		//runs query to find items
		prep_stmt = con->prepareStatement(query);
		prep_stmt->setString(1, dinningCourt);
		res = prep_stmt->executeQuery();
		while (res->next()) {
			bool pref[10] = { res->getBoolean(sql::SQLString("EGGS")) , res->getBoolean(sql::SQLString("FISH")),
				res->getBoolean(sql::SQLString("GLUTEN")),res->getBoolean(sql::SQLString("MILK")),
				res->getBoolean(sql::SQLString("PEANUNTS")),res->getBoolean(sql::SQLString("SHELFISH")),
				res->getBoolean(sql::SQLString("SOY")),res->getBoolean(sql::SQLString("TREENUTS")),
				res->getBoolean(sql::SQLString("WHEAT")),res->getBoolean(sql::SQLString("VEGETARIAN")) };
			//checks if the item matches the user preferences if it does add it to the return document
			if (matchesPref(pref)) {
				string name = res->getString(sql::SQLString("NAME"));
				string station = res->getString(sql::SQLString("STATION"));
				int id = res->getInt(sql::SQLString("ITEM_ID"));
				bool breakfast = res->getBoolean(sql::SQLString("BREAKFAST"));
				bool lunch = res->getBoolean(sql::SQLString("LUNCH"));;
				bool dinner = res->getBoolean(sql::SQLString("DINNER"));
				processItem(dinningCourt, name, station, id, breakfast, lunch, dinner);
			}

		}
		delete res;
		delete prep_stmt;
		delete con;
		delete stmt;
	}
	catch (sql::SQLException &e) {
		cout << "# ERR: SQLException in " << __FILE__;
		cout << "(" << __FUNCTION__ << ") on line " << __LINE__ << endl;
		cout << "# ERR: " << e.what();
		cout << " (MySQL error code: " << e.getErrorCode();
		cout << ", SQLState: " << e.getSQLState() << " )" << endl;

	}

	
	return "hello";
}

string getFoodDin(string dinningCourt, ::string date) {
	if (checkDate(date)) {
		return getFoodDin(dinningCourt);
	}
	//TODO make api call
	return NULL;
}
/*checks if item is being served by dinning court 
 * returns "NO" if there is not one or item id, 
 * dinning court and what meal it is being served 
 * for as json*/
string getItemDin(string dinningCourt, string item) {
	sql::Connection *con;
	sql::PreparedStatement *prep_stmt;
	sql::Statement *stmt;
	sql::ResultSet *res;
	//sets up connection
	con = sql_call.driver->connect(HOST, USERNAME, PASSWORD);
	cout << "never connects";
	//sees if it connects succesfully
	if (con->isValid()) {
		cout << "connectio is closed";
	}
	else {
		cout << "connection is not valid";
		return "ERROR";
	}
	//sets correct database to be used
	stmt = con->createStatement();
	stmt->execute("USE " DINN_DB);
	string query = "SELECT I.ITEM_ID, I.NAME,D.STATION,D.BREAKFAST,D.LUNCH,D.DINNER"
		"FROM DAILY_TABLE AS D"
		"JOIN ITEM_TABLE AS I"
		"ON D.ITEM_ID = I.ITEM_ID"
		"WHERE I.NAME = ? AND D.LOCATION = ?";
	prep_stmt = con->prepareStatement(query);
	prep_stmt->setString(1, item);
	prep_stmt->setString(2,dinningCourt);
	res = prep_stmt->executeQuery();
	string temp;
	if (res->next()) {
		string name = res->getString(sql::SQLString("NAME"));
		string station = res->getString(sql::SQLString("STATION"));
		int id = res->getInt(sql::SQLString("ITEM_ID"));
		bool breakfast = res->getBoolean(sql::SQLString("BREAKFAST"));
		bool lunch = res->getBoolean(sql::SQLString("LUNCH"));;
		bool dinner = res->getBoolean(sql::SQLString("DINNER"));
		processItem(dinningCourt, name, station, id, breakfast, lunch, dinner);
	}
	else {
		temp = "NO";
	}
	delete res;
	delete stmt;
	delete prep_stmt;
	delete con;
	return NULL;
}

string getItemDin(string dinningCourt, string item, string date) {
	if (checkDate(date)) {
		return getItemDin(dinningCourt,item);
	}
	//Todo make api call 
	return NULL;
}
//checks for item in all dinning courts returns "NO" if there is none, else a list of dinning courts and times in json.
string getItem(string item) {
	sql::Connection *con;
	sql::PreparedStatement *prep_stmt;
	sql::Statement *stmt;
	sql::ResultSet *res;
	//sets up connection
	con = sql_call.driver->connect(HOST, USERNAME, PASSWORD);
	cout << "never connects";
	//sees if it connects succesfully
	if (con->isValid()) {
		cout << "connectio is closed";
	}
	else {
		cout << "connection is not valid";
		return "ERROR";
	}
	//sets correct database to be used
	stmt = con->createStatement();
	stmt->execute("USE " DINN_DB);
	string query = "SELECT D.LOCATION, D.ITEM_ID, I.NAME,D.STATION,D.BREAKFAST,D.LUNCH,D.DINNER"
		"FROM DAILY_TABLE AS D"
		"WHERE D.ITEM_ID = (SELECT I.ITEM_ID"
		"FROM ITEM_TABLE AS I"
		"WHERE I.NAME = ?";
	prep_stmt = con->prepareStatement(query);
	prep_stmt->setString(1, item);
	res = prep_stmt->executeQuery();
	while(res->next()) {
		string name = item;
		string dinningCourt = res->getString(sql::SQLString("LOCATION"));
		string station = res->getString(sql::SQLString("STATION"));
		int id = res->getInt(sql::SQLString("ITEM_ID"));
		bool breakfast = res->getBoolean(sql::SQLString("BREAKFAST"));
		bool lunch = res->getBoolean(sql::SQLString("LUNCH"));;
		bool dinner = res->getBoolean(sql::SQLString("DINNER"));
		processItem(dinningCourt, name, station, id, breakfast, lunch, dinner);
	}
	delete res;
	delete stmt;
	delete prep_stmt;
	delete con;
	return NULL;
}
string getItem(string item, string  date) {
	if (checkDate(date)) {
		return getItem(item);
	}
	//TODO MAKE API CALL
	return	NULL;
}
/*checks if username and password match return to allow them to entere or return to say that they are wrong*/
string login(string user, string pass) {
	sql::Connection *con;
	sql::PreparedStatement *prep_stmt;
	sql::Statement *stmt;
	sql::ResultSet *res;
	//sets up connection
	con = sql_call.driver->connect(HOST, USERNAME, PASSWORD);
	cout << "never connects";
	//sees if it connects succesfully
	if (con->isValid()) {
		cout << "connectio is closed";
	}
	else {
		cout << "connection is not valid";
		return "ERROR";
	}
	//sets correct database to be used
	stmt = con->createStatement();
	stmt->execute("USE " DINN_DB);
	string query = "SELECT USERID"
		"FROM USER_TABLE"
		"WHERE USERNAME = ? AND PASSWORD = ?";
	prep_stmt = con->prepareStatement(query);
	prep_stmt->setString(1, user);
	prep_stmt->setString(2, pass);
	res = prep_stmt->executeQuery();
	string ret;
	if (res->next()) {
		int userId = res->getInt(sql::SQLString("USERID"));
		login(user, userId);
		ret = "SUCCESS";
	}
	else {
		ret = "FAILED";
	}
	delete res;
	delete stmt;
	delete prep_stmt;
	delete con;
	return ret;
}
string changePref(string pref[]) {
	return NULL;
}
string pinFavFood(int items[]) {
	//TODO ask him to change this 
	return NULL;
}
string pinFavDin(string name) {
	return NULL;
}
string getSavedPref() {
	//TODO have him make this table
	return NULL;
}
//gets Nutrition info from the id given
string getNutrition(int id) {
	sql::Connection *con;
	sql::PreparedStatement *prep_stmt;
	sql::Statement *stmt;
	sql::ResultSet *res;
	//sets up connection
	con = sql_call.driver->connect(HOST, USERNAME, PASSWORD);
	cout << "never connects";
	//sees if it connects succesfully
	if (con->isValid()) {
		cout << "connectio is closed";
	}
	else {
		cout << "connection is not valid";
		return "ERROR";
	}
	//sets correct database to be used
	stmt = con->createStatement();
	stmt->execute("USE " DINN_DB);
	string query = "SELECT NAME, LABEL_VALUE"
		"FROM NUTRITION"
		"WHERE ITEM_ID = ?";
	prep_stmt = con->prepareStatement(query);
	prep_stmt->setInt(1, id);
	res = prep_stmt->executeQuery();
	int size = res->rowsCount();
	string * name = new string[size];
	int * value = new int[size];
	for (int i = 0; i < size; i++) {
		if(!res->next()){
			size = i;
			break;
		}
		name[i] = res->getString(sql::SQLString("NAME"));
		value[i] = res->getInt(sql::SQLString("LABEL_VALUE"));
	}
	processNutrition(name, value, size);
	delete res;
	delete stmt;
	delete prep_stmt;
	delete con;
	delete name;
	delete value;
	return NULL;
}
//gets Allergen info from information given
string getAllergens(int id) {
	sql::Connection *con;
	sql::PreparedStatement *prep_stmt;
	sql::Statement *stmt;
	sql::ResultSet *res;
	//sets up connection
	con = sql_call.driver->connect(HOST, USERNAME, PASSWORD);
	cout << "never connects";
	//sees if it connects succesfully
	if (con->isValid()) {
		cout << "connectio is closed";
	}
	else {
		cout << "connection is not valid";
		return "ERROR";
	}
	//sets correct database to be used
	stmt = con->createStatement();
	stmt->execute("USE " DINN_DB);
	string query = "SELECT EGGS,FISH,GLUTEN,MILK,PEANUNTS,SHELFISH,SOY,TREENUTS,WHEAT,VEGETARIAN"
		"FROM ALLERGEN"
		"WHERE ITEM_ID = ?";
	prep_stmt = con->prepareStatement(query);
	prep_stmt->setInt(1, id);
	res = prep_stmt->executeQuery();
	if (res->next()) {
		bool pref[10] = { res->getBoolean(sql::SQLString("EGGS")) , res->getBoolean(sql::SQLString("FISH")),
			res->getBoolean(sql::SQLString("GLUTEN")),res->getBoolean(sql::SQLString("MILK")),
			res->getBoolean(sql::SQLString("PEANUNTS")),res->getBoolean(sql::SQLString("SHELFISH")),
			res->getBoolean(sql::SQLString("SOY")),res->getBoolean(sql::SQLString("TREENUTS")),
			res->getBoolean(sql::SQLString("WHEAT")),res->getBoolean(sql::SQLString("VEGETARIAN")) };
		processPref(pref);
	}
	else {
		return "ERROR";
	}
	delete res;
	delete stmt;
	delete prep_stmt;
	delete con;
	return NULL;
}
//creates a username if the username is not already taken, then gives the user the max id 
string createUser(string user, string pass,string first,string last,bool facebook) {
	sql::Connection *con;
	sql::PreparedStatement *prep_stmt;
	sql::Statement *stmt;
	sql::ResultSet *res;
	//sets up connection
	con = sql_call.driver->connect(HOST, USERNAME, PASSWORD);
	cout << "never connects";
	//sees if it connects succesfully
	if (con->isValid()) {
		cout << "connectio is closed";
	}
	else {
		cout << "connection is not valid";
		return "ERROR";
	}
	//sets correct database to be used
	stmt = con->createStatement();
	stmt->execute("USE " DINN_DB);
	string query = "SELECT USER_ID"
		"FROM USER_TABLE"
		"WHERE USER_NAME = ?";
	prep_stmt = con->prepareStatement(query);
	prep_stmt->setString(1, user);
	res = prep_stmt->executeQuery();
	string temp;
	if (res->next()) {
		temp = "USERNAME EXISTS";
	}
	else {
		query = "SELECT MAX(USER_ID) FROM USER_TABLE";
		prep_stmt = con->prepareStatement(query);
		res = prep_stmt->executeQuery();
		if (res->next()) {
			temp = "ERROR";
		}
		int id = res->getInt("USER_ID")+1;
		query = "INSERT INTO USER_TABLE"
			"VALUES(?,?,?,?,?,?)";
		prep_stmt = con->prepareStatement(query);
		prep_stmt->setInt(1, id);
		prep_stmt->setString(2, user);
		prep_stmt->setString(3, first);
		prep_stmt->setString(4, last);
		prep_stmt->setBoolean(5, facebook);
		prep_stmt->setString(6, pass);
		temp = prep_stmt->executeUpdate()+"";
		
	}
	delete res;
	delete stmt;
	delete prep_stmt;
	delete con;
	return temp;
}

int checkDate(string date) {
	time_t now = time(0);
	tm *ltm = localtime(&now);
	int year = ltm->tm_year + 1900;
	int month = 1 + ltm->tm_mon;
	int day = ltm->tm_mday; 
	char x[10]; // is the month/day/year;
	sprintf(x, "%d/%d/%d",month,day,year);
	if (!date.compare(x)) {
		return 1;
	}
	return 0;
}