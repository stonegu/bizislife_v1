package com.bizislife.core.entity.converter;

import org.apache.commons.lang.StringUtils;

import com.bizislife.core.entity.Attribute;
import com.bizislife.core.entity.AttributeAdapter;
import com.bizislife.core.entity.EntityAdaper;
import com.bizislife.core.entity.FolderEntity;
import com.bizislife.core.entity.ImgAttribute;
import com.bizislife.core.entity.IntegerAttribute;
import com.bizislife.core.entity.ProductEntity;
import com.bizislife.core.entity.RealAttribute;
import com.bizislife.core.entity.StringAttribute;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class EntityConverter implements Converter{

	@Override
	public boolean canConvert(Class type) {
		
		// let's convert anything which extends EntityAdaper: 
		// 		means if instances of class type can be assigned to the EntityAdaper class, they extends the abstract class EntityAdaper:
		return EntityAdaper.class.isAssignableFrom(type);
		
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		
		EntityAdaper entity = null;
		if(source.getClass().equals(FolderEntity.class)){
			entity = (FolderEntity) source;
			writer.addAttribute("type", "FolderEntity");
		}else if(source.getClass().equals(ProductEntity.class)){
			entity = (ProductEntity) source;
			writer.addAttribute("type", "ProductEntity");
		}
		
		if(entity.getAllAttributes()!=null && entity.getAllAttributes().size()>0){
			for(Attribute attr : entity.getAllAttributes()){
				writer.startNode("attr");
				context.convertAnother(attr);
				writer.endNode();
			}
		}
		
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		
		EntityAdaper entity = null;
		String type = reader.getAttribute("type");
		if(StringUtils.isNotBlank(type)){
			if(FolderEntity.class.getSimpleName().equals(type)){
				entity = new FolderEntity();
			}else if(ProductEntity.class.getSimpleName().equals(type)){
				entity = new ProductEntity();
			}
			
			while (reader.hasMoreChildren()) {
				reader.moveDown();
				if("attr".equals(reader.getNodeName())){
					String attrType = reader.getAttribute("type");
					Attribute attr = null;
					if(RealAttribute.class.getSimpleName().equals(attrType)){
						attr = (RealAttribute)context.convertAnother(entity, RealAttribute.class);
					}else if(IntegerAttribute.class.getSimpleName().equals(attrType)){
						attr = (IntegerAttribute)context.convertAnother(entity, IntegerAttribute.class);
					}else if(StringAttribute.class.getSimpleName().equals(attrType)){
						attr = (StringAttribute)context.convertAnother(entity, StringAttribute.class);
					}else if(ImgAttribute.class.getSimpleName().equals(attrType)){
						attr = (ImgAttribute)context.convertAnother(entity, ImgAttribute.class);
					}
					if(attr!=null){
						entity.addAttribute(attr);
					}
				}
				reader.moveUp();
			}
			
			return entity;
			
		}

		return null;
	}

}
