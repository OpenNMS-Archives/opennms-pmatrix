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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.opennms.features.vaadin.pmatrix.test.PartialXMLUnmarshaller;
import org.opennms.features.vaadin.pmatrix.test.TestMessageData;

import junit.framework.TestCase;

public class PartialUnmarshallerTest extends TestCase {
	
	public void testPartialUNmarshal(){
		System.out.println("starting PartialUnmarshallerTest");
		
		File file = new File("src/test/resources/testfile.xml");
		System.out.println("file location:" + file.getAbsolutePath());
		
		FileInputStream testfile=null;
		
	
		try {
			testfile = new FileInputStream(file);
			
			PartialXMLUnmarshaller<TestMessageData> unmarshalTestMessageData = new PartialXMLUnmarshaller<TestMessageData>(testfile, TestMessageData.class);
			
			while (unmarshalTestMessageData.hasNext()){
				TestMessageData tmd = unmarshalTestMessageData.next();
				System.out.println("Test Data: owner:'"+tmd.getOwner()+"' path:'"+tmd.getPath()+"' values:'"+tmd.getValues()+"'");
			}
			testfile.close();
			
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
		

		System.out.println("finished PartialUnmarshallerTest");
	}
	

}
