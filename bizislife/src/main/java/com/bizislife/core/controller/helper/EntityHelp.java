package com.bizislife.core.controller.helper;

import org.apache.commons.lang.StringUtils;

import com.bizislife.core.controller.SitedesignHelper;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.siteDesign.module.Module;
import com.bizislife.core.siteDesign.module.ModuleAttribute;
import com.bizislife.core.siteDesign.module.ModuleImageAttribute;
import com.bizislife.core.siteDesign.module.Module.AttrGroup;

public class EntityHelp {
	
	public static String getDefaultImageFromEntity(EntityDetail entitydetail, ModuleDetail moduleDetail){
		String defaultImage = null;
		
		if(entitydetail!=null && StringUtils.isNotBlank(entitydetail.getDetail())){
			
			Module instance = SitedesignHelper.getModuleFromXml(entitydetail.getDetail());
			
			// update the instance based on moduleDetail
			if(moduleDetail!=null && StringUtils.isNotBlank(moduleDetail.getXml())){
				Module module = SitedesignHelper.getModuleFromXml(moduleDetail.getXml());
				if(module!=null){
					SitedesignHelper.updateModuleInstanceByModule(module, instance);
				}
			}
			
			
			// find defaultImageAttr from module
			if(instance!=null && instance.getAttrGroupList()!=null && instance.getAttrGroupList().size()>0){
				
				
				
				
//				boolean defaultImageFound = false;
				for(AttrGroup g : instance.getAttrGroupList()){
					if(StringUtils.isNotBlank(defaultImage)) break;
					if(g.getAttrList()!=null && g.getAttrList().size()>0){
						for(ModuleAttribute a : g.getAttrList()){
							if(StringUtils.isNotBlank(defaultImage)) break;
							if(a.getClass().equals(ModuleImageAttribute.class)){
								if(((ModuleImageAttribute)a).getDefaultPicture()!=null && ((ModuleImageAttribute)a).getDefaultPicture()
									&& StringUtils.isNotBlank(((ModuleImageAttribute)a).getFileSystemName())
								){
									defaultImage = ((ModuleImageAttribute)a).getFileSystemName();
								}
							}
						}
					}
				}
			}
			
		}
		return defaultImage;
	}

}
