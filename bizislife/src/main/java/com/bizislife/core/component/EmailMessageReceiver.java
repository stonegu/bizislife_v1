package com.bizislife.core.component;

import java.util.*;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bizislife.core.service.AccountService;
import com.bizislife.core.service.activemq.QueueMsgSender;

import com.bizislife.core.configuration.ApplicationConfiguration;
import com.bizislife.core.hibernate.pojo.*;
import com.bizislife.util.EmailSender;
import com.bizislife.util.definition.EmailRelated;
import com.bizislife.util.definition.EmailRelated.EmailType;

@Component("emailMessageReceiver")
public class EmailMessageReceiver implements MessageListener{
	
    @Autowired
    private AccountService accountService;
    
    @Autowired
    protected ApplicationConfiguration applicationConfig;

    EmailSender emailSender = new EmailSender();


	@Override
	public void onMessage(Message msg) {
		if (msg instanceof ActiveMQMapMessage) {
            final ActiveMQMapMessage mapMessage = (ActiveMQMapMessage) msg;
            
            // do something
            try {
            	
            	// get new email type
            	String eType = (String)mapMessage.getString(QueueMsgSender.mapMsgName.emailType.name());
            	if(StringUtils.isNotBlank(eType)){
            		EmailRelated.EmailType emailType = EmailType.fromString(eType);
            		if(emailType!=null){
            			
    			    	// get email title and email content based on emailtype
    			    	EmailTemplate emailTemplate = accountService.getEmailTemplateByEmailtype(emailType.name());
    			    	// emailContentMap to hold content for each email
    			    	Map<String, String> emailContentMap = new HashMap<String, String>();
    			    	
    			    	if(emailTemplate!=null){
        			    	String emailContent_general = emailTemplate.getContent();
        			    	String emailContentArgs = emailTemplate.getArgs();
        			    	String[] argNames = emailContentArgs!=null?emailContentArgs.split(","):new String[0];

        			    	// get all receivers' email address
    			    		Map<String, Map<String, String>> emails = (Map<String, Map<String, String>>)mapMessage.getObject(QueueMsgSender.mapMsgName.emails.name());
                        	
                        	if(emails!=null && emails.size()>0){
                        		String emailContent = null;
                        		String[] args = null;
                        		for(Map.Entry<String, Map<String, String>> e : emails.entrySet()){
                					
                					Map<String, String> emailPropsMap = applicationConfig.getMailConfigInfoMap();
                			    	Properties emailProps = new Properties();
                			    	EmailSender.setEmailProps(emailPropsMap, emailProps);
                	                // replace emailContent's data place holder with real data:
                			    	args = new String[argNames.length];
                			    	// set args based on emailType, all args' name are in table emailtemplate's args. 
                			    	// note: args[]'s order should same as the order in table emailtemplate's args.
                			    	for(int i=0; i<argNames.length; i++){
                			    		args[i] = e.getValue().get(argNames[i]);
                			    	}
                			    	
                	                emailContent = EmailSender.setEmailContent(emailContent_general, args);
                					
                	                emailSender.sendEmailFromGmail(emailPropsMap.get("gmail.account"),
                	                        emailPropsMap.get("mail.test").trim().equals("true")?emailPropsMap.get("mail.test.receiver"):e.getKey().trim(),
                	                        emailTemplate.getTitle() + " on " + (new Date().toString()),
                	                        emailContent,
                	                        emailProps,
                	                        emailPropsMap.get("gmail.account.username"),
                	                        emailPropsMap.get("gmail.account.password"));
                        			
                        		}
                        	}
    			    		
    			    	}
            			
            		}
            		
            	}
            	
//				System.err.print(mapMessage.getString("msg"));
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
	}

}
