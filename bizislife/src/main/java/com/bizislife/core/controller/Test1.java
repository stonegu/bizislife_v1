package com.bizislife.core.controller;

public class Test1 {
	
	int i;

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + i;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Test1 other = (Test1) obj;
		if (i != other.i)
			return false;
		return true;
	}


	public void test11(int a){
		System.err.println("adfas");
	}
	
	
}
