package org.opennms.features.vaadin.pmatrix.model;

import java.util.List;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlTransient 
@XmlSeeAlso({PmatrixSpecificationListImpl.class})
public abstract class PmatrixSpecificationList {

	public abstract  List<PmatrixSpecification> getPmatrixSpecificationList();

	public abstract void setPmatrixSpecificationList(
			List<PmatrixSpecification> pmatrixSpecificationList);
	
	/**
	 *  returns the refresh rate set for all the pmatrix displays
	 * @return refresh rate in milliseconds
	 */
	public abstract Integer getRefreshRate();


	/**
	 * sets the refresh rate for all the pmatrix displays
	 * @param refreshRate in milliseconds
	 */
	public abstract void setRefreshRate(Integer refreshRate);
	
	/**
	 * gets the configuration for the PmatrixDpdCalculator used to drive the pmatrix tables 
	 * @param pmatrixDpdCalculatorConfig
	 */
	public abstract PmatrixDpdCalculatorConfig getPmatrixDpdCalculatorConfig();
	
	/**
	 * sets the configuration for the PmatrixDpdCalculator used to drive the pmatrix tables 
	 * @param pmatrixDpdCalculatorConfig
	 */
	public abstract void setPmatrixDpdCalculatorConfig(PmatrixDpdCalculatorConfig pmatrixDpdCalculatorConfig);

}