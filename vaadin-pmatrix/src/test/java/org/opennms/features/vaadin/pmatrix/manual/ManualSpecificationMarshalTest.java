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
import java.util.Arrays;
import java.util.List;

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
 * This test builds a specification manually and them marshals and unmarshals it. Useful to test jaxb configuration
 * @author opennms
 *
 */
public class ManualSpecificationMarshalTest extends TestCase {


	private PmatrixSpecificationImpl createNewSpecification(){

		// create data point definition
		DataPointDefinitionImpl dpd = new DataPointDefinitionImpl();
		dpd.setColName("colName");
		dpd.setFilePath("filePath");		
		dpd.setLatestDataValue(10d);
		dpd.setLatestDataValueRange(DataPointDefinition.RANGE_MAJOR);
		dpd.setMouseOverText("mouseOverText");
		dpd.setRowName("rowName");
		dpd.setGraphURL("http://");
		dpd.setLeftTrendArrow(DataPointDefinition.TREND_UP);
		dpd.setRightTrendArrow(DataPointDefinition.TREND_DOWN);
		dpd.setSecondaryValue(20d);
		dpd.setSecondaryValueRange(DataPointDefinition.RANGE_INDETERMINATE);

		// create specification
		PmatrixSpecificationImpl pmatrixSpec1=new PmatrixSpecificationImpl();

		pmatrixSpec1.setPmatrixName("firstSpec");
		
		//set name fields
		pmatrixSpec1.setLatestDataValueDescription("the latest data value received");
		pmatrixSpec1.setSecondaryNumberDescription("the secondary number is not defined");
		pmatrixSpec1.setLeftRangeArrowDescription("indicates immediate change in latest data value. Can be up, down or unchanged");
		pmatrixSpec1.setRightRangeArrowDescription("indicates long term change value. Can be up, down or unchanged");
		pmatrixSpec1.setDescriptiveText("this is a test table illustraating the featurs of the OpenNMS pmatrix feature");

		List<String> columnNames = new ArrayList<String>(Arrays.asList("column A", "column B", "column C"));
		pmatrixSpec1.setColumnNames(columnNames);

		List<String> rowNames= new ArrayList<String>(Arrays.asList("row A", "row B", "row C"));
		pmatrixSpec1.setRowNames(rowNames);

		String pmatrixTitle="this is the title";
		pmatrixSpec1.setPmatrixTitle(pmatrixTitle);

		List<DataPointDefinition> datapointDefinitions = new ArrayList<DataPointDefinition>();
		datapointDefinitions.add(dpd);
		pmatrixSpec1.setDatapointDefinitions(datapointDefinitions);
		return pmatrixSpec1;
	}

	public void testJaxbManual(){
		System.out.println("start of test:testJaxb()");
		try {
			String testFileName=this.getClass().getSimpleName()+"_File.xml";
			File file = new File("target/"+testFileName);
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			writer.close();
			System.out.println("file location:" + file.getAbsolutePath());

			JAXBContext jaxbContext = JAXBContext.newInstance("org.opennms.features.vaadin.pmatrix.model");
			// TODO these classes are listed in jaxb.index file
			//			JAXBContext jaxbContext = JAXBContext.newInstance(					
			//					PmatrixSpecificationImpl.class,
			//					DataPointDefinitionImpl.class,
			//					PmatrixSpecificationListImpl.class,
			//					NameValuePair.class,
			//					PmatrixDpdCalculatorConfigImpl.class);

			// **********************
			// marshal test file
			// **********************
			
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			//PmatrixSpecificationList pmatrixSpecificationList_context = (PmatrixSpecificationList) appContext.getBean("pmatrixSpecificationList");
			//PmatrixSpecification pmatrixSpec_Context = (PmatrixSpecification) appContext.getBean("pmatrixSpecification");



			// manual create process
			PmatrixSpecificationImpl newspec1 = createNewSpecification();
			newspec1.setPmatrixName("spec1");
			PmatrixSpecificationImpl newspec2 = createNewSpecification();
			newspec2.setPmatrixName("spec2");


			//create new specfication list
			PmatrixSpecificationListImpl pmatrixSpecificationList= new PmatrixSpecificationListImpl();
			pmatrixSpecificationList.setRefreshRate(1000);

			//create and add new DpdCalculator specification
			PmatrixDpdCalculatorConfig pmatrixDpdCalculatorConfig= new PmatrixDpdCalculatorConfigImpl();
			pmatrixDpdCalculatorConfig.setPmatrixDpdCalculatorClassName(PmatrixDpdCalculatorEmaImpl.class.getName()); 

			List<NameValuePair> configuration=new ArrayList<NameValuePair>();
			configuration.add(new NameValuePair("file", "fred.txt"));
			pmatrixDpdCalculatorConfig.setConfiguration(configuration);

			pmatrixSpecificationList.setPmatrixDpdCalculatorConfig(pmatrixDpdCalculatorConfig);

			//add specifications of each matrix
			List<PmatrixSpecification> pmsl = new ArrayList<PmatrixSpecification>();

			pmsl.add(newspec1);
			pmsl.add(newspec2);
			pmatrixSpecificationList.setPmatrixSpecificationList(pmsl);

			//System.out.println("list to be marshalled:");
			System.out.println(pmatrixSpecificationList);

			System.out.println("marshalled list:");
			//jaxbMarshaller.marshal(testDatalist, file);

			//jaxbMarshaller.marshal(pmatrixSpec, System.out); // works
			//jaxbMarshaller.marshal(pmatrixSpecificationList, System.out); //works

			//test of marshaling context data
			//jaxbMarshaller.marshal(pmatrixSpecificationList_context, System.out);
			jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://xmlns.opennms.org/xsd/config/pmatrix pmatrixConfig.xsd");

			jaxbMarshaller.marshal(pmatrixSpecificationList, file);
			//jaxbMarshaller.marshal(pmatrixSpecificationList_context, file);

			// **********************
			// unmarshal test file
			// **********************

			Unmarshaller jaxbUnMarshaller = jaxbContext.createUnmarshaller();

			//Object o = jaxbUnMarshaller.unmarshal( new StringReader( marshalledXml )  );

			Object o = jaxbUnMarshaller.unmarshal( file  );

			System.out.println("o.tostring:"+o.toString());
			if (o instanceof PmatrixSpecificationList){
				System.out.println("unmarshalled list:");
				System.out.println( (PmatrixSpecificationList) o);
			} else System.out.println("cant unmarshal object:");



		} catch (JAXBException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("end of test:testJaxb()");
	}
}
