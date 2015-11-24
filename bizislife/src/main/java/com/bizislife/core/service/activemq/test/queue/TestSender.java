package com.bizislife.core.service.activemq.test.queue;

import javax.jms.*;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class TestSender {

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
	
	public void send(final String messageParam)
	{
		MessageCreator mc = new MessageCreator() {
			public Message createMessage(Session session) throws JMSException
			{
				MapMessage message = session.createMapMessage();
				message.setString("msg",messageParam);
				return message;
			}
		};
		
		jmsTemplate.send(destination, mc);
		
	}
	
}
