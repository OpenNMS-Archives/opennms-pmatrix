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

import java.util.concurrent.ConcurrentMap;

import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculator;
import org.opennms.features.vaadin.pmatrix.model.PmatrixSpecificationList;

public interface DataPointMapDao {

	
	/**
	 * gets the pmatrixSpecficationList used to define the datapoints in the dao
	 * @param pmatrixSpecificationList the pmatrixSpecificationList to set
	 */
	public PmatrixSpecificationList getPmatrixSpecificationList();

	/**
	 * sets the pmatrixSpecficationList used to define the datapoints in the dao
	 * @param pmatrixSpecificationList the pmatrixSpecificationList to set
	 */
	public void setPmatrixSpecificationList(PmatrixSpecificationList pmatrixSpecificationList);
		

	/**
	 * @return the dataPointMap
	 * The DataPointMap is used by the update data thread to store relevant data for update
	 * The key of the data point map is the filepath from the datapoint definition. The filepath is the
	 * key in each message from OpenNMS which identifies uniquely the message
	 */
	public ConcurrentMap<String, PmatrixDpdCalculator> getDataPointMap();
	
	/**
	 * Adds a DataPointMapUpdateListener to the DataPointMapDao. 
	 * Each DataPointMapUpdateListener instance will only be added once no matter how many times this method is called.
	 * @param dataPointMapUpdateListener
	 * @return true if the element was added
	 */
	public boolean addDataPointMapUpdateListener(DataPointMapUpdateListener dataPointMapUpdateListener);

	/**
	 * removes a DataPointMapUpdateListener to the DataPointMapDao. 
	 * If the supplied DataPointMapUpdateListener is not present, this method will do nothing and return fales.
	 * @param dataPointMapUpdateListener
	 * @return true if this list contained the specified element
	 */
	public boolean removeDataPointMapUpdateListener(
			DataPointMapUpdateListener dataPointMapUpdateListener);
	
	/**
	 * Used to signal the DataPointMapUpdateListeners registered with this class using addDataPointMapUpdateListener()
	 * that the dao data has changed. When this is called, the dataPointMapUpdated() call back methods in 
	 * all of the registered DataPointMapUpdateListener objects are called.
	 * (This method is synchronised so that it can be used to force an update by the heartbeat process and the data listener process)
	 */
	public void notifyChange();
	
	/**
	 * This method checks if there have been any changes notified and then runs an update to all of the registered
	 * pmatrix tables. This must be scheduled externally in a thread (by spring)
	 */
	public void runUpdate();
	
	/**
	 * used to tell the DataPointMapDao to persist itself. 
	 * note this operation is not synchronised. Users must ensure that no changes are happening to the 
	 * DataPointMapDao while it is being persisted
	 * @return true if persist operation was successful
	 */
	public boolean persist();

}