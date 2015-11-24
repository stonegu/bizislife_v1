package com.bizislife.util;

import java.util.*;
import javax.naming.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;


public class SpringJNDIExporter implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(SpringJNDIExporter.class);

    private Map<String, Object> jndiMapping = null;

    @Override
    public void afterPropertiesSet() throws Exception {
    	logger.debug("Inserting JNDI entries");
        InitialContext ctx = new InitialContext();
        for(Map.Entry<String, Object> addToJndi: jndiMapping.entrySet()){
        	JNDIUtils.bind(ctx, addToJndi.getKey(), addToJndi.getValue());
        }
        NamingEnumeration<NameClassPair> list = ctx.list("");
        while (list.hasMore()) {
          logger.debug("Inserting: " + list.next().getName());
        }
    }

    public void setJndiMapping(Map<String, Object> jndiMapping) {
        this.jndiMapping = jndiMapping;
    }

}