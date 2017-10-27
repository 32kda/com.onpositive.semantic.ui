package com.onpositive.commons.contentassist;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.onpositive.commons.ui.dialogs.InputElementDialog;
import com.onpositive.semantic.common.ui.roles.IInformationalControlContentProducer;

/**
 * ContentProposalAdapter can be used to attach content proposal behavior to a
 * control. This behavior includes obtaining proposals, opening a popup dialog,
 * managing the content of the control relative to the selections in the popup,
 * and optionally opening up a secondary popup to further describe proposals.
 * <p>
 * A number of configurable options are provided to determine how the control
 * content is altered when a proposal is chosen, how the content proposal popup
 * is activated, and whether any filtering should be done on the proposals as
 * the user types characters.
 * <p>
 * This class is not intended to be subclassed.
 * 
 * @since 3.2
 */
public class ContentProposalAdapter {

	private boolean isCellEditor;

	public static final String POPUP_CONTENT_CREATOR = "POPUP_CONTENT_CREATOR"; //$NON-NLS-1$

	/*
	 * The lightweight popup used to show content proposals for a text field. If
	 * additional information exists for a proposal, then selecting that
	 * proposal will result in the information being displayed in a secondary
	 * popup.
	 */
	class ContentProposalPopup extends PopupDialog {

		protected void constrainShellSize() {
			super.constrainShellSize();
		}

		protected Rectangle getConstrainedShellBounds(Rectangle preferredSize) {
			return super.getConstrainedShellBounds(preferredSize);
		}

		protected Shell getParentShell() {
			return super.getParentShell();
		}

		/*
		 * The listener we install on the popup and related controls to
		 * determine when to close the popup. Some events (move, resize, close,
		 * deactivate) trigger closure as soon as they are received, simply
		 * because one of the registered listeners received them. Other events
		 * depend on additional circumstances.
		 */
		private final class PopupCloserListener implements Listener {
			private boolean scrollbarClicked = false;

			public void handleEvent(final Event e) {

				// If focus is leaving an important widget or the field's
				// shell is deactivating
				if (e.type == SWT.FocusOut) {
					this.scrollbarClicked = false;
					/*
					 * Ignore this event if it's only happening because focus is
					 * moving between the popup shells, their controls, or a
					 * scrollbar. Do this in an async since the focus is not
					 * actually switched when this event is received.
					 */
					e.display.asyncExec(new Runnable() {
						public void run() {
							if (ContentProposalPopup.this.isValid()) {
								if (PopupCloserListener.this.scrollbarClicked
										|| ContentProposalPopup.this.hasFocus()
										|| ((ContentProposalPopup.this.infoPopup != null) && ContentProposalPopup.this.infoPopup
												.hasFocus())) {
									return;
								}
								// Workaround a problem on X and Mac, whereby at
								// this point, the focus control is not known.
								// This can happen, for example, when resizing
								// the popup shell on the Mac.
								// Check the active shell.
								final Shell activeShell = e.display
										.getActiveShell();
								if ((activeShell == ContentProposalPopup.this
										.getShell())
										|| ((ContentProposalPopup.this.infoPopup != null) && (ContentProposalPopup.this.infoPopup
												.getShell() == activeShell))) {
									return;
								}
								/*
								 * System.out.println(e);
								 * System.out.println(e.display
								 * .getFocusControl());
								 * System.out.println(e.display
								 * .getActiveShell());
								 */
								ContentProposalPopup.this.close();
							}
						}
					});
					return;
				}

				// Scroll bar has been clicked. Remember this for focus event
				// processing.
				if (e.type == SWT.Selection) {
					this.scrollbarClicked = true;
					return;
				}
				// For all other events, merely getting them dictates closure.
				ContentProposalPopup.this.close();
			}

			// Install the listeners for events that need to be monitored for
			// popup closure.
			void installListeners() {
				// Listeners on this popup's table and scroll bar
				ContentProposalPopup.this.proposalTable.addListener(
						SWT.FocusOut, this);
				final ScrollBar scrollbar = ContentProposalPopup.this.proposalTable
						.getVerticalBar();
				if (scrollbar != null) {
					scrollbar.addListener(SWT.Selection, this);
				}

				// Listeners on this popup's shell
				ContentProposalPopup.this.getShell().addListener(
						SWT.Deactivate, this);
				ContentProposalPopup.this.getShell().addListener(SWT.Close,
						this);

				// Listeners on the target control
				ContentProposalAdapter.this.control.addListener(
						SWT.MouseDoubleClick, this);
				ContentProposalAdapter.this.control.addListener(SWT.MouseDown,
						this);
				ContentProposalAdapter.this.control.addListener(SWT.Dispose,
						this);
				ContentProposalAdapter.this.control.addListener(SWT.FocusOut,
						this);
				// Listeners on the target control's shell
				final Shell controlShell = ContentProposalAdapter.this.control
						.getShell();
				controlShell.addListener(SWT.Move, this);
				controlShell.addListener(SWT.Resize, this);

			}

			// Remove installed listeners
			void removeListeners() {
				if (ContentProposalPopup.this.isValid()) {
					ContentProposalPopup.this.proposalTable.removeListener(
							SWT.FocusOut, this);
					final ScrollBar scrollbar = ContentProposalPopup.this.proposalTable
							.getVerticalBar();
					if (scrollbar != null) {
						scrollbar.removeListener(SWT.Selection, this);
					}

					ContentProposalPopup.this.getShell().removeListener(
							SWT.Deactivate, this);
					ContentProposalPopup.this.getShell().removeListener(
							SWT.Close, this);
				}

				if ((ContentProposalAdapter.this.control != null)
						&& !ContentProposalAdapter.this.control.isDisposed()) {

					ContentProposalAdapter.this.control.removeListener(
							SWT.MouseDoubleClick, this);
					ContentProposalAdapter.this.control.removeListener(
							SWT.MouseDown, this);
					ContentProposalAdapter.this.control.removeListener(
							SWT.Dispose, this);
					ContentProposalAdapter.this.control.removeListener(
							SWT.FocusOut, this);

					final Shell controlShell = ContentProposalAdapter.this.control
							.getShell();
					controlShell.removeListener(SWT.Move, this);
					controlShell.removeListener(SWT.Resize, this);
				}
			}
		}

		/*
		 * The listener we will install on the target control.
		 */
		private final class TargetControlListener implements Listener {

			// Key events from the control
			public void handleEvent(Event e) {
				if (!ContentProposalPopup.this.isValid()) {
					return;
				}

				final char key = e.character;

				// Traverse events are handled depending on whether the
				// event has a character.
				if (e.type == SWT.Traverse) {
					// If the traverse event contains a legitimate character,
					// then we must set doit false so that the widget will
					// receive the key event. We return immediately so that
					// the character is handled only in the key event.
					// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=132101
					if (key != 0) {
						e.doit = false;
						return;
					}
					// Traversal does not contain a character. Set doit true
					// to indicate TRAVERSE_NONE will occur and that no key
					// event will be triggered. We will check for navigation
					// keys below.
					e.detail = SWT.TRAVERSE_NONE;
					e.doit = true;
				} else {
					// Default is to only propagate when configured that way.
					// Some keys will always set doit to false anyway.
					e.doit = ContentProposalAdapter.this.propagateKeys;
				}

				// No character. Check for navigation keys.

				if (key == 0) {
					int newSelection = ContentProposalPopup.this.proposalTable
							.getSelectionIndex();
					final int visibleRows = (ContentProposalPopup.this.proposalTable
							.getSize().y / ContentProposalPopup.this.proposalTable
							.getItemHeight()) - 1;
					switch (e.keyCode) {
					case SWT.ARROW_UP:
						newSelection -= 1;
						if (newSelection < 0) {
							newSelection = ContentProposalPopup.this.proposalTable
									.getItemCount() - 1;
						}
						// Not typical - usually we get this as a Traverse and
						// therefore it never propagates. Added for consistency.
						if (e.type == SWT.KeyDown) {
							// don't propagate to control
							e.doit = false;
						}

						break;

					case SWT.ARROW_DOWN:
						newSelection += 1;
						if (newSelection > ContentProposalPopup.this.proposalTable
								.getItemCount() - 1) {
							newSelection = 0;
						}
						// Not typical - usually we get this as a Traverse and
						// therefore it never propagates. Added for consistency.
						if (e.type == SWT.KeyDown) {
							// don't propagate to control
							e.doit = false;
						}

						break;

					case SWT.PAGE_DOWN:
						newSelection += visibleRows;
						if (newSelection >= ContentProposalPopup.this.proposalTable
								.getItemCount()) {
							newSelection = ContentProposalPopup.this.proposalTable
									.getItemCount() - 1;
						}
						if (e.type == SWT.KeyDown) {
							// don't propagate to control
							e.doit = false;
						}
						break;

					case SWT.PAGE_UP:
						newSelection -= visibleRows;
						if (newSelection < 0) {
							newSelection = 0;
						}
						if (e.type == SWT.KeyDown) {
							// don't propagate to control
							e.doit = false;
						}
						break;

					case SWT.HOME:
						newSelection = 0;
						if (e.type == SWT.KeyDown) {
							// don't propagate to control
							e.doit = false;
						}
						break;

					case SWT.END:
						newSelection = ContentProposalPopup.this.proposalTable
								.getItemCount() - 1;
						if (e.type == SWT.KeyDown) {
							// don't propagate to control
							e.doit = false;
						}
						break;

					// If received as a Traverse, these should propagate
					// to the control as keydown. If received as a keydown,
					// proposals should be recomputed since the cursor
					// position has changed.
					case SWT.ARROW_LEFT:
					case SWT.ARROW_RIGHT:
						if (e.type == SWT.Traverse) {
							e.doit = false;
						} else {
							e.doit = true;
							final String contents = ContentProposalAdapter.this
									.getControlContentAdapter()
									.getControlContents(
											ContentProposalAdapter.this
													.getControl());
							// If there are no contents, changes in cursor
							// position
							// have no effect. Note also that we do not affect
							// the filter
							// text on ARROW_LEFT as we would with BS.
							if (contents.length() > 0) {
								ContentProposalPopup.this
										.asyncRecomputeProposals(ContentProposalPopup.this.filterText);
							}
						}
						break;

					// Any unknown keycodes will cause the popup to close.
					// Modifier keys are explicitly checked and ignored because
					// they are not complete yet (no character).
					default:
						if ((e.keyCode != SWT.CAPS_LOCK)
								&& (e.keyCode != SWT.MOD1)
								&& (e.keyCode != SWT.MOD2)
								&& (e.keyCode != SWT.MOD3)
								&& (e.keyCode != SWT.MOD4)) {
							ContentProposalPopup.this.close();
						}
						return;
					}

					// If any of these navigation events caused a new selection,
					// then handle that now and return.
					if (newSelection >= 0) {
						ContentProposalPopup.this.selectProposal(newSelection);
					}
					return;
				}

				// key != 0
				// Check for special keys involved in cancelling, accepting, or
				// filtering the proposals.
				switch (key) {
				case SWT.ESC:
					e.doit = false;
					ContentProposalPopup.this.close();
					break;

				case SWT.LF:
				case SWT.CR:
					e.doit = false;
					final Object p = ContentProposalPopup.this
							.getSelectedProposal();
					if (p != null) {
						ContentProposalPopup.this.acceptCurrentProposal();
					} else {
						ContentProposalPopup.this.close();
					}
					break;

				case SWT.TAB:
					e.doit = false;
					ContentProposalPopup.this.getShell().setFocus();
					return;

				case SWT.BS:
					// Backspace should back out of any stored filter text
					if (ContentProposalAdapter.this.filterStyle != FILTER_NONE) {
						// We have no filter to back out of, so do nothing
						if (ContentProposalPopup.this.filterText.length() == 0) {
							return;
						}
						// There is filter to back out of
						ContentProposalPopup.this.filterText = ContentProposalPopup.this.filterText
								.substring(0,
										ContentProposalPopup.this.filterText
												.length() - 1);
						ContentProposalPopup.this
								.asyncRecomputeProposals(ContentProposalPopup.this.filterText);
						return;
					}
					// There is no filtering provided by us, but some
					// clients provide their own filtering based on content.
					// Recompute the proposals if the cursor position
					// will change (is not at 0).
					final int pos = ContentProposalAdapter.this
							.getControlContentAdapter().getCursorPosition(
									ContentProposalAdapter.this.getControl());
					// We rely on the fact that the contents and pos do not yet
					// reflect the result of the BS. If the contents were
					// already empty, then BS should not cause
					// a recompute.
					if (pos > 0) {
						ContentProposalPopup.this
								.asyncRecomputeProposals(ContentProposalPopup.this.filterText);
					}
					break;

				default:
					// If the key is a defined unicode character, and not one of
					// the special cases processed above, update the filter text
					// and filter the proposals.
					if (Character.isDefined(key)) {
						if (ContentProposalAdapter.this.filterStyle == FILTER_CUMULATIVE) {
							ContentProposalPopup.this.filterText = ContentProposalPopup.this.filterText
									+ String.valueOf(key);
						} else if (ContentProposalAdapter.this.filterStyle == FILTER_CHARACTER) {
							ContentProposalPopup.this.filterText = String
									.valueOf(key);
						}
						// Recompute proposals after processing this event.
						ContentProposalPopup.this
								.asyncRecomputeProposals(ContentProposalPopup.this.filterText);
					}
					break;
				}
			}
		}

		/*
		 * The listener installed on the target control.
		 */
		private Listener targetControlListener;

		/*
		 * The listener installed in order to close the popup.
		 */
		private PopupCloserListener popupCloser;

		/*
		 * The table used to show the list of proposals.
		 */
		private Table proposalTable;

		/*
		 * The proposals to be shown (cached to avoid repeated requests).
		 */
		private IContentProposal[] proposals;

		/*
		 * Secondary popup used to show detailed information about the selected
		 * proposal..
		 */
		private InfoPopupDialog infoPopup;

		/*
		 * Flag indicating whether there is a pending secondary popup update.
		 */
		private boolean pendingDescriptionUpdate = false;

		/*
		 * Filter text - tracked while popup is open, only if we are told to
		 * filter
		 */
		private String filterText = EMPTY;

		/**
		 * Constructs a new instance of this popup, specifying the control for
		 * which this popup is showing content, and how the proposals should be
		 * obtained and displayed.
		 * 
		 * @param infoText
		 *            Text to be shown in a lower info area, or
		 *            <code>null</code> if there is no info area.
		 */
		ContentProposalPopup(String infoText, IContentProposal[] proposals) {
			// IMPORTANT: Use of SWT.ON_TOP is critical here for ensuring
			// that the target control retains focus on Mac and Linux. Without
			// it, the focus will disappear, keystrokes will not go to the
			// popup, and the popup closer will wrongly close the popup.
			// On platforms where SWT.ON_TOP overrides SWT.RESIZE, we will live
			// with this.
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=126138
			super(ContentProposalAdapter.this.control.getShell(), SWT.RESIZE
					| SWT.ON_TOP, false, false, false, false, null, infoText);
			this.proposals = proposals;
		}

		/*
		 * Overridden to force change of colors. See
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=136244 (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.dialogs.PopupDialog#createContents(org.eclipse.
		 * swt.widgets.Composite)
		 */
		protected Control createContents(Composite parent) {
			final Control contents = super.createContents(parent);
			this.changeDefaultColors(parent);
			return contents;
		}

		/*
		 * Set the colors of the popup. The contents have already been created.
		 */
		private void changeDefaultColors(Control control) {
			this.applyForegroundColor(this.getShell().getDisplay()
					.getSystemColor(SWT.COLOR_LIST_FOREGROUND), control);
			this.applyBackgroundColor(this.getShell().getDisplay()
					.getSystemColor(SWT.COLOR_LIST_BACKGROUND), control);
		}

		/*
		 * Creates the content area for the proposal popup. This creates a table
		 * and places it inside the composite. The table will contain a list of
		 * all the proposals.
		 * 
		 * @param parent The parent composite to contain the dialog area; must
		 * not be <code>null</code>.
		 */
		protected final Control createDialogArea(final Composite parent) {
			// Use virtual where appropriate (see flag definition).
			if (USE_VIRTUAL) {
				this.proposalTable = new Table(parent, SWT.H_SCROLL
						| SWT.V_SCROLL | SWT.VIRTUAL);

				final Listener listener = new Listener() {
					public void handleEvent(Event event) {
						ContentProposalPopup.this.handleSetData(event);
					}
				};
				this.proposalTable.addListener(SWT.SetData, listener);
			} else {
				this.proposalTable = new Table(parent, SWT.H_SCROLL
						| SWT.V_SCROLL);
			}

			// set the proposals to force population of the table.
			this.setProposals(this.filterProposals(this.proposals,
					this.filterText));

			this.proposalTable.setHeaderVisible(false);
			this.proposalTable.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					// If a proposal has been selected, show it in the secondary
					// popup. Otherwise close the popup.
					if (e.item == null) {
						if (ContentProposalPopup.this.infoPopup != null) {
							ContentProposalPopup.this.infoPopup.close();
						}
					} else {
						ContentProposalPopup.this.showProposalDescription();
					}
				}

				// Default selection was made. Accept the current proposal.
				public void widgetDefaultSelected(SelectionEvent e) {
					ContentProposalPopup.this.acceptCurrentProposal();
				}
			});
			return this.proposalTable;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.PopupDialog.adjustBounds()
		 */
		protected void adjustBounds() {
			// Get our control's location in display coordinates.
			final Point location = ContentProposalAdapter.this.control
					.getDisplay().map(
							ContentProposalAdapter.this.control.getParent(),
							null,
							ContentProposalAdapter.this.control.getLocation());
			int initialX = location.x + POPUP_OFFSET;
			int initialY = location.y
					+ ContentProposalAdapter.this.control.getSize().y
					+ POPUP_OFFSET;
			// If we are inserting content, use the cursor position to
			// position the control.
			if (ContentProposalAdapter.this.getProposalAcceptanceStyle() == PROPOSAL_INSERT) {
				final Rectangle insertionBounds = ContentProposalAdapter.this.controlContentAdapter
						.getInsertionBounds(ContentProposalAdapter.this.control);
				initialX = initialX + insertionBounds.x;
				initialY = location.y + insertionBounds.y
						+ insertionBounds.height;
			}

			// If there is no specified size, force it by setting
			// up a layout on the table.
			if (ContentProposalAdapter.this.popupSize == null) {
				final GridData data = new GridData(GridData.FILL_BOTH);
				data.heightHint = this.proposalTable.getItemHeight()
						* POPUP_CHAR_HEIGHT;
				data.widthHint = Math.max(ContentProposalAdapter.this.control
						.getSize().x, POPUP_MINIMUM_WIDTH);
				this.proposalTable.setLayoutData(data);
				this.getShell().pack();
				ContentProposalAdapter.this.popupSize = this.getShell()
						.getSize();
			}
			this.getShell().setBounds(initialX, initialY,
					ContentProposalAdapter.this.popupSize.x,
					ContentProposalAdapter.this.popupSize.y);

			// Now set up a listener to monitor any changes in size.
			this.getShell().addListener(SWT.Resize, new Listener() {
				public void handleEvent(Event e) {
					ContentProposalAdapter.this.popupSize = ContentProposalPopup.this
							.getShell().getSize();
					if (ContentProposalPopup.this.infoPopup != null) {
						ContentProposalPopup.this.infoPopup.adjustBounds();
					}
				}
			});
		}

		/*
		 * Handle the set data event. Set the item data of the requested item to
		 * the corresponding proposal in the proposal cache.
		 */
		private void handleSetData(Event event) {
			final TableItem item = (TableItem) event.item;
			final int index = this.proposalTable.indexOf(item);

			if ((0 <= index) && (index < this.proposals.length)) {
				final IContentProposal current = this.proposals[index];
				item.setText(this.getString(current));
				item.setImage(this.getImage(current));
				item.setData(current);
			} else {
				// this should not happen, but does on win32
			}
		}

		/*
		 * Caches the specified proposals and repopulates the table if it has
		 * been created.
		 */
		private void setProposals(IContentProposal[] newProposals) {
			if ((newProposals == null) || (newProposals.length == 0)) {
				newProposals = this.getEmptyProposalArray();
			}
			this.proposals = newProposals;

			// If there is a table
			if (this.isValid()) {
				final int newSize = newProposals.length;
				if (USE_VIRTUAL) {
					// Set and clear the virtual table. Data will be
					// provided in the SWT.SetData event handler.
					this.proposalTable.setItemCount(newSize);
					this.proposalTable.clearAll();
				} else {
					// Populate the table manually
					this.proposalTable.setRedraw(false);
					this.proposalTable.setItemCount(newSize);
					final TableItem[] items = this.proposalTable.getItems();
					for (int i = 0; i < items.length; i++) {
						final TableItem item = items[i];
						final IContentProposal proposal = newProposals[i];
						item.setText(this.getString(proposal));
						item.setImage(this.getImage(proposal));
						item.setData(proposal);
					}
					this.proposalTable.setRedraw(true);
				}
				// Default to the first selection if there is content.
				if (newProposals.length > 0) {
					this.selectProposal(0);
				} else {
					// No selection, close the secondary popup if it was open
					if (this.infoPopup != null) {
						this.infoPopup.close();
					}

				}
			}
		}

		/*
		 * Get the string for the specified proposal. Always return a String of
		 * some kind.
		 */
		private String getString(IContentProposal proposal) {
			if (proposal == null) {
				return EMPTY;
			}
			if (ContentProposalAdapter.this.labelProvider == null) {
				return proposal.getLabel() == null ? proposal.getContent()
						: proposal.getLabel();
			}
			return ContentProposalAdapter.this.labelProvider.getText(proposal);
		}

		/*
		 * Get the image for the specified proposal. If there is no image
		 * available, return null.
		 */
		private Image getImage(IContentProposal proposal) {
			if ((proposal == null)
					|| (ContentProposalAdapter.this.labelProvider == null)) {
				return null;
			}
			return ContentProposalAdapter.this.labelProvider.getImage(proposal);
		}

		/*
		 * Return an empty array. Used so that something always shows in the
		 * proposal popup, even if no proposal provider was specified.
		 */
		private IContentProposal[] getEmptyProposalArray() {
			return new IContentProposal[0];
		}

		/*
		 * Answer true if the popup is valid, which means the table has been
		 * created and not disposed.
		 */
		private boolean isValid() {
			return (this.proposalTable != null)
					&& !this.proposalTable.isDisposed();
		}

		/*
		 * Return whether the receiver has focus.
		 */
		private boolean hasFocus() {
			if (!this.isValid()) {
				return false;
			}
			return this.getShell().isFocusControl()
					|| this.proposalTable.isFocusControl();
		}

		/*
		 * Return the current selected proposal.
		 */
		private IContentProposal getSelectedProposal() {
			if (this.isValid()) {
				final int i = this.proposalTable.getSelectionIndex();
				if ((this.proposals == null) || (i < 0)
						|| (i >= this.proposals.length)) {
					return null;
				}
				return this.proposals[i];
			}
			return null;
		}

		/*
		 * Select the proposal at the given index.
		 */
		private void selectProposal(int index) {
			Assert
					.isTrue(index >= 0,
							"Proposal index should never be negative"); //$NON-NLS-1$
			if (!this.isValid() || (this.proposals == null)
					|| (index >= this.proposals.length)) {
				return;
			}
			this.proposalTable.setSelection(index);
			this.proposalTable.showSelection();

			this.showProposalDescription();
		}

		/**
		 * Opens this ContentProposalPopup. This method is extended in order to
		 * add the control listener when the popup is opened and to invoke the
		 * secondary popup if applicable.
		 * 
		 * @return the return code
		 * 
		 * @see org.eclipse.jface.window.Window#open()
		 */
		public int openWidget() {
			final int value = super.open();
			if (this.popupCloser == null) {
				this.popupCloser = new PopupCloserListener();
			}
			this.popupCloser.installListeners();
			final IContentProposal p = this.getSelectedProposal();
			if (p != null) {
				this.showProposalDescription();
			}
			return value;
		}

		/**
		 * Closes this popup. This method is extended to remove the control
		 * listener.
		 * 
		 * @return <code>true</code> if the window is (or was already) closed,
		 *         and <code>false</code> if it is still open
		 */
		public boolean close() {
			if (this.popupCloser!=null){
			this.popupCloser.removeListeners();
			}
			if (this.infoPopup != null) {
				this.infoPopup.close();
			}
			final boolean ret = super.close();
			ContentProposalAdapter.this.notifyPopupClosed();
			return ret;
		}

		/*
		 * Show the currently selected proposal's description in a secondary
		 * popup.
		 */
		private void showProposalDescription() {
			// If we do not already have a pending update, then
			// create a thread now that will show the proposal description
			if (!this.pendingDescriptionUpdate) {
				// Create a thread that will sleep for the specified delay
				// before creating the popup. We do not use Jobs since this
				// code must be able to run independently of the Eclipse
				// runtime.
				final Runnable runnable = new Runnable() {
					public void run() {
						ContentProposalPopup.this.pendingDescriptionUpdate = true;
						try {
							Thread.sleep(POPUP_DELAY);
						} catch (final InterruptedException e) {
						}
						if (!ContentProposalPopup.this.isValid()) {
							return;
						}
						ContentProposalPopup.this.getShell().getDisplay()
								.syncExec(new Runnable() {
									private String role;
									private String theme;

									public void run() {
										// Query the current selection since we
										// have
										// been delayed
										final IContentProposal p = ContentProposalPopup.this
												.getSelectedProposal();
										if (p != null) {
											final String description = p
													.getDescription();
											if (description != null) {
												if (ContentProposalPopup.this.infoPopup == null) {
													ContentProposalPopup.this.infoPopup = new InfoPopupDialog(
															ContentProposalPopup.this,
															ContentProposalPopup.this
																	.getShell());
													ContentProposalPopup.this.infoPopup
															.open();
													ContentProposalPopup.this.infoPopup
															.getShell()
															.addDisposeListener(
																	new DisposeListener() {
																		public void widgetDisposed(
																				DisposeEvent event) {
																			ContentProposalPopup.this.infoPopup = null;
																		}
																	});
												}
												ContentProposalPopup.this.infoPopup
														.setContents(
																p,
																p
																		.getDescription(),
																this.role,
																this.theme);
											} else if (ContentProposalPopup.this.infoPopup != null) {
												ContentProposalPopup.this.infoPopup
														.close();
											}
											ContentProposalPopup.this.pendingDescriptionUpdate = false;
										}
									}
								});
					}
				};
				final Thread t = new Thread(runnable);
				t.start();
			}
		}

		/*
		 * Accept the current proposal.
		 */
		private void acceptCurrentProposal() {
			// Close before accepting the proposal.
			// This is important so that the cursor position can be
			// properly restored at acceptance, which does not work without
			// focus on some controls.
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=127108
			InputElementDialog.pushControlFromContentAssist(getControl());
			final IContentProposal proposal = this.getSelectedProposal();
			this.close();
			ContentProposalAdapter.this.proposalAccepted(proposal);
		}

		/*
		 * Request the proposals from the proposal provider, and recompute any
		 * caches. Repopulate the popup if it is open.
		 */
		private void recomputeProposals(String filterText) {
			final IContentProposal[] allProposals = ContentProposalAdapter.this
					.getProposals();
			// If the non-filtered proposal list is empty, we should
			// close the popup.
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=147377
			if (allProposals.length == 0) {
				this.proposals = allProposals;
				this.close();
			} else {
				// Keep the popup open, but filter by any provided filter text
				this.setProposals(this
						.filterProposals(allProposals, filterText));
			}
		}

		/*
		 * In an async block, request the proposals. This is used when clients
		 * are in the middle of processing an event that affects the widget
		 * content. By using an async, we ensure that the widget content is up
		 * to date with the event.
		 */
		private void asyncRecomputeProposals(final String filterText) {
			if (this.isValid()) {
				ContentProposalAdapter.this.control.getDisplay().asyncExec(
						new Runnable() {
							public void run() {
								ContentProposalAdapter.this
										.recordCursorPosition();
								ContentProposalPopup.this
										.recomputeProposals(filterText);
							}
						});
			} else {
				this.recomputeProposals(filterText);
			}
		}

		/*
		 * Filter the provided list of content proposals according to the filter
		 * text.
		 */
		private IContentProposal[] filterProposals(
				IContentProposal[] proposals, String filterString) {
			if (filterString.length() == 0) {
				return proposals;
			}

			// Check each string for a match. Use the string displayed to the
			// user, not the proposal content.
			final ArrayList<IContentProposal> list = new ArrayList<IContentProposal>();
			for (int i = 0; i < proposals.length; i++) {
				final String string = this.getString(proposals[i]);
				if ((string.length() >= filterString.length())
						&& string.substring(0, filterString.length())
								.equalsIgnoreCase(filterString)) {
					list.add(proposals[i]);
				}

			}
			return list.toArray(new IContentProposal[list.size()]);
		}

		Listener getTargetControlListener() {
			if (this.targetControlListener == null) {
				this.targetControlListener = new TargetControlListener();
			}
			return this.targetControlListener;
		}

		public IInformationalControlContentProducer getInformationControlCreator() {
			return ContentProposalAdapter.this.contentCreator;
		}

	}

	public boolean isCellEditor() {
		return this.isCellEditor;
	}

	public void setCellEditor(boolean isCellEditor) {
		this.isCellEditor = isCellEditor;
	}

	/**
	 * Flag that controls the printing of debug info.
	 */
	public static final boolean DEBUG = false;

	/**
	 * Indicates that a chosen proposal should be inserted into the field.
	 */
	public static final int PROPOSAL_INSERT = 1;
	
	
	public static final int PROPOSAL_INSERT_REPLACE = 4;

	/**
	 * Indicates that a chosen proposal should replace the entire contents of
	 * the field.
	 */
	public static final int PROPOSAL_REPLACE = 2;

	/**
	 * Indicates that the contents of the control should not be modified when a
	 * proposal is chosen. This is typically used when a client needs more
	 * specialized behavior when a proposal is chosen. In this case, clients
	 * typically register an IContentProposalListener so that they are notified
	 * when a proposal is chosen.
	 */
	public static final int PROPOSAL_IGNORE = 3;

	/**
	 * Indicates that there should be no filter applied as keys are typed in the
	 * popup.
	 */
	public static final int FILTER_NONE = 1;

	/**
	 * Indicates that a single character filter applies as keys are typed in the
	 * popup.
	 */
	public static final int FILTER_CHARACTER = 2;

	/**
	 * Indicates that a cumulative filter applies as keys are typed in the
	 * popup. That is, each character typed will be added to the filter.
	 */
	public static final int FILTER_CUMULATIVE = 3;

	/*
	 * Set to <code>true</code> to use a Table with SWT.VIRTUAL. This is a
	 * workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=98585#c40
	 * The corresponding SWT bug is
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=90321
	 */
	private static final boolean USE_VIRTUAL = !"motif".equals(SWT.getPlatform()); //$NON-NLS-1$

	/*
	 * The delay before showing a secondary popup.
	 */
	private static final int POPUP_DELAY = 750;

	/*
	 * The character height hint for the popup. May be overridden by using
	 * setInitialPopupSize.
	 */
	private static final int POPUP_CHAR_HEIGHT = 10;

	/*
	 * The minimum pixel width for the popup. May be overridden by using
	 * setInitialPopupSize.
	 */
	private static final int POPUP_MINIMUM_WIDTH = 300;

	/*
	 * The pixel offset of the popup from the bottom corner of the control.
	 */
	private static final int POPUP_OFFSET = 3;

	/*
	 * Empty string.
	 */
	static final String EMPTY = ""; //$NON-NLS-1$

	/*
	 * The object that provides content proposals.
	 */
	private IContentProposalProvider proposalProvider;

	/*
	 * A label provider used to display proposals in the popup, and to extract
	 * Strings from non-String proposals.
	 */
	private ILabelProvider labelProvider;

	/*
	 * The control for which content proposals are provided.
	 */
	private final Control control;

	/*
	 * The adapter used to extract the String contents from an arbitrary
	 * control.
	 */
	private final IControlContentAdapter controlContentAdapter;

	/*
	 * The popup used to show proposals.
	 */
	private ContentProposalPopup popup;

	/*
	 * The keystroke that signifies content proposals should be shown.
	 */
	private final KeyStroke triggerKeyStroke;

	/*
	 * The String containing characters that auto-activate the popup.
	 */
	private String autoActivateString;

	/*
	 * Integer that indicates how an accepted proposal should affect the
	 * control. One of PROPOSAL_IGNORE, PROPOSAL_INSERT, or PROPOSAL_REPLACE.
	 * Default value is PROPOSAL_INSERT.
	 */
	private int proposalAcceptanceStyle = PROPOSAL_INSERT;

	/*
	 * A boolean that indicates whether key events received while the proposal
	 * popup is open should also be propagated to the control. Default value is
	 * true.
	 */
	private boolean propagateKeys = true;

	/*
	 * Integer that indicates the filtering style. One of FILTER_CHARACTER,
	 * FILTER_CUMULATIVE, FILTER_NONE.
	 */
	private int filterStyle = FILTER_NONE;

	/*
	 * The listener we install on the control.
	 */
	private Listener controlListener;

	/*
	 * The list of IContentProposalListener listeners.
	 */
	private final ListenerList proposalListeners = new ListenerList();

	/*
	 * The list of IContentProposalListener2 listeners.
	 */
	private final ListenerList proposalListeners2 = new ListenerList();

	/*
	 * Flag that indicates whether the adapter is enabled. In some cases,
	 * adapters may be installed but depend upon outside state.
	 */
	private boolean isEnabled = true;

	/*
	 * The delay in milliseconds used when autoactivating the popup.
	 */
	private int autoActivationDelay = 0;

	/*
	 * A boolean indicating whether a keystroke has been received. Used to see
	 * if an autoactivation delay was interrupted by a keystroke.
	 */
	private boolean receivedKeyDown;

	/*
	 * The desired size in pixels of the proposal popup.
	 */
	private Point popupSize;

	/*
	 * The remembered position of the insertion position. Not all controls will
	 * restore the insertion position if the proposal popup gets focus, so we
	 * need to remember it.
	 */
	private int insertionPos = -1;

	/*
	 * A flag that indicates that a pending modify event was caused by the
	 * adapter rather than the user.
	 */
	private boolean modifyingControlContent = false;

	public IInformationalControlContentProducer getProducer() {
		return this.contentCreator;
	}

	private final IInformationalControlContentProducer contentCreator;

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
	 * @param keyStroke
	 *            the keystroke that will invoke the content proposal popup. If
	 *            this value is <code>null</code>, then proposals will be
	 *            activated automatically when any of the auto activation
	 *            characters are typed.
	 * @param autoActivationCharacters
	 *            An array of characters that trigger auto-activation of content
	 *            proposal. If specified, these characters will trigger
	 *            auto-activation of the proposal popup, regardless of whether
	 *            an explicit invocation keyStroke was specified. If this
	 *            parameter is <code>null</code>, then only a specified
	 *            keyStroke will invoke content proposal. If this parameter is
	 *            <code>null</code> and the keyStroke parameter is
	 *            <code>null</code>, then all alphanumeric characters will
	 *            auto-activate content proposal.
	 */
	public ContentProposalAdapter(Control control,
			IControlContentAdapter controlContentAdapter,
			IContentProposalProvider proposalProvider, KeyStroke keyStroke,
			char[] autoActivationCharacters) {
		super();
		// We always assume the control and content adapter are valid.
		Assert.isNotNull(control);
		Assert.isNotNull(controlContentAdapter);
		this.contentCreator = (IInformationalControlContentProducer) control
				.getData(POPUP_CONTENT_CREATOR);
		this.control = control;
		this.controlContentAdapter = controlContentAdapter;

		// The rest of these may be null
		this.proposalProvider = proposalProvider;
		this.triggerKeyStroke = keyStroke;
		if (autoActivationCharacters != null) {
			this.autoActivateString = new String(autoActivationCharacters);
		}
		this.addControlListener(control);
	}

	/**
	 * Get the control on which the content proposal adapter is installed.
	 * 
	 * @return the control on which the proposal adapter is installed.
	 */
	public Control getControl() {
		return this.control;
	}

	/**
	 * Get the label provider that is used to show proposals.
	 * 
	 * @return the {@link ILabelProvider} used to show proposals, or
	 *         <code>null</code> if one has not been installed.
	 */
	public ILabelProvider getLabelProvider() {
		return this.labelProvider;
	}

	/**
	 * Return a boolean indicating whether the receiver is enabled.
	 * 
	 * @return <code>true</code> if the adapter is enabled, and
	 *         <code>false</code> if it is not.
	 */
	public boolean isEnabled() {
		return this.isEnabled;
	}

	/**
	 * Set the label provider that is used to show proposals. The lifecycle of
	 * the specified label provider is not managed by this adapter. Clients must
	 * dispose the label provider when it is no longer needed.
	 * 
	 * @param labelProvider
	 *            the (@link ILabelProvider} used to show proposals.
	 */
	public void setLabelProvider(ILabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	/**
	 * Return the proposal provider that provides content proposals given the
	 * current content of the field. A value of <code>null</code> indicates that
	 * there are no content proposals available for the field.
	 * 
	 * @return the {@link IContentProposalProvider} used to show proposals. May
	 *         be <code>null</code>.
	 */
	public IContentProposalProvider getContentProposalProvider() {
		return this.proposalProvider;
	}

	/**
	 * Set the content proposal provider that is used to show proposals.
	 * 
	 * @param proposalProvider
	 *            the {@link IContentProposalProvider} used to show proposals
	 */
	public void setContentProposalProvider(
			IContentProposalProvider proposalProvider) {
		this.proposalProvider = proposalProvider;
	}

	/**
	 * Return the array of characters on which the popup is autoactivated.
	 * 
	 * @return An array of characters that trigger auto-activation of content
	 *         proposal. If specified, these characters will trigger
	 *         auto-activation of the proposal popup, regardless of whether an
	 *         explicit invocation keyStroke was specified. If this parameter is
	 *         <code>null</code>, then only a specified keyStroke will invoke
	 *         content proposal. If this value is <code>null</code> and the
	 *         keyStroke value is <code>null</code>, then all alphanumeric
	 *         characters will auto-activate content proposal.
	 */
	public char[] getAutoActivationCharacters() {
		if (this.autoActivateString == null) {
			return null;
		}
		return this.autoActivateString.toCharArray();
	}

	/**
	 * Set the array of characters that will trigger autoactivation of the
	 * popup.
	 * 
	 * @param autoActivationCharacters
	 *            An array of characters that trigger auto-activation of content
	 *            proposal. If specified, these characters will trigger
	 *            auto-activation of the proposal popup, regardless of whether
	 *            an explicit invocation keyStroke was specified. If this
	 *            parameter is <code>null</code>, then only a specified
	 *            keyStroke will invoke content proposal. If this parameter is
	 *            <code>null</code> and the keyStroke value is <code>null</code>
	 *            , then all alphanumeric characters will auto-activate content
	 *            proposal.
	 * 
	 */
	public void setAutoActivationCharacters(char[] autoActivationCharacters) {
		if (autoActivationCharacters == null) {
			this.autoActivateString = null;
		} else {
			this.autoActivateString = new String(autoActivationCharacters);
		}
	}

	/**
	 * Set the delay, in milliseconds, used before any autoactivation is
	 * triggered.
	 * 
	 * @return the time in milliseconds that will pass before a popup is
	 *         automatically opened
	 */
	public int getAutoActivationDelay() {
		return this.autoActivationDelay;

	}

	/**
	 * Set the delay, in milliseconds, used before autoactivation is triggered.
	 * 
	 * @param delay
	 *            the time in milliseconds that will pass before a popup is
	 *            automatically opened
	 */
	public void setAutoActivationDelay(int delay) {
		this.autoActivationDelay = delay;

	}

	/**
	 * Get the integer style that indicates how an accepted proposal affects the
	 * control's content.
	 * 
	 * @return a constant indicating how an accepted proposal should affect the
	 *         control's content. Should be one of <code>PROPOSAL_INSERT</code>,
	 *         <code>PROPOSAL_REPLACE</code>, or <code>PROPOSAL_IGNORE</code>.
	 *         (Default is <code>PROPOSAL_INSERT</code>).
	 */
	public int getProposalAcceptanceStyle() {
		return this.proposalAcceptanceStyle;
	}

	/**
	 * Set the integer style that indicates how an accepted proposal affects the
	 * control's content.
	 * 
	 * @param acceptance
	 *            a constant indicating how an accepted proposal should affect
	 *            the control's content. Should be one of
	 *            <code>PROPOSAL_INSERT</code>, <code>PROPOSAL_REPLACE</code>,
	 *            or <code>PROPOSAL_IGNORE</code>
	 */
	public void setProposalAcceptanceStyle(int acceptance) {
		this.proposalAcceptanceStyle = acceptance;
	}

	/**
	 * Return the integer style that indicates how keystrokes affect the content
	 * of the proposal popup while it is open.
	 * 
	 * @return a constant indicating how keystrokes in the proposal popup affect
	 *         filtering of the proposals shown. <code>FILTER_NONE</code>
	 *         specifies that no filtering will occur in the content proposal
	 *         list as keys are typed. <code>FILTER_CUMULATIVE</code> specifies
	 *         that the content of the popup will be filtered by a string
	 *         containing all the characters typed since the popup has been
	 *         open. <code>FILTER_CHARACTER</code> specifies the content of the
	 *         popup will be filtered by the most recently typed character. The
	 *         default is <code>FILTER_NONE</code>.
	 */
	public int getFilterStyle() {
		return this.filterStyle;
	}

	/**
	 * Set the integer style that indicates how keystrokes affect the content of
	 * the proposal popup while it is open. Popup-based filtering is useful for
	 * narrowing and navigating the list of proposals provided once the popup is
	 * open. Filtering of the proposals will occur even when the control content
	 * is not affected by user typing. Note that automatic filtering is not used
	 * to achieve content-sensitive filtering such as auto-completion. Filtering
	 * that is sensitive to changes in the control content should be performed
	 * by the supplied {@link IContentProposalProvider}.
	 * 
	 * @param filterStyle
	 *            a constant indicating how keystrokes received in the proposal
	 *            popup affect filtering of the proposals shown.
	 *            <code>FILTER_NONE</code> specifies that no automatic filtering
	 *            of the content proposal list will occur as keys are typed in
	 *            the popup. <code>FILTER_CUMULATIVE</code> specifies that the
	 *            content of the popup will be filtered by a string containing
	 *            all the characters typed since the popup has been open.
	 *            <code>FILTER_CHARACTER</code> specifies that the content of
	 *            the popup will be filtered by the most recently typed
	 *            character.
	 */
	public void setFilterStyle(int filterStyle) {
		this.filterStyle = filterStyle;
	}

	/**
	 * Return the size, in pixels, of the content proposal popup.
	 * 
	 * @return a Point specifying the last width and height, in pixels, of the
	 *         content proposal popup.
	 */
	public Point getPopupSize() {
		return this.popupSize;
	}

	/**
	 * Set the size, in pixels, of the content proposal popup. This size will be
	 * used the next time the content proposal popup is opened.
	 * 
	 * @param size
	 *            a Point specifying the desired width and height, in pixels, of
	 *            the content proposal popup.
	 */
	public void setPopupSize(Point size) {
		this.popupSize = size;
	}

	/**
	 * Get the boolean that indicates whether key events (including
	 * auto-activation characters) received by the content proposal popup should
	 * also be propagated to the adapted control when the proposal popup is
	 * open.
	 * 
	 * @return a boolean that indicates whether key events (including
	 *         auto-activation characters) should be propagated to the adapted
	 *         control when the proposal popup is open. Default value is
	 *         <code>true</code>.
	 */
	public boolean getPropagateKeys() {
		return this.propagateKeys;
	}

	/**
	 * Set the boolean that indicates whether key events (including
	 * auto-activation characters) received by the content proposal popup should
	 * also be propagated to the adapted control when the proposal popup is
	 * open.
	 * 
	 * @param propagateKeys
	 *            a boolean that indicates whether key events (including
	 *            auto-activation characters) should be propagated to the
	 *            adapted control when the proposal popup is open.
	 */
	public void setPropagateKeys(boolean propagateKeys) {
		this.propagateKeys = propagateKeys;
	}

	/**
	 * Return the content adapter that can get or retrieve the text contents
	 * from the adapter's control. This method is used when a client, such as a
	 * content proposal listener, needs to update the control's contents
	 * manually.
	 * 
	 * @return the {@link IControlContentAdapter} which can update the control
	 *         text.
	 */
	public IControlContentAdapter getControlContentAdapter() {
		return this.controlContentAdapter;
	}

	/**
	 * Set the boolean flag that determines whether the adapter is enabled.
	 * 
	 * @param enabled
	 *            <code>true</code> if the adapter is enabled and responding to
	 *            user input, <code>false</code> if it is ignoring user input.
	 * 
	 */
	public void setEnabled(boolean enabled) {
		// If we are disabling it while it's proposing content, close the
		// content proposal popup.
		if (this.isEnabled && !enabled) {
			if (this.popup != null) {
				this.popup.close();
			}
		}
		this.isEnabled = enabled;
	}

	/**
	 * Add the specified listener to the list of content proposal listeners that
	 * are notified when content proposals are chosen.
	 * </p>
	 * 
	 * @param listener
	 *            the IContentProposalListener to be added as a listener. Must
	 *            not be <code>null</code>. If an attempt is made to register an
	 *            instance which is already registered with this instance, this
	 *            method has no effect.
	 * 
	 * @see org.eclipse.jface.fieldassist.IContentProposalListener
	 */
	public void addContentProposalListener(IContentProposalListener listener) {
		this.proposalListeners.add(listener);
	}

	/**
	 * Removes the specified listener from the list of content proposal
	 * listeners that are notified when content proposals are chosen. </p>
	 * 
	 * @param listener
	 *            the IContentProposalListener to be removed as a listener. Must
	 *            not be <code>null</code>. If the listener has not already been
	 *            registered, this method has no effect.
	 * 
	 * @since 3.3
	 * @see org.eclipse.jface.fieldassist.IContentProposalListener
	 */
	public void removeContentProposalListener(IContentProposalListener listener) {
		this.proposalListeners.remove(listener);
	}

	/**
	 * Add the specified listener to the list of content proposal listeners that
	 * are notified when a content proposal popup is opened or closed. </p>
	 * 
	 * @param listener
	 *            the IContentProposalListener2 to be added as a listener. Must
	 *            not be <code>null</code>. If an attempt is made to register an
	 *            instance which is already registered with this instance, this
	 *            method has no effect.
	 * 
	 * @since 3.3
	 * @see org.eclipse.jface.fieldassist.IContentProposalListener2
	 */
	public void addContentProposalListener(IContentProposalListener2 listener) {
		this.proposalListeners2.add(listener);
	}

	/**
	 * Remove the specified listener from the list of content proposal listeners
	 * that are notified when a content proposal popup is opened or closed. </p>
	 * 
	 * @param listener
	 *            the IContentProposalListener2 to be removed as a listener.
	 *            Must not be <code>null</code>. If the listener has not already
	 *            been registered, this method has no effect.
	 * 
	 * @since 3.3
	 * @see org.eclipse.jface.fieldassist.IContentProposalListener2
	 */
	public void removeContentProposalListener(IContentProposalListener2 listener) {
		this.proposalListeners2.remove(listener);
	}

	/*
	 * Add our listener to the control. Debug information to be left in until
	 * this support is stable on all platforms.
	 */
	private void addControlListener(Control control) {
		if (DEBUG) {
			System.out
					.println("ContentProposalListener#installControlListener()"); //$NON-NLS-1$
		}

		if (this.controlListener != null) {
			return;
		}
		this.controlListener = new Listener() {
			public void handleEvent(Event e) {
				if (!ContentProposalAdapter.this.isEnabled) {
					return;
				}

				switch (e.type) {
				case SWT.Traverse:
				case SWT.KeyDown:
					if (DEBUG) {
						StringBuffer sb;
						if (e.type == SWT.Traverse) {
							sb = new StringBuffer("Traverse"); //$NON-NLS-1$
						} else {
							sb = new StringBuffer("KeyDown"); //$NON-NLS-1$
						}
						sb.append(" received by adapter"); //$NON-NLS-1$
						this.dump(sb.toString(), e);
					}
					// If the popup is open, it gets first shot at the
					// keystroke and should set the doit flags appropriately.
					if (ContentProposalAdapter.this.popup != null) {
						ContentProposalAdapter.this.popup
								.getTargetControlListener().handleEvent(e);
						if (DEBUG) {
							StringBuffer sb;
							if (e.type == SWT.Traverse) {
								sb = new StringBuffer("Traverse"); //$NON-NLS-1$
							} else {
								sb = new StringBuffer("KeyDown"); //$NON-NLS-1$
							}
							sb.append(" after being handled by popup"); //$NON-NLS-1$
							this.dump(sb.toString(), e);
						}

						return;
					}

					// We were only listening to traverse events for the popup
					if (e.type == SWT.Traverse) {
						return;
					}

					// The popup is not open. We are looking at keydown events
					// for a trigger to open the popup.
					if (ContentProposalAdapter.this.triggerKeyStroke != null) {
						// Either there are no modifiers for the trigger and we
						// check the character field...
						if (((ContentProposalAdapter.this.triggerKeyStroke
								.getModifierKeys() == KeyStroke.NO_KEY) && (ContentProposalAdapter.this.triggerKeyStroke
								.getNaturalKey() == e.character))
								||
								// ...or there are modifiers, in which case the
								// keycode and state must match
								((ContentProposalAdapter.this.triggerKeyStroke
										.getNaturalKey() == e.keyCode) && ((ContentProposalAdapter.this.triggerKeyStroke
										.getModifierKeys() & e.stateMask) == ContentProposalAdapter.this.triggerKeyStroke
										.getModifierKeys()))) {
							// We never propagate the keystroke for an explicit
							// keystroke invocation of the popup
							e.doit = false;
							ContentProposalAdapter.this
									.openProposalPopup(false);
							return;
						}
					}
					/*
					 * The triggering keystroke was not invoked. Check for
					 * autoactivation characters.
					 */
					if (e.character != 0) {
						// Auto-activation characters were specified. Check
						// them.
						if (ContentProposalAdapter.this.autoActivateString != null) {
							if (ContentProposalAdapter.this.autoActivateString
									.indexOf(e.character) >= 0) {
								e.doit = ContentProposalAdapter.this.propagateKeys;
								ContentProposalAdapter.this.autoActivate();
							}
						} else {
							// No autoactivation occurred, so record the key
							// down
							// as a means to interrupt any autoactivation that
							// is
							// pending.
							ContentProposalAdapter.this.receivedKeyDown = true;
						}
					}
					break;

				// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=147377
				// Given that we will close the popup when there are no valid
				// proposals, we must reopen it when there are. Normally, the
				// keydown event handling will catch all the cases where it
				// should reopen. But when autoactivation should occur on all
				// content changes, we check it here after keys have been
				// processed.
				// See also https://bugs.eclipse.org/bugs/show_bug.cgi?id=183650
				// We should not autoactivate if the content change was caused
				// by the popup itself.
				case SWT.Modify:
					if ((ContentProposalAdapter.this.triggerKeyStroke == null)
							&& (ContentProposalAdapter.this.autoActivateString == null)
							&& !ContentProposalAdapter.this.modifyingControlContent) {
						if (DEBUG) {
							this
									.dump(
											"Modify event triggers autoactivation", e); //$NON-NLS-1$
						}
						ContentProposalAdapter.this.autoActivate();
					}
					break;
				default:
					break;
				}
			}

			/**
			 * Dump the given events to "standard" output.
			 * 
			 * @param who
			 *            who is dumping the event
			 * @param e
			 *            the event
			 */
			private void dump(String who, Event e) {
				final StringBuffer sb = new StringBuffer(
						"--- [ContentProposalAdapter]\n"); //$NON-NLS-1$
				sb.append(who);
				sb.append(" - e: keyCode=" + e.keyCode + this.hex(e.keyCode)); //$NON-NLS-1$
				sb.append("; character=" + e.character + this.hex(e.character)); //$NON-NLS-1$
				sb.append("; stateMask=" + e.stateMask + this.hex(e.stateMask)); //$NON-NLS-1$
				sb.append("; doit=" + e.doit); //$NON-NLS-1$
				sb.append("; detail=" + e.detail + this.hex(e.detail)); //$NON-NLS-1$
				sb.append("; widget=" + e.widget); //$NON-NLS-1$
				System.out.println(sb);
			}

			private String hex(int i) {
				return "[0x" + Integer.toHexString(i) + ']'; //$NON-NLS-1$
			}
		};
		control.addListener(SWT.KeyDown, this.controlListener);
		control.addListener(SWT.Traverse, this.controlListener);
		control.addListener(SWT.Modify, this.controlListener);

		if (DEBUG) {
			System.out
					.println("ContentProposalAdapter#installControlListener() - installed"); //$NON-NLS-1$
		}
	}
	
	private static HashSet<Object> popopGlobal=new HashSet<Object>();
	
	public static boolean isContentPopupOpen(){
		return !popopGlobal.isEmpty();
	}

	/**
	 * Open the proposal popup and display the proposals provided by the
	 * proposal provider. If there are no proposals to be shown, do not show the
	 * popup. This method returns immediately. That is, it does not wait for the
	 * popup to open or a proposal to be selected.
	 * 
	 * @param autoActivated
	 *            a boolean indicating whether the popup was autoactivated. If
	 *            false, a beep will sound when no proposals can be shown.
	 */
	private void openProposalPopup(boolean autoActivated) {
		if (this.isValid()) {
			if (this.popup == null) {
				// Check whether there are any proposals to be shown.
				this.recordCursorPosition(); // must be done before getting
												// proposals
				final IContentProposal[] proposals = this.getProposals();
				if (proposals.length > 0) {
					if (DEBUG) {
						System.out.println("POPUP OPENED BY PRECEDING EVENT"); //$NON-NLS-1$
					}
					this.recordCursorPosition();
					this.popup = new ContentProposalPopup(null, proposals);
					popopGlobal.add(popup);
					this.popup.openWidget();
					this.popup.getShell().addDisposeListener(
							new DisposeListener() {
								public void widgetDisposed(DisposeEvent event) {
									final Object ol=popup;
									Display.getCurrent().asyncExec(new Runnable(){

										public void run() {
											popopGlobal.remove(ol);		
										}
										
									});
									ContentProposalAdapter.this.popup = null;
									
								}
							});
					this.notifyPopupOpened();
				} else if (!autoActivated) {
					this.getControl().getDisplay().beep();
				}
			}
		}
	}

	/**
	 * Open the proposal popup and display the proposals provided by the
	 * proposal provider. This method returns immediately. That is, it does not
	 * wait for a proposal to be selected. This method is used by subclasses to
	 * explicitly invoke the opening of the popup. If there are no proposals to
	 * show, the popup will not open and a beep will be sounded.
	 */
	protected void openProposalPopup() {
		this.openProposalPopup(false);
	}

	/**
	 * Close the proposal popup without accepting a proposal. This method
	 * returns immediately, and has no effect if the proposal popup was not
	 * open. This method is used by subclasses to explicitly close the popup
	 * based on additional logic.
	 * 
	 * @since 3.3
	 */
	public void closeProposalPopup() {
		if (this.popup != null) {
			this.popup.close();
		}
	}

	/*
	 * A content proposal has been accepted. Update the control contents
	 * accordingly and notify any listeners.
	 * 
	 * @param proposal the accepted proposal
	 */
	private void proposalAccepted(IContentProposal proposal) {
		switch (this.proposalAcceptanceStyle) {
		case (PROPOSAL_REPLACE):
			this.setControlContent(proposal.getContent(), proposal
					.getCursorPosition());
			break;
		case (PROPOSAL_INSERT):
			this.insertControlContent(proposal.getContent(), proposal
					.getCursorPosition());
			break;
		case (PROPOSAL_INSERT_REPLACE):
			if (this.isValid()) {
				// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=183650
				this.modifyingControlContent = true;
				String controlContents = this.controlContentAdapter.getControlContents(getControl());
				int insertionBounds = this.controlContentAdapter.getCursorPosition(getControl());
				int a=insertionBounds;
				a--;
				for (;a>0&&a<controlContents.length();a--){
					if (!Character.isJavaIdentifierPart(controlContents.charAt(a))){
						a++;
						break;
					}
				}
				if (a<0){
					a=0;
				}
				String substring = controlContents.substring(0,a);
				String string = substring+proposal.getContent();
				controlContents=string + controlContents.substring(insertionBounds);
				int cp=string.length();
				this.controlContentAdapter.setControlContents(getControl(), controlContents, cp);
				this.modifyingControlContent = false;
			}
			break;
		default:
			// do nothing. Typically a listener is installed to handle this in
			// a custom way.
			break;
		}

		// In all cases, notify listeners of an accepted proposal.
		this.notifyProposalAccepted(proposal);
	}

	/*
	 * Set the text content of the control to the specified text, setting the
	 * cursorPosition at the desired location within the new contents.
	 */
	private void setControlContent(String text, int cursorPosition) {
		if (this.isValid()) {
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=183650
			this.modifyingControlContent = true;

			this.controlContentAdapter.setControlContents(this.control, text,
					cursorPosition);

			this.modifyingControlContent = false;
		}
	}

	/*
	 * Insert the specified text into the control content, setting the
	 * cursorPosition at the desired location within the new contents.
	 */
	private void insertControlContent(String text, int cursorPosition) {
		if (this.isValid()) {
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=183650
			this.modifyingControlContent = true;
			// Not all controls preserve their selection index when they lose
			// focus, so we must set it explicitly here to what it was before
			// the popup opened.
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=127108
			if (this.insertionPos != -1) {
				this.controlContentAdapter.setCursorPosition(this.control,
						this.insertionPos);
			}
			this.controlContentAdapter.insertControlContents(this.control,
					text, cursorPosition);
			this.modifyingControlContent = false;
		}
	}

	/*
	 * Check that the control and content adapter are valid.
	 */
	private boolean isValid() {
		return (this.control != null) && !this.control.isDisposed()
				&& (this.controlContentAdapter != null);
	}

	/*
	 * Record the control's cursor position.
	 */
	private void recordCursorPosition() {
		if (this.isValid()) {
			this.insertionPos = this.getControlContentAdapter()
					.getCursorPosition(this.control);

		}
	}

	/*
	 * Get the proposals from the proposal provider. Gets all of the proposals
	 * without doing any filtering.
	 */
	private IContentProposal[] getProposals() {
		if ((this.proposalProvider == null) || !this.isValid()) {
			return null;
		}
		if (DEBUG) {
			System.out.println(">>> obtaining proposals from provider"); //$NON-NLS-1$
		}
		int position = this.insertionPos;
		if (position == -1) {
			position = this.getControlContentAdapter().getCursorPosition(
					this.getControl());
		}
		final String contents = this.getControlContentAdapter()
				.getControlContents(this.getControl());
		final IContentProposal[] proposals = this.proposalProvider
				.getProposals(contents, position);
		return proposals;
	}

	/**
	 * Autoactivation has been triggered. Open the popup using any specified
	 * delay.
	 */
	private void autoActivate() {
		if (this.noAutoActivate) {
			return;
		}
		if (this.autoActivationDelay > 0) {
			final Runnable runnable = new Runnable() {
				public void run() {
					ContentProposalAdapter.this.receivedKeyDown = false;
					try {
						Thread
								.sleep(ContentProposalAdapter.this.autoActivationDelay);
					} catch (final InterruptedException e) {
					}
					if (!ContentProposalAdapter.this.isValid()
							|| ContentProposalAdapter.this.receivedKeyDown) {
						return;
					}
					ContentProposalAdapter.this.getControl().getDisplay()
							.syncExec(new Runnable() {
								public void run() {
									ContentProposalAdapter.this
											.openProposalPopup(true);
								}
							});
				}
			};
			final Thread t = new Thread(runnable);
			t.start();
		} else {
			// Since we do not sleep, we must open the popup
			// in an async exec. This is necessary because
			// this method may be called in the middle of handling
			// some event that will cause the cursor position or
			// other important info to change as a result of this
			// event occurring.
			this.getControl().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (ContentProposalAdapter.this.isValid()) {
						ContentProposalAdapter.this.openProposalPopup(true);
					}
				}
			});
		}
	}

	/*
	 * A proposal has been accepted. Notify interested listeners.
	 */
	private void notifyProposalAccepted(IContentProposal proposal) {
		if (DEBUG) {
			System.out.println("Notify listeners - proposal accepted."); //$NON-NLS-1$
		}
		final Object[] listenerArray = this.proposalListeners.getListeners();
		for (int i = 0; i < listenerArray.length; i++) {
			((IContentProposalListener) listenerArray[i])
					.proposalAccepted(proposal);
		}
	}

	private boolean opened;

	private boolean noAutoActivate;

	public boolean isOpened() {
		return this.opened;
	}

	/*
	 * The proposal popup has opened. Notify interested listeners.
	 */
	private void notifyPopupOpened() {
		this.opened = true;
		if (DEBUG) {
			System.out.println("Notify listeners - popup opened."); //$NON-NLS-1$
		}
		final Object[] listenerArray = this.proposalListeners2.getListeners();
		for (int i = 0; i < listenerArray.length; i++) {
			((IContentProposalListener2) listenerArray[i])
					.proposalPopupOpened(this);
		}
	}

	/*
	 * The proposal popup has closed. Notify interested listeners.
	 */
	private void notifyPopupClosed() {
		this.opened = false;
		if (DEBUG) {
			System.out.println("Notify listeners - popup closed."); //$NON-NLS-1$
		}
		final Object[] listenerArray = this.proposalListeners2.getListeners();
		for (int i = 0; i < listenerArray.length; i++) {
			((IContentProposalListener2) listenerArray[i])
					.proposalPopupClosed(this);
		}
	}

	public void dispose() {
		this.closeProposalPopup();
	}

	public void setNoActivate(boolean b) {
		this.noAutoActivate = b;
	}
}
