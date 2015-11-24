package com.bizislife.core.siteDesign.module;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.bizislife.core.entity.converter.ModuleConverter;
import com.bizislife.core.entity.converter.ModuleAttrGroupConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("module")
@XStreamConverter(ModuleConverter.class)
public class Module implements Serializable{
	
	@XStreamAlias("attrGroup")
	@XStreamConverter(ModuleAttrGroupConverter.class)
	public static class AttrGroup implements Serializable{
		private static final long serialVersionUID = -5428158876631669280L;
		
		private String moduleGroupUuid; // used for instance, this uuid is groupUuid from module, which is indicated where this group instance from.  
		
		private String groupUuid;
		private String from_groupUuid; // used in copy or clone situation: from which group
		private String groupName; // used to identify which group
		private Boolean array;
		@XStreamImplicit
		private List<ModuleAttribute> attrs; // flat list of all attrs
		@XStreamOmitField
		private Map<String, List<ModuleAttribute>> attrsMap; // map for the attr name and attrs with same name
		@XStreamOmitField
		private Map<String, List<ModuleAttribute>> attrsMapWithSameModuleAttrUuid; // map for the attr's moduleAttrUuid and attrs
		
		@XStreamOmitField
		private Map<String, String> thisThatUuids; // the map to hold this & that uuids for groups and attrs when run equal method.
		
		public Boolean getArray() {
			return array;
		}
		public void setArray(Boolean array) {
			this.array = array;
		}
		
		@JsonIgnore
		public List<ModuleAttribute> getAttrList() {
			return attrs;
		}
		
		/**
		 * the purpose of this function is to group array of attributes together.
		 * @return map of attrName with List of attributes
		 */
		public Map<String, List<ModuleAttribute>> getAttrs() {
			
			attrsMap = new LinkedHashMap<String, List<ModuleAttribute>>();
			if(attrs!=null && attrs.size()>0){
				for(ModuleAttribute a : attrs){
					if(attrsMap.get(a.getName())==null){
						List<ModuleAttribute> mas = new ArrayList<ModuleAttribute>();
						mas.add(a);
						attrsMap.put(a.getName(), mas);
					}else{
						attrsMap.get(a.getName()).add(a);
					}
				}
			}
			
			return attrsMap;
		}
		
		/**
		 * @return map for the attr's moduleAttrUuid and attrs
		 */
		@JsonIgnore
		public Map<String, List<ModuleAttribute>> getAttrsMapWithSameModuleAttrUuid() {
			attrsMapWithSameModuleAttrUuid = new LinkedHashMap<String, List<ModuleAttribute>>();
			if(attrs!=null && attrs.size()>0){
				for(ModuleAttribute a : attrs){
					if(StringUtils.isNotBlank(a.getModuleAttrUuid())){ // instance only
						if(attrsMapWithSameModuleAttrUuid.get(a.getModuleAttrUuid())==null){
							List<ModuleAttribute> mas = new ArrayList<ModuleAttribute>();
							mas.add(a);
							attrsMapWithSameModuleAttrUuid.put(a.getModuleAttrUuid(), mas);
						}else{
							attrsMapWithSameModuleAttrUuid.get(a.getModuleAttrUuid()).add(a);
						}
					}
				}
			}
			
			return attrsMapWithSameModuleAttrUuid;
		}
		
		public void addAttr(ModuleAttribute attr) {
			// add to the list
			if(attrs==null) attrs = new ArrayList<ModuleAttribute>();
			
			// just add without check isArray
			attrs.add(attr);
			
//			if(attr.getArray()){ // just add if attr isArray
//				attrs.add(attr);
//			}else{ // do duplicate check
//				boolean exist = false;
//				for(ModuleAttribute p : attrs){
//					if(p.getName().trim().equals(attr.getName().trim())){
//						exist = true;
//						break;
//					}
//				}
//				if(!exist){
//					attrs.add(attr);
//				}
//			}
			
		}
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}
		
		@JsonIgnore
		public String getModuleGroupUuid() {
			return moduleGroupUuid;
		}
		public void setModuleGroupUuid(String moduleGroupUuid) {
			this.moduleGroupUuid = moduleGroupUuid;
		}
		
		public String getGroupUuid() {
			return groupUuid;
		}
		public void setGroupUuid(String groupUuid) {
			this.groupUuid = groupUuid;
		}
		@JsonIgnore
		public String getFrom_groupUuid() {
			return from_groupUuid;
		}
		public void setFrom_groupUuid(String from_groupUuid) {
			this.from_groupUuid = from_groupUuid;
		}
		
		public ModuleAttribute getModuleAttributeByUuid(String attrUuid){
			if(StringUtils.isNotBlank(attrUuid) && this.attrs!=null && this.attrs.size()>0){
				for(ModuleAttribute a : this.attrs){
					if(a.getUuid().equals(attrUuid)){
						return a;
					}
				}
			}
			return null;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("AttrGroup [groupUuid=");
			builder.append(groupUuid);
			builder.append(", groupName=");
			builder.append(groupName);
			builder.append(", array=");
			builder.append(array);
			builder.append(", attrs=");
			builder.append(attrs);
			builder.append(", attrsMap=");
			builder.append(attrsMap);
			builder.append("]");
			return builder.toString();
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((array == null) ? 0 : array.hashCode());
			result = prime * result + ((attrs == null) ? 0 : attrs.hashCode());
			result = prime * result
					+ ((groupName == null) ? 0 : groupName.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			
			if (obj == null)
				return false;
			
			if (getClass() != obj.getClass())
				return false;
			
			AttrGroup other = (AttrGroup) obj;
			
			if (array == null) {
				if (other.array != null)
					return false;
			} else if (!array.equals(other.array))
				return false;
			
			if (groupName == null) {
				if (other.groupName != null)
					return false;
			} else if (!groupName.equals(other.groupName))
				return false;
			
			if (attrs == null) {
				if (other.attrs != null)
					return false;
			} else{
				if(other.attrs==null){
					return false;
				}else{
					if(attrs.size()!=other.attrs.size()){
						return false;
					}else{
						// compare two list 
						List<String> usedAttrUuidInOtherGroup = new ArrayList<String>();
						boolean findSameAttr = false;
						for(ModuleAttribute a : attrs){
							findSameAttr = false;
							for(ModuleAttribute oa : other.attrs){
								// check if oa.getuuid is already compared, and find same attr in this.attrs
								if(!usedAttrUuidInOtherGroup.contains(oa.getUuid())){
									if(a.equals(oa)){
										usedAttrUuidInOtherGroup.add(oa.getUuid());
										findSameAttr = true;
										
										addThisThatUuids(a.getUuid(), oa.getUuid());
										break;
									}
								}
							}
							if(!findSameAttr){
								return false;
							}
						}
					}
				}
			}
			
			return true;
		}
		@JsonIgnore
		public Map<String, String> getThisThatUuids() {
			return thisThatUuids;
		}
		private void addThisThatUuids(String thisUuid, String thatUuid){
			if(thisThatUuids==null) thisThatUuids = new HashMap<String, String>();
			thisThatUuids.put(thisUuid, thatUuid);
		}
		
	}
	
	private static final long serialVersionUID = -4149818214035137171L;
	
	@XStreamImplicit
	private List<AttrGroup> attrGroups; // flat list to hold all groups
	
	@XStreamOmitField
	private Map<String, List<AttrGroup>> nameGroupsMap; // the map to hold groupName with groupList. groups with the same name will put into one list.  
	
	@XStreamOmitField
	private Map<String, List<AttrGroup>> modulegroupuuidGroupsMap; // the map to hold moduleGroupUuid with groupList
	
	@XStreamOmitField
	private Map<String, String> thisThatUuids; // the map to hold this & that uuids for groups and attrs when run equal method.
	
	// some fields from entitydetail / moduleinstance
	@XStreamOmitField
	private String systemName;
	@XStreamOmitField
	private String prettyName;
	@XStreamOmitField
	private String type;

	@JsonIgnore
	public List<AttrGroup> getAttrGroupList() {
		return attrGroups;
	}

//	public void setAttrGroups(List<AttrGroup> attrGroups) {
//		this.attrGroups = attrGroups;
//	}
	
	/**
	 * @return the map to hold groupName with groupList. groups with the same name will put into one list.
	 */
	public Map<String, List<AttrGroup>> getGroups() {
		nameGroupsMap = new LinkedHashMap<String, List<AttrGroup>>();
		if(attrGroups!=null && attrGroups.size()>0){
			for(AttrGroup g : attrGroups){
				if(nameGroupsMap.get(g.getGroupName())==null){
					List<AttrGroup> ags = new ArrayList<Module.AttrGroup>();
					ags.add(g);
					nameGroupsMap.put(g.getGroupName(), ags);
				}else{
					nameGroupsMap.get(g.getGroupName()).add(g);
				}
			}
		}
		
		return nameGroupsMap;
	}

	/**
	 * @return the map to hold moduleGroupUuid with groupList
	 */
	@JsonIgnore
	public Map<String, List<AttrGroup>> getModulegroupuuidGroupsMap() {
		modulegroupuuidGroupsMap = new LinkedHashMap<String, List<AttrGroup>>();
		if(attrGroups!=null && attrGroups.size()>0){
			for(AttrGroup g : attrGroups){
				if(StringUtils.isNotBlank(g.getModuleGroupUuid())){ // for instance only
					if(modulegroupuuidGroupsMap.get(g.getModuleGroupUuid())==null){
						List<AttrGroup> ags = new ArrayList<Module.AttrGroup>();
						ags.add(g);
						modulegroupuuidGroupsMap.put(g.getModuleGroupUuid(), ags);
					}else{
						modulegroupuuidGroupsMap.get(g.getModuleGroupUuid()).add(g);
					}
				}
			}
		}
		
		return modulegroupuuidGroupsMap;
	}

	public void addAttrGroup(AttrGroup attrGroup){
		if(attrGroups==null) attrGroups = new ArrayList<Module.AttrGroup>();

		// just add without isAttry check
		attrGroups.add(attrGroup);
		
		// check is array group
//		if(attrGroup.getArray()){ // if new group isArray, just add
//			attrGroups.add(attrGroup);
//		}else{ // do check no duplicate.
//			boolean exist = false;
//			for(AttrGroup g : attrGroups){
//				if(g.getGroupName().trim().equalsIgnoreCase(attrGroup.getGroupName().trim())){
//					exist = true;
//					break;
//				}
//			}
//			if(!exist){
//				attrGroups.add(attrGroup);
//			}
//		}
		
	}
	
	public AttrGroup getGroupByUuid(String groupUuid){
		if(StringUtils.isNotBlank(groupUuid) && this.attrGroups!=null && this.attrGroups.size()>0){
			for(AttrGroup g : this.attrGroups){
				if(g.getGroupUuid().equals(groupUuid)){
					return g;
				}
			}
		}
		
		return null;
	}
	
	public ModuleAttribute getModuleAttributeByUuid(String attrUuid){
		if(StringUtils.isNotBlank(attrUuid) && this.attrGroups!=null && this.attrGroups.size()>0){
			for(AttrGroup g : this.attrGroups){
				if(g.getAttrList()!=null && g.getAttrList().size()>0){
					for(ModuleAttribute a : g.getAttrList()){
						if(a.getUuid().equals(attrUuid)){
							return a;
						}
					}
				}
			}
		}
		
		return null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Module [attrGroups=").append(attrGroups).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attrGroups == null) ? 0 : attrGroups.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Module other = (Module) obj;
		
		if (attrGroups == null) {
			if (other.attrGroups != null)
				return false;
		}else{
			if(other.attrGroups==null){
				return false;
			}else{
				if(attrGroups.size()!=other.attrGroups.size()){
					return false;
				}else{
					// compare two list 
					List<String> usedGroupUuidInOtherModule = new ArrayList<String>();
					boolean findSameGroup = false;
					for(AttrGroup g : attrGroups){
						findSameGroup = false;
						for(AttrGroup og : other.attrGroups){
							if(!usedGroupUuidInOtherModule.contains(og.getGroupUuid())){
								if(g.equals(og)){
									usedGroupUuidInOtherModule.add(og.getGroupUuid());
									findSameGroup = true;
									
									addThisThatUuids(g.getGroupUuid(), og.getGroupUuid());
									
									if(g.getThisThatUuids()!=null && g.getThisThatUuids().size()>0){
										for(Map.Entry<String, String> en : g.getThisThatUuids().entrySet()){
											addThisThatUuids(en.getKey(), en.getValue());
										}
									}
									
									break;
								}
								
							}
						}
						if(!findSameGroup){
							return false;
						}
					}
				}
			}
		}
		
		return true;
	}

	@JsonIgnore
	public Map<String, String> getThisThatUuids() {
		return thisThatUuids;
	}
	private void addThisThatUuids(String thisUuid, String thatUuid){
		if(thisThatUuids==null) thisThatUuids = new HashMap<String, String>();
		thisThatUuids.put(thisUuid, thatUuid);
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getPrettyName() {
		return prettyName;
	}

	public void setPrettyName(String prettyName) {
		this.prettyName = prettyName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
