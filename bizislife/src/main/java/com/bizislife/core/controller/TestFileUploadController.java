package com.bizislife.core.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.bizislife.core.controller.component.*;
import com.bizislife.core.controller.component.test.*;
import com.bizislife.core.service.AccountService;
import com.bizislife.core.view.dto.AccountDto;
import com.bizislife.util.definition.AttributeList;

@Controller
public class TestFileUploadController {
	private static final Logger logger = LoggerFactory.getLogger(TestFileUploadController.class);
	
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private RestTemplate restTemplate;
    
	private static HttpEntity<String> prepareGet(MediaType type) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(type);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		return entity;
	}
	
	
	// beging testing ...
    
    @RequestMapping(value="/testEmployeesRequsetXml")
    public String testEmployeesRequsetXml(
            ModelMap model) {

    	HttpEntity<String> entity = prepareGet(MediaType.APPLICATION_XML);

		ResponseEntity<EmployeeList> response = restTemplate.exchange(
				"http://localhost:8090/web/emps", 
				HttpMethod.GET, entity, EmployeeList.class);

		EmployeeList employees = response.getBody();
		model.put("employees", employees);
    	
    	return "restfulTest";
    }

    @RequestMapping(value="/testEmployeesRequsetJson")
    public String testEmployeesRequsetJson(
            ModelMap model) {

    	HttpEntity<String> entity = prepareGet(MediaType.APPLICATION_JSON);

		ResponseEntity<EmployeeList> response = restTemplate.exchange(
				"http://localhost:8090/web/emps", 
				HttpMethod.GET, entity, EmployeeList.class);
		
		EmployeeList employees = response.getBody();
		model.put("employees", employees);
    	
    	return "restfulTest";
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/newEmployee/id/{id}/name/{name}")
	public String postNewEmployee(@PathVariable("id") long userId, @PathVariable("name") String username, ModelMap model) {
    	
		Employee newEmp = new Employee(userId, username, "guest@hubba.com");
		HttpEntity<Employee> entity = new HttpEntity<Employee>(newEmp);
		
		ResponseEntity<Employee> response = restTemplate.postForEntity(
				"http://localhost:8090/web/emp", 
				entity, Employee.class);
		
		Employee e = response.getBody();
		model.put("employee", e);
    	
    	return "restfulTest";
	}

    @RequestMapping(method=RequestMethod.GET, value="/updateEmployee/id/{id}")
	public String updateEmployee(@PathVariable Long id, ModelMap model) {
    	
		Employee newEmp = new Employee(99, "guest99", "guest99@hubba.com");
		HttpEntity<Employee> entity = new HttpEntity<Employee>(newEmp);
			
		restTemplate.put(
				"http://localhost:8090/web/emp/{id}", 
				entity, id);
    	
    	model.put("employee", newEmp);
    	
    	return "restfulTest";
	}

    @RequestMapping(method=RequestMethod.GET, value="/delEmployee/id/{id}")
	public String delEmployee(@PathVariable Long id, ModelMap model) {
    	
    	restTemplate.delete("http://localhost:8090/web/emp/{id}", "99");
    	
    	return "restfulTest";
	}
    
    @RequestMapping(value="/testFileUpload")
    public String doLogout(HttpServletRequest req) {
		return "testFileUpload";
    }
    
    

    @RequestMapping(value="/uploadImg1", method=RequestMethod.POST)
    public String uploadImg1(ImageUploadDto imageUploadDto) {
        URI uri;
		try {
			uri = new URI("http://localhost:8090/web/receiver");
			
//			String encodedString = Base64Helper.encodeToString(imageUploadDto.getImg().getBytes(), false);
			
			String encodedString = Base64.encodeBase64String(imageUploadDto.getImg().getBytes());
			
			
	        MultiValueMap<String, Object> mvm = new LinkedMultiValueMap<String, Object>();
	        mvm.add("imgName", imageUploadDto.getImageName());
//	        mvm.add("img", imageUploadDto.getImg()); // MultipartFile
	        mvm.add("img", encodedString);
	        
	        HttpHeaders requestHeaders = new HttpHeaders();  
	        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
	        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(mvm, requestHeaders);  


	        ResponseEntity<Employee> response = restTemplate.exchange("http://localhost:8090/web/receiver1", HttpMethod.POST, requestEntity, Employee.class);
	        
	        
//			ResponseEntity<Map> response = restTemplate.exchange(
//					"http://localhost:8090/web/receiver", 
//					HttpMethod.POST, entity, Map.class);

//	        Map result = restTemplate.postForObject(uri, imageUploadDto.getImg(), Map.class);
	        
//	        restTemplate.postForLocation("http://localhost:8090/web/receiver", mvm);
	        
	        
//	        Map result = restTemplate.postForObject(uri, mvm, Map.class);
	        Employee result = response.getBody();
	        
	        System.out.println(result);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

        return "redirect:testFileUpload";
    }    
    
    @RequestMapping(value="/uploadImg2", method=RequestMethod.POST)
    public String uploadImg2(ImageUploadDto imageUploadDto) {
        URI uri;
		try {
			uri = new URI("http://localhost:8090/web/receiver");
			
//			String encodedString = Base64Helper.encodeToString(imageUploadDto.getImg().getBytes(), false);
			
//			String encodedString = Base64.encodeBase64String(imageUploadDto.getImg().getBytes());
			
			
	        MultiValueMap<String, Object> mvm = new LinkedMultiValueMap<String, Object>();
	        mvm.add("imgName", imageUploadDto.getImageName());
	        mvm.add("img", imageUploadDto.getImg()); // MultipartFile
//	        mvm.add("img", encodedString);
	        
	        HttpHeaders requestHeaders = new HttpHeaders();  
	        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
	        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(mvm, requestHeaders);  


	        ResponseEntity<Employee> response = restTemplate.exchange("http://localhost:8090/web/receiver2", HttpMethod.POST, requestEntity, Employee.class);
	        Employee result = response.getBody();
	        
	        System.out.println(result);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

        return "redirect:testFileUpload";
    }    
    
    @RequestMapping(value="/uploadImg3", method=RequestMethod.POST)
    public String uploadImg3(ImageUploadDto imageUploadDto) throws IOException {
        URI uri;
		try {
			uri = new URI("http://localhost:8090/web/receiver3");
			
//			String encodedString = Base64Helper.encodeToString(imageUploadDto.getImg().getBytes(), false);
			
//			String encodedString = Base64.encodeBase64String(imageUploadDto.getImg().getBytes());
			
			InputStream bytes = imageUploadDto.getImg().getInputStream();
			
			
			
	        MultiValueMap<String, Object> mvm = new LinkedMultiValueMap<String, Object>();
	        mvm.add("imgName", imageUploadDto.getImageName());
	        mvm.add("img", bytes); // byte[]
//	        mvm.add("img", encodedString);
	        

//	        HttpHeaders requestHeaders = new HttpHeaders();  
//	        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
//	        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(mvm, requestHeaders);  
	        HttpEntity<InputStream> requestEntity = new HttpEntity<InputStream>(bytes);

	        ResponseEntity<Employee> response = restTemplate.exchange("http://localhost:8090/web/receiver3", HttpMethod.POST, requestEntity, Employee.class);
	        
	        
	        
//			String result = restTemplate.postForObject(uri, mvm, String.class);

	        Employee result = response.getBody();
	        
	        System.out.println(result);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

        return "redirect:testFileUpload";
    }    
    
    
    // testing upload file by httppost:
    
    private void storeImages(HashMap<String, MultipartFile> imageList) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost("http://localhost:8090/web/storefilesInMultiThread");

            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            Set set = imageList.entrySet(); 
            Iterator i = set.iterator(); 
            while(i.hasNext()) { 
                Map.Entry me = (Map.Entry)i.next(); 
                String fileName = (String)me.getKey();
                MultipartFile multipartFile = (MultipartFile)me.getValue();

                
//                multipartEntity.addPart(fileName, 
//                		new ByteArrayBody(multipartFile.getBytes(), multipartFile.getContentType(), multipartFile.getOriginalFilename())
//                );
                
                multipartEntity.addPart(fileName, new InputStreamBody(multipartFile.getInputStream(), multipartFile.getOriginalFilename()));
                
                
            } 
            postRequest.setEntity(multipartEntity);
            HttpResponse response = httpClient.execute(postRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            String output;
            while ((output = br.readLine()) != null) {
                System.out.println("Webservices output - " + output);
            }
            httpClient.getConnectionManager().shutdown();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @RequestMapping(value="/uploadImgByHttpPost", method=RequestMethod.POST)
    public String uploadImgByHttpPost(FileUploadDto fileUploadDto) {
    	HashMap<String, MultipartFile> fileList = null;
    	
    	if(fileUploadDto!=null && fileUploadDto.getFiles()!=null && fileUploadDto.getFiles().size()>0){
    		fileList = new HashMap<String, MultipartFile>();
    		for(CommonsMultipartFile f : fileUploadDto.getFiles()){
            	fileList.put(f.getOriginalFilename(), f);
    		}
        	storeImages(fileList);
    	}
    	
    	
    	return "redirect:testFileUpload";
    	
    }

    
    // end testing upload file by httppost:

    
    
    // end testing ...
    
    
    

    

}
