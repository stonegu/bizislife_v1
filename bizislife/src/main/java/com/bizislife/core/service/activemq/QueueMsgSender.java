package com.bizislife.core.service.activemq;

import java.util.Enumeration;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class QueueMsgSender {

	// **** all names are used by MapMessage
	public static enum mapMsgName {
		emails,
		emailType,
		receiversId,
		
		;
	}
	
	// *** end ***
	
	private JmsTemplate jmsTemplate;

	public void setJmsTemplate(JmsTemplate value)
	{
		jmsTemplate = value;
	}
	
	public void send(Destination destination, final MapMessage msg) {
		
		MessageCreator mc = new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				MapMessage message = session.createMapMessage();
				
				for (Enumeration e = msg.getMapNames();e.hasMoreElements();) {
					  String name = e.nextElement().toString();
//					  System.out.println(name + "=" + msg.getObject(name));
					  message.setObject(name, msg.getObject(name));
				}				
				return message;
			}
		};
		
		jmsTemplate.send(destination, mc);
	}

}
