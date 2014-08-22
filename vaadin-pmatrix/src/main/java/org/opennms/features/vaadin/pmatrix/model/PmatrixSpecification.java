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


/**
 * used to specify the cells in a Pmatrix display
 */
@XmlTransient 
@XmlSeeAlso({PmatrixSpecificationImpl.class})
//@XmlJavaTypeAdapter(PmatrixSpecificationImpl.Adapter.class)
public abstract class PmatrixSpecification {
	
	/**
	 * 
	 * @return returns the name of pmatrix table to which this specification applies
	 */
	public abstract String getPmatrixTitle();
	
	/**
	 * 
	 * @param pmatrixTitle sets the name of pmatrix table to which this specification applies
	 */
	public abstract void setPmatrixTitle(String pmatrixTitle);
	
	/**
	 * 
	 * @param pmatrixName gets the name of this pmatrix display. This is the name which 
	 * will be used as a handle for the display and also in the url parameters. 
	 */
	public abstract String getPmatrixName();

	/**
	 * 
	 * @param pmatrixName sets the name of this pmatrix display. This is the name which 
	 * will be used as a handle for the display and also in the url parameters. 
	 * The name must contain no spaces or special characters which can't be used without encoding 
	 * in a url (&,#,/,: etc)
	 */
	public abstract void setPmatrixName(String pmatrixName);
	
	/**
	 * If hidePmatrixHeaders is true, no row or column headers are displayed
	 * @return the hidePmatrixHeaders
	 */
	public abstract boolean getHidePmatrixHeaders();

	/**
	 * If hidePmatrixHeaders is true, no row or column headers are displayed
	 * @param hidePmatrixHeaders the hidePmatrixHeaders to set
	 */
	public abstract void setHidePmatrixHeaders(boolean hidePmatrixHeaders);

	
	/**
	 * @return componentHeight The recommended component height in CSS as per Vaadin spec 
	 * for the component (Table) using this data source. If null default values are used
	 */
	public abstract String getComponentHeight();
	
	/**
	 * @param componentHeight The recommended component height in CSS as per Vaadin spec 
	 * for the component (Table) using this data source. If null default values are used
	 */
	public abstract void setComponentHeight(String componentHeight);
	
	/**
	 * @return componentWidth The recommended component width in CSS as per Vaadin spec 
	 * for the component (Table) using this data source. If null default values are used
	 */
	public abstract String getComponentWidth();
	
	/**
	 * @param componentWidth The recommended component width in CSS as per Vaadin spec 
	 * for the component (Table) using this data source. If null default values are used
	 */
	public abstract void setComponentWidth(String componentWidth);
	
	/**
	 * latestDataValueDescription  description of latest data value. if null getPmatrixCellKeyText returns an empty string ""
	 * @return the latestDataValueDescription
	 */
	public abstract String getLatestDataValueDescription();

	/**
	 * latestDataValueDescription  description of latest data value. if null getPmatrixCellKeyText returns an empty string ""
	 * @param latestDataValueDescription the latestDataValueDescription to set
	 */
	public abstract void setLatestDataValueDescription(String latestDataValueDescription);
	
	/**
	 * @return the secondaryNumberDescription
	 *    secondaryNumberDescription  description of secondary number. If null, no secondary number is generated in key
	 */
	public abstract String getSecondaryNumberDescription();

	/**
	 * @param secondaryNumberDescription the secondaryNumberDescription to set
	 * secondaryNumberDescription  description of secondary number. If null, no secondary number is generated in key
	 */
	public abstract void setSecondaryNumberDescription(String secondaryNumberDescription);

	/**
	 * @return the leftRangeArrowDescription
	 *    leftRangeArrowDescription description of left range arrow. If null, no arrow is generated in key
	 */
	public abstract String getLeftRangeArrowDescription();

	/**
	 * @param leftRangeArrowDescription the leftRangeArrowDescription to set
	 *    leftRangeArrowDescription description of left range arrow. If null, no arrow is generated in key
	 */
	public abstract void setLeftRangeArrowDescription(String leftRangeArrowDescription);

	/**
	 * @return the rightRangeArrowDescription
	 *    rightRangeArrowDescription  description of right range arrow. If null, no arrow is generated in key
	 */
	public abstract String getRightRangeArrowDescription();

	/**
	 * @param rightRangeArrowDescription the rightRangeArrowDescription to set
	 *    rightRangeArrowDescription  description of right range arrow. If null, no arrow is generated in key
	 */
	public abstract void setRightRangeArrowDescription(String rightRangeArrowDescription);

	/**
	 * @return the descriptiveText
	 *    descriptiveText general descriptive text. If null, no descriptive text is generated
	 */
	public abstract String getDescriptiveText();

	/**
	 * @param descriptiveText the descriptiveText to set
	 *    descriptiveText general descriptive text. If null, no descriptive text is generated
	 */
	public abstract void setDescriptiveText(String descriptiveText);
	
	/**
	 * @return the colorDescriptionStr
	 */
	public abstract  String getColorDescriptionStr();

	/**
	 * if colorDescriptionStr is null, then a color description is generated. 
	 * If not null, the this string is used as color description and 
	 * the user is entirely responsible for populating this string. 
	 * it should be an empty string if you do not want a colorDescription 
	 * or simple text or a properly formatted html 5 element which will
	 * fit within a table &lt;TD&gt;&lt;/TD&gt; construct
	 * @param colorDescriptionStr the colorDescriptionStr to set
	 */
	public abstract void setColorDescriptionStr(String colorDescriptionStr);
	
	/**
	 * if true calculated left arrow is displayed for the cells in this pmatrix table
	 * @return the leftArrowEnabled
	 */
	public abstract boolean getLeftArrowEnabled();

	/**
	 * if true calculated left arrow is displayed for the cells in this pmatrix table
	 * @param leftArrowEnabled the leftArrowEnabled to set
	 */
	public abstract void setLeftArrowEnabled(boolean leftArrowEnabled);

	/**
	 * if true calculated right arrow is displayed for the cells in this pmatrix table
	 * @return the rightArrowEnabled
	 */
	public abstract boolean getRightArrowEnabled();

	/**
	 * if true calculated right arrow is displayed for the cells in this pmatrix table
	 * @param rightArrowEnabled the rightArrowEnabled to set
	 */
	public abstract void setRightArrowEnabled(boolean rightArrowEnabled);
	
	/**
	 * @return the latestDataValueEnabled
	 */
	public abstract boolean getLatestDataValueEnabled();

	/**
	 * @param latestDataValueEnabled the latestDataValueEnabled to set
	 */
	public abstract void setLatestDataValueEnabled(boolean latestDataValueEnabled);

	/**
	 * if true calculated secondaryNumber (lower number) is displayed for the cells in this pmatrix table
	 * @return the secondaryNumberEnabled
	 */
	public abstract boolean getSecondaryNumberEnabled();

	/**
	 * if true calculated secondaryNumber (lower number) is displayed for the cells in this pmatrix table
	 * @param secondaryNumberEnabled the secondaryNumberEnabled to set
	 */
	public abstract void setSecondaryNumberEnabled(boolean secondaryNumberEnabled);

	/**
	 * if true the displayed cell links to a url
	 * @return the linkUrlEnabled
	 */
	public abstract boolean getLinkUrlEnabled();

	/**
	 * if true the displayed cell links to a url
	 * @param linkUrlEnabled the linkUrlEnabled to set
	 */
	public abstract void setLinkUrlEnabled(boolean linkUrlEnabled);
	
	
	/**
	 * if true the mouseOverText is displayed for the cells in this pmatrix table
	 * @return the mouseOverTextEnabled
	 */
	public abstract boolean getMouseOverTextEnabled();

	/**
	 * if true the mouseOverText is displayed for the cells in this pmatrix table
	 * @param mouseOverTextEnabled the mouseOverTextEnabled to set
	 */
	public abstract void setMouseOverTextEnabled(boolean mouseOverTextEnabled);
	

	/**
	 * Get List of column names for use in a pmatrix display. 
	 * The names must be unique and in the order 
	 * in which you want to display them
	 */
	public abstract List<String> getColumnNames();

	/**
	 * set List of column names for use in a pmatrix display. 
	 * The names must be unique and in the order 
	 * in which you want to display them
	 */
	public abstract void setColumnNames(List<String> columnNames);

	/**
	 * Get List of row names for use in a pmatrix display
	 * The names must be unique and in the order 
	 * in which you want to display them
	 */
	public abstract List<String> getRowNames();

	/**
	 * Set List of row names for use in a pmatrix display
	 * The names must be unique and in the order 
	 * in which you want to display them
	 */
	public abstract void setRowNames(List<String> rowNames);

	/**
	 * Get List of data point definitions for use in a pmatrix display
	 * Each data point will be displayed in the matrix at the point defined by DataPointDefinition rowName,colName. If the 
	 * rowName,colName pair are not unique, the first one encountered will be displayed.
	 * Each data point will be filled with data defined by DataPointDefinition filePath. Only one DataPointDefinition in the pmatrix
	 * should have this filePath. if more than one DataPointDefinition has the same filePath, the first one encountered will be populated
	 * 
	 */
	public abstract List<DataPointDefinition> getDatapointDefinitions();

	/**
	 * Set List of data point definitions for use in a pmatrix display
	 * Each data point will be displayed in the matrix at the point defined by DataPointDefinition rowName,colName. If the 
	 * rowName,colName pair are not unique, the first one encountered will be displayed.
	 * Each data point will be filled with data defined by DataPointDefinition filePath. Only one DataPointDefinition in the pmatrix
	 * should have this filePath. if more than one DataPointDefinition has the same filePath, the first one encountered will be populated
	 * 
	 */
	public abstract void setDatapointDefinitions(
			List<DataPointDefinition> datapointDefinitions);

}