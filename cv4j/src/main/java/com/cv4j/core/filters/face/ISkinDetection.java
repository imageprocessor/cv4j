package com.cv4j.core.filters.face;


public interface ISkinDetection {
	
	boolean isSkin(int tr, int tg, int tb);
	
	boolean findSkin(int tr, int tg, int tb);
}
