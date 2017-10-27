package com.onpositive.semantic.model.ui.property.editors;

import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.ISetDelta;
import com.onpositive.semantic.ui.core.GenericLayoutHints;

public class DateTimeEditor extends AbstractEditor<DateTime> {

	private int style;

	private int size;

	private GregorianCalendar calendar = new GregorianCalendar();

	private boolean useCalendar = false;

	private DateTime timeControl;

	public final boolean isUseCalendar() {
		return useCalendar;
	}

	public final void setUseCalendar(boolean useCalendar) {
		this.useCalendar = useCalendar;
	}

	public DateTimeEditor() {
		getLayoutHints().setGrabVertical(false);

	}

	public int getStyle() {
		return style;
	}

	@HandlesAttributeDirectly("type")	
	public void setStyle(int style) {
		boolean rew = (this.style != style) && isCreated();
		this.style = style;
		if (rew) {
			recreate();
		}
	}

	public int getSize() {
		return size;
	}

	@HandlesAttributeDirectly("size")
	public void setSize(int size) {
		boolean rew = (this.size != size) && isCreated();
		this.size = size;
		if (rew) {
			recreate();
		}
	}

	protected void internalSetBinding(IBinding binding) {
		setValue(binding, binding.getValue());
	}

	protected void processValueChange(ISetDelta<?> valueElements) {
		if (!valueElements.getAddedElements().isEmpty()) {
			this.setValue(this.getBinding(), valueElements.getAddedElements()
					.iterator().next());
		} else {
			if (!valueElements.getChangedElements().isEmpty()) {
				this.setValue(this.getBinding(), valueElements
						.getChangedElements().iterator().next());
			} else {
				if (!valueElements.getRemovedElements().isEmpty()) {
					this.setValue(this.getBinding(), null);
				}
			}
		}
	}

	public void dispose() {
		super.dispose();
		owner = null;
	}

	private void setValue(IBinding binding, Object next) {
		GregorianCalendar gc = null;
		if (next instanceof Date) {
			Date d = (Date) next;
			gc = new GregorianCalendar();
			gc.setTime(d);
		} else if (next instanceof GregorianCalendar) {
			gc = (GregorianCalendar) next;
		}
		if (gc != null) {
			if (isCreated()) {
				initValue(getControl(), gc);
			} else {
				calendar = (GregorianCalendar) gc.clone();
			}
		}
		updateLayout(next);
	}

	private void updateLayout(Object next) {
		if (owner != null && layout != null) {
			if (next == null) {
				layout.topControl = t;
			} else {
				layout.topControl = cmtop;
			}
			owner.layout(true);
		}
	}

	private void initValue(DateTime dt, GregorianCalendar gc) {
		DateTime control = dt;
		update();
		control.setYear(gc.get(GregorianCalendar.YEAR));
		control.setMonth(gc.get(GregorianCalendar.MONTH));
		control.setDay(gc.get(GregorianCalendar.DAY_OF_MONTH));
		control.setHours(gc.get(GregorianCalendar.HOUR_OF_DAY));
		control.setMinutes(gc.get(GregorianCalendar.MINUTE));
		control.setSeconds(gc.get(GregorianCalendar.SECOND));
		if (timeControl != null) {
			timeControl.setHours(gc.get(GregorianCalendar.HOUR_OF_DAY));
			timeControl.setMinutes(gc.get(GregorianCalendar.MINUTE));
			timeControl.setSeconds(gc.get(GregorianCalendar.SECOND));
		}
	}

	public void update() {
		if (getBinding()!=null&&layout!=null){
			updateLayout(getBinding().getValue());
		}
	}

	public Object getValue() {
		if (isCreated()) {
			DateTime control = getControl();
			int year = control.getYear();
			int month = control.getMonth();
			int day = control.getDay();
			int hours = timeControl != null ? timeControl.getHours() : control
					.getHours();
			int mintes = timeControl != null ? timeControl.getMinutes()
					: control.getMinutes();
			int seconds = timeControl != null ? timeControl.getSeconds()
					: control.getSeconds();
			if ((style & (SWT.DATE | SWT.CALENDAR)) != 0) {
				calendar.set(GregorianCalendar.YEAR, year);
				calendar.set(GregorianCalendar.MONTH, month);
				calendar.set(GregorianCalendar.DAY_OF_MONTH, day);
			} else {
				calendar.set(GregorianCalendar.YEAR, 0);
				calendar.set(GregorianCalendar.MONTH, 0);
				calendar.set(GregorianCalendar.DAY_OF_MONTH, 0);
			}
			if (((style & SWT.TIME) != 0)) {
				calendar.set(GregorianCalendar.HOUR_OF_DAY, hours);
				calendar.set(GregorianCalendar.MINUTE, mintes);
				calendar.set(GregorianCalendar.SECOND, seconds);
			} else {
				calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
				calendar.set(GregorianCalendar.MINUTE, 0);
				calendar.set(GregorianCalendar.SECOND, 0);

			}
			calendar.set(GregorianCalendar.MILLISECOND, 0);
		}
		return this.useCalendar ? calendar : calendar.getTime();
	}

	Composite owner = null;

	private Text t;

	private StackLayout layout;

	private DateTime dt;

	public Control getLayoutControl() {
		if (owner != null) {
			return owner;
		}
		return super.getLayoutControl();
	}
	
	boolean allowClear;

	private Composite cmtop;

	public boolean isAllowClear() {
		return allowClear;
	}

	public void setAllowClear(boolean allowClear) {
		this.allowClear = allowClear;
	}

	protected DateTime createControl(Composite conComposite) {

		if ((style & SWT.DATE) != 0 && (style & SWT.TIME) != 0) {
			owner = new Composite(conComposite, SWT.NONE |SWT.BORDER);
			GridLayout gridLayout = new GridLayout(3, false);
			gridLayout.marginWidth = 0;
			gridLayout.marginHeight = 0;
			owner.setLayout(gridLayout);
			DateTime dt = new DateTime(owner, calcStyle() & ~SWT.TIME);
			dt.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					commitToBinding(getValue());
				}

			});
			timeControl = new DateTime(owner, calcStyle() & ~SWT.DATE);
			timeControl.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					commitToBinding(getValue());
				}

			});
			initValue(dt, calendar);
			return dt;
		} else {
			owner = new Composite(conComposite, SWT.NONE);
			layout = new StackLayout();
			owner.setLayout(layout);
			if (isAllowClear()){
			cmtop = new Composite(owner,SWT.NONE);
			cmtop.setBackground(conComposite.getBackground());
			GridLayout layout2 = new GridLayout(isAllowClear()?2:1, false);
			layout2.marginHeight=0;
			layout2.marginWidth=0;
			layout2.horizontalSpacing=2;
			
			cmtop.setLayout(layout2);
			dt = new DateTime(cmtop, calcStyle());
			dt.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					commitToBinding(getValue());
				}

			});
			dt.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());			
				ToolBar tb=new ToolBar(cmtop, SWT.NONE);
				ToolItem b=new ToolItem(tb,SWT.PUSH);
				b.setImage(SWTImageManager.getImage("com.onpositive.semantic.ui.delete"));
				b.addListener(SWT.Selection,new Listener(){

					public void handleEvent(Event event) {
						layout.topControl=t;
						commitToBinding(null);
						owner.layout();	
					}
					
				});
			}
			else{
				dt = new DateTime(owner, calcStyle());
				cmtop=dt;
				dt.addSelectionListener(new SelectionAdapter() {

					public void widgetSelected(SelectionEvent e) {
						commitToBinding(getValue());
					}

				});
				initValue(dt, calendar);
				layout.topControl=dt;
				layout=null;
				return dt;
			}
			t = new Text(owner, SWT.BORDER | SWT.READ_ONLY);
			t.setText("Not defined");
			t.addFocusListener(new FocusListener() {

				public void focusLost(FocusEvent e) {
					
				}

				public void focusGained(FocusEvent e) {
					layout.topControl=cmtop;
					commitToBinding(getValue());
					owner.layout();
				}
			});
			initValue(dt, calendar);
			return dt;
		}
	}

	public int calcStyle() {
		return super.calcStyle() | style | size;
	}
	
	
	protected Control createLabel(Composite contentParent) {
		Control label = super.createLabel(contentParent);
		return  label;
	}
	
	
}
