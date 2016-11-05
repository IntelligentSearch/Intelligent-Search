#!/bin/bash
#LOG=/home/cs307/Intelligent-Search/Scripts/Vehicle/log.txt
#rm $LOG
mysql -u root -pcz002 CITYBUS -e "update reference set pid = 1"
mysql -u root -pcz002 CITYBUS -e "update reference set count = 1"
while :
do
	count=$(mysql -u root -pcz002 CITYBUS -se "Select count from reference")
	echo $count 
	if [ $count -eq 0 ] 
	then 
		mysql -u root -pcz002 CITYBUS -e "update reference set pid = -1"
		break
	fi
		mysql -u root -pcz002 CITYBUS -e "update reference set pid     = -2"
		mysql -u root -pcz002 CITYBUS -e "drop Table IF EXISTS live_data"
		mysql -u root -pcz002 CITYBUS -e "CREATE TABLE live_data( dateTime varchar(100) NOT NULL ,lat float, vlong float, spd float, dir int(11), sched_status int(11), sched_delta int(11), route_status int(11), route_key varchar(255) NOT NULL, route_name varchar(255), pattern_key varchar(255) NOT NULL, pattern_name varchar(255), trip_key varchar(255) NOT NULL, trip_name varchar(255), name varchar(100) PRIMARY KEY, foreign key(route_key) REFERENCES Routes(route_id), foreign key(trip_key) REFERENCES Trips(trip_id))"
		javac Vehicle_Handler.java
		java -cp .:/home/cs307/Intelligent-Search/Scripts/CityBus/lib/mysql-connector-java-5.1.15-bin.jar Vehicle_Handler

		mysql -u root -pcz002 CITYBUS -e "update reference set pid         = 1"
	sleep 28
done
