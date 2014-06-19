#!/bin/bash

NAME=$1
N=$2
C=$3
RESOURCE=$4

# Execute gnuplot template commands
/usr/local/bin/gnuplot << EOF

# output as png image
set terminal png size 1280,720
 
# save file to "<argument>.png"
set output "graphs/$NAME.png"
 
# graph a title
set title "ab -n $N -c $C -g http://54.72.161.42/webapp/sos/rest/$RESOURCE"
 
# Set the aspect ratio of the graph
set size 1, 1

# Where to place the legend/key
set key left top
 
# Draw gridlines oriented on the y axis
set grid y
 
# Specify that the x-series data is time data
set xdata time
 
# Specify the *input* format of the time data
set timefmt "%s"
 
# Specify the *output* format for the x-axis tick labels
set format x "%S"
 
# Label the x-axis
set xlabel 'seconds'
 
# Label the y-axis
set ylabel "response time (ms)"
 
# Tell gnuplot to use tabs as the delimiter instead of spaces (default)
set datafile separator '\t'

# Plot the data
plot 'data/$NAME.txt' using 2:5 title 'Response time' with points

EOF

RET=$?
if [ $RET -eq 0 ];then
  echo "Plot generated from 'data/$NAME.txt' into 'graphs/$NAME.png'"
fi

