#!/bin/bash
echo ">> Parsing Current...";
PARSER_DIRECTORY="../parser";
PARSER_CLASSPATH=$(cat "$PARSER_DIRECTORY/classpath")
CLASS=$(cat "parser_class")
echo "CLASS: $CLASS";
CHANGE_DIRECTORY=$(cat "change_directory");
echo "CHANGE_DIRECTORY: $CHANGE_DIRECTORY";
RES_DIR="res-current"
echo "RES_DIR: $RES_DIR";
ARGS="input/gtfs.zip $CHANGE_DIRECTORY$RES_DIR/raw current_"
echo "ARGS: $ARGS";
java -Xms2048m -Xmx8192m -Dfile.encoding=UTF-8 \
-classpath \
bin:\
$PARSER_CLASSPATH \
$CLASS \
$ARGS;
RESULT=$?;
echo ">> Parsing Current... DONE";
exit $RESULT;
