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

package org.opennms.features.vaadin.pmatrix.manual;


import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculator;
import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculatorEmaImpl;
import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculatorRepository;
import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculatorSimpleMovingAvgImpl;
import org.opennms.features.vaadin.pmatrix.model.NameValuePair;
import org.opennms.features.vaadin.pmatrix.model.PmatrixDpdCalculatorConfig;
import org.opennms.features.vaadin.pmatrix.model.PmatrixDpdCalculatorConfigImpl;
import org.opennms.features.vaadin.pmatrix.model.PmatrixSpecification;
import org.opennms.features.vaadin.pmatrix.model.PmatrixSpecificationImpl;
import org.opennms.features.vaadin.pmatrix.model.PmatrixSpecificationList;
import org.opennms.features.vaadin.pmatrix.model.PmatrixSpecificationListImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.support.StaticApplicationContext;

import junit.framework.TestCase;

public class PmatrixDpdCalculatorSimpleMovingAverageImplMarshalTest extends TestCase {
	private static final Logger LOG = LoggerFactory.getLogger(PmatrixDpdCalculatorSimpleMovingAverageImplMarshalTest.class);


	public void testMarshalData(){
		System.out.println("start of test:testMarshalData()");
		
		// check slf4j settings
		LOG.debug("debug message");
		LOG.warn("warn message");
		LOG.info("info message");

		try {
			String testFileName=this.getClass().getSimpleName()+"_File.xml";
			File file = new File("target/"+testFileName);
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			writer.close();
			System.out.println("file location:" + file.getAbsolutePath());

			JAXBContext jaxbContext = JAXBContext.newInstance("org.opennms.features.vaadin.pmatrix.calculator");

			// *****************
			// create calculator
			// *****************
			PmatrixDpdCalculatorSimpleMovingAvgImpl simpleMovingAvgCalc= new PmatrixDpdCalculatorSimpleMovingAvgImpl();
			

			simpleMovingAvgCalc.setLatestDataValue(1010d);
			simpleMovingAvgCalc.setLatestTimestamp(new Date().getTime());
			simpleMovingAvgCalc.setPrevDataValue(3040d);
			simpleMovingAvgCalc.setPreviousTimestamp(new Date().getTime());
			
			NameValuePair property = new NameValuePair(PmatrixDpdCalculatorSimpleMovingAvgImpl.MAX_SAMPLE_NO_PROPERTY_NAME, "10");
			simpleMovingAvgCalc.getConfiguration().add(property);
			
			for (int i=1; i<15 ; i++){
				Double latestDataValue=Double.valueOf(i);
				Long latestTimestamp = new Date().getTime()+i; // adding to show small increment in time
				simpleMovingAvgCalc.updateCalculation(latestDataValue, latestTimestamp);
			}

			// **********************
			// marshal test file
			// **********************

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://xmlns.opennms.org/xsd/config/pmatrix pmatrixConfig.xsd");

			jaxbMarshaller.marshal(simpleMovingAvgCalc, file);
			//jaxbMarshaller.marshal(pmatrixSpecificationList_context, file);

			// **********************
			// unmarshal test file
			// **********************

			Unmarshaller jaxbUnMarshaller = jaxbContext.createUnmarshaller();

			//Object o = jaxbUnMarshaller.unmarshal( new StringReader( marshalledXml )  );

			Object o = jaxbUnMarshaller.unmarshal( file  );

			System.out.println("o.tostring:"+o.toString());
			if (o instanceof PmatrixDpdCalculatorSimpleMovingAvgImpl){
				System.out.println("unmarshalled list:");
				System.out.println( (PmatrixDpdCalculatorSimpleMovingAvgImpl) o);
			} else System.out.println("cant unmarshal object:");



		} catch (JAXBException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("end of test:testMarshalData()");

	}

	public void testUnMarshalData(){
		System.out.println("start of test:testUnMarshalData()");


		System.out.println("end of test:testUnMarshalData()");
	}
}
