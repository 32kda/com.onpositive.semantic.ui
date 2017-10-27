package com.onpositive.semantic.model.java.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.binding.AbstractBinding;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.BindingSerializer;
import com.onpositive.semantic.model.binding.IBinding;

import junit.framework.TestCase;

public class SerializationTest extends TestCase {

	static class A implements Serializable{
		int x;
	}

	public void test0() {
		IProperty q = PropertyAccess.getProperty(new A(), "x");
		TestCase.assertEquals(q, cycle(q));
	}
	
	public void test1(){
		A object = new A();
		object.x=2;
		cycle(object);
		Binding b=new Binding(object);
		IListenableExpression<Object> binding = b.getBinding("x");
		IBinding cycle = (IBinding) ((IListenableExpression<Object>) cycle(binding));
		TestCase.assertEquals(cycle.getValue(),2);
		cycle.setValue(5);
		A a1=(A) cycle.getParent().getValue();
		TestCase.assertEquals(a1.x, 5);
	}
	
	public void test2(){
		A object = new A();
		object.x=2;
		cycle(object);
		Binding b=new Binding(object);
		IListenableExpression<Object> binding = b.getBinding("x");
		try {
			BindingSerializer.serializeBinding((AbstractBinding) binding, new DataOutputStream(new ByteArrayOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static byte[] serialize(Object o) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(
					byteArrayOutputStream);
			os.writeObject(o);
			os.close();
			byte[] byteArray = byteArrayOutputStream.toByteArray();
			return byteArray;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	public static Object cycle(Object o){
		return deserialize(serialize(o));
	}

	public static Object deserialize(byte[] z) {
		try {
			ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(z);
			ObjectInputStream is=new ObjectInputStream(byteArrayOutputStream);
			return is.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
}
