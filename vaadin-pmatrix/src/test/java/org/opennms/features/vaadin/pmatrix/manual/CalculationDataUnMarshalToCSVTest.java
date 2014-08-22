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

import java.util.Map;

import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculator;
import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculatorRepository;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.support.StaticApplicationContext;

import junit.framework.TestCase;

/**
 * Unmarshals data into csv file
 * run as junit test or use
 * mvn exec:java -Dexec.mainClass="org.opennms.features.vaadin.pmatrix.manual.CalculationDataUnMarshalToCSVTest" -Dexec.args="./PmatrixHistory/historyConfig.xml" -Dexec.classpathScope=test "   
 * @author opennms
 *
 */
public class CalculationDataUnMarshalToCSVTest extends TestCase {

	// values used only for this test
	//File file = new File("target/data-marshaltestfile.xml");
	//private static String archiveFileLocation="file:/home/isawesome/devel/gitrepo/entimoss-misc/opennms-pmatrix/vaadin-pmatrix/PmatrixHistory/historyConfig.xml";
	private static String archiveFileDirectoryLocation="file:./PmatrixHistory";

	private static String archiveFileName="historyConfig.xml";

	private static int archiveFileMaxNumber=2;


	// static so that lives between tests on this class
	private static StaticApplicationContext appContext= new StaticApplicationContext();

	public static void main(String[] args) {

		System.out.println(""
				+"***********************\n"
				+ "Pmatrix Data to CSV    \n"
				+ "***********************\n");
		if (args.length < 1) {
			System.out.println("This program unmarshalls a Pmatrix data file into csv data written to standard out\n"
					+ " To change default values supply arguments: filename.\n");
		} else {
			String filename = args[0];
			File file = new File(filename);
			archiveFileName= file.getName();
			archiveFileDirectoryLocation=("file:"+file.getParent());
		}

		CalculationDataUnMarshalToCSVTest csvtest=new CalculationDataUnMarshalToCSVTest();
		csvtest.testLoadAppContext();
		csvtest.testUnMarshalCSVData();

	}


	// set up application context
	public void testLoadAppContext(){
		System.out.println("start of test:testLoadAppContext()");
		// set up app context to load ResourceLoader
		appContext.registerSingleton("beanPostProcessor", CommonAnnotationBeanPostProcessor.class);
		appContext.registerSingleton("pmatrixDpdCalculatorRepository", PmatrixDpdCalculatorRepository.class);
		appContext.refresh();
		System.out.println("end of test:testLoadAppContext()");
	}


	public void testUnMarshalCSVData(){
		System.out.println("start of test:testUnMarshalCSVData()");

		PmatrixDpdCalculatorRepository pmatrixDpdCalculatorRepository=
				(PmatrixDpdCalculatorRepository) appContext.getBean("pmatrixDpdCalculatorRepository");


		pmatrixDpdCalculatorRepository.setArchiveFileDirectoryLocation(archiveFileDirectoryLocation);
		pmatrixDpdCalculatorRepository.setArchiveFileName(archiveFileName);
		pmatrixDpdCalculatorRepository.setArchiveFileMaxNumber(archiveFileMaxNumber);
		pmatrixDpdCalculatorRepository.setPersistHistoricData(true);

		boolean success = pmatrixDpdCalculatorRepository.load();
		if(success) {
			System.out.println("unmarshalled from file:''"+ pmatrixDpdCalculatorRepository.getArchiveFileDirectoryLocation()
					+File.separator+pmatrixDpdCalculatorRepository.getArchiveFileName()+"'\n"
							+ "START OF DATA\n"
							+ "*************");
			Map<String, PmatrixDpdCalculator> dataPointMap = pmatrixDpdCalculatorRepository.getDataPointMap();
			PmatrixDpdCalculator pmatrixDpdCalculator;

			for (String filename:dataPointMap.keySet()){
				pmatrixDpdCalculator = dataPointMap.get(filename);
				System.out.println(filename+", "+pmatrixDpdCalculator.getClass().getName()+", "+pmatrixDpdCalculator.dataToCSV());
			}

		} else {
			System.out.println("did not unmarshal from file:'"+ pmatrixDpdCalculatorRepository.getArchiveFileDirectoryLocation()
					+File.separator+pmatrixDpdCalculatorRepository.getArchiveFileName()+"'");
		}

		System.out.println(""
				+ "*************\n"
				+ "END OF DATA\n"
				+ "end of test:testUnMarshalCSVData()");
	}
}
