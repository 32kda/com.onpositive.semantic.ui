package com.onpositive.semantic.model.api.wc;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

public class WorkingCopyAccess {

	public static Object getWorkingCopy(Object m){
		return new DefaultWorkingCopyFactory().getWorkingCopy(m);		
	}
	
	public static void applyWorkingCopy(Object source,Object target){
		new DefaultWorkingCopyFactory().applyWorkingCopy(source, target);
	}
	
	public static void applyWorkingCopyWithUndo(Object source,Object target,
			Object undoCtx) {
		new DefaultWorkingCopyFactory().applyWorkingCopyWithUndo(source, target,undoCtx);
		
	}

	public static boolean isSame(Object original,Object another){
		if (original==another){
			return true;
		}
		if (original==null||another==null){
			return false;
		}
		if (original.equals(another)){
			return true;
		}
		if (original instanceof Serializable&&another instanceof Serializable){
			byte[] ba = toBytes(original);
			byte[] ba1 = toBytes(another);
			if (Arrays.equals(ba, ba1)){
				return true;
			}
			return false;
		}
		throw new IllegalStateException("Can not compare");	
	}

	private static byte[] toBytes(Object original) {
		byte[] ba=null;
		try{
		ByteArrayOutputStream b=new ByteArrayOutputStream();
		ObjectOutputStream s=new ObjectOutputStream(b);
		s.writeObject(original);
		s.close();
		ba=b.toByteArray();
		}catch (Exception e) {
		}
		return ba;
	}
	
	public static void disposeWorkingCopy(Object wc){
		
	}
}
