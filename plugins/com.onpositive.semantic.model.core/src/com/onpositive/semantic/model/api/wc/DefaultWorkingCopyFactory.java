package com.onpositive.semantic.model.api.wc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.undo.UndoMetaUtils;

public class DefaultWorkingCopyFactory implements IWorkingCopyCreator{

	@Override
	public Object getWorkingCopy(Object source) {
		//FIXME PARENT,INDEPENDENT
		Serializable s=(Serializable) source;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(source);
			objectOutputStream.close();
			ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
			return objectInputStream.readObject();
		} catch (IOException e) {
			throw new IllegalStateException();
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException();
		}
	}

	@Override
	public void applyWorkingCopy(Object source, Object target) {
		Iterable<IProperty> properties = PropertyAccess.getProperties(source);
		CompositeCommand z=new CompositeCommand();
		for (IProperty p:properties){
			if (!DefaultMetaKeys.isReadonly(p)&&!DefaultMetaKeys.isComputed(p)&&!DefaultMetaKeys.isStatic(p)){
				z.addCommand(PropertyAccess.createSetValueCommand(target,p.getValue(source), p));
			}
		}
		z.execute();
	}

	public void applyWorkingCopyWithUndo(Object source, Object target,
			Object undoCtx) {
		Iterable<IProperty> properties = PropertyAccess.getProperties(source);
		CompositeCommand z=new CompositeCommand();
		for (IProperty p:properties){
			if (!DefaultMetaKeys.isReadonly(p)&&!DefaultMetaKeys.isComputed(p)&&!DefaultMetaKeys.isStatic(p)){
				z.addCommand(PropertyAccess.createSetValueCommand(target,p.getValue(source), p));
			}
		}
		UndoMetaUtils.setUndoContext(z, undoCtx);
		UndoMetaUtils.markUndoable(z, true);
		z.execute();
		
	}

}
