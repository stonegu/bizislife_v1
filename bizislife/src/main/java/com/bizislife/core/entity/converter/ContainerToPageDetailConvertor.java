package com.bizislife.core.entity.converter;

import java.math.BigDecimal;

import org.apache.commons.lang.math.NumberUtils;

import com.bizislife.core.controller.component.ContainerTreeNode;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ContainerToPageDetailConvertor implements Converter{
	
	private static int DECIMALS = 3;
	private static int ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;
	
	public static final String SYSNAME = "sysName";

	@Override
	public boolean canConvert(Class type) {
		return type.equals(ContainerTreeNode.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		ContainerTreeNode ctNode = (ContainerTreeNode)source;
		//writer.startNode("node");
		
		writer.addAttribute(SYSNAME, ctNode.getSystemName());
		
		if(ctNode.getPrettyName()!=null){
			writer.startNode("pretyName");
			writer.setValue(ctNode.getPrettyName());
			writer.endNode();
		}
		
		if(ctNode.getCssClassInfos()!=null){
			writer.startNode("cssClasses");
			writer.setValue(ctNode.getCssClassInfos());
			writer.endNode();
		}
		
		if(ctNode.getDirection()!=null){
			writer.startNode("direction");
			writer.setValue(ctNode.getDirection());
			writer.endNode();
		}
		
		if(ctNode.getDomvalue()!=null){
			writer.startNode("domvalue");
			writer.setValue(ctNode.getDomvalue());
			writer.endNode();
		}
		
		if(ctNode.getHeight()!=null){
			writer.startNode("height");
			writer.setValue(ctNode.getHeight().toString());
			writer.endNode();
		}
		
		if(ctNode.getLeftposition()!=null){
			writer.startNode("leftPosition");
			writer.setValue(ctNode.getLeftposition().toString());
			writer.endNode();
		}
		
		if(ctNode.getTopposition()!=null){
			writer.startNode("topPosition");
			writer.setValue(ctNode.getTopposition().toString());
			writer.endNode();
		}
		
		if(ctNode.getWidth()!=null){
			writer.startNode("width");
			writer.setValue(ctNode.getWidth().toString());
			writer.endNode();
		}
		
		if(ctNode.getMarginLeft()!=null){
			writer.startNode("marginLeft");
			writer.setValue(ctNode.getMarginLeft().toString());
			writer.endNode();
		}
		
		if(ctNode.getMarginTop()!=null){
			writer.startNode("marginTop");
			writer.setValue(ctNode.getMarginTop().toString());
			writer.endNode();
		}
		
		if(ctNode.getHexColor()!=null){
			writer.startNode("hexColor");
			writer.setValue(ctNode.getHexColor());
			writer.endNode();
		}
		
		if(ctNode.getRelativeMarginLeft()!=null){
			writer.startNode("relativeMarginLeft");
			writer.setValue(ctNode.getRelativeMarginLeft().toString());
			writer.endNode();
		}
		
		if(ctNode.getRelativeWidth()!=null){
			writer.startNode("relativeWidth");
			writer.setValue(ctNode.getRelativeWidth().toString());
			writer.endNode();
		}
		
		if(ctNode.getRelativeHeight()!=null){
			writer.startNode("relativeHeight");
			writer.setValue(ctNode.getRelativeHeight().toString());
			writer.endNode();
		}
		
		if(ctNode.getSubnodes()!=null && ctNode.getSubnodes().size()>0){
			for(ContainerTreeNode n : ctNode.getSubnodes()){
				writer.startNode("containerNode");
				context.convertAnother(n);
				writer.endNode();
			}
		}
//		writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		ContainerTreeNode ctNode = new ContainerTreeNode();
		
		ctNode.setSystemName(reader.getAttribute(SYSNAME));
		
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if("pretyName".equals(reader.getNodeName())){
				ctNode.setPrettyName(reader.getValue());
			}else if("cssClasses".equals(reader.getNodeName())){
				ctNode.setCssClassInfos(reader.getValue());
			}else if("direction".equals(reader.getNodeName())){
				ctNode.setDirection(reader.getValue());
			}else if("domvalue".equals(reader.getNodeName())){
				ctNode.setDomvalue(reader.getValue());
			}else if("height".equals(reader.getNodeName())){
				ctNode.setHeight(NumberUtils.isNumber(reader.getValue())?Integer.valueOf(reader.getValue()):0);
			}else if("leftPosition".equals(reader.getNodeName())){
				ctNode.setLeftposition(NumberUtils.isNumber(reader.getValue())?Integer.valueOf(reader.getValue()):0);
			}else if("topPosition".equals(reader.getNodeName())){
				ctNode.setTopposition(NumberUtils.isNumber(reader.getValue())?Integer.valueOf(reader.getValue()):0);
			}else if("width".equals(reader.getNodeName())){
				ctNode.setWidth(NumberUtils.isNumber(reader.getValue())?Integer.valueOf(reader.getValue()):0);
			}else if("marginLeft".equals(reader.getNodeName())){
				ctNode.setMarginLeft(NumberUtils.isNumber(reader.getValue())?Integer.valueOf(reader.getValue()):null);
			}else if("marginTop".equals(reader.getNodeName())){
				ctNode.setMarginTop(NumberUtils.isNumber(reader.getValue())?Integer.valueOf(reader.getValue()):null);
			}else if("hexColor".equals(reader.getNodeName())){
				ctNode.setHexColor(reader.getValue());
			}else if("relativeMarginLeft".equals(reader.getNodeName())){
				ctNode.setRelativeMarginLeft(Double.valueOf(reader.getValue()));
			}else if("relativeWidth".equals(reader.getNodeName())){
				ctNode.setRelativeWidth(Double.valueOf(reader.getValue()));
			}else if("relativeHeight".equals(reader.getNodeName())){
				ctNode.setRelativeHeight(Double.valueOf(reader.getValue()));
			}else if("containerNode".equals(reader.getNodeName())){
				ContainerTreeNode child = (ContainerTreeNode)context.convertAnother(ctNode, ContainerTreeNode.class);
				if(child!=null){
					ctNode.addSubnode(child);
				}
			}
			
			reader.moveUp();
		}
		
		
		return ctNode;
	}

}
