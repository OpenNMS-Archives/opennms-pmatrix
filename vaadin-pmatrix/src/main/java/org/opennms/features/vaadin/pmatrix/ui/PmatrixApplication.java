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

package org.opennms.features.vaadin.pmatrix.ui;

import java.text.DecimalFormat;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Theme("pmatrixtheme")
@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
//@PreserveOnRefresh // sets UI session scoped
public class PmatrixApplication extends UI {
	private static final Logger LOG = LoggerFactory.getLogger(PmatrixApplication.class);

	private UiComponentFactory uiComponentFactory = null;

	// private PmatrixTable pmatrixTable = null;

	/**
	 * @return the uiComponentFactory
	 */
	public UiComponentFactory getUiComponentFactory() {
		return uiComponentFactory;
	}

	/**
	 * @param uiComponentFactory
	 *            the uiComponentFactory to set
	 */
	@Autowired
	public void setUiComponentFactory(UiComponentFactory uiComponentFactory) {
		this.uiComponentFactory = uiComponentFactory;
	}

	// public PmatrixTable getPmatrixTable() {
	// return pmatrixTable;
	// }
	//
	// @Autowired
	// public void setPmatrixTable(PmatrixTable pmatrixTable) {
	// this.pmatrixTable = pmatrixTable;
	// }

	public static class Servlet extends VaadinServlet {
	}

	//TODO https://vaadin.com/book/vaadin7/-/page/application.lifecycle.html

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setWidth("-1px");
		layout.setHeight("-1px");
		layout.setDefaultComponentAlignment(Alignment.TOP_LEFT);
		layout.setMargin(true);
		setContent(layout);

		//used to test that detach events are happening
		addDetachListener(new DetachListener() {
			@Override
			public void detach(DetachEvent event) {
				LOG.debug("Pmatrix UI instance detached:"+this);
			}
		});

		Component uiComponent = uiComponentFactory.getUiComponent(request);

		if (uiComponent == null) {

			StringBuilder sb = new StringBuilder(
					"Error: Cannot create the UI because the URL request parameters are not recognised<BR>\n"
							+ "you need to provide atleast '?"+UiComponentFactory.COMPONENT_REQUEST_PARAMETER+"="
							+UiComponentFactory.DEFAULT_COMPONENT_REQUEST_VALUE+"'<BR>\n"
							+ "Parameters passed in URL:<BR>\n");
			for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
				sb.append("parameter:'" + entry.getKey() + "' value:'");
				for (String s: entry.getValue()){
					sb.append("{"+s+"}");
				}
				sb.append("'<BR>\n");
			}
			Label label = new Label();
			label.setWidth("600px");
			label.setContentMode(ContentMode.HTML);
			label.setValue(sb.toString());
			layout.addComponent(label);

		} else {
			layout.addComponent(uiComponent);

			// refresh interval to apply to the UI
			int pollInterval=uiComponentFactory.getRefreshRate();
			setPollInterval(pollInterval);

			// display poll interval in seconds
			DecimalFormat dformat = new DecimalFormat("##.##");
			Label label = new Label();
			label.setCaption("(refresh rate:"+dformat.format(pollInterval/1000)+" seconds)");
			layout.addComponent(label);
		}

	}
}
