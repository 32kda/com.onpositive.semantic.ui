package com.onpositive.semantic.model.java.tests;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.onpositive.semantic.model.api.property.java.annotations.Name;
import com.onpositive.semantic.model.api.xml.SimpleXMLAccess;

public class XMLModelTest extends TestCase{

	
	@Name("model")
	public static class Model{
		int x;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((mdl == null) ? 0 : mdl.hashCode());
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
			Model other = (Model) obj;
			if (mdl == null) {
				if (other.mdl != null)
					return false;
			} else if (!mdl.equals(other.mdl))
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

		String name;

		
		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}
		
		protected ArrayList<Model>mdl=new ArrayList<XMLModelTest.Model>();
	}
	
	
	public void test0(){
		Model obj = new Model();
		obj.name="ZZ";
		obj.x=1;
		Model model = new Model();
		model.name="A";
		obj.mdl.add(model);
		String write = SimpleXMLAccess.write(obj,true);
		System.out.println(write);
		String toTest="<model name=\"ZZ\" x=\"1\">\r\n" + 
				"  <model name=\"A\"/>\r\n" + 
				"</model>\r\n";
		TestCase.assertEquals(toTest, write);
		Model read = SimpleXMLAccess.read(toTest, Model.class);
		TestCase.assertEquals(read, obj);
	}
	
}
