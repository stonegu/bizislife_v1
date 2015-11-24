package com.bizislife.core.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.bizislife.core.configuration.ApplicationConfiguration;
import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.FileUploadDto;
import com.bizislife.core.controller.component.FileUploadResponse;
import com.bizislife.core.hibernate.pojo.MediaDetail;
import com.bizislife.core.service.AccountService;
import com.bizislife.core.service.MediaService;
import com.bizislife.util.WebUtil;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

@Controller
public class UploadController {
	
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private MediaService mediaService;

    @Autowired
    protected ApplicationConfiguration applicationConfig;
	
    private String storeFiles(HashMap<String, MultipartFile> fileList) {
        StringBuilder json = new StringBuilder();
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost("http://"+applicationConfig.getFileServerLocation()+"/web/storefilesInMultiThread");

            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            
            for(Map.Entry<String, MultipartFile> e : fileList.entrySet()) { 
                String fileName = (String)e.getKey();
                MultipartFile multipartFile = (MultipartFile)e.getValue();
                multipartEntity.addPart(fileName, new InputStreamBody(multipartFile.getInputStream(), multipartFile.getContentType(), multipartFile.getOriginalFilename()));
            } 
            postRequest.setEntity(multipartEntity);
            HttpResponse response = httpClient.execute(postRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            String output;
            while ((output = br.readLine()) != null) {
//                System.out.println("Webservices output - " + output);
                json.append(output);
            }
            httpClient.getConnectionManager().shutdown();
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
    
    /**
     * this method is used for upload file(s) by using form and submit directly.
     * current support image, pdf, text, css, js only
     * 
     * @param fileUploadDto
     * @return
     */
    @RequestMapping(value="/uploadFileByHttpPost", method=RequestMethod.POST)
    public @ResponseBody ApiResponse uploadFileByHttpPost(FileUploadDto fileUploadDto) {
    	String res = null;
    	ApiResponse apires = null;
    	long maxUploadSize = applicationConfig.getUploadFileMaxSizeInKB()*1024;

    	HashMap<String, MultipartFile> fileList = null;
    	Map<String, String> wrongTypeFileMap = new HashMap<String, String>();
    	
    	if(accountService.getCurrentAccount()!=null && fileUploadDto!=null && fileUploadDto.getFiles()!=null && fileUploadDto.getFiles().size()>0){
    		fileList = new HashMap<String, MultipartFile>();
    		for(CommonsMultipartFile f : fileUploadDto.getFiles()){
    			long size = f.getSize();
    			
    			if((f.getContentType().indexOf(MediaDetail.MediaType.image.name())>-1 
//    				|| f.getContentType().indexOf(MediaDetail.MediaType.pdf.name())>-1
    				|| f.getContentType().indexOf(MediaDetail.MediaType.text.name())>-1
    				|| f.getContentType().indexOf(MediaDetail.MediaType.javascript.name())>-1
    				|| f.getContentType().indexOf(MediaDetail.MediaType.css.name())>-1) 
    				&& size<=maxUploadSize
    			){
                	fileList.put(f.getOriginalFilename(), f);
    			}else{
    				StringBuilder errormsg = new StringBuilder();
    				if(size>maxUploadSize){
    					errormsg.append("file size (").append(size).append("bytes) is bigger than the maximum upload size (").append(maxUploadSize).append("bytes).");
    				}else{
    					errormsg.append("file type (").append(f.getContentType()).append(") is not supported.");
    				}
    				wrongTypeFileMap.put(f.getOriginalFilename(), errormsg.toString());
    			}
    		}
        	res = storeFiles(fileList);
    	}
    	
    	// update the json string res to include FileUploadDto.targetNodeId
    	if(res!=null){
    		
    		Gson gson = new Gson();
    		apires = gson.fromJson(res, ApiResponse.class);
    		if(apires==null) apires = new ApiResponse();
    		apires.setSuccess(true);
    		
			if(apires.getResponse1()!=null){
    			apires.setResponse2(fileUploadDto.getTargetNodeId());
			
            	for(LinkedTreeMap<String, String> o : (List<LinkedTreeMap>)apires.getResponse1()){
//            		o.put("extraInfo", "upload succeeded!");
            		
            		String contentType = o.get("contentType");
            		String newNodeName = o.get("originalName");
            		String nodeUuid = o.get("generatedName");
            		
            		if(StringUtils.isNotBlank(nodeUuid) && StringUtils.isNotBlank(contentType) && StringUtils.isNotBlank(newNodeName)){
                		if(contentType.indexOf(MediaDetail.MediaType.image.name())>-1){
                    		mediaService.newMediaNode(MediaDetail.MediaType.image, fileUploadDto.getTargetNodeId(), newNodeName, nodeUuid);
                		}else if(contentType.indexOf(MediaDetail.MediaType.pdf.name())>-1){
                    		mediaService.newMediaNode(MediaDetail.MediaType.pdf, fileUploadDto.getTargetNodeId(), newNodeName, nodeUuid);
                		}else if(contentType.indexOf(MediaDetail.MediaType.text.name())>-1){
                			if(contentType.indexOf(MediaDetail.MediaType.css.name())>-1){
                    			mediaService.newMediaNode(MediaDetail.MediaType.css, fileUploadDto.getTargetNodeId(), newNodeName, nodeUuid);
                    		}else{
                    			mediaService.newMediaNode(MediaDetail.MediaType.text, fileUploadDto.getTargetNodeId(), newNodeName, nodeUuid);
                    		}
                		}else if(contentType.indexOf(MediaDetail.MediaType.javascript.name())>-1){
                			mediaService.newMediaNode(MediaDetail.MediaType.javascript, fileUploadDto.getTargetNodeId(), newNodeName, nodeUuid);
                		}
            		}
            		
            	}
			
			}
    		
    		if(wrongTypeFileMap.size()>0){
    			List<LinkedTreeMap> wtfList = new ArrayList<LinkedTreeMap>();
    			for(Map.Entry<String, String> en : wrongTypeFileMap.entrySet()){
    				LinkedTreeMap m = new LinkedTreeMap();
    				m.put("originalName", en.getKey());
    				m.put("extraInfo", en.getValue());
    				wtfList.add(m);
    			}
        		if(apires.getResponse1()!=null){
        			((List<LinkedTreeMap>)apires.getResponse1()).addAll(wtfList);
        		}else{
        			apires.setResponse1(wtfList);
        		}
    		}
    		
    	}
    	
    	return apires;
    	
    }
    
    @Deprecated
    @RequestMapping(value="/uploadFileByQQ", method=RequestMethod.POST)
    public @ResponseBody String uploadFileByQQ(
    		byte[] qqfile,
    		@RequestParam(value="qqfile", required=false) String qqfileName,
    		HttpServletRequest request
    		){
        StringBuilder json = new StringBuilder();
    	
		try {
	        DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost postRequest = new HttpPost("http://localhost:8090/web/storefilesInMultiThread");
	        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	        multipartEntity.addPart(qqfileName, new ByteArrayBody(qqfile, qqfileName));

	        postRequest.setEntity(multipartEntity);
	        HttpResponse response;
			response = httpClient.execute(postRequest);
	        if (response.getStatusLine().getStatusCode() != 200) {
	            throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
	        }

	        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

	        String output;
	        while ((output = br.readLine()) != null) {
	            System.out.println("Webservices output - " + output);
	            json.append(output);
	        }
	        httpClient.getConnectionManager().shutdown();
	        
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json.toString();
    }
    
}
