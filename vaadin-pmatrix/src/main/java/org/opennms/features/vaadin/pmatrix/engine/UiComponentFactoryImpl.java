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

import org.opennms.features.vaadin.pmatrix.model.PmatrixSpecification;
import org.opennms.features.vaadin.pmatrix.model.PmatrixSpecificationList;
import org.opennms.features.vaadin.pmatrix.ui.PmatrixTable;
import org.opennms.features.vaadin.pmatrix.ui.UiComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
public class UiComponentFactoryImpl implements UiComponentFactory, ApplicationContextAware {
	private static final Logger LOG = LoggerFactory.getLogger(UiComponentFactoryImpl.class);
	
	private PmatrixSpecificationList pmatrixSpecificationList=null;
	
	private DataPointMapDao dataPointMapDao;
	
	public DataPointMapDao getDataPointMapDao() {
		return dataPointMapDao;
	}
	
	/**
	 * this imports the application context in order to use in this factory
	 */
	//TODO @Autowired removed because doesn't work in OSGi. Using ApplicationContextAware instead
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext=applicationContext;
	}
	
	@Autowired
	public void setDataPointMapDao(DataPointMapDao dataPointMapDao) {
		this.dataPointMapDao = dataPointMapDao;
	}
	
	/**
	 * @return the pmatrixSpecificationList
	 */
	public PmatrixSpecificationList getPmatrixSpecificationList() {
		return pmatrixSpecificationList;
	}

	/**
	 * @param pmatrixSpecificationList the pmatrixSpecificationList to set
	 */
	@Autowired
	public void setPmatrixSpecificationList(PmatrixSpecificationList pmatrixSpecificationList) {
		this.pmatrixSpecificationList = pmatrixSpecificationList;
	}

	@Override
	public Component getUiComponent(VaadinRequest request) {
		
		//works with the following URL examples
		//http://localhost:8080/vaadin-pmatrix/?debug&uiComponent=default
		//http://localhost:8080/vaadin-pmatrix/?uiComponent=default
		
		String componentName = request.getParameter(COMPONENT_REQUEST_PARAMETER);
		
		// no component name defined so return null
		if (componentName==null) return null;

		//search for specification and construct a component with this name
		if (pmatrixSpecificationList==null) throw new IllegalStateException("pmatrixSpecificationList cannot be null");
		
		PmatrixSpecification pmatrixSpecification=null;
		for (PmatrixSpecification pms : pmatrixSpecificationList.getPmatrixSpecificationList()){
			if(componentName.equals(pms.getPmatrixName())) {
				pmatrixSpecification=pms;
				break;
			}
		}
	
		// there is no specification for this pmatrix or else we have an unrecognized request so return null
		if (pmatrixSpecification==null) return null;
		
		// otherwise return the constructed table
		if(LOG.isDebugEnabled()) LOG.debug("constructing a new pmatrixTable for UI with pmatrixName:'"+pmatrixSpecification.getPmatrixName()
				+ "' pmatrixTitle:'"+pmatrixSpecification.getPmatrixTitle()+"'");
		
		if (applicationContext==null) throw new IllegalStateException("applicationContext cannot be null");
		
		PmatrixDataSourceImpl pmatrixDataSource = (PmatrixDataSourceImpl) applicationContext.getBean("pmatrixDataSource");
		if(pmatrixDataSource==null) throw new IllegalStateException("cannot get new pmatrixDatasource instance from application context");

		pmatrixDataSource.setPmatrixSpecification(pmatrixSpecification);
				
		PmatrixTable pmatrixTable = new PmatrixTable(pmatrixDataSource);

		return pmatrixTable; 
	}

	@Override
	public Integer getRefreshRate() {
		if( pmatrixSpecificationList.getRefreshRate()==null){
			return 10000;
		} else 
		return pmatrixSpecificationList.getRefreshRate();
	}





}
