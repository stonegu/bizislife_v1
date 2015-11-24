package com.bizislife.core.entity.converter;

import com.bizislife.core.siteDesign.module.Module;
import com.bizislife.core.siteDesign.module.ModuleAttribute;
import com.bizislife.core.siteDesign.module.ModuleStringAttribute;
import com.bizislife.core.siteDesign.module.Module.AttrGroup;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ModuleConverter implements Converter{

	@Override
	public boolean canConvert(Class type) {
		return type.equals(Module.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Module module = (Module)source;
		if(module.getAttrGroupList()!=null && module.getAttrGroupList().size()>0){
			for(AttrGroup g : module.getAttrGroupList()){
				writer.startNode("attrGroup");
				context.convertAnother(g);
				writer.endNode();
			}
		}
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Module module = new Module();
		
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if("attrGroup".equals(reader.getNodeName())){
				AttrGroup group = (AttrGroup)context.convertAnother(module, AttrGroup.class);
				if(group!=null) module.addAttrGroup(group);
			}
			reader.moveUp();
		}
		
		return module;
	}

}
