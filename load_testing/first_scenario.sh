#!/bin/bash

# Note that the sensor specified in $OBSERVATIONS_POST_FILE must be registered in the system beforehand 

BASE_URI="http://54.72.170.113:8080/webapp/sos/rest"

N=$1
C=$2
STEPS=$3

CONTENT_TYPE="application/gml+xml"
ACCEPT_ENCODING="gzip, deflate"

OBSERVATIONS_POST_FILE="1st_scenario_post_data.xml"

for (( i=1; i<=$STEPS; i++ )); do
  FILENAME="observations_n${N}c${C}_$i"
  DATA_PATH="data/${FILENAME}.txt"
  OUTPUT_PATH="output/${FILENAME}.txt"

  /usr/sbin/ab -n $N -c $C -p $OBSERVATIONS_POST_FILE -T $CONTENT_TYPE -H "Accept: $CONTENT_TYPE" -H "Accept-Encoding: $ACCEPT_ENCODING" -g $DATA_PATH $BASE_URI/observations > $OUTPUT_PATH

  echo "\nOutput stored in '$OUTPUT_PATH'"

  # Plot the resulting data
  ./plot.sh $FILENAME $N $C $RESOURCE

  sleep 50
done

echo "Executed $STEPS times"
