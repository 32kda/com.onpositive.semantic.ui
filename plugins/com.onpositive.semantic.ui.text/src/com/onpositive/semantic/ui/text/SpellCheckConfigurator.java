package com.onpositive.semantic.ui.text;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.quickassist.QuickAssistAssistant;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.Reconciler;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ActionHandler;
import org.eclipse.ui.commands.HandlerSubmission;
import org.eclipse.ui.commands.IHandler;
import org.eclipse.ui.commands.Priority;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.IReadOnlyDependent;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingAnnotation;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;
import org.eclipse.ui.texteditor.spelling.SpellingReconcileStrategy;
import org.eclipse.ui.texteditor.spelling.SpellingService;

import com.onpositive.semantic.ui.text.spelling.SpellingCompletionProcessor;

@SuppressWarnings("deprecation")
public class SpellCheckConfigurator {

	public static class SpellingSourceViewerConfiguration extends
			SourceViewerConfiguration {
		public class SpellingReconciler extends Reconciler {
			private final ISourceViewer sourceViewer;

			public SpellingReconciler(ISourceViewer sourceViewer) {
				this.sourceViewer = sourceViewer;

			}

			protected void process(DirtyRegion dirtyRegion) {

				IRegion region = dirtyRegion;

				if (region == null)
					region = new Region(0, getDocument().getLength());

				int length = dirtyRegion.getLength();

				IDocument document = getDocument();
				int offset = Math.min(dirtyRegion.getOffset(), document
						.getLength());
				if (offset == 0 && length == 0)
					return;
				try {
					IRegion lineInformationOfOffset = document
							.getLineInformationOfOffset(offset);
					IRegion lineInformationOfOffset1 = document
							.getLineInformationOfOffset(Math.min(document
									.getLength(), offset + length - 1));

					ITypedRegion r = new TypedRegion(lineInformationOfOffset
							.getOffset(), lineInformationOfOffset1.getLength()
							+ lineInformationOfOffset1.getOffset()
							- lineInformationOfOffset.getOffset(),
							IDocument.DEFAULT_CONTENT_TYPE);
					IReconcilingStrategy s = getReconcilingStrategy(r.getType());
					dirtyRegion = null;
					r = new TypedRegion(0, document.getLength(),
							IDocument.DEFAULT_CONTENT_TYPE);
					if (s != null) {
						if (dirtyRegion != null)
							s.reconcile(dirtyRegion, r);
						else
							s.reconcile(r);
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								StyledText textWidget = viewer.getTextWidget();
								if (textWidget != null) {
									textWidget.redraw();
								}
							}
						});

					}
				} catch (BadLocationException e) {
					// e.printStackTrace();
				}
			}

			public IReconcilingStrategy getReconcilingStrategy(
					String contentType) {
				SpellingReconcileStrategy spellingReconcileStrategy = new SpellingReconcileStrategy(
						sourceViewer, spellingService);
				spellingReconcileStrategy.setDocument(viewer.getDocument());
				return spellingReconcileStrategy;
			}
		}

		private final SpellingService spellingService;
		private final SourceViewer viewer;
		protected HashMap<SourceViewer, ContentAssistant> contentAssistants;

		public SpellingSourceViewerConfiguration(
				SpellingService spellingService, SourceViewer viewer) {
			this.spellingService = spellingService;
			this.viewer = viewer;
			contentAssistants = new HashMap<SourceViewer, ContentAssistant>();
		}

		public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
			ContentAssistant contentAssistant = contentAssistants
					.get(sourceViewer);
			if (contentAssistant == null) {
				contentAssistant = new ContentAssistant();
				contentAssistants.put((SourceViewer) sourceViewer,
						contentAssistant);
				SpellingCompletionProcessor processor = new SpellingCompletionProcessor();
				contentAssistant.setContentAssistProcessor(processor,
						IDocument.DEFAULT_CONTENT_TYPE);
			}
			return contentAssistant;
		}

		public IQuickAssistAssistant getQuickAssistAssistant(
				ISourceViewer sourceViewer) {
			final QuickAssistAssistant quickAssistAssistant = new QuickAssistAssistant();
			quickAssistAssistant
					.setQuickAssistProcessor(new org.eclipse.ui.texteditor.spelling.SpellingCorrectionProcessor());
			return quickAssistAssistant;
		}

		public IPresentationReconciler getPresentationReconciler(
				ISourceViewer sourceViewer) {
			return super.getPresentationReconciler(sourceViewer);
		}

		public IReconciler getReconciler(final ISourceViewer sourceViewer) {
			return new SpellingReconciler(sourceViewer);
		}
	}

	public final static class FocusController implements FocusListener {
		private SourceViewer viewer;
		private CommandManager commandManager;
		private Shell activeShell;

		private boolean createdForDialog = false;

		public boolean isCreatedForDialog() {
			return createdForDialog;
		}

		public void setCreatedForDialog(Shell shell) {
			if (shell != null) {
				this.createdForDialog = true;
				activeShell = shell;
			} else {
				createdForDialog = false;
			}
		}

		private Action quickAssist;
		private Action contentAssist;
		private Action undo;
		private Action redo;
		private Action cut;
		private Action copy;
		private Action paste;

		private ArrayList<HandlerSubmission> submissions;

		private KeyListener listener;

		private IEditorSite site;

		public IEditorSite getSite() {
			return site;
		}

		public void setSite(IEditorSite site) {
			this.site = site;
		}

		public FocusController(SourceViewer viewer) {
			submissions = new ArrayList<HandlerSubmission>();
			this.viewer = viewer;
		}

		public void setViewer(SourceViewer viewer) {
			this.viewer = viewer;
		}

		public void focusGained(FocusEvent e) {
			setFocus();
		}

		public void focusLost(FocusEvent e) {
			//site = null;
			//viewer = null;
			if (!createdForDialog) {
				if (commandManager != null
						&& commandManager.fActivationCodeTrigger != null) {
					this.commandManager.fActivationCodeTrigger
							.unregisterActionFromKeyActivation(quickAssist);
					this.commandManager.fActivationCodeTrigger
							.unregisterActionFromKeyActivation(contentAssist);
					this.commandManager.fActivationCodeTrigger
							.unregisterActionFromKeyActivation(undo);
					this.commandManager.fActivationCodeTrigger
							.unregisterActionFromKeyActivation(redo);
					this.commandManager.fActivationCodeTrigger.uninstall();
					this.commandManager = null;
				} else {
					for (int i = 0; i < submissions.size(); i++) {
						HandlerSubmission hs = submissions.get(i);
						if(hs != null){
							PlatformUI.getWorkbench().getCommandSupport()
                            .removeHandlerSubmission(hs);
							hs.getHandler().dispose();
						}
					}
					submissions.clear();
				}
			}

		}

		public void setFocus() {
			createActionList();
			if (!createdForDialog) {
				registerActionForEditor();
			} else {
				registerActionForShell();
			}
		}

		private void registerActionForShell() {

			IHandler handlerCopy = new ActionHandler(copy);
			HandlerSubmission submissionCopy = new HandlerSubmission(null,
					activeShell, site, copy.getActionDefinitionId(),
					handlerCopy, Priority.MEDIUM);

			IHandler handlerPaste = new ActionHandler(paste);
			HandlerSubmission submissionPaste = new HandlerSubmission(null,
					activeShell, site, paste.getActionDefinitionId(),
					handlerPaste, Priority.MEDIUM);

			IHandler handlerUndo = new ActionHandler(undo);
			HandlerSubmission submissionUndo = new HandlerSubmission(null,
					activeShell, site, undo.getActionDefinitionId(),
					handlerUndo, Priority.MEDIUM);

			IHandler handlerRedo = new ActionHandler(redo);
			HandlerSubmission submissionRedo = new HandlerSubmission(null,
					activeShell, site, redo.getActionDefinitionId(),
					handlerRedo, Priority.MEDIUM);

			IHandler handlerCA = new ActionHandler(contentAssist);
			HandlerSubmission submissionCA = new HandlerSubmission(null,
					activeShell, site, contentAssist.getActionDefinitionId(),
					handlerCA, Priority.MEDIUM);

			IHandler handlerQA = new ActionHandler(quickAssist);
			HandlerSubmission submissionQA = new HandlerSubmission(null,
					activeShell, site, quickAssist.getActionDefinitionId(),
					handlerQA, Priority.MEDIUM);

			submissions.add(submissionCopy);
			submissions.add(submissionPaste);
			submissions.add(submissionUndo);
			submissions.add(submissionRedo);
			submissions.add(submissionQA);
			submissions.add(submissionCA);

			PlatformUI.getWorkbench().getCommandSupport().addHandlerSubmission(
					submissionCopy);

			PlatformUI.getWorkbench().getCommandSupport().addHandlerSubmission(
					submissionPaste);

			PlatformUI.getWorkbench().getCommandSupport().addHandlerSubmission(
					submissionUndo);

			PlatformUI.getWorkbench().getCommandSupport().addHandlerSubmission(
					submissionRedo);

			PlatformUI.getWorkbench().getCommandSupport().addHandlerSubmission(
					submissionCA);

			PlatformUI.getWorkbench().getCommandSupport().addHandlerSubmission(
					submissionQA);

			return;
		}

		private void registerActionForEditor() {
			try {

				IWorkbenchPartSite ieSite = null;

				if (site == null) {
					ieSite = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage()
							.getActivePart().getSite();
				} else {
					ieSite = site;
				}

				final IKeyBindingService keyBindingService = ieSite
						.getKeyBindingService();
				if (this.commandManager == null) {
					System.out.println("CM == null! "
							+ keyBindingService.toString());
					this.commandManager = new CommandManager(keyBindingService,
							viewer);

					this.commandManager.setAction(
							ITextEditorActionDefinitionIds.QUICK_ASSIST,
							quickAssist);
					this.commandManager
							.setAction(
									ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS,
									contentAssist);
					this.commandManager.setAction(
							ITextEditorActionDefinitionIds.UNDO, undo);
					this.commandManager.setAction(
							ITextEditorActionDefinitionIds.REDO, redo);
					this.commandManager.setAction(
							ITextEditorActionDefinitionIds.COPY, copy);
					this.commandManager.setAction(
							ITextEditorActionDefinitionIds.PASTE, paste);
					this.commandManager.setAction(
							ITextEditorActionDefinitionIds.CUT, cut);

				}
			} catch (IllegalStateException e) {
				if (listener == null) {
					listener = new KeyListener() {

						public void keyPressed(KeyEvent e) {
							if (e.character == '1' && e.stateMask == SWT.CTRL) {
								viewer.doOperation(ISourceViewer.QUICK_ASSIST);
							}
							if (e.keyCode == ' ' && e.stateMask == SWT.CTRL) {
								viewer
										.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
							}
							if (e.keyCode == 'z' && e.stateMask == SWT.CTRL) {
								viewer.doOperation(ITextOperationTarget.UNDO);
							}
							if (e.keyCode == 'y' && e.stateMask == SWT.CTRL) {
								viewer.doOperation(ITextOperationTarget.REDO);
							}
						}

						public void keyReleased(KeyEvent e) {

						}

					};
					viewer.getTextWidget().addKeyListener(listener);
				}
			}
		}

		private void createActionList() {
			quickAssist = new Action() {

				public void run() {
					viewer.doOperation(ISourceViewer.QUICK_ASSIST);
				}
			};
			contentAssist = new Action() {

				public void run() {
					viewer.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
				}
			};
			contentAssist.setId("ContentAssist");
			;

			contentAssist
					.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
			quickAssist.setId(ITextEditorActionConstants.QUICK_ASSIST);
			quickAssist
					.setActionDefinitionId(ITextEditorActionDefinitionIds.QUICK_ASSIST);
			undo = new Action() {

				public void run() {
					viewer.doOperation(ITextOperationTarget.UNDO);
				}
			};

			redo = new Action() {

				public void run() {
					viewer.doOperation(ITextOperationTarget.REDO);
				}
			};
			redo.setActionDefinitionId(ITextEditorActionDefinitionIds.REDO);
			undo.setActionDefinitionId(ITextEditorActionDefinitionIds.UNDO);
			copy = new Action() {
				public void run() {
					if (viewer instanceof ITextViewer) {
						ITextViewer rtv = (ITextViewer) viewer;
						boolean focused = rtv.getTextWidget().isFocusControl();
						Control focusControl = Display.getCurrent()
								.getFocusControl();
						if (!focused) {
							if (focusControl instanceof StyledText) {
								((StyledText) focusControl).copy();
								return;
							}
							if(focusControl instanceof Text){
								((Text)focusControl).copy();
								return;
							}							
						}
						try{
						Method method = viewer.getClass().getMethod("copy");
						method.invoke(viewer);							
						} catch (Exception e) {
							viewer.doOperation(ITextOperationTarget.COPY);
						}
						
					} else {
						viewer.doOperation(ITextOperationTarget.COPY);
					}

				}
			};

			paste = new Action() {
				public void run() {					
					if (viewer instanceof ITextViewer) {
						ITextViewer rtv = (ITextViewer) viewer;
						boolean focused = rtv.getTextWidget().isFocusControl();
						Control focusControl = Display.getCurrent()
								.getFocusControl();
						if (!focused) {
							if (focusControl instanceof StyledText) {
								((StyledText) focusControl).paste();
								return;
							}
							if(focusControl instanceof Text){
								((Text)focusControl).paste();
								return;
							}
						}
						try{
							Method method = viewer.getClass().getMethod("paste");
							method.invoke(viewer);							
							} catch (Exception e) {
								viewer.doOperation(ITextOperationTarget.PASTE);
							}
					} else {
						viewer.doOperation(ITextOperationTarget.PASTE);
					}
				}
			};
			cut = new Action() {
				public void run() {
					if (viewer instanceof ITextViewer) {
						ITextViewer rtv = (ITextViewer) viewer;
						boolean focused = rtv.getTextWidget().isFocusControl();
						Control focusControl = Display.getCurrent()
								.getFocusControl();
						if (!focused) {
							if (focusControl instanceof StyledText) {
								((StyledText) focusControl).cut();
								return;
							}
							if(focusControl instanceof Text){
								//((Text)focusControl).cut();
								return;
							}	
						}
						try{
							Method method = viewer.getClass().getMethod("cut");
							method.invoke(viewer);							
							} catch (Exception e) {
							viewer.doOperation(ITextOperationTarget.PASTE);
						}
					} else {
						viewer.doOperation(ITextOperationTarget.CUT);
					}
				}
			};
			copy.setActionDefinitionId(ITextEditorActionDefinitionIds.COPY);
			paste.setActionDefinitionId(ITextEditorActionDefinitionIds.PASTE);
			cut.setActionDefinitionId(ITextEditorActionDefinitionIds.CUT);
		}
	}

	@SuppressWarnings("unchecked")
	static class CommandManager {

		private final Map fActions = new HashMap(10);

		public Map getfActions() {
			return fActions;
		}

		private final List fActivationCodes = new ArrayList(2);
		/** The verify key listener for activation code triggering. */
		private final ActivationCodeTrigger fActivationCodeTrigger = new ActivationCodeTrigger();

		public void setAction(String actionID, IAction action) {
			Assert.isNotNull(actionID);
			if (action == null) {
				action = (IAction) this.fActions.remove(actionID);
				if (action != null) {
					this.fActivationCodeTrigger
							.unregisterActionFromKeyActivation(action);
				}
			} else {
				if (action.getId() == null) {
					action.setId(actionID); // make sure the action ID has been
				}
				// set
				this.fActions.put(actionID, action);
				this.fActivationCodeTrigger
						.registerActionForKeyActivation(action);
			}
		}

		public CommandManager(IKeyBindingService serv, ISourceViewer viewer) {
			this.fActivationCodeTrigger.install(serv, viewer);
		}

		/**
		 * Internal key verify listener for triggering action activation codes.
		 */
		class ActivationCodeTrigger implements VerifyKeyListener {

			SourceViewer fSourceViewer;

			public IAction getAction(String actionID) {
				Assert.isNotNull(actionID);
				final IAction action = (IAction) CommandManager.this.fActions
						.get(actionID);

				return action;
			}

			/** Indicates whether this trigger has been installed. */
			private boolean fIsInstalled = false;
			/**
			 * The key binding service to use.
			 * 
			 * @since 2.0
			 */
			private IKeyBindingService fKeyBindingService;

			/*
			 * @see
			 * VerifyKeyListener#verifyKey(org.eclipse.swt.events.VerifyEvent)
			 */
			public void verifyKey(VerifyEvent event) {
				ActionActivationCode code = null;
				final int size = CommandManager.this.fActivationCodes.size();
				System.out.println("KEY!!! " + size);
				for (int i = 0; i < size; i++) {
					code = (ActionActivationCode) CommandManager.this.fActivationCodes
							.get(i);
					if (code.matches(event)) {
						final IAction action = this.getAction(code.fActionId);
						if (action != null) {

							if (action instanceof IUpdate) {
								((IUpdate) action).update();
							}

							if (!action.isEnabled()
									&& (action instanceof IReadOnlyDependent)) {
								final IReadOnlyDependent dependent = (IReadOnlyDependent) action;
								final boolean writable = dependent
										.isEnabled(true);
								if (writable) {
									event.doit = false;
									return;
								}
							} else if (action.isEnabled()) {
								event.doit = false;
								action.run();
								return;
							}
						}
					}
				}
			}

			/**
			 * Installs this trigger on the editor's text widget.
			 * 
			 * @since 2.0
			 */
			public void install(IKeyBindingService service, ISourceViewer viewer) {
				this.fKeyBindingService = service;
				this.fSourceViewer = (SourceViewer) viewer;
				System.out.println("---- install ----");
				if (!this.fIsInstalled) {
					System.out.println("not installed");
					if (this.fSourceViewer instanceof ITextViewerExtension) {
						System.out.println("!!!! 1");
						final ITextViewerExtension e = this.fSourceViewer;
						e.prependVerifyKeyListener(this);
					} else {
						final StyledText text = this.fSourceViewer
								.getTextWidget();
						text.addVerifyKeyListener(this);
					}

					this.fIsInstalled = true;
				}
			}

			/**
			 * Uninstalls this trigger from the editor's text widget.
			 * 
			 * @since 2.0
			 */
			public void uninstall() {
				if (this.fIsInstalled) {

					if (this.fSourceViewer instanceof ITextViewerExtension) {
						final ITextViewerExtension e = this.fSourceViewer;
						e.removeVerifyKeyListener(this);
					} else if (this.fSourceViewer != null) {
						final StyledText text = this.fSourceViewer
								.getTextWidget();
						if ((text != null) && !text.isDisposed()) {
							text
									.removeVerifyKeyListener(CommandManager.this.fActivationCodeTrigger);
						}
					}

					this.fIsInstalled = false;
					this.fKeyBindingService = null;
				}
			}

			/**
			 * Registers the given action for key activation.
			 * 
			 * @param action
			 *            the action to be registered
			 * @since 2.0
			 */
			public void registerActionForKeyActivation(IAction action) {
				if (this.fIsInstalled
						&& (action.getActionDefinitionId() != null)) {
					this.fKeyBindingService.registerAction(action);
				}
			}

			/**
			 * The given action is no longer available for key activation
			 * 
			 * @param action
			 *            the action to be unregistered
			 * @since 2.0
			 */
			public void unregisterActionFromKeyActivation(IAction action) {
				if (this.fIsInstalled
						&& (action.getActionDefinitionId() != null)) {
					this.fKeyBindingService.unregisterAction(action);
				}
			}

			/**
			 * Sets the key binding scopes for this editor.
			 * 
			 * @param keyBindingScopes
			 *            the key binding scopes
			 * @since 2.1
			 */
			public void setScopes(String[] keyBindingScopes) {
				if ((keyBindingScopes != null) && (keyBindingScopes.length > 0)) {
					this.fKeyBindingService.setScopes(keyBindingScopes);
				}
			}
		}

		/**
		 * Representation of action activation codes.
		 */
		static class ActionActivationCode {

			/** The action id. */
			public String fActionId;
			/** The character. */
			public char fCharacter;
			/** The key code. */
			public int fKeyCode = -1;
			/** The state mask. */
			public int fStateMask = SWT.DEFAULT;

			/**
			 * Creates a new action activation code for the given action id.
			 * 
			 * @param actionId
			 *            the action id
			 */
			public ActionActivationCode(String actionId) {
				this.fActionId = actionId;
			}

			/**
			 * Returns <code>true</code> if this activation code matches the
			 * given verify event.
			 * 
			 * @param event
			 *            the event to test for matching
			 * @return whether this activation code matches <code>event</code>
			 */
			public boolean matches(VerifyEvent event) {
				return ((event.character == this.fCharacter)
						&& ((this.fKeyCode == -1) || (event.keyCode == this.fKeyCode)) && ((this.fStateMask == SWT.DEFAULT) || (event.stateMask == this.fStateMask)));
			}
		}

	}

	// public void configure(ViewerTextElement element) {
	// Activator.getDefault().getLog().log(
	// new Status(IStatus.WARNING, Activator.getDefault().getBundle()
	// .getSymbolicName(), 1, "Started", null));
	// try {
	// final SourceViewer viewer = element.getViewer();
	// configure(viewer);
	// } catch (final Exception e) {
	// Activator.getDefault().getLog().log(
	// new Status(IStatus.WARNING, Activator.getDefault()
	// .getBundle().getSymbolicName(), 1, "Finished", e)); //
	// }
	//
	// // new CommandManager().
	// }

	public void configure(final SourceViewer viewer) {
		final SharedTextColors sharedTextColors = new SharedTextColors();
		final DefaultMarkerAnnotationAccess defaultMarkerAnnotationAccess = new DefaultMarkerAnnotationAccess();
		final SourceViewerDecorationSupport svs = new SourceViewerDecorationSupport(
				viewer, null, defaultMarkerAnnotationAccess, sharedTextColors) {

		};
		svs.setAnnotationPreference(new AnnotationPreference(
				"org.eclipse.ui.workbench.texteditor.spelling", "", "", "", 0));
		IPreferenceStore preferenceStore = EditorsUI.getPreferenceStore();
		final SpellingService spellingService = new SpellingService(
				preferenceStore);

		svs.install(null);

		viewer.configure(new SpellingSourceViewerConfiguration(spellingService,
				viewer));
		FocusController flistener = new FocusController(viewer);
		viewer.getTextWidget().addFocusListener(flistener);
		final IPropertyChangeListener listener = new IPropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent event) {
				doCheck(spellingService, viewer);
			}

		};
		if (Display.getCurrent().getFocusControl() == viewer.getTextWidget()) {
			flistener.setFocus();
		}
		EditorsUI.getPreferenceStore().addPropertyChangeListener(listener);
		viewer.getControl().addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				EditorsUI.getPreferenceStore().removePropertyChangeListener(
						listener);
				svs.dispose();
			}

		});

	}

	public static void doCheck(final SpellingService spellingService,
			final SourceViewer sv) {
		final ISpellingProblemCollector collector = new ISpellingProblemCollector() {

			ArrayList<SpellingProblem> prblms = new ArrayList<SpellingProblem>();

			public void accept(SpellingProblem problem) {
				final ICompletionProposal[] proposals = problem.getProposals();
				this.prblms.add(problem);
			}

			public void beginCollecting() {
				this.prblms.clear();
			}

			public void endCollecting() {
				final AnnotationModel annotationModel = (AnnotationModel) sv
						.getAnnotationModel();
				annotationModel.removeAllAnnotations();
				final Map annotationsToAdd = new HashMap();
				for (final SpellingProblem p : this.prblms) {
					final Annotation annotation = new SpellingAnnotation(p);
					annotationsToAdd.put(annotation, new Position(
							p.getOffset(), p.getLength()));
				}

				annotationModel.replaceAnnotations(new Annotation[] {},
						annotationsToAdd);
			}
		};
		final SpellingContext context = new SpellingContext();
		spellingService.check(sv.getDocument(), context, collector, null);
	}
}
