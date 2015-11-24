package com.bizislife.core.hibernate.dao;

import java.util.List;

import com.bizislife.core.hibernate.pojo.EmailTemplate;
import com.bizislife.core.hibernate.pojo.PropertyInTable;
import com.bizislife.core.view.dto.*;


public interface MiscellaneousDao {

    public List<NaicDTO> getAllNaics();
    
    public EmailTemplate getEmailTemplateByEmailtype(String type);
	
    public PropertyInTable getPropertyByKey(String key);
	
    /**
     * this method is for initializing the database during the application's deployment.
     */
//    public void initialDb();
    
}
