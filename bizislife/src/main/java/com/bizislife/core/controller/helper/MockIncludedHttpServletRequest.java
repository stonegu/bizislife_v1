package com.bizislife.core.controller.helper;

import javax.servlet.*;

import org.springframework.mock.web.MockHttpServletRequest;

public class MockIncludedHttpServletRequest extends MockHttpServletRequest {

	public MockIncludedHttpServletRequest() {
		super();
	}

	public DispatcherType getDispatcherType() {
		return DispatcherType.INCLUDE;
	}

	public boolean isAsyncSupported() {
		return false;
	}
}