package com.onpositive.semantic.ui.realm.fastviewer;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;


public class DoubleStringColumnRenderer implements IColumnRenderer {

	private IPaintInfoProvider provider;

	private TextLayout layout = new TextLayout(Display.getCurrent());

	private int fontSize = 8;
	
	{
		layout.setText(" 100,00%");
		fontSize=layout.getBounds().width;
	}

	public int measureWidth(RowItem node) {
		return 0;
	}

	public void renderColumn(RowItem item, Event event) {
		if (provider != null) {
			ITreeNode node = item.getNode();
			
			ColumnPaintInfo paintInfo = provider.getInfo(node);
			Rectangle clipping = event.gc.getClipping();

			if (paintInfo != null) {
				String left = paintInfo.leftString;

				if (left == null) {
					left = "";
				}
				String right = paintInfo.rightString;
				if (right == null) {
					right = "";
					if (left.length() != 0) {
						right = left;
						left = "";
					}
				}
				layout.setText(right);
				Rectangle bounds = layout.getBounds();

				int xp = clipping.x + clipping.width - bounds.width - 3;
				int yp = clipping.y + clipping.height - bounds.height - 1;
				if (paintInfo.leftColor != null) {
					event.gc.setForeground(paintInfo.leftColor);
				}
				layout.draw(event.gc, xp, yp);
				layout.setText(left);
				bounds = layout.getBounds();
				xp = clipping.x + clipping.width - fontSize-4 
						- (bounds.width);
				Color rightColor = paintInfo.rightColor;
				if (paintInfo.rightColor != null) {
					event.gc.setForeground(rightColor);
				}
				layout.draw(event.gc, xp, yp);
				if (paintInfo.image != null) {
					event.gc.drawImage(paintInfo.image, clipping.x + 2,
							clipping.y + 3);
				}
			}
		}

	}

	public void setInfoProvider(IPaintInfoProvider paintInfoProvider) {
		this.provider = paintInfoProvider;
	}

	public String getText(Object el) {
		ColumnPaintInfo info = provider.getInfo(el);
		if (info == null) {
			return "";
		}
		return info.leftString;
	}

}
