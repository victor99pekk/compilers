#!/bin/bash

# Write a script to run your optimizer in this file 
# This script should take one command line argument: an path to 
# an input ir file as 
# This script should output an optimized ir file named "out.ir"

# Matthew
# run from workspace folder:
# java -cp materials/src/bin/ Optimizer <input program path> <output program path>

java -cp materials/src/bin/ Optimizer $1