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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * PmatrixDpdCalculatorRepository is used to persist a dataPointMap of historic calculations
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name="PmatrixDpdCalculatorRepository", propOrder={"datePersisted","dataPointMap"})
public class PmatrixDpdCalculatorRepository implements ResourceLoaderAware{
	private static final Logger LOG = LoggerFactory.getLogger(PmatrixDpdCalculatorRepository.class);

	static final String dateFormatString="yyyymmddhhmmss"; 

	/**
	 *  used by Spring ResourceLoaderAware interface
	 */
	private ResourceLoader resourceLoader=null;

	/**
	 * Maximum number of old archived files to keep before the oldest one is deleted
	 */
	private int archiveFileMaxNumber=1;

	/**
	 * directory where the archive files are stored
	 */
	private String archiveFileDirectoryLocation=null;

	/**
	 * name of the current archive file. 
	 * Note that the older archive files have a date appended to this name
	 */
	private String archiveFileName=null;

	/**
	 * if false no data history will be used
	 * if true historic date will be loaded on startup from pmatrix.archive.fileName and saved by running application
	 * to pmatrix.archive.fileName. Each time a new <archiveFileName>, is saved, the old file is renamed <archiveFileName><Date>
	 */
	private boolean persistHistoricData=true;

	/**
	 * set to true if repository has been successfully loaded from persistence file
	 */
	private boolean repositoryLoaded=false;

	private Map <String,PmatrixDpdCalculator> dataPointMap = new HashMap<String, PmatrixDpdCalculator>();

	private Date datePersisted;

	/**
	 * used by Spring ResourceLoaderAware interface to inject resource loader into bean
	 */
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	/**
	 * @return the archiveFileLocation
	 */
	public String getArchiveFileName() {
		return archiveFileName;
	}

	/**
	 * Name of the file to use to load and save an archive
	 * @param archiveFileName the archiveFileLocation to set
	 */
	public void setArchiveFileName(String archiveFileName) {
		this.archiveFileName = archiveFileName;
	}

	/**
	 * gets the location of the directory where the archive files are stored
	 * @return the archiveFileDirectoryLocation
	 */
	public String getArchiveFileDirectoryLocation() {
		return archiveFileDirectoryLocation;
	}

	/**
	 * Sets the location of the directory where the archive files are stored
	 * @param archiveFileDirectoryLocation the archiveFileDirectoryLocation to set
	 */
	public void setArchiveFileDirectoryLocation(
			String archiveFileDirectoryLocation) {
		this.archiveFileDirectoryLocation = archiveFileDirectoryLocation;
	}

	/**
	 * @return the archiveFileMaxNumber
	 */
	public int getArchiveFileMaxNumber() {
		return archiveFileMaxNumber;
	}

	/**
	 * @param archiveFileMaxNumber the archiveFileMaxNumber to set
	 */
	public void setArchiveFileMaxNumber(int archiveFileMaxNumber) {
		this.archiveFileMaxNumber = archiveFileMaxNumber;
	}

	/**
	 * @return the persistHistoricData
	 */
	public boolean isPersistHistoricData() {
		return persistHistoricData;
	}

	/**
	 * @param persistHistoricData the persistHistoricData to set
	 */
	public void setPersistHistoricData(boolean persistHistoricData) {
		this.persistHistoricData = persistHistoricData;
	}

	/**
	 * Returns true if the repository loaded the archive file successfully
	 * @return the repositoryLoaded
	 */
	public boolean getRepositoryLoaded() {
		return repositoryLoaded;
	}

	/**
	 * @return the datePersisted
	 */
	@XmlElement
	public Date getDatePersisted() {
		return datePersisted;
	}

	/**
	 * @param datePersisted the datePersisted to set
	 */
	public void setDatePersisted(Date datePersisted) {
		this.datePersisted = datePersisted;
	}


	@XmlElement()
	public Map<String, PmatrixDpdCalculator> getDataPointMap() {
		return dataPointMap;
	}

	public void setDataPointMap(
			Map<String, PmatrixDpdCalculator> dataPointMap) {
		this.dataPointMap = dataPointMap;
	}


	/**
	 * Causes the dataPointMap to be persisted to a file
	 * @param file file definition to persist the data set to
	 * @return true if dataPointMap persisted correctly, false if not
	 */
	public boolean persist(){
		File currentArchiveFile=null;
		File tmpCurrentArchiveFile=null;
		Resource tmpResource=null;

		if (!persistHistoricData){
			LOG.debug("not persisting data as persistHistoricData=false");
			return false;
		}

		if (archiveFileName==null || archiveFileDirectoryLocation==null) {
			LOG.error("cannot save historical data to file as incorrect file location:"
					+ " archiveFileDirectoryLocation="+archiveFileDirectoryLocation
					+ " archiveFileName="+archiveFileName );
			return false;
		}

		// set the date on which this file was persisted
		datePersisted = new Date();

		// used to get file name suffix
		SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormatString);

		// persist data to temporary file <archiveFileName.tmp>
		String tmpArchiveFileName=archiveFileName+".tmp";
		String tmpArchiveFileLocation=archiveFileDirectoryLocation+File.separator+tmpArchiveFileName;
		LOG.debug("historical data will be written to temporary file :"+tmpArchiveFileLocation);

		try {
			tmpResource = resourceLoader.getResource(tmpArchiveFileLocation);
			tmpCurrentArchiveFile = new File(tmpResource.getURL().getFile());
		} catch (IOException e) {
			LOG.error("cannot save historical data to file at archiveFileLocation='"+tmpArchiveFileLocation+"' due to error:",e);
			return false;
		}				

		LOG.debug("persisting historical data to temporary file location:" + tmpCurrentArchiveFile.getAbsolutePath());

		// marshal the data
		PrintWriter writer=null;
		boolean marshalledCorrectly=false;
		try {
			// create  directory if doesn't exist
			File directory = new File(tmpCurrentArchiveFile.getParentFile().getAbsolutePath());
			directory.mkdirs();
			// create file if doesn't exist
			writer = new PrintWriter(tmpCurrentArchiveFile, "UTF-8");
			writer.close();

			// see http://stackoverflow.com/questions/1043109/why-cant-jaxb-find-my-jaxb-index-when-running-inside-apache-felix
			// need to provide bundles class loader
			ClassLoader cl = org.opennms.features.vaadin.pmatrix.model.DataPointDefinition.class.getClassLoader();
			JAXBContext jaxbContext = JAXBContext.newInstance("org.opennms.features.vaadin.pmatrix.model:org.opennms.features.vaadin.pmatrix.calculator", cl);

			//JAXBContext jaxbContext = JAXBContext.newInstance("org.opennms.features.vaadin.pmatrix.model:org.opennms.features.vaadin.pmatrix.calculator");

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			// TODO CHANGE output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// marshal this Data Repository
			jaxbMarshaller.marshal(this, tmpCurrentArchiveFile);

			marshalledCorrectly=true;
		} catch (JAXBException e) {
			LOG.error("problem marshalling file: ", e);
		}  catch (Exception e) {
			LOG.error("problem marshalling file: ", e);
		} finally {
			if (writer!=null) writer.close();
		}
		if (marshalledCorrectly==false) return false;

		// marshaling succeeded so rename tmp file
		String archiveFileLocation=archiveFileDirectoryLocation+File.separator+archiveFileName;
		LOG.info("historical data will be written to:"+archiveFileLocation);

		Resource resource = resourceLoader.getResource(archiveFileLocation);

		if(resource.exists()){
			String oldArchiveFileName=archiveFileName+"."+dateFormatter.format(datePersisted);
			String oldArchiveFileLocation=archiveFileDirectoryLocation+File.separator+oldArchiveFileName;
			LOG.info("previous historical file at archiveFileLocation='"+archiveFileLocation+"' exists so being renamed to "+oldArchiveFileLocation);
			Resource oldresource = resourceLoader.getResource(oldArchiveFileLocation);
			try {
				currentArchiveFile = new File(resource.getURL().getFile());
				File oldArchiveFile = new File(oldresource.getURL().getFile());
				// rename current archive file to old archive file name
				if (!currentArchiveFile.renameTo(oldArchiveFile))        {
					throw new IOException("cannot rename current archive file:"+currentArchiveFile.getAbsolutePath()+" to "+oldArchiveFile.getAbsolutePath());
				}
				// rename temporary archive file to current archive file name
				if (!tmpCurrentArchiveFile.renameTo(currentArchiveFile))        {
					throw new IOException("cannot rename temporary current archive file:"+tmpCurrentArchiveFile.getAbsolutePath()+" to "+currentArchiveFile.getAbsolutePath());
				}
			} catch (IOException e) {
				LOG.error("Problem archiving old persistance file",e);
			}
			// remove excess files
			try {
				Resource directoryResource =resourceLoader.getResource(archiveFileDirectoryLocation);
				File archiveFolder = new File(directoryResource.getURL().getFile());
				File[] listOfFiles = archiveFolder.listFiles(); 

				String filename;
				//this will sort earliest to latest date
				TreeMap<Date,File>  sortedFiles = new TreeMap<Date, File>();

				for (int i = 0; i < listOfFiles.length; i++) {
					if (listOfFiles[i].isFile())  {
						filename = listOfFiles[i].getName();
						if ((!filename.equals(archiveFileName))
								&& (!filename.equals(tmpArchiveFileName)) 
								&& (filename.startsWith(archiveFileName))){
							String beforeTimeString=archiveFileName+".";
							String timeSuffix=filename.substring(beforeTimeString.length());
							if (!"".equals(timeSuffix)){
								Date fileCreatedate=null;
								try {
									fileCreatedate = dateFormatter.parse(timeSuffix);
								} catch (ParseException e) {
									LOG.debug("cant parse file name suffix to time for filename:"+filename, e);
								} 
								if (fileCreatedate!=null){
									sortedFiles.put(fileCreatedate, listOfFiles[i]);
								}
							}
						}
					}
				}

				while (sortedFiles.size() > archiveFileMaxNumber){
					File removeFile = sortedFiles.remove(sortedFiles.firstKey());
					LOG.debug("deleting archive file:'"+removeFile.getName()+"' so that number of archive files <="+archiveFileMaxNumber);
					removeFile.delete();
				}
				for (File archivedFile: sortedFiles.values()){
					LOG.debug("not deleting archive file:'"+archivedFile.getName()+"' so that number of archive files <="+archiveFileMaxNumber);
				}

				return true;
			} catch (IOException e) {
				LOG.error("Problem removing old persistance files",e);
			}
		} else {
			// if resource doesn't exist just rename the new tmp file to the archive file name
			try {
				currentArchiveFile = new File(resource.getURL().getFile());
				// rename temporary archive file to current archive file name
				if (!tmpCurrentArchiveFile.renameTo(currentArchiveFile))        {
					throw new IOException("cannot rename temporary current archive file:"+tmpCurrentArchiveFile.getAbsolutePath()+" to "+currentArchiveFile.getAbsolutePath());
				}
				return true;
			} catch (IOException e) {
				LOG.error("cannot rename temporary current archive ",e);
			}
		}

		return false;

	}


	/**
	 * Causes the historical dataPointMap to load from a file determined by archiveFileLocation
	 * @param archiveFile file definition to persist the data set to
	 * @return true if dataPointMap loaded correctly, false if not
	 */
	@PostConstruct
	public boolean load(){

		repositoryLoaded=false;

		if (!persistHistoricData){
			LOG.info("not loading persisted data as persistHistoricData=false");
			return false;
		}

		if (archiveFileName==null || archiveFileDirectoryLocation==null) {
			LOG.error("not using historical data from file as incorrect file location:"
					+ " archiveFileDirectoryLocation="+archiveFileDirectoryLocation
					+ " archiveFileName="+archiveFileName );
			return false;
		}

		String archiveFileLocation=archiveFileDirectoryLocation+File.separator+archiveFileName;

		File archiveFile=null;

		try {
			Resource resource = resourceLoader.getResource(archiveFileLocation);
			if(!resource.exists()){
				LOG.warn("cannot load historical data as file at archiveFileLocation='"+archiveFileLocation+"' does not exist");
				return false;
			}

			archiveFile = new File(resource.getURL().getFile());
		} catch (IOException e) {
			LOG.error("cannot load historical data from file at archiveFileLocation='"+archiveFileLocation+"' due to error:",e);
			return false;
		} catch (Exception e) {
			LOG.error("cannot load historical data from file at archiveFileLocation='"+archiveFileLocation+"' due to error:",e);
			return false;
		}			

		try {

			//TODO CHANGE TO PACKAGE
			//			JAXBContext jaxbContext = JAXBContext.newInstance(
			//					PmatrixDpdCalculatorImpl.class,
			//					PmatrixDpdCalculatorEmaImpl.class,
			//					PmatrixDpdCalculatorRepository.class,
			//					org.opennms.features.vaadin.pmatrix.model.NameValuePair.class);
			
			// see http://stackoverflow.com/questions/1043109/why-cant-jaxb-find-my-jaxb-index-when-running-inside-apache-felix
			// need to provide bundles class loader
			ClassLoader cl = org.opennms.features.vaadin.pmatrix.model.DataPointDefinition.class.getClassLoader();
			JAXBContext jaxbContext = JAXBContext.newInstance("org.opennms.features.vaadin.pmatrix.model:org.opennms.features.vaadin.pmatrix.calculator", cl);

			//JAXBContext jaxbContext = JAXBContext.newInstance("org.opennms.features.vaadin.pmatrix.model:org.opennms.features.vaadin.pmatrix.calculator");


			Unmarshaller jaxbUnMarshaller = jaxbContext.createUnmarshaller();

			Object unmarshalledObject = jaxbUnMarshaller.unmarshal( archiveFile  );

			if (unmarshalledObject instanceof PmatrixDpdCalculatorRepository ){
				PmatrixDpdCalculatorRepository pdcr= (PmatrixDpdCalculatorRepository) unmarshalledObject;
				dataPointMap = pdcr.getDataPointMap();

				LOG.info("successfully unmarshalled historical pmatrix data from file location:" + archiveFile.getAbsolutePath());
				repositoryLoaded=true;
				return true;
			} else {
				LOG.error("cant unmarshal received object:"+unmarshalledObject);
			}

		} catch (JAXBException e) {
			LOG.error("problem unmarshalling file: "+archiveFile.getAbsolutePath(), e);
		}  catch (Exception e) {
			LOG.error("problem unmarshalling file: "+archiveFile.getAbsolutePath(), e);
		}
		return false;

	}
}
