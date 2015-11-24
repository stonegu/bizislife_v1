package com.bizislife.core.hibernate.pojo;

import java.util.Date;

public interface ScheduleInterface {
	public Date getStartdate();
	public Date getEnddate();
	public Integer getPriority();
	public String getUuid();
}
