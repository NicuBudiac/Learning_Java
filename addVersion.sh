#!/bin/bash

REPO_PATH=/media/artifacts/AutoTests/SW_Upgrade_HE

TestsPackVersion=SW_Upgrade_HE_TestsVersion.txt
FILE_PATH=/home/jenkins/workspace/"$TestsPackVersion"

TestsPack_PATH=/home/automation
TestsPack_NAME=/home/automation/sw_Upgrade_HE.zip
INIT_TestsPackName=/home/automation/sw_Upgrade_HE_100.0.0.1.zip

CountEmpty="$(ls -A $REPO_PATH | wc -l)"

# check if there is already a zip on repo_path if not add it with initial version
if [ "$CountEmpty"  -eq 0 ]; then

	echo sw_Upgrade_HE_100.0.0.0.zip > /home/jenkins/workspace/"$TestsPackVersion"

else
	echo "$REPO_PATH folder is not empty."
	ls -t "$REPO_PATH" | head -1 > /home/jenkins/workspace/"$TestsPackVersion"

fi

if [ -f "$FILE_PATH" ]; then

    echo "$FILE_PATH txt file exist."

	sed -i 's/\.zip$//' "$FILE_PATH"
    sed -i 's/sw_Upgrade_HE_//' "$FILE_PATH"

	myvar="$(cat $FILE_PATH)"

	# Here comma is our delimiter value
	IFS="." read -a myarray <<< $myvar

	# to be removed
	echo "My array: ${myarray[@]}"
	echo "Number of elements in the array: ${#myarray[@]}"

	myarray_length=${#myarray[@]}
	echo "My array length is: ${myarray_length}"


	build=${myarray[-1]}
	echo "Initial Build Number is: ${build}"

	newbuild=$((build+1))
	echo "Current Build Number is: $newbuild"

	#sed -i "s/$build/$newbuild/" "$FILE_PATH"
	 sed -r -i "s/[0-9]+$/$newbuild/" "$FILE_PATH"

	newvar="$(cat $FILE_PATH)"
	echo "Newvar is equal: $newvar"
	sed  -i "s/\($newvar\)/sw_Upgrade_HE_\1\.zip/" "$FILE_PATH"

	sed -r -i  's/\s+//g' "$FILE_PATH"
	newPackName="$(cat $FILE_PATH)"
	echo "NewPackName is: $newPackName"

	mv "$TestsPack_NAME" "$TestsPack_PATH"/"$newPackName"

else
    echo "$FILE_PATH file does not exist."

	mv "$TestsPack_Name" "$INIT_TestsPack_NAME"

fi

# ----- copy package with tests to \\gna2\pituach\AutoTests\

mv "$TestsPack_PATH"/"$newPackName" "$REPO_PATH"/.






