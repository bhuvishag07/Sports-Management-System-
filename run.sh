#!/bin/bash
# Execution script for Case Study 47 - Sports Club Membership Management System

set -e
cd "$(dirname "$0")"
./compile.sh

echo "Launching Sports Club Membership Management System (Java Swing GUI)..."
exec java -cp bin Main
