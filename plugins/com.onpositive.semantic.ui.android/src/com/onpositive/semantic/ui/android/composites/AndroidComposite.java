package com.onpositive.semantic.ui.android.composites;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;

import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIComposite;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;
import com.onpositive.semantic.ui.core.GenericLayoutHints;
import com.onpositive.semantic.ui.core.Point;
import com.onpositive.semantic.ui.core.Rectangle;

public abstract class AndroidComposite extends BasicUIComposite<View>{

	private static int[] colors = new int[]{ Color.GREEN,  Color.RED, Color.CYAN, Color.YELLOW, Color.BLUE, Color.MAGENTA };
	private static int colorIndex = 0 ;
	
	protected static void setBackground( View... views ){
//		for( View v : views ){		
//			if( colorIndex >= colors.length )
//				colorIndex = 0 ;
//			
//			int color = colors[colorIndex];
//			v.setBackgroundColor( color) ;
//			colorIndex++ ;
//		}
	}
	
	
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -7635771747298430470L;

	protected Context context=null;

	public AndroidComposite() {
		getLayoutHints().setGrabHorizontal(true);
	}

	@Override
	public void executeOnUiThread(Runnable runnable) {
		Activity activity = (Activity)getContext();
		if (activity != null) {
			activity.runOnUiThread(runnable);
		} else {
			runnable.run();
		}
	}
	
	@Override
	public void redraw() {
		getControl().refreshDrawableState();
	}
	
	protected void onDisplayable(IUIElement<?>element,boolean displayable){
		if (element.getControl() instanceof View) {
			((View)element.getControl()).setVisibility(displayable?View.VISIBLE:View.INVISIBLE);
		}
	}
	
	public Context getContext(){
		if (context!=null){
			return context;
		}
		BasicUIElement<View> root = getRoot();
		if (root != null && root != this) {
			return context=((AndroidComposite)root).getContext();
		}
		return null;
	}
	
	
	@Override
	protected void onCreate(ICompositeElement<?,?> parent) {
		super.onCreate(parent);
	
		ViewGroup t=(ViewGroup) getControl();
		Object data = getData(MARGIN_PROP_ID);
		if(data != null){
			Rectangle margin = getMargin();
			t.setPadding(margin.x,margin.y,margin.width,margin.height);
		}
		configureTotalWeight(t);
	}

	protected void configureTotalWeight(ViewGroup t) {
		float totalWeight=0;
		for (BasicUIElement<?>m:getChildren()){
//			adapt((BasicUIElement<View>) m);
			if (m.getLayoutHints().getGrabHorizontal()){
				totalWeight+=1;
			}
		}
		if(totalWeight>0){
			if (t instanceof LinearLayout){
				((LinearLayout)t).setWeightSum(totalWeight);
			}
		}
	}
	
	@Override
	protected View createControl(ICompositeElement<?,?> parent) {
		LinearLayout layout=new LinearLayout(getContext());				
		return layout;
	}

	@Override
	protected void adapt(BasicUIElement<View> element) {		
		super.adapt(element);
		if (isCreated()){
			ViewGroup m=(ViewGroup) getControl();
			GenericLayoutHints layoutHints = element.getLayoutHints();
			LinearLayout.LayoutParams p = createLayoutParams(layoutHints);
			configureLayoutParams(layoutHints, p);
			m.addView(element.getControl(),p);			
			setBackground( element.getControl() ) ;
		}
	}

	protected LinearLayout.LayoutParams createLayoutParams(
			GenericLayoutHints layoutHints) {
		int i = LayoutParams.WRAP_CONTENT;
		int j = layoutHints.getGrabVertical()?LayoutParams.FILL_PARENT:LayoutParams.WRAP_CONTENT;
		LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(i, j,layoutHints.getGrabHorizontal()?1:0);
		return p;
	}

	protected void configureLayoutParams(GenericLayoutHints layoutHints,
			MarginLayoutParams params) {
		Point indent = layoutHints.getIndent();
		if (indent != null) {
			params.leftMargin = (int) indent.horizontal.value;
			params.topMargin = (int) indent.vertical.value;
		}
	}
	
	public boolean propagateKeystroke(View v, int keyCode, KeyEvent event) {
		if (getParent() instanceof AndroidComposite) {
			((AndroidComposite) getParent()).propagateKeystroke(v,keyCode,event);
		}
		return false;
	}

	public boolean needKeystrokePropagation() {
		if (getParent() instanceof AndroidComposite) {
			return ((AndroidComposite) getParent()).needKeystrokePropagation();
		}
		return false;
	}
	
	@Override
	protected void unadapt(BasicUIElement<View> element) {
		if (isCreated()){
			ViewGroup m=(ViewGroup) getControl();
			m.removeView(element.getControl());
		}
		super.unadapt(element);
	}
}