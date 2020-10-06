#!/bin/bash

$CALL_GRAPH="$1.callgraph"
opt -print-callgraph $1 1>/dev/null 2>$CALL_GRAPH 
java Test1 $CALL_GRAPH $2 $3 2>/dev/null
