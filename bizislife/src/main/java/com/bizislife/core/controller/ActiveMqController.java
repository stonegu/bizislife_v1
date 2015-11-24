package com.bizislife.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bizislife.core.service.AccountService;
import com.bizislife.core.service.activemq.test.queue.*;

@Controller
public class ActiveMqController {
	private static final Logger logger = LoggerFactory.getLogger(ActiveMqController.class);
	
    @Autowired
    private TestSender sender;
    @Autowired
    private TestReceiver receiver;
    
    
    @RequestMapping(value = "/test_send")
    public ModelAndView test_send(@RequestParam(value="message", required=false) String messageParam) 
    {
    	ModelAndView modelAndView = new ModelAndView("activemq_test_send");
    	sender.send(messageParam);
    	modelAndView.addObject("msg",messageParam);
		return modelAndView;
    }
    
    @RequestMapping(value = "/test_receive")
    public ModelAndView test_receive() 
    {
    	Object result = receiver.receive();
    	ModelAndView modelAndView = new ModelAndView("activemq_test_receive", "result", result);
		return modelAndView;
    }

    
    
    
    
    
    

}
