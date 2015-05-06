AOS Project 3 

Sudhanshu Iyer	- sxi120530
Gaurav Dey 	- gxd130330
Nischal Colluru	- nxc130530

The projects implements Dynamic Volting Protocol for a replicated file system 
within the constraints given in project description.

The program utilizes TCP Server/Client for socket communication. 

****** TO COMPILE ******
The project uses Apache Maven for dependency management. Hence to compile maven is required on the Path.
To do a clean compile issue the following command in the project root folder:

	mvn clean compile assembly:single

This commands creates a JAR file under target/ directory with all dependencies packaged 
inside for execution.

****** TO RUN ******
To run the program issue the following command:

	java -jar target/<jar-name>.jar <mode> <node_id>
	
The program expects a configuration file named "AOS_P3_CONF.json" at the project root directory.
The program expects the "filesystem" in the form of seperate folders for each node named as root0
root1, root2 respectively for each node id.
Each root folder should contain the same number of files and same file names initially. 
The program creates a folder named testClocks which contains the entry/exit vector clock timestamps 
for each operation performed. This is used later in the Testing Framework to ensure that the operations did not 
overlap.

Modes -
The program can run in 2 modes:
	Daemon Mode (-D):
		Used to make automated read/write requests with given input probablity on each file with uniform probablity.

	Interactive Mode (-I):
		Interactive mode is used to test the program executes correctly for each condition. 
		This mode is also used to demonstrate node failure and recovery. 

****** TESTING FRAMEWORK ******

 - Detecting READ/WRITE Violations:
 We have implemented Vector Clocks to test our program executes correctly. The testClocks folder contains a
 log of entry/exit vector clocks for each operation performed. 
 Another program "VectorClockValidator" takes each file in "testClocks" folder as input and tests if READ/WRITE
 WRITE/WRITE Mutual Exclusion was violated or not.

The program was tested with Java 7.