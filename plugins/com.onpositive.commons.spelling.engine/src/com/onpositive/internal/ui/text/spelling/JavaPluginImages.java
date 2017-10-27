package com.onpositive.internal.ui.text.spelling;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.onpositive.semantic.ui.text.spelling.Activator;

public class JavaPluginImages {

	public static final String IMG_OBJS_NLS_NEVER_TRANSLATE = null;
	public static final String IMG_CORRECTION_RENAME = "rename";
	public static final String IMG_CORRECTION_ADD = "add";
	public static final String IMG_ICON_SHEET = "sheet";
	public static final String IMG_ICON_ATTACHMENT = "attachment";
	public static final String IMG_ICON_IMAGE = "image_obj";

	static Image change;
	static Image add;
	static Image ignore;
	static Image sheet;
	static Image attachment;
	static Image image;

	static {
		change = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.getDefault().getBundle().getSymbolicName(),
				"/icons/correction_change.gif").createImage();
		add = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.getDefault().getBundle().getSymbolicName(),
				"/icons/add.gif").createImage();
		ignore = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.getDefault().getBundle().getSymbolicName(),
				"/icons/ignore.gif").createImage();
		sheet = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.getDefault().getBundle().getSymbolicName(),
				"/icons/sheet.gif").createImage();
		attachment = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.getDefault().getBundle().getSymbolicName(),
				"/icons/attachment.png").createImage();
		image = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.getDefault().getBundle().getSymbolicName(),
				"/icons/image_obj.gif").createImage();
	}

	public static Image get(String imgObjsNlsNeverTranslate) {
		if (imgObjsNlsNeverTranslate != null) {
			if (imgObjsNlsNeverTranslate.equals(IMG_CORRECTION_RENAME)) {
				return change;
			}
			if (imgObjsNlsNeverTranslate.equals(IMG_CORRECTION_ADD)) {
				return add;
			}
			if (imgObjsNlsNeverTranslate.equals(IMG_ICON_SHEET)) {
				return sheet;
			}
			if (imgObjsNlsNeverTranslate.equals(IMG_ICON_ATTACHMENT)) {
				return attachment;
			}
			if (imgObjsNlsNeverTranslate.equals(IMG_ICON_IMAGE)) {
				return image;
			}
		}
		return ignore;
	}

}
