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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opennms.features.vaadin.pmatrix.test.PerfDataSender;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class PerfDataSingleCellSenderTest extends TestCase {
	
    public static void main(String[] args) throws Throwable {
    	PerfDataSingleCellSenderTest pdst = new PerfDataSingleCellSenderTest();
    	pdst.testSender();
    }
    
    PerfDataSingleCellSenderTest(){
    	super();
    }
    
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PerfDataSingleCellSenderTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite(){
        return new TestSuite( PerfDataSingleCellSenderTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testSender() {
    	String host="localhost";
    	int port=8999;
    	
		double min=1000; // min value of range
		double max=1200; // max value of range
		int maxnumberOfMessages=100;
		
    	

    	System.out.println("sending OpenNMS QOS Data to Host:"+host+" TCP Port:"+String.valueOf(port)+"...");
    	for (int numberOfMessages=0 ; numberOfMessages<maxnumberOfMessages;numberOfMessages++ ){
    		try {
    	    	PerfDataSender perfSender= new PerfDataSender(host, port);

    			String filename="/home/isawesome/devel/opennms-test/dist/share/rrd/response/173.252.110.27/icmp.jrb";
    			String owner="173.252.110.27";
    			Long timestamp= new Date().getTime();
    			List<Double> values = new ArrayList<Double>();

    			Double randomValue = min + (Math.random() * ((max - min) + 1));
    			values.add(randomValue);
    			System.out.println("sending message"+numberOfMessages+" timestamp:"+timestamp+ " value:"+randomValue);
    			perfSender.addData(filename, owner, timestamp, values);
    			perfSender.writeData();


    		} catch (Throwable t) {
    			t.printStackTrace(System.err);
    		}
    		

			try {
	    		Thread.sleep(1000*3); // 3 seconds
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

    	}
    		
          assertTrue( true );
     		System.out.println("end of test:"+this.getName());
     		 
    }
}
