package com.onpositive.semantic.model.ui.property.editors;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.onpositive.commons.contentassist.ContentProposalAdapter;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.ui.generic.widgets.IMultitextElement;

public class ViewerTextElement extends AbstractTextElement<String, Composite> implements IViewerTextElement ,IMultitextElement<Composite>{

	private SourceViewer viewer;
	private IViewerConfigurator configurator;
	private boolean isMultiline = true;
	private boolean wrapText = true;

	public boolean isWrapText() {
		return this.wrapText;
	}

	public void setWrapText(boolean wrapText) {
		if (this.wrapText != wrapText) {
			this.wrapText = wrapText;
			if (this.isCreated()) {
				this.recreate();
			}
		}
	}

	private boolean hasRuler;

	public boolean isMultiline() {
		return this.isMultiline;
	}
	
	public void setMultiline(boolean isMultiline) {
		if (!isMultiline) {
			this.getLayoutHints().setGrabVertical(false);
		} else {
			this.getLayoutHints().setGrabVertical(true);
		}
		if (isMultiline != this.isMultiline) {
			this.isMultiline = isMultiline;
			this.recreate();
		}
	}

	
	protected ContentProposalAdapter createContentAssistAdapter(Composite text) {
		return null;
	}

	
	public String getText() {
		if (!this.isCreated()) {
			return this.text;
		}
		final IDocument document = this.viewer.getDocument();
		if (document != null) {
			return document.get();
		}
		return this.text;
	}

	protected void internalSetCaption() {
		if (!this.isMultiline){
			super.internalSetCaption();
		}
	}
	
	
	public boolean needsLabel() {
		return !this.isMultiline;
	}

	public void setText(String txt) {
		this.text = txt;
		if (this.isCreated()) {
			final IDocument document = this.viewer.getDocument();
			if (document == null) {
				this.viewer.setDocument(new Document(txt), new AnnotationModel());

			} else {
				document.set(txt);
			}
		}
	}

	
	protected Composite internalCreateControl(Composite conComposite) {
		if (this.isMultiline) {
			final int getWrapStyle = this.isWrapText() ? SWT.WRAP : SWT.H_SCROLL;
			this.viewer = new SourceViewer(conComposite,
					this.hasRuler ? new VerticalRuler(16) : null, SWT.MULTI
							| SWT.V_SCROLL | getWrapStyle | SWT.BORDER);
			
			this.viewer.setDocument(new Document(), new AnnotationModel());
		} else {
			this.viewer = new SourceViewer(conComposite,
					this.hasRuler ? new VerticalRuler(0) : null, SWT.SINGLE
							| SWT.BORDER);
			this.viewer.setDocument(new Document(), new AnnotationModel());
			this.viewer.getControl().addFocusListener(new FocusListener() {

				public void focusGained(FocusEvent e) {
//					final IAllowsRegisterCommands service = ViewerTextElement.this.getService(IAllowsRegisterCommands.class);
				}

				public void focusLost(FocusEvent e) {

				}

			});
			viewer.addTextListener(new ITextListener() {
				
				public void textChanged(TextEvent event) {
					doCommit(viewer.getDocument());
				}
			});

		}
		this.configureViewer();
		this.viewer.getControl().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		viewer.setEditable(!isReadOnly());
		return (Composite) this.viewer.getControl();
	}

	protected void doCommit(IDocument document) {
		commitToBinding(document.get());
	}

	private void configureViewer() {
		if (this.configurator != null) {
			this.configurator.configure(this);
		}
	}

	public boolean isHasVerticalRuler() {
		return this.hasRuler;
	}

	public void setHasVerticalRuler(boolean hasRuler) {
		if (this.hasRuler != hasRuler) {
			this.hasRuler = hasRuler;
			if (this.isCreated()) {
				this.recreate();
			}
		}
	}
	
	@HandlesAttributeDirectly("sourceViewerConfigurator")
	public void setConfigurator(IViewerConfigurator configurator2) {
		this.configurator = configurator2;
		if (this.isCreated()) {
			this.recreate();
		}
	}

	public IViewerConfigurator getConfigurator() {
		return this.configurator;
	}

	public SourceViewer getViewer() {
		return this.viewer;
	}

	public AbstractUIElement<?> getElement()
	{
		return this;
	}

	public SourceViewer getSourceViewer()
	{
		return viewer;
	}
}
