package com.bizislife.core.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.arjuna.ats.internal.arjuna.utils.UuidProcessId;
import com.bizislife.core.configuration.ApplicationConfiguration;
import com.bizislife.core.entity.MetaData;
import com.bizislife.core.entity.converter.ContainerToPageDetailConvertor;
import com.bizislife.core.entity.converter.ModuleConverter;
import com.bizislife.core.entity.converter.ModuleAttrConverter;
import com.bizislife.core.entity.converter.ModuleAttrGroupConverter;
import com.bizislife.core.hibernate.pojo.ContainerDetail;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.ModuleInstance;
import com.bizislife.core.hibernate.pojo.ModuleInstanceInterface;
import com.bizislife.core.hibernate.pojo.Organization;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.siteDesign.module.Module;
import com.bizislife.core.siteDesign.module.ModuleAttributeList;
import com.bizislife.core.siteDesign.module.ModuleEntityCategoryListAttribute;
import com.bizislife.core.siteDesign.module.ModuleImageAttribute;
import com.bizislife.core.siteDesign.module.ModuleIntegerAttribute;
import com.bizislife.core.siteDesign.module.ModuleAttribute;
import com.bizislife.core.siteDesign.module.ModuleLinkAttribute;
import com.bizislife.core.siteDesign.module.ModuleMoneyAttribute;
import com.bizislife.core.siteDesign.module.ModuleNumberAttribute;
import com.bizislife.core.siteDesign.module.ModuleProductListAttribute;
import com.bizislife.core.siteDesign.module.ModuleStringAttribute;
import com.bizislife.core.siteDesign.module.ModuleTextAttribute;
import com.bizislife.core.siteDesign.module.Module.AttrGroup;
import com.bizislife.core.siteDesign.module.Money;
import com.bizislife.util.WebUtil;
import com.bizislife.util.definition.DatabaseRelatedCode;
import com.bizislife.util.validation.ValidationSet;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class SitedesignHelper {

	public static Module getModuleFromXml(String xml){
		if(StringUtils.isNotBlank(xml)){
			XStream stream = new XStream(new DomDriver());
			stream.registerConverter(new ModuleConverter());
			stream.registerConverter(new ModuleAttrGroupConverter());
			stream.registerConverter(new ModuleAttrConverter());
			stream.processAnnotations(new Class[]{Module.class, AttrGroup.class, ModuleTextAttribute.class, ModuleNumberAttribute.class});
			return (Module)stream.fromXML(xml);
		}
		return null;
	}
	
	public static Module getModuleFromInstance(ModuleInstanceInterface instance){
		if(instance!=null && instance.getxml()!=null){
			String xml = instance.getxml();
			Module module = getModuleFromXml(xml);
			module.setPrettyName(instance.getPrettyname());
			module.setSystemName(instance.getUuid());
			
			String type = null;
			if(instance.getClass().equals(EntityDetail.class)){
				type = EntityDetail.EntityType.fromCode(instance.getType()).name();
			}else if(instance.getClass().equals(ModuleInstance.class)){
				type = ModuleDetail.Type.fromCode(instance.getType()).name();
			}
			
			module.setType(type);
			
			return module;
		}
		return null;
	}
	
	
	
	/**
	 * @param module
	 * @return 'AttrGroup' : List of group's uuids, 'ModuleAttribute' : List of attr's uuids
	 */
	public static Map<String, List<String>> findAllElementUuids(Module module) {
		if(module!=null){
			List<String> groupUuids = new ArrayList<String>();
			List<String> attrUuids = new ArrayList<String>();
			if(module.getAttrGroupList()!=null){
				for(AttrGroup g : module.getAttrGroupList()){
					groupUuids.add(g.getGroupUuid());
					if(g.getAttrList()!=null){
						for(ModuleAttribute at : g.getAttrList()){
							attrUuids.add(at.getUuid());
						}
					}
				}
			}
			Map<String, List<String>> uuidsWithType = new HashMap<String, List<String>>();
			uuidsWithType.put(AttrGroup.class.getSimpleName(), groupUuids);
			uuidsWithType.put(ModuleAttribute.class.getSimpleName(), attrUuids);
			return uuidsWithType;
		}
		return null;
	}
	
	public static String getXmlFromModule(Module module){
		if(module!=null){
			XStream stream = new XStream(new DomDriver());
			stream.registerConverter(new ModuleConverter());
			stream.registerConverter(new ModuleAttrGroupConverter());
			stream.registerConverter(new ModuleAttrConverter());
			stream.processAnnotations(new Class[]{Module.class, AttrGroup.class, ModuleTextAttribute.class, ModuleNumberAttribute.class});
			return stream.toXML(module);
		}
		return null;
	}
	
	@Deprecated
	public static Module createInstanceFromHttprequest(HttpServletRequest request, Module moduleTemplate){
		if(request!=null && moduleTemplate!=null){
			
//			Enumeration attrNames = request.getAttributeNames();
//			Enumeration paramNames = request.getParameterNames(); 
			Map<String, String[]> paramMap = request.getParameterMap(); // get orguuid and moduleuuid and all other's params
			
			String orguuid = null;
			String moduleuuid = null;

			//Map<groupIndex, Map<groupname, Map<paramName, paramValues[]> >>
			Map<String, Map<String, Map<String, String[]>>> groupMaps = new HashMap<String, Map<String,Map<String,String[]>>>();
			SortedSet<Integer> groupIndexs = new TreeSet<Integer>(); // hold sorted group index
			for(Map.Entry<String, String[]> e : paramMap.entrySet()){
				if("orgId".equalsIgnoreCase(e.getKey().trim())){
					orguuid = e.getValue()[0];
				}else if("moduleId".equalsIgnoreCase(e.getKey().trim())){
					moduleuuid = e.getValue()[0];
				}else{
					String[] groupWithParamName = e.getKey().trim().split("_"); //0: groupname, 1: param name, 2: group index
					if(groupWithParamName!=null && groupWithParamName.length==3){
						// for group index
						Map<String, Map<String, String[]>> groupMap = groupMaps.get(groupWithParamName[2]);
						if(groupMap==null){
							groupMap = new HashMap<String, Map<String,String[]>>();
							groupMap.put(groupWithParamName[0], null);
							groupMaps.put(groupWithParamName[2], groupMap);
							groupIndexs.add(Integer.valueOf(groupWithParamName[2]));
						}

						// for param
						if(groupMap.get(groupWithParamName[0])==null){
							Map<String, String[]> paramNameValuesMap = new HashMap<String, String[]>();
							paramNameValuesMap.put(groupWithParamName[1], e.getValue());
							groupMap.put(groupWithParamName[0], paramNameValuesMap);
						}else{
							Map<String, String[]> paramNameValuesMap = groupMap.get(groupWithParamName[0]);
							if(paramNameValuesMap.get(groupWithParamName[1])==null){
								paramNameValuesMap.put(groupWithParamName[1], e.getValue());
							}
						}
						
					}
				}
//				System.out.print(e);
			}
			
			if(moduleTemplate.getAttrGroupList()!=null){
				Module moduleInstance = new Module();
				
				for(AttrGroup g : moduleTemplate.getAttrGroupList()){ // loop through module template
					// find group data from groupMaps based on group name
					for(Integer gIdx: groupIndexs){
						Map<String, Map<String, String[]>> e = groupMaps.get(gIdx.toString());
//					}
//					
//					for(Map.Entry<String, Map<String, Map<String, String[]>>> e : groupMaps.entrySet()){ // loop through instance value map
						if(e.containsKey(g.getGroupName().trim())){
							// for group
							AttrGroup groupInstance = new AttrGroup();
							groupInstance.setArray(g.getArray());
							groupInstance.setGroupName(g.getGroupName());
							// for a list of params
							for(ModuleAttribute p : g.getAttrList()){
								String[] values = e.get(g.getGroupName()).get(p.getName().trim()); 
								if(values!=null && values.length>0){
									if("ModuleStringParam".equalsIgnoreCase(p.getType())){
										if(p.getArray()){ // create params based on values
											for(String v : values){
												ModuleStringAttribute paramInstance = new ModuleStringAttribute();
												paramInstance.setArray(p.getArray());
												paramInstance.setDocumentation(p.getDocumentation());
												paramInstance.setMaxLength(((ModuleStringAttribute)p).getMaxLength());
												paramInstance.setMinLength(((ModuleStringAttribute)p).getMinLength());
												paramInstance.setName(p.getName());
												paramInstance.setRequired(p.getRequired());
												paramInstance.setTitle(p.getTitle());
												paramInstance.setType(p.getType());
												paramInstance.setEditable(p.getEditable());
												paramInstance.setDefaultValue(v);
												groupInstance.addAttr(paramInstance);
											}
										}else{ // only get first value to create a param
											ModuleStringAttribute paramInstance = new ModuleStringAttribute();
											paramInstance.setArray(p.getArray());
											paramInstance.setDocumentation(p.getDocumentation());
											paramInstance.setMaxLength(((ModuleStringAttribute)p).getMaxLength());
											paramInstance.setMinLength(((ModuleStringAttribute)p).getMinLength());
											paramInstance.setName(p.getName());
											paramInstance.setRequired(p.getRequired());
											paramInstance.setTitle(p.getTitle());
											paramInstance.setType(p.getType());
											paramInstance.setEditable(p.getEditable());
											paramInstance.setDefaultValue(values[0]);
											groupInstance.addAttr(paramInstance);
										}
									}else if("ModuleIntegerParam".equalsIgnoreCase(p.getType())){
										if(p.getArray()){
											for(String v : values){
												ModuleIntegerAttribute paramInstance = new ModuleIntegerAttribute();
												paramInstance.setArray(p.getArray());
												paramInstance.setDocumentation(p.getDocumentation());
												paramInstance.setMaxValue(((ModuleIntegerAttribute)p).getMaxValue());
												paramInstance.setMinValue(((ModuleIntegerAttribute)p).getMinValue());
												paramInstance.setName(p.getName());
												paramInstance.setRequired(p.getRequired());
												paramInstance.setTitle(p.getTitle());
												paramInstance.setType(p.getType());
												paramInstance.setEditable(p.getEditable());
												paramInstance.setDefaultValue(Integer.valueOf(v));
												groupInstance.addAttr(paramInstance);
											}
										}else{
											ModuleIntegerAttribute paramInstance = new ModuleIntegerAttribute();
											paramInstance.setArray(p.getArray());
											paramInstance.setDocumentation(p.getDocumentation());
											paramInstance.setMaxValue(((ModuleIntegerAttribute)p).getMaxValue());
											paramInstance.setMinValue(((ModuleIntegerAttribute)p).getMinValue());
											paramInstance.setName(p.getName());
											paramInstance.setRequired(p.getRequired());
											paramInstance.setTitle(p.getTitle());
											paramInstance.setType(p.getType());
											paramInstance.setEditable(p.getEditable());
											paramInstance.setDefaultValue(Integer.valueOf(values[0]));
											groupInstance.addAttr(paramInstance);
										}
									}
								}
							}
							moduleInstance.addAttrGroup(groupInstance);
						}
					}
				}
				
				return moduleInstance;
			}
			
		}
		return null;
	}
	
	
	/**
	 * create a default instance based on Module, remove all extra info, only put value in the instance.
	 * 
	 * @param module
	 * @return
	 */
	public static Module getDefaultModuleInstance(Module module){
		if(module!=null){
			Module moduleInstance = new Module();
			if(module.getAttrGroupList()!=null && module.getAttrGroupList().size()>0){
				for(AttrGroup g : module.getAttrGroupList()){
					AttrGroup insAttrGroup = new AttrGroup();
					insAttrGroup.setGroupUuid(UUID.randomUUID().toString());
					insAttrGroup.setModuleGroupUuid(g.getGroupUuid());
					
					if(g.getAttrList()!=null && g.getAttrList().size()>0){
						for(ModuleAttribute a : g.getAttrList()){
							ModuleAttribute attr = getDefaultModuleAttr(a);
							if(attr!=null) insAttrGroup.addAttr(attr);
							
						}
					}
					moduleInstance.addAttrGroup(insAttrGroup);
					
				}
			}
			return moduleInstance;
		}
		return null;
	}
	
	
	/**
	 * generate a instance default attribute based on module attribute, the default attribute only has value in it
	 * 
	 * @param moduleAttr
	 * @return
	 */
	public static ModuleAttribute getDefaultModuleAttr(ModuleAttribute moduleAttr){
		if(moduleAttr!=null){
			if(moduleAttr.getClass().equals(ModuleTextAttribute.class)){
				ModuleTextAttribute defaultAttr = new ModuleTextAttribute();
				defaultAttr.setUuid(UUID.randomUUID().toString());
				defaultAttr.setModuleAttrUuid(moduleAttr.getUuid());
				defaultAttr.setDefaultValue(((ModuleTextAttribute)moduleAttr).getDefaultValue());
				
				defaultAttr.setTitle(moduleAttr.getTitle());
				// put all boolean to null
//				defaultAttr.setArray(null);
//				defaultAttr.setRequired(null);
//				defaultAttr.setTextArea(null);
//				defaultAttr.setVisibility(null);
				return defaultAttr;
			}else if(moduleAttr.getClass().equals(ModuleNumberAttribute.class)){
				ModuleNumberAttribute defaultAttr = new ModuleNumberAttribute();
				defaultAttr.setUuid(UUID.randomUUID().toString());
				defaultAttr.setModuleAttrUuid(moduleAttr.getUuid());
				defaultAttr.setDefaultValue(((ModuleNumberAttribute)moduleAttr).getDefaultValue());

				defaultAttr.setTitle(moduleAttr.getTitle());

				// put all boolean to null
//				defaultAttr.setArray(null);
//				defaultAttr.setRequired(null);
//				defaultAttr.setVisibility(null);
				return defaultAttr;
				
			}else if(moduleAttr.getClass().equals(ModuleImageAttribute.class)){
				ModuleImageAttribute defaultAttr = new ModuleImageAttribute();
				defaultAttr.setUuid(UUID.randomUUID().toString());
				defaultAttr.setModuleAttrUuid(moduleAttr.getUuid());
				defaultAttr.setFileSystemName(((ModuleImageAttribute)moduleAttr).getFileSystemName());
				
				defaultAttr.setTitle(moduleAttr.getTitle());
				
				// put all boolean to null
//				defaultAttr.setArray(null);
//				defaultAttr.setRequired(null);
//				defaultAttr.setVisibility(null);
				return defaultAttr;
			}else if(moduleAttr.getClass().equals(ModuleLinkAttribute.class)){
				ModuleLinkAttribute defaultAttr = new ModuleLinkAttribute();
				defaultAttr.setUuid(UUID.randomUUID().toString());
				defaultAttr.setModuleAttrUuid(moduleAttr.getUuid());
				
				defaultAttr.setHref(((ModuleLinkAttribute)moduleAttr).getHref());
				defaultAttr.setRel(((ModuleLinkAttribute)moduleAttr).getRel());
				defaultAttr.setTarget(((ModuleLinkAttribute)moduleAttr).getTarget());
//				defaultAttr.setLinkTitle(((ModuleLinkAttribute)moduleAttr).getLinkTitle());
				defaultAttr.setLinkValue(((ModuleLinkAttribute)moduleAttr).getLinkValue());

				defaultAttr.setTitle(moduleAttr.getTitle());
				
				return defaultAttr;
			}else if(moduleAttr.getClass().equals(ModuleMoneyAttribute.class)){
				ModuleMoneyAttribute defaultAttr = new ModuleMoneyAttribute();
				defaultAttr.setUuid(UUID.randomUUID().toString());
				defaultAttr.setModuleAttrUuid(moduleAttr.getUuid());
				
				Money clonedMoney = ((ModuleMoneyAttribute)moduleAttr).getMoney().clone();
				defaultAttr.setMoney(clonedMoney);

				defaultAttr.setTitle(moduleAttr.getTitle());
				
				return defaultAttr;
			}else if(moduleAttr.getClass().equals(ModuleProductListAttribute.class)){
				ModuleProductListAttribute defaultAttr = new ModuleProductListAttribute();
				defaultAttr.setUuid(UUID.randomUUID().toString());
				defaultAttr.setModuleAttrUuid(moduleAttr.getUuid());
				
				defaultAttr.setTotalNumProductsInPage(((ModuleProductListAttribute)moduleAttr).getTotalNumProductsInPage());
				defaultAttr.setHasPagination(((ModuleProductListAttribute)moduleAttr).getHasPagination());
				
				defaultAttr.setTitle(moduleAttr.getTitle());
				
				return defaultAttr;
			}else if(moduleAttr.getClass().equals(ModuleEntityCategoryListAttribute.class)){
				ModuleEntityCategoryListAttribute defaultAttr = new ModuleEntityCategoryListAttribute();
				defaultAttr.setUuid(UUID.randomUUID().toString());
				defaultAttr.setModuleAttrUuid(moduleAttr.getUuid());
				
				defaultAttr.setCatType(((ModuleEntityCategoryListAttribute)moduleAttr).getCatType());
				defaultAttr.setSortType(((ModuleEntityCategoryListAttribute)moduleAttr).getSortType());
				defaultAttr.setLevelOfCategory(((ModuleEntityCategoryListAttribute)moduleAttr).getLevelOfCategory());
//				defaultAttr.setCatListPageuuid(((ModuleEntityCategoryListAttribute)moduleAttr).getCatListPageuuid());
//				defaultAttr.setDeskCatListPageuuid(((ModuleEntityCategoryListAttribute)moduleAttr).getDeskCatListPageuuid());
//				defaultAttr.setMobileCatListPageuuid(((ModuleEntityCategoryListAttribute)moduleAttr).getMobileCatListPageuuid());
				
				defaultAttr.setTitle(moduleAttr.getTitle());
				
				return defaultAttr;
			}
			
		}
		return null;
	}
	
	/**
	 * add extra info for instance
	 * 
	 * @param module
	 * @param instance
	 */
	public static void updateModuleInstanceByModule(Module module, Module instance){
		if(module!=null && instance!=null){
			
			// the map hold groupuuid or AttrUuid with group and attr for module
			Map<String, Object> uuidWithGroupAndAttrMap = new HashMap<String, Object>();
			if(module.getAttrGroupList()!=null && module.getAttrGroupList().size()>0){
				for(AttrGroup g : module.getAttrGroupList()){
					uuidWithGroupAndAttrMap.put(g.getGroupUuid(), g);
					if(g.getAttrList()!=null && g.getAttrList().size()>0){
						for(ModuleAttribute a : g.getAttrList()){
							uuidWithGroupAndAttrMap.put(a.getUuid(), a);
						}
					}
				}
			}
			
			// module must have group
			if(uuidWithGroupAndAttrMap.size()>0){
				if(instance.getAttrGroupList()!=null && instance.getAttrGroupList().size()>0){
					
					for(Map.Entry<String, List<AttrGroup>> entry : instance.getModulegroupuuidGroupsMap().entrySet()){
						AttrGroup moduleGroup = (AttrGroup)uuidWithGroupAndAttrMap.get(entry.getKey());
						// check exist
						if(moduleGroup!=null && entry.getValue()!=null && entry.getValue().size()>0){
							// check array
							if(moduleGroup.getArray()!=null && moduleGroup.getArray()){ // group can be duplicated
								for(AttrGroup g : entry.getValue()){
									g.setArray(Boolean.TRUE);
									g.setGroupName(moduleGroup.getGroupName());
									
									// for attr
									if(g.getAttrList()!=null && g.getAttrList().size()>0){
										Map<String, List<ModuleAttribute>> instanceGroupAttrsMap = g.getAttrsMapWithSameModuleAttrUuid();
										if(instanceGroupAttrsMap!=null && instanceGroupAttrsMap.size()>0){
											for(Map.Entry<String, List<ModuleAttribute>> attrEntry : instanceGroupAttrsMap.entrySet()){
												ModuleAttribute moduleAttr = (ModuleAttribute)uuidWithGroupAndAttrMap.get(attrEntry.getKey());
												if(moduleAttr!=null && attrEntry.getValue()!=null && attrEntry.getValue().size()>0){
													// check array
													if(moduleAttr.getArray()!=null && moduleAttr.getArray()){ // attr can be duplicated
														for(ModuleAttribute targetAttr : attrEntry.getValue()){
															updateAttr(targetAttr, moduleAttr);
														}
													}else{
														ModuleAttribute targetAttr = attrEntry.getValue().get(0);
														updateAttr(targetAttr, moduleAttr);
													}
												}
											}
										}
									}
									
									
								}
							}else{ // group can't be duplicated
								entry.getValue().get(0).setArray(Boolean.FALSE);
								entry.getValue().get(0).setGroupName(moduleGroup.getGroupName());
								
								// for attr
								if(entry.getValue().get(0).getAttrList()!=null && entry.getValue().get(0).getAttrList().size()>0){
									Map<String, List<ModuleAttribute>> instanceGroupAttrsMap = entry.getValue().get(0).getAttrsMapWithSameModuleAttrUuid();
									if(instanceGroupAttrsMap!=null && instanceGroupAttrsMap.size()>0){
										for(Map.Entry<String, List<ModuleAttribute>> attrEntry : instanceGroupAttrsMap.entrySet()){
											ModuleAttribute moduleAttr = (ModuleAttribute)uuidWithGroupAndAttrMap.get(attrEntry.getKey());
											// check exist
											if(moduleAttr!=null && attrEntry.getValue()!=null && attrEntry.getValue().size()>0){
												// check array
												if(moduleAttr.getArray()!=null && moduleAttr.getArray()){ // attr can be duplicated
													for(ModuleAttribute targetAttr : attrEntry.getValue()){
														updateAttr(targetAttr, moduleAttr);
													}
												}else{ // attr can't be duplicated
													ModuleAttribute targetAttr = attrEntry.getValue().get(0);
													updateAttr(targetAttr, moduleAttr);
													
												}
											}
											
										}
									}
									
								}
							}
							
						}
					}
					
					
				}
			}
		}
		
	}
	
	
	/**
	 * add extra info for instance: 
	 * 1) follow moduleDetail's attributes order
	 * 2) add empty attr in instance if new attr is added in moduleDetail
	 * 
	 * @param module
	 * @param instance
	 */
	public static void updateModuleInstanceByModule_v2(Module module, Module instance){
		updateModuleInstanceByModule(module, instance);

		if(module!=null && module.getAttrGroupList()!=null && instance!=null){
			
			// get groups' uuid with Idx, get attrs' uuid with Idx in moduleDetail
			Map<String, Integer> moduleGroupUuidWithIdx = new HashMap<String, Integer>();
			Map<String, Integer> moduleAttrUuidWithIdx = new HashMap<String, Integer>();
			// map to hold moduleDetail's module's attribute's uuid with groupUuid - which attribute from which group
			Map<String, String> moduleAttriUuidWithGroupUuid = new HashMap<String, String>();
			
			for(int i=0; i<module.getAttrGroupList().size(); i++){
				AttrGroup groupInModule = module.getAttrGroupList().get(i);
				moduleGroupUuidWithIdx.put(groupInModule.getGroupUuid(), Integer.valueOf(i));
				if(groupInModule.getAttrList()!=null){
					for(int j=0; j<groupInModule.getAttrList().size(); j++){
						ModuleAttribute attrInModule = groupInModule.getAttrList().get(j);
						moduleAttrUuidWithIdx.put(attrInModule.getUuid(), Integer.valueOf(j));
						moduleAttriUuidWithGroupUuid.put(attrInModule.getUuid(), groupInModule.getGroupUuid());
					}
				}
			}
			
			// the map to hold moduledetail's module's groupuuid with instance's module's groupuuid - easy to find group in instance from moduleGroupUuid.
			Map<String, String> moduleGroupUuidWithInstanceGroupUuid = new HashMap<String, String>();
			
			if(instance.getAttrGroupList()!=null){
				// re-sort instance by previous map
				for(int i=0; i<instance.getAttrGroupList().size(); i++){
					AttrGroup groupInInstance = instance.getAttrGroupList().get(i);
					
					moduleGroupUuidWithInstanceGroupUuid.put(groupInInstance.getModuleGroupUuid(), groupInInstance.getGroupUuid());
					
					Integer groupRelocateIdx = moduleGroupUuidWithIdx.get(groupInInstance.getModuleGroupUuid());
					if(groupRelocateIdx!=null){
//						relocateGroupByPosition(instance.getAttrGroupList(), i, groupRelocateIdx.intValue());
						// remove key:value pair from moduleGroupUuidWithIdx to prevent usage again
						moduleGroupUuidWithIdx.remove(groupInInstance.getModuleGroupUuid());
					}
					
					if(groupInInstance.getAttrList()!=null){
						List<ModuleAttribute> tempAttrs = new ArrayList<ModuleAttribute>(groupInInstance.getAttrList());
						
						
						for(int j=0; j<tempAttrs.size(); j++){
							ModuleAttribute attrInInstance = tempAttrs.get(j);
							Integer attrRelocateIdx = moduleAttrUuidWithIdx.get(attrInInstance.getModuleAttrUuid());
							if(attrRelocateIdx!=null){
								relocateAttrByPosition(groupInInstance.getAttrList(), attrInInstance, attrRelocateIdx);
								moduleAttrUuidWithIdx.remove(attrInInstance.getModuleAttrUuid());
							}
						}
						
					}
				}
				
			}
			
			
			// check if there have extra groupUuid with idx remaining in moduleGroupUuidWithIdx, which is meaning that
			// there have new groups added in moduleDetail.
			if(moduleGroupUuidWithIdx.size()>0){
				for(Map.Entry<String, Integer> groupUuidWithIdx : moduleGroupUuidWithIdx.entrySet()){
					String newGroupUuid = groupUuidWithIdx.getKey();
					int newGroupPosition = groupUuidWithIdx.getValue();
					
					AttrGroup newGroup = module.getGroupByUuid(newGroupUuid);
					if(newGroup!=null){
						// initial newGroup
						newGroup.setFrom_groupUuid(null);
						newGroup.setModuleGroupUuid(newGroup.getGroupUuid());
						newGroup.setGroupUuid(null);
						if(newGroup.getAttrList()!=null){
							for(ModuleAttribute newAttr : newGroup.getAttrList()){
								// remove this attrUuid from map to avoid duplicated new Attributes 
								moduleAttrUuidWithIdx.remove(newAttr.getUuid());
								
								newAttr.setFrom_attrUuid(null);
								newAttr.setModuleAttrUuid(newAttr.getUuid());
								newAttr.setUuid(null);
							}
						}
						
						if(instance.getAttrGroupList()!=null && newGroupPosition<instance.getAttrGroupList().size()){ // insert
							instance.getAttrGroupList().add(newGroupPosition, newGroup);
						}else{ // apend to the end
							instance.getAttrGroupList().add(newGroup);
						}
					}
				}
			}
			
			// check if there have extra attrUuid with idx remaining in moduleAttrUuidWithIdx, which is mean that
			// there have new attrs added in moduleDetail.
			if(moduleAttrUuidWithIdx.size()>0){
				for(Map.Entry<String, Integer> attrUuidWithIdx : moduleAttrUuidWithIdx.entrySet()){
					String newAttrUuid = attrUuidWithIdx.getKey();
					int newAttrPosition = attrUuidWithIdx.getValue();
					
					ModuleAttribute attrInModule = module.getModuleAttributeByUuid(newAttrUuid);
					if(attrInModule!=null){
						ModuleAttribute newAttr = SitedesignHelper.getDefaultModuleAttr(attrInModule);
						if(newAttr!=null){
							
							//initial newAttr
							newAttr.setFrom_attrUuid(null);
							newAttr.setUuid(null);
							newAttr.setName(attrInModule.getName());
							
							// determine this newAttr should belong to which instance's group
							String instanceGroupUuid = null;
							String moduleGroupUuid = moduleAttriUuidWithGroupUuid.get(newAttr.getModuleAttrUuid());
							if(moduleGroupUuid!=null){
								instanceGroupUuid = moduleGroupUuidWithInstanceGroupUuid.get(moduleGroupUuid);
							}
							AttrGroup targetInstanceGroup = instance.getGroupByUuid(instanceGroupUuid);
							if(targetInstanceGroup!=null){
								
								if(targetInstanceGroup.getAttrList()!=null && newAttrPosition<targetInstanceGroup.getAttrList().size()){ // insert
									targetInstanceGroup.getAttrList().add(newAttrPosition, newAttr);
								}else{ // append to the end
									targetInstanceGroup.getAttrList().add(newAttr);
								}
							}
						}
					}
					
				}
			}
			
		}
		
	}
	
//	public static void relocateGroupByPosition(List<AttrGroup> groups, int currentPosition, int relocatePosition){
//		if(groups!=null && currentPosition>-1 && relocatePosition>-1 && currentPosition!=relocatePosition){
//			// IndexOutOfBoundsException - if the index is out of range (index < 0 || index > size())
//			if(relocatePosition<groups.size()){
//				AttrGroup relocateGroup = groups.get(currentPosition);
//				groups.remove(currentPosition);
//				groups.add(relocatePosition, relocateGroup);
//			}
//		}
//	}
	
	public static void relocateAttrByPosition(List<ModuleAttribute> attrs, ModuleAttribute relocateAttr, int relocatePosition){
		if(attrs!=null && relocateAttr!=null && relocatePosition>-1){
			// IndexOutOfBoundsException - if the index is out of range (index < 0 || index > size())
			if(relocatePosition<attrs.size()){
				int currentPosition = attrs.indexOf(relocateAttr);
				if(currentPosition!=relocatePosition){
//					ModuleAttribute relocateAttr = attrs.get(currentPosition);
					attrs.remove(currentPosition);
					attrs.add(relocatePosition, relocateAttr);
				}
			}
		}
	}
	
	public static void updateAttrGroup(AttrGroup targetGroup, AttrGroup groupFromModule){
		if(targetGroup!=null && groupFromModule!=null){
			targetGroup.setArray(groupFromModule.getArray());
			targetGroup.setGroupName(groupFromModule.getGroupName());
			
			if(targetGroup.getAttrList()!=null && targetGroup.getAttrList().size()>0 && groupFromModule.getAttrList()!=null && groupFromModule.getAttrList().size()>0){
				Map<String, ModuleAttribute> moduleAttrUUidWithAttrMap = new HashMap<String, ModuleAttribute>();
				for(ModuleAttribute a : groupFromModule.getAttrList()){
					moduleAttrUUidWithAttrMap.put(a.getUuid(), a);
				}
				
				if(moduleAttrUUidWithAttrMap.size()>0){
					for(Map.Entry<String, List<ModuleAttribute>> e : targetGroup.getAttrsMapWithSameModuleAttrUuid().entrySet()){
						ModuleAttribute moduleAttr = moduleAttrUUidWithAttrMap.get(e.getKey());
						if(moduleAttr!=null && e.getValue()!=null && e.getValue().size()>0){
							if(moduleAttr.getArray()!=null && moduleAttr.getArray()){ // can duplicated
								for(ModuleAttribute a : e.getValue()){
									updateAttr(a, moduleAttr);
								}
							}else{ // can't duplicated
								updateAttr(e.getValue().get(0), moduleAttr);
							}
						}
					}
				}
			}
			
		}
	}
	
	/**
	 * add extra info for instance's attr from module's attr
	 * 
	 * @param targetAttr
	 * @param attrFromModule
	 */
	public static void updateAttr(ModuleAttribute targetAttr, ModuleAttribute attrFromModule){
		if(targetAttr!=null && attrFromModule!=null){
			
			ModuleAttribute moduleAttr = attrFromModule;
			
			if(moduleAttr!=null){
				targetAttr.setArray(moduleAttr.getArray());
				targetAttr.setDocumentation(moduleAttr.getDocumentation());
				targetAttr.setName(moduleAttr.getName());
				targetAttr.setRequired(moduleAttr.getRequired());
//				targetAttr.setTitle(moduleAttr.getTitle());
				targetAttr.setType(moduleAttr.getType());
				targetAttr.setEditable(moduleAttr.getEditable());
				
				if(targetAttr.getClass().equals(ModuleTextAttribute.class)){
					moduleAttr = (ModuleTextAttribute)attrFromModule;
					((ModuleTextAttribute)targetAttr).setMaxLength(((ModuleTextAttribute)moduleAttr).getMaxLength());
					((ModuleTextAttribute)targetAttr).setMinLength(((ModuleTextAttribute)moduleAttr).getMinLength());
					((ModuleTextAttribute)targetAttr).setTextArea(((ModuleTextAttribute)moduleAttr).getTextArea());
				}else if(targetAttr.getClass().equals(ModuleNumberAttribute.class)){
					moduleAttr = (ModuleNumberAttribute)attrFromModule;
					((ModuleNumberAttribute)targetAttr).setMaxValue(((ModuleNumberAttribute)moduleAttr).getMaxValue());
					((ModuleNumberAttribute)targetAttr).setMinValue(((ModuleNumberAttribute)moduleAttr).getMinValue());
					((ModuleNumberAttribute)targetAttr).setScale(((ModuleNumberAttribute)moduleAttr).getScale());
				}else if(targetAttr.getClass().equals(ModuleImageAttribute.class)){
					moduleAttr = (ModuleImageAttribute)attrFromModule;
					((ModuleImageAttribute)targetAttr).setDefaultPicture(((ModuleImageAttribute)moduleAttr).getDefaultPicture());
				}else if(targetAttr.getClass().equals(ModuleLinkAttribute.class)){
					// nothing to add
				}else if(targetAttr.getClass().equals(ModuleMoneyAttribute.class)){
					// nothing to add
				}else if(targetAttr.getClass().equals(ModuleProductListAttribute.class)){
					// nothing to add
				}else if(targetAttr.getClass().equals(ModuleEntityCategoryListAttribute.class)){
					// nothing to add
				}
			}
		}
	}
	
	/**
	 * 
	 * remove instance's attr's extra info, only value remaining.
	 * @param attr
	 */
	public static void removeAttrExtraInfo(ModuleAttribute attr){
		if(attr!=null){
			attr.setArray(null);
			attr.setDocumentation(null);
			attr.setName(null);
			attr.setRequired(null);
//			attr.setTitle(null);
			attr.setType(null);
			attr.setEditable(null);
			
			if(attr.getClass().equals(ModuleTextAttribute.class)){
				((ModuleTextAttribute)attr).setMaxLength(null);
				((ModuleTextAttribute)attr).setMinLength(null);
				((ModuleTextAttribute)attr).setTextArea(null);
			}else if(attr.getClass().equals(ModuleNumberAttribute.class)){
				((ModuleNumberAttribute)attr).setMaxValue(null);
				((ModuleNumberAttribute)attr).setMinValue(null);
				((ModuleNumberAttribute)attr).setScale(null);
			}else if(attr.getClass().equals(ModuleImageAttribute.class)){
				((ModuleImageAttribute)attr).setDefaultPicture(null);
			}else if(attr.getClass().equals(ModuleLinkAttribute.class)){
				// nothing to set
			}else if(attr.getClass().equals(ModuleMoneyAttribute.class)){
				// nothing to set
			}else if(attr.getClass().equals(ModuleProductListAttribute.class)){
				// nothing to set
			}else if(attr.getClass().equals(ModuleEntityCategoryListAttribute.class)){
				// nothing to set
			}
		}
		
	}
	
	
	/**
	 * @param orgId
	 * @return a new generic product moduledetail for organization
	 */
	public static ModuleDetail newProductModule(Long orgId){
		if(orgId!=null){
			ModuleDetail prodModule = new ModuleDetail(-1l, 
					ModuleDetail.Type.productModule.name(), 
					ModuleDetail.Type.productModule.getDesc(), 
					null, 
					ModuleDetail.Type.productModule.getCode(), 
					null, 
					null, 
					null, null, null, orgId, null, null, ModuleDetail.Visibility.visibleInsideOrg.getCode(), new Date(), 
					Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
			
			return prodModule;
		}
		
		return null;
	}
	
	/**
	 * @param fromInstance
	 * @return copied instance from fromInstance
	 */
	public static String duplicateInstance(String fromInstance){
		if(StringUtils.isNotBlank(fromInstance)){
			Module instance = getModuleFromXml(fromInstance);
			if(instance!=null && instance.getAttrGroupList()!=null){
				for(AttrGroup g : instance.getAttrGroupList()){
					g.setGroupUuid(UUID.randomUUID().toString());
					if(g.getAttrList()!=null){
						for(ModuleAttribute a : g.getAttrList()){
							a.setUuid(UUID.randomUUID().toString());
						}
					}
				}
			}
			
			if(instance!=null){
				return getXmlFromModule(instance);
			}
		}
		return null;
	}
	
	public static String getXmlFromMetaData(MetaData meta){
		if(meta!=null){
			XStream stream = new XStream(new DomDriver());
			stream.processAnnotations(new Class[]{ArrayList.class, HashMap.class, MetaData.class});
			StringWriter sw = new StringWriter();
			stream.marshal(meta, new CompactWriter(sw));
			return sw.toString();
		}
		return null;
	}
	
	public static MetaData getMetaDataFromXml(String xml){
		if(StringUtils.isNotBlank(xml)){
			XStream stream = new XStream(new DomDriver());
			stream.processAnnotations(new Class[]{ArrayList.class, HashMap.class, MetaData.class});
			return (MetaData)stream.fromXML(xml);
		}
		return null;
	}
	
	/**
	 * @param containerListInPageDetail
	 * @return all containerDetails' uuids from pagedetail's detail. All containers in pagedetail's detail are published!!! 
	 */
	public static List<String> findAllContainerUuidsFromPageDetail(String containerListInPageDetail) {
		
		if(StringUtils.isNotBlank(containerListInPageDetail)){
			
			 List<String> uuids = new ArrayList<String>();
			 
			 int fromIndex = 0;
			 int idx_begin = containerListInPageDetail.indexOf(ContainerToPageDetailConvertor.SYSNAME+"=", fromIndex);
			 while (idx_begin>-1) {
				 idx_begin = idx_begin + 9;
				 int idx_end = containerListInPageDetail.indexOf("\"", idx_begin);
				 if(idx_end>-1){
					 String uuid = containerListInPageDetail.substring(idx_begin, idx_end);
					 
					 uuids.add(uuid);
					 
					 idx_begin = containerListInPageDetail.indexOf(ContainerToPageDetailConvertor.SYSNAME+"=", idx_end);
				 }
			 }
			 
			 if(uuids.size()>0) return uuids;
		}
		
		return null;
	}
	
	/**
	 * @param attr
	 * @return a list of validation information. ie, required -> value can't be null, maxlength, minLength, ...
	 */
	public static List<String> moduleAttributeValidation(ModuleAttribute attr){
		if(attr!=null && StringUtils.isNotBlank(attr.getUuid())){
			List<String> validationInfos = new ArrayList<String>();
			
			// for requied:
			if(attr.getRequired()!=null && attr.getRequired()){
				if(attr.getType().equals(ModuleLinkAttribute.class.getSimpleName())){
					if(StringUtils.isBlank(((ModuleLinkAttribute)attr).getHref())){
						validationInfos.add("href is requied!");
					}
					if(StringUtils.isBlank(((ModuleLinkAttribute)attr).getLinkValue())){
						validationInfos.add("link value is requied!");
					}
					
				}else if(attr.getType().equals(ModuleMoneyAttribute.class.getSimpleName())){
					if(((ModuleMoneyAttribute)attr).getMoney()==null || ((ModuleMoneyAttribute)attr).getMoney().getAmount()==null){
						validationInfos.add("amount is requied!");
					}
					
				}else if(attr.getType().equals(ModuleNumberAttribute.class.getSimpleName())){
					if(((ModuleNumberAttribute)attr).getValue()==null){
						validationInfos.add("value is requied!");
					}
				}else if(attr.getType().equals(ModuleImageAttribute.class.getSimpleName())){
					if(StringUtils.isBlank(((ModuleImageAttribute)attr).getValue())){
						validationInfos.add("image is required!");
					}
				}else if(attr.getType().equals(ModuleProductListAttribute.class.getSimpleName())){
					
				}else if(attr.getType().equals(ModuleTextAttribute.class.getSimpleName())){
					if(StringUtils.isBlank(((ModuleTextAttribute)attr).getValue())){
						validationInfos.add("value is required!");
					}
				}else if(attr.getType().equals(ModuleEntityCategoryListAttribute.class.getSimpleName())){
					
				}
			}
			
			// for number's min, max, scale
			if(attr.getType().equals(ModuleNumberAttribute.class.getSimpleName())){
				// min:
				if(((ModuleNumberAttribute)attr).getMinValue()!=null 
						&& ((ModuleNumberAttribute)attr).getMinValue().compareTo(new BigDecimal(0))>=0){
					if(((ModuleNumberAttribute)attr).getValue()==null){
						validationInfos.add("minimal valule is "+((ModuleNumberAttribute)attr).getMinValue());
					}else{
						if(((ModuleNumberAttribute)attr).getValue().compareTo(((ModuleNumberAttribute)attr).getMinValue())<0){
							validationInfos.add("minimal valule is "+((ModuleNumberAttribute)attr).getMinValue());
						}
					}
				}
				// max:
				if(((ModuleNumberAttribute)attr).getMaxValue()!=null 
						&& ((ModuleNumberAttribute)attr).getMaxValue().compareTo(new BigDecimal(0))>=0){
					if(((ModuleNumberAttribute)attr).getValue()!=null 
						&& ((ModuleNumberAttribute)attr).getValue().compareTo(((ModuleNumberAttribute)attr).getMaxValue())>0){
						validationInfos.add("maximal value is "+((ModuleNumberAttribute)attr).getMaxValue());
					}
				}
				// scale
				if(((ModuleNumberAttribute)attr).getScale()!=null && ((ModuleNumberAttribute)attr).getScale().intValue()>=0){
					if(((ModuleNumberAttribute)attr).getValue()!=null && ((ModuleNumberAttribute)attr).getValue().scale()>((ModuleNumberAttribute)attr).getScale().intValue()){
						validationInfos.add("the scale for the value is "+((ModuleNumberAttribute)attr).getScale());
					}
				}
				
			}
			
			// for text's min, max
			if(attr.getType().equals(ModuleTextAttribute.class.getSimpleName())){
				int currentValueLength = 0;
				if(((ModuleTextAttribute)attr).getValue()!=null){
					currentValueLength = ((ModuleTextAttribute)attr).getValue().length();
				}
				
				// min:
				if(((ModuleTextAttribute)attr).getMinLength()!=null && ((ModuleTextAttribute)attr).getMinLength().intValue()>0){
					if(currentValueLength<((ModuleTextAttribute)attr).getMinLength().intValue()){
						validationInfos.add("minimal length for value is "+((ModuleTextAttribute)attr).getMinLength()+", your value is "+currentValueLength+" characters.");
					}
				}
				
				// max:
				if(((ModuleTextAttribute)attr).getMaxLength()!=null 
					&& ((ModuleTextAttribute)attr).getMaxLength().intValue()>0){
					if(currentValueLength>((ModuleTextAttribute)attr).getMaxLength().intValue()){
						validationInfos.add("maximal length for value is "+((ModuleTextAttribute)attr).getMaxLength()+", your value is "+currentValueLength+" characters.");
					}
				}
				
				
			}
			
			// for href 
			if(attr.getType().equals(ModuleLinkAttribute.class.getSimpleName())){
				if(StringUtils.isNotBlank(((ModuleLinkAttribute)attr).getHref())){
					boolean isUrl = ValidationSet.isUrl(((ModuleLinkAttribute)attr).getHref());
					if(!isUrl){
						validationInfos.add("URL malformatted for href");
					}
				}
			}
			
			
			if(validationInfos.size()>0) return validationInfos;
			
		}
		
		return null;
	}
	
	
	public static String transferStringIntoHtmlFormat(String str){
		if(str!=null){
			String escapedHtmlFormatString = StringEscapeUtils.escapeHtml(str);
			
			StringWriter out = new StringWriter(escapedHtmlFormatString.length() * 2);
	        int sz;
	        sz = escapedHtmlFormatString.length();
	        for (int i = 0; i < sz; i++) {
	            char ch = escapedHtmlFormatString.charAt(i);
	            if (ch < 32) {
	                switch (ch) {
//	                    case '\b' : // backspace
//	                        out.write('\\');
//	                        out.write('b');
//	                        break;
	                    case '\n' : // newline
	                        out.write("<br/>");
	                        break;
	                    case '\t' : // tab
	                        out.write("&nbsp;");
	                        break;
//	                    case '\f' : // formfeed
//	                        out.write('\\');
//	                        out.write('f');
//	                        break;
	                    case '\r' : // carriage
	                        out.write("<br/>");
	                        break;
	                    default :
	                        out.write(ch);
	                        break;
	                }
	            }else if(ch == 32){ // space
	            	out.write("&nbsp;");
	            }else{
	            	out.write(ch);
	            }
	        }
	        
	        return out.toString();
		}
		return null;
	}
	
	public static PageDetail createFakePage(Organization org){
		if(org!=null){
			Date now = new Date();
			PageDetail fakepage = new PageDetail();
			
			fakepage.setCreatedate(now);
			fakepage.setCreator_id(Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
//			fakepage.setDetail(detail);
			fakepage.setId(Long.valueOf("888888888888"));
			fakepage.setOrganization_id(org.getId());
			fakepage.setPageuuid(PageDetail.FAKEPAGEUUID);
			fakepage.setPrettyname("fake page");
			fakepage.setType(PageDetail.Type.Desktop.getCode());
			fakepage.setUrl("#");
			
			return fakepage;
			
		}
		return null;
	}
	
}
