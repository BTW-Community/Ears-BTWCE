package com.unascribed.ears.main;

public interface WritableEarsImage extends EarsImage {

	void setARGB(int x, int y, int argb);
	
	WritableEarsImage copy();
	
}
