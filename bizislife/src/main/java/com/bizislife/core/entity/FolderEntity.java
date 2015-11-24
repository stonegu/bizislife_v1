package com.bizislife.core.entity;

import com.bizislife.core.entity.converter.EntityConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("folderEntity")
@XStreamConverter(EntityConverter.class)
public class FolderEntity extends EntityAdaper{

}
