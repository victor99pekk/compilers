.PHONY: all run clean

all:
	@mkdir -p bin
	@javac -d bin $(shell find . -name '*.java')

run: all
	@java -cp bin/ Main public_test_cases/quicksort/quicksort.ir
	# @java -cp bin/ Main public_test_cases/prime.ir

	# ./run.sh 

test: run
	./run_test.sh

clean:
	@rm -rf bin