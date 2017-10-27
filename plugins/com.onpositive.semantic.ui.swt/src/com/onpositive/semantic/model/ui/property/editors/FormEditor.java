package com.onpositive.semantic.model.ui.property.editors;

import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.model.api.property.IStatusChangeListener;
import com.onpositive.semantic.model.api.roles.LabelManager;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.CodeAndMessage;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.IProvidesToolbarManager;
import com.onpositive.semantic.model.ui.property.editors.structured.ContributionItemConverter;
import com.onpositive.semantic.model.ui.viewer.IDrawsBorder;
import com.onpositive.semantic.model.ui.viewer.IHasInnerComposite;

public class FormEditor extends CompositeEditor implements IDrawsBorder,
		IProvidesToolbarManager {

	public void removeFromToolbar(
			com.onpositive.semantic.model.ui.actions.IContributionItem action) {
		IContributionItem from = ContributionItemConverter.from(action);
		toolbarContributions.remove(from);
		if (FormEditor.this.isCreated()) {
			final IToolBarManager toolBarManager = FormEditor.this.getControl()
					.getToolBarManager();
			toolBarManager.remove(from);
			toolBarManager.update(true);
		}
	}
	
	public void addToToolbar(
			com.onpositive.semantic.model.ui.actions.IContributionItem bindedAction) {
		IContributionItem from = ContributionItemConverter.from(bindedAction);
		toolbarContributions.add(from);
		if (FormEditor.this.isCreated()) {
			final IToolBarManager toolBarManager = FormEditor.this.getControl()
					.getToolBarManager();
			toolBarManager.add(from);
			toolBarManager.update(true);
		}
	}
	
	public void addToToolbar(IContributionItem bindedAction) {
		FormEditor.this.toolbarContributions.add(bindedAction);
		if (FormEditor.this.isCreated()) {
			final IToolBarManager toolBarManager = FormEditor.this.getControl()
					.getToolBarManager();
			toolBarManager.add(bindedAction);
			toolBarManager.update(true);
		}
	}

	public void addToToolbar(IAction bindedAction) {
		FormEditor.this.toolbarContributions.add(new ActionContributionItem(
				bindedAction));
		if (FormEditor.this.isCreated()) {
			final IToolBarManager toolBarManager = FormEditor.this.getControl()
					.getToolBarManager();
			toolBarManager.add(bindedAction);
			toolBarManager.update(true);
		}
	}

	public AbstractUIElement<?> getUI() {
		return FormEditor.this;
	}

	public void removeFromToolbar(IAction action) {
		final ActionContributionItem actionContributionItem = new ActionContributionItem(
				action);
		FormEditor.this.toolbarContributions.remove(actionContributionItem);
		if (FormEditor.this.isCreated()) {
			final IToolBarManager toolBarManager = FormEditor.this.getControl()
					.getToolBarManager();
			toolBarManager.remove(actionContributionItem);
			toolBarManager.update(true);
		}
	}

	private FormToolkit toolkit;

	private IStatusChangeListener statusListener;

	private final ArrayList<IContributionItem> toolbarContributions = new ArrayList<IContributionItem>();

	private boolean showSeparator;

	private boolean decorateHeading = true;
	private Image currentImage;
	private String description;

	private com.onpositive.semantic.model.api.roles.ImageDescriptor descriptor;

	public FormToolkit getToolkit() {
		return toolkit;
	}

	public boolean isGroup() {
		return true;
	}

	protected void internalSetBinding(IBinding binding2) {
		super.internalSetBinding(binding2);
		if (binding2 == null) {
			return;
		}
		this.statusListener = new IStatusChangeListener() {

			public void statusChanged(IBinding bnd, CodeAndMessage cm) {
				FormEditor.this.refreshStatus(cm);
			}
		};
		binding2.addStatusChangeListener(this.statusListener);
		if (this.isCreated()) {
			this.statusListener.statusChanged(this.getBinding(), this
					.getBinding().getStatus());
		}
	}

	String originalDescription;
	
	protected void internalSet(Object value) {
		if (value == null) {
			return;
		}
		final Object object = value;
		final String description = LabelManager.getInstance().getDescription(
				object, this.getRole(), this.getTheme());
		if (this.bindingInTitle) {
			if ((this.description == null)
					|| !this.description.equals(description)) {
				if (this.isCreated()) {
					if (getBinding() != null
							&& getBinding().getStatus().getCode() == IStatus.OK) {
						this.getControl().setMessage(description);
					}
				}
				this.setToolTipText(description);
				this.originalDescription=this.description;
				this.description = description;
				
			}
			this.setCaption(LabelManager.getInstance().getText( object, this.getRole(), this.getTheme()) );

			if (this.isCreated()) {
				final Image image = SWTImageManager.getImage(object, this.getRole(),
								this.getTheme());
				if (image != this.currentImage) {
					this.getControl().setImage(image);
					this.currentImage = image;
				}
			}
		}
		else{
			this.description=originalDescription;
		}
		super.internalSet(value);
	}

	protected Composite createControl(Composite conComposite) {
		this.toolkit = new FormToolkit(Display.getCurrent());

		final Form createControl = this.toolkit.createForm(conComposite);
		if (descriptor != null) {
			createControl.setImage(SWTImageManager.getImage(descriptor));
		}
		createControl.setSeparatorVisible(this.showSeparator);
		if (this.decorateHeading) {
			this.toolkit.decorateFormHeading(createControl);
		}
		this.toolkit.paintBordersFor(createControl.getBody());
		createControl.getBody().setBackgroundMode(SWT.INHERIT_DEFAULT);
		createControl.getHead().setBackgroundMode(SWT.INHERIT_DEFAULT);
		final IToolBarManager man = createControl.getToolBarManager();
		if (!this.toolbarContributions.isEmpty()) {
			for (final IContributionItem i : this.toolbarContributions) {
				if(i!=null)
					man.add(i);
			}
			man.update(true);
		}
		return createControl;
	}

	public void dispose() {
		super.dispose();
		this.toolkit.dispose();
	}
	@Override
	public void setBindingInTitle(boolean bindingInTitle) {
		super.setBindingInTitle(bindingInTitle);
		if (!isBindingInTitle()){
			this.description=originalDescription;
			if (isCreated()){
				getControl().setMessage(description);
			}
		}
	}

	protected void accept(AbstractUIElement<?> element) {
		for (final Control e : element.getAllControls()) {
			if (!(e instanceof Hyperlink) && (!(e instanceof Section))) {
				this.toolkit.adapt(e, true, false);
			}
			if (e instanceof SashForm) {
				final SashForm s = (SashForm) e;

				s.addMouseTrackListener(new MouseTrackListener() {

					public void mouseEnter(MouseEvent e) {
						s.setBackground(toolkit.getColors().getColor(
								IFormColors.H_HOVER_FULL));
					}

					public void mouseExit(MouseEvent e) {
						s.setBackground(null);
					}

					public void mouseHover(MouseEvent e) {

					}

				});

			}
		}

		if (element instanceof ICompositeElement<?,?>) {
			final ICompositeElement<AbstractUIElement<Control>,?> m = (ICompositeElement) element;
			for (final AbstractUIElement<?> e : m.getChildren()) {
				this.accept(e);
			}
			this.toolkit.paintBordersFor((Composite) element.getControl());
		}
		if (element instanceof IHasInnerComposite) {
			final IHasInnerComposite cm = (IHasInnerComposite) element;
			this.toolkit.paintBordersFor(cm.getComposite());
		}
	}

	public FormEditor() {
		super();
		this.setLayoutManager(new OneElementOnLineLayouter());
	}

	public FormEditor(Object object, boolean autoCommit) {
		super(object, autoCommit);
	}

	public FormEditor(Object object) {
		super(object, true);
	}

	public Composite getContentParent() {
		return this.getControl().getBody();
	}

	public Form getControl() {
		return (Form) super.getControl();
	}

	protected Hyperlink createControlHyperLink(Composite contentParent) {
		Hyperlink createHyperlink = this.toolkit.createHyperlink(contentParent,
				"", SWT.NONE);
		this.toolkit.getHyperlinkGroup().add(createHyperlink);
		return createHyperlink; //$NON-NLS-1$
	}

	protected Label createControlLabel(Composite contentParent) {
		final Label createLabel = this.toolkit.createLabel(contentParent, "");
		return createLabel;
	}

	public void addSeparator(final boolean vertical) {
		this.add(new AbstractUIElement<Label>() {

			protected Label createControl(Composite conComposite) {
				return FormEditor.this.toolkit.createSeparator(conComposite,
						vertical ? SWT.VERTICAL : SWT.HORIZONTAL);
			}

		});
	}

	public boolean isShowSeparator() {
		return this.showSeparator;
	}

	public void setShowSeparator(boolean showSeparator) {
		this.showSeparator = showSeparator;
		if (this.isCreated()) {
			this.setShowSeparator(showSeparator);
		}
	}

	public boolean isDecorateHeading() {
		return this.decorateHeading;
	}

	public void setDecorateHeading(boolean decorateHeading) {
		this.decorateHeading = decorateHeading;
		if (this.isCreated()) {
			this.recreate();
		}
	}

	@SuppressWarnings("unchecked")
	public <R> R getService(Class<R> clazz) {
		if (clazz == IProvidesToolbarManager.class) {
			return (R) this;
		}
		if (clazz == IDrawsBorder.class) {
			if (this.toolkit != null) {
				final int borderStyle = this.toolkit.getBorderStyle();
				if (borderStyle == SWT.NULL) {
					return (R) this;
				}
				return null;
			}
		}
		if (clazz == FormToolkit.class) {
			return (R) this.toolkit;
		}
		return super.getService(clazz);
	}

	private void refreshStatus(CodeAndMessage cm) {
		if (this.isCreated()) {
			if (cm.getCode() == IStatus.OK) {
				this.getControl().setMessage(this.description);
				return;
			}
			int code = cm.getCode();
			switch (code) {
			case IStatus.ERROR:
				code = IMessageProvider.ERROR;
				break;
			case IStatus.WARNING:
				code = IMessageProvider.WARNING;
				break;
			case IStatus.INFO:
				code = IMessageProvider.INFORMATION;
				break;
			default:
				break;
			}

			this.getControl().setMessage(cm.getMessage(), code);
		}
	}

	public String toString() {
		return "Form " + this.getCaption(); //$NON-NLS-1$
	}

	public void setImageDescriptor(
			com.onpositive.semantic.model.api.roles.ImageDescriptor imageDescriptor) {
		this.descriptor = imageDescriptor;
	}

}
