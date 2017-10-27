package com.onpositive.commons.contentassist;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;

/**
 * ContentAssistCommandAdapter extends {@link ContentProposalAdapter} to invoke
 * content proposals using a specified {@link org.eclipse.ui.commands.ICommand}.
 * The ability to specify a {@link org.eclipse.jface.bindings.keys.KeyStroke}
 * that explicitly invokes content proposals is hidden by this class, and
 * instead the String id of a command is used. If no command id is specified by
 * the client, then the default workbench content assist command is used.
 * <p>
 * As of 3.3, ContentAssistCommandAdapter can be optionally configured to
 * install the content assist decoration on its control.
 * <p>
 * This class is not intended to be subclassed.
 * 
 * @since 3.2
 */
public class ContentAssistCommandAdapter extends ContentProposalAdapter {

	private static final String CONTENT_ASSIST_DECORATION_ID = "org.eclipse.ui.fieldAssist.ContentAssistField"; //$NON-NLS-1$
	private String commandId;

	/**
	 * The command id used for content assist. (value
	 * <code>"org.eclipse.ui.edit.text.contentAssist.proposals"</code>)
	 */
	public static final String CONTENT_PROPOSAL_COMMAND = "org.eclipse.ui.edit.text.contentAssist.proposals"; //$NON-NLS-1$

	// Default autoactivation delay in milliseconds
	// TODO: This should eventually be controlled by
	// a platform UI preference.
	private static final int DEFAULT_AUTO_ACTIVATION_DELAY = 500;

	private ControlDecoration decoration;

	/**
	 * Construct a content proposal adapter that can assist the user with
	 * choosing content for the field. No visual indicator of content assist is
	 * shown.
	 * 
	 * @param control
	 *            the control for which the adapter is providing content assist.
	 *            May not be <code>null</code>.
	 * @param controlContentAdapter
	 *            the <code>IControlContentAdapter</code> used to obtain and
	 *            update the control's contents as proposals are accepted. May
	 *            not be <code>null</code>.
	 * @param proposalProvider
	 *            the <code>IContentProposalProvider</code> used to obtain
	 *            content proposals for this control, or <code>null</code> if no
	 *            content proposal is available.
	 * @param commandId
	 *            the String id of the command that will invoke the content
	 *            assistant. If not supplied, the default value will be
	 *            "org.eclipse.ui.edit.text.contentAssist.proposals".
	 * @param autoActivationCharacters
	 *            An array of characters that trigger auto-activation of content
	 *            proposal. If specified, these characters will trigger
	 *            auto-activation of the proposal popup, regardless of the
	 *            specified command id.
	 */
	public ContentAssistCommandAdapter(Control control,
			IControlContentAdapter controlContentAdapter,
			IContentProposalProvider proposalProvider, String commandId,
			char[] autoActivationCharacters) {
		this(control, controlContentAdapter, proposalProvider, commandId,
				autoActivationCharacters, false);
	}

	/**
	 * Construct a content proposal adapter that can assist the user with
	 * choosing content for the field.
	 * 
	 * @param control
	 *            the control for which the adapter is providing content assist.
	 *            May not be <code>null</code>.
	 * @param controlContentAdapter
	 *            the <code>IControlContentAdapter</code> used to obtain and
	 *            update the control's contents as proposals are accepted. May
	 *            not be <code>null</code>.
	 * @param proposalProvider
	 *            the <code>IContentProposalProvider</code> used to obtain
	 *            content proposals for this control, or <code>null</code> if no
	 *            content proposal is available.
	 * @param commandId
	 *            the String id of the command that will invoke the content
	 *            assistant. If not supplied, the default value will be
	 *            "org.eclipse.ui.edit.text.contentAssist.proposals".
	 * @param autoActivationCharacters
	 *            An array of characters that trigger auto-activation of content
	 *            proposal. If specified, these characters will trigger
	 *            auto-activation of the proposal popup, regardless of the
	 *            specified command id.
	 * @param installDecoration
	 *            A boolean that specifies whether a content assist control
	 *            decoration should be installed. The client is responsible for
	 *            ensuring that adequate space is reserved for the decoration.
	 *            Clients that want more fine-grained control of the
	 *            decoration's location or appearance should use
	 *            <code>false</code> for this parameter, creating their own
	 *            {@link ControlDecoration} and managing it directly.
	 * @since 3.3
	 */
	public ContentAssistCommandAdapter(Control control,
			IControlContentAdapter controlContentAdapter,
			IContentProposalProvider proposalProvider, String commandId,
			char[] autoActivationCharacters, boolean installDecoration) {
		super(control, controlContentAdapter, proposalProvider, null,
				autoActivationCharacters);
		this.commandId = commandId;
		if (commandId == null) {
			this.commandId = CONTENT_PROPOSAL_COMMAND;
		}

		// If no autoactivation characters were specified, set them to the empty
		// array so that we don't get the alphanumeric auto-trigger of our
		// superclass.
		if (autoActivationCharacters == null) {
			this.setAutoActivationCharacters(new char[] {});
		}
		// Set a default autoactivation delay.
		this.setAutoActivationDelay(DEFAULT_AUTO_ACTIVATION_DELAY);

		// Add listeners to the control to manage activation of the handler
		this.addListeners(control);

		// Cache the handler service so we don't have to retrieve it each time
		if (installDecoration) {
			// Note top left is used for compatibility with 3.2, although
			// this may change to center alignment in the future.
			this.decoration = new ControlDecoration(control, SWT.TOP | SWT.LEFT);
			this.decoration.setShowOnlyOnFocus(true);
			final FieldDecoration dec = this.getContentAssistFieldDecoration();
			this.decoration.setImage(dec.getImage());
			this.decoration.setDescriptionText(dec.getDescription());
		}

	}

	/*
	 * Add the listeners needed in order to activate the content assist command
	 * on the control.
	 */
	private void addListeners(Control control) {
		control.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == ' ') {
					if ((e.stateMask & SWT.CONTROL) != 0) {
						ContentAssistCommandAdapter.this.openProposalPopup();
					}
				}
			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

		});
	}

	/**
	 * Return the string command ID of the command used to invoke content
	 * assist.
	 * 
	 * @return the command ID of the command that invokes content assist.
	 */
	public String getCommandId() {
		return this.commandId;
	}

	/*
	 * Return the field decoration that should be used to indicate that content
	 * assist is available for a field. Ensure that the decoration text includes
	 * the correct key binding.
	 * 
	 * @return the {@link FieldDecoration} that should be used to show content
	 * assist.
	 * 
	 * @since 3.3
	 */
	private FieldDecoration getContentAssistFieldDecoration() {
		final FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
		// Look for a decoration installed for this particular command id.
		final String decId = CONTENT_ASSIST_DECORATION_ID + this.getCommandId();
		FieldDecoration dec = registry.getFieldDecoration(decId);

		// If there is not one, baseProperty ours on the standard JFace one.
		if (dec == null) {
			final FieldDecoration originalDec = registry
					.getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);

			registry.registerFieldDecoration(decId, null, originalDec
					.getImage());
			dec = registry.getFieldDecoration(decId);
		}
		// Always update the decoration text since the key binding may
		// have changed since it was last retrieved.
		// Now return the field decoration
		return dec;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Overridden to hide and show the content assist decoration
	 * 
	 * @see
	 * org.eclipse.jface.fieldassist.ContentProposalAdapter#setEnabled(boolean)
	 * 
	 * @since 3.3
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (this.decoration == null) {
			return;
		}
		if (enabled) {
			this.decoration.show();
		} else {
			this.decoration.hide();
		}
	}
}
