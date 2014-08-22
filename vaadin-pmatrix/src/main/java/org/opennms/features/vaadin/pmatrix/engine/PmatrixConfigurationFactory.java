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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculator;
import org.opennms.features.vaadin.pmatrix.model.NameValuePair;
import org.opennms.features.vaadin.pmatrix.model.PmatrixDpdCalculatorConfig;
import org.opennms.features.vaadin.pmatrix.model.PmatrixSpecificationList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * This class is used by the application context to bootstrap the configuration
 * for the pmatrix displays. It unmarshals the configuration from the configuration files
 * and provides methods which will return each configured data type to the requesting objects
 * 
 * SEE http://docs.spring.io/spring/docs/3.0.x/reference/beans.html 3.3.2.3 Instantiation using an instance factory method
 * TODO pmatrixSpecificationList
 * TODO pmatrixDpdCalculator - prototype ?
 * 
 */

public class PmatrixConfigurationFactory  implements ResourceLoaderAware{
	private static final Logger LOG = LoggerFactory.getLogger(PmatrixConfigurationFactory.class);

	private String configFileLocation=null;

	private PmatrixSpecificationList pmatrixSpecificationList=null;

	private String pmatrixDpdCalculatorClassName=null;

	private PmatrixDpdCalculatorConfig pmatrixDpdCalculatorConfig=null;
	
	/**
	 * Used to ensure configuration only loaded once
	 */
	private AtomicBoolean atomicInitialized = new AtomicBoolean(false);

	/**
	 *  used by Spring ResourceLoaderAware interface
	 */
	private ResourceLoader resourceLoader;

	/**
	 * used by Spring ResourceLoaderAware interface to inject resource loader into bean
	 */
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	/**
	 * returns the PmatrixSpecificationList singleton which has been loaded by this factory
	 * @return
	 */
	public PmatrixSpecificationList returnPmatrixSpecificationList(){
		if (pmatrixSpecificationList==null) throw new IllegalStateException("The pmatrixSpecificationList must be loaded before being used");
		return this.pmatrixSpecificationList;
	}

	/**
	 * 
	 * @return returns a new instance of a PmatrixDpdCalculator loaded with configuration given in the PmatrixSpecificationList
	 */
	public PmatrixDpdCalculator createPmatrixDpdCalculator(){
		if (pmatrixDpdCalculatorClassName==null) throw new IllegalStateException("pmatrixDpdCalculatorClassName must not be null");

		PmatrixDpdCalculator pmatrixDpdCalculator = null;
		try {
			Class <? extends PmatrixDpdCalculator>  c = Class.forName (pmatrixDpdCalculatorClassName).asSubclass (PmatrixDpdCalculator.class);
			pmatrixDpdCalculator=c.newInstance();

		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("cannot instantiate pmatrixDpdCalculatorClassName:'"+pmatrixDpdCalculatorClassName+"'",e);
		} catch (InstantiationException e) {
			throw new IllegalStateException("cannot instantiate pmatrixDpdCalculatorClassName:'"+pmatrixDpdCalculatorClassName+"'",e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("cannot instantiate pmatrixDpdCalculatorClassName:'"+pmatrixDpdCalculatorClassName+"'",e);
		}

		if (pmatrixDpdCalculatorConfig.getConfiguration()!=null){
			pmatrixDpdCalculator.setConfiguration(pmatrixDpdCalculatorConfig.getConfiguration());
		} else LOG.debug("configration not defined in specification for new "+pmatrixDpdCalculatorClassName );

		return pmatrixDpdCalculator;

	}


	/**
	 * This method loads the configuration from the external file. 
	 * It is called post construct so that the pmatrixSpecificationList is populated when needed
	 * NOTE - @PostConstruct problems in OSGi so using init-method="loadConfiguration" in application context
	 */
	@PostConstruct
	private void loadConfiguration(){

		if (atomicInitialized.compareAndSet(false, true)) {
			// load configuration if not loaded else just return

			LOG.info("pmatrix loading configuration from configFileLocation: "+ configFileLocation);

			File configFile=null;
			
			Resource resource = resourceLoader.getResource(configFileLocation);
			
			if(!resource.exists()){
				throw new IllegalStateException("problem loading configuration file, configFileLocation='"+configFileLocation+"' does not exist");
			} else {
				
				try {
					//configFile = new File(resource.getURL().getFile());	
					
					// see http://stackoverflow.com/questions/1043109/why-cant-jaxb-find-my-jaxb-index-when-running-inside-apache-felix
					// need to provide bundles class loader
					ClassLoader cl = org.opennms.features.vaadin.pmatrix.model.DataPointDefinition.class.getClassLoader();
					JAXBContext jaxbContext = JAXBContext.newInstance("org.opennms.features.vaadin.pmatrix.model", cl);

					//JAXBContext jaxbContext = JAXBContext.newInstance("org.opennms.features.vaadin.pmatrix.model");
					Unmarshaller jaxbUnMarshaller = jaxbContext.createUnmarshaller();
					
					Object unmarshalledObject = jaxbUnMarshaller.unmarshal(resource.getInputStream());
					//Object unmarshalledObject = jaxbUnMarshaller.unmarshal(configFile);
					
					if (!(unmarshalledObject instanceof PmatrixSpecificationList)){
						throw new IllegalStateException("problem loading configuration file, configFileLocation='"+configFileLocation+"' "
								+ "unmarshalledObject:'"+unmarshalledObject.getClass().getName()
								+ "' is not an instance of PmatrixSpecificationList");
					} else {
						this.pmatrixSpecificationList=(PmatrixSpecificationList) unmarshalledObject;
						LOG.info("pmatrix configuration successfully loaded from: "+ configFileLocation);
						LOG.info("Loaded pmatrixSpecificationList:"
								+ "\n********************\n"
								+ pmatrixSpecificationList.toString()
								+"\n********************\n");

						// get the DpdCalculatorClassName
						pmatrixDpdCalculatorConfig = pmatrixSpecificationList.getPmatrixDpdCalculatorConfig();
						if (pmatrixDpdCalculatorConfig==null) throw new IllegalStateException("pmatrixDpdCalculatorConfig is undefined in config File='"+configFileLocation+"'");
						pmatrixDpdCalculatorClassName= pmatrixDpdCalculatorConfig.getPmatrixDpdCalculatorClassName();
						if (pmatrixDpdCalculatorClassName==null) throw new IllegalStateException("pmatrixDpdCalculatorClassName is undefined in config File ='"+configFileLocation+"'");
						// check if defined pmatrixDpdCalculatorClass can be loaded by class loader
						Class<?> checkClass=null;
						try {
							checkClass =Class.forName(pmatrixDpdCalculatorClassName);
						} catch (ClassNotFoundException e) {
							throw new IllegalStateException("Cannot instantiate class pmatrixDpdCalculatorClassName='"+pmatrixDpdCalculatorClassName
									+ "' in config File ='"+configFileLocation+"'");
						}
						// check if defined pmatrixDpdCalculatorClass extends PmatrixDpdCalculator
						if (!(PmatrixDpdCalculator.class.isAssignableFrom(checkClass))){
							throw new IllegalStateException("class pmatrixDpdCalculatorClassName='"+pmatrixDpdCalculatorClassName
									+ "' in config File ='"+configFileLocation+"' is not an instance of "+ PmatrixDpdCalculator.class.getName());
						}
						//check configuration list exists
						List<NameValuePair> configuration = pmatrixDpdCalculatorConfig.getConfiguration();
						if (configuration==null) {
							LOG.warn("no configuration set for pmatrixDpdCalculatorClassName='"+pmatrixDpdCalculatorClassName);
						}

					}
				} catch (IOException e) {
					throw new IllegalStateException("problem loading configuration file, configFileLocation='"+configFileLocation+"' :", e);
				} catch (JAXBException e) {
					throw new IllegalStateException("problem unmarshalling configuration file, configFileLocation='"+configFileLocation+"' :", e);
				}  
			}  

		}

	}

	/**
	 * @return the configFileLocation
	 */
	public String getConfigFileLocation() {
		return configFileLocation;
	}

	/**
	 * location and name of the configuration file to load
	 * @param configFileLocation the configFileLocation to set
	 */
	public void setConfigFileLocation(String configFileLocation) {
		this.configFileLocation = configFileLocation;
	}

	@PreDestroy
	private void closedown(){
		LOG.info("pmatrix saving configuration and closing down");
	}



}
