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

package org.opennms.features.vaadin.pmatrix.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;



/**
 * 
 * The UiComponentFactory is used to create new UI components 
 * for the application based upon the calling URL request variables
 *
 */
public interface UiComponentFactory {
	
	/**
	 * URL request parameter which this component factory will recognise to create a component
	 */
	public static final String COMPONENT_REQUEST_PARAMETER ="uiComponent";
	
	/**
	 * URL request value for COMPONENT_REQUEST_PARAMETER parameter which this 
	 * component factory will recognize to create a default component.
	 * (if a url request string contains ?<UiComponentFactory.COMPONENT_REQUEST_PARAMETER>=<UiComponentFactory.DEFAULT_COMPONENT_REQUEST_VALUE>
	 * the UiComponentFactory will return a default component )
	 */
	public static final String DEFAULT_COMPONENT_REQUEST_VALUE ="default";

	
	/**
	 * Parses the request message to get parameters to construct the UI component
	 * and returns the appropriately configured Vaddin component for display by the application
	 * 
	 * at a minimum the request must contain the parameter componentName (request.getParameter("componentName"))
	 *  which gives the name of the component instance which the factory must return.
	 * 
	 * @param request request parameters by which this session was initiated
	 * 
	 * @return UI component to be displayed or null if request not recognized
	 */
	Component getUiComponent(VaadinRequest request);
	
	/**
	 * 
	 * @return returns the refresh rate in milliseconds to apply to the returned component displays
	 */
	public Integer getRefreshRate() ;

}
