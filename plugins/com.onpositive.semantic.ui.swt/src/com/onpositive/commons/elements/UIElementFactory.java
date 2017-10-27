package com.onpositive.commons.elements;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormText;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.semantic.model.ui.generic.ElementCreationListener;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public final class UIElementFactory {

	private UIElementFactory() {

	}

	public static UniversalUIElement<Text> createText(String caption) {
		final UniversalUIElement<Text> universalUIElement = new UniversalUIElement<Text>(
				Text.class, SWT.BORDER);
		universalUIElement.setCaption(caption);
		return universalUIElement;
	}

	public static UniversalUIElement<Button> createButton(String caption) {
		final UniversalUIElement<Button> universalUIElement = new UniversalUIElement<Button>(
				Button.class, SWT.PUSH);
		universalUIElement.setCaption(caption);
		return universalUIElement;
	}

	public static UniversalUIElement<Button> createCheckBox(String caption) {
		final UniversalUIElement<Button> universalUIElement = new UniversalUIElement<Button>(
				Button.class, SWT.CHECK);
		universalUIElement.setCaption(caption);
		return universalUIElement;
	}

	public static UniversalUIElement<Button> createRadio(String caption) {
		final UniversalUIElement<Button> universalUIElement = new UniversalUIElement<Button>(
				Button.class, SWT.RADIO);
		universalUIElement.setCaption(caption);
		return universalUIElement;
	}

	public static UniversalUIElement<Label> createLabel(String caption) {
		final UniversalUIElement<Label> universalUIElement = new UniversalUIElement<Label>(
				Label.class, SWT.NONE);
		universalUIElement.setCaption(caption);
		return universalUIElement;
	}

	public static UniversalUIElement<Label> createMultiLineLabel(String caption) {
		final UniversalUIElement<Label> universalUIElement = new UniversalUIElement<Label>(
				Label.class, SWT.WRAP);
		universalUIElement.setCaption(caption);
		return universalUIElement;
	}

	public static UniversalUIElement<Label> createHorizontalSeparator() {
		final UniversalUIElement<Label> universalUIElement = new UniversalUIElement<Label>(
				Label.class, SWT.SEPARATOR | SWT.HORIZONTAL);
		universalUIElement.setLayoutData(new GridData(GridData.FILL_BOTH));
		universalUIElement.getLayoutHints().setIndent(new com.onpositive.semantic.ui.core.Point(0, 4));
		return universalUIElement;
	}

	public static UniversalUIElement<Label> createHorizontalSeparator(
			boolean grabVertical, boolean isTop) {
		final UniversalUIElement<Label> universalUIElement = new UniversalUIElement<Label>(
				Label.class, SWT.SEPARATOR | SWT.HORIZONTAL);
		universalUIElement.setLayoutData(new GridData(GridData.FILL_BOTH));
		universalUIElement.getLayoutHints().setAlignmentVertical(
				isTop ? SWT.TOP : SWT.BOTTOM);
		universalUIElement.getLayoutHints().setGrabVertical(grabVertical);
		universalUIElement.getLayoutHints().setIndent(new com.onpositive.semantic.ui.core.Point(0, 4));
		return universalUIElement;
	}

	public static UniversalUIElement<Label> createVerticalSeparator() {
		return new UniversalUIElement<Label>(Label.class, SWT.SEPARATOR
				| SWT.HORIZONTAL);
	}

	public static AbstractUIElement<?> createBannerLabel(String caption) {
		final UniversalUIElement<Label> universalUIElement = new UniversalUIElement<Label>(
				Label.class, SWT.NONE);
		universalUIElement.setCaption(caption);
		universalUIElement.setFont(JFaceResources.BANNER_FONT);
		return universalUIElement;
	}

	public static AbstractUIElement<?> createRichLabel(String caption) {
		final UniversalUIElement<FormText> universalUIElement = new UniversalUIElement<FormText>(
				FormText.class, SWT.NONE) {

			protected void internalSetText(String txt) {
				if (txt != null) {
					final String txt2 = adaptText(txt);
					try{
					this.getControl().setText(txt2, true, true);
					}catch (Exception e) {
						this.getControl().setText("", false, false); //$NON-NLS-1$
					}
				} else {
					this.getControl().setText("", false, false); //$NON-NLS-1$
				}
			}

		};
		universalUIElement.setText(caption);
		return universalUIElement;
	}

	public static AbstractUIElement<?> createHeaderLabel(String caption) {
		final UniversalUIElement<Label> universalUIElement = new UniversalUIElement<Label>(
				Label.class, SWT.NONE);
		universalUIElement.setCaption(caption);
		universalUIElement.setFont(JFaceResources.HEADER_FONT);
		return universalUIElement;
	}

	public static UniversalUIElement<Label> createImageLabel(
			final String imageId) {
		final UniversalUIElement<Label> universalUIElement = new UniversalUIElement<Label>(
				Label.class, SWT.NONE);
		universalUIElement
				.addElementListener(new ElementCreationListener() {

					public void elementCreated(IUIElement<?> element) {
						((Label)element.getControl()).setImage(
								SWTImageManager.getImage(imageId));
					}

				});
		return universalUIElement;
	}

	public static String adaptText(String txt) {
		if (txt == null) {
			return ""; //$NON-NLS-1$
		}
		if (!txt.startsWith("<p>")) { //$NON-NLS-1$
			txt = "<p>" + txt + "</p>"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		final String txt2 = "<form>" + txt + "</form>";//$NON-NLS-1$//$NON-NLS-2$					
		return txt2;
	}
}
