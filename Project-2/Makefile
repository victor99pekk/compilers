.PHONY: all run clean

all:
	@mkdir -p bin
	@javac -d bin $(shell find . -name '*.java')

run: all
	@java -cp bin/ Main public_test_cases/quicksort/quicksort.ir

	# ./run.sh 
clean:
	@rm -rf bin