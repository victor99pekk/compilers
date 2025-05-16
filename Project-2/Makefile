# Variables
SRC := $(shell find . -name "*.java")
BIN := bin
MAIN := Main
CLASSDIR := $(BIN)
JFLAGS := -d $(CLASSDIR)

# Default target: compile the code
all: $(CLASSDIR)/$(MAIN).class

# Create bin directory if it doesn't exist
$(CLASSDIR):
	mkdir -p $(CLASSDIR)

# Compile all Java files
$(CLASSDIR)/$(MAIN).class: $(SRC) | $(CLASSDIR)
	javac $(JFLAGS) $(SRC)

# Run backend: takes 2 arguments: input file and --naive or --greedy
run: all
	@if [ -z "$(file)" ] || [ -z "$(mode)" ]; then \
		echo "Usage: make run file=path/to/file.ir mode=--naive|--greedy"; \
		exit 1; \
	fi
	java -cp $(CLASSDIR) $(MAIN) $(file) $(mode)

# Clean up
clean:
	rm -rf $(BIN)
