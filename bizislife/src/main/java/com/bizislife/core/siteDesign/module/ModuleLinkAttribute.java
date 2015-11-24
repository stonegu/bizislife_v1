package com.bizislife.core.siteDesign.module;

import com.bizislife.core.entity.converter.ModuleAttrConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("moduleLinkAttr")
@XStreamConverter(ModuleAttrConverter.class)
public class ModuleLinkAttribute extends ModuleAttribute{
	public static final String MODULEATTRIBUTEDESC = "Link attribute";

	private static final long serialVersionUID = 5585003036488661664L;
	
	public static enum linkRel {
		alternate,
		author,
		bookmark,
		help,
		license,
		next,
		nofollow,
		noreferrer,
		prefetch,
		prev,
		search,
		tag,
	}
	
	public static enum linkTarget {
		_blank,
		_parent,
		_self,
		_top,
		framename,
	}

	private String href;
	private String rel;
	private String target;
//	private String linkTitle;
	
	private String linkValue;
	
	public ModuleLinkAttribute() {
		super();
	}

	public ModuleLinkAttribute(String href, String rel, String target, String linkValue) {
		super();
		this.href = href;
		this.rel = rel;
		this.target = target;
		this.linkValue = linkValue;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getLinkValue() {
		return linkValue;
	}

	public void setLinkValue(String linkValue) {
		this.linkValue = linkValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModuleLinkAttribute [href=").append(href)
				.append(", rel=").append(rel).append(", target=")
				.append(target).append(", linkValue=").append(linkValue).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((href == null) ? 0 : href.hashCode());
		result = prime * result
				+ ((linkValue == null) ? 0 : linkValue.hashCode());
		result = prime * result + ((rel == null) ? 0 : rel.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModuleLinkAttribute other = (ModuleLinkAttribute) obj;
		if (href == null) {
			if (other.href != null)
				return false;
		} else if (!href.equals(other.href))
			return false;
		if (linkValue == null) {
			if (other.linkValue != null)
				return false;
		} else if (!linkValue.equals(other.linkValue))
			return false;
		if (rel == null) {
			if (other.rel != null)
				return false;
		} else if (!rel.equals(other.rel))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	
}
