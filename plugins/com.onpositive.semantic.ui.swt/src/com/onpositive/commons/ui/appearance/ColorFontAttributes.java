package com.onpositive.commons.ui.appearance;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * @author kor
 * 
 */
public class ColorFontAttributes  {

	private final String fontAtribute;
	private final String foreGroundColor;
	private final String backgroundColor;
	private IPropertyChangeListener flistener;
	private IPropertyChangeListener clistener;
	private IPropertyChangeListener blistener;
	private PaintListener listener;

	public ColorFontAttributes(String font, String foreground, String bground) {
		this.fontAtribute = font;
		this.backgroundColor = foreground;
		this.foreGroundColor = bground;
	}

	public void dispose() {
		if (this.flistener != null) {
			JFaceResources.getFontRegistry().removeListener(this.flistener);
		}
		if (this.clistener != null) {
			JFaceResources.getColorRegistry().removeListener(this.clistener);
		}
		if (this.blistener != null) {
			JFaceResources.getColorRegistry().removeListener(this.blistener);
		}
	}

	public void apply(final Control cm) {
		listener = new PaintListener(){

			public void paintControl(PaintEvent e) {
				internalApply(cm);
				cm.removePaintListener(this);
			}
			
		};
		cm.addPaintListener(listener);
		internalApply(cm);
	}

	private void internalApply(final Control cm) {
		if (this.fontAtribute != null) {
			final FontRegistry fontRegistry = JFaceResources.getFontRegistry();
			Font font = fontRegistry.get(this.fontAtribute);
			if (font==null){
				font=calculateFont(fontAtribute);
			}
			if (font == null) {
				throw new IllegalArgumentException(
						"Unknown key " + this.fontAtribute + " known fonts:" + fontRegistry.getKeySet()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			cm.setFont(font);
			this.flistener = new IPropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent event) {
					if (event.getProperty().equals(
							ColorFontAttributes.this.fontAtribute)) {
						cm.setFont(fontRegistry
								.get(ColorFontAttributes.this.fontAtribute));
					}
				}
			};
			fontRegistry.addListener(this.flistener);
			cm.addDisposeListener(new DisposeListener() {

				public void widgetDisposed(DisposeEvent e) {
					fontRegistry
							.removeListener(ColorFontAttributes.this.flistener);
				}
			});
		}
		if (this.foreGroundColor != null) {
			final ColorRegistry cs = JFaceResources.getColorRegistry();
			Color c = cs.get(this.foreGroundColor);
			if (c == null) {
				c = parseColor(foreGroundColor);
			}
			if (c == null) {
				throw new IllegalArgumentException(
						"Unknown color " + this.foreGroundColor + " known colors:" + cs.getKeySet()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			this.recusiveApply(cm, c, true);
			this.clistener = new IPropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent event) {
					if (event.getProperty().equals(
							ColorFontAttributes.this.foreGroundColor)) {
						ColorFontAttributes.this.recusiveApply(cm, cs
								.get(ColorFontAttributes.this.foreGroundColor),true);
					}
				}
			};
			cs.addListener(this.clistener);
			cm.addDisposeListener(new DisposeListener() {

				public void widgetDisposed(DisposeEvent e) {
					cs.removeListener(ColorFontAttributes.this.clistener);
				}
			});
		}
		if (this.backgroundColor != null) {
			final ColorRegistry cs = JFaceResources.getColorRegistry();
			Color c = cs.get(this.backgroundColor);
			if (c == null) {
				c = parseColor(backgroundColor);
			}
			if (c == null) {
				throw new IllegalArgumentException(
						"Unknown background color " + this.backgroundColor + " known colors:" + cs.getKeySet()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			this.recusiveApply(cm, c, false);
			this.blistener = new IPropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent event) {
					if (event.getProperty().equals(
							ColorFontAttributes.this.backgroundColor)) {
						ColorFontAttributes.this.recusiveApply(cm, cs
								.get(ColorFontAttributes.this.backgroundColor),false);
					}
				}
			};
			cs.addListener(this.blistener);
			cm.addDisposeListener(new DisposeListener() {

				public void widgetDisposed(DisposeEvent e) {
					cs.removeListener(ColorFontAttributes.this.blistener);
				}
			});
		}
	}

	private Font calculateFont(String fontAtribute2) {
		FontData[] asFontData = StringConverter.asFontDataArray(fontAtribute2);
		JFaceResources.getFontRegistry().put(fontAtribute2, asFontData);
		return JFaceResources.getFontRegistry().get(fontAtribute2);
	}

	private Color parseColor(String color) {
		try {
			ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
			String[] split = color.trim().split(",");
			int red = Integer.parseInt(split[0].trim());
			int green = Integer.parseInt(split[1].trim());
			int blue = Integer.parseInt(split[2].trim());
			RGB rgb = new RGB(red, green, blue);
			colorRegistry.put(color, rgb);
			return colorRegistry.get(color);
		} catch (Exception e) {

		}
		return null;
	}

	private void recusiveApply(Control cm, Color c, boolean b) {
		if (b) {
			cm.setForeground(c);
		} else {
			cm.setBackground(c);
		}
		if (cm instanceof Composite) {
			final Control[] children = ((Composite) cm).getChildren();
			for (int i = 0; i < children.length; i++) {
				this.recusiveApply(children[i], c,b);
			}
		}
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.backgroundColor == null) ? 0 : this.backgroundColor
						.hashCode());
		result = prime
				* result
				+ ((this.fontAtribute == null) ? 0 : this.fontAtribute
						.hashCode());
		result = prime
				* result
				+ ((this.foreGroundColor == null) ? 0 : this.foreGroundColor
						.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final ColorFontAttributes other = (ColorFontAttributes) obj;
		if (this.backgroundColor == null) {
			if (other.backgroundColor != null) {
				return false;
			}
		} else if (!this.backgroundColor.equals(other.backgroundColor)) {
			return false;
		}
		if (this.fontAtribute == null) {
			if (other.fontAtribute != null) {
				return false;
			}
		} else if (!this.fontAtribute.equals(other.fontAtribute)) {
			return false;
		}
		if (this.foreGroundColor == null) {
			if (other.foreGroundColor != null) {
				return false;
			}
		} else if (!this.foreGroundColor.equals(other.foreGroundColor)) {
			return false;
		}
		return true;
	}
}
