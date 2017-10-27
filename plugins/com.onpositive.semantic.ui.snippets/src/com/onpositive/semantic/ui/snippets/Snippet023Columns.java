package com.onpositive.semantic.ui.snippets;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Point;

import com.onpositive.commons.Activator;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.model.api.property.java.FieldProperty;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.Column;
import com.onpositive.semantic.model.ui.generic.ColumnLayoutData;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.structured.columns.TableEnumeratedValueSelector;

public class Snippet023Columns extends AbstractSnippet {

	static class Person {

		String name;
		int age;
		boolean married;

		public Person(int age, String name) {
			super();
			this.age = age;
			this.name = name;
		}
	}

	protected ArrayList<Person> persons = new ArrayList<Person>();

	public Snippet023Columns() {
		this.persons.add(new Person(26, "Pavel Petrochenko"));
		this.persons.add(new Person(25, "Denis Denisenko"));
		this.persons.add(new Person(24, "Eugeny Chesnokov"));
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
		column.setLayoutData(new ColumnLayoutData(3, true));
		sl.addColumn(column);
		final Column column1 = new Column();
		column1.setCaption("Age");
		try {
			column.setProperty(new FieldProperty(Person.class
					.getDeclaredField("name")));
			column1.setProperty(new FieldProperty(Person.class
					.getDeclaredField("age")));
		} catch (final SecurityException e) {
			Activator.log(e);
		} catch (final NoSuchFieldException e) {
			Activator.log(e);
		}
		column1.setLayoutData(new ColumnLayoutData(1, true));
		final Column column2 = new Column("Married", "married");
		sl.addColumn(column1);
		sl.addColumn(column2);

		el.add(sl);
		DisposeBindingListener.linkBindingLifeCycle(bs, el);
		return el;
	}

	
	protected String getDescription() {
		return "Snippet shows how to create table with columns";
	}

	
	public String getGroup() {
		return "Java";
	}

	
	protected String getName() {
		return "Table with columns";
	}

	
	protected Point getSize() {
		return new Point(450, 300);
	}
}