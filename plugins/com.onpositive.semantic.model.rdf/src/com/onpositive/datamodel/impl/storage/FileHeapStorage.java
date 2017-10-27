package com.onpositive.datamodel.impl.storage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.BitSet;
import java.util.HashMap;

import com.onpositive.commons.Activator;

public class FileHeapStorage extends AbstractValueTable {

	public static final int SECTOR_SIZE = 32;

	private BitSet blocksState = new BitSet();
	private HashMap<String, Integer> ids = new HashMap<String, Integer>();
	private RandomAccessFile currentFile;
	private File blockState;

	private int maxUsedBlock;
	private int freeBlockCount;

	private String path;

	public void close() throws IOException {
		currentFile.close();
	}

	public int size() throws IOException {
		return (int) ((int) currentFile.length()+blockState.length());
	}

	public void delete() throws IOException {
		currentFile.close();
		new File(path).delete();
		blockState.delete();
	}

	public FileHeapStorage(ObjectPool pool, String path) throws IOException {
		super(pool);
		currentFile = new RandomAccessFile(path, "rw");
		this.path = path;
		File sa = new File(path + ".blocks");
		blockState = sa;
		if (sa.exists()) {
			try {
				FileInputStream stream = new FileInputStream(sa);
				try {
					ObjectInputStream z = new ObjectInputStream(stream);
					try {
						try {
							initStorage(z);
						} finally {
							z.close();
							stream.close();
						}
					} catch (ClassNotFoundException e) {
						throw new LinkageError();
					}
				} finally {
					stream.close();
				}
			} catch (RuntimeException e) {
				blockState.delete();
				//throw new RuntimeException();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void initStorage(ObjectInputStream z) throws IOException,
			ClassNotFoundException {
		blocksState = (BitSet) z.readObject();
		maxUsedBlock = z.readInt();
		freeBlockCount = z.readInt();
		ids = (HashMap<String, Integer>) z.readObject();
	}

	private int write(GrowingByteArray array) throws IOException {
		int internalAlloc = internalAlloc(array.getSize());
		writeTo(array, internalAlloc);
		// syncBlocks();
		return internalAlloc;
	}

	protected void syncBlocks() throws IOException {
		FileOutputStream da = new FileOutputStream(blockState);
		ByteArrayOutputStream ba=new ByteArrayOutputStream();
		ObjectOutputStream z = new ObjectOutputStream(ba);
		z.writeObject(blocksState);
		z.writeInt(maxUsedBlock);
		z.writeInt(freeBlockCount);
		z.writeObject(ids);
		z.close();
		da.write(ba.toByteArray());
		da.close();
	}

	private void writeTo(GrowingByteArray array, int internalAlloc)
			throws IOException {
		currentFile.seek(internalAlloc);
		currentFile.writeInt(array.getSize());
		currentFile.write(array.array(), 0, array.getSize());
	}

	private int internalAlloc(int size) throws IOException {
		int blockCount = getBlockCount(size);
		if (freeBlockCount * SECTOR_SIZE >= size) {
			int count = 0;
			int lastFree = -1;
			for (int a = 0; a < maxUsedBlock; a++) {
				boolean b = blocksState.get(a);
				if (!b) {
					count = 0;
				} else {
					if (count == 0) {
						lastFree = a;
					}
					count++;
					if (count == blockCount) {
						for (int ba = lastFree; ba < lastFree + blockCount; ba++) {
							blocksState.clear(ba);
							freeBlockCount--;
						}
						return lastFree * SECTOR_SIZE;
					}
				}
			}
		}
		allocateNewBlocs(blockCount);
		int i = maxUsedBlock * SECTOR_SIZE;
		maxUsedBlock = maxUsedBlock + blockCount;
		return i;
	}

	private int getBlockCount(int size) {
		size = size + 4;
		int blockCount = size / SECTOR_SIZE;
		if (size % SECTOR_SIZE != 0) {
			blockCount++;
		}
		return blockCount;
	}

	private void allocateNewBlocs(int size) throws IOException {
		int i = maxUsedBlock + size;
		currentFile.setLength(i * SECTOR_SIZE);
	}

	int rewrite(String id, int address, GrowingByteArray array)
			throws IOException {
		int newSize = array.getSize();
		int pa = address / SECTOR_SIZE;
		currentFile.seek(address);
		int sz = currentFile.readInt();
		int ob = getBlockCount(sz);
		int blockCount = getBlockCount(newSize);
		if (ob <= blockCount) {
			writeTo(array, address);
			for (int a = pa + blockCount; a < pa + ob; a++) {
				blocksState.set(a);
			}
			return address;
		} else {
			internalFree(id);
			return write(array);
		}
	}

	public GrowingByteArray readArray(String id) throws IOException {
		Integer adr = ids.get(id);
		if (adr == null) {
			return null;
		}
		int address = adr;
		currentFile.seek(address);
		int readInt = currentFile.readInt();
		GrowingByteArray ba = new GrowingByteArray(readInt);
		ba.size = readInt;
		currentFile.readFully(ba.bytes, 0, readInt);
		return ba;
	}

	public void writeArray(String id, GrowingByteArray array)
			throws IOException {
		Integer adr = ids.get(id);
		if (adr == null) {
			int write = write(array);
			ids.put(id, write);
		} else {
			rewrite(id, adr, array);
		}
		syncBlocks();
	}

	public void freeArray(String id) throws IOException {
		internalFree(id);
		syncBlocks();
	}

	private void internalFree(String id) throws IOException {
		Integer adr = ids.get(id);
		if (adr == null) {
			return;
		}
		ids.remove(id);
		int position = adr.intValue();
		currentFile.seek(position);
		int sz = currentFile.readInt();
		int ob = getBlockCount(sz);
		int bo = position / SECTOR_SIZE;
		int i = bo + ob;
		for (int a = bo; a < i; a++) {
			blocksState.set(a);
			freeBlockCount++;
		}
	}

	public Object[] getValues(String id) {
		GrowingByteArray readArray;
		try {
			readArray = readArray(id);
			if (readArray==null){
				return null;
			}
			int int1 = readArray.getInt(0);
			Object[] ra = new Object[int1];
			int offset = 4;
			for (int a = 0; a < ra.length; a++) {
				offset = decodeValue(offset, a, ra, readArray);
			}
			return ra;
		} catch (IOException e) {
			Activator.log(e);
			throw new RuntimeException(e);
		}
	}

	public void setValues(String id, Object... objects) {
		try {
			if (objects == null || objects.length == 0) {
				freeArray(id);
				return;
			}
			GrowingByteArray ba = new GrowingByteArray(100);
			ba.add(objects.length);
			for (Object o:objects){
				encodeValue(o, ba);
			}
			writeArray(id, ba);
		} catch (IOException e) {
			Activator.log(e);
			throw new RuntimeException(e);
		}
	}
}
