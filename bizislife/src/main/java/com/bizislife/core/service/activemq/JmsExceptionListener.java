package com.bizislife.core.service.activemq;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class JmsExceptionListener implements ExceptionListener {
    private static final Logger logger = LoggerFactory.getLogger(JmsExceptionListener.class);
	
	private CachingConnectionFactory cachingConnectionFactory;
	
    public void onException( final JMSException e )
    {
        e.printStackTrace();
        
        if (logger.isDebugEnabled()){
			logger.debug("onException() is called!");
        }
		cachingConnectionFactory.onException(e);        
        
    }
    
    public CachingConnectionFactory getCachingConnectionFactory() {
		return cachingConnectionFactory;
	}

	public void setCachingConnectionFactory(CachingConnectionFactory cachingConnectionFactory) {
		this.cachingConnectionFactory = cachingConnectionFactory;
	}    
}