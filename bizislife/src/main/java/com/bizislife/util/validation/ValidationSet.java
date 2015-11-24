package com.bizislife.util.validation;

import java.util.regex.*;

public class ValidationSet {

	/**
	 * Validate the email address
	 */
	public static boolean isValidEmail(String email){
		try{
			Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
			Matcher m = p.matcher(email);
			return m.matches();
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean isValidPhone(String phone){
		try{
			Pattern p = Pattern.compile("\\d\\d([,\\s])?\\d\\d\\d\\d([,\\s])?\\d\\d\\d\\d");
			Matcher m = p.matcher(phone);
			return m.matches();
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isAlphaOnly(String string){
		try{
			Pattern p = Pattern.compile("^[A-Za-z]+$");
			Matcher m = p.matcher(string);
			return m.matches();
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isAlphaNumOnly(String string){
		try{
			Pattern p = Pattern.compile("^[A-Za-z0-9]+$");
			Matcher m = p.matcher(string);
			return m.matches();
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isAlphaNumUnderscoreOnly(String string){
		try{
			Pattern p = Pattern.compile("^[A-Za-z0-9_]+$");
			Matcher m = p.matcher(string);
			return m.matches();
			
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isAlphaNumUnderscoreSpaceOnly(String string){
		try{
			Pattern p = Pattern.compile("^[A-Za-z0-9_\\s]+$");
			Matcher m = p.matcher(string);
			return m.matches();
			
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isAlphaNumUnderscoreDashSpaceDotOnly(String string){
		try{
			Pattern p = Pattern.compile("^[A-Za-z0-9_\\-\\s\\.]+$");
			Matcher m = p.matcher(string);
			return m.matches();
			
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isValidLoginName(String loginName){
		try{
			Pattern p = Pattern.compile("^[A-Za-z0-9_@\\.\\-]+$");
			Matcher m = p.matcher(loginName);
			return m.matches();
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isValidHexColor(String hexColor){
		try{
			Pattern p = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
			Matcher m = p.matcher(hexColor);
			return m.matches();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isUrl(String url){
		try{
			Pattern urlPattern = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
			Matcher m = urlPattern.matcher(url);
			return m.matches();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isDomain(String domain){
		try{
			Pattern urlPattern = Pattern.compile("^[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
			Matcher m = urlPattern.matcher(domain);
			return m.matches();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
}
