#!/bin/bash
#LOG=/home/cs307/Intelligent-Search/Scripts/Vehicle/log.txt
#rm $LOG
echo $$
mysql -u root -pcz002 CITYBUS -e "update reference set pid = $$"
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
		javac Vehicle_Handler.java
		time java -cp .:/home/cs307/Intelligent-Search/Scripts/CityBus/lib/mysql-connector-java-5.1.15-bin.jar Vehicle_Handler

	sleep 28
done
