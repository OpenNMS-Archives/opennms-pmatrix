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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculator;
import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculatorEmaImpl;
import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculatorRepository;
import org.opennms.features.vaadin.pmatrix.model.NameValuePair;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.support.StaticApplicationContext;

import junit.framework.TestCase;

public class CalculationDataMarshalTest extends TestCase {
	
	// values used only for this test
	//File file = new File("target/data-marshaltestfile.xml");
	//private static String archiveFileLocation="file:/home/isawesome/devel/gitrepo/entimoss-misc/opennms-pmatrix/vaadin-pmatrix/PmatrixHistory/historyConfig.xml";
	private static String archiveFileDirectoryLocation="file:./PmatrixHistory";
	
	private static String archiveFileName="data-marshaltestfile.xml";
	
	private static int archiveFileMaxNumber=2;


	// static so that lives between tests on this class
	private static StaticApplicationContext appContext= new StaticApplicationContext();

	// set up application context
	public void testLoadAppContext(){
		System.out.println("start of test:testLoadAppContext()");
		// set up app context to load ResourceLoader
		appContext.registerSingleton("beanPostProcessor", CommonAnnotationBeanPostProcessor.class);
		appContext.registerSingleton("pmatrixDpdCalculatorRepository", PmatrixDpdCalculatorRepository.class);
		appContext.refresh();
		System.out.println("end of test:testLoadAppContext()");
	}

	public void testMarshalData(){
		System.out.println("start of test:testMarshalData()");
		PmatrixDpdCalculatorRepository pmatrixDpdCalculatorRepository=
				(PmatrixDpdCalculatorRepository) appContext.getBean("pmatrixDpdCalculatorRepository");
		pmatrixDpdCalculatorRepository.setArchiveFileDirectoryLocation(archiveFileDirectoryLocation);
		pmatrixDpdCalculatorRepository.setArchiveFileMaxNumber(archiveFileMaxNumber);
		pmatrixDpdCalculatorRepository.setArchiveFileName(archiveFileName);

		// create new PmatrixDpdCalculator
		PmatrixDpdCalculatorEmaImpl pmatrixDpdCalculator= new PmatrixDpdCalculatorEmaImpl();
		pmatrixDpdCalculator.setAlpha(10d);
		pmatrixDpdCalculator.setLatestDataValue(1010d);
		pmatrixDpdCalculator.setLatestTimestamp(new Date().getTime());
		pmatrixDpdCalculator.setMovingAverage(2020d);
		pmatrixDpdCalculator.setMovingVariance(3030d);
		pmatrixDpdCalculator.setPrevDataValue(3040d);
		pmatrixDpdCalculator.setPreviousTimestamp(new Date().getTime());

		// create configuration for calculator
		List<NameValuePair> configuration = new ArrayList<NameValuePair>();
		NameValuePair nvp= new NameValuePair();
		nvp.setName("first name");
		nvp.setValue("first value");
		configuration.add(nvp);
		NameValuePair nvp2= new NameValuePair();
		nvp2.setName("second name");
		nvp2.setValue("second value");
		configuration.add(nvp2);
		pmatrixDpdCalculator.setConfiguration(configuration );

		pmatrixDpdCalculatorRepository.getDataPointMap().put("filename", pmatrixDpdCalculator);

		//File file = new File("target/data-marshaltestfile.xml");
		pmatrixDpdCalculatorRepository.setArchiveFileDirectoryLocation(archiveFileDirectoryLocation);
		pmatrixDpdCalculatorRepository.setArchiveFileName(archiveFileName);
		pmatrixDpdCalculatorRepository.setArchiveFileMaxNumber(2);
		pmatrixDpdCalculatorRepository.setPersistHistoricData(true);

		boolean success = pmatrixDpdCalculatorRepository.persist();

		if(success) {
			System.out.println("marshalled to file:'"+ pmatrixDpdCalculatorRepository.getArchiveFileDirectoryLocation()
					+File.separator+pmatrixDpdCalculatorRepository.getArchiveFileName()+"'");
		} else {
			System.out.println("did not marshal to file:'"+ pmatrixDpdCalculatorRepository.getArchiveFileDirectoryLocation()
					+File.separator+pmatrixDpdCalculatorRepository.getArchiveFileName()+"'");
		}

		appContext.close();

		System.out.println("end of test:testMarshalData()");

	}

	public void testUnMarshalData(){
		System.out.println("start of test:testUnMarshalData()");

		PmatrixDpdCalculatorRepository pmatrixDpdCalculatorRepository=
				(PmatrixDpdCalculatorRepository) appContext.getBean("pmatrixDpdCalculatorRepository");


		pmatrixDpdCalculatorRepository.setArchiveFileDirectoryLocation(archiveFileDirectoryLocation);
		pmatrixDpdCalculatorRepository.setArchiveFileName(archiveFileName);
		pmatrixDpdCalculatorRepository.setArchiveFileMaxNumber(2);
		pmatrixDpdCalculatorRepository.setPersistHistoricData(true);

		boolean success = pmatrixDpdCalculatorRepository.load();
		if(success) {
			System.out.println("unmarshalled from file:''"+ pmatrixDpdCalculatorRepository.getArchiveFileDirectoryLocation()
					+File.separator+pmatrixDpdCalculatorRepository.getArchiveFileName()+"'");
			Map<String, PmatrixDpdCalculator> dataPointMap = pmatrixDpdCalculatorRepository.getDataPointMap();
			PmatrixDpdCalculatorEmaImpl pmatrixDpdCalculator;
			for (String filename:dataPointMap.keySet()){
				pmatrixDpdCalculator = (PmatrixDpdCalculatorEmaImpl)dataPointMap.get(filename);
				System.out.println("pmatrixDpdCalculator:"
						+ "    pmatrixDpdCalculator.getAlpha(10d): "+pmatrixDpdCalculator.getAlpha() 
						+ "    pmatrixDpdCalculator.getLatestDataValue(1010d): "+pmatrixDpdCalculator.getLatestDataValue()
						+ " : "
						);
				if(pmatrixDpdCalculator.getConfiguration()!=null){
					System.out.println("    configuration:");
					for (NameValuePair nvp : pmatrixDpdCalculator.getConfiguration()){
						System.out.println("       name:"+nvp.getName()
								+ " value:"+nvp.getValue());

					}
				}
			}


		} else {
			System.out.println("did not unmarshal from file:'"+ pmatrixDpdCalculatorRepository.getArchiveFileDirectoryLocation()
					+File.separator+pmatrixDpdCalculatorRepository.getArchiveFileName()+"'");
		}

		System.out.println("end of test:testUnMarshalData()");
	}
}
