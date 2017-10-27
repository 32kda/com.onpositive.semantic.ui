package com.onpositive.semantic.model.ui.property.editors;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.UIElementFactory;
import com.onpositive.commons.platform.configuration.IAbstractConfiguration;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.api.roles.LabelManager;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.IProvidesToolbarManager;
import com.onpositive.semantic.model.ui.generic.IProvidesUI;
import com.onpositive.semantic.model.ui.property.editors.structured.ContributionItemConverter;

public class SectionEditor extends CompositeEditor implements
		IProvidesToolbarManager, IProvidesUI {


	public void removeFromToolbar(
			com.onpositive.semantic.model.ui.actions.IContributionItem action) {
		mn.remove(ContributionItemConverter.from(action));
	}
	
	public void addToToolbar(
			com.onpositive.semantic.model.ui.actions.IContributionItem bindedAction) {
		if (this.mn == null) {
			this.mn = new ToolBarManager();
			if (isCreated()){
				installToolbar(getControl());
			}
		}
		
		mn.add(ContributionItemConverter.from(bindedAction));
	}

	private boolean expanded;
	private boolean hasTitleBar = true;
	private ToolBarManager mn;
	private boolean isExpandable = true;

	public void internalLoadConfiguration(IAbstractConfiguration configuration) {
		final String sm = configuration.getStringAttribute("expanded");
		if ((sm != null) && (sm.length() > 0)) {
			this.expanded = Boolean.parseBoolean(sm);
		}
		if (!isExpandable){
			this.expanded=true;
		}
		super.loadConfiguration(configuration);
	}

	public void internlStoreConfiguration(IAbstractConfiguration configuration) {
		if (this.getControl()!=null){
		configuration
				.setBooleanAttribute("expanded", this.getControl().isExpanded());
		}
		super.storeConfiguration(configuration);
	}

	public SectionEditor() {
		super();
		getLayoutHints().setGrabHorizontal(true);
	}

	public SectionEditor(Object object, boolean autoCommit) {
		super(object, autoCommit);
		getLayoutHints().setGrabHorizontal(true);
	}

	public boolean isGroup() {
		return true;
	}

	public Composite getContentParent() {
		return (Composite) this.getControl().getClient();
	}

	protected void internalSetBinding(IBinding binding2) {
		if (this.getCaption() == null&&binding2!=null) {
			this.setCaption(LabelManager.getInstance().getText(
					this.getObject(), this.getRole(), this.getTheme()));

			final String description = LabelManager.getInstance().getDescription(
					this.getObject(), this.getRole(), this.getTheme());
			this.setToolTipText(description);
			if (this.isCreated()) {
				((FormText) this.getControl().getDescriptionControl()).setText(
						UIElementFactory.adaptText(description), true, false);
			}
		}
	}

	public Section getControl() {
		return (Section) super.getControl();
	}

	protected Composite createControl(Composite conComposite) {
		final FormToolkit service = this.getService(FormToolkit.class);
		Section section = null;
		if (service != null) {
			section = service.createSection(conComposite,
					(this.hasTitleBar ? ExpandableComposite.TITLE_BAR : SWT.NONE)
							| this.getExpandStyle());
		} else {
			section = new Section(conComposite, this.calcStyle()
					| (this.hasTitleBar ? ExpandableComposite.TITLE_BAR : SWT.NONE)
					| this.getExpandStyle());
		}
		final FormText descriptionControl = new FormText(section, SWT.NONE);
		if (service != null) {
			service.adapt(descriptionControl);
		}
		this.installToolbar(section);
		section.setDescriptionControl(descriptionControl);
		final Composite c = new Composite(section, SWT.NONE);
		c.setLayout(new FillLayout(SWT.VERTICAL));

		if (service != null) {
			service.adapt(c);
		}

		section.setClient(c);
		return section;
	}

	private int getExpandStyle() {
		return this.isExpandable ? ExpandableComposite.TWISTIE : SWT.NONE;
	}

	public boolean isExpandable() {
		return this.isExpandable;
	}
	@HandlesAttributeDirectly("expandable")
	public void setExpandable(boolean isExpandable) {
		if (this.isExpandable != isExpandable) {
			this.isExpandable = isExpandable;
			if (this.isCreated()) {
				this.recreate();
			}
		}
	}

	private void installToolbar(Section section) {
		if (this.mn != null) {
			final ToolBar createControl = this.mn.createControl(section);
			section.setTextClient(createControl);
			section.layout(true, true);
		}
	}

	public boolean isExpanded() {
		return this.expanded;
	}
	
	@HandlesAttributeDirectly("expanded")
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
		if (this.isCreated()) {
			this.getControl().setExpanded(expanded);
		}
	}

	@SuppressWarnings("unchecked")
	public <R> R getService(Class<R> clazz) {
		if (clazz == IProvidesToolbarManager.class) {
			return (R) this;
		}
		return super.getService(clazz);
	}

	public boolean isHasTitleBar() {
		return this.hasTitleBar;
	}

	@HandlesAttributeDirectly("decorateTitle")
	public void setHasTitleBar(boolean hasTitleBar) {
		this.hasTitleBar = hasTitleBar;
		if (this.isCreated()) {
			this.recreate();
		}
	}

	public void addToToolbar(IContributionItem bindedAction) {
		if (this.mn == null) {
			this.mn = new ToolBarManager();
			if (isCreated()){
				installToolbar(getControl());
			}
		}
		this.mn.add(bindedAction);
		mn.update(true); //TODO
		
	}

	public void addToToolbar(IAction bindedAction) {
		if (this.mn == null) {
			this.mn = new ToolBarManager();
			if (isCreated()){
				installToolbar(getControl());
			}
		}
		this.mn.add(bindedAction);
		mn.update(true); //TODO
		
	}

	public AbstractUIElement<?> getUI() {
		return this;
	}

	public void removeFromToolbar(IAction action) {
		if (this.mn == null) {
			this.mn = new ToolBarManager();
		}
		this.mn.remove(new ActionContributionItem(action));
	}

	public void internalCreate() {
		super.internalCreate();
		if (this.expanded) {
			this.getControl().setExpanded(this.expanded);
		} else {
			// workarounding layout bug
			this.getContentParent().setVisible(false);
			this.getControl().setExpanded(false);
		}
	}
}
