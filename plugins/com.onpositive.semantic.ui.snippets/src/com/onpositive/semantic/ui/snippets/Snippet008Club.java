package com.onpositive.semantic.ui.snippets;

import java.util.HashSet;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.onpositive.commons.ISWTDescriptor;
import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.UIElementFactory;
import com.onpositive.commons.elements.UniversalUIElement;
import com.onpositive.semantic.model.api.property.adapters.RealmProviderAdapter;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
import com.onpositive.semantic.model.api.property.java.annotations.Unique;
import com.onpositive.semantic.model.api.roles.DecorationContext;
import com.onpositive.semantic.model.api.roles.IImageDescriptorDecorator;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.Realm;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.OneLineTextElement;
import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;

public class Snippet008Club extends AbstractSnippet {

	private final class ColoringDescriptor extends ImageDescriptor implements ISWTDescriptor {
		private final Object object;
		private final com.onpositive.semantic.model.api.roles.ImageDescriptor original;
		private final int groupCount;

		private ColoringDescriptor(Object object, com.onpositive.semantic.model.api.roles.ImageDescriptor original) {
			this.object = object;
			this.original = original;
			this.groupCount = ((Person) object).participates.size();
		}

		
		public ImageData getImageData() {
			final Display current = Display.getCurrent();
			final ImageData imageData = SWTImageManager.getImage(this.original).getImageData();
			final Image s = new Image(current, imageData.width, imageData.height);

			final GC g = new GC(s);
			final Image image = new Image(current, imageData);
			g.drawImage(image, 0, 0);
			image.dispose();
			final Person p = (Person) this.object;
			final int size = p.participates.size();
			for (int x = 0; x < imageData.width; x++) {
				for (int y = 0; y < imageData.height; y++) {
					final int pixel = imageData.getPixel(x, y);
					if (pixel != imageData.transparentPixel) {
						g.setForeground(new Color(current, new RGB(
								255 * size / 5, 100, 255 - 255 * size / 5)));
						g.drawPoint(x, y);
					}
					// imageData.setPixel(x, y, pixel);
				}
			}
			g.dispose();
			final ImageData imageData2 = s.getImageData();
			s.dispose();
			System.out.println(imageData2.palette.isDirect);
			return imageData2;
		}

		
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.getOuterType().hashCode();
			result = prime * result
					+ ((this.original == null) ? 0 : this.original.hashCode());
			return result * this.groupCount;
		}

		
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			final ColoringDescriptor other = (ColoringDescriptor) obj;
			if (!this.getOuterType().equals(other.getOuterType())) {
				return false;
			}

			if (this.original == null) {
				if (other.original != null) {
					return false;
				}
			} else if (!this.original.equals(other.original)) {
				return false;
			}

			return this.groupCount == other.groupCount;
		}

		private Snippet008Club getOuterType() {
			return Snippet008Club.this;
		}


		public ImageDescriptor getDescripror() {
			return this;
		}


		
	}

	public static class Club {
		Realm<String> groups = new Realm<String>();

		Realm<Person> persons = new Realm<Person>();

		public void add(Person person) {
			person.club = this;
			this.persons.add(person);
		}

		public Club() {
			this.groups.add("Chess"); //$NON-NLS-1$
			this.groups.add("Computer Games"); //$NON-NLS-1$
			this.groups.add("Climbing"); //$NON-NLS-1$
			this.groups.add("Painting"); //$NON-NLS-1$
			this.groups.add("Football fans"); //$NON-NLS-1$
		}

		public void print() {
			for (final Person c : this.persons.getContents()) {
				c.print();
			}
		}
	}

	public static class Person {

		public Person(String string) {
			this.name = string;
		}

		public void print() {
			System.out.println(this.name + this.participates);
		}

		@Unique
		@Required
		String name;

		@RealmProvider(GroupsDescriptor.class)
		HashSet<String> participates = new HashSet<String>();

		private Club club;

		
		public String toString() {
			return this.name;
		}

	}

	public static class GroupsDescriptor extends RealmProviderAdapter<String> {

		
		public IRealm<String> getRealm(IBinding model) {
			final Person ps = (Person) model.getObject();
			if (ps.club == null) {
				return null;
			}
			return ps.club.groups;
		}
	}

	Club club = new Club();

	public Snippet008Club() {
		final Person person = new Person("Mike"); //$NON-NLS-1$
		person.participates.add("Climbing"); //$NON-NLS-1$
		person.participates.add("Painting"); //$NON-NLS-1$
		this.club.add(person);
		final Person person2 = new Person("Columbo"); //$NON-NLS-1$
		person2.participates.add("Computer Games"); //$NON-NLS-1$
		this.club.add(person2);
	}

	
	protected AbstractUIElement<?> createContent() {
		JFaceResources.getColorRegistry().put("group", new RGB(200, 100, 100)); //$NON-NLS-1$
		final Binding objectBinding = new Binding(this);
		objectBinding.setMaxCardinality(1);
		final Binding name = objectBinding.getBinding("name"); //$NON-NLS-1$
		final Binding owner = objectBinding.getBinding("participates"); //$NON-NLS-1$
		objectBinding.setAutoCommit(true);
		objectBinding.setRegisterListeners(true);
		objectBinding.setRealm(this.club.persons);
		final Runnable printer = new Runnable() {

			public void run() {
				Snippet008Club.this.club.print();
			}
		};
		final Container el = new Container();
		el.setLayout(new GridLayout(3, false));
		final ListEnumeratedValueSelector<Person> sn = new ListEnumeratedValueSelector<Person>(
				objectBinding);
		sn.setNoScrollBar(true);
		sn.addDecorator(new IImageDescriptorDecorator() {

			public com.onpositive.semantic.model.api.roles.ImageDescriptor decorateImageDescriptor(
					com.onpositive.semantic.model.api.roles.ImageDescriptor original,
					DecorationContext context) {
				return new ColoringDescriptor(context.object, original);				
			}

		});
		name.setName("Name"); //$NON-NLS-1$
		final OneLineTextElement<String> str = new OneLineTextElement<String>(name);
		final ListEnumeratedValueSelector<String> snq = new ListEnumeratedValueSelector<String>(
				owner);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.minimumWidth = 200;
		snq.setAsCheckBox(true);
		layoutData.verticalSpan = 2;
		sn.setLayoutData(layoutData);
		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.minimumHeight = 200;
		layoutData.minimumWidth = 200;
		layoutData.horizontalSpan = 2;
		snq.setLayoutData(layoutData);
		final ButtonSelector sl = new ButtonSelector();
		sl.setCaption("Print Values"); //$NON-NLS-1$
		sl.setValue(printer);
		el.add(sn);
		el.add(str);
		el.add(snq);
		final UniversalUIElement<Label> createHorizontalSeparator = UIElementFactory
				.createHorizontalSeparator();
		createHorizontalSeparator.setLayoutData(GridDataFactory.fillDefaults()
				.span(3, 1).create());
		el.add(createHorizontalSeparator);
		el.add(sl);
		DisposeBindingListener.linkBindingLifeCycle(objectBinding, el);
		return el;
	}

	
	protected String getDescription() {
		return "This sample shows how edit club members"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Sample with sets"; //$NON-NLS-1$
	}

	
	public String getGroup() {
		return "Java"; //$NON-NLS-1$
	}
}
