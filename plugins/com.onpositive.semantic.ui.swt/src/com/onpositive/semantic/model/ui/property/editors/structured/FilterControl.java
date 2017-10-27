package com.onpositive.semantic.model.ui.property.editors.structured;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.xml.language.CustomAttributeHandler;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.api.roles.DecorationContext;
import com.onpositive.semantic.model.api.roles.IRichTextDecorator;
import com.onpositive.semantic.model.api.roles.StyledString;
import com.onpositive.semantic.model.api.roles.StyledString.Style;
import com.onpositive.semantic.model.ui.generic.ElementListenerAdapter;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.property.editors.Messages;
import com.onpositive.semantic.model.ui.viewer.IHasInnerComposite;
import com.onpositive.semantic.model.ui.viewer.structured.PatternFilter;
import com.onpositive.semantic.model.ui.viewer.structured.StringMatcher.Position;
import com.onpositive.viewer.extension.coloring.ColoredViewersManager;

public class FilterControl extends AbstractUIElement<Composite> implements IHasInnerComposite
{
	private PatternFilter patternFilter;
	private String pattern;
	private Style decoratorStyle = new Style(null, "search"); //$NON-NLS-1$
	private Text innerControl;
	private boolean markOccurences = true;

	static {
		Display.getDefault();
		final Color color = JFaceResources.getColorRegistry().get("search"); //$NON-NLS-1$
		if (color == null) {
			JFaceResources.getColorRegistry().put("search", //$NON-NLS-1$
					new RGB(206, 204, 247));
		}
	}

	IRichTextDecorator decorator = new IRichTextDecorator() {

		public StyledString decorateRichText(DecorationContext context,
				StyledString text) {
			if ((FilterControl.this.pattern == null) || (FilterControl.this.pattern.length() == 0) || (text == null)) {
				return text;
			}
			final String string = text.toString();
			final Position indexOf = patternFilter==null?null:FilterControl.this.patternFilter.indexOf(string);
			if (indexOf == null) {
				return text;
			}
			text.colorize(indexOf.getStart(), indexOf.getEnd()
					- indexOf.getStart(), FilterControl.this.decoratorStyle);
			return text;
		}

	};
	private AbstractEnumeratedValueSelector<?> selector;
	private Composite owner;

	public String getPattern() {
		return this.pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern.toLowerCase().trim();
		if (this.patternFilter != null) {
			if (this.selector != null)
				ColoredViewersManager.clearLabels((ColumnViewer) this.selector.viewer);
			this.patternFilter.setPattern(pattern);
		}
	}

	public Style getDecoratorStyle() {
		return this.decoratorStyle;
	}

	public void setDecoratorStyle(Style decoratorStyle) {
		this.decoratorStyle = decoratorStyle;
		this.refreshSelector();
	}

	public FilterControl() {
		this.setCaption(Messages.FilterControl_Caption0);
		this.getLayoutHints().setGrabVertical(false);
	}

	@CustomAttributeHandler(value="targetId",handler=TargetIdHandler.class)
	public void setSelector(final AbstractEnumeratedValueSelector<?> sel) {
		if (this.selector != null) {
			this.selector.removeDecorator(this.decorator);
		}
		this.selector = sel;
		if (this.selector == null)
			return;
		if (this.markOccurences) {
			this.selector.addDecorator(this.decorator);
		}
		sel.addElementListener(new ElementListenerAdapter() {

			public void elementCreated(IUIElement<?> element) {
				FilterControl.this.handleCreate(sel);
			}
		});
		if (sel.isCreated()) {
			this.handleCreate(sel);
		}
	}

	protected Composite createControl(Composite conComposite)
	{
		this.owner = new Composite(conComposite, SWT.NONE);
		final GridLayout ls = new GridLayout();
		ls.marginHeight = 2;
		ls.marginWidth = 1;
		this.owner.setLayout(ls);
		innerControl = new Text(this.owner, !this.parentDrawsBorder() ? SWT.BORDER
				: SWT.NONE);
		
		innerControl.setLayoutData(new GridData(GridData.FILL_BOTH));
		innerControl.addKeyListener(new KeyListener(){
		
			public void keyReleased(KeyEvent e)
			{
				// TODO Auto-generated method stub
				
			}
		
			public void keyPressed(KeyEvent e)
			{
				if (selector != null && (e.keyCode == SWT.ARROW_DOWN || e.character == '\t'))			
					selector.getControl().setFocus();
			}
		});
		innerControl.addModifyListener(new ModifyListener() {

			long l0;
			Thread cs;
			
			public void modifyText(ModifyEvent e) {
				l0=System.currentTimeMillis();
				if (cs==null) {
				cs=new Thread() {

					
					
					public void run() {
						while(System.currentTimeMillis()-l0<300){
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								
							}
						}
						Display.getDefault().syncExec(new Runnable() {

							public void run() {
								if (!innerControl.isDisposed()){
								FilterControl.this.setPattern(innerControl.getText());
								cs=null;
								}
							}
							
						});
					}
				};
				cs.start();
				}
				
				
				
			}
		});
		return this.owner;
	}

	public boolean isMarkOccurences() {
		return this.markOccurences;
	}

	@HandlesAttributeDirectly("markOccurences")
	public void setMarkOccurences(boolean markOccurences) {
		this.markOccurences = markOccurences;
		if (markOccurences) {
			if (this.selector != null) {
				this.selector.addDecorator(this.decorator);
				this.refreshSelector();
			}
		} else {
			if (this.selector != null) {
				this.selector.removeDecorator(this.decorator);
				this.refreshSelector();
			}
		}
	}

	private void refreshSelector() {
		if ((this.selector != null) && this.selector.isCreated()) {
			ColoredViewersManager.clearLabels((ColumnViewer) this.selector.viewer);
			this.selector.viewer.getControl().redraw();
		}
	}

	private void handleCreate(final AbstractEnumeratedValueSelector<?> sel) {
		final ColumnViewer viewer = (ColumnViewer) sel.viewer;
		this.patternFilter = new PatternFilter(viewer);
		if (this.isCreated()) {
			this.patternFilter.setPattern(this.getText());
		}
	}

	public Composite getComposite() {
		return this.owner;
	}

	public Control getMainControl()
	{
		return innerControl;
	}
	
	
	protected Control getEventControl()
	{
		return getMainControl();
	}
}