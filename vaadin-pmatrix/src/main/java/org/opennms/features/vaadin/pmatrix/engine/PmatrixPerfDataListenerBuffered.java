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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculator;
import org.opennms.netmgt.rrd.tcp.PerformanceDataProtos;
import org.opennms.netmgt.rrd.tcp.PerformanceDataProtos.PerformanceDataReadings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * The PmatrixPerfDataListener class runs in a separate thread listening for OpenNMS performance messages.
 * When a message is received, it looks in the dataPointMapDao to see if a pmatrixDpdCalculator is defined 
 * with a key corresponding to the 'filepath' for that message. If an entry is defined, the calculator is run
 * After all of the data packed in the message is processed, the pmatrix tables registered with
 * the dataPointMapDao are notified to look in the dataPointMapDao for new updates.
 * 
 * This version uses an additional thread to queue incoming messages so that we can handle them faster and
 * not block sockets in a WAIT state.
 * 
 * @author C Gallen
 *
 */
@ManagedResource
public class PmatrixPerfDataListenerBuffered implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(PmatrixPerfDataListenerBuffered.class);

	private Thread perfDataQueueListener=null;

	private PerfDataSocketListenerThread perfDataSocketListener=null;

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

	/**
	 *  used to count received message buffers for debug
	 */
	private AtomicLong numberMessageBuffersReceived=new AtomicLong(0);  

	/**
	 * used to count message buffers actually processed
	 */
	private AtomicLong numberMessageBuffersProcessed=new AtomicLong(0);

	/**
	 *  used to count messages actually received
	 */
	private AtomicLong numberPerfMessagesReceived=new AtomicLong(0);

	/**
	 * used to count messages actually processed into a matrix
	 */
	private AtomicLong numberPerfMessagesProcessedByMatrix=new AtomicLong(0);

	/**
	 * maximum length of queue of unprocessed message buffers
	 */
	private static final int MESSAGE_BUFFER_QUEUE_LENGTH=500;

	/**
	 * used to queue messages for processing if consumption is too slow
	 */
	private final BlockingQueue<PerformanceDataProtos.PerformanceDataReadings> m_queue = new LinkedBlockingQueue<PerformanceDataProtos.PerformanceDataReadings>(MESSAGE_BUFFER_QUEUE_LENGTH);


	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}


	@Override
	public void run() {

		LOG.info("PmatrixPerfDataQueueListener is starting to Listen for messages"); 

		try {
			while (run.get()) {
				PerformanceDataReadings messages=null;
				try {
				    messages = m_queue.take(); // waits for messages
				} catch (InterruptedException ie) {
					// interrupt used to force stop of thread
			  		run.set(false);
			  		break;
			    }
				
				numberMessageBuffersProcessed.getAndIncrement();

				for (PerformanceDataProtos.PerformanceDataReading message : messages.getMessageList()) {

					numberPerfMessagesReceived.getAndIncrement();

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
						LOG.debug("Message received. "
								+ "\n   Message buffers (received:'"+numberMessageBuffersReceived.get()
								+ " processed:'"+numberMessageBuffersProcessed.get()
								+ "' In Queue:'"+m_queue.size()
								+ "') "
								+ "\n   Perf Messages (received:'"+numberPerfMessagesReceived.get()
								+ " processed:'"+numberPerfMessagesProcessedByMatrix.get()
								+ "')"
								+ "\n   Message { "
								+ "  path: \""
								+ message.getPath() + "\", "
								+ "owner: \""
								+ message.getOwner() + "\", "
								+ "timestamp: \""
								+ message.getTimestamp()
								+ "\", " + "values: "
								+ values.toString() + " }");
					}

					// try to update the table with data
					// synchronized so that an update operation does not happen while a persist operation is completing
					synchronized(persistLockObject){

						try{
							PmatrixDpdCalculator pmatrixDpdCalculator = dataPointMapDao.getDataPointMap().get(message.getPath());
							if (pmatrixDpdCalculator==null) {
								if(LOG.isDebugEnabled()) LOG.debug("WARNING: data calculator not defined for filepath:'"+message.getPath()+"'");
							} else {
								numberPerfMessagesProcessedByMatrix.getAndIncrement();
								if(LOG.isDebugEnabled()) LOG.debug("SUCCESS: (message processed count:'"+numberPerfMessagesProcessedByMatrix.get()
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

				// tell the dataPointMapDao that the table has changed
				// don't tell it if thread stopping
				if(run.get()) try{
					dataPointMapDao.notifyChange();
				} catch (Exception e){
					LOG.error("problem trying to notify change to dataPointmapDao:", e);
				}

			}

			LOG.info("PmatrixPerfDataQueueListener thread has stopped");
			run.set(false);
		} catch (Exception e){
			LOG.error("Problem with PmatrixPerfDataQueueListener:", e);
		}
	}


	@PostConstruct
	public synchronized void startThread(){
		run.set(true);
		perfDataQueueListener= new Thread(this);
		perfDataQueueListener.setName("PmatrixPerfDataQueueListenerThread" );
		perfDataQueueListener.start();

		perfDataSocketListener= new PerfDataSocketListenerThread(port, run, m_queue, numberMessageBuffersProcessed, numberMessageBuffersReceived);
		perfDataSocketListener.setName("pmatrixPerfDataSocketListenerThread" );
		perfDataSocketListener.start();

	}

	@PreDestroy
	public void stopThread(){
		LOG.info("Stopping PmatrixPerfDataListener"); 
		run.set(false); // stops both threads
		if (perfDataSocketListener!=null){
			try {
				perfDataSocketListener.stopListenerThread();
				perfDataSocketListener.join(); //wait to stop
			} catch (InterruptedException e) {
				LOG.error("Exception when stopping PmatrixPerfDataListener perfDataSocketListener",e);
			}
		}
		if (perfDataQueueListener!=null){
			try {
				perfDataQueueListener.interrupt();
				perfDataQueueListener.join(); //wait to stop
			} catch (InterruptedException e) {
				LOG.error("Exception when stopping PmatrixPerfDataListener perfDataQueueListener",e);
			}
		}
		LOG.info("PmatrixPerfDataListener Stopped"); 

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

	/**
	 * This Thread listens on the port for protobuff messages and adds the to the processing queue 
	 * before quickly resetting the socket
	 * @author opennms
	 *
	 */
	private static class PerfDataSocketListenerThread extends Thread {

		BlockingQueue<PerformanceDataProtos.PerformanceDataReadings> m_queue=null;
		int port=0;
		AtomicBoolean run=null;
		AtomicLong numberMessageBuffersReceived=null;
		AtomicLong numberMessageBuffersProcessed=null;


		ServerSocket ssocket;

		PerfDataSocketListenerThread(int port, AtomicBoolean run, BlockingQueue<PerformanceDataProtos.PerformanceDataReadings> m_queue, 
				AtomicLong numberMessageBuffersProcessed, AtomicLong numberMessageBuffersReceived){
			this.port=port;
			this.run=run;
			this.m_queue=m_queue;
			this.numberMessageBuffersProcessed=numberMessageBuffersReceived;
			this.numberMessageBuffersReceived=numberMessageBuffersReceived;
		}

		@Override
		public void run() {

			LOG.info("PmatrixPerfDataListener PerfDataSocketListenerThread is starting to Listen for messages on port:"+port); 

			try {
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

						PerformanceDataProtos.PerformanceDataReadings messages = PerformanceDataProtos.PerformanceDataReadings.parseFrom(is);
						numberMessageBuffersReceived.getAndIncrement();
						if(m_queue.offer(messages)==false){
							if(LOG.isDebugEnabled()) LOG.debug("Message Buffer Disguarded (message queue is full)."
									+ "\n   Number of messages buffers in queue:"+m_queue.size()
									+ "\n   Message buffers received:"+numberMessageBuffersReceived.get()
									+ "\n   Message buffers processed:"+numberMessageBuffersProcessed.get());
						} else if(LOG.isDebugEnabled()) LOG.debug("Message Buffer Received."
								+ "\n   Number of message buffers in queue:"+m_queue.size()
								+ "\n   Message buffers received:"+numberMessageBuffersReceived.get()
								+ "\n   Message buffers processed:"+numberMessageBuffersProcessed.get());

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
				}
			} catch (IOException e) {
				LOG.error("IO exception when setting up server socket for performance messages ",e);
			} finally {
				run.set(false); // stop queue thread as well //TODO - may try to restart
				if (ssocket!=null) try{
					ssocket.close();
				} catch (IOException e2) {
					LOG.error("IO exception closing performance message server socket ssocket:",e2);
				} 
			}
			LOG.info("PmatrixPerfDataListener ListenerThread thread has stopped");

		}


		public void stopListenerThread(){
			LOG.info("Stopping PmatrixPerfDataListener ListenerThread"); 
			run.set(false);
			if(ssocket!=null){
				try {
					ssocket.close();
				} catch (IOException e) {
					LOG.error("IO exception when closing sockets for performance messages ",e);
				}
			}
		}
	}

	// jmx getters for management information
	/**
	 * @return the numberMessageBuffersReceived
	 */
	@ManagedAttribute(description="Count message buffers received by PerfDataSocketListenerThread ")
	public long getNumberMessageBuffersReceived() {
		return numberMessageBuffersReceived.get();
	}

	/**
	 * @return the numberMessageBuffersProcessed
	 */
	@ManagedAttribute(description="Count of message buffers processed by PmatrixPerfDataQueueListenerThread")
	public long getNumberMessageBuffersProcessed() {
		return numberMessageBuffersProcessed.get();
	}

	/**
	 * @return the numberPerfMessagesReceived
	 */
	@ManagedAttribute(description="Count of performance messages received")
	public long getNumberPerfMessagesReceived() {
		return numberPerfMessagesReceived.get();
	}

	/**
	 * @return the numberPerfMessagesProcessedByMatrix
	 */
	@ManagedAttribute(description="Count of performance messages received and used for display")
	public long getNumberPerfMessagesProcessedByMatrix() {
		return numberPerfMessagesProcessedByMatrix.get();
	}
	
	@ManagedAttribute(description="number of message buffers in message queue")
	public int getMessageQueueLength(){
		return m_queue.size();
	}
	
	@ManagedOperation(description="reset all message counters to zero")
	public void resetCounters(){
		numberPerfMessagesProcessedByMatrix.set(0);
		numberPerfMessagesReceived.set(0);
		numberMessageBuffersProcessed.set(0);
		numberMessageBuffersReceived.set(0);
	}


}
