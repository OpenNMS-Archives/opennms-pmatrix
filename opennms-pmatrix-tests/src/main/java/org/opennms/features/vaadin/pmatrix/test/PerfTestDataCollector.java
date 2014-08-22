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

import org.opennms.netmgt.rrd.tcp.PerformanceDataProtos;


/**
 * This class collects data from the opennms performance output stream and 
 * stores it in an xml file
 *
 */
public class PerfTestDataCollector {

	static final String dateFormatString="yyyymmddhhmmss"; 

	private volatile static Thread m_listenerThread;

	public static void main(String[] args) {
		Thread listener = null;

		// default values if not supplied
		int port = 8999;
		String fileLocation = "./target/OpenNMSTestData.xml";

		System.out.println(
				  "***********************\n"
				+ "OpenNMS QOS Data Reader\n"
				+ "***********************\n");
		if (args.length < 2) {
			System.out.println("This program receives OpenNMS performance data and publishes to a file\n"
							+ " To change default values supply arguments: port filename.\n");
		} else {
			port = Integer.valueOf(args[0]);
			fileLocation = args[1];
		}

		System.out.println("Ready to receive OpenNMS QOS Data on TCP Port:"
				+ String.valueOf(port) 
				+ "\nto supplied archive file location:" + fileLocation + " ...");

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
					throw new IOException("Cannot rename existing archive file:"+currentArchiveFile.getAbsolutePath()+" to "+oldArchiveFile.getAbsolutePath());
				}
				System.out.println("Renamed existing archive file:"+currentArchiveFile.getAbsolutePath()+" to "+oldArchiveFile.getAbsolutePath());
			} catch (IOException e) {
				System.err.println("Problem archiving old persistance file"+e.getLocalizedMessage());
			}
		}

		PrintWriter writer = null;

		try {
			System.out.println("\nWriting data to:"+currentArchiveFile.getAbsolutePath());
			writer = new PrintWriter(currentArchiveFile, "UTF-8");
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
			writer.println("<testMessageList>");
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
				System.out.println(
						  "***************************************\n"
						+ "End of Program: OpenNMS QOS Data Reader\n"
						+ "***************************************\n");
				writer.println("</testMessageList>");
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

								//outputs in format
								//<testMessageList>
								//    <dataValues>
								//        <owner>owner set here</owner>
								//        <path>path set here</path>
								//        <timestamp>1388625189508</timestamp>
								//        <values>12011.0</values>
								//        <values>12012.0</values>
								//    </dataValues>
								//</testMessageList>

								writer.println("    <dataValues>");
								writer.println("        <owner>"+ message.getOwner() +"</owner>");
								writer.println("        <path>"+ message.getPath() +"</path>");
								writer.println("        <timestamp>"+ message.getTimestamp() +"</timestamp>");
								if (message.getValueCount()!=0){
									for (int i = 0; i < message.getValueCount(); i++) {
										writer.println("        <values>"+message.getValue(i)+"</values>");
									}
								}
								writer.println("    </dataValues>");
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
