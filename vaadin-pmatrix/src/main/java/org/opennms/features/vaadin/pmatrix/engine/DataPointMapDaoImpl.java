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


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculator;
import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculatorRepository;
import org.opennms.features.vaadin.pmatrix.model.DataPointDefinition;
import org.opennms.features.vaadin.pmatrix.model.NameValuePair;
import org.opennms.features.vaadin.pmatrix.model.PmatrixSpecification;
import org.opennms.features.vaadin.pmatrix.model.PmatrixSpecificationList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource
public class DataPointMapDaoImpl implements DataPointMapDao {
	private static final Logger LOG = LoggerFactory.getLogger(DataPointMapDaoImpl.class);

	/**
	 * this imports the application context in order to use it to manufacture PmatrixDpdCalculators
	 */
	@Autowired 
	private ApplicationContext applicationContext;

	/**
	 * repository where datapoint calculators are persisted
	 * Must be preloaded from the persistent store before use
	 */
	@Autowired 
	private PmatrixDpdCalculatorRepository pmatrixDpdCalculatorRepository=null;

	/**
	 * Used to store listeners for updates to the datapoint map
	 */
	private CopyOnWriteArrayList<DataPointMapUpdateListener> dataPointMapUpdateListeners = new CopyOnWriteArrayList<DataPointMapUpdateListener>();

	/**
	 * Used to store data point definitions for update by key where key is the filePath
	 */
	private ConcurrentMap<String,PmatrixDpdCalculator> dataPointMap = new ConcurrentHashMap<String,PmatrixDpdCalculator>();

	/**
	 * used to store reference to pmatrix specificationList used in the initialisation of this class
	 */
	private PmatrixSpecificationList pmatrixSpecificationList=null;

	/**
	 * used to notify the table update process that a change has happened to data in the dataPointMapDao
	 */
	private AtomicBoolean changeNotified= new AtomicBoolean(false);

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointMapDao#getDataPointMap()
	 */
	@Override
	public ConcurrentMap<String,PmatrixDpdCalculator> getDataPointMap() {
		return dataPointMap;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointMapDao#addDataPointMapUpdateListener()
	 */
	@Override
	public boolean addDataPointMapUpdateListener( DataPointMapUpdateListener dataPointMapUpdateListener) {
		if (dataPointMapUpdateListener==null) throw new IllegalArgumentException("dataPointMapUpdateListener must not be null");
		boolean updateListenerAdded=dataPointMapUpdateListeners.addIfAbsent(dataPointMapUpdateListener);
		if(LOG.isDebugEnabled()) {
			StringBuffer msg = new StringBuffer("addDataPointMapUpdateListener() ");
			if(updateListenerAdded) {
				msg.append("update listener object "+dataPointMapUpdateListener +" added to dataPointMap. ");
			} else {
				msg.append("update listener object "+dataPointMapUpdateListener +" already present in dataPointMap. ");
			}
			LOG.debug(msg+"Number of update listeners registered "+dataPointMapUpdateListeners.size());
		}
		return updateListenerAdded;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointMapDao#removeDataPointMapUpdateListener()
	 */
	@Override
	public boolean removeDataPointMapUpdateListener(DataPointMapUpdateListener dataPointMapUpdateListener) {
		if (dataPointMapUpdateListener==null) throw new IllegalArgumentException("dataPointMapUpdateListener must not be null");
		boolean updateListenerRemoved=dataPointMapUpdateListeners.remove(dataPointMapUpdateListener);
		if(LOG.isDebugEnabled()) {
			StringBuffer msg = new StringBuffer("removeDataPointMapUpdateListener() ");
			if(updateListenerRemoved) {
				msg.append("update listener object "+dataPointMapUpdateListener +" removed from dataPointMap. ");
			} else {
				msg.append("update listener object "+dataPointMapUpdateListener +" not present in dataPointMap. ");
			}
			LOG.debug(msg+"Number of update listeners registered "+dataPointMapUpdateListeners.size());
		}

		return updateListenerRemoved;
	}


	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointMapDao#notifyChange()
	 */
	@Override
	public void notifyChange() {
		changeNotified.set(true);
	}


	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointMapDao#runUpdate()
	 */
	@Override
	public synchronized void runUpdate(){
		if (LOG.isDebugEnabled()) LOG.debug("runUpdate called by scheduler");
		if (changeNotified.compareAndSet(true, false)) {
			int size=dataPointMapUpdateListeners.size();
			if (LOG.isDebugEnabled()) LOG.debug("runUpdate changeNotified is true - updating "+size+ " pmatrix table instances");
			try{
				Iterator<DataPointMapUpdateListener> dpmudlIterator = dataPointMapUpdateListeners.iterator(); 
				while (dpmudlIterator.hasNext()){
					DataPointMapUpdateListener dpmudl = dpmudlIterator.next();
					dpmudl.dataPointMapUpdated();
				}
			} catch (Exception e){
				LOG.error("Problem updating changes in DataPointMapDao to DataPointMapUpdateListeners: " , e);
			}
			if (LOG.isDebugEnabled()) LOG.debug("runUpdate finished updating "+size+ " pmatrix table instances");
		}
	}


	/**
	 * Adds all of the data point definitions described in the pmatrixSpecification to the DataPointMapDao
	 * each DataPointDefinition is added only once and is uniquely identified by its filepath
	 * @param pmatrixSpecification
	 */
	@PostConstruct
	public void addPmatrixSpecifications() {

		LOG.debug("adding data definitions in pmatrixSpecificationsList to DataPointMap");


		for (PmatrixSpecification  pmatrixSpecification : pmatrixSpecificationList.getPmatrixSpecificationList()){

			String pmatrixName= pmatrixSpecification.getPmatrixName();

			LOG.info("loading data definitions for table name:"+pmatrixName);

			HashSet<String> localCheck = new HashSet<String>();

			List<DataPointDefinition> dataPointDefintions = pmatrixSpecification.getDatapointDefinitions();

			for (DataPointDefinition dpd : dataPointDefintions) {

				if (dpd.getStaticTextCell()!=null && dpd.getStaticTextCell()==true){
					// static text cells do not have data persisted
					if(LOG.isDebugEnabled()) LOG.debug("Static Text Cell dedined for:"+pmatrixName+" for data point definition:"
							+ "' row name:'"
							+ dpd.getRowName()
							+ "' col name:'"
							+ dpd.getColName()
							+ "' staticTextCellString:' "
							+ dpd.getStaticTextCellString() + "'");
				} else if (dpd.getFilePath()==null) {
					LOG.warn("the filePath is null in a dataPointDefinition in pmatrix table name:"+pmatrixName+" for data point definition:"
							+ "' row name:'"
							+ dpd.getRowName()
							+ "' col name:'"
							+ dpd.getColName()
							+ "' filePath:' "
							+ dpd.getFilePath() + "'");
				} else 	if (localCheck.add(dpd.getFilePath())==false){
					LOG.warn("the filePath is duplicated in a dataPointDefinition in pmatrix table name:"+pmatrixName+" for data point definition:"
							+ "' row name:'"
							+ dpd.getRowName()
							+ "' col name:'"
							+ dpd.getColName()
							+ "' filePath:' "
							+ dpd.getFilePath() + "'");
				} else {
					//insert data value point in dataPointMap if not already present.
					boolean duplicate=this.getDataPointMap().containsKey(dpd.getFilePath());
					if (!duplicate){
						// create new calculator object - this is a costly operation so only done if needed
						// the application context allows us to inject any PmatrixDpdCalculator implementation we want
						PmatrixDpdCalculator pmatrixDpdCalculator = (PmatrixDpdCalculator) applicationContext.getBean("pmatrixDpdCalculator");
						if(pmatrixDpdCalculator==null) throw new IllegalStateException("cannot get new pmatrixDpdCalculator instance from application context");

						// check if repository exists and check if new configuration for each object matches persisted object in repository
						if(pmatrixDpdCalculatorRepository==null || ! (pmatrixDpdCalculatorRepository.getRepositoryLoaded())){
							// no repository exists so simply use the new definition
							LOG.info("No data point history is defined or loaded. Creating new datapoint without history for datapoint filepath"+dpd.getFilePath());
						} else {
							if(LOG.isDebugEnabled()) LOG.debug("Checking if history is usable for datapoint filepath"+dpd.getFilePath());
							// repository is defined so try and restore data to datapoints
							if (! pmatrixDpdCalculatorRepository.getDataPointMap().containsKey(dpd.getFilePath())){
								// if the repository does not contain the same data point calculator then use the new one
								// created from the application context
								// note it is possible that new data points have been added for which no history exists
								LOG.info("the history does not contain a persisted version of datapoint name:'"+dpd.getFilePath()+"' Creating a new data point.");
							} else {
								// if the repository contains the same data point calculator then try to load the historical data
								// note it is possible that new data points have been added for which no history exists
								PmatrixDpdCalculator storedPmatrixDpdCalculator =
										pmatrixDpdCalculatorRepository.getDataPointMap().get(dpd.getFilePath());
								// check that we are using the same class and configuration as the stored class
								boolean sameCalculatorConfiguration=true;
								if (storedPmatrixDpdCalculator==null){
									if(LOG.isDebugEnabled()) LOG.debug("the persisted version of datapoint name:'"+dpd.getFilePath()+"' is null.");
									sameCalculatorConfiguration=false;
								} else {
									// check that same class used to save and load values
									Class<? extends PmatrixDpdCalculator> x = storedPmatrixDpdCalculator.getClass();
									Class<? extends PmatrixDpdCalculator> y = pmatrixDpdCalculator.getClass();
									if (! x.getName().equals(y.getName())) {
										sameCalculatorConfiguration=false;
										//TODO may want to check class assignability further as new version of class may be able to load old history
										// not same class used to load and save values
									} else if(pmatrixDpdCalculator.getConfiguration()==null){
										if(LOG.isDebugEnabled()) LOG.debug("the DpdCalculator specification for datapoint name:'"+dpd.getFilePath()+"' has a null configuration.");
									} else {
										// the DpdCalculator specification is not null test if the stored DpdCalculator spec is null
										if(storedPmatrixDpdCalculator.getConfiguration()==null){
											if(LOG.isDebugEnabled()) LOG.debug("the persisted version of datapoint name:'"+dpd.getFilePath()+"' has a null configuration.");
											sameCalculatorConfiguration=false;
										} else {
											if (pmatrixDpdCalculator.getConfiguration().size()!=storedPmatrixDpdCalculator.getConfiguration().size()){
												// configuration contents different size
												LOG.warn("the persisted version of dpdCalculator for datapoint name:'"+dpd.getFilePath()+"' has a different number of config items to the new specification."
														+ " New specification config no of items:"+pmatrixDpdCalculator.getConfiguration().size()
														+ " Persisted specification config no of items "+storedPmatrixDpdCalculator.getConfiguration().size());
												sameCalculatorConfiguration=false;
											} else {
												// check configuration contents are the same
												HashMap<String,String> newConfigHm = new HashMap<String, String>();
												Iterator<NameValuePair> nvpiterator = pmatrixDpdCalculator.getConfiguration().iterator();
												// check for duplicate entries
												while(nvpiterator.hasNext()){
													NameValuePair nameValuePair = nvpiterator.next();
													if (newConfigHm.put(nameValuePair.getName(), nameValuePair.getValue())!=null){
														sameCalculatorConfiguration=false;
														//configuration list has duplicate entries
														LOG.warn("pmatrixconfiguration has duplicate entries for DpdCalculator configuration property:"+nameValuePair.getName());
													}
												}
												Iterator<NameValuePair> storednvpiterator = storedPmatrixDpdCalculator.getConfiguration().iterator();
												while(storednvpiterator.hasNext()){
													NameValuePair storednameValuePair = storednvpiterator.next();
													if ( (! newConfigHm.containsKey( storednameValuePair.getName() ))
															|| (! newConfigHm.get(storednameValuePair.getName()).equals(storednameValuePair.getValue() )) 
															){
														//configuration list values are not the same
														LOG.warn("new pmatrixconfiguration has different DpdCalculator configuration property than persisted value Key:'"+storednameValuePair.getName()
																+ "' new value:"+newConfigHm.get(storednameValuePair.getName())
																+ "' persisted value"+storednameValuePair.getValue()
																+ "'");
														sameCalculatorConfiguration=false;
													}
												}
											}
										}
									}
								}
								if (sameCalculatorConfiguration==true){
									// use the stored class instead of the new class
									pmatrixDpdCalculator=storedPmatrixDpdCalculator;
									if(LOG.isDebugEnabled()) LOG.debug("Using a persisted version of:"+pmatrixDpdCalculator.getClass().getName()
											+" from pmatrixDpdCalculatorRepository for for datapoint name:'"+dpd.getFilePath()+"'");

								} else {
									LOG.error("The persisted history for datapoint name:'"+dpd.getFilePath()+"'"
											+ " does not have a configuration matching the current configuration. Will create a new datapoint with no history."
											+ " Persisted class :"+pmatrixDpdCalculator.getClass().getName()+" Configuration class: "+storedPmatrixDpdCalculator.getClass());
								}
							} 
						}

						// add the data definition to the data point map and check again if duplicated
						duplicate=(this.getDataPointMap().putIfAbsent(dpd.getFilePath(), pmatrixDpdCalculator) != null);
					}
					if (duplicate) {
						// duplicate data point definitions aren't a problem since more than one table can reference a data point
						LOG.info("For information only: the filePath in a dataPointDefinition is already defined in another pmatrix table. "
								+ "This dataPointDefinition: "
								+ " pmatrix table:'"
								+ pmatrixName
								+ "' row name:'"
								+ dpd.getRowName()
								+ "' col name:'"
								+ dpd.getColName()
								+ "' filePath:' "
								+ dpd.getFilePath() + "'");
					}

				}
			}
		}
		// save the list
		if(pmatrixDpdCalculatorRepository!=null){
			// attach the newly loaded dataPointMap to the persister
			pmatrixDpdCalculatorRepository.setDataPointMap(dataPointMap);;

			if (persist()){
				LOG.info("newly loaded data point map has been persisted at startup");
			} else LOG.error("unable to persist new datapoint map at startup");
		}

	}

	@Override
	public synchronized boolean persist(){
		if(pmatrixDpdCalculatorRepository==null) {
			if(LOG.isDebugEnabled()) LOG.debug("data point map has not been persisted because the pmatrixDpdCalculatorRepository is null");
			return false;
		}
		if (pmatrixDpdCalculatorRepository.persist()){
			if(LOG.isDebugEnabled()) LOG.debug("data point map has been persisted");
			return true;
		} else return false;
	}

	/**
	 * @return the pmatrixSpecificationList
	 */
	@Override
	public PmatrixSpecificationList getPmatrixSpecificationList() {
		return pmatrixSpecificationList;
	}

	/**
	 * @param pmatrixSpecificationList the pmatrixSpecificationList to set
	 */
	@Override
	public void setPmatrixSpecificationList(PmatrixSpecificationList pmatrixSpecificationList) {
		this.pmatrixSpecificationList = pmatrixSpecificationList;
	}
	
	// jmx management data
	@ManagedAttribute(description="Count of instances of pmatrix displays registered for updates.")
	public int getDataPointMapUpdateListenersCount(){
		return dataPointMapUpdateListeners.size();
	}

	/**
	 * @param pmatrixDpdCalculatorRepository the pmatrixDpdCalculatorRepository to set
	 */
	public void setPmatrixDpdCalculatorRepository(
			PmatrixDpdCalculatorRepository pmatrixDpdCalculatorRepository) {
		this.pmatrixDpdCalculatorRepository = pmatrixDpdCalculatorRepository;
	}


}
