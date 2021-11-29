RUNNING DIRECTIONS FOR LINUX

Note: the .java file is named "Building2.java", it should be fairly obvious as this is the only .java file in the zip folder.

mkdir semaEle
	// ---> upload Building2.java into semaEle

cd semaEle			//make sure you are in the semaEle directory
javac Building2.java 		//compile

cd				//make sure you are one level above semaEle
java semaEle/Building2		//execute
