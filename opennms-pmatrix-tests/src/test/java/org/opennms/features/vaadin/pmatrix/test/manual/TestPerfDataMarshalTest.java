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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.opennms.features.vaadin.pmatrix.test.TestMessageData;
import org.opennms.features.vaadin.pmatrix.test.TestMessageList;

/**
 * Unit test for simple App.
 */
public class TestPerfDataMarshalTest extends TestCase {

	public static void main(String[] args) throws Throwable {
		TestPerfDataMarshalTest pdlt = new TestPerfDataMarshalTest();
		pdlt.testMarshal();
		pdlt.testUnMarshal();
	}

	TestPerfDataMarshalTest() {

		super();

	}

	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public TestPerfDataMarshalTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(TestPerfDataMarshalTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testMarshal() {
		System.out.println("start of test:" + this.getName());

		TestMessageData data = new TestMessageData();
		data.setOwner("owner set here");
		data.setPath("path set here");
		data.setTimestamp(new Date().getTime());
		ArrayList<Double> values = new ArrayList<Double>();
		values.add(new Double(12011));
		values.add(new Double(12012));
		data.setValues(values);

		TestMessageList testDatalist = new TestMessageList();

		ArrayList<TestMessageData> dataValues = new ArrayList<TestMessageData>();
		dataValues.add(data);

		testDatalist.setDataValues(dataValues);

		try {

			File file = new File("src/test/resources/testfile.xml");
			System.out.println("file location:" + file.getAbsolutePath());

			JAXBContext jaxbContext = JAXBContext.newInstance(
					TestMessageData.class, TestMessageList.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(testDatalist, file);
			jaxbMarshaller.marshal(testDatalist, System.out);

		} catch (JAXBException e) {
			e.printStackTrace();
		}

		System.out.println("end of test:" + this.getName());

	}

	public void testUnMarshal() {
		System.out.println("start of unmarshal test:" + this.getName());

		File file = new File("src/test/resources/testfile.xml");

		JAXBContext jaxbContext;
		try {

			System.out.println("test file location:" + file.getAbsolutePath());

			jaxbContext = JAXBContext.newInstance(TestMessageData.class,
					TestMessageList.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			TestMessageList testDatalist = (TestMessageList) jaxbUnmarshaller
					.unmarshal(file);

			for (TestMessageData data : testDatalist.getDataValues()) {

				System.out.println("timestamp:'" + data.getTimestamp()
						+ "' path:'" + data.getPath() + "' values:"
						+ data.getValues());

			}

		} catch (JAXBException e) {
			e.printStackTrace();
		}

		System.out.println("end of unmarshal test:" + this.getName());

	}
}
