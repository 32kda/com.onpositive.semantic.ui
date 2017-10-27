package greendroid.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Custom layout that arranges children in a grid-like manner, optimizing for even horizontal and
 * vertical whitespace.
 */
public class Page extends ViewGroup {

    public Page(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private LayoutData layoutData;
    int maxElementsCount;
    
//    private IContentProvider contentProvider;
//    private IViewLabelProvider labelProvider;

//    public Page(Context context, IContentProvider cp, IViewLabelProvider lp) {
//        super(context, null);
//        this.contentProvider = cp;
//        this.labelProvider = lp;
//        refresh();
//    }
//    
//    public Page(Context context, IContentProvider cp, ILabelProvider lp) {
//        super(context, null);
//        this.contentProvider = cp;
//        this.labelProvider = new DefaultViewLabelProvider(lp);
//        refresh();
//    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    
        final int count = getChildCount();
        int cnt = 0;
        int width = r - l;
        int height = b - t;
        
        // layoutData = new LayoutData(width, height, 5, 10);
 
        for(int i=0; i < getLayoutData().rowsCount; i++)
        for(int j=0; j < getLayoutData().columnsCount; j++) {
        	if(cnt<count){
        		final View child = getChildAt(cnt);
        		
        		int left = getLayoutData().hgap/2+j*(getLayoutData().elementSize+getLayoutData().hgap);
        		int top = getLayoutData().vgap/2+i*(getLayoutData().elementSize+getLayoutData().vgap);
        		int right =getLayoutData().hgap/2+j*(getLayoutData().elementSize+getLayoutData().hgap)+getLayoutData().elementSize;
        		int bottom =getLayoutData().vgap/2+i*(getLayoutData().elementSize+getLayoutData().vgap)+getLayoutData().elementSize;
        		
                child.layout(left, top, right, bottom);
        	}
        	cnt++;
        }

    }
    
    public void refresh(){
//    	removeAllViews();
//    	for(Object element : contentProvider.getContent()) {
//    		addView(labelProvider.viewFor(this.getContext(),element));
//    	}
    }

	public void setLayoutData(LayoutData layoutData) {
		this.layoutData = layoutData;
	}

	public LayoutData getLayoutData() {
		return layoutData;
	};
}

