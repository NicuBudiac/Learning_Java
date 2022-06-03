#! /bin/bash
PackFile=sw_Upgrade_HE.zip
PackFolder=swUpgrade_HE_Package
Dir_Path=/home/automation
Curr_Path="$(pwd)"
SCENARIOS=SW_Upgrade_HE.xlsx

# cleaning  required folders and used files
# remove file if already exist

if [ -f  "$PackFile" ]; then
	echo "$PackFile zip file exist."
	rm "$PackFile"
else
	echo "$PackFile file does not exist."
fi

if [ -d "$PackFolder" ]; then
	echo "$PackFolder folder exists."
	rm -rf "$PackFolder"
else
	echo "$PackFolder folder does not exists."
fi

# create directory where all required files and libraries will be stored
#mkdir AutoTestPackage1

mkdir "$Dir_Path"/"$PackFolder"
#copy testng.xml file ... it contains all tests names ...
cp "$Curr_Path"/testng.xml "$Dir_Path"/"$PackFolder"/.

#copy SW_Upgrade_HE.xlsx file ... it contains all tests scenarios ...
cp "$Curr_Path"/"$SCENARIOS" "$Dir_Path"/"$PackFolder"/.

#copy all jar libraries and .jar with tests
cp -r "$Curr_Path"/target/*.jar "$Dir_Path"/"$PackFolder"/.

#copy runTestNG.bat script, which specifies  command how to run specific test from the AutoTestPackage.jar on windows
cp -r "$Curr_Path"/runTestNG.bat "$Dir_Path"/"$PackFolder"/.


#zip all files and libraries into one package
#zip -r AutoTestPackage1.zip AutoTestPackage1/
zip -r -j "$Dir_Path"/"$PackFile" "$Dir_Path"/"$PackFolder"/


# ----- cleanup folder - remove created files from temporary folder ----------
rm -rf "$Dir_Path"/"$PackFolder"/

