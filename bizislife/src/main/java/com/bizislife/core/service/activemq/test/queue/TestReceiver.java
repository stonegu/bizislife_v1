package com.bizislife.core.service.activemq.test.queue;

import javax.jms.*;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class TestReceiver {

	private JmsTemplate jmsTemplate;
	private Destination destination;
	
	public void setJmsTemplate(JmsTemplate value)
	{
		jmsTemplate = value;
	}
	
	public void setDestination(Destination value)
	{
		destination = value;
	}
	
	public Object receive()
	{
		Message m = jmsTemplate.receive(destination);
		System.out.println(m);
		return m;

	}
	
}
