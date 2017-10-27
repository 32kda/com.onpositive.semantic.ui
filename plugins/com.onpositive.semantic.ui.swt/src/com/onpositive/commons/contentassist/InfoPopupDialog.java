/**
 * 
 */
package com.onpositive.commons.contentassist;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.onpositive.commons.elements.RootElement;
import com.onpositive.commons.elements.UIElementFactory;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.common.ui.roles.ContentAssistManager;
import com.onpositive.semantic.common.ui.roles.ContentAssistObject;
import com.onpositive.semantic.common.ui.roles.IInformationalControlContentProducer;

public class InfoPopupDialog extends PopupDialog {

	/**
	 * 
	 */
	private final ContentProposalAdapter.ContentProposalPopup contentProposalPopup;

	/*
	 * The text control that displays the text.
	 */
	private Composite owner;

	private Composite root;

	/*
	 * Construct an info-popup with the specified parent.
	 */
	InfoPopupDialog(
			ContentProposalAdapter.ContentProposalPopup contentProposalPopup,
			Shell parent) {
		super(parent, PopupDialog.HOVER_SHELLSTYLE, false, false, false, false,
				null, null);
		this.contentProposalPopup = contentProposalPopup;
	}

	/*
	 * Create a text control for showing the info about a proposal.
	 */
	protected Control createDialogArea(Composite parent) {
		this.owner = new Composite(parent, SWT.NONE);
		this.owner.setBackgroundMode(SWT.INHERIT_FORCE);
		// Use the compact margins employed by PopupDialog.
		final GridData gd = new GridData(GridData.BEGINNING | GridData.FILL_BOTH);
		gd.horizontalIndent = PopupDialog.POPUP_HORIZONTALSPACING;
		gd.verticalIndent = PopupDialog.POPUP_VERTICALSPACING;
		this.owner.setLayoutData(gd);
		// text.setText(contents);
		// since SWT.NO_FOCUS is only a hint...
		this.owner.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent event) {
				// contentProposalPopup.close();
			}
		});
		return this.owner;
	}

	/*
	 * Adjust the bounds so that we appear adjacent to our parent shell
	 */
	protected void adjustBounds() {
		final Rectangle parentBounds = this.contentProposalPopup.getShell()
				.getBounds();
		Rectangle proposedBounds;
		// Try placing the info popup to the right
		Rectangle rightProposedBounds = new Rectangle(parentBounds.x
				+ parentBounds.width + PopupDialog.POPUP_HORIZONTALSPACING,
				parentBounds.y + PopupDialog.POPUP_VERTICALSPACING,
				parentBounds.width, parentBounds.height);
		rightProposedBounds = this.contentProposalPopup
				.getConstrainedShellBounds(rightProposedBounds);
		// If it won't fit on the right, try the left
		if (rightProposedBounds.intersects(parentBounds)) {

			Rectangle leftProposedBounds = new Rectangle(
					parentBounds.x
							- parentBounds.width
							- PopupDialog.POPUP_HORIZONTALSPACING
							- 1, parentBounds.y, parentBounds.width,
					parentBounds.height);
			leftProposedBounds = this.contentProposalPopup
					.getConstrainedShellBounds(leftProposedBounds);
			// If it won't fit on the left, choose the proposed bounds
			// that fits the best
			if (leftProposedBounds.intersects(parentBounds)) {
				if (rightProposedBounds.x - parentBounds.x >= parentBounds.x
						- leftProposedBounds.x) {
					rightProposedBounds.x = parentBounds.x + parentBounds.width
							+ PopupDialog.POPUP_HORIZONTALSPACING;
					proposedBounds = rightProposedBounds;
				} else {
					leftProposedBounds.width = parentBounds.x
							- PopupDialog.POPUP_HORIZONTALSPACING
							- leftProposedBounds.x;
					proposedBounds = leftProposedBounds;
				}
			} else {
				// use the proposed bounds on the left
				proposedBounds = leftProposedBounds;
			}
		} else {
			// use the proposed bounds on the right
			proposedBounds = rightProposedBounds;
		}
		this.getShell().setBounds(proposedBounds);
	}

	/*
	 * Set the text contents of the popup.
	 */
	void setContents(IContentProposal p, String newContents, String role,
			String theme) {

		if (newContents == null) {
			newContents = ContentProposalAdapter.EMPTY;
		}
		if ((this.owner != null) && !this.owner.isDisposed()) {
			this.owner.getShell().setRedraw(false);
			if (this.root != null) {
				this.root.dispose();
			}
			final IInformationalControlContentProducer informationControlCreator = this.contentProposalPopup
					.getInformationControlCreator();
			if (informationControlCreator != null) {
				this.root = (Composite) informationControlCreator.create(this, this.owner, p, theme,
						role, newContents);
			} else {
				Object o = p;
				if (p instanceof BasicContentProposal) {
					final BasicContentProposal bs = (BasicContentProposal) o;
					o = bs.getElement();
				}

				final ContentAssistObject tooltipObject = ContentAssistManager
						.getInstance().getContentAssistObject(o, role, theme);
				
				if (tooltipObject != null) {
					final IInformationalControlContentProducer contentProducer = tooltipObject
							.getContentProducer();
					if (contentProducer != null) {
						this.root = (Composite) contentProducer.create(this, this.owner, o, role,
								theme, newContents);
					}
				}

				final RootElement root = new RootElement(this.owner);
				root.setLayout(GridLayoutFactory.fillDefaults().create());
				root.setLayoutManager(new OneElementOnLineLayouter());
				// root.add(UIElementFactory.createLabel(newContents));
				root.add(UIElementFactory.createRichLabel(newContents));
				root.getContentParent().setBackgroundMode(SWT.INHERIT_FORCE);
				this.root = root.getContentParent();
			}
			this.owner.layout(true, true);
			this.owner.getShell().setRedraw(true);
		}
	}

	/*
	 * Return whether the popup has focus.
	 */
	boolean hasFocus() {
		if ((this.owner == null) || this.owner.isDisposed()) {
			return false;
		}
		return this.owner.getShell().isFocusControl() || this.owner.isFocusControl();
	}
}