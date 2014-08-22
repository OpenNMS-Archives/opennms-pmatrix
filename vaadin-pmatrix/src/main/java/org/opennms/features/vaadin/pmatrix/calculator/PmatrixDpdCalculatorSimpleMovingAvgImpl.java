package org.opennms.features.vaadin.pmatrix.calculator;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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
public class PmatrixDpdCalculatorSimpleMovingAvgImpl extends PmatrixDpdCalculator {
	private static final Logger LOG = LoggerFactory.getLogger(PmatrixDpdCalculatorSimpleMovingAvgImpl.class);

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
	private Long sampleTimeout=null;

	/**
	 *  The last time in real time this calculator was updated
	 *  This value is not persisted and is used only to detect timeouts
	 */
	private long realUpdateTime=new Date().getTime();

	/**
	 * @return the realUpdateTime  the last time in real time this calculator was updated
	 * This value is not persisted and is used only to detect timeouts
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

		// set configuration if not set for timeout
		if (sampleTimeout==null){
			sampleTimeout=DEFAULT_TIMEOUT;
			String timeOutStr = this.getConfig().get(SAMPLE_TIMEOUT_KEY);
			if (timeOutStr==null){
				LOG.warn("No value set for "+SAMPLE_TIMEOUT_KEY+":"+timeOutStr+" setting timeout to "+sampleTimeout);
			} else try{
				sampleTimeout=Long.valueOf(timeOutStr);
			} catch (Exception e){
				LOG.error("cannot parse integer value for "+SAMPLE_TIMEOUT_KEY+":"+timeOutStr+" setting timeout to "+sampleTimeout);
			}
			LOG.debug("Sample timeout has been set to:"+sampleTimeout);
		}
		
		//test if timeout passed
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

		StringBuffer thresholdMouseTxtBuff = new StringBuffer();
		StringBuffer mouseOverTextBuff = new StringBuffer();
		// this sets the last real time this calculator has been updated
		this.realUpdateTime=new Date().getTime();

		this.latestDataValue= latestDataValue;
		this.latestTimestamp=latestTimestamp;
		if (previousTimestamp==null) previousTimestamp=latestTimestamp;

		// set color normal range
		latestDataValueRange=DataPointDefinition.RANGE_NORMAL;

		// calculate basic mouseover text. You Can replace this with a better example
		Date date = new Date(latestTimestamp);
		Date prevDate=new Date(previousTimestamp);

		DecimalFormat decimalFormat = new DecimalFormat("#.###");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS");
		String prevDateStr= (prevDataValue==null) ? "null" : Double.toString(prevDataValue);
		mouseOverTextBuff.append("Value Statistics:")
				.append("<BR>\n  Latest Data Value: ").append(decimalFormat.format(latestDataValue))
				.append("<BR>\n  Latest Timestamp: " ).append(latestTimestamp).append(" (").append(dateFormat.format(date)).append(")")
				.append("<BR>\n  Previous DataValue: ").append(prevDateStr)
				.append("<BR>\n  Previous Timestamp: ").append(previousTimestamp).append(" (").append(dateFormat.format(prevDate)).append(")")
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

		// work out average, variance, std deviation
		add(latestDataValue);
		Double average = this.getAverage();
		Double variance = this.getVariance();
		Double stdDeviation = Math.sqrt(variance);
		Double absoluteDifferenceFromAverage = Math.sqrt((latestDataValue-average)*(latestDataValue-average));

		// add additional information to mouseOverText
		mouseOverTextBuff.append("<BR>\n  Average: ").append(decimalFormat.format(average)).append(" over ").append(persistwindow.size()).append(" samples (of max window size ").append(maxNoOfSamples).append(")")
				.append("<BR>\n  Std Deviation: ").append(decimalFormat.format(stdDeviation))
				.append(" (Variance: ").append(decimalFormat.format(variance)).append(")")
				.append("<BR>\n  Absolute Difference from Average: ").append(decimalFormat.format(absoluteDifferenceFromAverage))
				.append("<BR>\n  Lowest Value: ").append(decimalFormat.format(lowWaterMark)).append("  Highest Value: ").append(decimalFormat.format(hiWaterMark));

		// define threshold type configuration

		// update configuration if thresholdType is not yet defined
		if(thresholdType==null) {
			thresholdType= getConfig().get(THRESHOLD_TYPE_PROPERTY_NAME);
			if (thresholdType==null) {
				thresholdType=THRESHOLD_TYPE_STANDARD_DEVIATION;
				LOG.warn("Threshold value not set in configuration. Defaulting to use "+thresholdType);
			}

			String warningThresholdStr=getConfig().get(WARNING_THRESHOLD_MULTIPLIER);
			String minorThresholdStr=getConfig().get(MINOR_THRESHOLD_MULTIPLIER);
			String majorThresholdStr=getConfig().get(MAJOR_THRESHOLD_MULTIPLIER);
			String criticalThresholdStr=getConfig().get(CRITICAL_THRESHOLD_MULTIPLIER);

			if(criticalThresholdStr==null){
				LOG.warn("configuration property "+CRITICAL_THRESHOLD_MULTIPLIER+" is undefined");
			} else 	try {
				criticalThresholdMultiplier=Double.valueOf(criticalThresholdStr);
			} catch (Exception e) {
				LOG.error("unable to parse configuration property "+CRITICAL_THRESHOLD_MULTIPLIER+":"+criticalThresholdStr);
			}
			if(majorThresholdStr==null){
				LOG.warn("configuration property "+MAJOR_THRESHOLD_MULTIPLIER+" is undefined");
			} else try {
				majorThresholdMultiplier=Double.valueOf(majorThresholdStr);
			} catch (Exception e) {
				LOG.error("unable to parse configuration property "+MAJOR_THRESHOLD_MULTIPLIER+":"+majorThresholdStr);
			}
			if(minorThresholdStr==null){
				LOG.warn("configuration property "+MINOR_THRESHOLD_MULTIPLIER+" is undefined");
			} else try {
				minorThresholdMultiplier=Double.valueOf(minorThresholdStr);
			} catch (Exception e) {
				LOG.error("unable to parse configuration property "+MINOR_THRESHOLD_MULTIPLIER+":"+minorThresholdStr);
			}
			if(warningThresholdStr==null){
				LOG.warn("configuration property "+WARNING_THRESHOLD_MULTIPLIER+" is undefined");
			} else try {
				warningThresholdMultiplier=Double.valueOf(warningThresholdStr);
			} catch (Exception e) {
				LOG.error("unable to parse configuration property "+WARNING_THRESHOLD_MULTIPLIER+":"+warningThresholdStr);
			}

			LOG.debug("Using the following property values for thresholds. Threshold type:"+thresholdType+"\n"
					+ "   Warning Threshold Multiplier String:"+warningThresholdStr+"\n"
					+ "   Minor Threshold Multiplier String:"+minorThresholdStr+"\n"
					+ "   Major Threshold Multiplier String:"+majorThresholdStr+"\n"
					+ "   Critical Threshold Multiplier String:"+criticalThresholdStr+"\n");

			// check threshold string
			if(THRESHOLD_TYPE_ABSOLUTE.equals(thresholdType)) {
				// this is text to include in mouseover text if absolute thresholds
				thresholdMouseTxtBuff.append("<BR>\n  Threshold Type:").append(THRESHOLD_TYPE_ABSOLUTE)
						.append("<BR>\n")
						.append(" Absolute Thresholds: Warn:").append(warningThresholdMultiplier)
						.append(" Minor:").append(minorThresholdMultiplier)
						.append(" Major:").append(majorThresholdMultiplier)
						.append(" Critical:").append(criticalThresholdMultiplier).append("\n");
			} else {
				if ( THRESHOLD_TYPE_STANDARD_DEVIATION.equals(thresholdType)
						|| THRESHOLD_TYPE_AVERAGE.equals(thresholdType)){
					// do nothing - recognised values
				}
				else {
					LOG.error("unknown threshold type defined for property name:'"+THRESHOLD_TYPE_PROPERTY_NAME
							+ "' Value='"+thresholdType+"' Defaulting to stdDeviation");
				}
				// this is text to include in mouseover text
				thresholdMouseTxtBuff.append("<BR>\n  Threshold Type:").append(thresholdType)
						.append("<BR>\n")
						.append(" Threshold Multipliers: Warn:").append(warningThresholdMultiplier)
						.append(" Minor:").append(minorThresholdMultiplier)
						.append(" Major:").append(majorThresholdMultiplier)
						.append(" Critical:").append(criticalThresholdMultiplier).append("\n");

			}
		}
		mouseOverTextBuff.append(thresholdMouseTxtBuff);

		Double warningThresholdValue=null;
		Double minorThresholdValue=null;
		Double majorThresholdValue=null;
		Double criticalThresholdValue=null;
		Double thresholdValue=null;

		if(THRESHOLD_TYPE_ABSOLUTE.equals(thresholdType)){
			// the threshold value is absolute
			warningThresholdValue=warningThresholdMultiplier;
			minorThresholdValue=minorThresholdMultiplier;
			majorThresholdValue=majorThresholdMultiplier;
			criticalThresholdValue=criticalThresholdMultiplier;
		} else {
			// the threshold value a relative multiplier of average or sd
			if(THRESHOLD_TYPE_STANDARD_DEVIATION.equals(thresholdType)){
				thresholdValue=stdDeviation;
			} else if(THRESHOLD_TYPE_AVERAGE.equals(thresholdType)){
				thresholdValue=average;
			} else {
				// default value is stdDeviation if we don't know the type
				thresholdValue=stdDeviation;
			}
			warningThresholdValue=thresholdValue*warningThresholdMultiplier;
			minorThresholdValue=thresholdValue*minorThresholdMultiplier;
			majorThresholdValue=thresholdValue*majorThresholdMultiplier;
			criticalThresholdValue=thresholdValue*criticalThresholdMultiplier;
		}

		// compare threshold values and set range color
		if(absoluteDifferenceFromAverage<warningThresholdValue) {
			latestDataValueRange=DataPointDefinition.RANGE_NORMAL;
		} else if(absoluteDifferenceFromAverage<minorThresholdValue) {
			latestDataValueRange=DataPointDefinition.RANGE_WARNING;
		} else if(absoluteDifferenceFromAverage<majorThresholdValue) {
			latestDataValueRange=DataPointDefinition.RANGE_MINOR;
		} else if(absoluteDifferenceFromAverage<criticalThresholdValue) {
			latestDataValueRange=DataPointDefinition.RANGE_MAJOR;
		} else {
			latestDataValueRange=DataPointDefinition.RANGE_CRITICAL;
		} 		

		// define secondaryValue
		String secondValSelector = getConfig().get(SECOND_VALUE_PROPERTY_NAME);
		Double newSecondaryValue=null;
		if (SECOND_VALUE_AVERAGE.equals(secondValSelector)){
			newSecondaryValue=average;
		} else if (SECOND_VALUE_HI.equals(secondValSelector)){
			newSecondaryValue=hiWaterMark;
		} else if (SECOND_VALUE_LOW.equals(secondValSelector)){
			newSecondaryValue=lowWaterMark;
		} else if (SECOND_VALUE_NONE.equals(secondValSelector)){
			// newSecondaryValue == null so not rendered
			// also not rendered if unrecognized selector
		} 
		Double tmpSecondaryValue=newSecondaryValue;

		//calculate right arrow - using absolute change in value
		if (newSecondaryValue==null){
			this.rightTrendArrow=null; // do not render if no secondary value
		} else {
			if (secondaryValue==null) secondaryValue=newSecondaryValue;
			if (newSecondaryValue>secondaryValue) {
				this.rightTrendArrow=DataPointDefinition.TREND_UP;
			} else if (newSecondaryValue<secondaryValue){
				this.rightTrendArrow=DataPointDefinition.TREND_DOWN;
			} else {
				this.rightTrendArrow=DataPointDefinition.TREND_LEVEL;
			}
		}
		secondaryValue=tmpSecondaryValue;

		// set color normal range
		secondaryValueRange=DataPointDefinition.RANGE_NORMAL;

		//TODO END
		
		// return mouseover text 
		mouseOverText=mouseOverTextBuff.toString();

		// finish by updating previous timestamp values
		LOG.debug("updateCalculation result:"+mouseOverText);
		previousTimestamp=latestTimestamp;
		prevDataValue=latestDataValue;

	}

	/*
	 * ********************************************
	 * Added Methods and fields for this calculator
	 * ********************************************
	 */


	/**
	 *  persistwindow is the circular store of values used for calculations. The the storing class
	 *  must implement the Queue interface. We are using ArrayDeque because it is 
	 *  more memory efficient than LinkedList
	 */
	@XmlElementWrapper(name="data")
	@XmlElement(name="x")
	private Collection<Double> persistwindow = new ArrayDeque<Double>();

	/**
	 * @return the persistwindow
	 */
	public Collection<Double> getPersistwindow() {
		return persistwindow;
	}

	/**
	 * @param persistwindow the persistwindow to set
	 */
	public void setPersistwindow(Collection<Double> persistwindow) {
		this.persistwindow = persistwindow;
	}



	private Integer maxNoOfSamples=null; //must be a positive integer
	private Double sum = null;
	private Double sumOfSquares = null;

	private Double lowWaterMark=null;
	private Double hiWaterMark=null;

	//default values for thresholds
	private Double criticalThresholdMultiplier=3d;
	private Double majorThresholdMultiplier=2d;
	private Double minorThresholdMultiplier=1.5d;
	private Double warningThresholdMultiplier=1d;

	private String thresholdType=null;


	/**
	 * property key for determining max number of samples defined in configuration
	 */
	public static final String MAX_SAMPLE_NO_PROPERTY_NAME="maxSampleNo";

	/**
	 * property key and values for determining second value
	 */
	public static final String SECOND_VALUE_PROPERTY_NAME="secondValue";
	public static final String SECOND_VALUE_AVERAGE="averageValue";
	public static final String SECOND_VALUE_HI="highestValue";
	public static final String SECOND_VALUE_LOW="lowestValue";
	public static final String SECOND_VALUE_NONE="noValue";

	/**
	 * property keys and values for determining alarm thresholds. The value for each key determines the multiple
	 * applied to average or standard deviation which is used to determine the threshold
	 */
	public static final String MAJOR_THRESHOLD_MULTIPLIER="majorThresholdMultiplier";
	public static final String MINOR_THRESHOLD_MULTIPLIER="minorThresholdMultiplier";
	public static final String WARNING_THRESHOLD_MULTIPLIER="warningThresholdMultiplier";
	public static final String CRITICAL_THRESHOLD_MULTIPLIER="criticalThresholdMultiplier";

	/**
	 * property keys and values for determining if alarm threshold is based on standard deviation or average or absolute value
	 */
	public static final String THRESHOLD_TYPE_PROPERTY_NAME="thresholdType";
	public static final String THRESHOLD_TYPE_STANDARD_DEVIATION="standardDeviation";
	public static final String THRESHOLD_TYPE_AVERAGE="average";
	public static final String THRESHOLD_TYPE_ABSOLUTE="absolute";

	/**
	 * default maximum number of samples
	 */
	public static final Integer DEFAULT_MAX_NO_OF_SAMPLES=100;

	private void add(Double latestSample) {

		// check configuration
		// if max number of samples maxNoOfSamples > current sample list size , resize ArrayDeque
		// if loaded persistwindow.size() > maxNoOfSamples, issue warning but don't change size of queue
		// This allows us to load persisted data into a larger sample buffer and not loose persisted data 
		// if sample buffer is reduced in size
		if (maxNoOfSamples==null) {
			String maxNoStr = getConfig().get(MAX_SAMPLE_NO_PROPERTY_NAME);

			if (maxNoStr==null) {
				maxNoOfSamples = DEFAULT_MAX_NO_OF_SAMPLES;
				LOG.warn("No maximum number of samples defined using configuration property name:'"+MAX_SAMPLE_NO_PROPERTY_NAME
						+ "' for calculator '"+this.getClass().getSimpleName()+"'. Using default value:"+DEFAULT_MAX_NO_OF_SAMPLES);
			} else try {
				maxNoOfSamples= Integer.valueOf(maxNoStr);
			} catch (NumberFormatException e){
				maxNoOfSamples = DEFAULT_MAX_NO_OF_SAMPLES;
				LOG.warn("cannot parse '"+maxNoStr
						+ "' as Integer number of samples defined using configuration property name:'"+MAX_SAMPLE_NO_PROPERTY_NAME
						+ "' for calculator '"+this.getClass().getSimpleName()+"'. Using default value:"+DEFAULT_MAX_NO_OF_SAMPLES);

			}
			if (persistwindow.size()>maxNoOfSamples){
				LOG.warn("The actual size of the persisted sample list ("+persistwindow.size()+") is > max number of samples ("
						+maxNoOfSamples+ ") defined using configuration property name:"+MAX_SAMPLE_NO_PROPERTY_NAME
						+ "for calculator "+this.getClass().getSimpleName()+". We will use the size of the larger persisted list as the size");
				//TODO note alternative option would be to reduce size of list
				maxNoOfSamples=persistwindow.size();
			} else {
				ArrayDeque<Double> newpersistwindow = new ArrayDeque<Double>(maxNoOfSamples);
				newpersistwindow.addAll(persistwindow);
				persistwindow=newpersistwindow;
			}
		}

		// Initialize sum, sumOfSquares low and high water marks if not set
		if(sum==null){
			sum = 0d;
			sumOfSquares=0d;

			lowWaterMark=Double.MAX_VALUE;
			hiWaterMark=Double.MIN_VALUE;

			for (Double sample: persistwindow)	{
				sum=sum+sample;
				sumOfSquares=sumOfSquares+sample*sample;

				if(sample>hiWaterMark) hiWaterMark=sample;
				if(sample<lowWaterMark) lowWaterMark=sample;
			}

		}

		Queue<Double> window = (Queue<Double>) persistwindow;

		if(latestSample>hiWaterMark){
			hiWaterMark=latestSample;
		}

		if(latestSample<lowWaterMark){
			lowWaterMark=latestSample;
		}

		sum = sum+latestSample;
		sumOfSquares=sumOfSquares+latestSample*latestSample;
		window.add(latestSample);
		if (window.size() > maxNoOfSamples) {
			Double endValue=window.remove();

			sum = sum - endValue;
			sumOfSquares=sumOfSquares-endValue*endValue;

			// recalculate low and hi water marks only if removed sample is low or hi water mark
			// this is inefficient but hopefully only occasional
			// note >= and <= to catch any rounding issues
			if(endValue<=lowWaterMark){
				lowWaterMark=Double.MAX_VALUE;
				for (Double sample: persistwindow)	{
					if(sample<lowWaterMark) lowWaterMark=sample;
				}
			} else if(endValue>=hiWaterMark){ 
				hiWaterMark=Double.MIN_VALUE;
				for (Double sample: persistwindow)	{
					if(sample>hiWaterMark) hiWaterMark=sample;
				}
			}
		}
	}

	public Double getAverage() {
		Queue<Double> window = (Queue<Double>) persistwindow;

		if (window.isEmpty()||sum==null) return 0d; // technically the average is undefined
		Double n = Double.valueOf(window.size());
		return sum / n;
	}


	public Double getVariance(){
		//http://www.dsprelated.com/showmessage/97276/1.php

		//Calculate Moving Average: (done in getAverage)
		// M = SX1 / N

		// Calculating Moving Variance
		// V = (N * SX2 - (SX1 * SX1)) / (N * (N - 1))
		Queue<Double> window = (Queue<Double>) persistwindow;
		double average = getAverage();
		double n = Double.valueOf(window.size());
		if (window.isEmpty()||sumOfSquares==null) return 0d; // technically the variance is undefined
		double variance= (n * sumOfSquares - average*average) / (n*(n-1));
		return variance;
	}

	@Override
	public String dataToCSV(){
		StringBuffer sb= new StringBuffer();
		Iterator<Double> it = persistwindow.iterator();

		while(it.hasNext()){
			sb.append(it.next());
			if (it.hasNext()) sb.append(',');
		}
		return sb.toString();
	}

	/* notes
		// see http://stackoverflow.com/questions/5147378/rolling-variance-algorithm
		//int window_size=10;
		//double next_index = (index + 1)/window_size;    // oldest x value is at next_index.

		//double mean;
		//double new_mean = mean + (x_new - xs[next_index])/window_size;

		//I have adapted Welford's algorithm and it works for all the values that I have tested with.

		//varSum = var_sum + (x_new - mean) * (x_new - new_mean) - (xs[next_index] - mean) * (xs[next_index] - new_mean);

		//xs[next_index] = x_new;
		//index = next_index;

	 */


}
