#!/bin/bash
# -------------------------------------------
# run.sh: regenerate, compile, and run Tiger parser
# Usage: ./run.sh path/to/file.tiger
# -------------------------------------------

if [ $# -ne 1 ]; then
  echo "Usage: $0 <source.tiger>"
  exit 1
fi

SRC="$1"
JAR="lib/antlr-4.12.0-complete.jar"
GEN_DIR="antlr_generated_tiger"

# 1. Regenerate lexer & parser (overwrite $GEN_DIR)
# rm -rf "$GEN_DIR"
# java -jar "$JAR" -Dlanguage=Java -o "$GEN_DIR" Tiger.g4 || { echo "ANTLR generation failed."; exit 1; }

# 2. Compile all Java sources
# Option A: output .class files into project root (-d .)  ⬇️ recommended (simpler run class-path)
javac -cp .:"$JAR" -d . "$GEN_DIR"/*.java Main.java TigerErrorListener.java
# If you prefer keeping classes inside $GEN_DIR, comment the line above and uncomment:
# javac -cp .:"$JAR" "$GEN_DIR"/*.java Main.java TigerErrorListener.java

[ $? -ne 0 ] && { echo "Compilation failed."; exit 1; }

# 3. Run
# If you compiled with -d . (classes in project root):
java -cp .:"$JAR" Main "$SRC"

# If you decided to keep .class files in $GEN_DIR, use instead:
# java -cp .:"$GEN_DIR":"$JAR" Main "$SRC"
