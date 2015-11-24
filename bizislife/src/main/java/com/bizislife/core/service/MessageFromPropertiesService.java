package com.bizislife.core.service;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Service;

/**
 * @author Stone Gu
 * 
 * This class is used to read properties by key, for example:
 * messageService.getMessageSource().getMessage("email.exist", new Object[] { email }, Locale.US)
 *
 */
@Service
public class MessageFromPropertiesService implements MessageSourceAware{
	private MessageSource messageSource;
	
	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

}
