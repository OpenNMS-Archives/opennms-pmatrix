/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2010-2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General public License for more details.
 *
 * You should have received a copy of the GNU General public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.vaadin.pmatrix.model;


import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;


/**
 * 
 * Defines a data point in the pmatrix display
 *
 */
@XmlTransient 
@XmlSeeAlso({DataPointDefinitionImpl.class})
public abstract class DataPointDefinition {

	// DataPointDefinition constants

	/**
	 * Indicates that the trend for this value is upwards
	 */
	public static final String TREND_UP="trendUp";

	/**
	 * Indicates that the trend for this value is level (no change)
	 */
	public static final String TREND_LEVEL="trendLevel";

	/**
	 * Indicates that the trend for this value is down
	 */
	public static final String TREND_DOWN="trendDown";

	/**
	 * Used to set the color of the displayed value depending on its assessment by the calculation
	 * Integer value and recommended colors match OpenNMS severities except for indeterminant which is displayed blue
	 * critical(7) Color: Purple : #C00
	 */
	public static final int RANGE_CRITICAL=7;
	/**
	 * Used to set the color of the displayed value depending on its assessment by the calculation
	 * Integer value and recommended colors match OpenNMS severities except for indeterminant which is displayed blue
	 * major(6) Color: Color: Red : #F30 
	 */
	public static final int RANGE_MAJOR=6;    
	/**
	 * Used to set the color of the displayed value depending on its assessment by the calculation
	 * Integer value and recommended colors match OpenNMS severities except for indeterminant which is displayed blue
	 * minor(5) Color: Orange : #F90
	 */
	public static final int RANGE_MINOR=5;  
	/**
	 * Used to set the color of the displayed value depending on its assessment by the calculation
	 * Integer value and recommended colors match OpenNMS severities except for indeterminant which is displayed blue
	 * warning(4) Color: yellow : #FC0
	 */
	public static final int RANGE_WARNING=4; 
	/**
	 * Used to set the color of the displayed value depending on its assessment by the calculation
	 * Integer value and recommended colors match OpenNMS severities except for indeterminant which is displayed blue
	 * normal(3) Color: Dark green : #360 
	 */
	public static final int RANGE_NORMAL=3;
	/**
	 * Used to set the color of the displayed value depending on its assessment by the calculation
	 * Integer value and recommended colors match OpenNMS severities except for indeterminate which is displayed blue
	 * This indicates that we have not recently received an update
	 * indeterminate(1) Color: Blue : #00B (different from event indeterminate in OpenNMS  Light green : #990 )
	 */
	public static final int RANGE_INDETERMINATE=1;



	/**
	 * Get the name of the row where this data element will be placed (x coordinate).
	 * The Row and column names must be predefined and must be unique for each data point.
	 */
	public abstract String getRowName();

	/**
	 * Set the name of the row where this data element will be placed (x coordinate).
	 * The Row and column names must be predefined and must be unique for each data point.
	 */
	public abstract void setRowName(String rowName);

	/**
	 * Get the name of the column where this data element will be placed (y coordinate).
	 * The Row and column names must be predefined and must be unique for each data point.
	 */
	public abstract String getColName();

	/**
	 * Set the name of the column where this data element will be placed (y coordinate).
	 * The Row and column names must be predefined and must be unique for each data point.
	 */
	public abstract void setColName(String colName);

	/**
	 * Get the file path to the RRD which stores the data. A File path is included in each
	 * PM data message and this value is used to filter data messages for ones which update this
	 * data point. 
	 */
	public abstract String getFilePath();

	/**
	 * Set the file path to the RRD which stores the data. A File path is included in each
	 * PM data message and this value is used to filter data messages for ones which update this
	 * data point. 
	 */
	public abstract void setFilePath(String filePath);

	/**
	 * Get the url which will display the full rrd graph if the data point is selected. 
	 * (TODO This is not easily derived from the file path)
	 */
	public abstract String getGraphURL();

	/**
	 * Set the url which will display the full rrd graph if the data point is selected. 
	 * (TODO This is not easily derived from the file path)
	 */
	public abstract void setGraphURL(String graphURL);

	/**
	 * @param latest data value associated with this data point definition. 
	 * This is not normally set at configuration time until updated from OpenNMS
	 * if null no latestDataValue has been set
	 */
	public abstract void setLatestDataValue(Double latestDataValue);

	/**
	 * @return  latest data value associated with this data point definition. 
	 */
	public abstract Double getLatestDataValue();

	/**
	 * Determines severity (and color) associated with secondary value. 
	 * Can be any constant value between DataPointDefinition.RANGE_INDETERMINATE and DataPointDefinition.RANGE_CRITICAL
	 * If null no latestDataValueRange has been set
	 * @return secondaryValueRange
	 */
	public abstract Integer getLatestDataValueRange();

	/**
	 * Determines severity (and color) associated with secondary value. 
	 * Can be any constant value between DataPointDefinition.RANGE_INDETERMINATE and DataPointDefinition.RANGE_CRITICAL
	 * If null no latestDataValueRange has been set
	 * @param secondaryValueRange
	 */
	public abstract void setLatestDataValueRange(Integer latestDataValueRange);

	/**
	 * @param timetamp latest time stamp value associated with this data point definition. 
	 */
	public abstract void setLatestTimestamp(Long latestTimestamp);

	/**
	 * @return latest data value associated with this data point definition. 
	 */
	public abstract Long getLatestTimestamp();





	/**
	 * It may be set by the DpdCalculator as a result of the calculation.
	 * @return provides mouse over text for values
	 */
	public abstract String getMouseOverText();

	/**
	 * It may be set by the DpdCalculator as a result of the calculation.
	 * @param mouseOverText provides mouse over text for values
	 */
	public abstract void setMouseOverText(String mouseOverText);

	/**
	 * The secondary value for this data point definition is used to produce a number in the second row of each cell. 
	 * The meaning of this number is application specific and not defined. 
	 * It may be set by the DpdCalculator as a result of the calculation.
	 * If this value is null, it is ignored and not displayed
	 * @return secondary value for this data point definition
	 */
	public abstract Double getSecondaryValue();

	/**
	 * The secondary value for this data point definition is used to produce a number in the second row of each cell. 
	 * The meaning of this number is application specific and not defined. 
	 * It may be set by the DpdCalculator as a result of the calculation.
	 * @param secondary value for this data point definition
	 */
	public abstract void setSecondaryValue(Double secondaryValue);

	/**
	 * Determines severity (and color) associated with secondary value. 
	 * Can be any constant value between DataPointDefinition.RANGE_INDETERMINATE and DataPointDefinition.RANGE_CRITICAL
	 * If null no secondaryValueRange has been set
	 * @return secondaryValueRange
	 */
	public abstract Integer getSecondaryValueRange();

	/**
	 * Determines severity (and color) associated with secondary value. 
	 * Can be any constant value between DataPointDefinition.RANGE_INDETERMINATE and DataPointDefinition.RANGE_CRITICAL
	 * If null no secondaryValueRange has been set
	 * @param secondaryValueRange
	 */
	public abstract void setSecondaryValueRange(Integer secondaryValueRange);

	/**
	 * The leftTrendArrow value is used to determine the left hand trend arrow in each cell display.
	 * Note that the meaning of the trend arrow is application specific and determined by the DpdCalculator
	 * values:
	 *    If null no  left hand trend arrow is displayed
	 *    If set to String constant;
	 *      DataPointDefinition.TREND_UP indicates that the trend for this value is upwards
	 *      DataPointDefinition.TREND_LEVEL Indicates that the trend for this value is level (no change)
	 *      DataPointDefinition.TREND_DOWN Indicates that the trend for this value is down
	 * @return leftTrendArrow
	 */
	public abstract String getLeftTrendArrow();

	/**
	 * The leftTrendArrow value is used to determine the left hand trend arrow in each cell display.
	 * Note that the meaning of the trend arrow is application specific and determined by the DpdCalculator
	 * values:
	 *    If null no  left hand trend arrow is displayed
	 *    If set to String constant;
	 *      DataPointDefinition.TREND_UP indicates that the trend for this value is upwards
	 *      DataPointDefinition.TREND_LEVEL Indicates that the trend for this value is level (no change)
	 *      DataPointDefinition.TREND_DOWN Indicates that the trend for this value is down
	 * @param leftTrendArrowSetting
	 */
	public abstract void setLeftTrendArrow(String leftTrendArrow);

	/**
	 * The rightTrendArrow value is used to determine the right hand trend arrow in each cell display.
	 * Note that the meaning of the trend arrow is application specific and determined by the DpdCalculator
	 * values:
	 *    If null no  right hand trend arrow is displayed
	 *    If set to String constant;
	 *      DataPointDefinition.TREND_UP indicates that the trend for this value is upwards
	 *      DataPointDefinition.TREND_LEVEL Indicates that the trend for this value is level (no change)
	 *      DataPointDefinition.TREND_DOWN Indicates that the trend for this value is down
	 * @return rightTrendArrow
	 */
	public abstract String getRightTrendArrow();

	/**
	 * The rightTrendArrow value is used to determine the right hand trend arrow in each cell display.
	 * Note that the meaning of the trend arrow is application specific and determined by the DpdCalculator
	 * values:
	 *    If null no  right hand trend arrow is displayed
	 *    If set to String constant;
	 *      DataPointDefinition.TREND_UP indicates that the trend for this value is upwards
	 *      DataPointDefinition.TREND_LEVEL Indicates that the trend for this value is level (no change)
	 *      DataPointDefinition.TREND_DOWN Indicates that the trend for this value is down
	 * @param rightTrendArrowSetting
	 */
	public abstract void setRightTrendArrow(String rightTrendArrow);

	/**
	 * @return the realUpdateTime  the last time in real time this data point was updated
	 * This value is not persisted and is used only to detect timeouts
	 */
	public abstract long getRealUpdateTime();

	/**
	 * @param realUpdateTime  the last time in real time this data point was updated
	 * This value is not persisted and is used only to detect timeouts
	 */
	public abstract void setRealUpdateTime(long realUpdateTime);

	/**
	 * Set true if this cell will contain only static text and no calculated values
	 * The static text will be set by the staticTextCellString. 
	 * @return
	 */
	public abstract Boolean getStaticTextCell();

	/**
	 * Set true if this cell will contain only static text and no calculated values
	 * The static text will be set by the staticTextCellString. 
	 * @param textOnlyCell
	 */
	public abstract void setStaticTextCell(Boolean staticTextCell);

	/**
	 * The static text set by the staticTextCellString will be used in place of any calculated values
	 * if  staticTextCell is set true.
	 * @return
	 */
	public abstract String getStaticTextCellString();

	/**
	 * The static text set by the staticTextCellString will be used in place of any calculated values
	 * if  staticTextCell is set true.
	 * @param textOnlyCellText
	 */
	public abstract void setStaticTextCellString(String staticTextCellString);


}