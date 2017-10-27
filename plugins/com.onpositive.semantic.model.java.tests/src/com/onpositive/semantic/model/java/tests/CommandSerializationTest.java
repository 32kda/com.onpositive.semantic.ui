package com.onpositive.semantic.model.java.tests;

import java.io.Serializable;

import junit.framework.TestCase;

import com.onpositive.semantic.model.api.property.java.annotations.Id;

public class CommandSerializationTest extends TestCase{

	
	static class M0 implements Serializable{
		public M0(String string) {
			this.name=string;
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		int x;		
		String name;
		boolean age;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (age ? 1231 : 1237);
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + x;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			M0 other = (M0) obj;
			if (age != other.age)
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (x != other.x)
				return false;
			return true;
		}
	}
	static class M1 implements Serializable{
		public M1(String string) {
			this.name=string;
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		int x;		
		@Id
		String name;
		boolean age;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (age ? 1231 : 1237);
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + x;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			M0 other = (M0) obj;
			if (age != other.age)
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (x != other.x)
				return false;
			return true;
		}
	}
	
//	public void test0(){
//		M0 m0 = new M0("a");
//		ICommand createSetValueCommand = PropertyAccess.createSetValueCommand("age",m0, 15);
//		ICommand transferToRemoteForm = CompositeCommand.transferToRemoteForm(createSetValueCommand);
//		ICommand restoreFromRemoteForm = CompositeCommand.restoreFromRemoteForm(transferToRemoteForm);
//		TestCase.assertEquals(restoreFromRemoteForm, createSetValueCommand);
//		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//		Hessian2Output m=new Hessian2Output(byteArrayOutputStream);
//		try {
//			m.writeObject(transferToRemoteForm);
//			m.close();
//			byte[] byteArray = byteArrayOutputStream.toByteArray();
//			System.out.println(byteArray.length);
//			Hessian2Input ii=new Hessian2Input(new ByteArrayInputStream(byteArray));
//			ii.setSerializerFactory(new SerializerFactory(CommandSerializationTests.class.getClassLoader()));
//			ICommand c=(ICommand) ii.readObject();
//			TestCase.assertEquals(CompositeCommand.restoreFromRemoteForm(c), createSetValueCommand);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	public void test1(){
//		M1 m1 = new M1("a");
//		ICommand createSetValueCommand = PropertyAccess.createSetValueCommand("age",m1, 15);
//		ICommand transferToRemoteForm = CompositeCommand.transferToRemoteForm(createSetValueCommand);
//		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//		Hessian2Output m=new Hessian2Output(byteArrayOutputStream);
//		try {
//			m.writeObject(transferToRemoteForm);
//			m.close();
//			byte[] byteArray = byteArrayOutputStream.toByteArray();
//			System.out.println(byteArray.length);
//			Hessian2Input ii=new Hessian2Input(new ByteArrayInputStream(byteArray));
//			ii.setSerializerFactory(new SerializerFactory(CommandSerializationTests.class.getClassLoader()));
//			ICommand c=(ICommand) ii.readObject();
//			ICommand restoreFromRemoteForm = CompositeCommand.restoreFromRemoteForm(c);
//			TestCase.assertEquals(restoreFromRemoteForm, createSetValueCommand);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
