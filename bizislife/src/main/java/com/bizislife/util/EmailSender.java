package com.bizislife.util;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;

public class EmailSender {

    public synchronized void sendEmailFromGmail(String from, String to, String subject, String content, Properties props, final String username, final String password){

        Authenticator auth = new Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(username, password);
            }
        };
        Session session = Session.getInstance(props, auth);
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            InternetAddress[] address = { new InternetAddress(to) };
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
//            msg.setContent("This is a test <b>HOWTO<b>", "text/html; charset=ISO-8859-1");
//            msg.setText(content);
            msg.setContent(content, "text/html; charset=UTF-8");
            Transport.send(msg);
        } catch (MessagingException mex){
            mex.printStackTrace();
        }

    }
	
    public synchronized void sendEmail(String toEmail, String subjectStr, String bodyStr) throws UnsupportedEncodingException, MessagingException
    {
        Properties props = new Properties();
        // ask IT People for company mail server
        String mailHost = "Your Company Email Server";
        String mailSender = "Your Office Mail ID";
        String malSenderName = "Your Email";
        props.put("mail.host", mailHost);
        props.put("mail.user", mailSender);
        Session mailSession = Session.getDefaultInstance(props, null);
        MimeMessage msg = new MimeMessage(mailSession);
        Multipart multipart = new MimeMultipart();
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(bodyStr, "text/html");
        multipart.addBodyPart(messageBodyPart);
        msg.setFrom(new InternetAddress(mailSender, malSenderName));
        msg.addRecipient(Message.RecipientType.TO,
        new InternetAddress(toEmail));
        msg.setSubject(subjectStr);
        msg.setContent(multipart);
        Transport.send(msg);
    }

    public static void setEmailProps(Map emailPropsMap, Properties p){
    	if(emailPropsMap!=null && emailPropsMap.size()>0 && p!=null){
            p.put("mail.smtp.host", emailPropsMap.get("gmail.smtp.host"));
            p.put("mail.smtp.auth", emailPropsMap.get("gmail.smtp.auth"));
            p.put("mail.smtp.starttls.enable", emailPropsMap.get("gmail.smtp.starttls.enable"));
            p.put("mail.smtp.socketFactory.port", emailPropsMap.get("gmail.smtp.socketFactory.port"));
            p.put("mail.smtp.socketFactory.class", emailPropsMap.get("gmail.smtp.socketFactory.class"));
            p.put("mail.smtp.socketFactory.fallback", emailPropsMap.get("gmail.smtp.socketFactory.fallback"));
    	}
    }
    
    public static String setEmailContent(String emailContent, Object[] args){
    	if(StringUtils.isNotBlank(emailContent) && args!=null && args.length>0){
    		return  MessageFormat.format(emailContent, args);
    	}
    	
    	return emailContent;
    }
    
	
}
