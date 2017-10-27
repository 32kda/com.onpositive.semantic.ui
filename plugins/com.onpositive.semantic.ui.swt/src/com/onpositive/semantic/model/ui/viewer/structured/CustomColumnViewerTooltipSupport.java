package com.onpositive.semantic.model.ui.viewer.structured;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.commons.elements.UIElementFactory;
import com.onpositive.semantic.common.ui.roles.ContentProducerObject;
import com.onpositive.semantic.common.ui.roles.IInformationalControlContentProducer;
import com.onpositive.semantic.common.ui.roles.TooltipManager;
import com.onpositive.semantic.model.api.roles.LabelManager;

public class CustomColumnViewerTooltipSupport extends
		ColumnViewerToolTipSupport {

	private String theme;
	private String role = "row"; //$NON-NLS-1$

	private final IInformationalControlContentProducer producer;

	public String getTheme() {
		return this.theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	protected Composite createToolTipContentArea(final Event event, final Composite parent) {
		
//		MouseTrackListener listener = new MouseTrackListener(){
//
//			public void mouseEnter(MouseEvent e) {
//				final Shell sm=new Shell((Shell) parent.getShell().getParent(),SWT.RESIZE);
//				sm.setLayout(new FillLayout());
//				Rectangle bounds = parent.getShell().getBounds();
//				Rectangle computeTrim = sm.computeTrim(bounds.x, bounds.y, bounds.width,bounds.height);
//				
//				sm.setBounds(computeTrim);
//				internalCreate(event, sm);				
//				sm.open();
//				
//				hide();
//			}
//
//			public void mouseExit(MouseEvent e) {
//				
//			}
//
//			public void mouseHover(MouseEvent e) {
//				
//			}};
//		
//		parent.addMouseTrackListener(listener);
		Composite internalCreate = internalCreate(event, parent);
		//attach(listener, internalCreate);
		
		return internalCreate;
	}



	private void attach(MouseTrackListener listener,Composite parent) {
		for (Control c:parent.getChildren()){
			c.addMouseTrackListener(listener);
			if (c instanceof Composite){
				Composite m=(Composite) c;
				attach(listener,m);
			}
		}
	}

	private Composite internalCreate(Event event, final Composite parent) {
		final ViewerCell toolTipArea = (ViewerCell) this.getToolTipArea(event);
		parent.setBackground(super.getBackgroundColor(event));
		parent.setForeground(super.getForegroundColor(event));
		parent.setBackgroundMode(SWT.INHERIT_DEFAULT);
		if (toolTipArea != null) {
			final Object element = toolTipArea.getElement();
			if (this.producer != null) {
				final Composite create = (Composite) this.producer.create(this, parent,
						element, this.role, this.theme, this.getText(event));

				return create;
			} else {
				final ContentProducerObject tooltipObject = TooltipManager
						.getInstance().getTooltipObject(element, this.role,
								this.theme);
				if (tooltipObject != null) {
					final IInformationalControlContentProducer contentProducer = tooltipObject
							.getContentProducer();
					if (contentProducer != null) {
						final Composite create = (Composite) contentProducer.create(this,
								parent, element, this.role, this.theme, this
										.getText(event));

						return create;
					}
				}
			}
		}
		if (toolTipArea==null){
			return null;
		}
		
		
		String text = LabelManager.getInstance().getDescription(toolTipArea.getElement(), role, theme);
		if (text != null)
			return createTextTooltip(event,parent,text);
		
		return this.createDefaultTooltip(event, parent);
	}

	private Composite createDefaultTooltip(Event event, Composite parent) {
		String text = this.getText(event);
		if (text != null)
			return createTextTooltip(event,parent,text);
		final ViewerCell toolTipArea = (ViewerCell) this.getToolTipArea(event);
		if (toolTipArea != null)
		{
			return createTextTooltip(event,parent,toolTipArea.getElement().toString());
		}
		return null;
	}
	
	private Composite createTextTooltip(Event event, Composite parent, String text)
	{
		final RootElement cs = new RootElement(parent);
		final AbstractUIElement<?> createRichLabel = UIElementFactory
				.createRichLabel(text);
		final Composite contentParent = cs.getContentParent();
		cs.add(createRichLabel);
		cs.setLayout(GridLayoutFactory.fillDefaults().margins(2, 1).create());
		return contentParent;
	}
	
	protected boolean shouldCreateToolTip(Event event) {
		final ViewerCell toolTipArea = (ViewerCell) this.getToolTipArea(event);
		return toolTipArea != null;
	}


	public CustomColumnViewerTooltipSupport(
			ColumnViewer viewer,
			IInformationalControlContentProducer informationalControlContentProducer) {
		super(viewer, ToolTip.NO_RECREATE, false);
		this.producer = informationalControlContentProducer;		
	}
}
