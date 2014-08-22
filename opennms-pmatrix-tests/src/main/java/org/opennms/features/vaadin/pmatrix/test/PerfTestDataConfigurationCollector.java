/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2010-2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.vaadin.pmatrix.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import org.opennms.netmgt.rrd.tcp.PerformanceDataProtos;


/**
 * This class collects data from the opennms performance output stream and generates a template pmatrix configuration file
 * which lists all of the messages and IP addresses in the stream. This can then be used to design a pmatrix for your environment
 */
public class PerfTestDataConfigurationCollector {

	static final String dateFormatString="yyyymmddhhmmss"; 

	// used to construct a basic working configuration
	static final String pmatrixConfigFileHeader=""
			+ "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "\n"
			+ "\n"
			+ "<PmatrixConfigurations xmlns=\"http://xmlns.opennms.org/xsd/config/pmatrix\"" + "\n" 
			+ "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + "\n"
			+ "     xsi:schemaLocation=\"http://xmlns.opennms.org/xsd/config/pmatrix pmatrixConfig.xsd\">" + "\n"
			+ "\n"
			+ "   <!-- refreshRate defines in milliseconds how often all client displays are refreshed -->" + "\n"
			+ "   <refreshRate>10000</refreshRate>" + "\n"
			+ "\n"
			+ "   <!-- pmatrixDpdCalculatorClassName defines which calculator implementation is used for all the pmatrix displays -->" + "\n"
			+ "   <dataPointCalculator xsi:type=\"PmatrixDpdCalculatorConfig\" pmatrixDpdCalculatorClassName=\"org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculatorSimpleMovingAvgImpl\">" + "\n"
			+ "      <configuration>" + "\n"
			+ "\n"
			+ "		    <!-- If data samples are not updated within a timeout, the sample will be displayed as UNDEFINED (blue) -->" + "\n"
			+ "		    <!-- The sampleTimeout property sets the timeout for calculators in milliseconds                        -->" + "\n"
			+ "		    <property name=\"sampleTimeout\" value=\"750000\"/>" + "\n"
			+ "\n"
			+ "		    <!-- property maxSampleNo defines maximum number of samples stored for calculations          -->" + "\n"
			+ "		    <property name=\"maxSampleNo\" value=\"1000\"/>" + "\n"
			+ "\n"
			+ "		    <!-- property secondValue defines what value is displayed as bottom value of each cell       -->" + "\n"
			+ "		    <property name=\"secondValue\" value=\"averageValue\"/>" + "\n"
			+ "\n"
			+ "		    <!-- The thresholdType determines if the alarm threshold is based on standard deviation, average or          -->" + "\n"
			+ "		    <property name=\"thresholdType\" value=\"average\"/>" + "\n"
			+ "\n"
			+ "		    <!-- The threshold multipliers determine how much the thresholdType value is multiplied to determine          -->" + "\n"
			+ "		    <property name=\"warningThresholdMultiplier\"  value=\"0.025\"/> <!-- 2.5% deviation from average -->" + "\n"
			+ "		    <property name=\"minorThresholdMultiplier\"    value=\"0.05\"/>  <!-- 5% deviation from average   -->" + "\n"
			+ "		    <property name=\"majorThresholdMultiplier\"    value=\"0.1\"/>   <!-- 10% deviation from average  -->" + "\n"
			+ "		    <property name=\"criticalThresholdMultiplier\" value=\"0.5\"/>   <!-- 50% deviation from average  -->" + "\n"
			+ "\n"
			+ "      </configuration>" + "\n"
			+ "   </dataPointCalculator>" + "\n"
			+ "\n";

	private static SortedMap<String,String> sortedPathMap = new TreeMap<String, String>();
	private static SortedMap<String,String> sortedOwnerIPMap = new TreeMap<String, String>();

	private volatile static Thread m_listenerThread;

	public static void main(String[] args) {
		Thread listener = null;

		// default values if not supplied
		int port = 8999;
		String fileLocation = "./target/OpenNMSTestConfiguration.xml";

		System.out.println(""
				+ "***********************************************\n"
				+ "OpenNMS QOS Data Configuration Reader          \n"
				+ "-------------------------------------          \n"
				+ "This class collects data from the opennms      \n"
				+ "performance output stream and generates a      \n"
				+ "template pmatrix configuration file which      \n"
				+ "lists all of the messages and IP addresses     \n"
				+ "in the stream. This can then be used to        \n"
				+ "design a pmatrix for your environment          \n"
				+ "This class generates a single table of all IPs \n"
				+ "***********************************************\n");
		if (args.length < 2) {
			System.out.println("This program receives OpenNMS performance data and creates a pmatrix config table\n"
					+ " To change default values supply arguments: port filename.\n");
		} else {
			port = Integer.valueOf(args[0]);
			fileLocation = args[1];
		}

		System.out.println("Ready to receive OpenNMS QOS Data on TCP Port:"
				+ String.valueOf(port) 
				+ "\n to supplied archive file location:" + fileLocation + " ...");

		File currentArchiveFile = new File(fileLocation);
		System.out.println("absolute file location:" + currentArchiveFile.getAbsolutePath());

		// rotate file if exists

		// set the date on which this file was persisted
		Date datePersisted = new Date();
		// used to get file name suffix
		SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormatString);

		if(currentArchiveFile.exists()){
			String oldArchiveFileName=fileLocation+"."+dateFormatter.format(datePersisted);
			try {
				File oldArchiveFile = new File(oldArchiveFileName);
				// rename current archive file to old archive file name
				if (!currentArchiveFile.renameTo(oldArchiveFile))        {
					throw new IOException("Cannot rename existing config file:"+currentArchiveFile.getAbsolutePath()+" to "+oldArchiveFile.getAbsolutePath());
				}
				System.out.println("Renamed existing config file:"+currentArchiveFile.getAbsolutePath()+" to "+oldArchiveFile.getAbsolutePath());
			} catch (IOException e) {
				System.err.println("Problem archiving old config file"+e.getLocalizedMessage());
			}
		}

		PrintWriter writer = null;

		try {
			System.out.println("\nWriting data to:"+currentArchiveFile.getAbsolutePath());
			writer = new PrintWriter(currentArchiveFile, "UTF-8");
			writer.println(pmatrixConfigFileHeader); // print out default configuration header
			writer.print(""
					+ "      <pmatrixSpecifications>"+"\n"
					+ "         <pmatrixSpecification xsi:type=\"PmatrixSpecification\">"+"\n"
					+ "	           <!-- <pmatrixName>"+currentArchiveFile.getName()+"</pmatrixName>-->"+"\n"
					+ "	           <pmatrixName>default</pmatrixName>"+"\n"
					+ "	           <pmatrixTitle>Pmatrix Table Generated by PerfTestDataConfigurationCollector in file "+currentArchiveFile.getName()+"</pmatrixTitle>"+"\n"
					+ "	           <latestDataValueEnabled>true</latestDataValueEnabled>"+"\n"
					+ "	           <secondaryNumberEnabled>true</secondaryNumberEnabled>"+"\n"
					+ "	           <rightArrowEnabled>true</rightArrowEnabled>"+"\n"
					+ "	           <leftArrowEnabled>true</leftArrowEnabled>"+"\n"
					+ "	           <linkUrlEnabled>true</linkUrlEnabled>"+"\n"
					+ "	           <mouseOverTextEnabled>true</mouseOverTextEnabled>"+"\n\n"
					+ "	           <!-- The colorDescriptionStr will be generated automatically -->"+"\n"
					+ "            <!-- <colorDescriptionStr><colorDescriptionStr>-->"+"\n\n"
					+ "            <datapointDefinitions>"+"\n"
					);
			writer.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

		Runtime.getRuntime().addShutdownHook(createShutdownHook(writer));

		try {
			listener = createListenerThread(port, writer);
			listener.start();
			listener.join();
		} catch (Throwable t) {
			System.err.print(t.getLocalizedMessage() + "\n\n" + t);
		}

	}

	public static Thread createShutdownHook(final PrintWriter writer) {
		Thread t = new Thread() {
			@Override
			public void run() {
				System.out.println(""
						+"************************************************\n"
						+ "End of Program: OpenNMS QOS Data Configuration \n"
						+ "***********************************************\n");
				System.out.close();
				writer.print(""
						+"             </datapointDefinitions>"+"\n"
						+ "\n"
						+"             <rowNames>"+"\n"
						);
				for(String rowName :sortedPathMap.keySet()){
					writer.print(""
							+"                <row>"+rowName+"</row>"+"\n"
							);
				}

				writer.print(""
						+"             </rowNames>"+"\n"
						+ "\n"
						+"             <columnNames>"+"\n"
						);

				for(String columnName :sortedOwnerIPMap.keySet()){
					writer.print(""
							+"                <column>"+columnName+"</column>"+"\n"
							);
				}

				writer.print(""
						+"             </columnNames>"+"\n\n"
						+"	         </pmatrixSpecification>\n"
						+ "      </pmatrixSpecifications>"+"\n"
						+ "</PmatrixConfigurations>" + "\n" 
						);
				writer.close();
				Runtime.getRuntime().halt(0);
			}
		};
		return t;
	}

	public static Thread createListenerThread(final int port,
			final PrintWriter writer) {
		m_listenerThread = new Thread() {
			public void run() {
				this.setName("fail");
				ServerSocket ssocket=null;
				try {
					ssocket = new ServerSocket(port);
					ssocket.setSoTimeout(0);
					Socket socket=null;
					while (true) {
						try {
							socket = ssocket.accept();
							InputStream is = socket.getInputStream();
							PerformanceDataProtos.PerformanceDataReadings messages = PerformanceDataProtos.PerformanceDataReadings
									.parseFrom(is);
							for (PerformanceDataProtos.PerformanceDataReading message : messages
									.getMessageList()) {

								// outputs in format
								//<datapointDefinition xsi:type="DataPointDefinition">
								//    <filePath>/home/isawesome/devel/opennms-test/dist/share/rrd/response/173.194.41.177/icmp.jrb</filePath>
								//    <rowName>row 1</rowName>
								//    <colName>col 2</colName>
								//    <!-- initial value examples -->
								//    <graphURL></graphURL>
								//    <latestDataValue>650.0</latestDataValue>
								//    <latestDataValueRange>1</latestDataValueRange>
								//    <secondaryValue>20.0</secondaryValue>
								//    <secondaryValueRange>1</secondaryValueRange>
								//    <leftTrendArrow>trendUp</leftTrendArrow>
								//    <rightTrendArrow>trendDown</rightTrendArrow>
								//    <mouseOverText></mouseOverText>
								//</datapointDefinition>

								if (! sortedPathMap.containsKey(message.getPath())){ 
									String shortRowName=message.getPath();
									sortedPathMap.put(message.getPath(), shortRowName);
									sortedOwnerIPMap.put(message.getOwner(), "");
									double latestDataValue=0;
									if (message.getValueCount()!=0){
										latestDataValue=message.getValue(0);
									}
									writer.println(""
											+"                <datapointDefinition xsi:type=\"DataPointDefinition\">"+"\n"
											+"                    <filePath>"+message.getPath()+"</filePath>"+"\n"
											+"                    <rowName>"+shortRowName+"</rowName>"+"\n"
											+"                    <colName>"+message.getOwner()+"</colName>"+"\n"
											+"                    <graphURL></graphURL>"+"\n"
											+"                    <!-- initial example values REMOVE IN REAL CONFIGURATION-->"+"\n"
											+"                    <latestDataValue>"+latestDataValue+"</latestDataValue>"+"\n"
											+"                    <latestDataValueRange>1</latestDataValueRange>"+"\n"
											+"                    <secondaryValue>"+latestDataValue+"</secondaryValue>"+"\n"
											+"                    <secondaryValueRange>1</secondaryValueRange>"+"\n"
											+"                    <leftTrendArrow>trendUp</leftTrendArrow>"+"\n"
											+"                    <rightTrendArrow>trendDown</rightTrendArrow>"+"\n"
											+"                    <mouseOverText>Message Path:'"+message.getPath()+"'</mouseOverText>"+"\n"
											+"                </datapointDefinition>"+"\n"
											);
								}

								writer.flush();
							}
						} catch (SocketTimeoutException e) {
							System.err.println(e.getLocalizedMessage());
							if (this.isInterrupted()) {
								System.err.println("Interrupted.");
								this.setName("notfailed");
								return;
							}
						} catch (IOException e) {
							System.err.println(e.getLocalizedMessage());
						} finally {
							if (socket!=null) try{
								socket.close();
							} catch (IOException e2) {
								System.err.println("IO exception closing performance message socket:"+e2);
							}
						}
					}

				} catch (IOException e) {
					System.err.println(e.getLocalizedMessage());
				} catch (Throwable e) {
					System.err.println(e.getLocalizedMessage());
				} finally {
					if (ssocket!=null) try{
						ssocket.close();
					} catch (IOException e2) {
						System.err.println("IO exception closing server socket ssocket:"+e2);
					}
				}
			}
		};

		return m_listenerThread;

	}



}
