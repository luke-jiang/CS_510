#!/bin/bash

TESTS=$(find . -maxdepth 1 -type d -name "test*" -printf "%f\n" | sort)
NAME=$(whoami)
LOG=/tmp/testing-$NAME-pi-$(date +%F_%T).log
TOTAL=0
PASSED=0

function verifyTest() {
  TESTNAME=$1
  T_SUPPORT=$2
  T_CONFIDENCE=$3

  OUT_FILE="$TESTNAME"_"$T_SUPPORT"_"$T_CONFIDENCE".out
  GOLD_FILE=gold_"$T_SUPPORT"_"$T_CONFIDENCE"

  TOTAL=$(echo $TOTAL | awk '{ print $1+1 }')
  echo -n Verifying $TESTNAME $T_SUPPORT $T_CONFIDENCE...

  # give 3 minutes
  ./timeout.sh 180 make $OUT_FILE -C $TESTNAME >>$LOG 2>&1

  RET=$?

  if [ "$RET" -eq 124 ]; then
    echo "time out."
    return
  fi
  if [ "$RET" -ne 0 ]; then
    echo "error."
    return
  fi

  TEST_RESULT="diff <(sort $TESTNAME/$OUT_FILE 2>>$LOG) <(sort $TESTNAME/$GOLD_FILE 2>>$LOG)"

  DIFF_COUNT=$(eval $TEST_RESULT | wc -l 2>>$LOG)
  TOTAL_COUNT=$(cat $TESTNAME/$GOLD_FILE 2>>$LOG | wc -l 2>>$LOG)

  if [ $? -eq 0 -a $DIFF_COUNT -eq 0 ]; then
    echo -e "\\e[1;32mPASS.\\e[0m"
    PASSED=$(echo $PASSED | awk '{ print $1+1 }')
  else
    MISSING_COUNT=$(eval $TEST_RESULT | grep -e [\<] 2>>$LOG | wc -l 2>>$LOG)
    FALSE_COUNT=$(eval $TEST_RESULT | grep -e [\>] 2>>$LOG | wc -l 2>>$LOG)
    echo -e "\\e[1;31mFAIL! with $MISSING_COUNT missing, $FALSE_COUNT extra and $TOTAL_COUNT total.\\e[0m"
  fi
}

###########################
# Check for a makefile
if [ ! -f Makefile ]; then
    echo "No Makefile found"
    exit 1
fi
./clean.sh >$LOG 2>&1

# Compile the program
make
if [ $? -ne 0 ]; then
    echo "Failed make"
    exit 1
fi

# Check for the pipair executable
if [ ! -f pipair ]; then
    echo "No pipair found"
    exit 1
fi

# Show the log file
echo "Log: " $LOG

# Perform the testing
for TEST in $TESTS
do
  verifyTest $TEST 3 65
  verifyTest $TEST 10 80
done

echo PASSED/TOTAL:\ $PASSED/$TOTAL

