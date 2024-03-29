package com.bizislife.core.controller.helper;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;

public class JspLocaleResolver implements LocaleResolver {

	private static final String JSP_LOCALE = "com.bizislife.core.controller.helper.LOCALE";

	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		Locale locale = (Locale) request.getAttribute(JSP_LOCALE);
		if (null == locale) {
			locale = Locale.getDefault();
		}
		return locale;
	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		if (locale == null) {
			locale = Locale.getDefault();
		}
		response.setLocale(locale);
		request.setAttribute(JSP_LOCALE, locale);

	}

}