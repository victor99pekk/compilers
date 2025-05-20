


# test_dir="quicksort/"
# test_dir="sqrt/"

# test="quicksort.ir"
test="public_test_cases/quicksort/"

# choose whether to run default ir or (our) optimized irwhere 
# test_file="$test_prefix$test_dir$test"
test_file="out.s"

for i in {0..9}; do 

    test_case="$test${i}.in"
    spim -file "$test_file" < "$test_case"
    
done