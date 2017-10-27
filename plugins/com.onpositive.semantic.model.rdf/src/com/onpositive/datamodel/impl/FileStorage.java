package com.onpositive.datamodel.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.onpositive.datamodel.core.IDocument;
import com.onpositive.datamodel.core.IEntry;
import com.onpositive.datamodel.impl.storage.GrowingByteArray;
import com.onpositive.semantic.model.realm.EmptyDelta;
import com.onpositive.semantic.model.realm.ISetDelta;

public class FileStorage  implements IStorage {

	private final File file;
	private IDataStoreRealm realm;

	public FileStorage(File file) {
		this.file = file;
	}

	public FileStorage(String path) {
		this.file = new File(path);
	}

	public ISetDelta<IEntry> delta(IDocument current) {
		return EmptyDelta.getDelta();
	}
	
	public void setRealm(IDataStoreRealm realm){
		this.realm=realm;
	}

	public IDocument load() throws IOException {
		final BinaryRDFDocument doc = new BinaryRDFDocument();
		doc.setRealm(realm);
		final GrowingByteArray ba = new GrowingByteArray((int) this.file
				.length());
		if (this.file.exists()) {
			final BufferedInputStream fileInputStream = new BufferedInputStream(
					new FileInputStream(this.file));
			ba.load(fileInputStream);
			if (ba.getSize()>0){
			doc.load(ba, 0);
			}
			fileInputStream.close();
		}
		return doc;
	}

	public void store(IDocument document) throws IOException {
		final GrowingByteArray ba = new GrowingByteArray(100000);
		document.store(ba);
		File createTempFile = File.createTempFile("temp", ".tmp", this.file
				.getParentFile());
		final FileOutputStream fs = new FileOutputStream(createTempFile);
		final BufferedOutputStream baa = new BufferedOutputStream(fs);
		ba.write(baa);
		baa.close();
		boolean renameTo = createTempFile.renameTo(this.file);
		if (!renameTo) {
			boolean delete = this.file.delete();
			if (delete) {
				renameTo = createTempFile.renameTo(this.file);
				if (!renameTo) {
					throw new IllegalArgumentException();
				}
			}
		}
		createTempFile.delete();
	}
}
