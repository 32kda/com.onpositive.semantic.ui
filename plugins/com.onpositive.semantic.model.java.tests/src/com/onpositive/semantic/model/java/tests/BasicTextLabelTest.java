package com.onpositive.semantic.model.java.tests;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

import junit.framework.TestCase;

import com.onpositive.semantic.model.api.expressions.ConstantExpression;
import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.expressions.GetPropertyLookup;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.api.labels.NotFoundException;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.property.java.annotations.NoMultiValue;
import com.onpositive.semantic.model.api.property.java.annotations.TextLabel;

public class BasicTextLabelTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		//MetaAccess.setDefaultMetaProvider(new BeanMetaProvider());
	}

	@TextLabel("{x}+{y}={x+y}")
	static class X {
		int x = 1;
		int y = 2;
	}
	
	
	static class BT {
		@TextLabel("this!=null?(this?'yes':'no'):'undefined'")
		Boolean mn;
	}
	static class BT1 {
		@TextLabel("this!=null?(this?'yes':'no'):'undefined'")
		boolean mn;
	}

//	static class IL1 {
//		
//		int mn;
//	}

	public void test0() {
		X x = new X();
		String text = LabelAccess.getLabel(x);
		TestCase.assertEquals(text,"1+2=3");
	}
	public void test1() {
		BT b0=new BT();
		IProperty property = PropertyAccess.getProperty(b0, "mn");
		String label=LabelAccess.getLabel(property,b0,property.getValue(b0));
		TestCase.assertEquals(label,"undefined");
		b0.mn=true;
		label=LabelAccess.getLabel(property,b0,property.getValue(b0));
		TestCase.assertEquals(label,"yes");
		b0.mn=false;
		label=LabelAccess.getLabel(property,b0,property.getValue(b0));
		TestCase.assertEquals(label,"no");
		try {
			b0.mn=(Boolean) LabelAccess.lookupFromLabel(property, b0, "yes");
		} catch (NotFoundException e) {
			TestCase.assertTrue(false);
		}		
		TestCase.assertEquals(b0.mn, (Boolean)true);
		try {
			b0.mn=(Boolean) LabelAccess.lookupFromLabel(property, b0, "undefined");
		} catch (NotFoundException e) {
			TestCase.assertTrue(false);
		}
		TestCase.assertEquals(b0.mn, (Boolean)null);
	}
	public void test2() {
		BT1 b0=new BT1();
		IProperty property = PropertyAccess.getProperty(b0, "mn");		
		try {
			b0.mn=(Boolean) LabelAccess.lookupFromLabel(property, b0, "undefined");
		} catch (NotFoundException e) {
			return;
		}
		TestCase.assertTrue(false);
	}
	
	public void test3(){
		ArrayList<String>st=new ArrayList<String>();
		String label = LabelAccess.getLabel(st);
		TestCase.assertTrue(label.length()==0);
		st.add("a");
		label = LabelAccess.getLabel(st);
		TestCase.assertEquals(label, "a");
		st.add("b");
		label = LabelAccess.getLabel(st);
		TestCase.assertEquals(label, "a, b");
		st.add("c");
		label = LabelAccess.getLabel(st);
		TestCase.assertEquals(label, "a, b, c");
		st.add("d");
		label = LabelAccess.getLabel(st);
		TestCase.assertEquals(label, "a, b, c, d, ...");
	}
	
	public void test4(){
		TestCurrency x = new TestCurrency();
		x.value=12132.2;
		TestCase.assertEquals(ExpressionAccess.calculate("value!=null", x),true);
		String label = LabelAccess.getLabel(x);
		
		TestCase.assertEquals(label,NumberFormat.getInstance().format(x.value)+" UNKNOWN");
		x.value=null;
		label = LabelAccess.getLabel(x);
		
		TestCase.assertEquals(label,"UNDEFINED");
	}
	
	public void test5(){
		TestCurrency1 x = new TestCurrency1();
		x.value=12132.2;
		String label = LabelAccess.getLabel(x);
		
		TestCase.assertEquals(label,NumberFormat.getInstance().format(x.value));
		x.value=null;
		label = LabelAccess.getLabel(x);
		
		TestCase.assertEquals(label,"UNDEFINED");
	}
	
	public void test6(){
		ItemRange<Integer> itemRange = new ItemRange<Integer>();
		itemRange.max=10;
		String label = LabelAccess.getLabel(itemRange);
		TestCase.assertEquals("[any...10]", label);
		itemRange.max=null;
		label = LabelAccess.getLabel(itemRange);
		TestCase.assertEquals("[any...any]", label);
	}
	
	public void test7(){
		ItemRange<Integer> itemRange = new ItemRange<Integer>();
		itemRange.max=10;
		IListenableExpression<?> parse = ExpressionAccess.parse("[{min.@label}...{max.@label}]", new GetPropertyLookup(new ConstantExpression(itemRange), null));
		String label = (String) parse.getValue();
		TestCase.assertEquals("[any...10]", label);		
	}
	
	@TextLabel("{value!=null?(value.@label+(cr!=null?cr.Symbol:' UNKNOWN')):'UNDEFINED'}")
	static class TestCurrency{
	
		Currency cr;
		Double value;
	}
	@TextLabel("{value.@label}{cr.@label}{(cr==null&&value==null)?'UNDEFINED':''}")
	static class TestCurrency1{
	
		@TextLabel("this!=null?Symbol:''")
		Currency cr;
		
		@TextLabel("this!=null?this.@label:''")
		Double value;
	}
	
	
	static class ML{
		ArrayList<Integer>ms=new ArrayList<Integer>();
	}
	
	static class ML1{
		@NoMultiValue
		ArrayList<Integer>ms=new ArrayList<Integer>();
	}
}
