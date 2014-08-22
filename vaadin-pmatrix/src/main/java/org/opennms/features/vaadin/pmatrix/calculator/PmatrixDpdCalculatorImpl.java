package org.opennms.features.vaadin.pmatrix.calculator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.features.vaadin.pmatrix.model.DataPointDefinition;
import org.opennms.features.vaadin.pmatrix.model.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * simple base implementation of PmatrixDpdCalculator which can be used as a template
 * for a more specific example. 
 * Note that Jaxb cannot marshal class extensions so you must re-define any variables 
 * you wish to marshal with local annotations
 * It may be easier to copy and modify this class rather than extend it.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class PmatrixDpdCalculatorImpl extends PmatrixDpdCalculator {
	private static final Logger LOG = LoggerFactory.getLogger(PmatrixDpdCalculatorImpl.class);

	/**
	 * Fully qualified name of class used to persist data
	 */
	@XmlElement
	private String PersistingPmatrixDpdCalculatorClassName= this.getClass().getName();

	/**
	 * Name value pair configuration for this calculator. 
	 */
	@XmlElementWrapper(name="configuration")
	@XmlElement(name="property")
	private List<NameValuePair> configuration=new ArrayList<NameValuePair>();

	@XmlElement
	private Double latestDataValue=null;

	@XmlElement
	private Long latestTimestamp=null;

	@XmlElement
	private String mouseOverText="Value Statistics:<BR>   No Data Received"; //default value

	@XmlElement
	private Integer latestDataValueRange=null;

	@XmlElement
	private Double secondaryValue=null;

	@XmlElement
	private Integer secondaryValueRange=null;

	@XmlElement
	private String leftTrendArrow=null;

	@XmlElement
	private String rightTrendArrow=null;

	/**
	 * previous data value
	 */
	@XmlElement
	private Double prevDataValue; 

	/**
	 * time when the previous data value was collected
	 */
	@XmlElement
	private Long previousTimestamp;
	
	/**
	 *  the sample timeout value set for this calculator in milliseconds
	 */
	private long sampleTimeout=DEFAULT_TIMEOUT;
	
	/**
	 *  the last time this calculator was updated
	 */
	private long realUpdateTime=new Date().getTime();
	
	/**
	 * @return the realUpdateTime = the last time in real time this calculator was updated
	 */
	@Override
	public long getRealUpdateTime() {
		return realUpdateTime;
	}
	
	/**
	 * the sample timeout value set for this calculator in milliseconds
	 * @return the sampleTimeout
	 */
	@Override
	public long getSampleTimeout() {
		return sampleTimeout;
	}

	/**
	 * the sample timeout value set for this calculator in milliseconds
	 * @param sampleTimeout the sampleTimeout to set
	 */
	@Override
	public void setSampleTimeout(long sampleTimeout) {
		this.sampleTimeout = sampleTimeout;
	}
	
	/**
	 * If in real time this calculator has not been updated within timeout (milliseconds)
	 * this will return true
	 * 
	 * @return true if calculator timed out, false if calculator has been updated in the time
	 */
	@Override
	public boolean sampleUpdateTimedOut(){
	   long currentTime=new Date().getTime();
	   long deltatime =currentTime-realUpdateTime;
	   if (deltatime >= sampleTimeout){
		   return true;
	   }
	   return false;
	}

	/*
	 * map reconstructed from NameValuePair configuration List to ease look up of properties
	 */
	private Map<String,String> config= new HashMap<String, String>();

	/**
	 * @return HashMap of property values indexed by property name filled from configuration List
	 */
	private Map<String,String> getConfig(){
		if (config.isEmpty()) {
			for (NameValuePair nvp: this.configuration){
				config.put(nvp.getName(), nvp.getValue());
			}
		}
		return config;
	}



	@Override
	public void setConfiguration(List<NameValuePair> configuration) {
		this.configuration=configuration;
	}

	@Override
	public List<NameValuePair> getConfiguration() {
		return configuration;
	}

	@Override
	public String getPersistingPmatrixDpdCalculatorClassName() {
		return this.getClass().getName();
	}

	@Override
	public void setPersistingPmatrixDpdCalculatorClassName( String persistingPmatrixDpdCalculatorClassName) {
		PersistingPmatrixDpdCalculatorClassName = persistingPmatrixDpdCalculatorClassName;
	}

	@Override
	public Integer getLatestDataValueRange() {
		return latestDataValueRange;
	}

	@Override
	public void setLatestDataValueRange(Integer latestDataValueRange) {
		this.latestDataValueRange = latestDataValueRange;
	}

	@Override
	public Double getSecondaryValue() {
		return secondaryValue;
	}

	@Override
	public void setSecondaryValue(Double secondaryValue) {
		this.secondaryValue = secondaryValue;
	}

	@Override
	public Integer getSecondaryValueRange() {
		return secondaryValueRange;
	}

	@Override
	public void setSecondaryValueRange(Integer secondaryValueRange) {
		this.secondaryValueRange = secondaryValueRange;
	}

	@Override
	public String getLeftTrendArrow() {
		return leftTrendArrow;
	}

	@Override
	public void setLeftTrendArrow(String leftTrendArrow) {
		this.leftTrendArrow = leftTrendArrow;
	}

	@Override
	public String getRightTrendArrow() {
		return rightTrendArrow;
	}

	@Override
	public void setRightTrendArrow(String rightTrendArrow) {
		this.rightTrendArrow = rightTrendArrow;
	}

	@Override
	public void setLatestDataValue(Double latestDataValue) {
		this.latestDataValue = latestDataValue;
	}

	@Override
	public Double getLatestDataValue() {
		return latestDataValue;
	}

	@Override
	public Long getLatestTimestamp() {
		return latestTimestamp;
	}

	@Override
	public void setLatestTimestamp(Long latestTimestamp) {
		this.latestTimestamp = latestTimestamp;
	}

	@Override
	public String getMouseOverText() {
		return mouseOverText;
	}

	@Override
	public void setMouseOverText(String mouseOverText) {
		this.mouseOverText = mouseOverText;
	}

	@Override
	public Double getPrevDataValue() {
		return prevDataValue;
	}

	@Override
	public void setPrevDataValue(Double prevDataValue) {
		this.prevDataValue = prevDataValue;
	}

	@Override
	public Long getPreviousTimestamp() {
		return previousTimestamp;
	}

	@Override
	public void setPreviousTimestamp(Long previousTimestamp) {
		this.previousTimestamp = previousTimestamp;
	}

	@Override
	public DataPointDefinition updateDpd(DataPointDefinition dpd) {
		// only change values if local values not null
		// the local values only change if updateCalculation has been called
		if (latestDataValue!=null) dpd.setLatestDataValue(latestDataValue);
		if (latestTimestamp!=null) dpd.setLatestTimestamp(latestTimestamp);
		if (mouseOverText!=null) dpd.setMouseOverText(mouseOverText);

		if (latestDataValueRange!=null) dpd.setLatestDataValueRange(latestDataValueRange);
		if (secondaryValue!=null) dpd.setSecondaryValue(secondaryValue);
		if (secondaryValueRange!=null) dpd.setSecondaryValueRange(secondaryValueRange);
		if (leftTrendArrow!=null) dpd.setLeftTrendArrow(leftTrendArrow);
		if (rightTrendArrow!=null) dpd.setRightTrendArrow(rightTrendArrow);
		
		// sets update time of data point to the update time of this calculator
		dpd.setRealUpdateTime(realUpdateTime);

		if (sampleUpdateTimedOut()) {
			dpd.setLatestDataValueRange(DataPointDefinition.RANGE_INDETERMINATE);
			dpd.setSecondaryValueRange(DataPointDefinition.RANGE_INDETERMINATE);
			if (LOG.isDebugEnabled()){
				LOG.debug("sample update timed out. set INDETERMINATE for definition filepath:"+dpd.getFilePath());
			}
		}
		return dpd;
	}

	@Override
	public void updateCalculation(Double latestDataValue, Long latestTimestamp) {
		if ((latestDataValue==null)||(latestTimestamp==null)){
			throw new IllegalStateException("updateCalculation() cannot have null values: latestTimestamp:"
					+latestTimestamp+" latestDataValue: "+latestDataValue);
		}
		
		// this sets the last real time this calculator has been updated
		this.realUpdateTime=new Date().getTime();
		
		this.latestDataValue= latestDataValue;
		this.latestTimestamp=latestTimestamp;
		if (previousTimestamp==null) previousTimestamp=latestTimestamp;

		// calculate basic mouseover text. You Can replace this with a better example
		Date date = new Date(latestTimestamp);
		Date prevDate=new Date(previousTimestamp);
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS");
		mouseOverText="Value Statistics:"
				+ "<BR>\n  Latest Data Value: "+latestDataValue
				+ "<BR>\n  Latest Timestamp:"+latestTimestamp+" ("+df.format(date)+")"
				+ "<BR>\n  Previous DataValue: "+prevDataValue
				+ "<BR>\n  Previous Timestamp "+previousTimestamp+" ("+df.format(prevDate)+")"
				;

		//calculate left arrow - absolute change in value
		if (prevDataValue==null) prevDataValue=latestDataValue;
		if (latestDataValue>prevDataValue) {
			this.leftTrendArrow=DataPointDefinition.TREND_UP;
		} else if (latestDataValue<prevDataValue){
			this.leftTrendArrow=DataPointDefinition.TREND_DOWN;
		} else {
			this.leftTrendArrow=DataPointDefinition.TREND_LEVEL;
		}

		//TODO ADD FULL CALCULATION METHODS HERE IN EXTENDED CLASS
		
		// finish by updating previous timestamp values
		LOG.debug("updateCalculation result:"+mouseOverText);
		previousTimestamp=latestTimestamp;
		prevDataValue=latestDataValue;

	}

	@Override
	public String dataToCSV(){
		StringBuffer sb= new StringBuffer();
		sb.append(latestDataValue.toString());

		return sb.toString();
	}

}
