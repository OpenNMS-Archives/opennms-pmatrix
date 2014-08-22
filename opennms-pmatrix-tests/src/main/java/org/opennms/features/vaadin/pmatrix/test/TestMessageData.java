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

package org.opennms.features.vaadin.pmatrix.test;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestMessageData {
	private String path;
	private String owner;
	private Long timestamp; 
	private ArrayList<Double> values;
    
	public String getPath() {
		return path;
	}
	
	@XmlElement
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getOwner() {
		return owner;
	}
	
	@XmlElement
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}
	
	@XmlElement
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	

	public ArrayList<Double> getValues() {
		return values;
	}
	
	@XmlElement
	public void setValues(ArrayList<Double> values) {
		this.values = values;
	}

    
}
