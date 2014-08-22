package org.opennms.features.vaadin.pmatrix.pax;

import org.springframework.context.ApplicationContext;

public interface SpringContextActivator {

	/**
	 * @return the ctx
	 */
	public abstract ApplicationContext getContext();

}