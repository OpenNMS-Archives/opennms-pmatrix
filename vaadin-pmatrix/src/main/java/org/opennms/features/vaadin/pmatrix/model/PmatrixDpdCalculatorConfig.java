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

import java.util.List;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlTransient 
@XmlSeeAlso({PmatrixDpdCalculatorConfigImpl.class})
public abstract class PmatrixDpdCalculatorConfig {

	/**
	 * Returns the fully qualified class name of the pmatrixDpdCalculator to be used by the tables
	 * 
	 * @return the pmatrixDpdCalculator
	 */
	public abstract String getPmatrixDpdCalculatorClassName();

	/**
	 * Sets the fully qualified class name of the pmatrixDpdCalculator to be used by the tables
	 * (Must be defined such that we can use Class.forName to load the definition. 
	 * eg PmatrixDpdCalculatorImpl.class.getName() = org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculatorEmaImpl)
	 * @param pmatrixDpdCalculator the pmatrixDpdCalculator to set
	 */
	public abstract void setPmatrixDpdCalculatorClassName(String pmatrixDpdCalculatorClassName);

	/**
	 * @return the configuration
	 */
	public abstract List<NameValuePair> getConfiguration();

	/**
	 * Sets the name value pair configuration for the pmatrixDpdCalculator. The required names and values are
	 * determined by the needs of the particular PmatrixDpdCalculator implementation used
	 * @param configuration the configuration to set
	 */
	public abstract void setConfiguration(List<NameValuePair> configuration);


}
