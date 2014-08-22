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

package org.opennms.features.vaadin.pmatrix.engine;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.opennms.features.vaadin.pmatrix.calculator.PmatrixDpdCalculator;
import org.opennms.features.vaadin.pmatrix.model.DataPointDefinition;
import org.opennms.features.vaadin.pmatrix.model.PmatrixSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;


public class PmatrixDataSourceImpl implements PmatrixDataSource, DataPointMapUpdateListener {
	private static final Logger LOG = LoggerFactory.getLogger(PmatrixDataSourceImpl.class);

	/**
	 * local data point map used to cross reference data points to columns and rows in table
	 * String key = the filepath in the data point message 
	 * DataPointDefinition value =  the row and column in the table. Note that the actual value is NOT used here
	 * 
	 */
	private ConcurrentMap<String,DataPointDefinition> localDataPointMap = new ConcurrentHashMap<String,DataPointDefinition>();

	/**
	 * reference for the dataPointMap 
	 */
	private DataPointMapDao dataPointMapDao = null;

	private String componentHeight=null;
	private String componentWidth=null;	
	private String pmatrixTitle = "OpenNMS Pmatrix Table (Title undefined)";
	private String pmatrixName= null;
	private boolean hidePmatrixHeaders=false;
	private PmatrixSpecification pmatrixSpecification = null;
	private Component attachedComponent;
	private IndexedContainer pmatrixDataContainer = null;

	/*
	 * values for setting the key description for this pmatrix display
	 */
	private String latestDataValueDescription=null;
	private String secondaryNumberDescription=null;
	private String leftRangeArrowDescription=null;
	private String rightRangeArrowDescription=null;
	private String descriptiveText=null;
	private String colorDescriptionStr=null;

	/*
	 * values for switching on or off cell decorations
	 */
	private boolean latestDataValueEnabled=true;
	private boolean secondaryNumberEnabled=true;
	private boolean leftArrowEnabled =true;
	private boolean rightArrowEnabled=true;
	private boolean linkUrlEnabled=true;
	private boolean mouseOverTextEnabled=true;

	@Override
	public String getComponentHeight() {
		return componentHeight;
	}

	@Override
	public void setComponentHeight(String componentHeight) {
		this.componentHeight=componentHeight;
	}

	@Override
	public String getComponentWidth() {
		return componentWidth;
	}

	@Override
	public void setComponentWidth(String componentWidth) {
		this.componentWidth=componentWidth;
	}

	public synchronized IndexedContainer  getPmatrixDataContainer() {
		if (null == pmatrixDataContainer) {
			// can be a race condition if this module is registered for events before
			// pmatrixSpecification is loaded
			if(pmatrixSpecification!=null) createPmatrixDataContainer();
		}
		return pmatrixDataContainer;
	}

	/**
	 * @return the pmatrixSpecification
	 */

	public PmatrixSpecification getPmatrixSpecification() {
		return pmatrixSpecification;
	}

	/**
	 * creates and initializes a PmatrixDataContainer from the
	 * pmatrixSpecification
	 */
	private void createPmatrixDataContainer() {

		if(LOG.isDebugEnabled()) LOG.debug("constructing new pmatrix table for table pmatrixName:'"+pmatrixName+ "' matrixTitle:'"+pmatrixTitle+"'");

		// Define two columns for the built-in container

		pmatrixDataContainer = new IndexedContainer();

		// first column contains row names
		pmatrixDataContainer.addContainerProperty("rowName", String.class, null);

		// used to test for duplicate or missing row/column names
		// note some tests are duplicated - so may show two errors for same problem. However better to keep error messages together
		Set<String> testColNames=new HashSet<String>();
		Set<String> testRowNames=new HashSet<String>();

		// set up properties for container
		if(pmatrixSpecification.getColumnNames()==null || pmatrixSpecification.getColumnNames().isEmpty()){
			LOG.error("Pmatrix definition with name:'"+pmatrixName+"' has no column names defined");
		} else for (String columnName : pmatrixSpecification.getColumnNames()) {
			if(testColNames.contains(columnName)){
				LOG.error("Pmatrix definition with name:'"+pmatrixName+"' has duplicate column name: '"+columnName+"'");
			} else {
				if( LOG.isDebugEnabled()) LOG.debug("adding columnName:'"+columnName+ "' to table:'"+pmatrixName+"'");
				testColNames.add(columnName);
				pmatrixDataContainer.addContainerProperty(columnName, Component.class,	null);
			}
		}

		// set up items for container
		if(pmatrixSpecification.getRowNames()==null || pmatrixSpecification.getRowNames().isEmpty()){
			LOG.error("Pmatrix definition with name:'"+pmatrixName+"' has no row names defined");
		} else for (String rowName : pmatrixSpecification.getRowNames()) {
			if( LOG.isDebugEnabled()) LOG.debug("adding rowName:'"+rowName+ "' to table:'"+pmatrixName);
			if(testRowNames.contains(rowName)){
				LOG.error("Pmatrix definition with name:'"+pmatrixName+"' has duplicate row name: '"+rowName+"'");
			} else {
				testRowNames.add(rowName);

				Item row = pmatrixDataContainer.addItem(rowName);
				row.getItemProperty("rowName").setValue(rowName);

				// fill empty cells with empty values
				for (String colName : pmatrixSpecification.getColumnNames()) {
					String notDefinedStr ="<div class=\"pmatrix.notdefined\" style=\"vertical-align: middle; text-align: center;  color:black;  \">-</div>";
					Label label = new Label(notDefinedStr, ContentMode.HTML);
					label.setDescription("A data value has not <BR>been defined for this cell.");
					row.getItemProperty(colName).setValue(label);
				}
			}
		}

		// set up data types if exist
		List<DataPointDefinition> dataPointDefintions = pmatrixSpecification.getDatapointDefinitions();

		for (DataPointDefinition dpd : dataPointDefintions) {
			if(dpd.getRowName()!=null 
					&& dpd.getColName()!=null 
					&& testColNames.contains(dpd.getColName())
					&& testRowNames.contains(dpd.getRowName())){
			} else {
				LOG.error("data point definition uses unknown row or column names for table name:'"+pmatrixName
						+ "': row name:'"
						+ dpd.getRowName()
						+"' is in table:"
						+ testRowNames.contains(dpd.getRowName())
						+ "; col name:'"
						+ dpd.getColName()
						+ "' is in table:"
						+ testColNames.contains(dpd.getColName())
						+ "; filePath:'"
						+ dpd.getFilePath() + "'");
			}
		}


		for (DataPointDefinition dpd : dataPointDefintions) {

			Item row = pmatrixDataContainer.getItem(dpd.getRowName());
			if (row == null) {
				LOG.error("pmatrix row in definition does not exist for table name:'"+pmatrixName+"': row name:'"
						+ dpd.getRowName()
						+ "' filePath:'"
						+ dpd.getFilePath() + "'");
			} else {
				Property cell = row.getItemProperty(dpd.getColName());

				// strange - vaadin will return a cell with a null value even if not defined
				if (cell == null || cell.getValue()==null) {
					LOG.error("pmatrix cell in definition does not exist for table name:'"+pmatrixName
							+ "': row name:'"
							+ dpd.getRowName()
							+"' col name:'"
							+ dpd.getColName()
							+ "' filePath:'"
							+ dpd.getFilePath() + "'");
				} else {
					if(dpd.getGraphURL()==null){
						String filePath = dpd.getFilePath();
						if(LOG.isDebugEnabled()) LOG.debug("graphURL not defined. You need to create a url for point definition filePath:"+filePath);

						// TODO create URL from filePath if doesn't exist
						// Not possible to easily do a translation from filepath TO URL - information is missing for translation to correct graph
						// <filePath>/home/isawesome/devel/opennms-test/dist/share/rrd/response/204.79.197.200/icmp.jrb</filePath>
						// <graphURL>http://localhost:8980/opennms/graph/results.htm?resourceId=node[3].responseTime[204.79.197.200]&amp;reports=icmp</graphURL>
						// This is difficult because PSMSDuration is actually included in a different report see jvm-graph.properties 
						// report.jvm.gc.psms.columns=PSMSCollCnt, PSMSCollTime, PSMSDuration
						// <path>/home/isawesome/devel/opennms-test/dist/share/rrd/snmp/4/opennms-jvm/PSMSDuration.jrb</path>
						// http://localhost:8980/opennms/graph/results.htm?zoom=true&relativetime=custom&resourceId=node[4].responseTime[127.0.0.1]&reports=http&start=1392033278231&end=1392119678231
						// http://localhost:8980/opennms/graph/results.htm?zoom=true&relativetime=custom&resourceId=node[4].responseTime[127.0.0.1]&reports=icmp&start=1392033278231&end=1392119678231
					}

					String labelStr = labelStrFromDpd(dpd);

					//Note using Label instead of Link because Link proved very difficult to style
					Label label= new Label(labelStr, ContentMode.HTML);

					String mouseOverText = mouseOverTextEnabled ? dpd.getMouseOverText() : "";

					label.setDescription(mouseOverText);
					cell.setValue(label);

					// insert data value point in localDataPointMap if not
					// duplicated.
					if (localDataPointMap.putIfAbsent(dpd.getFilePath(), dpd) != null) {
						LOG.error("the filePath in a dataPointDefinition is duplicated in table definition name:'"+pmatrixName+"' for data point definition:"
								+ " row name:'"
								+ dpd.getRowName()
								+ "' col name:'"
								+ dpd.getColName()
								+ "' filePath:' "
								+ dpd.getFilePath() + "'");
					}

				}
			}
		}

	}

	/**
	 * @param pmatrixSpecification
	 *            the pmatrixSpecification to set the pmatrixSpecification
	 *            defines the data set and layout of the pmatrix table. It is
	 *            part of the initial configuration of the feature
	 */
	public void setPmatrixSpecification(PmatrixSpecification pmatrixSpecification) {
		this.pmatrixSpecification = pmatrixSpecification;

		if (pmatrixSpecification==null) throw new IllegalArgumentException("pmatrixSpecification must not be null");

		if(pmatrixSpecification.getPmatrixName()==null || "".equals(pmatrixSpecification.getPmatrixName())) {
			throw new IllegalArgumentException("pmatrixSpecification pMatrixName must not be empty or null");
		} else {
			pmatrixName=pmatrixSpecification.getPmatrixName();
		}

		componentHeight=pmatrixSpecification.getComponentHeight();
		if(componentHeight==null){
			LOG.warn("the component (table) height is not defined in the pmatrixSpecification for pmatrixName '"+pmatrixName+"'. Using Default.");
		} else LOG.info("the component (table) height for pmatrixName '"+pmatrixName+"' is:"+componentHeight);

		componentWidth=pmatrixSpecification.getComponentWidth();
		if(componentWidth==null){
			LOG.warn("the component (table) width is not defined in the pmatrixSpecification for pmatrixName '"+pmatrixName+"'. Using Default.");
		} else LOG.info("the component (table) width for pmatrixName '"+pmatrixName+"' is:"+componentWidth);

		if (pmatrixSpecification.getPmatrixTitle()==null) {
			LOG.warn("the pmatrixSpecification definition does not have a pmatrixTitle defined for pmatrixName '"+pmatrixName+"'. Using Default.");
		} else {
			pmatrixTitle= pmatrixSpecification.getPmatrixTitle();
		}

		LOG.info("Pmatrix table name:'"+pmatrixName+"' Title set to:"+pmatrixTitle);

		// Set up if the headers will be hidden
		hidePmatrixHeaders=pmatrixSpecification.getHidePmatrixHeaders();
		
		// Set up the description text fields for the table
		latestDataValueDescription=pmatrixSpecification.getLatestDataValueDescription();
		secondaryNumberDescription=pmatrixSpecification.getSecondaryNumberDescription();
		leftRangeArrowDescription=pmatrixSpecification.getLeftRangeArrowDescription();
		rightRangeArrowDescription=pmatrixSpecification.getRightRangeArrowDescription();
		descriptiveText=pmatrixSpecification.getDescriptiveText();
		colorDescriptionStr=pmatrixSpecification.getColorDescriptionStr();

		// set up switches for cell decorations for the table
		latestDataValueEnabled=pmatrixSpecification.getLatestDataValueEnabled();
		secondaryNumberEnabled=pmatrixSpecification.getSecondaryNumberEnabled();
		leftArrowEnabled =pmatrixSpecification.getLeftArrowEnabled();
		rightArrowEnabled=pmatrixSpecification.getRightArrowEnabled();
		linkUrlEnabled=pmatrixSpecification.getLinkUrlEnabled();
		mouseOverTextEnabled=pmatrixSpecification.getMouseOverTextEnabled();

	}

	/**
	 * updates the DataSourceContainer from a dataPointMapDao
	 * first synchronizes with the attached Vaddin UI component if possible
	 * see https://vaadin.com/forum#!/thread/1820683 for explanation
	 * @param dataPointMapDao
	 */
	//TODO REMOVE
	public synchronized void updateDataSourceContainerOLD(){

		// test if we have a vaadin session to synchronize with
		if ( (attachedComponent !=null) && 
				(attachedComponent.getUI() !=null ) &&
				(attachedComponent.getUI().getSession() !=null )){

			// this does a synchronized vaadin update
			attachedComponent.getUI().getSession().getLockInstance().lock();
			try {
				updateDsc();
			} catch (Exception e){
				LOG.error("problem updating data source container for pmatrix table pmatrixName:" +pmatrixName);
			} finally {
				// test added because sometimes null pointer exception here - not sure why
				if( (attachedComponent.getUI() !=null ) && (attachedComponent.getUI().getSession() !=null ))
					attachedComponent.getUI().getSession().getLockInstance().unlock();
			}
		} else {
			// this does an unsynchronized vaadin update if there is no session defined
			updateDsc(); 
		}

	}

	public synchronized void updateDataSourceContainer(){

		//  used access method to update tables - although has same problem as before
		// see https://vaadin.com/book/vaadin7/-/page/advanced.push.html
		boolean updateWithoutSync = true;

		// test if we have a vaadin UI to synchronize with
		if ( attachedComponent!=null ){
			UI ui=attachedComponent.getUI(); 
			if(ui!=null) {
				updateWithoutSync =false;
				//this does a synchronised vaadin update
				try{
					ui.accessSynchronously(new Runnable() {
						@Override
						public void run() {
							try {
								if(LOG.isDebugEnabled()) LOG.debug("updating pmatrix UI in access thread for pmatrixName:"+pmatrixName);
								updateDsc(); 
							} catch (Exception e){
								LOG.error("problem in thread synchronously updating data source container for pmatrix table pmatrixName:" +pmatrixName+" :", e);
							}
						}
					});
				} catch (Exception e){
					LOG.error("problem calling thread to synchronously update data source container for pmatrix table pmatrixName:" +pmatrixName+" :", e);
				}
			}
		} 

		if (updateWithoutSync ==true){
			// this does an unsynchronized vaadin update if there is no session defined
			try {
				if(LOG.isDebugEnabled()) LOG.debug("updating pmatrix ui WITH NO LOCK for pmatrixName:"+pmatrixName);
				updateDsc(); 
			} catch (Exception e){
				LOG.error("problem updating data source container WITH NO LOCK for pmatrix table pmatrixName:" +pmatrixName);
			}
			updateDsc(); 
		}

	}

	/**
	 * updates the DataSourceContainer from a dataPointMapDao without synchronization
	 */
	private void updateDsc(){

		IndexedContainer pmdc = getPmatrixDataContainer();

		// this deals with the minor race when the PmatrixDatasource registers for updates before the table is fully defined.
		if (pmdc==null) {
			if(LOG.isDebugEnabled()) LOG.debug("cannot update datasource container as still being constructed");
			return;
		}

		// check each value in dataPointMapDao if defined in localDataPointMap. If not then ignore.
		for (String dataPointFilePath : dataPointMapDao.getDataPointMap().keySet()) {

			DataPointDefinition localdpd = localDataPointMap.get(dataPointFilePath);
			if(localdpd==null) {
				// if no local definition for this data point then do nothing and return
			} else {

				//Long lastCellUpdateTime = localdpd.getLatestTimestamp();

				// if there is a local definition update it with the latest calculation				
				PmatrixDpdCalculator pmatrixDpdCalculator = dataPointMapDao.getDataPointMap().get(dataPointFilePath);

				// if the localdpd has a different update time to the calculator then the calculator has been updated
				// this prevents display container updates when not needed
				
				//TODO SET ALWAYS TRUE THE PROBLEM IS THAT IF UPDATE ONLY HAPPENS ONCE, ALL TABLES ARE NOT UPDATED
				//THERE SEEMS TO BE A PROBLEM WITH VAADIN UPDATING TABLES
				boolean dataIsChanged=true; 
				if (localdpd.getRealUpdateTime()!=pmatrixDpdCalculator.getRealUpdateTime()){
					dataIsChanged=true;
				}

				pmatrixDpdCalculator.updateDpd(localdpd);

				// if there is a local definition then use the row name and column name to update the local cell with data
				Property cell = pmdc.getContainerProperty(localdpd.getRowName(),localdpd.getColName());
				if (cell == null) {
					LOG.error("could not copy value from dataPointMap to pmatrixDataContainer because it couldnt be found in pmatrixDataContainer "
							+ "' dataPointFilePath:' " + dataPointFilePath + "'");
				} else {
					//Note using Label instead of Link because Link proved very difficult to style
					String labelStr = labelStrFromDpd(localdpd);

					// create new label and update container if data is changed or value is indeterminate 
					if(dataIsChanged || Integer.valueOf(DataPointDefinition.RANGE_INDETERMINATE).equals(localdpd.getLatestDataValueRange())){
						//Label label= new Label(labelStr, ContentMode.HTML);
						Label label = (Label) cell.getValue();
						label.setValue(labelStr);
						String mouseOverText = mouseOverTextEnabled ? localdpd.getMouseOverText() : "";
						label.setDescription(mouseOverText);
						cell.setValue(label);
					}
				}
			}
		}

	}

	@Override
	public IndexedContainer getDataSourceContainer() {

		IndexedContainer pmdc = getPmatrixDataContainer();
		// update container with latest values if necessary before return
		updateDataSourceContainer();

		return pmdc;

	}

	@Override
	public DataPointMapDao getDataPointMapDao() {
		return dataPointMapDao;
	}

	@Override
	public void setDataPointMapDao(DataPointMapDao dataPointMapDao) {
		this.dataPointMapDao = dataPointMapDao;
	}

	@Override
	public void dataPointMapUpdated() {
		updateDataSourceContainer();
	}

	@Override
	public String getPmatrixTitle() {
		return pmatrixTitle;
	}

	@Override
	public void setPmatrixTitle(String pmatrixTitle) {
		this.pmatrixTitle=pmatrixTitle;
	}

	@Override
	public String getPmatrixName() {
		return pmatrixName;
	}

	@Override
	public void setPmatrixName(String pmatrixName) {
		this.pmatrixName = pmatrixName;
	}


	@Override
	public boolean getHidePmatrixHeaders() {
		return hidePmatrixHeaders;
	}

	@Override
	public void setHidePmatrixHeaders(boolean hidePmatrixHeaders) {
		this.hidePmatrixHeaders = hidePmatrixHeaders;
	}

	@Override
	public synchronized void setAttachedComponent(Component attachedComponent) {
		this.attachedComponent=attachedComponent;
	}

	@PostConstruct
	@Override
	public void registerWithDataPointMapDao() throws Exception {
		if(LOG.isDebugEnabled()) LOG.debug("registering PmatrixDataSource for updates from dataPointMapDao for pmatrix name:"+ pmatrixName);
		if(!dataPointMapDao.addDataPointMapUpdateListener(this)) {
			if(LOG.isDebugEnabled()) LOG.debug("tried to register PmatrixDataSource pmatrix name:"+ pmatrixName +" with dataPointMapDao but it is already registered");
		}
	}

	@PreDestroy
	@Override
	public void unRegisterWithDataPointMapDao() throws Exception {
		if(LOG.isDebugEnabled()) LOG.debug("Unregistering PmatrixDataSource for updates from dataPointMapDao for pmatrix name:"+ pmatrixName);
		if(!dataPointMapDao.removeDataPointMapUpdateListener(this)) {
			if(LOG.isDebugEnabled()) LOG.debug("tried to unregister PmatrixDataSource pmatrix title:"+ pmatrixName +" from dataPointMapDao but it is not registered");
		}
	}

	/**
	 * translates the range values between DataPointDefinition.RANGE_CRITICAL and DataPointDefinition.RANGE_INDETERMINATE
	 * into appropriate HTML colours. 
	 * @param dataPointDefinitionRange
	 * @return corresponding html color string
	 */
	public String linkColorForRange(Integer dataPointDefinitionRange){

		String linkcolor="black;";
		if (dataPointDefinitionRange!=null)	switch (dataPointDefinitionRange) {
		case DataPointDefinition.RANGE_CRITICAL:  linkcolor = "purple;";
		break;
		case DataPointDefinition.RANGE_MAJOR:  linkcolor = "red;";
		break;
		case DataPointDefinition.RANGE_MINOR:  linkcolor = "LightSalmon;"; // orange hard to distinguish from goldenRod
		break;
		case DataPointDefinition.RANGE_WARNING:  linkcolor = "goldenRod;"; // chosen because yellow has poor contrast
		break;
		case DataPointDefinition.RANGE_NORMAL:  linkcolor = "green;";
		break;
		case DataPointDefinition.RANGE_INDETERMINATE:  linkcolor = "blue;";
		break;
		default: linkcolor = "black;";
		break;
		}
		return linkcolor;
	}

	/**
	 * translates  returned values of DataPointDefinition.getLeftTrendArrow(); and .getRightTrendArrow()
	 * to html arrow character equivalents
	 * DataPointDefinition.TREND_LEVEL : arrow left and right (&#8596; or &harr;)
	 * DataPointDefinition.TREND_UP    : arrow up (&#8593; or &uarr;)
	 * DataPointDefinition.TREND_DOWN  : arrow down (&#8595; or &darr;)
	 * 
	 */
	public String trendArrowStr(String trendArrow){

		if (DataPointDefinition.TREND_LEVEL.equals(trendArrow)) return "&harr;";
		if (DataPointDefinition.TREND_UP.equals(trendArrow)) return "&uarr;";
		if (DataPointDefinition.TREND_DOWN.equals(trendArrow)) return  "&darr;";

		return ""; // null or all other values
	}

	/**
	 * Creates and populates a properly HTML formatted label string from a given data point definition.
	 * returns an empty string if data point definition is null
	 * @return HTML formatted string for a label
	 */
	public String labelStrFromDpd(DataPointDefinition localdpd){

		if (localdpd==null) return "";
		
		StringBuffer labelStr= new StringBuffer();
		
		// static text cell string is used in cell if StaticTextCell boolean is true
		if (localdpd.getStaticTextCell()==true) {
			// link opens in new window or tab
			labelStr.append("<div class=\"pmatrix.statictextcell\" style=\"vertical-align: middle; text-align: center; color:black; text-decoration:none; \">");
			if(linkUrlEnabled){
				String linkUrl= (localdpd.getGraphURL()!=null) ? localdpd.getGraphURL() : "" ;
				labelStr.append("<a href=\"").append(linkUrl).append("\" target=\"_blank\" >") ;
			} else {
				// href with relative link to this page if url not enabled
				labelStr.append("<a href=\""+"\" >");
			}
			labelStr.append(localdpd.getStaticTextCellString()+"</div></a>");
			return labelStr.toString();
		}

		// if not static text cell then populate cell normally
		
		DecimalFormat pmatrixDformat = new DecimalFormat("####.##");

		//set primary number if enabled
		String valStr="";
		if(latestDataValueEnabled){
			valStr = (localdpd.getLatestDataValue() == null) ? "no&nbsp;data" // &nbsp; = no break space keep words together
					: pmatrixDformat.format(localdpd.getLatestDataValue());
		}

		//Note using Label instead of Link because Link proved very difficult to style

		String leftTrendArrow = leftArrowEnabled ? trendArrowStr(localdpd.getLeftTrendArrow()): "" ;

		String latestDataValueColor = linkColorForRange(localdpd.getLatestDataValueRange());

		// line below top number used to underline top number if there is a secondary value
		String topUnderline	=" border-bottom: thin solid gray;"; 
		// line above lower number used to underline top number if there is a secondary value
		String bottomTopline	=" border-top: thin solid gray;"; 

		// if only latest data value or only secondaryNumber then do not draw line between upper and lower numbers
		if (!secondaryNumberEnabled || localdpd.getSecondaryValue()==null 
				|| (!latestDataValueEnabled && secondaryNumberEnabled)) {
			topUnderline="";
			bottomTopline="";
		}

		// formats cell as numerator / denominator with left and right arrows
		// see example http://www.periodni.com/mathematical_and_chemical_equations_on_web.html
		// link opens in new window or tab
		if(linkUrlEnabled){
			String linkUrl= (localdpd.getGraphURL()!=null) ? localdpd.getGraphURL() : "" ;
			labelStr.append("<a href=\"").append(linkUrl).append("\" target=\"_blank\" >") ;
		} else {
			// href with relative link to this page if url not enabled
			labelStr.append("<a href=\""+"\" >");
		}

		labelStr.append("<div class=\"pmatrix.cell\" style=\"display: inline-block; vertical-align: middle; white-space:nowrap; text-decoration:none; \">")
		.append("<span class=\"pmatrix.leftarrow\" style=\"vertical-align: middle; text-align:left; color:gray;  font-size:200%\">")
		.append(leftTrendArrow)
		.append("</span>")
		.append("<div class=\"pmatrix.fraction\" style=\"")
		.append(" display: inline-block;")
		.append(" position: relative;")
		.append(" vertical-align: middle;")
		.append(" letter-spacing: 0.01em; ")
		.append(" text-align: center;")
		.append(" color:black;")
		.append("\">")
		.append("<span class=\"pmatrix.fup\" style= \"")
		.append(" display: block;")
		.append(" padding: 0.2em; ")
		.append(topUnderline)
		.append(" color:"+latestDataValueColor)
		.append(" \">")
		.append(valStr)
		.append("</span>");

		// if there is no secondary value defined (i.e. null ) do not print denominator line and secondary value 
		if(secondaryNumberEnabled && localdpd.getSecondaryValue()!=null) {
			String secondValStr = pmatrixDformat.format(localdpd.getSecondaryValue());
			String secondaryValueColor= linkColorForRange(localdpd.getSecondaryValueRange());
			labelStr.append("<span class=\"pmatrix.bar\" style=\"display: none;\">/</span>")
			.append("<span class=\"pmatrix.fdn\" style=\"")
			.append(" display: block;")
			.append(" padding: 0.2em; ")
			.append(bottomTopline)
			.append(" color:"+secondaryValueColor+"\">")
			.append(secondValStr)
			.append("</span>");
		}

		String rightTrendArrow = rightArrowEnabled ? trendArrowStr(localdpd.getRightTrendArrow()): "" ;
		labelStr.append("</div>")
		.append("<span class=\" matrix.rightarrow\" style=\"vertical-align: middle; color:gray; text-align: right; font-size:200%\">")
		.append(rightTrendArrow)
		.append("</span>")
		.append("</div>")
		.append("</a>");

		return labelStr.toString();
	}


	/**
	 * Creates a HTML encoded string for use as a key to describe the cells in a pmatrix table.
	 * THese values are set from the specification
	 *    latestDataValueDescription  description of latest data value. if null getPmatrixCellKeyText returns an empty string ""
	 *    secondaryNumberDescription  description of secondary number. If null, no secondary number is generated in key
	 *    leftRangeArrowDescription description of left range arrow. If null, no arrow is generated in key
	 *    rightRangeArrowDescription  description of right range arrow. If null, no arrow is generated in key
	 *    descriptiveText general descriptive text. If null, no descriptive text is generated
	 * @return HTML encoded table of key for the cells
	 */
	@Override
	public String getPmatrixDescription(){
		//TODO COULD BE COMBINED WITH labelStrFromDpd()
		String topUnderline	=" border-bottom: thin solid black;"; // used to underline top number if there is a secondary value
		if (secondaryNumberDescription==null) topUnderline="";

		DecimalFormat dformat = new DecimalFormat("####.##");


		String leftArrow="";
		String rightArrow="";
		if (leftRangeArrowDescription!=null) leftArrow= trendArrowStr(DataPointDefinition.TREND_UP);
		if (rightRangeArrowDescription!=null) rightArrow= trendArrowStr(DataPointDefinition.TREND_DOWN);
		// formats cell as numerator / denominator with left and right arrows
		// see example http://www.periodni.com/mathematical_and_chemical_equations_on_web.html

		String labelStr=""
				+ "<div class=\"pmatrix.cell\" style=\"display: inline-block; vertical-align: middle; white-space:nowrap; text-decoration:none; \">"
				+ "<span class=\"pmatrix.leftarrow\" style=\"vertical-align: middle; color:gray; font-size:200%\">"
				+ leftArrow
				+ "</span>"
				+ "<div class=\"pmatrix.fraction\" style=\""
				+ " display: inline-block;"
				+ " position: relative;"
				+ " vertical-align: middle;"
				+ " letter-spacing: 0.01em; "
				+ " text-align: center;"
				+ " color:black;"
				+ "\">"
				+ "<span class=\"pmatrix.fup\" style= \""
				+ " display: block;"
				+ " padding: 0.2em; "
				+ topUnderline
				+ " color:"+linkColorForRange(DataPointDefinition.RANGE_MAJOR)
				+ " \">"
				+ dformat.format(12345.12345)
				+ "</span>";

		// if there is no secondary value defined (i.e. null ) do not print denominator line and secondary value 
		if(secondaryNumberDescription!=null) {
			String secondValStr = dformat.format(5678.5678);
			labelStr = labelStr
					+ "<span class=\"pmatrix.bar\" style=\"display: none;\">/</span>"
					+ "<span class=\"pmatrix.fdn\" style=\"border-top: thin solid black; padding: 0.2em; "
					+ " color:"+linkColorForRange(DataPointDefinition.RANGE_NORMAL)+"\">"
					+ secondValStr
					+ "</span>";
		}
		labelStr = labelStr
				+ "</div>"
				+ "<span class=\"pmatrix.rightarrow\" style=\"vertical-align: middle; color:gray; font-size:200%\">"
				+ rightArrow
				+ "</span>"
				+ "</div>"
				+ "";

		String secondarystr = (secondaryNumberDescription==null) ? "" : secondaryNumberDescription;
		String leftrangestr =(leftRangeArrowDescription==null) ? "" : leftRangeArrowDescription;
		String rightrangestr = (rightRangeArrowDescription==null) ? "" : rightRangeArrowDescription;



		String keyStr="";
		if (descriptiveText!=null) keyStr=keyStr+"<table border=0 style=\"width:500px;\">"
				+ "<tr><td>Table Description:</td></tr>"
				+ "<tr><td>"+descriptiveText+"</td></tr>"
				+ "</table>"
				+ "";

		String cellDescriptionStr="";
		if(latestDataValueDescription!=null) cellDescriptionStr=cellDescriptionStr+ "<table border=0 style=\"width:500px;\">"
				+ "<tr><td colspan=\"3\">Key to cells:</td></tr>"
				+ "<tr><td></td><td>"+latestDataValueDescription+"</td><td></td></tr>"
				+ "<tr><td style=\"\">"+leftrangestr+"</td><td style=\"\">"+labelStr+"</td><td style=\"\">"+rightrangestr+"</td></tr>"
				+ "<tr><td></td><td>"+secondarystr+"</td><td></td></tr>"
				+ "</table>"
				+ "";

		// generate a colorDescriptionString if not defined in configuration
		if(colorDescriptionStr==null) colorDescriptionStr="<table  border=0 >"
				+ "<tr><td colspan=\"2\">Number Color indicates the following Severities:</td></tr>"
				+ "<tr style=\"padding-bottom: 0.01em;padding-top: 0.01em;\"><td style=\"color:"+linkColorForRange(DataPointDefinition.RANGE_CRITICAL)+"\">Critical</td><td></td></tr>"
				+ "<tr style=\"padding-bottom: 0.01em;padding-top: 0.01em;\"><td style=\"color:"+linkColorForRange(DataPointDefinition.RANGE_MAJOR)+"\">Major</td><td></td></tr>"
				+ "<tr style=\"padding-bottom: 0.01em;padding-top: 0.01em;\"><td style=\"color:"+linkColorForRange(DataPointDefinition.RANGE_MINOR)+"\">Minor</td><td></td></tr>"
				+ "<tr style=\"padding-bottom: 0.01em;padding-top: 0.01em;\"><td style=\"color:"+linkColorForRange(DataPointDefinition.RANGE_WARNING)+"\">Warning</td><td></td></tr>"
				+ "<tr style=\"padding-bottom: 0.01em;padding-top: 0.01em;\"><td style=\"color:"+linkColorForRange(DataPointDefinition.RANGE_NORMAL)+"\">Normal</td><td></td></tr>"
				+ "<tr style=\"padding-bottom: 0.01em;padding-top: 0.01em;\"><td style=\"color:"+linkColorForRange(DataPointDefinition.RANGE_INDETERMINATE)+"\">Indeterminate (Refresh is overdue)</td><td></td></tr>"
				+ "</table>"
				+ "";

		keyStr=keyStr+ "<table  border=0 >"
				+ "<tr><td>"+cellDescriptionStr+"</td><td>"+colorDescriptionStr+"</td></tr>"
				+ "</table>"
				+ "";

		return keyStr;

	}

}



