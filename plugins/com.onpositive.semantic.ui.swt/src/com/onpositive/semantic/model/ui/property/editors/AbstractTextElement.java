package com.onpositive.semantic.model.ui.property.editors;

import java.util.ArrayList;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.contentassist.BasicContentProposal;
import com.onpositive.commons.contentassist.ContentProposalAdapter;
import com.onpositive.commons.contentassist.IContentProposalProvider2;
import com.onpositive.commons.contentassist.IHasContentAssist;
import com.onpositive.commons.contentassist.ProposalConversion;
import com.onpositive.commons.contentassist.RealmProposalProvider;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.common.ui.roles.IInformationalControlContentProducer;
import com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider;
import com.onpositive.semantic.model.api.property.adapters.NotFoundException;
import com.onpositive.semantic.model.api.roles.IImageDescriptorProvider;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IFactory;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.ui.generic.IContentAssistConfiguration;
import com.onpositive.semantic.model.ui.generic.widgets.ISingleLineTextElement;
import com.onpositive.semantic.ui.core.IHasErrorDecoration;

public abstract class AbstractTextElement<T, C extends Control> extends
		TextElement<T, C> implements IHasErrorDecoration, IHasContentAssist,ISingleLineTextElement<C> {

	public final class DefaultContentProposalLabelProvider extends
			LabelProvider {
		public Image getImage(Object element) {
			if (element instanceof BasicContentProposal) {
				final BasicContentProposal ps = (BasicContentProposal) element;
				if (AbstractTextElement.this.setlabelProvider != null) {
					return AbstractTextElement.this.setlabelProvider
							.getImage(ps.getElement());
				} else {
					return SWTImageManager.getImage(
									ps.getElement(),
									AbstractTextElement.this.contentAssistRole == null ? "row" : AbstractTextElement.this.contentAssistRole, AbstractTextElement.this.getTheme()); //$NON-NLS-1$
				}
			}
			if (AbstractTextElement.this.setlabelProvider != null) {
				return AbstractTextElement.this.setlabelProvider
						.getImage(element);
			}
			return SWTImageManager.getImage(
							element,
							AbstractTextElement.this.contentAssistRole == null ? "row" : AbstractTextElement.this.contentAssistRole, AbstractTextElement.this.getTheme()); //$NON-NLS-1$
		}

		public String getText(Object element) {
			if (element instanceof IContentProposal) {
				final IContentProposal ps = (IContentProposal) element;
				
				final String label = ps.getLabel();
				return label == null ? ps.getContent() : label;
			}
			if (AbstractTextElement.this.setlabelProvider != null) {
				return AbstractTextElement.this.setlabelProvider
						.getText(element);
			}
			return element.toString();
		}
	}

	char[] autoActivationCharacters;
	IFactory selector;
	String contentAssistRole;
	boolean isContentAssistEnabled = true;

	//@HandlesAttribute( "enablement" )
	public void setEnabled(boolean enabled) {
		if (!enabled) {

			if (this.isCreated()) {
				if (this.contentAssist != null) {
					if (this.contentAssist.isOpened()) {
						this.contentAssist.closeProposalPopup();

					}
				}
			}
		}
		if (this.contentAssist != null) {
			this.contentAssist.setEnabled(enabled);
		}
		super.setEnabled(enabled);
	}

	com.onpositive.semantic.model.ui.generic.IContentProposalProvider contentAssistProposalProvider;
	protected ContentProposalAdapter contentAssist;
	protected IContentProposalProvider proposalProvider;
	private LabelProvider labelProvider;
	private LabelProvider setlabelProvider;
	private Button selectorButton;
	protected IInformationalControlContentProducer infoContentAssist;

	public ContentProposalAdapter getContentAssist() {
		return this.contentAssist;
	}

	public AbstractTextElement(String caption) {
		this.setCaption(caption);
		this.initProvider();
	}

	public AbstractTextElement(IBinding binding) {
		this.setBinding(binding);
		this.initProvider();
	}

	private void initProvider() {
		this.proposalProvider = new IContentProposalProvider() {

			public IContentProposal[] getProposals(String contents, int position) {
				if (AbstractTextElement.this.contentAssistProposalProvider == null) {
					return new IContentProposal[0];
				}
				com.onpositive.semantic.model.ui.generic.IContentProposal[] proposals = AbstractTextElement.this.contentAssistProposalProvider
						.getProposals(contents, position);
				
				return ProposalConversion.convert(proposals);
			}
		};
	}

	public AbstractTextElement() {
		this.initProposalProvider();
	}

	private void initProposalProvider() {
		initProvider();
	}

	protected C createControl(Composite conComposite) {
		final C text = this.internalCreateControl(conComposite);

		this.installSelector(conComposite);
		text.addListener(SWT.Modify, new Listener() {

			public void handleEvent(Event event) {
				if (AbstractTextElement.this.shouldIgnoreChanges() ) {
					return;
				}
				final IBinding binding2 = AbstractTextElement.this.getBinding();
				if (binding2 != null) {
					doCommit(binding2);
				}
			}
		});
		this.installDecorations(text);
		this.installContentAssist(text);
		return text;
	}

	protected abstract C internalCreateControl(Composite conComposite);

	protected void internalSetText(String txt) {
		boolean en = false;
		if (this.contentAssist != null) {
			if (this.contentAssist.isEnabled()) {
				this.contentAssist.setEnabled(false);
				en = true;
			}
		}
		super.internalSetText(txt);
		if (en) {
			this.contentAssist.setEnabled(true);
		}
	}

	protected void installSelector(Composite conComposite) {
		if (this.selector != null) {
			if (this.selectorButton == null) {
				this.selectorButton = new Button(conComposite, SWT.PUSH);
				this.selectorButton.setText(this.selector.getName());
				this.selectorButton.addListener(SWT.Selection, new Listener() {

					public void handleEvent(Event event) {

						Object value = AbstractTextElement.this.selector
								.getValue(AbstractTextElement.this.getBinding()
										.getValue());
						if (value != null) {
							onValue(value);
							final ITextLabelProvider adapter = AbstractTextElement.this
									.getBinding().getAdapter(
											ITextLabelProvider.class);
							if (adapter != null) {
								value = adapter.getText(value);
							}
							AbstractTextElement.this.setText(value.toString());
						}
					}

				});
				this.registerPart(this.selectorButton);
			}
		} else {
			if (this.selectorButton != null) {
				this.selectorButton.dispose();
				this.selectorButton = null;
			}
		}
	}

	protected void onValue(Object value) {
		// TODO Auto-generated method stub
		
	}

	private void installContentAssist(C text) {
		if (this.getBinding() != null) {

			final IContentAssistConfiguration prProvider = this.getBinding()
					.getAdapter(IContentAssistConfiguration.class);
			if (prProvider != null) {
				this.contentAssist = this.createContentAssistAdapter(text);
				final com.onpositive.semantic.model.ui.generic.IContentProposalProvider proposalProvider2 = prProvider.getProposalProvider();
				this.contentAssistProposalProvider = proposalProvider2;
				if (proposalProvider2 instanceof IContentProposalProvider2){
					IContentProposalProvider2 p=(IContentProposalProvider2) contentAssistProposalProvider;
					p.setContentAssistOwner(this);
				}
				this.contentAssist.setProposalAcceptanceStyle(prProvider
						.getProposalAcceptanceStyle());
				final ITextLabelProvider proposalLabelProvider0 = prProvider.getProposalLabelProvider();
				org.eclipse.jface.viewers.ILabelProvider proposalLabelProvider = new LabelProvider(){
					
					@Override
					public String getText(Object element) {						
						return proposalLabelProvider0.getText(element);
					}
					
					@Override
					public Image getImage(Object element) {
						if (proposalLabelProvider0 instanceof IImageDescriptorProvider){
							IImageDescriptorProvider p0=(IImageDescriptorProvider) proposalLabelProvider0;
							return SWTImageManager.getImage(p0.getImageDescriptor(element));
						}
						return super.getImage(element);
					}
				};
				if (proposalLabelProvider0==null){
					proposalLabelProvider=new DefaultContentProposalLabelProvider();
				}
				this.contentAssist.setLabelProvider(proposalLabelProvider);
				this.contentAssist.setAutoActivationCharacters(prProvider
						.getAutoactivationCharacters());
				this.contentAssist.setFilterStyle(prProvider.getFilterStyle());
				this.contentAssist.setEnabled(this.isContentAssistEnabled);
				return;
			}
			final IRealm<?> adapter = this.getBinding().getRealm();
			if ((adapter != null)
					|| (this.contentAssistProposalProvider != null)) {
				if (this.contentAssist == null) {

					this.contentAssist = this.createContentAssistAdapter(text);
					if (this.contentAssist==null){
						return;
					}
					this.contentAssist.setAutoActivationDelay(100);
					this.contentAssist
							.setAutoActivationCharacters(this.autoActivationCharacters);
					this.labelProvider = new DefaultContentProposalLabelProvider();
					this.contentAssist.setLabelProvider(this.labelProvider);
					this.contentAssist.setEnabled(this.isContentAssistEnabled);
				}
				if (adapter != null) {
					this.contentAssist
							.setProposalAcceptanceStyle((!this.getBinding()
									.allowsMultiValues()) ? ContentProposalAdapter.PROPOSAL_REPLACE
									: ContentProposalAdapter.PROPOSAL_INSERT);
					final IRealm<?> realm = this.getBinding().getRealm();
					if (realm != null) {
						this.contentAssistProposalProvider = new RealmProposalProvider<T>(
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

	public void dispose() {
		if (this.contentAssist != null) {
			this.contentAssist.dispose();
		}
		this.contentAssist = null;
		super.dispose();
	}

	protected abstract ContentProposalAdapter createContentAssistAdapter(C text);

	protected void internalSetBinding(IBinding binding) {
		if (binding != null) {
			super.internalSetBinding(binding);
			this.selector = binding.getElementFactory();
			if (this.isCreated()) {
				this.installSelector(this.getParent().getContentParent());
				this.installContentAssist(this.getControl());
				final Control labelControl = this.getLabelControl();
				if (labelControl != null) {
					labelControl.pack();
				}
				this.installDecorations(this.getControl());
				// getParent().getContentParent().layout(true,true);
			}
		}
	}

	public char[] getAutoActivationCharacters() {
		return this.autoActivationCharacters;
	}

	public void setAutoActivationCharacters(char[] autoActivationCharacters) {
		this.autoActivationCharacters = autoActivationCharacters;
		if (this.contentAssist != null) {
			this.contentAssist
					.setAutoActivationCharacters(autoActivationCharacters);
		}
	}

	public boolean isContentAssistEnabled() {
		return this.isContentAssistEnabled;
	}

	public void setContentAssistEnabled(boolean isContentAssistEnabled) {
		this.isContentAssistEnabled = isContentAssistEnabled;
		if (this.contentAssist != null) {
			this.contentAssist.setEnabled(isContentAssistEnabled);
		}
	}

	public Object getDefaultLayoutData() {
		final Container parent2 = this.getParent();
		if (parent2 != null) {
			if (parent2.getContentParent() != null) {
				final Layout layout = parent2.getContentParent().getLayout();
				if (layout instanceof GridLayout) {
					final GridData ds = new GridData(SWT.FILL, SWT.CENTER,
							true, false);
					// ds.widthHint=100;
					ds.minimumWidth = 100;
					ds.horizontalIndent = 4;
					return ds;
				}
			}
		}
		return null;
	}

	public IFactory getSelector() {
		return this.selector;
	}

	public void setSelector(IFactory selector) {
		this.selector = selector;
	}

	public com.onpositive.semantic.model.ui.generic.IContentProposalProvider getContentAssistProposalProvider() {
		return this.contentAssistProposalProvider;
	}

	public void setContentAssistProposalProvider(
			com.onpositive.semantic.model.ui.generic.IContentProposalProvider contentAssistProposalProvider) {
		this.contentAssistProposalProvider = contentAssistProposalProvider;
	}

	public LabelProvider getContentAssistLabelProvider() {
		return this.setlabelProvider;
	}

	public void setContentAssistLabelProvider(LabelProvider setlabelProvider) {
		this.setlabelProvider = setlabelProvider;
	}

	public String getContentAssistRole() {
		return this.contentAssistRole;
	}

	public void setContentAssistRole(String contentAssistRole) {
		this.contentAssistRole = contentAssistRole;
	}

	public void setContentAssistInformationControlCreator(
			IInformationalControlContentProducer informationalControlContentProducer) {
		this.infoContentAssist = informationalControlContentProducer;
	}

	protected void doCommit(final IBinding binding2) {
		final String text2 = AbstractTextElement.this.getText();
		if (binding2.allowsMultiValues()) {
			final ArrayList<String> content = AbstractTextElement.this
					.getContent();
			Object lookupResult;
			try {
				lookupResult = binding2
						.lookupByLabels(content);
			} catch (final NotFoundException e) {
				return;
			}
			AbstractTextElement.this
					.commitToBinding(lookupResult);
		} else {
			Object lookupResult;
			try {
				lookupResult = binding2
						.lookupByLabel(text2);
			} catch (final NotFoundException e) {
				return;
			}
			AbstractTextElement.this
					.commitToBinding(lookupResult);
		}
	}
}