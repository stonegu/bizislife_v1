package com.bizislife.core.entity.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.bizislife.core.entity.RealAttribute;
import com.bizislife.core.siteDesign.module.ModuleAttribute;
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
import com.bizislife.core.siteDesign.module.Money;
import com.bizislife.core.siteDesign.module.ModuleLinkAttribute.linkRel;
import com.bizislife.core.siteDesign.module.ModuleLinkAttribute.linkTarget;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ModuleAttrConverter implements Converter{

	@Override
	public boolean canConvert(Class type) {
		return ModuleAttribute.class.isAssignableFrom(type);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		ModuleAttribute moduleAttr = null;
		if(source.getClass().equals(ModuleStringAttribute.class)){
			moduleAttr = (ModuleStringAttribute)source;
		}else if(source.getClass().equals(ModuleIntegerAttribute.class)){
			moduleAttr = (ModuleIntegerAttribute)source;
		}else if(source.getClass().equals(ModuleTextAttribute.class)){
			moduleAttr = (ModuleTextAttribute)source;
		}else if(source.getClass().equals(ModuleNumberAttribute.class)){
			moduleAttr = (ModuleNumberAttribute)source;
		}else if(source.getClass().equals(ModuleImageAttribute.class)){
			moduleAttr = (ModuleImageAttribute)source;
		}else if(source.getClass().equals(ModuleLinkAttribute.class)){
			moduleAttr = (ModuleLinkAttribute)source;
		}else if(source.getClass().equals(ModuleMoneyAttribute.class)){
			moduleAttr = (ModuleMoneyAttribute)source;
		}else if(source.getClass().equals(ModuleProductListAttribute.class)){
			moduleAttr = (ModuleProductListAttribute)source;
		}else if(source.getClass().equals(ModuleEntityCategoryListAttribute.class)){
			moduleAttr = (ModuleEntityCategoryListAttribute)source;
		}
		
		writer.addAttribute("type", moduleAttr.getClass().getSimpleName());
		if(StringUtils.isNotBlank(moduleAttr.getName())){
			writer.addAttribute("name", moduleAttr.getName());
		}
		writer.addAttribute("uuid", moduleAttr.getUuid());
		
		if(StringUtils.isNotBlank(moduleAttr.getFrom_attrUuid())){
			writer.startNode("from_attrUuid");
			writer.setValue(moduleAttr.getFrom_attrUuid());
			writer.endNode();
		}
		
		//
		if(StringUtils.isNotBlank(moduleAttr.getModuleAttrUuid())){
			writer.startNode("moduleAttrUuid");
			writer.setValue(moduleAttr.getModuleAttrUuid());
			writer.endNode();
		}
		
		// title:
		if(StringUtils.isNotBlank(moduleAttr.getTitle())){
			writer.startNode("title");
			writer.setValue(moduleAttr.getTitle());
			writer.endNode();
		}
		
		// editable
		if(moduleAttr.getEditable()!=null){
			writer.startNode("editable");
			writer.setValue(moduleAttr.getEditable().toString());
			writer.endNode();
		}
		
//		else{ // default will be true
//			writer.startNode("editable");
//			writer.setValue(Boolean.TRUE.toString());
//			writer.endNode();
//		}
		
		// documentation:
		if(StringUtils.isNotBlank(moduleAttr.getDocumentation())){
			writer.startNode("documentation");
			writer.setValue(moduleAttr.getDocumentation());
			writer.endNode();
		}
		
		// required:
		if(moduleAttr.getRequired()!=null){
			writer.startNode("required");
			writer.setValue(moduleAttr.getRequired().toString());
			writer.endNode();
		}
		
//		else{
//			writer.startNode("required");
//			writer.setValue(Boolean.FALSE.toString());
//			writer.endNode();
//		}
		
		// array:
		if(moduleAttr.getArray()!=null){
			writer.startNode("array");
			writer.setValue(moduleAttr.getArray().toString());
			writer.endNode();
		}
		
//		else{
//			writer.startNode("array");
//			writer.setValue(Boolean.FALSE.toString());
//			writer.endNode();
//		}
		
		if(source.getClass().equals(ModuleStringAttribute.class)){
			writer.startNode("minLength");
			writer.setValue(((ModuleStringAttribute)moduleAttr).getMinLength().toString());
			writer.endNode();
			
			writer.startNode("maxLength");
			writer.setValue(((ModuleStringAttribute)moduleAttr).getMaxLength().toString());
			writer.endNode();
			
			writer.startNode("defaultValue");
			writer.setValue(((ModuleStringAttribute)moduleAttr).getDefaultValue());
			writer.endNode();
			
		}else if(source.getClass().equals(ModuleIntegerAttribute.class)){
			writer.startNode("minValue");
			writer.setValue(((ModuleIntegerAttribute)moduleAttr).getMinValue().toString());
			writer.endNode();
			
			writer.startNode("maxValue");
			writer.setValue(((ModuleIntegerAttribute)moduleAttr).getMaxValue().toString());
			writer.endNode();
			
			writer.startNode("defaultValue");
			writer.setValue(((ModuleIntegerAttribute)moduleAttr).getDefaultValue().toString());
			writer.endNode();
		}else if(source.getClass().equals(ModuleTextAttribute.class)){
			if(((ModuleTextAttribute)moduleAttr).getMinLength()!=null){
				writer.startNode("minLength");
				writer.setValue(((ModuleTextAttribute)moduleAttr).getMinLength().toString());
				writer.endNode();
			}

			if(((ModuleTextAttribute)moduleAttr).getMaxLength()!=null){
				writer.startNode("maxLength");
				writer.setValue(((ModuleTextAttribute)moduleAttr).getMaxLength().toString());
				writer.endNode();
			}
			
			if(((ModuleTextAttribute)moduleAttr).getDefaultValue()!=null){
				writer.startNode("defaultValue");
				writer.setValue(((ModuleTextAttribute)moduleAttr).getDefaultValue());
				writer.endNode();
			}
			
			if(((ModuleTextAttribute)moduleAttr).getTextArea()!=null){
				writer.startNode("textArea");
				writer.setValue(((ModuleTextAttribute)moduleAttr).getTextArea().toString());
				writer.endNode();
			}
			
		}else if(source.getClass().equals(ModuleNumberAttribute.class)){
			if(((ModuleNumberAttribute)moduleAttr).getMinValue()!=null){
				writer.startNode("minValue");
				writer.setValue(((ModuleNumberAttribute)moduleAttr).getMinValue().toPlainString());
				writer.endNode();
			}
			
			if(((ModuleNumberAttribute)moduleAttr).getMaxValue()!=null){
				writer.startNode("maxValue");
				writer.setValue(((ModuleNumberAttribute)moduleAttr).getMaxValue().toPlainString());
				writer.endNode();
			}
			
			if(((ModuleNumberAttribute)moduleAttr).getDefaultValue()!=null){
				writer.startNode("defaultValue");
				writer.setValue(((ModuleNumberAttribute)moduleAttr).getDefaultValue().toPlainString());
				writer.endNode();
			}
			
			if(((ModuleNumberAttribute)moduleAttr).getScale()!=null){
				writer.startNode("scale");
				writer.setValue(((ModuleNumberAttribute)moduleAttr).getScale().toString());
				writer.endNode();
			}
			
		}else if(source.getClass().equals(ModuleImageAttribute.class)){
			if(StringUtils.isNotBlank(((ModuleImageAttribute)moduleAttr).getFileSystemName())){
				writer.startNode("fileSystemName");
				writer.setValue(((ModuleImageAttribute)moduleAttr).getFileSystemName());
				writer.endNode();
			}
			if(((ModuleImageAttribute)moduleAttr).getDefaultPicture()!=null){
				writer.startNode("defaultPicture");
				writer.setValue(((ModuleImageAttribute)moduleAttr).getDefaultPicture().toString());
				writer.endNode();
			}
		}else if(source.getClass().equals(ModuleMoneyAttribute.class)){
			if(((ModuleMoneyAttribute)moduleAttr).getMoney()!=null){
				writer.startNode("money");
				writer.addAttribute("currency", ((ModuleMoneyAttribute)moduleAttr).getMoney().getCurrency().getCurrencyCode());
				writer.addAttribute("roundingMode", ((ModuleMoneyAttribute)moduleAttr).getMoney().getRounding().name());
				writer.setValue(((ModuleMoneyAttribute)moduleAttr).getMoney().getAmount().toPlainString());
				writer.endNode();
			}
		}else if(source.getClass().equals(ModuleLinkAttribute.class)){
			if(((ModuleLinkAttribute)moduleAttr).getHref()!=null){
				writer.startNode("linkHref");
				writer.setValue(((ModuleLinkAttribute)moduleAttr).getHref());
				writer.endNode();
			}
			if(((ModuleLinkAttribute)moduleAttr).getRel()!=null){
				writer.startNode("linkRel");
				writer.setValue(((ModuleLinkAttribute)moduleAttr).getRel());
				writer.endNode();
			}
			if(((ModuleLinkAttribute)moduleAttr).getTarget()!=null){
				writer.startNode("linkTarget");
				writer.setValue(((ModuleLinkAttribute)moduleAttr).getTarget());
				writer.endNode();
			}
//			if(((ModuleLinkAttribute)moduleAttr).getLinkTitle()!=null){
//				writer.startNode("linkTitle");
//				writer.setValue(((ModuleLinkAttribute)moduleAttr).getLinkTitle());
//				writer.endNode();
//			}
			if(((ModuleLinkAttribute)moduleAttr).getLinkValue()!=null){
				writer.startNode("linkValue");
				writer.setValue(((ModuleLinkAttribute)moduleAttr).getLinkValue());
				writer.endNode();
			}
		}else if(source.getClass().equals(ModuleProductListAttribute.class)){
			if(((ModuleProductListAttribute)moduleAttr).getTotalNumProductsInPage()!=null && ((ModuleProductListAttribute)moduleAttr).getTotalNumProductsInPage()>0){
				writer.startNode("totalNumProductsInPage");
				writer.setValue(((ModuleProductListAttribute)moduleAttr).getTotalNumProductsInPage().toString());
				writer.endNode();
			}
			// for pagination:
			writer.startNode("hasPagination");
			if(((ModuleProductListAttribute)moduleAttr).getHasPagination()!=null && ((ModuleProductListAttribute)moduleAttr).getHasPagination()){
				writer.setValue("true");
			}else{
				writer.setValue("false");
			}
			writer.endNode();
			
//			if(((ModuleProductListAttribute)moduleAttr).getDesktopProductPageUuid()!=null){
//				writer.startNode("desktopProductPageUuid");
//				writer.setValue(((ModuleProductListAttribute)moduleAttr).getDesktopProductPageUuid());
//				writer.endNode();
//			}
//			if(((ModuleProductListAttribute)moduleAttr).getMobileProductPageuuid()!=null){
//				writer.startNode("mobileProductPageuuid");
//				writer.setValue(((ModuleProductListAttribute)moduleAttr).getMobileProductPageuuid());
//				writer.endNode();
//			}
		}else if(source.getClass().equals(ModuleEntityCategoryListAttribute.class)){
			if(StringUtils.isNotBlank(((ModuleEntityCategoryListAttribute)moduleAttr).getCatType())){
				writer.startNode("categoryType");
				writer.setValue(((ModuleEntityCategoryListAttribute)moduleAttr).getCatType());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(((ModuleEntityCategoryListAttribute)moduleAttr).getSortType())){
				writer.startNode("sortType");
				writer.setValue(((ModuleEntityCategoryListAttribute)moduleAttr).getSortType());
				writer.endNode();
			}
			// for cat level
			int catLevel = 1; // default & minimal is 1;
			if(((ModuleEntityCategoryListAttribute)moduleAttr).getLevelOfCategory()>1) catLevel = ((ModuleEntityCategoryListAttribute)moduleAttr).getLevelOfCategory();
			writer.startNode("catLevel");
			writer.setValue(Integer.toString(catLevel));
			writer.endNode();
			
//			// for catpage - desk
//			if(StringUtils.isNotBlank(((ModuleEntityCategoryListAttribute)moduleAttr).getDeskCatListPageuuid())){
//				writer.startNode("deskCatListPage");
//				writer.setValue(((ModuleEntityCategoryListAttribute)moduleAttr).getDeskCatListPageuuid());
//				writer.endNode();
//			}
//			// for catpage - mobile
//			if(StringUtils.isNotBlank(((ModuleEntityCategoryListAttribute)moduleAttr).getMobileCatListPageuuid())){
//				writer.startNode("mobileCatListPage");
//				writer.setValue(((ModuleEntityCategoryListAttribute)moduleAttr).getMobileCatListPageuuid());
//				writer.endNode();
//			}
			
		}
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		
		String type = reader.getAttribute("type");
		String name = reader.getAttribute("name");
		String uuid = reader.getAttribute("uuid");
		
		if(StringUtils.isNotBlank(type) && StringUtils.isNotBlank(uuid)){
			
			ModuleAttribute attr = null;
			
			if(ModuleTextAttribute.class.getSimpleName().equals(type)){
				attr = new ModuleTextAttribute();
				attr.setType(ModuleTextAttribute.class.getSimpleName());
			}else if(ModuleNumberAttribute.class.getSimpleName().equals(type)){
				attr = new ModuleNumberAttribute();
				attr.setType(ModuleNumberAttribute.class.getSimpleName());
			}else if(ModuleImageAttribute.class.getSimpleName().equals(type)){
				attr = new ModuleImageAttribute(); 
				attr.setType(ModuleImageAttribute.class.getSimpleName());
			}else if(ModuleMoneyAttribute.class.getSimpleName().equals(type)){
				attr = new ModuleMoneyAttribute();
				attr.setType(ModuleMoneyAttribute.class.getSimpleName());
			}else if(ModuleLinkAttribute.class.getSimpleName().equals(type)){
				attr = new ModuleLinkAttribute();
				attr.setType(ModuleLinkAttribute.class.getSimpleName());
			}else if(ModuleProductListAttribute.class.getSimpleName().equals(type)){
				attr = new ModuleProductListAttribute();
				attr.setType(ModuleProductListAttribute.class.getSimpleName());
			}else if(ModuleEntityCategoryListAttribute.class.getSimpleName().equals(type)){
				attr = new ModuleEntityCategoryListAttribute();
				attr.setType(ModuleEntityCategoryListAttribute.class.getSimpleName());
			}
			
			if(attr!=null){
				attr.setUuid(uuid);
				
				if(StringUtils.isNotBlank(name)){
					attr.setName(name);
				}
				
				while(reader.hasMoreChildren()){
					reader.moveDown();
					
					// for common elements
					if("title".equals(reader.getNodeName()) && StringUtils.isNotBlank(reader.getValue())){
						attr.setTitle(reader.getValue().trim());
					}else if("editable".equals(reader.getNodeName())){
						// editable will be true by default
						attr.setEditable(Boolean.valueOf((reader.getValue()!=null && reader.getValue().equalsIgnoreCase("false"))?"false":"true"));
					}else if("documentation".equals(reader.getNodeName()) && StringUtils.isNotBlank(reader.getValue())){
						attr.setDocumentation(reader.getValue().trim());
					}else if("required".equals(reader.getNodeName())){
						// required will be false by default
						attr.setRequired(Boolean.valueOf((reader.getValue()!=null && reader.getValue().equalsIgnoreCase("true"))?"true":"false"));
					}else if("array".equals(reader.getNodeName())){
						// array will be false by default
						attr.setArray(Boolean.valueOf((reader.getValue()!=null && reader.getValue().equalsIgnoreCase("true"))?"true":"false"));
					}else if("moduleAttrUuid".equals(reader.getNodeName()) && StringUtils.isNotBlank(reader.getValue())){
						attr.setModuleAttrUuid(reader.getValue());
					}else if("from_attrUuid".equals(reader.getNodeName()) && StringUtils.isNotBlank(reader.getValue())){
						attr.setFrom_attrUuid(reader.getValue());
					}
					
					// for special elements
					else if("minLength".equals(reader.getNodeName())){
						if(ModuleTextAttribute.class.getSimpleName().equals(type)){
							((ModuleTextAttribute)attr).setMinLength(NumberUtils.isNumber(reader.getValue())?Integer.valueOf(reader.getValue()):null);
						}
					}else if("maxLength".equals(reader.getNodeName())){
						if(ModuleTextAttribute.class.getSimpleName().equals(type)){
							((ModuleTextAttribute)attr).setMaxLength(NumberUtils.isNumber(reader.getValue())?Integer.valueOf(reader.getValue()):null);
						}
					}else if("defaultValue".equals(reader.getNodeName()) && StringUtils.isNotBlank(reader.getValue())){
						if(ModuleTextAttribute.class.getSimpleName().equals(type)){
							((ModuleTextAttribute)attr).setDefaultValue(reader.getValue().trim());
						}else if(ModuleNumberAttribute.class.getSimpleName().equals(type)){
							((ModuleNumberAttribute)attr).setDefaultValue(new BigDecimal(reader.getValue()));
						}
						
					}else if("textArea".equals(reader.getNodeName())){
						if(ModuleTextAttribute.class.getSimpleName().equals(type)){
							// default is false
							((ModuleTextAttribute)attr).setTextArea(Boolean.valueOf((reader.getValue()!=null && reader.getValue().equalsIgnoreCase("true"))?"true":"false"));
						}
						
					}else if("minValue".equals(reader.getNodeName())){
						if(ModuleNumberAttribute.class.getSimpleName().equals(type)){
							((ModuleNumberAttribute)attr).setMinValue(StringUtils.isNotBlank(reader.getValue())?new BigDecimal(reader.getValue()):null);
						}
					}else if("maxValue".equals(reader.getNodeName())){
						if(ModuleNumberAttribute.class.getSimpleName().equals(type)){
							((ModuleNumberAttribute)attr).setMaxValue(StringUtils.isNotBlank(reader.getValue())?new BigDecimal(reader.getValue()):null);
						}
					}else if("scale".equals(reader.getNodeName())){
						if(ModuleNumberAttribute.class.getSimpleName().equals(type)){
							((ModuleNumberAttribute)attr).setScale(NumberUtils.isNumber(reader.getValue())?Integer.valueOf(reader.getValue()):null);
						}
					}else if("fileSystemName".equals(reader.getNodeName()) && StringUtils.isNotBlank(reader.getValue())){
						if(ModuleImageAttribute.class.getSimpleName().equals(type)){
							((ModuleImageAttribute)attr).setFileSystemName(reader.getValue());
						}
					}else if("defaultPicture".equals(reader.getNodeName()) && StringUtils.isNotBlank(reader.getValue())){
						if(ModuleImageAttribute.class.getSimpleName().equals(type)){
							((ModuleImageAttribute)attr).setDefaultPicture(Boolean.valueOf(reader.getValue()));
						}
					}else if("money".equals(reader.getNodeName())){
						if(ModuleMoneyAttribute.class.getSimpleName().equals(type)){
							Money money = null;
							Currency currency = StringUtils.isNotBlank(reader.getAttribute("currency"))?Currency.getInstance(reader.getAttribute("currency")):null;
							RoundingMode roundingMode = StringUtils.isNotBlank(reader.getAttribute("roundingMode"))?RoundingMode.valueOf(reader.getAttribute("roundingMode")):null;
							
							if(currency!=null && roundingMode!=null){
								BigDecimal value = StringUtils.isNotBlank(reader.getValue())?new BigDecimal(reader.getValue()):new BigDecimal(0);
								money = new Money(value, currency, roundingMode);
							}
							
							((ModuleMoneyAttribute)attr).setMoney(money);
						}
					}else if("linkHref".equals(reader.getNodeName()) && StringUtils.isNotBlank(reader.getValue())){
						if(ModuleLinkAttribute.class.getSimpleName().equals(type)){
							((ModuleLinkAttribute)attr).setHref(reader.getValue());
						}
					}else if("linkRel".equals(reader.getNodeName()) && StringUtils.isNotBlank(reader.getValue())){
						if(ModuleLinkAttribute.class.getSimpleName().equals(type)){
							((ModuleLinkAttribute)attr).setRel(reader.getValue());
						}
					}else if("linkTarget".equals(reader.getNodeName()) && StringUtils.isNotBlank(reader.getValue())){
						if(ModuleLinkAttribute.class.getSimpleName().equals(type)){
							((ModuleLinkAttribute)attr).setTarget(reader.getValue());
						}
					}else if("linkValue".equals(reader.getNodeName()) && StringUtils.isNotBlank(reader.getValue())){
						if(ModuleLinkAttribute.class.getSimpleName().equals(type)){
							((ModuleLinkAttribute)attr).setLinkValue(reader.getValue());
						}
					}else if("totalNumProductsInPage".equals(reader.getNodeName())){
						if(ModuleProductListAttribute.class.getSimpleName().equals(type)){
							Integer num = 0;
							if(StringUtils.isNotBlank(reader.getValue())){
								num = Integer.valueOf(reader.getValue());
							}
							((ModuleProductListAttribute)attr).setTotalNumProductsInPage(num);
						}
					}else if("hasPagination".equals(reader.getNodeName())){
						if(ModuleProductListAttribute.class.getSimpleName().equals(type)){
							Boolean hasPagination = Boolean.FALSE;
							if(StringUtils.isNotBlank(reader.getValue())){
								if("true".equalsIgnoreCase(reader.getValue())){
									hasPagination = Boolean.TRUE;
								}
							}
							((ModuleProductListAttribute)attr).setHasPagination(hasPagination);
						}
						
					}
					
//					else if("desktopProductPageUuid".equals(reader.getNodeName()) && StringUtils.isNotBlank(reader.getValue())){
//						if(ModuleProductListAttribute.class.getSimpleName().equals(type)){
//							((ModuleProductListAttribute)attr).setDesktopProductPageUuid(reader.getValue());
//						}
//					}else if("mobileProductPageuuid".equals(reader.getNodeName()) && StringUtils.isNotBlank(reader.getValue())){
//						if(ModuleProductListAttribute.class.getSimpleName().equals(type)){
//							((ModuleProductListAttribute)attr).setMobileProductPageuuid(reader.getValue());
//						}
//					}
					
					else if("categoryType".equals(reader.getNodeName())){
						if(ModuleEntityCategoryListAttribute.class.getSimpleName().equals(type)){
							if(StringUtils.isNotBlank(reader.getValue())){
								if(ModuleEntityCategoryListAttribute.Type.valueOf(reader.getValue())!=null){
									((ModuleEntityCategoryListAttribute)attr).setCatType(reader.getValue().trim());
								}else{ // default is product
									((ModuleEntityCategoryListAttribute)attr).setCatType(ModuleEntityCategoryListAttribute.Type.Product.name());
								}
							}else{ // default is product
								((ModuleEntityCategoryListAttribute)attr).setCatType(ModuleEntityCategoryListAttribute.Type.Product.name());
							}
						}
					}else if("sortType".equals(reader.getNodeName())){
						if(ModuleEntityCategoryListAttribute.class.getSimpleName().equals(type)){
							if(StringUtils.isNotBlank(reader.getValue())){
								if(ModuleEntityCategoryListAttribute.SortType.valueOf(reader.getValue())!=null){
									((ModuleEntityCategoryListAttribute)attr).setSortType(reader.getValue().trim());
								}else{ // default is not sort
									((ModuleEntityCategoryListAttribute)attr).setSortType(ModuleEntityCategoryListAttribute.SortType.NotSort.name());
								}
							}else{ // default is not sort
								((ModuleEntityCategoryListAttribute)attr).setSortType(ModuleEntityCategoryListAttribute.SortType.NotSort.name());
							}
						}
					}else if("catLevel".equals(reader.getNodeName())){
						if(ModuleEntityCategoryListAttribute.class.getSimpleName().equals(type)){
							if(StringUtils.isNotBlank(reader.getValue()) && NumberUtils.isNumber(reader.getValue())){
								int catlevel = Integer.parseInt(reader.getValue());
								if(catlevel<1) catlevel = 1;
								((ModuleEntityCategoryListAttribute)attr).setLevelOfCategory(catlevel);
							}else{ // default and min is 1
								((ModuleEntityCategoryListAttribute)attr).setLevelOfCategory(1);
							}
						}
					}
					
					reader.moveUp();
				}
				
				return attr;
			}
			
		}
		return null;
	}

}
