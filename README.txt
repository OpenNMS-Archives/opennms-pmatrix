README.txt
**********

Introduction
************
This project contains the stand alone implementation of the OpenNMS 
Pmatrix feature which displays table of real time performance values.

License
*******
This program is copyright 2014 OpenNMS Group Inc. and is issued 
under the GPL GNU GENERAL PUBLIC LICENSE Version 2. 
For details see the accompanying GPL.txt, LICENCE.txt and COPYING.txt files.

Building the Pmatrix project
****************************

The first time you build the project you need to compile the vaadin javascript. This takes quite a long time but only needs to be done once (unless you change the widget set - which we aren't doing) by enabling the compile-widgetset profile

cd opennms-pmatrix
mvn clean install -Pcompile-widgetset

(Note you will get some maven reported errors such as
[ERROR] INFO: Widgetsets found from classpath:
The vaadin documentation advises not to mind these "ERROR" labels as they are just an issue with the Vaadin Plugin for Maven. See https://vaadin.com/book/-/page/addons.maven.html)

after the widgetset has been compiled, you can do shorter builds by just using
mvn clean install

This should start the build. Maven will download a number of dependencies before building the project. The build should complete with the words 'build successful'

h2. View the project in eclipse

(Note these steps require that eclipse has the m2e plugin installed)
start eclipse and select a new workspace location
right click in the Project Explorer and select import>Maven>Existing Maven Projects
browse to opennms-pmatrix as the Root Folder
select all the projects pom.xml and Finish
Eclipse should display dialogues saying importing maven projects and updating dependencies
When complete you will have three projects in your workspace;
opennms-pmatrix (the parent project)
opennms-pmatrix-tests (miscellaneous test projects)
vaadin-pmatrix (the war project which is the main application)

Test the project

you can test the project in situ using the following commands
cd entimoss-misc/vaadin-pmatrix
in the vaadin-pmatrix project type
mvn jetty:run
open a browser and point to http://localhost:8080/vaadin-pmatrix/