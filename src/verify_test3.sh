#!/bin/bash

# Verify that test case 3 output is correct

# 1. Compile all Java files
echo "Compiling..."
javac *.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

# 2. Run test case 3
echo "---------------------------"
echo "Running Test Case 3..."
echo "---------------------------"

java HW5 hw5in3.txt > my_output3.txt

# Compare the output with the expected output
diff my_output3.txt hw5out3.txt > diff_result3.txt

if [ $? -eq 0 ]; then
    echo "TEST 3: PASSED! ✅"
else
    echo "TEST 3: FAILED! ❌"
    echo "Differences found between my_output3.txt and hw5out3.txt:"
    cat diff_result3.txt
fi
echo "---------------------------"
