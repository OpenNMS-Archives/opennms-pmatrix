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

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Component;


public interface PmatrixDataSource {
	
	/**
	 * @return componentHeight The recommended component height in CSS as per Vaadin spec 
	 * for the component (Table) using this data source. If null default values are used
	 */
	public String getComponentHeight();
	
	/**
	 * @param componentHeight The recommended component height in CSS as per Vaadin spec 
	 * for the component (Table) using this data source. If null default values are used
	 */
	public void setComponentHeight(String componentHeight);
	
	/**
	 * @return componentWidth The recommended component width in CSS as per Vaadin spec 
	 * for the component (Table) using this data source. If null default values are used
	 */
	public String getComponentWidth();
	
	/**
	 * @param componentWidth The recommended component width in CSS as per Vaadin spec 
	 * for the component (Table) using this data source. If null default values are used
	 */
	public void setComponentWidth(String componentWidth);

	/**
	 * @return the data container (IndexedContainer) to be used by the pmatrix table. Note that the properties of the 
	 * IndexedContainer must be set before adding to the table otherwise they will not display.
	 * This is used by Spring to inject the data into to the table
	 * 
	 */
	public IndexedContainer getDataSourceContainer();
	
	/**
	 * 
	 * @return returns a String containing the name for the table 
	 */
	public String getPmatrixTitle();
		
	/**
	 * 
	 * @param pmatrixTitle a String containing the name for the table 
	 */
	public void setPmatrixTitle(String pmatrixTitle);
	

	/**
	 * @return pmatrixName is the name which is used as a handle for this pmatrix data source in URLs
	 */
	public String getPmatrixName();

	/**
	 * @param pmatrixName is the name which is used as a handle for this pmatrix data source in URLs
	 */
	public void setPmatrixName(String pmatrixName);
	
	/**
	 * if hidePmatrixHeaders is true, no row or column headers are displayed
	 * @return the hidePmatrixHeaders
	 */
	public boolean getHidePmatrixHeaders();

	/**
	 * if hidePmatrixHeaders is true, no row or column headers are displayed
	 * @param hidePmatrixHeaders the hidePmatrixHeaders to set
	 */
	public void setHidePmatrixHeaders(boolean hidePmatrixHeaders);
	
	/**
	 * Creates a HTML encoded string for use as a key to describe the cells in a pmatrix table.
	 * THese values are set from the specification
	 *    latestDataValueDescription  description of latest data value. if null getPmatrixCellKeyText returns an empty string ""
	 *    secondaryNumberDescription  description of secondary number. If null, no secondary number is generated in key
	 *    leftRangeArrowDescription description of left range arrow. If null, no arrow is generated in key
	 *    rightRangeArrowDescription  description of right range arrow. If null, no arrow is generated in key
	 *    descriptiveText general descriptive text. If null, no descriptive text is generated
	 * @return HTML encoded table of key for the cells and descriptive text
	 */
	public String getPmatrixDescription();

	/**
	 * The dataPointMapDao which is updated by new data and used to back this pmatrix
	 * @return
	 */
	public DataPointMapDao getDataPointMapDao() ;

	/**
	 * 
	 * @param dataPointMapDao The dataPointMapDao which is updated by new data and used to back this pmatrix
	 */
	public void setDataPointMapDao(DataPointMapDao dataPointMapDao) ;
	
	/**
	 * Used to pass back to the data source a reference to the component for which this is the datasource
	 * This allows synchronization of UI
	 * @param attachedComponent component which is using this container
	 */
	public void setAttachedComponent(Component attachedComponent);
	
	/**
	 * Causes this PmatrixDatasource instance to register with the DataPointMapDao for events
	 * the DataPointMapDao must be set before this is called
	 * @throws Exception
	 */
	public void registerWithDataPointMapDao() throws Exception;
	
	/**
	 * Causes this PmatrixDatasource instance to unregister with the DataPointMapDao for events
	 * the DataPointMapDao must be set before this is called
	 * @throws Exception
	 */
	public void unRegisterWithDataPointMapDao() throws Exception;

}