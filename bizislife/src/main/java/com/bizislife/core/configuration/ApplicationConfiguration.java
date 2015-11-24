package com.bizislife.core.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.bizislife.util.EmailSender;
import com.bizislife.util.WebUtil;

@Configuration
public class ApplicationConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationConfiguration.class);

	/**
	 * Loaded config file.
	 */
	private Properties config;


	@Autowired 
	private ApplicationContext appContext;
	
	EmailSender emailSender = new EmailSender();

	@PostConstruct
	protected void init() {
		try {
			Resource res = appContext.getResource("/WEB-INF/classes/GlobalConfiguration.properties");
			if (res==null) throw new IllegalArgumentException("Cannot find configuration file: /WEB-INF/classes/GlobalConfiguration.properties");
			InputStream in = res.getInputStream();
			Reader r = new InputStreamReader(in,"UTF-8");
			Properties props = new Properties();
			props.load(r);
			r.close();

			config = props;
			
			if(serverIpDetect()){
				// send out a ip addresses (internal & external) when tomcat start.
				StringBuilder allips = new StringBuilder();
				// for local address
				InetAddress localIp = WebUtil.getLocalIp();
				if(localIp!=null){
					allips.append("<p/>Local ip:<br/>").append(localIp.getHostAddress()).append("<br/>");
				}
				// for external ip
				String externalIp = WebUtil.getExternalIp();
				if(externalIp!=null){
					allips.append("<p/>external ip: <br/>").append(externalIp).append("<br/>");
				}
		        System.out.println("============ ips ============");
		        System.out.println(allips.toString());
		        
		        // log also:
		        logger.info("Someone deploys and opens the bizislife application in IP: "+allips.toString());
				
				Map<String, String> emailPropsMap = getMailConfigInfoMap();
		    	Properties emailProps = new Properties();
		    	EmailSender.setEmailProps(emailPropsMap, emailProps);
		    	
	            emailSender.sendEmailFromGmail(emailPropsMap.get("gmail.account"),
	            		emailPropsMap.get("mail.test.receiver"),
	                    "Someone deploys and opens the bizislife application in IP:",
	                    allips.toString(),
	                    emailProps,
	                    emailPropsMap.get("gmail.account.username"),
	                    emailPropsMap.get("gmail.account.password"));
	            
			}
			
		} catch (java.io.IOException e) {
			throw new IllegalArgumentException("Cannot read GlobalConfiguration.propertires", e);
		}
	} // init

	// use this for testing - pass it the filename of the properties file
	public void initFromFile (File filename) {
		try {
			InputStream in =new FileInputStream(filename);
			Reader r = new InputStreamReader(in,"UTF-8");
			Properties props = new Properties();
			props.load(r);
			r.close();

			config = props;
		} catch (java.io.IOException e) {
			throw new IllegalArgumentException("Cannot read GlobalConfiguration.propertires", e);
		}
	}


    public Map<String, String> getMailConfigInfoMap(){
        Map<String, String> mailConfigInfoMap = new HashMap<String, String>();

        mailConfigInfoMap.put("gmail.smtp.host", config.getProperty("gmail.smtp.host"));
        mailConfigInfoMap.put("gmail.smtp.auth", config.getProperty("gmail.smtp.auth"));
        mailConfigInfoMap.put("gmail.smtp.starttls.enable", config.getProperty("gmail.smtp.starttls.enable"));
        mailConfigInfoMap.put("gmail.smtp.socketFactory.port", config.getProperty("gmail.smtp.socketFactory.port"));
        mailConfigInfoMap.put("gmail.smtp.socketFactory.class", config.getProperty("gmail.smtp.socketFactory.class"));
        mailConfigInfoMap.put("gmail.smtp.socketFactory.fallback", config.getProperty("gmail.smtp.socketFactory.fallback"));
        mailConfigInfoMap.put("gmail.account.username", config.getProperty("gmail.account.username"));
        mailConfigInfoMap.put("gmail.account.password", config.getProperty("gmail.account.password"));
        mailConfigInfoMap.put("gmail.account", config.getProperty("gmail.account"));
        mailConfigInfoMap.put("mail.test", config.getProperty("mail.test"));
        mailConfigInfoMap.put("mail.test.receiver", config.getProperty("mail.test.receiver"));

        return mailConfigInfoMap;
    }

    public String getHostName(){
    	return config.getProperty("hostname");
    }
    
    public String[] getBizislifeRelatedHostName(){
    	String hostnamesRelated = config.getProperty("bizislife.hostname.related");
    	if(StringUtils.isNotBlank(hostnamesRelated)){
    		return hostnamesRelated.split(",");
    	}
    	return null;
    }
    
    public Map<String, Integer> getPageDesignArea(){
    	Map<String, Integer> area = new HashMap<String, Integer>();
    	area.put("width", Integer.valueOf(config.getProperty("pb.area.width")));
    	area.put("height", Integer.valueOf(config.getProperty("pb.area.height")));
    	return area;
    }
    
    public Map<String, Integer> getContainerDefaultSize(){
    	Map<String, Integer> size = new HashMap<String, Integer>();
    	size.put("width", Integer.valueOf(config.getProperty("pb.container.width")));
    	size.put("height", Integer.valueOf(config.getProperty("pb.container.height")));
    	return size;
    }
    
    /**
     * @return total allow container level in pagebuilder
     */
    public int getTotalAllowedContainerLevel(){
    	String level = config.getProperty("pb.container.level.total");
    	if(StringUtils.isNotBlank(level) && NumberUtils.isNumber(level)){
    		return Integer.parseInt(level);
    	}
    	return 0;
    }
    
    public String getFileServerLocation(){
    	return config.getProperty("fileserver.location");
    }
    
    public long getUploadFileMaxSizeInKB(){
    	return Long.parseLong(config.getProperty("upload.file.maxsize"));
    }
    
    public String getBugReceiver(){
    	return config.getProperty("bug.receiver");
    }
    
    public String getApplicationReceiver(){
    	return config.getProperty("application.receiver");
    }
    
    public String getContactInfoReceiver(){
    	return config.getProperty("contactInfo.receiver");
    }
    
    
////    # three type of org can do: freemium type & silver type & gold type
////  # freemium type: 
//    public long getFreemiumPagesCanHave(){
//    	return Long.parseLong(config.getProperty("freemiumPagesCanHave").trim());
//    }
//    public long getFreemiumContainersCanHave(){
//    	return Long.parseLong(config.getProperty("freemiumContainersCanHave").trim());
//    }
//    public long getFreemiumModuleDetailsCanHave(){
//    	return Long.parseLong(config.getProperty("freemiumModuleDetailsCanHave").trim());
//    }
//    public long getFreemiumInstancesCanHave(){
//    	return Long.parseLong(config.getProperty("freemiumInstancesCanHave").trim());
//    }
//    public long getFreemiumProductsCanHave(){
//    	return Long.parseLong(config.getProperty("freemiumProductsCanHave").trim());
//    }
//    public long getFreemiumMaxCharsPerModuledetail(){
//    	return Long.parseLong(config.getProperty("freemiumMaxCharsPerModuledetail").trim());
//    }
//    public long getFreemiumMaxCharsPerInstance(){
//    	return Long.parseLong(config.getProperty("freemiumMaxCharsPerInstance").trim());
//    }
//    public long getFreemiumMaxCharsPerJsp(){
//    	return Long.parseLong(config.getProperty("freemiumMaxCharsPerJsp").trim());
//    }
//    public long getFreemiumMaxCharsPerCss(){
//    	return Long.parseLong(config.getProperty("freemiumMaxCharsPerCss").trim());
//    }
//
////  # silver type: 
//    public long getSilverPagesCanHave(){
//    	return Long.parseLong(config.getProperty("silverPagesCanHave").trim());
//    }
//    public long getSilverContainersCanHave(){
//    	return Long.parseLong(config.getProperty("silverContainersCanHave").trim());
//    }
//    public long getSilverModuleDetailsCanHave(){
//    	return Long.parseLong(config.getProperty("silverModuleDetailsCanHave").trim());
//    }
//    public long getSilverInstancesCanHave(){
//    	return Long.parseLong(config.getProperty("silverInstancesCanHave").trim());
//    }
//    public long getSilverProductsCanHave(){
//    	return Long.parseLong(config.getProperty("silverProductsCanHave").trim());
//    }
//    public long getSilverMaxCharsPerModuledetail(){
//    	return Long.parseLong(config.getProperty("silverMaxCharsPerModuledetail").trim());
//    }
//    public long getSilverMaxCharsPerInstance(){
//    	return Long.parseLong(config.getProperty("silverMaxCharsPerInstance").trim());
//    }
//    public long getSilverMaxCharsPerJsp(){
//    	return Long.parseLong(config.getProperty("silverMaxCharsPerJsp").trim());
//    }
//    public long getSilverMaxCharsPerCss(){
//    	return Long.parseLong(config.getProperty("silverMaxCharsPerCss").trim());
//    }
//
////  # gold type: 
//    public long getGoldPagesCanHave(){
//    	return Long.parseLong(config.getProperty("goldPagesCanHave").trim());
//    }
//    public long getGoldContainersCanHave(){
//    	return Long.parseLong(config.getProperty("goldContainersCanHave").trim());
//    }
//    public long getGoldModuleDetailsCanHave(){
//    	return Long.parseLong(config.getProperty("goldModuleDetailsCanHave").trim());
//    }
//    public long getGoldInstancesCanHave(){
//    	return Long.parseLong(config.getProperty("goldInstancesCanHave").trim());
//    }
//    public long getGoldProductsCanHave(){
//    	return Long.parseLong(config.getProperty("goldProductsCanHave").trim());
//    }
//    public long getGoldMaxCharsPerModuledetail(){
//    	return Long.parseLong(config.getProperty("goldMaxCharsPerModuledetail").trim());
//    }
//    public long getGoldMaxCharsPerInstance(){
//    	return Long.parseLong(config.getProperty("goldMaxCharsPerInstance").trim());
//    }
//    public long getGoldMaxCharsPerJsp(){
//    	return Long.parseLong(config.getProperty("goldMaxCharsPerJsp").trim());
//    }
//    public long getGoldMaxCharsPerCss(){
//    	return Long.parseLong(config.getProperty("goldMaxCharsPerCss").trim());
//    }
    
    // server ip detect
    public boolean serverIpDetect(){
    	return Boolean.valueOf(config.getProperty("server.ip.detect"));
    }
    
    public String getProxyId() {
    	return config.getProperty("proxy.ip");
    }
}
