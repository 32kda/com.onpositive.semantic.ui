package com.onpositive.semantic.model.ui.property.editors.structured;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.api.property.adapters.IRoleDependentLabelProvider;
import com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.ui.generic.widgets.ICanBeReadOnly;
import com.onpositive.semantic.model.ui.generic.widgets.IUseLabelsForNull;
import com.onpositive.semantic.model.ui.viewer.structured.RealmContentProvider;

/**
 * 
 * @author Pavel
 * 
 *         Supports: Selection of one value from arbitrary realm, also if
 *         associated IRange supports no value for a given property allows
 *         setting it.
 * @param <T>
 */
public class ComboEnumeratedValueSelector<T> extends
		AbstractEnumeratedValueSelector<T> implements ICanBeReadOnly<Control>, IUseLabelsForNull {

	private ITextLabelProvider tProvider;

	private boolean selectDefault=false;

	public boolean isSelectDefault() {
		return selectDefault;
	}

	@HandlesAttributeDirectly("selectDefault")
	public void setSelectDefault(boolean selectDefault) {
		this.selectDefault = selectDefault;
	}

	protected void initializeContent(IBinding bm) {
		super.initializeContent(bm);
		if (!bm.isRequired()) {
			((ComboViewer) this.viewer).add(this);
		}
	}

	protected void postProcessSelection(HashSet<Object> elements) {
		if (elements.isEmpty()) {
			final IBinding bm = this.getBinding();
			if (!bm.isRequired()) {
				elements.add(this);
			}
			if (isSelectDefault()) {
				@SuppressWarnings("unchecked")
				IRealm<T> realm = (IRealm<T>) getRealm();
				if (realm != null) {
					Collection<T> contents = realm.getContents();
					if (!contents.isEmpty()) {
						elements.add(contents.iterator().next());
					}
					commitToBinding(elements);
				}
			}
		} else {
			elements.remove(this);
		}
		this.viewer.setSelection(new StructuredSelection(elements.toArray()));
	}

	protected void processSelection(StructuredSelection ss) {
		if (ss.getFirstElement() == this) {
			super.processSelection(new StructuredSelection());
			if (this.tProvider != null) {
				if (!ss.isEmpty()) {
					if( useLabelProviderForNull )
						this.viewer.getControl().setToolTipText(
								this.tProvider.getDescription(null));
					else
						this.viewer.getControl().setToolTipText( "" ) ;
				} else {
					this.viewer.getControl().setToolTipText(null);
				}
			}
		} else {
			super.processSelection(ss);
			if (this.tProvider != null) {
				if (!ss.isEmpty()) {
					this.viewer.getControl()
							.setToolTipText(
									this.tProvider.getDescription(ss
											.getFirstElement()));
				} else {
					this.viewer.getControl().setToolTipText(null);
				}
			}
		}
	}

	protected void initContentProvider() {
		final RealmContentProvider realmLazyContentProvider = new RealmContentProvider();
		this.viewer.setContentProvider(realmLazyContentProvider);
		this.provider = realmLazyContentProvider;
	}

	protected void initLabelProvider(final ITextLabelProvider pr) {
		this.tProvider = pr;
		if (pr != null) {
			final LabelProvider ll = new LabelProvider() {

				public String getText(Object element) {
					if (pr instanceof IRoleDependentLabelProvider) {
						final IRoleDependentLabelProvider la = (IRoleDependentLabelProvider) pr;
						la.setRole(ComboEnumeratedValueSelector.this.getRole());
						la.setTheme(ComboEnumeratedValueSelector.this
								.getTheme());
					}
					if (element == ComboEnumeratedValueSelector.this) {
						
						if( useLabelProviderForNull )
							return pr.getText(null);
						else
							return "" ;
					}
					return pr.getText(element);
				}
			};
			this.viewer.setLabelProvider(ll);
		} else {
			final LabelProvider ll = new LabelProvider() {

				public String getText(Object element) {
					if (element == null) {
						return "";
					}
					if (element == ComboEnumeratedValueSelector.this) {
						return "";
					}
					return element.toString();
				}
			};
			this.viewer.setLabelProvider(ll);
		}
	}

	public ComboEnumeratedValueSelector() {
		this.setInstallRequiredDecoration(false);
	}

	public ComboEnumeratedValueSelector(IBinding bnd) {
		this.setInstallRequiredDecoration(false);
		this.setBinding(bnd);
	}
	boolean isCCombo=false;

	private boolean readOnly=true;

	public boolean isCCombo() {
		return isCCombo;
	}

	public void setCCombo(boolean isCCombo) {
		this.isCCombo = isCCombo;
	}

	protected StructuredViewer createViewer(Composite parent) {
		int style2 = readOnly?(SWT.READ_ONLY|SWT.BORDER):SWT.BORDER;
		if (isCCombo){
		
		final ComboViewer comboViewer = new ComboViewer(new CCombo(parent, style2));
		
		this.installDecorations(comboViewer.getControl());
		return comboViewer;
		}
		else{
			final ComboViewer comboViewer = new ComboViewer(new Combo(parent,style2));
			
			this.installDecorations(comboViewer.getControl());
			return comboViewer;	
		}
	}


	public void setReadOnly(boolean parseBoolean) {
		this.readOnly=parseBoolean;
	}

	public boolean isReadOnly() {
		return readOnly;
	}



	public void editorValueApplied(Object element, Object value) {
		
	}
	private boolean useLabelProviderForNull;
	public void setUseLabelProviderForNull(boolean useLabelProviderForNull) {
		this.useLabelProviderForNull = useLabelProviderForNull;
	}

}
