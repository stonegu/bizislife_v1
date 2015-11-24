package com.bizislife.core.controller.component;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class ImageUploadDto {
	private String imageName;
	private CommonsMultipartFile img; // contentType : image/jpeg ...
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public CommonsMultipartFile getImg() {
		return img;
	}
	public void setImg(CommonsMultipartFile img) {
		this.img = img;
	}
}
