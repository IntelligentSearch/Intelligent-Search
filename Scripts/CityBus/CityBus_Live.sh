#!/bin/bash
PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin

#get new files
wget -P /home/cs307/Intelligent-Search/Scripts/CityBus/ /http://myride.gocitybus.com/public/laf/GTFS/google_transit.zip

#last modified time
TIME=$(stat -c %Y google_transit.zip)
PREV=$(cat last_update.txt)

LOG=/home/cs307/Intelligent-Search/Scripts/CityBus/log.txt

#echo $TIME
#echo $PREV

echo $(date) > $LOG

#check if file has been modified
if [ $TIME -eq $PREV ] 
then
	#nothing needs to be done because no update
	echo "No update to static data." >> $LOG
	#do nothing
else
	#write new time
	echo $TIME > /home/cs307/Intelligent-Search/Scripts/CityBus/last_update.txt

	#delete old files and then unzip folder
	rm /home/cs307/Intelligent-Search/Scripts/CityBus/google_transit/*
	unzip /home/cs307/Intelligent-Search/Scripts/CityBus/google_transit.zip -d /home/cs307/Intelligent-Search/Scripts/CityBus/google_transit/

	#run java file and redirect output 
	java -cp .:/home/cs307/Intelligent-Search/Scripts/CityBus/lib/mysql-connector-java-5.1.15-bin.jar /home/cs307/Intelligent-Search/Scripts/CityBus/ParseTransit >> $LOG
fi

#delete zip file
rm /home/cs307/Intelligent-Search/Scripts/CityBus/google_transit.zip
