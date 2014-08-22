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

package org.opennms.features.vaadin.pmatrix.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="pmatrixDpdCalculatorConfig")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name="PmatrixDpdCalculatorConfig")
public class PmatrixDpdCalculatorConfigImpl extends PmatrixDpdCalculatorConfig {

	String pmatrixDpdCalculatorClassName=null;
	
	boolean loadHistoryOnStartup=false;

	List<NameValuePair> configuration=new ArrayList<NameValuePair>();

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.model.PmatrixDpdCalculatorConfig#getPmatrixDpdCalculator()
	 */
	@Override
	public String getPmatrixDpdCalculatorClassName() {
		return pmatrixDpdCalculatorClassName;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.model.PmatrixDpdCalculatorConfig#setPmatrixDpdCalculator(java.lang.String)
	 */
	@XmlAttribute(required=true)
	@Override
	public void setPmatrixDpdCalculatorClassName(String pmatrixDpdCalculatorClassName) {
		this.pmatrixDpdCalculatorClassName = pmatrixDpdCalculatorClassName;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.model.PmatrixDpdCalculatorConfig#getConfiguration()
	 */
	@Override
	public List<NameValuePair> getConfiguration() {
		return configuration;
	}
	
	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.model.PmatrixDpdCalculatorConfig#setConfiguration(java.util.List)
	 */
	@Override
	@XmlElementWrapper(name="configuration")
	@XmlElement(name="property")
	public void setConfiguration(List<NameValuePair> configuration) {
		this.configuration = configuration;
	}

	

}
