package com.onpositive.businessdroids.mainmenu;

import java.io.IOException;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;

public class MainMenuBackgroundDrawable extends Drawable {
	
	private BitmapDrawable drawable;

	public MainMenuBackgroundDrawable(Context context) {
//		try {
////			int width = 320;
////			int height = 480;
////			if (context instanceof Activity) {
////				Display defaultDisplay = ((Activity) context).getWindowManager().getDefaultDisplay();
////				width = defaultDisplay.getWidth();
////				height = defaultDisplay.getHeight();
////			}
////			IReadableObjectCollection<Object> collection = FightersBase.getCollection(context);
////			IPropertyMetadata property = collection.getProperty(IPropertyConstants.MAIN_PICTURE_PROPERTY_METADATA_ID);
////			int i = 0;
////			int j = 0;
////			int currentImg = 0;
////			Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
////		    Canvas canvas = new Canvas(bmpGrayscale);
////		    Paint paint = new Paint();
////		    ColorMatrix colorMatrix = new ColorMatrix();
////		    colorMatrix.setSaturation(0);
////		    ColorMatrixColorFilter f = new ColorMatrixColorFilter(colorMatrix);
////		    paint.setColorFilter(f);
////		    int maxHeight = 0;
////		    Rect src = new Rect();
////		    Rect dest = new Rect();
////		    canvas.drawColor(Color.WHITE);
////			while ((j < height) && currentImg < collection.size()) {
////				Object value = collection.getValue(currentImg++,property); 
////				while (value == null && currentImg < collection.size())
////					value = collection.getValue(currentImg++,property);
////				if (value != null) {
////					Bitmap bitmap = BitmapFactory.decodeByteArray((byte[])value,0,((byte[])value).length);
////					if (bitmap != null && !bitmap.isRecycled()) {
////						src.set(0,0,bitmap.getWidth(), bitmap.getHeight());
////						double dWidth = src.width() / (width / 2);
////						double dHeight = src.height() / (height / 2);
////						if (dWidth > 1 || dHeight > 1) {
////							if (dWidth > dHeight) {
////								dest.set(i,j,i + (int)(src.width() / dWidth), j + (int)(src.height() / dWidth));
////							} else {
////								dest.set(i,j,i + (int)(src.width() / dHeight), j + (int)(src.height() / dHeight));
////							}
////						} else 
////							dest.set(i,j, i + src.width(), j + src.height());
////						canvas.drawBitmap(bitmap,src,dest,paint);
////						i += dest.width();
////						maxHeight = Math.max(maxHeight, dest.height());
////						if (i >= width) {
////							i = 0;
////							j += maxHeight;
////							maxHeight = 0;
////						}
////					}
////				}
////			}
////			drawable = new BitmapDrawable(bmpGrayscale);
////			drawable.setBounds(0,0,width,height);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void draw(Canvas canvas) {
		drawable.draw(canvas);
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

}
