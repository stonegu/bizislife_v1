package com.bizislife.core.controller.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

@Component
public class TechOddViewResolver extends UrlBasedViewResolver {

	@Override
	protected Class<JstlView> getViewClass() {
		return JstlView.class;
	}

	@Override
	protected String getSuffix() {
		return ".jsp";
	}

	@Override
	protected String getPrefix() {
		// return "/WEB-INF/jsp/";
		return "/WEB-INF/view/";
	}

	public String urlForView(String view) {
		String result = view;
		if (!result.startsWith(getPrefix())) {
			result = getPrefix() + result;
		}
		if (!result.endsWith(getSuffix())) {
			result = result + getSuffix();
		}
		return result;
	}
}