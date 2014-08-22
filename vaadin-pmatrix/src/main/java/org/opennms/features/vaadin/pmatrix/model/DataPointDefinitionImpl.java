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

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Used to define what data is used in a single element in the pmatrix display
 *
 */
@XmlRootElement(name="PmatrixDataPointDefinition")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name="DataPointDefinition", propOrder={"staticTextCell", "staticTextCellString",  "filePath", "rowName",  "colName", "graphURL", "latestTimestamp", 
		"latestDataValue", "latestDataValueRange", "secondaryValue", "secondaryValueRange", "leftTrendArrow", "rightTrendArrow",
"mouseOverText"})
public class DataPointDefinitionImpl extends DataPointDefinition  {

	/** 
	 * used to store latest timestamp associated with latestDataValue
	 */
	private Long latestTimestamp=null;

	/**
	 * Used to store the datavalue associated with this data point definition
	 */
	private Double latestDataValue=null;

	/**
	 * determines the severity color of the latestdataValue
	 * can have values between DataPointDefinition.RANGE_INDETERMINATE and DataPointDefinition.RANGE_CRITICAL
	 */
	private Integer latestDataValueRange=null;


	/**
	 * Defines the name of the row where this data element will be placed (x coordinate).
	 * The Row and column names must be predefined and must be unique for each data point.
	 */
	private String rowName ="";
	/**
	 * Defines the name of the column where this data element will be placed (y coordinate).
	 * The Row and column names must be predefined and must be unique for each data point.
	 */
	private String colName ="";

	/**
	 * Defines the file path to the RRD which stores the data. A File path is included in each
	 * PM data message and this value is used to filter data messages for ones which update this
	 * data point. 
	 */
	private String filePath="";

	/**
	 * Defines the url which will display the full rrd graph if the data point is selected. 
	 * (TODO This is not easily derived from the file path)
	 */
	private String graphURL="";

	/**
	 * Text which will define mouse over event text
	 */
	private String mouseOverText="";

	/**
	 * determines the secondaryValue to put in a cell. 
	 * If null the cell will contain no placeholder for secondaryValue
	 */
	private Double secondaryValue=null;

	/**
	 * determines the severity color of the secondaryValueRange
	 * can have values between DataPointDefinition.RANGE_INDETERMINATE and DataPointDefinition.RANGE_CRITICAL
	 */
	private Integer secondaryValueRange=null;

	/**
	 * The leftTrendArrow value is used to determine the right hand trend arrow in each cell display.
	 * It can have values:
	 * 	    DataPointDefinition.TREND_UP indicates that the trend for this value is upwards
	 *      DataPointDefinition.TREND_LEVEL Indicates that the trend for this value is level (no change)
	 *      DataPointDefinition.TREND_DOWN Indicates that the trend for this value is down
	 */
	private String leftTrendArrow=null;

	/**
	 * The rightTrendArrow value is used to determine the right hand trend arrow in each cell display.
	 * It can have values:
	 * 	    DataPointDefinition.TREND_UP indicates that the trend for this value is upwards
	 *      DataPointDefinition.TREND_LEVEL Indicates that the trend for this value is level (no change)
	 *      DataPointDefinition.TREND_DOWN Indicates that the trend for this value is down
	 */
	private String rightTrendArrow=null;
	
	/**
	 * The static text set by the staticTextCellString will be used in place of any calculated values
	 */
	private String staticTextCellString="";

	/**
	 * Set true if this cell will contain only static text and no calculated values
	 */
	private Boolean staticTextCell=false;

	/**
	 * the last time in real time this calculator was updated. This value is not persisted
	 * and is used only to detect timeouts
	 */
	private long realUpdateTime=new Date().getTime();


	@Override
	public long getRealUpdateTime() {
		return realUpdateTime;
	}
	
    @Override
	public void setRealUpdateTime(long realUpdateTime){
		this.realUpdateTime=realUpdateTime;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointDefinitionI#getRowName()
	 */
	@Override
	public String getRowName() {
		return rowName;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointDefinitionI#setRowName(java.lang.String)
	 */
	@XmlElement(required=true)
	@Override
	public void setRowName(String rowName) {
		this.rowName = rowName;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointDefinitionI#getColName()
	 */
	@Override
	public String getColName() {
		return colName;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointDefinitionI#setColName(java.lang.String)
	 */
	@XmlElement(required=true)
	@Override
	public void setColName(String colName) {
		this.colName = colName;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointDefinitionI#getFilePath()
	 */
	@Override
	public String getFilePath() {
		return filePath;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointDefinitionI#setFilePath(java.lang.String)
	 */
	@XmlElement(required=true)
	@Override
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointDefinitionI#getGraphURL()
	 */
	@Override
	public String getGraphURL() {
		return graphURL;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointDefinitionI#setGraphURL(java.lang.String)
	 */
	@XmlElement(required=true)
	@Override
	public void setGraphURL(String graphURL) {
		this.graphURL = graphURL;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointDefinitionI#setDataValue(java.lang.Double)
	 */
	@XmlElement
	@Override
	public void setLatestDataValue(Double latestDataValue) {
		this.latestDataValue=latestDataValue;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointDefinitionI#getDataValue(java.lang.Double)
	 */
	@Override
	public Double getLatestDataValue() {
		return this.latestDataValue;
	}

	@Override
	public Integer getLatestDataValueRange() {
		return latestDataValueRange;
	}

	@XmlElement
	@Override
	public void setLatestDataValueRange(Integer latestDataValueRange) {
		this.latestDataValueRange=latestDataValueRange;
	}


	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointDefinitionI#getMouseOverText
	 */
	@Override
	public String getMouseOverText() {
		return mouseOverText;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointDefinitionI#setMouseOverText(java.lang.String)
	 */
	@XmlElement
	@Override
	public void setMouseOverText(String mouseOverText) {
		this.mouseOverText = mouseOverText;

	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointDefinitionI#setLatestTimestamp(java.lang.Long)
	 */
	@XmlElement
	@Override
	public void setLatestTimestamp(Long latestTimestamp) {
		this.latestTimestamp=latestTimestamp;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.DataPointDefinitionI#getLatestTimestamp
	 */

	@Override
	public Long getLatestTimestamp() {
		return latestTimestamp;
	}


	@Override
	public Double getSecondaryValue() {
		return secondaryValue;
	}

	@XmlElement
	@Override
	public void setSecondaryValue(Double secondaryValue) {
		this.secondaryValue=secondaryValue;

	}

	@Override
	public Integer getSecondaryValueRange() {
		return secondaryValueRange;
	}

	@XmlElement
	@Override
	public void setSecondaryValueRange(Integer secondaryValueRange) {
		this.secondaryValueRange=secondaryValueRange;		
	}

	@Override
	public String getLeftTrendArrow() {
		return leftTrendArrow;
	}

	@XmlElement
	@Override
	public void setLeftTrendArrow(String leftTrendArrow) {
		this.leftTrendArrow=leftTrendArrow;

	}

	@Override
	public String getRightTrendArrow() {
		return rightTrendArrow;
	}

	@XmlElement
	@Override
	public void setRightTrendArrow(String rightTrendArrow) {
		this.rightTrendArrow=rightTrendArrow;

	}

	@Override
	public Boolean getStaticTextCell() {
		return staticTextCell;
	}

	@XmlElement
	@Override
	public void setStaticTextCell(Boolean staticTextCell) {
		this.staticTextCell=staticTextCell;
		
	}

	@Override
	public String getStaticTextCellString() {
		return staticTextCellString;
	}

	@XmlElement
	@Override
	public void setStaticTextCellString(String staticTextCellString) {
		this.staticTextCellString=staticTextCellString;
	}

}
