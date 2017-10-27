package com.onpositive.semantic.model.java.tests;

import java.util.ArrayList;

import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.property.java.annotations.Child;
import com.onpositive.semantic.model.api.property.java.annotations.CommutativeWith;
import com.onpositive.semantic.model.api.property.java.annotations.OnModify;
import com.onpositive.semantic.model.api.property.java.annotations.Parent;

import junit.framework.TestCase;

public class AdvancedPropertyTest extends TestCase{

	
	public static class TreeNode{
		
		
		@Parent
		@CommutativeWith("children")
		TreeNode parent;
		
		@Child
		@CommutativeWith("parent")
		ArrayList<TreeNode>children=new ArrayList<AdvancedPropertyTest.TreeNode>();
	
		int version;
		
		@OnModify
		public void increaseVersion(){
			version++;
		}
	}
	
	public void test0() {
		TreeNode n0=new TreeNode();
		TreeNode n1=new TreeNode();
		PropertyAccess.addValue(PropertyAccess.getProperty(n0, "children"), n0, n1);
		TestCase.assertEquals(n1.parent, n0);
		TestCase.assertTrue(n0.children.contains(n1));
		TestCase.assertTrue(n0.version==1);
		TestCase.assertTrue(n1.version==1);
	}
	
	public void test1() {
		TreeNode n0=new TreeNode();
		TreeNode n1=new TreeNode();
		PropertyAccess.setValue(PropertyAccess.getProperty(n0, "children"), n0, n1);
		TestCase.assertEquals(n1.parent, n0);
		TestCase.assertTrue(n0.children.contains(n1));
		TestCase.assertTrue(n0.version==1);
		TestCase.assertTrue(n1.version==1);
	}
}
