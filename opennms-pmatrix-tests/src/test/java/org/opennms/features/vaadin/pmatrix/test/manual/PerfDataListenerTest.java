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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class PerfDataListenerTest extends TestCase {
	
    public static void main(String[] args) throws Throwable {
    	PerfDataListenerTest pdlt = new PerfDataListenerTest();
    	pdlt.testListener();
    }
    
    PerfDataListenerTest(){
    	super();
    }
    
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PerfDataListenerTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite(){
        return new TestSuite( PerfDataListenerTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testListener() {
    	
    	PerfDataListener perfListener= new PerfDataListener();
    	

    		Thread listener = null;

    		int port = 8999;
    		
    		System.out.println("Ready to receive OpenNMS QOS Data on TCP Port:"+String.valueOf(port)+"...");
    		try {
    			listener = perfListener.createListenerThread(port);
    			listener.start();
    			int mseconds=100000;
    			System.out.println("waiting for messages:"+mseconds+" milliseconds");

    			Thread.sleep(mseconds);
    			//listener.join();

    		} catch (Throwable t) {
    			t.printStackTrace(System.err);
    		}
    		
          assertTrue( true );
     		System.out.println("end of test:"+this.getName());
     		 
    }
}
