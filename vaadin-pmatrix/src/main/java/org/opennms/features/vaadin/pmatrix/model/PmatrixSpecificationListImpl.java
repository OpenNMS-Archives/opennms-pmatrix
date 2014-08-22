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
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="PmatrixConfigurations")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name="PmatrixSpecificationList", propOrder={"pmatrixDpdCalculatorConfig","refreshRate", "pmatrixSpecificationList"})
public class PmatrixSpecificationListImpl extends PmatrixSpecificationList  {


	private List<PmatrixSpecification> pmatrixSpecificationList = new ArrayList<PmatrixSpecification>();

	/**
	 * Refresh Rate in milliseconds for the pmatrix displays
	 */
	private  Integer refreshRate=10000;

	/**
	 * configuration for the DpdCalculator used to calculate the tables
	 */
	private PmatrixDpdCalculatorConfig pmatrixDpdCalculatorConfig;

	/**
	 * Constructor which loads PmatrixSpecificationList with a map of values
	 * @param pmatrixSpecificationList
	 */
	public PmatrixSpecificationListImpl(List<PmatrixSpecification> pmatrixSpecificationList){
		this.pmatrixSpecificationList = pmatrixSpecificationList;
	}

	/**
	 * Constructor create an empty PmatrixSpecificationListImpl
	 */
	public PmatrixSpecificationListImpl() {
	}


	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.PmatrixSpecificationList#getPmatrixSpecificationList()
	 */
	@Override
	public List<PmatrixSpecification> getPmatrixSpecificationList() {
		return pmatrixSpecificationList;
	}


	/* (non-Javadoc)
	 * @see org.opennms.features.vaadin.pmatrix.engine.PmatrixSpecificationList#setPmatrixSpecificationList(java.util.concurrent.ConcurrentMap)
	 */
	@XmlElementWrapper(name="pmatrixSpecifications", required=true)
	@XmlElement(name="pmatrixSpecification")
	@Override
	public void setPmatrixSpecificationList(List<PmatrixSpecification> pmatrixSpecificationList) {
		this.pmatrixSpecificationList = pmatrixSpecificationList;
	}


	@Override
	public Integer getRefreshRate() {
		return refreshRate;
	}

	@XmlElement
	@Override
	public void setRefreshRate(Integer refreshRate) {
		if (refreshRate!=null) this.refreshRate=refreshRate;

	}

	@Override
	public PmatrixDpdCalculatorConfig getPmatrixDpdCalculatorConfig() {
		return pmatrixDpdCalculatorConfig;
	}

	@XmlElement(name="dataPointCalculator", required=true)
	@Override
	public void setPmatrixDpdCalculatorConfig( PmatrixDpdCalculatorConfig pmatrixDpdCalculatorConfig) {
		this.pmatrixDpdCalculatorConfig = pmatrixDpdCalculatorConfig;

	}


	/**
	 * overrides toString method to return the contents of a complete pmatrixSpecificationList
	 */
	@Override
	public String toString() {

		PmatrixSpecificationList pmatrixSpecificationList =this;
		StringBuffer sbuff= new StringBuffer();

		sbuff.append("\nPmatrixSpecificationList RefreshRate:"+pmatrixSpecificationList.getRefreshRate());

		sbuff.append("\ndpdCalculatorConfig :");
		PmatrixDpdCalculatorConfig dpdCalculatorConfig = pmatrixSpecificationList.getPmatrixDpdCalculatorConfig();
		if (dpdCalculatorConfig==null) {
			sbuff.append("\ndpdCalculatorConfig is null");
		} else {
			sbuff.append("\n   dpdCalculatorConfig.getPmatrixDpdCalculatorClassName():"+dpdCalculatorConfig.getPmatrixDpdCalculatorClassName());
			if(dpdCalculatorConfig.getConfiguration()==null){
				sbuff.append("\n   dpdCalculatorConfig.getConfiguration() is null");
			} else {
				sbuff.append("\n   dpdCalculatorConfig.getConfiguration():");
				for(NameValuePair property : dpdCalculatorConfig.getConfiguration()){
					sbuff.append("\n      property name:'"+property.getName()+"' value:'"+property.getValue()+"'");
				}
			}

		}

		for (PmatrixSpecification pmatrixSpec : pmatrixSpecificationList.getPmatrixSpecificationList()){

			sbuff.append("\nPmatrixSpecification Name:"+pmatrixSpec.getPmatrixName());

			sbuff.append("\n  column names:"
					+ " size:" +pmatrixSpec.getColumnNames().size()
					+ " names:"+pmatrixSpec.getColumnNames().toString());
			sbuff.append("\n  row names:"
					+ " size:" +pmatrixSpec.getColumnNames().size()
					+ " names:"+pmatrixSpec.getColumnNames().toString());
			sbuff.append("\n  number of datapoints:"+ pmatrixSpec.getDatapointDefinitions().size());
			for (DataPointDefinition dpd: pmatrixSpec.getDatapointDefinitions()){
				sbuff.append("\n  data point definition:' rowName:'"+dpd.getRowName()
						+ " 'colName:'"+dpd.getColName()
						+ "' filePath:'" +dpd.getFilePath()
						+ "' graphURL:'"+dpd.getGraphURL()
						+ "' mouseOverText:'"+dpd.getMouseOverText()
						+ "' latestDataValueRange:'"+dpd.getLatestDataValueRange()
						+ "' secondaryValue:'"+dpd.getSecondaryValue()
						+ "' secondaryValueRange:'"+dpd.getSecondaryValueRange()
						+ "' rightTrendArrow:'"+dpd.getRightTrendArrow()
						+ "' leftTrendArrow:'"+dpd.getLeftTrendArrow() );

			}
		}
		return sbuff.toString();
	}


}
