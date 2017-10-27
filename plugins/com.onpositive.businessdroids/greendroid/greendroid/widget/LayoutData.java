package greendroid.widget;

public class LayoutData {
	public int rowsCount,columnsCount, vgap, hgap;
	
	int elementSize;
	
	public LayoutData(int width, int height, int percent, int minGap) {
		elementSize = (int)(0.1*Math.sqrt(percent*width*height));
		
		columnsCount=(int)((float)width/((float)elementSize + (float)minGap));
		rowsCount=(int)((float)height/((float)elementSize + (float)minGap));
		
		hgap = (int)((float)width/(float)columnsCount-(float)elementSize);
		vgap = (int)((float)height/(float)rowsCount-(float)elementSize);
	}

}
