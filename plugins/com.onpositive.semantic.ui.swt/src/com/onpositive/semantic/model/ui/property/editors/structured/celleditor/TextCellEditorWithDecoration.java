package com.onpositive.semantic.model.ui.property.editors.structured.celleditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.contentassist.BasicContentProposal;
import com.onpositive.commons.contentassist.ContentProposalAdapter;
import com.onpositive.commons.contentassist.ContentProposalProvider;
import com.onpositive.commons.contentassist.IContentProposalProvider2;
import com.onpositive.commons.contentassist.IHasContentAssist;
import com.onpositive.commons.contentassist.ProposalConversion;
import com.onpositive.commons.contentassist.RealmProposalProvider;
import com.onpositive.semantic.common.ui.roles.IInformationalControlContentProducer;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyMetaData;
import com.onpositive.semantic.model.api.property.IStatusChangeListener;
import com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider;
import com.onpositive.semantic.model.api.property.adapters.NotFoundException;
import com.onpositive.semantic.model.api.roles.IImageDescriptorProvider;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.CodeAndMessage;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.ui.generic.IContentAssistConfiguration;

public class TextCellEditorWithDecoration extends TextCellEditor implements
		IRichCellEditor, IHasContentAssist {

	protected ControlDecoration errorDecoration;

	protected ControlDecoration warningDecoration;

	protected IProperty property;

	protected Binding bnd;

	private final boolean useLabelProviderForNull = false;

	private static final String SEPARATOR_CHARACTERS = ",";

	public void deactivate() {
		this.errorDecoration.hideHover();
		super.deactivate();
	}

	char[] autoActivationCharacters;

	protected Object initValue;

	private LabelProvider setlabelProvider;

	private ContentProposalAdapter contentAssist;

	private com.onpositive.semantic.model.ui.generic.IContentProposalProvider contentAssistProposalProvider;

	private static final boolean IS_CONTENT_ASSIST_ENABLED = true;

	private IInformationalControlContentProducer infoContentAssist;

	
	public boolean isValueValid() {
		return this.bnd.getStatus().getCode() != IStatus.ERROR;
	}

	public TextCellEditorWithDecoration(Composite control, Object parent,
			IProperty property) {
		super();
		this.property = property;
		this.bnd = new Binding(parent, property, null);
		this.bnd.setAutoCommit(false);
		this.create(control);
	}

	private FieldDecoration getErrorDecoration() {
		final FieldDecorationRegistry registry = FieldDecorationRegistry
				.getDefault();
		final FieldDecoration dec = registry
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		return dec;
	}

	protected void focusLost() {
		if (this.contentAssist != null) {
			this.contentAssist.dispose();
			this.contentAssist = null;
		}
		super.focusLost();
	}

	public void dispose() {
		if (!this.text.isDisposed()) {
			this.txt = (String) super.doGetValue();
		}
		super.dispose();
	}

	String txt;

	public String getText() {
		if (this.text.isDisposed()) {
			return this.txt;
		}
		return (String) super.doGetValue();
	}

	public ArrayList<String> getContent() {
		final ArrayList<String> prevs = new ArrayList<String>();
		IPropertyMetaData propertyMetaData = this.property.getPropertyMetaData();
		if (propertyMetaData!=null&&propertyMetaData.isMultivalue()) {
			final String text2 = this.getText();
			int a = text2.length();
			if (this.SEPARATOR_CHARACTERS != null) {
				int b = a - 1;
				for (; b > 0; b--) {
					if (this.SEPARATOR_CHARACTERS.indexOf(text2.charAt(b)) != -1) {
						final String substring = text2.substring(b + 1, a);
						final String trim = substring.trim();
						if (trim.length() > 0) {
							if (!prevs.contains(trim)) {
								prevs.add(trim);
							}
						}
						a = b;
					}
				}
				final String substring = a != text2.length() ? text2.substring(
						b, a) : text2;
				final String trim = substring.trim();
				if (trim.length() > 0) {
					if (!prevs.contains(trim)) {
						prevs.add(trim);
					}
				}
			}
		} else {
			prevs.add(this.getText().trim());
		}
		Collections.reverse(prevs);
		return prevs;
	}

	protected void doSetValue(Object value) {
		this.initValue = value;
		IPropertyMetaData propertyMetaData = this.property.getPropertyMetaData();
		ITextLabelProvider adapter = propertyMetaData.getAdapter(ITextLabelProvider.class);
		if (adapter==null){
			adapter=bnd.getAdapter(ITextLabelProvider.class);
		}
		this.bnd.setValue(value, null);
		if (adapter != null) {
			if ((propertyMetaData!=null&&propertyMetaData.isMultivalue())
					&& (value instanceof Collection)) {
				final Collection<?> c = (Collection<?>) value;
				if (c.isEmpty()) {
					if (this.useLabelProviderForNull) {
						this.setText(adapter.getText(null));
					} else {
						this.setText(""); //$NON-NLS-1$
					}
				} else {
					final Object next = c.iterator().next();
					this.setText(adapter.getText(next));
				}
			} else {
				if ((value == null) && !this.isUseLabelProviderForNull()) {
					this.setText(""); //$NON-NLS-1$
				} else {
					if (value instanceof Collection) {
						final StringBuilder bl = new StringBuilder();
						final Collection<?> c = (Collection<?>) value;
						int pos = 0;
						for (final Object o : c) {
							bl.append(adapter.getText(o));
							if (pos != c.size() - 1) {
								bl.append(this.SEPARATOR_CHARACTERS.charAt(0));
							}
							pos++;
						}
						this.setText(value != null ? bl.toString() : ""); //$NON-NLS-1$
					} else {
						this.setText(adapter.getText(value));
					}
				}
			}
		} else {
			if (value instanceof Collection) {
				final StringBuilder builder = new StringBuilder();
				final Collection<?> c = (Collection<?>) value;
				int pos = 0;
				for (final Object o : c) {
					builder.append(o.toString());
					
					if (pos != c.size() - 1) {
						builder.append(this.SEPARATOR_CHARACTERS.charAt(0));
					}
					pos++;
				}
				this.setText(builder.toString()); //$NON-NLS-1$
			} else {
				this.setText(value != null ? value.toString() : ""); //$NON-NLS-1$
			}
		}

	}

	private boolean isUseLabelProviderForNull() {
		return false;
	}

	public void setText(String text) {
		if (this.contentAssist != null) {
			this.contentAssist.setNoActivate(true);
		}
		super.doSetValue(text);
		if (this.contentAssist != null) {
			this.contentAssist.setNoActivate(false);
		}
	}

	protected Object doGetValue() {
		if (this.bnd.getStatus().getCode() != IStatus.ERROR) {
			return this.doGetValue1();
		} else {
			return this.initValue;
		}
	}

	protected void editOccured(ModifyEvent e) {
		if (this.text.isDisposed()) {
			return;
		}
		super.editOccured(e);
	}

	protected Object doGetValue1() {
		final String text2 = this.getText();
		IPropertyMetaData propertyMetaData = this.property.getPropertyMetaData();
		if (propertyMetaData!=null&&propertyMetaData.isMultivalue()) {
			final ArrayList<String> content = this.getContent();
			Object adjustStringIfNeeded;
			try {
				adjustStringIfNeeded = this.bnd.lookupByLabels(content);
			} catch (final NotFoundException e) {
				return null;
			}
			return adjustStringIfNeeded;
		} else {
			Object adjustStringIfNeeded;
			try {
				adjustStringIfNeeded = this.bnd.lookupByLabel(text2);
				IProperty property2 = this.bnd.getProperty();
				if(property2!=null){
					IPropertyMetaData propertyMetaData2 = property2.getPropertyMetaData();
					ITextLabelProvider adapter = propertyMetaData2.getAdapter(ITextLabelProvider.class);
					if (adapter!=null){
						IRealm<Object> realm = bnd.getRealm();
						if (realm!=null){
							for (Object o:realm){
								String text3 = adapter.getText(o);
								if (text3!=null&&text3.equals(adjustStringIfNeeded)){
									return o;
								}
							}
						}
					}
				}
			} catch (final NotFoundException e) {
				return null;
			}
			return adjustStringIfNeeded;
		}
	}

	protected Control createControl(Composite parent) {
		final long l0 = System.currentTimeMillis();
		final Composite cm = new Composite(parent, SWT.NONE);
		// cm.setBackground(Display.getCurrent().getSystemColor(SWT.
		// COLOR_WIDGET_BACKGROUND));
		cm.setLayoutDeferred(true);
		// cm.setBackground(parent.getBackground());
		final Control createControl = super.createControl(cm);
		final GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginHeight = 1;
		gridLayout.marginRight = 3;
		cm.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				TextCellEditorWithDecoration.this.bnd.dispose();
			}
		});
		cm.setLayout(gridLayout);
		this.text.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		text.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		this.text.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				final Object doGetValue1 = TextCellEditorWithDecoration.this
						.doGetValue1();
				if (TextCellEditorWithDecoration.this.isValueValid()
						|| (doGetValue1 != null)) {
					TextCellEditorWithDecoration.this.bnd.setValue(doGetValue1,
							null);
				}
			}

		});
		createControl.setLayoutData(new GridData(GridData.FILL_BOTH));
		if (this.errorDecoration == null) {
			this.errorDecoration = new ControlDecoration(this.text, SWT.TOP
					| SWT.RIGHT);
		}
		this.errorDecoration.setShowOnlyOnFocus(true);
		final FieldDecoration dec = this.getErrorDecoration();
		this.errorDecoration.setImage(dec.getImage());
		this.errorDecoration.setDescriptionText(dec.getDescription());
		this.errorDecoration.setMarginWidth(1);

		this.bnd.addStatusChangeListener(new IStatusChangeListener() {

			public void statusChanged(IBinding bnd, CodeAndMessage cm) {
				if (cm.getCode() == IStatus.OK) {
					TextCellEditorWithDecoration.this.errorDecoration.hide();
					TextCellEditorWithDecoration.this.errorDecoration
							.setShowHover(false);
					TextCellEditorWithDecoration.this.errorDecoration
							.hideHover();
				} else if (cm.getCode() == IStatus.WARNING) {
					TextCellEditorWithDecoration.this.errorDecoration
							.setImage(FieldDecorationRegistry
									.getDefault()
									.getFieldDecoration(
											FieldDecorationRegistry.DEC_WARNING)
									.getImage());
					TextCellEditorWithDecoration.this.errorDecoration
							.setDescriptionText(cm.getMessage());
					TextCellEditorWithDecoration.this.errorDecoration.show();
					TextCellEditorWithDecoration.this.errorDecoration
							.setShowHover(true);
					TextCellEditorWithDecoration.this.errorDecoration
							.showHoverText(cm.getMessage());
				} else if (cm.getCode() == IStatus.ERROR) {
					TextCellEditorWithDecoration.this.errorDecoration
							.setImage(FieldDecorationRegistry.getDefault()
									.getFieldDecoration(
											FieldDecorationRegistry.DEC_ERROR)
									.getImage());
					TextCellEditorWithDecoration.this.errorDecoration
							.setDescriptionText(cm.getMessage());
					TextCellEditorWithDecoration.this.errorDecoration.show();
					TextCellEditorWithDecoration.this.errorDecoration
							.setShowHover(true);
					TextCellEditorWithDecoration.this.errorDecoration
							.showHoverText(cm.getMessage());
				}
			}

		});
		this.errorDecoration.hide();

		this.installContentAssist(this.text);
		cm.setLayoutDeferred(false);
		final long l1 = System.currentTimeMillis();
		System.out.println(l1 - l0);
		return cm;
	}

	private IContentProposalProvider proposalProvider;

	private LabelProvider labelProvider;

	protected void keyReleaseOccured(KeyEvent keyEvent) {
		if (keyEvent.character == '\u001b') { // Escape character
			if ((this.contentAssist != null) && this.contentAssist.isOpened()) {
				return;
			}
			super.keyReleaseOccured(keyEvent);
		}
		super.keyReleaseOccured(keyEvent);
	}

	private ContentProposalAdapter createContentAsistAdapter(Text text) {
		if (this.contentAssist != null) {
			return this.contentAssist;
		}

		text.setData(ContentProposalAdapter.POPUP_CONTENT_CREATOR,
				this.infoContentAssist);
		final ContentProposalAdapter createContentProposalAdapter = ContentProposalProvider
				.createContentProposalAdapter(text, new TextContentAdapter(),
						this.proposalProvider, null);
		createContentProposalAdapter.setCellEditor(true);
		return createContentProposalAdapter;
	}

	private void initProvider() {
		this.proposalProvider = new IContentProposalProvider() {

			public IContentProposal[] getProposals(String contents, int position) {
				if (TextCellEditorWithDecoration.this.contentAssistProposalProvider == null) {
					return new IContentProposal[0];
				}
				com.onpositive.semantic.model.ui.generic.IContentProposal[] proposals = TextCellEditorWithDecoration.this.contentAssistProposalProvider
						.getProposals(contents, position);				
				return ProposalConversion.convert(proposals);
			}
		};
	}

	@SuppressWarnings("unchecked")
	protected void installContentAssist(Text text) {
		this.initProvider();
		if (this.getBinding() != null) {

			final IContentAssistConfiguration prProvider = this.getBinding()
					.getAdapter(IContentAssistConfiguration.class);
			if (prProvider != null) {
				this.contentAssist = this.createContentAsistAdapter(text);
				
				this.contentAssist.setPropagateKeys(false);
				com.onpositive.semantic.model.ui.generic.IContentProposalProvider proposalProvider2 = prProvider.getProposalProvider();
				this.contentAssistProposalProvider=proposalProvider2;
				if (contentAssistProposalProvider instanceof IContentProposalProvider2){
					IContentProposalProvider2 dd= (IContentProposalProvider2) contentAssistProposalProvider;
					dd.setContentAssistOwner(this);
				}
				this.contentAssist.setProposalAcceptanceStyle(prProvider
						.getProposalAcceptanceStyle());
				final ITextLabelProvider proposalLabelProvider = prProvider.getProposalLabelProvider();
				this.contentAssist.setLabelProvider(new LabelProvider(){
					
					@Override
					public String getText(Object element) {
						return proposalLabelProvider.getText(element);
					}
					
					@Override
					public Image getImage(Object element) {
						if (proposalLabelProvider instanceof IImageDescriptorProvider){
							IImageDescriptorProvider p=(IImageDescriptorProvider) proposalLabelProvider;
							return SWTImageManager.getImage(p.getImageDescriptor(element));
						}
						return super.getImage(element);
					}
					
				}); 
				this.contentAssist.setPropagateKeys(true);
				this.contentAssist.setAutoActivationCharacters(prProvider
						.getAutoactivationCharacters());
				this.contentAssist.setFilterStyle(prProvider.getFilterStyle());
				this.contentAssist.setEnabled(TextCellEditorWithDecoration.IS_CONTENT_ASSIST_ENABLED);
				return;
			}
			final IRealm<?> adapter = this.getBinding().getRealm();
			if ((adapter != null)
					|| (this.contentAssistProposalProvider != null)) {
				if (this.contentAssist == null) {

					this.contentAssist = this.createContentAsistAdapter(text);

					this.contentAssist
							.setProposalAcceptanceStyle(!this.getBinding()
									.allowsMultiValues() ? ContentProposalAdapter.PROPOSAL_REPLACE
									: ContentProposalAdapter.PROPOSAL_INSERT);
					this.contentAssist.setAutoActivationDelay(100);
					this.contentAssist
							.setAutoActivationCharacters(this.autoActivationCharacters);
					this.labelProvider = new LabelProvider() {

						public Image getImage(Object element) {
							if (element instanceof BasicContentProposal) {
								final BasicContentProposal ps = (BasicContentProposal) element;
								if (TextCellEditorWithDecoration.this.setlabelProvider != null) {
									return TextCellEditorWithDecoration.this.setlabelProvider
											.getImage(ps.getElement());
								} else {
									return SWTImageManager.getImage(ps.getElement(),
													TextCellEditorWithDecoration.this
															.getContentAssistRole() == null ? "row" : TextCellEditorWithDecoration.this.getContentAssistRole(), TextCellEditorWithDecoration.this.getTheme()); //$NON-NLS-1$
								}
							}
							if (TextCellEditorWithDecoration.this.setlabelProvider != null) {
								return TextCellEditorWithDecoration.this.setlabelProvider
										.getImage(element);
							}
							return super.getImage(element);
						}

						public String getText(Object element) {
							if (element instanceof IContentProposal) {
								final IContentProposal ps = (IContentProposal) element;
								final String label = ps.getLabel();
								return label == null ? ps.getContent() : label;
							}
							return element.toString();
						}

					};
					this.contentAssist.setLabelProvider(this.labelProvider);
					this.contentAssist.setEnabled(this.IS_CONTENT_ASSIST_ENABLED);
				}
				if (adapter != null) {
					final IRealm<Object> realm = this.getBinding().getRealm();
					if (realm != null) {
						this.contentAssistProposalProvider = new RealmProposalProvider(
								this, realm);
						this.contentAssist.setEnabled(true);
					} else {
						if (this.contentAssistProposalProvider == null) {
							this.contentAssist.setEnabled(false);
						} else {
							this.contentAssist.setEnabled(true);
						}
					}
				}

				final ITextLabelProvider adapter2 = this.getBinding()
						.getAdapter(ITextLabelProvider.class);
				if ((adapter2 != null) && (adapter2 instanceof LabelProvider)) {
					this.setlabelProvider = (LabelProvider) adapter2;
				}
			}
		}
	}

	public String getTheme() {
		return null;
	}

	public Binding getBinding() {
		return this.bnd;
	}

	public String getSeparatorCharacters() {
		return this.SEPARATOR_CHARACTERS;
	}

	public String getContentAssistRole() {
		return "row";
	}

	public boolean handlesDown() {
		return false;
	}

	public boolean handlesEnd() {
		return this.handlesRight();
	}

	public boolean handlesHome() {
		return this.handlesLeft();
	}

	public boolean handlesLeft() {
		final boolean b = this.text.getCaretPosition() > 0;
		return b;
	}

	public boolean handlesPageDown() {
		return false;
	}

	public boolean handlesPageUp() {
		return false;
	}

	public boolean handlesRight() {
		final int length = this.text.getText().length();
		final boolean b = this.text.getCaretPosition() < length;
		return b || (this.text.getSelectionCount() == length);
	}

	public boolean handlesUp() {
		return false;
	}

	public void initCellEditor(Object owner, IRichCellEditorSupport support) {

	}

	public void mouseDownOnElement(Object data) {
	}

	public ContentProposalAdapter getContentAssist() {
		return this.contentAssist;
	}
}