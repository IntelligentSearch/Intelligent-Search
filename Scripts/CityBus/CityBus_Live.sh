#get new files
wget http://myride.gocitybus.com/public/laf/GTFS/google_transit.zip

#last modified time
TIME=$(stat -c %Y google_transit.zip)
PREV=$(cat last_update.txt)

LOG=./log.txt

#echo $TIME
#echo $PREV

#check if file has been modified
if [ $TIME -eq $PREV ] 
then
	#nothing needs to be done because no update
	echo "No update to static data." > $LOG
else
	echo "Found update to static data." > $LOG

	#write new time
	echo $TIME > last_update.txt

	#delete old files and then unzip folder
	rm google_transit/*
	unzip google_transit.zip -d google_transit/

	#run java file and redirect output 
	java ParseTransit > $LOG	
fi

#delete zip file
rm google_transit.zip
