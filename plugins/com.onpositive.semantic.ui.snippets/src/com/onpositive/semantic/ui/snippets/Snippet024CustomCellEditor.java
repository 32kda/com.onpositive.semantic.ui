package com.onpositive.semantic.ui.snippets;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.adapters.ILabelLookup;
import com.onpositive.semantic.model.api.property.adapters.NotFoundException;
import com.onpositive.semantic.model.api.property.java.annotations.LabelLookup;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.Column;
import com.onpositive.semantic.model.ui.generic.ColumnLayoutData;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.structured.celleditor.CellEditorFactory;
import com.onpositive.semantic.model.ui.property.editors.structured.celleditor.ICellEditorFactory;
import com.onpositive.semantic.model.ui.property.editors.structured.columns.TableEnumeratedValueSelector;

public class Snippet024CustomCellEditor extends AbstractSnippet {

	public static class FileLabelLookup implements ILabelLookup {

		public Object lookUpByLabel(IBinding model, String label)
				throws NotFoundException {
			final File fl = new File(label);
			final boolean exists = fl.exists();
			if (exists) {
				return fl;
			}
			throw new NotFoundException("File " + label
					+ " not exists or points to unaccessible path");
		}
	}

	public static class FileCellEditorFactory implements ICellEditorFactory {

		public CellEditor createEditor(Object parentObject, Object object,
				Viewer parent, IProperty property) {
			final DialogCellEditor dlg = new DialogCellEditor((Composite) parent
					.getControl()) {

				
				
				protected Object openDialogBox(Control cellEditorWindow) {
					final DirectoryDialog dlg = new DirectoryDialog(cellEditorWindow
							.getShell(), SWT.OPEN);
					final String sm = dlg.open();
					if (sm != null) {
						return new File(sm);
					}
					return null;
				}
			};
			return dlg;
		}

	}

	static class BuildPathEntry {

		String name;

		@LabelLookup(FileLabelLookup.class)
		@CellEditorFactory(FileCellEditorFactory.class)
		File path;

		public BuildPathEntry(String name, File path) {
			this.name = name;
			this.path = path;
		}
	}

	protected ArrayList<BuildPathEntry> persons = new ArrayList<BuildPathEntry>();

	public Snippet024CustomCellEditor() {
		final File[] roots = File.listRoots();
		int a = 0;
		for (final File file : roots) {
			this.persons.add(new BuildPathEntry("Root" + a++, file));
		}
	}

	
	protected AbstractUIElement<?> createContent() {
		final Binding bs = new Binding(this.persons);
		final Container el = new Container();
		el.setLayoutManager(new OneElementOnLineLayouter());
		final TableEnumeratedValueSelector sl = new TableEnumeratedValueSelector();
		sl.setAllowCellEditing(true);
		sl.setBinding(bs);
		final Column column = new Column();
		column.setCaption("Name");
		column.setId("name");
		column.setRole("buildpathEntry");
		column.setLayoutData(new ColumnLayoutData(1, true));
		sl.addColumn(column);
		final Column column1 = new Column();
		column1.setCaption("Path");
		column1.setId("path");
		column1.setLayoutData(new ColumnLayoutData(2, true));
		sl.addColumn(column1);
		el.add(sl);
		DisposeBindingListener.linkBindingLifeCycle(bs, el);
		return el;
	}

	
	protected String getDescription() {
		return "Snippet shows how to setup custom cell editor using annotations";
	}

	
	public String getGroup() {
		return "Java";
	}

	
	protected String getName() {
		return "Table with custom cell editor";
	}

	
	protected Point getSize() {
		return new Point(450, 300);
	}
}