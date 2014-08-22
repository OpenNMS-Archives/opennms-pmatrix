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

package org.opennms.features.vaadin.pmatrix.engine;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculator;
import org.opennms.netmgt.rrd.tcp.PerformanceDataProtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The PmatrixPerfDataListener class runs in a separate thread listening for OpenNMS performance messages.
 * When a message is received, it looks in the dataPointMapDao to see if a pmatrixDpdCalculator is defined 
 * with a key corresponding to the 'filepath' for that message. If an entry is defined, the calculator is run
 * After all of the data packed in the message is processed, the pmatrix tables registered with
 * the dataPointMapDao are notified to look in the dataPointMapDao for new updates.
 * 
 * This is a simple listener which works provided messages don't arrive at more than 5ms intervals
 * 
 * @author C Gallen
 *
 */
public class PmatrixPerfDataListener implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(PmatrixPerfDataListener.class);

	private Thread perfDataListener=null;

	/**
	 * Used to store data a dataPointMap singleton
	 */
	private DataPointMapDao dataPointMapDao=null;

	/**
	 * used to ensure that no updates happen to dataPointMapDao while it is being persisted
	 */
	private Object persistLockObject = new Object();

	private AtomicBoolean run= new AtomicBoolean(true);

	private int port=8999; // default value

	private ServerSocket ssocket;
	
	private long numberMessagesReceived=0;  // used to count received messages for debug
	
	private long numberMessagesProcessed=0; // used to count messages actually processed


	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}


	@Override
	public void run() {

		LOG.info("PmatrixPerfDataListener is starting to Listen for messages on port:"+port); 

		try {
			// TODO remove // ssocket = new ServerSocket(port);
			ssocket = new ServerSocket(); // don't bind just yet
			ssocket.setSoTimeout(0);
            // Avoid TIME_WAIT when killing the server
			ssocket.setReuseAddress(true);
			ssocket.bind(new InetSocketAddress(port)); // can bind with reuse = true
			while (run.get()) {
				Socket socket=null;
				try {
					socket = ssocket.accept();
					InputStream is = socket.getInputStream();
					// may speed up access
					//final int BUFFERED_SIZE=16000;
					//BufferedInputStream bis= new BufferedInputStream(is,BUFFERED_SIZE);
					
					PerformanceDataProtos.PerformanceDataReadings messages = PerformanceDataProtos.PerformanceDataReadings.parseFrom(is);
					numberMessagesReceived++;
					for (PerformanceDataProtos.PerformanceDataReading message : messages.getMessageList()) {
						
						// only create debug message if debug logging enabled
						if(LOG.isDebugEnabled()){
							StringBuffer values = new StringBuffer();
							values.append("{ ");
							for (int i = 0; i < message.getValueCount(); i++) {
								if (i != 0) {
									values.append(", ");
								}
								values.append(message.getValue(i));
							}
							values.append(" }");
							LOG.debug("Message received count:'"+numberMessagesReceived
									+ "' { "
									+ "path: \""
									+ message.getPath() + "\", "
									+ "owner: \""
									+ message.getOwner() + "\", "
									+ "timestamp: \""
									+ message.getTimestamp()
									+ "\", " + "values: "
									+ values.toString() + " }");
						}

						socket.close(); // close socket before processing received data

						// try to update the table with data
						// synchronized so that an update operation does not happen while a persist operation is completing
						synchronized(persistLockObject){

							try{
								PmatrixDpdCalculator pmatrixDpdCalculator = dataPointMapDao.getDataPointMap().get(message.getPath());
								if (pmatrixDpdCalculator==null) {
									if(LOG.isDebugEnabled()) LOG.debug("WARNING: data calculator not defined for filepath:'"+message.getPath()+"'");
								} else {
									numberMessagesProcessed++;
									if(LOG.isDebugEnabled()) LOG.debug("SUCCESS: (message processed count:'"+numberMessagesProcessed
											+ "') data calculator defined for filepath:'"+message.getPath()+"' number of update messages: "+message.getValueCount());
									
									for (int i = 0; i < message.getValueCount(); i++){
										// adds all values in message
										Double latestValue = new Double(message.getValue(i));
										Long latestTimestamp= new Long(message.getTimestamp());
										//message.getOwner();
										pmatrixDpdCalculator.updateCalculation(latestValue, latestTimestamp);
									}

								}
							} catch (Exception e){
								LOG.error("problem trying to update dataPointmapDao with message.getPath():'"+message.getPath()+"'", e);
							}
						}
					}

				} catch (SocketTimeoutException e) {
					LOG.error("socket has timed out when listening for performance messages:", e);
				} catch (IOException e) {
					LOG.error("IO exception when listening for performance messages:",e);
				} finally {
					if (socket!=null) try{
						socket.close();
					} catch (IOException e2) {
						LOG.error("IO exception closing performance message socket:",e2);
					}
				}
				// tell the dataPointMapDao that the table has changed
				// don't tell it if thread stopping
				if(run.get()) try{
					dataPointMapDao.notifyChange();
				} catch (Exception e){
					LOG.error("problem trying to notify change to dataPointMapDao:", e);
				}

			}
		} catch (IOException e) {
			LOG.error("IO exception when setting up server socket for performance messages ",e);
		} finally {
			if (ssocket!=null) try{
				ssocket.close();
			} catch (IOException e2) {
				LOG.error("IO exception closing performance message server socket ssocket:",e2);
			} 
		}
		LOG.info("PmatrixPerfDataListener thread has stopped");
		run.set(false);
	}

	@PostConstruct
	public synchronized void startThread(){
		run.set(true);
		perfDataListener= new Thread(this);
		perfDataListener.setName("PmatrixPerfDataListenerThread" );
		perfDataListener.start();

	}

	@PreDestroy
	public void stopThread(){
		LOG.info("Stopping PmatrixPerfDataListener"); 
		run.set(false);
		if (perfDataListener!=null){
			if(ssocket!=null){
				try {
					ssocket.close();
					perfDataListener.join(); //wait to stop
				} catch (IOException e) {
					LOG.error("IO exception when closing sockets for performance messages ",e);
				} catch (InterruptedException e) {
					LOG.error("Exception when stopping PmatrixPerfDataListener",e);
				}
			}
		}

	}

	/**
	 * Pauses the updates from the update listener and instructs the attached DataPointMapDao to persist its data
	 * @return true if the DataPointMapDao says it has persisted
	 */
	public synchronized  boolean persistDataPointMapDao(){
		boolean persistSuccessfull=false;
		LOG.debug("DEBUG: persistDataPointMapDao() called");
		if (dataPointMapDao==null) return false;
		synchronized(persistLockObject) {
			persistSuccessfull=dataPointMapDao.persist();
		}
		return persistSuccessfull;
	}


	/**
	 * @return the dataPointMapDao
	 */
	public DataPointMapDao getDataPointMapDao() {
		return dataPointMapDao;
	}

	/**
	 * @param dataPointMapDao the dataPointMapDao to set
	 */
	public void setDataPointMapDao(DataPointMapDao dataPointMapDao) {
		this.dataPointMapDao = dataPointMapDao;
	}

}
