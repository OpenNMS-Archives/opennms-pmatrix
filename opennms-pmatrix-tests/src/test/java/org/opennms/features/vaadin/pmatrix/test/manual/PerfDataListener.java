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

package org.opennms.features.vaadin.pmatrix.test.manual;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.opennms.netmgt.rrd.tcp.PerformanceDataProtos;

public class PerfDataListener {

	private volatile Thread m_listenerThread;
	
	private boolean run=true;
	
	private long messagesReceived=0;


	public Thread createListenerThread(final int port) {
		m_listenerThread = new Thread() {
			
			
			public void run() {
				System.out.println("Listening for messages"); 
				this.setName("fail");
				ServerSocket ssocket=null;
				try {
					ssocket = new ServerSocket(); // don't bind just yet
					ssocket.setSoTimeout(0);
		            // Avoid TIME_WAIT when killing the server
					ssocket.setReuseAddress(true);
					ssocket.bind(new InetSocketAddress(port)); // can bind with reuse = true
					// TODO remove // ssocket = new ServerSocket(port);
					//ssocket.setSoTimeout(0);
					while (run) {
						Socket socket=null;
						try {
							socket = ssocket.accept();
							messagesReceived++;
							System.out.println("Conection Message received: count: "+messagesReceived);
							InputStream is = socket.getInputStream();
							//BufferedInputStream bis= new BufferedInputStream(is); // seems to cause problems
							PerformanceDataProtos.PerformanceDataReadings messages = PerformanceDataProtos.PerformanceDataReadings.parseFrom(is);
							for (PerformanceDataProtos.PerformanceDataReading message : messages.getMessageList()) {
								StringBuffer values = new StringBuffer();
								values.append("{ ");
								for (int i = 0; i < message.getValueCount(); i++) {
									if (i != 0) {
										values.append(", ");
									}
									values.append(message.getValue(i));
								}
								values.append(" }");
								System.out.println("Message received: { "
												+ "path: \""
												+ message.getPath() + "\", "
												+ "owner: \""
												+ message.getOwner() + "\", "
												+ "timestamp: \""
												+ message.getTimestamp()
												+ "\", " + "values: "
												+ values.toString() + " }");

							}
						} catch (SocketTimeoutException e) {
							System.err.println(e.getLocalizedMessage());
							if (this.isInterrupted()) {
								System.err.println("Interrupted.");
								this.setName("notfailed");
								return;
							}
						} catch (IOException e) {
							System.err.println(e);
						} finally {
							if (socket!=null) try{
								socket.close();
							} catch (IOException e2) {
								System.err.println("IO exception closing performance message socket:"+e2);
							}
						}
					}
					
					
				} catch (IOException e) {
					System.err.println(e);
				} catch (Throwable e) {
					System.err.println(e);
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
