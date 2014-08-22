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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class PartialXMLUnmarshaller<T> {
    XMLStreamReader reader;
    Class<T> clazz;
    Unmarshaller unmarshaller;

    /** modified from answer in http://stackoverflow.com/questions/1134189/can-jaxb-parse-large-xml-files-in-chunks 
     * Can JAXB parse large XML files in chunks
     * usage PartialUnmarshaller<YourClass>(stream, YourClass.class)
     * @param stream
     * @param clazz
     * @throws XMLStreamException
     * @throws JAXBException
     */
    public PartialXMLUnmarshaller(InputStream stream, Class<T> clazz) throws XMLStreamException, JAXBException {
        this.clazz = clazz;
        this.unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();
        this.reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);

        /* ignore headers */
        skipElements(javax.xml.stream.XMLStreamConstants.START_DOCUMENT, javax.xml.stream.XMLStreamConstants.DTD);
        /* ignore root element */
        reader.nextTag();
        /* if there's no tag, ignore root element's end */
        skipElements(javax.xml.stream.XMLStreamConstants.END_ELEMENT);
    }

    public T next() throws XMLStreamException, JAXBException {
        if (!hasNext()) throw new NoSuchElementException();

        T value = unmarshaller.unmarshal(reader, clazz).getValue();

        skipElements(javax.xml.stream.XMLStreamConstants.CHARACTERS, javax.xml.stream.XMLStreamConstants.END_ELEMENT);
        return value;
    }

    public boolean hasNext() throws XMLStreamException {
        return reader.hasNext();
    }

    public void close() throws XMLStreamException {
        reader.close();
    }

    void skipElements(int... elements) throws XMLStreamException {
        int eventType = reader.getEventType();
        
        List<Integer> types = new ArrayList<Integer>(elements.length);
        for (int value : elements) {
            types.add(value);
        }

        while (types.contains(eventType))
            eventType = reader.next();
    }
}