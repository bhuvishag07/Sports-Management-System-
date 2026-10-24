#!/bin/bash
# Compilation script for Case Study 47 - Sports Club Membership Management System

mkdir -p bin data
echo "Compiling Sports Club Membership Management System..."
javac -d bin -sourcepath src src/Main.java src/TestRunner.java src/model/*.java src/controller/*.java src/view/*.java

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful! Binaries created in 'bin/' directory."
else
    echo "✗ Compilation failed. Please check errors above."
    exit 1
fi
