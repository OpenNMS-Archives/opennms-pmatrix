package org.opennms.features.vaadin.pmatrix.test;

public class Menu {

	public static void main(String[] args) {
		// this simply prints a message and returns
		// you need to specify the correct command string to run the separate tests
		System.out.println(
				  "**********************************\n"
				+ "OpenNMS TCP Performance Data Tests\n"
				+ "**********************************\n\n"
				+ "This jar contains tests to collect TCP performance data from OpenNMS into an xml file\n"
				+ "or to send performance data from an xml file as if it came from OpenNMS\n"
				+ "\n"
				+ "To collect data use the following java command on this jar file:\n"
				+ "java -cp opennms-pmatrix-tests-<version>-jar-with-dependencies.jar "+ PerfTestDataCollector.class.getName()+"  8999 ./newtestfile.xml\n"
				+ "(where 8999 can be any port number and ./newtestfile.xml can be any relative or absolute file name)\n"
				+ "(Note if the file already exists its name will be appended with a date and the file preserved before a new file is written)\n"
				+ ""
				+ "\n"
				+ "To send data use the following java command on this jar file:\n"
				+ "java -cp opennms-pmatrix-tests-<version>-jar-with-dependencies.jar "+ PerfTestDataSender.class.getName()+" localhost 8999 ./testfile.xml 5\n"
				+ "(where localhost can be replaced with a dns name or IP address,\n"
				+ "       8999 can be any port number,"
				+ "       ./testfile.xml can be any relative or absolute file name and \n"
				+ "       '5' is the delay between messags in ms)\n"
				+ "\n"
				+ "This jar also contains tests to generate a default pmatrix configuration from received TCP performance data from OpenNMS\n"
				+ "The intention of this configuration is to make it easier to create a working table for a given OpenNMS performance feed.\n"
				+ "The generated table row names = the file path and the column names = the owner IP of each message type received"
				+ "\n"
				+ "To create a configuration which generates a default menu table and one table per performance message owner (port IP address)\n"
				+ "use the following java command on this jar file:\n"
				+ "java -cp opennms-pmatrix-tests-<version>-jar-with-dependencies.jar "+ PerfTestDataConfigurationCollectorOneTablePerIP.class.getName()+"  8999 ./OpenNMSTestConfigurationOneTablePerIP.xml\n"
				+ "(where 8999 can be any port number and ./OpenNMSTestConfigurationOneTablePerIP.xml can be any relative or absolute file name)\n"
				+ "(Note if the file already exists its name will be appended with a date and the file preserved before a new file is written)\n"
				+ "\n"
				+ "To create a configuration which generates a single table with one column per performance message owner (port IP address)\n"
				+ "use the following java command on this jar file:\n"
				+ "java -cp opennms-pmatrix-tests-<version>-jar-with-dependencies.jar "+ PerfTestDataConfigurationCollector.class.getName()+"  8999 ./OpenNMSTestConfigurationOneTable.xml\n"
				+ "(where 8999 can be any port number and ./OpenNMSTestConfigurationOneTable.xml can be any relative or absolute file name)\n"
				+ "(Note if the file already exists its name will be appended with a date and the file preserved before a new file is written)\n"
				+ "(Note also that this single table configuration is only suitable for small installations.\n "
				+ " The generated single table can be too large for the available memory)\n"
				+ "**********************************\n\n"
				);

	}

}
