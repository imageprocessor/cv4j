package com.cv4j.core.filters;


public interface ISkinDetection {
	
	public boolean isSkin(int tr, int tg, int tb);
	
	public boolean findSkin(int tr, int tg, int tb);

}
