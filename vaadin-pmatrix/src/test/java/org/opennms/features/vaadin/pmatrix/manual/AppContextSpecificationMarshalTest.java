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
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;

import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculatorEmaImpl;
import org.opennms.features.vaadin.pmatrix.model.DataPointDefinition;
import org.opennms.features.vaadin.pmatrix.model.DataPointDefinitionImpl;
import org.opennms.features.vaadin.pmatrix.model.NameValuePair;
import org.opennms.features.vaadin.pmatrix.model.PmatrixDpdCalculatorConfig;
import org.opennms.features.vaadin.pmatrix.model.PmatrixDpdCalculatorConfigImpl;
import org.opennms.features.vaadin.pmatrix.model.PmatrixSpecification;
import org.opennms.features.vaadin.pmatrix.model.PmatrixSpecificationImpl;
import org.opennms.features.vaadin.pmatrix.model.PmatrixSpecificationList;
import org.opennms.features.vaadin.pmatrix.model.PmatrixSpecificationListImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.w3c.dom.Document;

import junit.framework.TestCase;

/**
 * This test builds a specification from a working configuration in the application context and marshals and unmarshals it.
 *  Useful to test jaxb configuration and create a working specification
 */
public class AppContextSpecificationMarshalTest extends TestCase {

	public static ApplicationContext appContext=null;

	public void testAppContext(){
		System.out.println("starting testAppContext");
		appContext = new FileSystemXmlApplicationContext("/src/test/resources/testApplicationContext.xml");

		//PmatrixSpecification pmatrixSpec = (PmatrixSpecification) appContext.getBean("pmatrixSpecification");
		PmatrixSpecificationList pmatrixSpecificationList = (PmatrixSpecificationList) appContext.getBean("pmatrixSpecificationList");


		System.out.println(pmatrixSpecificationList);


		System.out.println("test finished ending testAppContext");
	}


	public void testJaxbFromContext(){
		System.out.println("start of test:testJaxbFromContext()");
		try {
			String testFileName=this.getClass().getSimpleName()+"_File.xml";
			File file = new File("target/"+testFileName);
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			writer.close();
			System.out.println("file location:" + file.getAbsolutePath());

			// see http://stackoverflow.com/questions/1043109/why-cant-jaxb-find-my-jaxb-index-when-running-inside-apache-felix
			// need to provide bundles class loader
			ClassLoader cl = org.opennms.features.vaadin.pmatrix.model.DataPointDefinition.class.getClassLoader();
			JAXBContext jaxbContext = JAXBContext.newInstance("org.opennms.features.vaadin.pmatrix.model", cl);
			
			//JAXBContext jaxbContext = JAXBContext.newInstance("org.opennms.features.vaadin.pmatrix.model");
			
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			PmatrixSpecificationList pmatrixSpecificationList_context = (PmatrixSpecificationList) appContext.getBean("pmatrixSpecificationList");
			//PmatrixSpecification pmatrixSpec_Context = (PmatrixSpecification) appContext.getBean("pmatrixSpecification");

			//System.out.println("list to be marshalled:");
			System.out.println(pmatrixSpecificationList_context);

			System.out.println("marshalled list:");
			//jaxbMarshaller.marshal(testDatalist, file);

			//jaxbMarshaller.marshal(pmatrixSpec, System.out); // works
			//jaxbMarshaller.marshal(pmatrixSpecificationList, System.out); //works

			//test of marshaling context data
			//jaxbMarshaller.marshal(pmatrixSpecificationList_context, System.out);
			jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://xmlns.opennms.org/xsd/config/pmatrix pmatrixConfig.xsd");

			jaxbMarshaller.marshal(pmatrixSpecificationList_context, file);
			//jaxbMarshaller.marshal(pmatrixSpecificationList_context, file);

			//unmarshal test file

			Unmarshaller jaxbUnMarshaller = jaxbContext.createUnmarshaller();

			//Object o = jaxbUnMarshaller.unmarshal( new StringReader( marshalledXml )  );

			Object o = jaxbUnMarshaller.unmarshal( file  );

			System.out.println("o.tostring:"+o.toString());
			if (o instanceof PmatrixSpecificationList){
				System.out.println("unmarshalled list:");
				System.out.println((PmatrixSpecificationList) o);

			} else System.out.println("cant unmarshal object:");

		} catch (JAXBException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("end of test:testAppContext()");
	}

}
