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

package org.opennms.features.vaadin.pmatrix.calculator;

import java.util.List;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.opennms.features.vaadin.pmatrix.model.DataPointDefinition;
import org.opennms.features.vaadin.pmatrix.model.NameValuePair;

/**
 * Interface for objects which persist calculations and update them based upon 
 * new data supplied by the updateCalculation method. 
 * Each PmatrixDpdCalculator implementation is responsible for its own persistence
 * 
 */
@XmlTransient 
@XmlSeeAlso({PmatrixDpdCalculatorImpl.class, PmatrixDpdCalculatorEmaImpl.class, PmatrixDpdCalculatorSimpleMovingAvgImpl.class})
public abstract class PmatrixDpdCalculator {
	
	/** 
	 * sets the default timeouts for calculators
	 * to missed 2.5 * 15 minute samples
	 */
	public static final long DEFAULT_TIMEOUT= 2250000; //2.5*15*60*1000;
	
	/**
	 * used to set the configuration value of the timeout between samples
	 */
	public static final String SAMPLE_TIMEOUT_KEY="sampleTimeout";

	/**
	 * @return the fully qualified class name of the PmatrixDpdCalculator implementation  used to persist data
	 */
	public abstract String getPersistingPmatrixDpdCalculatorClassName();

	/**
	 * @param the fully qualified class name of the PmatrixDpdCalculator implementation  being used
	 */
	public abstract void setPersistingPmatrixDpdCalculatorClassName(String persistingPmatrixDpdCalculatorClassName);

	/**
	 * @return updates the given data point definition with the result of the latest calculation
	 * and sets MouseOverText text to latestValue and latestTimestamp and other calculated values.
	 */
	public abstract DataPointDefinition updateDpd(DataPointDefinition dpd);

	/**
	 * Updates the running calculations using the latestValue and latestTimestamp
	 * This method should be overridden for new algorithms
	 * @param latestValue
	 * @param latestTimestamp
	 */
	public abstract void updateCalculation(Double latestValue, Long latestTimestamp);

	/**
	 * Sets the name value pair list of configuration parameters to be passed to the PmatrixDpdCalculator
	 * Note that the configuration parameters required are not specified by this interface and may be different for each
	 * PmatrixDpdCalculator implementation
	 * @param configuration for this calculator in the form of a name value pair
	 */
	public abstract void setConfiguration(List<NameValuePair> configuration);

	/**
	 * @return the name value pair configuration for this PmatrixDpdCalculator
	 */
	public abstract List<NameValuePair> getConfiguration();

	/**
	 * @return the latestDataValueRange
	 */
	public abstract Integer getLatestDataValueRange();

	/**
	 * @param latestDataValueRange the latestDataValueRange to set
	 */
	public abstract void setLatestDataValueRange(Integer latestDataValueRange);

	/**
	 * @return the secondaryValue
	 */
	public abstract Double getSecondaryValue() ;

	/**
	 * @param secondaryValue the secondaryValue to set
	 */
	public abstract void setSecondaryValue(Double secondaryValue);

	/**
	 * @return the secondaryValueRange
	 */
	public abstract Integer getSecondaryValueRange() ;

	/**
	 * @param secondaryValueRange the secondaryValueRange to set
	 */
	public abstract void setSecondaryValueRange(Integer secondaryValueRange);

	/**
	 * 	
	 * The calculated leftTrendArrow value is used to determine the left hand trend arrow in each cell display.
	 * It can have values:
	 * 	    DataPointDefinition.TREND_UP indicates that the trend for this value is upwards
	 *      DataPointDefinition.TREND_LEVEL Indicates that the trend for this value is level (no change)
	 *      DataPointDefinition.TREND_DOWN Indicates that the trend for this value is down
	 * @return the leftTrendArrow
	 */
	public abstract String getLeftTrendArrow();

	/**
	 * @param leftTrendArrow the leftTrendArrow to set
	 */
	public abstract void setLeftTrendArrow(String leftTrendArrow);

	/**
	 * The calculated rightTrendArrow value is used to determine the right hand trend arrow in each cell display.
	 * It can have values:
	 * 	    DataPointDefinition.TREND_UP indicates that the trend for this value is upwards
	 *      DataPointDefinition.TREND_LEVEL Indicates that the trend for this value is level (no change)
	 *      DataPointDefinition.TREND_DOWN Indicates that the trend for this value is down
	 * @return the rightTrendArrow
	 */
	public abstract String getRightTrendArrow();

	/**
	 * @param rightTrendArrow the rightTrendArrow to set
	 */
	public abstract void setRightTrendArrow(String rightTrendArrow);

	/**
	 * Only for testing. Use the updateCalculation(Double latestValue, Long latestTimestamp) to update values
	 * @param latestDataValue
	 */
	public abstract void setLatestDataValue(Double latestDataValue);

	/**
	 * @return latestDataValue
	 */
	public abstract Double getLatestDataValue();

	/**
	 * 
	 * @return latestTimestamp
	 */
	public abstract Long getLatestTimestamp();

	/**
	 * Only for testing. Use the updateCalculation(Double latestValue, Long latestTimestamp) to update values
	 * @param latestTimestamp
	 */
	public abstract void setLatestTimestamp(Long latestTimestamp);

	/**
	 * 
	 * @return mouseOverText
	 */
	public abstract String getMouseOverText();

	/**
	 * Only for testing. Use the updateCalculation(Double latestValue, Long latestTimestamp) to update values and calculate new
	 * @param mouseOverText
	 */
	public abstract  void setMouseOverText(String mouseOverText);

	/**
	 * 
	 * @return prevDataValue
	 */
	public abstract Double getPrevDataValue();

	/**
	 * 
	 * @param prevDataValue
	 */
	public abstract void setPrevDataValue(Double prevDataValue);

	/**
	 * 
	 * @return previousTimestamp
	 */
	public abstract Long getPreviousTimestamp();

	/**
	 * 
	 * @param previousTimestamp
	 */
	public abstract void setPreviousTimestamp(Long previousTimestamp); 
	
	/**
	 * @return the realUpdateTime  the last time in real time this calculator was updated 
	 * (as generated by Date.getTimeStamp())
	 */
	public abstract long getRealUpdateTime();
	
	/**
	 * the sample timeout value set for this calculator in milliseconds
	 * @return the sampleTimeout
	 */
	public abstract long getSampleTimeout();

	/**
	 * the sample timeout value set for this calculator in milliseconds
	 * @param sampleTimeout the sampleTimeout to set
	 */
	public abstract void setSampleTimeout(long sampleTimeout);

	/**
	 * If in real time this calculator has not been updated within timeout (milliseconds)
	 * this will return true
	 * 
	 * @return true if calculator timed out, false if calculator has been updated in the time
	 */

	public abstract boolean sampleUpdateTimedOut();
	
	/**
	 * @return returns the internally stored data sequence as a CSV string of doubles
	 * Used to export data for other math processing
	 */
	public abstract String dataToCSV();


}
