package org.opennms.features.vaadin.pmatrix.pax;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContextActivatorImpl implements SpringContextActivator {
	private static final Logger LOG = LoggerFactory.getLogger(SpringContextActivatorImpl.class);

	private AbstractApplicationContext appContext=null;

	private String contextPath="";


	public SpringContextActivatorImpl(String contextPath){
		this.contextPath=contextPath;
	}


	/**
	 * Extending ClassPathXmlApplicationContext to provide the bundle's classloader
	 * as it will use the thread context classloader otherwise.
	 * See http://stackoverflow.com/questions/8039931/how-do-i-use-a-spring-bean-inside-an-osgi-bundle
	 * See also http://stackoverflow.com/questions/5660115/loading-spring-context-with-specific-classloader
	 */
	@Override
	public ApplicationContext getContext() {
		if (appContext!=null) return appContext;
		synchronized (this) { 
			if (appContext==null) try{

				final ClassLoader properClassLoader = SpringContextActivatorImpl.class.getClassLoader();
				//final ClassLoader properClassLoader = this.getClass().getClassLoader();

				if(LOG.isDebugEnabled()) LOG.debug("initialising pmatrix bundle spring app context from contextPath:"+contextPath);
				appContext = new ClassPathXmlApplicationContext(contextPath) {
					protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader)  {
						super.initBeanDefinitionReader(reader);
						reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);
						//reader.setBeanClassLoader(getClassLoader());
						reader.setBeanClassLoader(properClassLoader);
						setClassLoader(properClassLoader);
					}
				};
				//appContext = new ClassPathXmlApplicationContext(contextPath);
			}
			catch (Exception e){
				LOG.error("unable to initialise the pmatrix bundle spring app context.",e);
			}		
		}

		return appContext;
	}
	
	/**
	 * blueprint.xml init-method
	 */
	public void init(){
		getContext();
	}
	
	/**
	 * blueprint.xml destroy-method
	 */
	public void destroy(){
		if (appContext!=null) appContext.close();
		LOG.debug("pmatrix application context closed by bundle blueprint destroy event");
	}

}

