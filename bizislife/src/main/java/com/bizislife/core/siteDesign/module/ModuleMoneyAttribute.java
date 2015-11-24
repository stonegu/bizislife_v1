package com.bizislife.core.siteDesign.module;

import java.math.BigDecimal;

import com.bizislife.core.entity.converter.ModuleAttrConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("moduleMoneyAttr")
@XStreamConverter(ModuleAttrConverter.class)
public class ModuleMoneyAttribute extends ModuleAttribute{
	public static final String MODULEATTRIBUTEDESC = "Money attribute";

	private static final long serialVersionUID = 8004648507461638744L;

	private Money money;

	public ModuleMoneyAttribute() {
		this.money = new Money(new BigDecimal(0));
	}

	public ModuleMoneyAttribute(Money money) {
		super();
		this.money = money;
	}

	public Money getMoney() {
		return money;
	}

	public void setMoney(Money money) {
		this.money = money;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModuleMoneyAttribute [money=").append(money).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((money == null) ? 0 : money.hashCode());
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
		ModuleMoneyAttribute other = (ModuleMoneyAttribute) obj;
		if (money == null) {
			if (other.money != null)
				return false;
		} else if (!money.equals(other.money))
			return false;
		return true;
	}
	
}
