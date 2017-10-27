package com.onpositive.datastorage.tests;

import java.util.Collection;
import java.util.Set;

import com.onpositive.datamodel.core.DataStoreRealm;
import com.onpositive.datamodel.core.IEntry;
import com.onpositive.datamodel.impl.BinaryRDFDocument;
import com.onpositive.datamodel.model.DataModel;
import com.onpositive.datamodel.model.DefaultModelProperty;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.property.IProperty;

public class DataStoreRealmTest {

	private static final String STREET = "Street "; //$NON-NLS-1$
	private static final String LOCATION = "location"; //$NON-NLS-1$

	public static void main(String[] args) {
		final BinaryRDFDocument rs = new BinaryRDFDocument();
		final DataModel model = new DataModel();
		model.registerProperty(new DefaultModelProperty(LOCATION));
		final DataStoreRealm ra = new DataStoreRealm(model);
		ra.addDataStore(rs);
		final IProperty property = ra.getProperty(LOCATION);
		final long l001 = System.currentTimeMillis();
		final CompositeCommand cm = new CompositeCommand();
		for (int a = 0; a < 20000; a++) {
			final IEntry newObject = ra.newObject();
			final ICommand objectAdditionCommand = ra
					.getObjectAdditionCommand(newObject);
			cm.addCommand(objectAdditionCommand);
			final ICommand createSetValuesCommand = property.getCommandFactory()
					.createSetValuesCommand(property, newObject, LOCATION, a, 10, STREET
							+ a);
			cm.addCommand(createSetValuesCommand);
		}
		ra.execute(cm);
		Set<Object> values = null;
		final long l00 = System.currentTimeMillis();
		System.out.println("Content populating:" + (l00 - l001)); //$NON-NLS-1$
		final Collection<IEntry> contents = ra.getContents();
		final long l0 = System.currentTimeMillis();
		System.out.println("Content Acquiring:" + (l0 - l00)); //$NON-NLS-1$
		for (final IEntry e : contents) {
			values = property.getValues(e);
		}
		final long l1 = System.currentTimeMillis();
		System.out.println(values);
		System.out.println("Value Iteration:" + (l1 - l0)); //$NON-NLS-1$
		System.out.println("Count:" + contents.size()); //$NON-NLS-1$
	}
}
