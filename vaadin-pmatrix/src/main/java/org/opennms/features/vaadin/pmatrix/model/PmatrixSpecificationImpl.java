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

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;

/**
 * Defines the matrix of values and the header and column names
 * which will be displayed on a pmatrix display.
 *
 */
@XmlRootElement(name="PmatrixTableSpecification")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name="PmatrixSpecification",propOrder={"pmatrixName", "pmatrixTitle", 
		"componentHeight", "componentWidth", "hidePmatrixHeaders",
		"descriptiveText", "latestDataValueDescription", "secondaryNumberDescription", 
		"rightRangeArrowDescription", "leftRangeArrowDescription",	"colorDescriptionStr",
		"latestDataValueEnabled", "secondaryNumberEnabled", 
		"leftArrowEnabled", "rightArrowEnabled", 
		"linkUrlEnabled", "mouseOverTextEnabled",
		"rowNames", "columnNames", "datapointDefinitions" })
public class PmatrixSpecificationImpl extends PmatrixSpecification {

	/**
	 * Holds human readable title of this pmatrix
	 */
	private String pmatrixTitle="OpenNMS Pmatrix Table (untitled)";


	/**
	 * List of column names for use in a pmatrix display. 
	 * The names must be unique and in the order 
	 * in which you want to display them
	 */
	private List<String> columnNames = new ArrayList<String>();

	/**
	 * List of row names for use in a pmatrix display
	 * The names must be unique and in the order 
	 * in which you want to display them
	 */
	private List<String> rowNames = new ArrayList<String>();

	/**
	 * List of data point definitions for use in a pmatrix display
	 * Each data point will be displayed in the matrix at the point defined by DataPointDefinition rowName,colName. If the 
	 * rowName,colName pair are not unique, the first one encountered will be displayed.
	 * Each data point will be filled with data defined by DataPointDefinition filePath. Only one DataPointDefinition in the pmatrix
	 * should have this filePath. if more than one DataPointDefinition has the same filePath, the first one encountered will be populated
	 * 
	 */
	private List<DataPointDefinition> datapointDefinitions = new ArrayList<DataPointDefinition>();

	/**
	 * holds the name which is used as a handle for this pmatrix specification
	 */
	private String pmatrixName;
	
	/**
	 * The recommended component height in CSS as per Vaadin spec 
	 */
	private String componentHeight=null;
	
	/**
	 * The recommended component width in CSS as per Vaadin spec 
	 */
	private String componentWidth=null;	
	
	/**
	 * 	latestDataValueDescription  description of latest data value. if null getPmatrixCellKeyText returns an empty string ""
	 */
	private String latestDataValueDescription;
	
	/**
	 *  secondaryNumberDescription  description of secondary number. If null, no secondary number is generated in key
	 */
	private String secondaryNumberDescription;
	
	/**
	 *  leftRangeArrowDescription description of left range arrow. If null, no arrow is generated in key
	 */
	private String leftRangeArrowDescription;
	
	/**
	 *  rightRangeArrowDescription  description of right range arrow. If null, no arrow is generated in key
	 */
	private String rightRangeArrowDescription;
	
	/**
	 *  descriptiveText general descriptive text. If null, no descriptive text is generated
	 */
	private String descriptiveText;
	
	/**
	 * if colorDescriptionStr is null, then a color description is generated. 
	 * If not null, the this string is used as color description and 
	 * the user is entirely responsible for populating this string. 
	 * it should be an empty string if you do not want a colorDescription 
	 * or simple text or a properly formatted html 5 element which will
	 * fit within a table &lt;TD&gt;&lt;/TD&gt; construct
	 */
	private String colorDescriptionStr;
	
	/**
	 * if true calculated right arrow is displayed for cells in this pmatrix table
	 */
	private boolean leftArrowEnabled =true;
	
	/**
	 * if true calculated left arrow is displayed for cells in this pmatrix table
	 */
	private boolean rightArrowEnabled=true;
	
	/**
	 * if true latest data value (upper number) is displayed for cells in this pmatrix table
	 */
	private boolean latestDataValueEnabled=true;
	
	/**
	 * if true calculated secondaryNumber (lower number) is displayed for cells in this pmatrix table
	 */
	private boolean secondaryNumberEnabled=true;
	
	/**
	 * if true the displayed cell links to a url
	 */
	private boolean linkUrlEnabled=true;
	
	/**
	 * if true the mouseOverText is displayed for the cells in this pmatrix table
	 */
	private boolean mouseOverTextEnabled=true;


	/**
	 * if hidePmatrixHeaders is true, no row or column headers are displayed
	 */
	private boolean hidePmatrixHeaders=false;
	
	@Override
	public String getComponentHeight() {
		return componentHeight;
	}

	@XmlElement
	@Override
	public void setComponentHeight(String componentHeight) {
		this.componentHeight=componentHeight;
	}

	@Override
	public String getComponentWidth() {
		return componentWidth;
	}

	@XmlElement
	@Override
	public void setComponentWidth(String componentWidth) {
		this.componentWidth=componentWidth;
	}


	/**
	 * if true calculated left arrow is displayed for the cells in this pmatrix table
	 * @return the leftArrowEnabled
	 */
	@Override
	public boolean getLeftArrowEnabled() {
		return leftArrowEnabled;
	}

	/**
	 * if true calculated left arrow is displayed for the cells in this pmatrix table
	 * @param leftArrowEnabled the leftArrowEnabled to set
	 */
	@XmlElement
	@Override
	public void setLeftArrowEnabled(boolean leftArrowEnabled) {
		this.leftArrowEnabled = leftArrowEnabled;
	}

	/**
	 * if true calculated right arrow is displayed for the cells in this pmatrix table
	 * @return the rightArrowEnabled
	 */
	@Override
	public boolean getRightArrowEnabled() {
		return rightArrowEnabled;
	}

	/**
	 * if true calculated right arrow is displayed for the cells in this pmatrix table
	 * @param rightArrowEnabled the rightArrowEnabled to set
	 */
	@XmlElement
	@Override
	public void setRightArrowEnabled(boolean rightArrowEnabled) {
		this.rightArrowEnabled = rightArrowEnabled;
	}

	/**
	 * @return the latestDataValueEnabled
	 */
	@Override
	public boolean getLatestDataValueEnabled() {
		return latestDataValueEnabled;
	}

	/**
	 * @param latestDataValueEnabled the latestDataValueEnabled to set
	 */
	@XmlElement
	@Override
	public void setLatestDataValueEnabled(boolean latestDataValueEnabled) {
		this.latestDataValueEnabled = latestDataValueEnabled;
	}

	/**
	 * if true calculated secondaryNumber (lower number) is displayed for the cells in this pmatrix table
	 * @return the secondaryNumberEnabled
	 */
	@Override
	public boolean getSecondaryNumberEnabled() {
		return secondaryNumberEnabled;
	}

	/**
	 * if true calculated secondaryNumber (lower number) is displayed for the cells in this pmatrix table
	 * @param secondaryNumberEnabled the secondaryNumberEnabled to set
	 */
	@XmlElement
	@Override
	public void setSecondaryNumberEnabled(boolean secondaryNumberEnabled) {
		this.secondaryNumberEnabled = secondaryNumberEnabled;
	}

	/**
	 * if true the displayed cell links to a url
	 * @return the linkUrlEnabled
	 */
	@Override
	public boolean getLinkUrlEnabled() {
		return linkUrlEnabled;
	}

	/**
	 * if true the displayed cell links to a url
	 * @param linkUrlEnabled the linkUrlEnabled to set
	 */
	@XmlElement
	@Override
	public void setLinkUrlEnabled(boolean linkUrlEnabled) {
		this.linkUrlEnabled = linkUrlEnabled;
	}

	/**
	 * if true the mouseOverText is displayed for the cells in this pmatrix table
	 * @return the mouseOverTextEnabled
	 */
	@Override
	public boolean getMouseOverTextEnabled() {
		return mouseOverTextEnabled;
	}

	/**
	 * if true the mouseOverText is displayed for the cells in this pmatrix table
	 * @param mouseOverTextEnabled the mouseOverTextEnabled to set
	 */
	@XmlElement
	@Override
	public void setMouseOverTextEnabled(boolean mouseOverTextEnabled) {
		this.mouseOverTextEnabled = mouseOverTextEnabled;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.PmatrixSpecification#getColunmNames()
	 */
	@Override
	public List<String> getColumnNames() {
		return columnNames;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.PmatrixSpecification#setColunmNames(java.util.List)
	 */
	@XmlElementWrapper(required=true)
	@XmlElement(name="column", required=true)
	@Override
	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;

	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.PmatrixSpecification#getRowNames()
	 */
	@Override
	public List<String> getRowNames() {
		return rowNames;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.PmatrixSpecification#setRowNames(java.util.List)
	 */
	@XmlElementWrapper(required=true)
	@XmlElement(name="row", required=true)
	@Override
	public void setRowNames(List<String> rowNames) {
		this.rowNames = rowNames;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.PmatrixSpecification#getDatapointDefinitions()
	 */
	@Override
	public List<DataPointDefinition> getDatapointDefinitions() {
		return datapointDefinitions;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.PmatrixSpecification#setDatapointDefinitions(java.util.List)
	 */
	@XmlElementWrapper(required=true)
	@XmlElement(name="datapointDefinition")
	@Override
	public void setDatapointDefinitions(
			List<DataPointDefinition> datapointDefinitions) {
		this.datapointDefinitions = datapointDefinitions;

	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.PmatrixSpecification#getPmatrixTitle()
	 */
	@Override
	public String getPmatrixTitle() {
		return pmatrixTitle;
	}

	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.PmatrixSpecification#setPmatrixTitle(java.util.String)
	 */

	@XmlElement
	@Override
	public void setPmatrixTitle(String pmatrixTitle) {
		this.pmatrixTitle=pmatrixTitle;
	}

	@XmlElement(required=true)
	@Override
	public String getPmatrixName() {
		 return pmatrixName;
	}

	@Override
	public void setPmatrixName(String pmatrixName) {
		this.pmatrixName=pmatrixName;
		
	}
	
	@XmlElement()
	@Override
	public boolean getHidePmatrixHeaders() {
		return hidePmatrixHeaders;
	}

	@Override
	public void setHidePmatrixHeaders(boolean hidePmatrixHeaders) {
		this.hidePmatrixHeaders = hidePmatrixHeaders;
	}
	
	/**
	 * @return the latestDataValueDescription
	 */
	@XmlElement
	@Override
	public String getLatestDataValueDescription() {
		return latestDataValueDescription;
	}

	/**
	 * @param latestDataValueDescription the latestDataValueDescription to set
	 */
	@Override
	public void setLatestDataValueDescription(String latestDataValueDescription) {
		this.latestDataValueDescription = latestDataValueDescription;
	}

	/**
	 * @return the secondaryNumberDescription
	 */
	@XmlElement
	@Override
	public String getSecondaryNumberDescription() {
		return secondaryNumberDescription;
	}

	/**
	 * @param secondaryNumberDescription the secondaryNumberDescription to set
	 */
	@Override
	public void setSecondaryNumberDescription(String secondaryNumberDescription) {
		this.secondaryNumberDescription = secondaryNumberDescription;
	}

	/**
	 * @return the leftRangeArrowDescription
	 */
	@XmlElement
	@Override
	public String getLeftRangeArrowDescription() {
		return leftRangeArrowDescription;
	}

	/**
	 * @param leftRangeArrowDescription the leftRangeArrowDescription to set
	 */
	@Override
	public void setLeftRangeArrowDescription(String leftRangeArrowDescription) {
		this.leftRangeArrowDescription = leftRangeArrowDescription;
	}

	/**
	 * @return the rightRangeArrowDescription
	 */
	@XmlElement
	@Override
	public String getRightRangeArrowDescription() {
		return rightRangeArrowDescription;
	}

	/**
	 * @param rightRangeArrowDescription the rightRangeArrowDescription to set
	 */
	@Override
	public void setRightRangeArrowDescription(String rightRangeArrowDescription) {
		this.rightRangeArrowDescription = rightRangeArrowDescription;
	}

	/**
	 * @return the descriptiveText
	 */
	@XmlElement
	@Override
	public String getDescriptiveText() {
		return descriptiveText;
	}

	/**
	 * @param descriptiveText the descriptiveText to set
	 */
	@Override
	public void setDescriptiveText(String descriptiveText) {
		this.descriptiveText = descriptiveText;
	}

	/**
	 * @return the colorDescriptionStr
	 */
	@Override
	public String getColorDescriptionStr() {
		return colorDescriptionStr;
	}

	/**
	 * if colorDescriptionStr is null, then a color description is generated. 
	 * If not null, the this string is used as color description and 
	 * the user is entirely responsible for populating this string. 
	 * it should be an empty string if you do not want a colorDescription 
	 * or simple text or a properly formatted html 5 element which will
	 * fit within a table &lt;TD&gt;&lt;/TD&gt; construct
	 * @param colorDescriptionStr the colorDescriptionStr to set
	 */
	@XmlElement
	@Override
	public void setColorDescriptionStr(String colorDescriptionStr) {
		this.colorDescriptionStr = colorDescriptionStr;
	}


}
