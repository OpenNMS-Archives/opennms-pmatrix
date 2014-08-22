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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * Takes data from an xml file and sends it as if coming from an OpenNMS performance output stream 
 */
public class PerfTestDataSender {

	public static void main(String[] args) {

		long messageCount=0;
		int messageInterval= 5; // millisecond delay between messages - 5 ms seems maximum
		
		int port = 8999;
		String hostname="localhost";
		String fileLocation = "src/test/resources/testfile.xml";

		System.out.println(
				  "****************************\n"
				+ "OpenNMS QOS test Data Sender\n"
				+ "****************************\n");
		if (args.length < 3) {
			System.out.println("Takes data from an xml file and sends it as if coming from an opennms performance output stream\n"
							+ "To change defaults supply arguments: hostname port filename (optional) messageInterval (in ms).\n"
							+ "e.g. java -cp opennms-pmatrix-tests-<version>-jar-with-dependencies.jar "+ PerfTestDataSender.class.getName()+" localhost 8999 testfile.xml 5");
		} else {
			hostname=args[0];
			port = Integer.valueOf(args[1]);
			fileLocation = args[2];
			if (args.length > 3 ){
				messageInterval = Integer.valueOf(args[3]);
			}
			
		}

		System.out.println("Ready to send OpenNMS QOS Data as TCP to Hostname:"
				+ hostname
				+ " Port:"+ String.valueOf(port)
				+ "\nFrom supplied input test file name:" + fileLocation + " message interval:"+messageInterval
						+ " ms ...");

		File file = new File(fileLocation);
		System.out.println("Absolute input file location:" + file.getAbsolutePath());
		
		BufferedInputStream testfile=null;
		
		try {
			// speeds up access
			final int BUFFERED_SIZE=16000;
			testfile = new BufferedInputStream(new FileInputStream(file),BUFFERED_SIZE);
			
			PartialXMLUnmarshaller<TestMessageData> unmarshalTestMessageData = new PartialXMLUnmarshaller<TestMessageData>(testfile, TestMessageData.class);
			
			while (unmarshalTestMessageData.hasNext()){
				messageCount++;
				TestMessageData tmd = unmarshalTestMessageData.next();
				System.out.println("Sending Test Data: (count:'"+messageCount
						+ "') timestamp:'"+tmd.getTimestamp()+"' owner:'"+tmd.getOwner()+"' path:'"+tmd.getPath()+"' values:'"+tmd.getValues()+"'");
				
				// note could pack more messages into each protocol buffer but here just sending one message at a time
				PerfDataSender pds = new PerfDataSender(hostname, port); 				
				pds.addData(tmd.getPath(), tmd.getOwner(), tmd.getTimestamp(), tmd.getValues());
				pds.writeData();
				try {
				    Thread.sleep(messageInterval);
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			}
			testfile.close();
			System.out.println(
					  "**************************************\n"
					+ "End of program - finished sending data\n"
					+ "**************************************\n");
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}





	}


}
