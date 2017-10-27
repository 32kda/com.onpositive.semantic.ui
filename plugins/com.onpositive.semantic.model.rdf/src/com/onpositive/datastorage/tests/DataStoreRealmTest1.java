package com.onpositive.datastorage.tests;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.Set;

import com.onpositive.datamodel.core.DataStoreRealm;
import com.onpositive.datamodel.core.IEntry;
import com.onpositive.datamodel.impl.BinaryRDFDocument;
import com.onpositive.datamodel.impl.storage.GrowingByteArray;
import com.onpositive.datamodel.model.DataModel;
import com.onpositive.datamodel.model.DefaultModelProperty;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.property.IProperty;

public class DataStoreRealmTest1 {

	private static final String id = "id";
	private static final String from = "mail";
	private static final String to = "to";
	private static final String cc = "cc";
	private static final String bcc = "bcc";
	private static final String content = "content";
	private static final String subject = "subject";
	private static final String received = "reveived";
	private static final String sended = "sended";

	public static void addMailCommand(DataStoreRealm ra, int a) {
		final CompositeCommand cm = new CompositeCommand();
		final IEntry newObject = ra.newObject();
		final ICommand objectAdditionCommand = ra
				.getObjectAdditionCommand(newObject);
		cm.addCommand(objectAdditionCommand);
		setProperty(newObject, ra, cm, id, a);
		setProperty(newObject, ra, cm, from, "Pavel" + a + "@mail.aptana.com");
		setProperty(newObject, ra, cm, to, "Denis" + a + "@mail.aptana.com",
				"Mike" + a + "@mail.aptana.com");
		setProperty(newObject, ra, cm, cc, "D@mail.aptana.com");
		setProperty(newObject, ra, cm, bcc, "CD@mail.aptana.com");
		setProperty(newObject, ra, cm, subject, "Item" + a % 10);
		setProperty(newObject, ra, cm, received, new Date());
		setProperty(newObject, ra, cm, sended, new Date());
		//setProperty(newObject, ra, cm, content, createContent());
		ra.execute(cm);
	}

	private static Object createContent() {
		StringBuilder bld = new StringBuilder();
		Random r = new Random();
		int nextInt = r.nextInt(1000);
		for (int a = 0; a < nextInt; a++) {
			bld.append(nextInt + "\r\nr");
		}
		System.out.println(bld.length());
		return bld.toString();
	}

	public static void setProperty(IEntry entry, DataStoreRealm ra,
			CompositeCommand cm, String property, Object... values) {
		IProperty property2 = ra.getProperty(property);
		ICommand createSetValuesCommand = property2.getCommandFactory().createSetValuesCommand(
				property2, entry, values);
		cm.addCommand(createSetValuesCommand);
	}

	public static void main(String[] args) {
		final DataModel model = new DataModel();

		model.registerProperty(new DefaultModelProperty(id));
		model.registerProperty(new DefaultModelProperty(from));
		model.registerProperty(new DefaultModelProperty(to));
		model.registerProperty(new DefaultModelProperty(cc));
		model.registerProperty(new DefaultModelProperty(bcc));
		model.registerProperty(new DefaultModelProperty(subject));
		model.registerProperty(new DefaultModelProperty(content));
		model.registerProperty(new DefaultModelProperty(sended));
		model.registerProperty(new DefaultModelProperty(received));
		final DataStoreRealm ra = new DataStoreRealm(model);

		final BinaryRDFDocument rs = new BinaryRDFDocument();
		
		final long l001 = System.currentTimeMillis();
		File file = new File("C:/content/ttt.dta");
//		try {
//			FileInputStream ss = new FileInputStream(file);
//			GrowingByteArray growingByteArray = new GrowingByteArray((int) file
//					.length());
//			growingByteArray.load(new BufferedInputStream(ss));
//			rs.load(growingByteArray, 0);
//			ss.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//			for (int a = 0; a < 20000; a++) {
//				addMailCommand(ra, a);
//			}
//		}
		ra.addDataStore(rs);
		for (int a = 0; a < 20000; a++) {
			addMailCommand(ra, a);
		}
		
		// final IProperty<Object> property = ra.getProperty(LOCATION);
		final long l00 = System.currentTimeMillis();
		System.out.println("Content populating:" + (l00 - l001)); //$NON-NLS-1$
		Set<Object> values = null;
		
		final Collection<IEntry> contents = ra.getContents();
		final long l0 = System.currentTimeMillis();
		System.out.println("Content Acquiring:" + (l0 - l00)); //$NON-NLS-1$
		IProperty property = ra.getProperty(subject);
		for (final IEntry e : contents) {
			values = property.getValues(e);
		}
		final long l1 = System.currentTimeMillis();
		long lt0 = System.currentTimeMillis();
		GrowingByteArray ba = new GrowingByteArray(100000);
		rs.store(ba);
		try {
			FileOutputStream ss = new FileOutputStream(file);
			ba.write(new BufferedOutputStream(ss));
			ss.close();
		} catch (FileNotFoundException e1) {
			//TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		long lt1 = System.currentTimeMillis();
		System.out.println(lt1 - lt0);
		System.out.println(ba.getSize());
		System.out.println(values);
		System.out.println("Value Iteration:" + (l1 - l0)); //$NON-NLS-1$
		System.out.println("Count:" + contents.size()); //$NON-NLS-1$
	}
}
