#!/bin/bash

test_prefix="../materials/public_test_cases/"

# test_dir="quicksort/"
test_dir="sqrt/"

# test="quicksort.ir"
test="sqrt.ir"

# choose whether to run default ir or (our) optimized ir
test_file="$test_prefix$test_dir$test"
# test_file="../out.ir"

for i in {0..9}; do 
    test_case="$test_prefix$test_dir${i}.in"
    
    java -cp ../materials/src/bin/ IRInterpreter "$test_file" < "$test_case"
done