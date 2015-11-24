package com.bizislife.core.entity.converter;

import org.apache.commons.lang.StringUtils;

import com.bizislife.core.entity.RealAttribute;
import com.bizislife.core.siteDesign.module.Module.AttrGroup;
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
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ModuleAttrGroupConverter implements Converter{

	@Override
	public boolean canConvert(Class type) {
		return type.equals(AttrGroup.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		AttrGroup attrGroup = (AttrGroup)source;
		
		if(StringUtils.isNotBlank(attrGroup.getGroupName())){
			writer.addAttribute("name", attrGroup.getGroupName());
		}
		writer.addAttribute("uuid", attrGroup.getGroupUuid());
		
//		writer.startNode("groupName");
//		writer.setValue(attrGroup.getGroupName());
//		writer.endNode();
		
		if(StringUtils.isNotBlank(attrGroup.getFrom_groupUuid())){
			writer.startNode("from_groupUuid");
			writer.setValue(attrGroup.getFrom_groupUuid());
			writer.endNode();
		}
		
		if(attrGroup.getArray()!=null){
			writer.startNode("array");
			writer.setValue(attrGroup.getArray().toString());
			writer.endNode();
		}
		
		if(StringUtils.isNotBlank(attrGroup.getModuleGroupUuid())){
			writer.startNode("moduleGroupUuid");
			writer.setValue(attrGroup.getModuleGroupUuid());
			writer.endNode();
		}
		
		if(attrGroup.getAttrList()!=null && attrGroup.getAttrList().size()>0){
			for(ModuleAttribute p : attrGroup.getAttrList()){
				writer.startNode("attr");
				context.convertAnother(p);
				writer.endNode();
			}
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		
		AttrGroup group = new AttrGroup();
		
		String name = reader.getAttribute("name");
		String uuid = reader.getAttribute("uuid");
		if(StringUtils.isNotBlank(uuid)){
			if(name!=null){
				group.setGroupName(name.trim());
			}
			group.setGroupUuid(uuid);
			while (reader.hasMoreChildren()) {
				reader.moveDown();
//				if("groupName".equals(reader.getNodeName())){
//					group.setGroupName(reader.getValue());
//				}else 
				if("array".equals(reader.getNodeName())){
					// array will be true only if reader.getValue()=="true", otherwise array will be false
					group.setArray(reader.getValue()!=null?Boolean.valueOf(reader.getValue().equalsIgnoreCase("true")?"true":"false"):null);
				}
				else if("moduleGroupUuid".equals(reader.getNodeName())){
					group.setModuleGroupUuid(reader.getValue());
				}
				else if("from_groupUuid".equals(reader.getNodeName())){
					group.setFrom_groupUuid(reader.getValue());
				}
				else if("attr".equals(reader.getNodeName())){
					String attrType = reader.getAttribute("type");
					ModuleAttribute attr = null;
					if(ModuleStringAttribute.class.getSimpleName().equals(attrType)){
						attr = (ModuleAttribute)context.convertAnother(group, ModuleStringAttribute.class);
					}else if(ModuleIntegerAttribute.class.getSimpleName().equals(attrType)){
						attr = (ModuleAttribute)context.convertAnother(group, ModuleIntegerAttribute.class);
					}else if(ModuleTextAttribute.class.getSimpleName().equals(attrType)){
						attr = (ModuleAttribute)context.convertAnother(group, ModuleTextAttribute.class);
					}else if(ModuleNumberAttribute.class.getSimpleName().equals(attrType)){
						attr = (ModuleAttribute)context.convertAnother(group, ModuleNumberAttribute.class);
					}else if(ModuleImageAttribute.class.getSimpleName().equals(attrType)){
						attr = (ModuleAttribute)context.convertAnother(group, ModuleImageAttribute.class);
					}else if(ModuleLinkAttribute.class.getSimpleName().equals(attrType)){
						attr = (ModuleAttribute)context.convertAnother(group, ModuleLinkAttribute.class);
					}else if(ModuleMoneyAttribute.class.getSimpleName().equals(attrType)){
						attr = (ModuleAttribute)context.convertAnother(group, ModuleMoneyAttribute.class);
					}else if(ModuleProductListAttribute.class.getSimpleName().equals(attrType)){
						attr = (ModuleProductListAttribute)context.convertAnother(group, ModuleProductListAttribute.class);
					}else if(ModuleEntityCategoryListAttribute.class.getSimpleName().equals(attrType)){
						attr = (ModuleEntityCategoryListAttribute)context.convertAnother(group, ModuleEntityCategoryListAttribute.class);
					}
					if(attr!=null) group.addAttr(attr);
				}
				reader.moveUp();
			}
			
		}

		
		return group;
	}

}
