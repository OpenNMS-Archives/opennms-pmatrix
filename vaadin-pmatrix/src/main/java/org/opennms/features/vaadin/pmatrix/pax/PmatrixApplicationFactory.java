/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2012 The OpenNMS Group, Inc.
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
package org.opennms.features.vaadin.pmatrix.pax;

import org.ops4j.pax.vaadin.AbstractApplicationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.UI;

/**
 * A factory for creating Pmatrix application objects.
 * 
 * @author cgallen@opennms.org
 */
public class PmatrixApplicationFactory extends AbstractApplicationFactory {
	private static final Logger LOG = LoggerFactory.getLogger(PmatrixApplicationFactory.class);


	private SpringContextActivator springContextActivator=null;
	
	/**
	 * @return the springContextActivator
	 */
	public SpringContextActivator getSpringContextActivator() {
		return springContextActivator;
	}

	/**
	 * @param springContextActivator the springContextActivator to set
	 */
	public void setSpringContextActivator(SpringContextActivator springContextActivator) {
		this.springContextActivator = springContextActivator;
	}


    /* (non-Javadoc)
     * @see org.ops4j.pax.vaadin.AbstractApplicationFactory#getUI()
     */
    @Override
    public UI createUI() {
    	UI ui =null;
    	try{
    	 ui = (UI) springContextActivator.getContext().getBean("pmatrixApplication");
    	} catch(Exception e){
    		LOG.error("problem creating UI from springContextActivator:",e);
    	}
        return ui;
    }

    /* (non-Javadoc)
     * @see org.ops4j.pax.vaadin.AbstractApplicationFactory#getUIClass()
     */
    @Override
    public Class<? extends UI> getUIClass() {
    	Object obj = springContextActivator.getContext().getBean("pmatrixApplication");
    	return (Class<? extends UI>) obj.getClass();
    }



}
