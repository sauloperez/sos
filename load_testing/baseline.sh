#!/bin/bash

BASE_URI="http://54.72.170.113:8080/webapp/sos/rest"

N=$1
C=$2
RESOURCE=$3

URL=$BASE_URI/$RESOURCE
CONTENT_TYPE="application/gml+xml"
ACCEPT_ENCODING="gzip, deflate"
POST_FILE="post_${RESOURCE}_data.xml"

FILENAME="${RESOURCE}_n${N}c$C"
DATA_PATH="data/$FILENAME.txt"
OUTPUT_PATH="output/$FILENAME.txt"

# Execute Apache Bench test
/usr/sbin/ab -n $N -c $C -p $POST_FILE -T $CONTENT_TYPE -H "Accept: $CONTENT_TYPE" -H "Accept-Encoding: $ACCEPT_ENCODING" -g $DATA_PATH $URL > $OUTPUT_PATH

echo "Output stored in '$OUTPUT_PATH'"

# Plot the resulting data
./plot.sh $FILENAME $N $C $RESOURCE

