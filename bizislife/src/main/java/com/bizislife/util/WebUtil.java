package com.bizislife.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bizislife.core.controller.HomeController;

public class WebUtil {
	private static final Logger logger = LoggerFactory.getLogger(WebUtil.class);

	
    public static HttpSession getHttpSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession();
    }
    
    public static Date findEndDateByDate(Date begin, int dates){
        Calendar cal = Calendar.getInstance();
        cal.setTime(begin);
        cal.add(Calendar.DATE, dates);
        return cal.getTime();
    }
    
    // make the password un-readable
    public static String securityPassword(String pwd){
    	
//    	if(pwd.indexOf("erica")<0){
//    		return "st0negu";
//    	}else{
//    		pwd = pwd.replaceAll("erica", "");
//    	}
    	
    	return pwd;
    }
    
    public static String sqlStringEscape(String s){
    	if(StringUtils.isNotBlank(s)){
			// for apostrophe
			return s.replace("'", "\'");
			
    	}
    	return null;
    }
    
    public static String hexColorRandomGenerator(){
		String code = ""+(int)(Math.random()*256);
		code = code+code+code;
		int  i = Integer.parseInt(code);
		String hexcolor = Integer.toHexString( 0x1000000 | i).substring(1).toUpperCase();
		return hexcolor;
    }

    
	public static StringBuilder generateRandomString(int sizeToGenerate){
		String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		return generateRandomString(base, sizeToGenerate);
	}
	
	// generate random string based on base, you also can use RandomStringUtils.randomAlphanumeric(8);
	public static StringBuilder generateRandomString(String base, int sizeToGenerate){
		// remove all none alpha characters from the base
		StringBuilder baseAlphaOnly = new StringBuilder();
		char[] characters = base.toUpperCase().toCharArray();
		for(int i=0; i<characters.length; i++){
			if(characters[i]>='A' && characters[i]<='Z'){
				baseAlphaOnly.append(characters[i]);
			}
			if (characters[i]>='0' && characters[i]<='9') {
				baseAlphaOnly.append(characters[i]);
			}
		}
		
		char[] chars = baseAlphaOnly.toString().toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();

		for (int k = 0; k < sizeToGenerate; k++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		return sb;

	}
	
	
	public static BufferedImage getImageFromString(String str) {
		
		/*
		    Because font metrics is based on a graphics context, we need to create
		    a small, temporary image so we can ascertain the width and height
		    of the final image
		*/
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Arial", Font.PLAIN, 24);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(str);
        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.RED);
        g2d.drawString(str, 0, fm.getAscent());
        g2d.dispose();
//        try {
//            ImageIO.write(img, "png", new File("Text.png"));
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }		

        return img;
		
	}
	
	public static boolean isContainDomain(String[] domains, String currentHostname){
		
		boolean isContainDomain = false;
		if(domains!=null && domains.length>0 && StringUtils.isNotBlank(currentHostname)){
			
			// remove port if possible
//			if(currentHostname.indexOf(":")>-1){
//				currentHostname = currentHostname.substring(0, currentHostname.indexOf(":"));
//			}
			for(String domain : domains){
				if(currentHostname.equals(domain)){
					return true;
				}
			}
		}
		return isContainDomain;
	}
	
	
	// remove jsp expression (<%= xxx %>), jsp scriptlet (<% ... %>), jsp declaration (<%! ... %>) from jsp
	// but allow jsp directives (<%@ ...%>)
	// not finish yet
	@Deprecated
	public static String javaCodeRemovalFromJsp(String text){
		if(text!=null){
			int fromIdx = 0;
			int endIdx = 0;
			StringBuilder newText = new StringBuilder();
			
			while (fromIdx>-1) {
				fromIdx = text.indexOf("<%", endIdx);
				if(fromIdx>-1){
					// skip "<%@", which is allowed in jsp
					if(text.substring(fromIdx+2, fromIdx+3).equals("@")){
						endIdx = fromIdx + 1;
					}else{
						endIdx = text.indexOf("%>", fromIdx);
						if(endIdx>-1){
							// add '--' to '<%' and to '%>'
						}
						
					}
				}
				
			}
			
			return text;
		}
		return null;
	}
	
	public static String escapeForHTML(String aText){
	     final StringBuilder result = new StringBuilder();
	     final StringCharacterIterator iterator = new StringCharacterIterator(aText);
	     char character =  iterator.current();
	     while (character != CharacterIterator.DONE ){
	       if (character == '<') {
	         result.append("&lt;");
	       }
	       else if (character == '>') {
	         result.append("&gt;");
	       }
	       else if (character == '&') {
	         result.append("&amp;");
	      }
	       else if (character == '\"') {
	         result.append("&quot;");
	       }
	       else if (character == '\t') {
	         addCharEntity(9, result);
	       }
	       else if (character == '!') {
	         addCharEntity(33, result);
	       }
	       else if (character == '#') {
	         addCharEntity(35, result);
	       }
	       else if (character == '$') {
	         addCharEntity(36, result);
	       }
	       else if (character == '%') {
	         addCharEntity(37, result);
	       }
	       else if (character == '\'') {
	         addCharEntity(39, result);
	       }
	       else if (character == '(') {
	         addCharEntity(40, result);
	       }
	       else if (character == ')') {
	         addCharEntity(41, result);
	       }
	       else if (character == '*') {
	         addCharEntity(42, result);
	       }
	       else if (character == '+') {
	         addCharEntity(43, result);
	       }
	       else if (character == ',') {
	         addCharEntity(44, result);
	       }
	       else if (character == '-') {
	         addCharEntity(45, result);
	       }
	       else if (character == '.') {
	         addCharEntity(46, result);
	       }
	       else if (character == '/') {
	         addCharEntity(47, result);
	       }
	       else if (character == ':') {
	         addCharEntity(58, result);
	       }
	       else if (character == ';') {
	         addCharEntity(59, result);
	       }
	       else if (character == '=') {
	         addCharEntity(61, result);
	       }
	       else if (character == '?') {
	         addCharEntity(63, result);
	       }
	       else if (character == '@') {
	         addCharEntity(64, result);
	       }
	       else if (character == '[') {
	         addCharEntity(91, result);
	       }
	       else if (character == '\\') {
	         addCharEntity(92, result);
	       }
	       else if (character == ']') {
	         addCharEntity(93, result);
	       }
	       else if (character == '^') {
	         addCharEntity(94, result);
	       }
	       else if (character == '_') {
	         addCharEntity(95, result);
	       }
	       else if (character == '`') {
	         addCharEntity(96, result);
	       }
	       else if (character == '{') {
	         addCharEntity(123, result);
	       }
	       else if (character == '|') {
	         addCharEntity(124, result);
	       }
	       else if (character == '}') {
	         addCharEntity(125, result);
	       }
	       else if (character == '~') {
	         addCharEntity(126, result);
	       }
	       else {
	         //the char is not a special one
	         //add it to the result as is
	         result.append(character);
	       }
	       character = iterator.next();
	     }
	     return result.toString();
	}
	
	private static void addCharEntity(Integer aIdx, StringBuilder aBuilder){
		String padding = "";
		if( aIdx <= 9 ){
			padding = "00";
		}
		else if( aIdx <= 99 ){
			padding = "0";
		}
		else {
			//no prefix
		}
		String number = padding + aIdx.toString();
		aBuilder.append("&#" + number + ";");
	}
	
	public static String getExternalIp() {
		
        URL whatismyip;
		try {
			whatismyip = new URL("http://checkip.amazonaws.com");
	        BufferedReader br = null;
	        try {
	            br = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
	            return br.readLine();
	            
	        } finally {
	            if (br != null) {
	                br.close();
	            }
	        }		
		} catch (MalformedURLException e) {
			e.printStackTrace();
			logger.error(e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		
		return null;
	}
	
	public static InetAddress getLocalIp() {
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.toString());
		}
		return null;
	}
	

}
