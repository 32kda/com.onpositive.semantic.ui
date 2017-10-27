package com.onpositive.semantic.model.java.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Description;
import com.onpositive.semantic.model.api.property.java.annotations.FixedBound;
import com.onpositive.semantic.model.api.property.java.annotations.Id;
import com.onpositive.semantic.model.api.property.java.annotations.NestedValidation;
import com.onpositive.semantic.model.api.property.java.annotations.NoUndo;
import com.onpositive.semantic.model.api.property.java.annotations.Range;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.RegExp;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
import com.onpositive.semantic.model.api.property.java.annotations.Validator;
import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaContributor;
import com.onpositive.semantic.model.api.realm.ConstantRealmProvider;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.undo.UndoMetaUtils;
import com.onpositive.semantic.model.api.validation.ValidationAccess;
import com.onpositive.semantic.model.ui.generic.IContentAssistConfiguration;
import com.onpositive.semantic.model.ui.generic.IContentProposalProvider;
import com.onpositive.semantic.model.ui.generic.annotations.ContentAssist;

public class BasicAnnotationMetaTest extends TestCase {

	private static final String THIS_IS_DESCRIPTION_OF_USER_ID = "This is description of user id";
	private static final String USER_ID = "User id";
	private static final String BASIC_ANNOTATION_META_TESTS_0 = "Basic AnnotationMetaTests 0";

	@Override
	protected void setUp() throws Exception {

	}

	static class TestModel {

		@Required("Id is required")
		@Caption(USER_ID)
		@Description(THIS_IS_DESCRIPTION_OF_USER_ID)
		Integer id;

		@Id
		@RegExp(value = "(.)+", message = BASIC_ANNOTATION_META_TESTS_0)
		String name;

	}

	static class TestIssue {

		@Validator(value = "this!=$.reporter", message = "owner should not be equal to reporter")
		String owner;

		@Validator(value = "this!=$.owner", message = "owner should not be equal to reporter")
		String reporter;

	}

	@NestedValidation
	static class TestIssue2 {

		@Validator(value = "this!=$.reporter", message = "owner should not be equal to reporter")
		TestIssue owner;

		@Validator(value = "this!=$.owner", message = "owner should not be equal to reporter")
		TestIssue reporter;

	}

	static class TestIssue3 {

		@Validator(value = "this!=$.reporter", message = "owner should not be equal to reporter")
		TestIssue3 owner;

		@Validator(value = "this!=$.owner", message = "owner should not be equal to reporter")
		TestIssue3 reporter;

	}
	
	static class TestIssue4 {

		TestIssue owner=new TestIssue();
		
		

	}
	static class TestIssue5 {

		
		TestIssue owner=new TestIssue();		

	}
	
	@NestedValidation
	static class TestIssue6 {

		
		TestIssue owner=new TestIssue();		

	}
	@Validator(value="(this.x+this.y)==10")
	static class TestIssue7 {

		
		int x=5;		

		int y=9;
	}
	
	@Validator(value="z==1")
	static class TestIssue10 extends TestIssue7{
	
		int z=2;
	}
	
	static class TestIssue8 {
		
		@Validator(value="(this>3)&&(this<50)")
		@RegExp("\\d\\d")
		int pattern;
	}
	
	@NestedValidation
	static class TestIssue9 {

		
		Object owner=new TestIssue();
		
		

	}
	
	@NestedValidation
	static class TestIssue11 {

		
		Object[] owner=new Object[]{new TestIssue()};		
		

	}
	static class TestIssue12 {

		
		List owner=new ArrayList();		
		{
			owner.add(new TestIssue());
		}

	}
	static class TestIssue13 {

		
		Map owner=new HashMap<String, TestIssue>();		
		{
			owner.put("a" ,new TestIssue());
		}

	}

	public static class TestContentAssistConfig implements
			IContentAssistConfiguration {

		@Override
		public char[] getAutoactivationCharacters() {
			return null;
		}

		@Override
		public IContentProposalProvider getProposalProvider() {
			return null;
		}

		@Override
		public ITextLabelProvider getProposalLabelProvider() {
			return null;
		}

		@Override
		public int getProposalAcceptanceStyle() {
			return 0;
		}

		@Override
		public int getFilterStyle() {
			return 0;
		}

	}

	@NoUndo
	static class NoUndoTest {

		@ContentAssist(TestContentAssistConfig.class)
		int mm;
	}

	public static void test4() {
		TestCase.assertTrue(!UndoMetaUtils.undoAllowed(MetaAccess
				.getMeta(new NoUndoTest())));
	}
	

	public static void test5() {
		IProperty property = PropertyAccess.getProperty(
				new NoUndoTest(), "mm");
		TestCase.assertTrue(DefaultMetaKeys.getService(property,
				IContentAssistConfiguration.class) instanceof TestContentAssistConfig);
	}

	public void test0() throws SecurityException, NoSuchFieldException {
		String str = BASIC_ANNOTATION_META_TESTS_0; //$NON-NLS-1$
		BaseMeta baseMeta = new BaseMeta();
		MetaContributor.contribute(baseMeta,
				TestModel.class.getDeclaredField("name"));
		TestCase.assertTrue(DefaultMetaKeys.isUnique(baseMeta));
//		IValidator<?> iValidator = DefaultMetaKeys.getValue(baseMeta,
//				IValidationContext.VALIDATOR_KEY, IValidator.class, null);
		// iValidator.isValid(context, object)
		//TestCase.assertNotNull(iValidator);
		TestModel object = new TestModel();
		object.id=2;
		CodeAndMessage validate = ValidationAccess.validate(object);
		String message = validate.getMessage();
		TestCase.assertEquals(message, str);
	}

	public void test2() throws SecurityException, NoSuchFieldException {
		TestIssue object = new TestIssue();
		object.reporter = "22".substring(1);
		object.owner = "2";
		CodeAndMessage validate = ValidationAccess.validate(object);
		String message = validate.getMessage();
		TestCase.assertEquals(message, "owner should not be equal to reporter");
	}

	public void test3() throws SecurityException, NoSuchFieldException {
		TestIssue2 object = new TestIssue2();
		object.reporter = new TestIssue();
		object.owner = object.reporter;
		CodeAndMessage validate = ValidationAccess.validate(object);
		String message = validate.getMessage();
		TestCase.assertEquals(message, "owner should not be equal to reporter");
	}

	public void test7() throws SecurityException, NoSuchFieldException {
		TestIssue2 object = new TestIssue2();
		object.reporter = new TestIssue();
		object.owner = new TestIssue();
		CodeAndMessage validate = ValidationAccess.validate(object);
		String message = validate.getMessage();
		TestCase.assertEquals(message, "owner should not be equal to reporter");
	}

	public void test8() throws SecurityException, NoSuchFieldException {
		for (int a = 0; a < 100; a++) {
			TestIssue3 object = new TestIssue3();
			object.reporter = new TestIssue3();
			object.reporter.owner = object;
			CodeAndMessage validate = ValidationAccess.validate(object);
			TestCase.assertFalse(validate.isError());
		}
	}
	public void test9() throws SecurityException, NoSuchFieldException {
		TestIssue4 object = new TestIssue4();
		CodeAndMessage validate = ValidationAccess.validate(object);
		TestCase.assertFalse(validate.isError());
	}
	public void test10() throws SecurityException, NoSuchFieldException {
		TestIssue5 object = new TestIssue5();
		CodeAndMessage validate = ValidationAccess.validate(object);
		TestCase.assertFalse(validate.isError());
	}
	public void test11() throws SecurityException, NoSuchFieldException {
		TestIssue6 object = new TestIssue6();
		CodeAndMessage validate = ValidationAccess.validate(object);
		TestCase.assertTrue(validate.isError());
	}
	public void test12() throws SecurityException, NoSuchFieldException {
		TestIssue9 object = new TestIssue9();
		object.owner=new TestIssue();
		CodeAndMessage validate = ValidationAccess.validate(object);
		TestCase.assertTrue(validate.isError());
	}
	public void test13() throws SecurityException, NoSuchFieldException {
		TestIssue8 object = new TestIssue8();
		object.pattern=0;
		CodeAndMessage validate = ValidationAccess.validate(object);
		TestCase.assertTrue(validate.isError());
		object.pattern=43;
		validate = ValidationAccess.validate(object);
		TestCase.assertTrue(!validate.isError());
		object.pattern=60;
		validate = ValidationAccess.validate(object);
		TestCase.assertTrue(validate.isError());
		
	}
	public void test14() throws SecurityException, NoSuchFieldException {
		TestIssue10 ts=new TestIssue10();
		CodeAndMessage validate = ValidationAccess.validate(ts);
		TestCase.assertTrue(validate.isError());
		ts.z=1;
		validate = ValidationAccess.validate(ts);
		TestCase.assertTrue(validate.isError());
		ts.y=5;
		validate = ValidationAccess.validate(ts);
		TestCase.assertTrue(!validate.isError());
	}
	
	public void test15() throws SecurityException, NoSuchFieldException {
		TestIssue11 ts=new TestIssue11();
		CodeAndMessage validate = ValidationAccess.validate(ts);
		TestCase.assertTrue(validate.isError());
		TestIssue12 ts1=new TestIssue12();
		validate = ValidationAccess.validate(ts1);
		TestCase.assertTrue(validate.isError());
		TestIssue13 ts2=new TestIssue13();
		validate = ValidationAccess.validate(ts2);
		TestCase.assertTrue(!validate.isError());
	}
	
	public static class RequiredTest{
		
		@Required
		String a;
	}
	
	public void test16(){
		RequiredTest ts=new RequiredTest();
		CodeAndMessage validate = ValidationAccess.validate(ts);
		TestCase.assertTrue(validate.isError());
		ts.a="asa";
		validate = ValidationAccess.validate(ts);
		TestCase.assertTrue(!validate.isError());
	}
	
	public static class FixedBoundTest{
		
		@FixedBound
		String a;
	}
	
	public static class FixedBoundTest2{
		
		@FixedBound
		@Required
		@RealmProvider(SimpleRealm.class)
		String a;
	}
	public static class SimpleRealm extends ConstantRealmProvider{
		
		public SimpleRealm() {
			super("a","b");
		}
	}
	
	public void test17(){
		FixedBoundTest tst=new FixedBoundTest();
		CodeAndMessage validate = ValidationAccess.validate(tst);
		TestCase.assertTrue(!validate.isError());
		tst.a="a";
		validate = ValidationAccess.validate(tst);
		TestCase.assertTrue(validate.isError());		
	}
	public void test18(){
		FixedBoundTest2 tst=new FixedBoundTest2();
		CodeAndMessage validate = ValidationAccess.validate(tst);
		TestCase.assertTrue(validate.isError());
		tst.a="a";
		validate = ValidationAccess.validate(tst);
		TestCase.assertTrue(!validate.isError());		
	}
	
	public static class FixedBoundTest3{
		
		@FixedBound
		@Required("Field is required")
		@RealmProvider(SimpleRealm.class)
		ArrayList<String> a=new ArrayList<String>();
	}
	
	public void test19(){
		FixedBoundTest3 tst=new FixedBoundTest3();
		CodeAndMessage validate = ValidationAccess.validate(tst);
		TestCase.assertTrue(validate.isError());
		tst.a.add("a");
		validate = ValidationAccess.validate(tst);
		TestCase.assertTrue(!validate.isError());		
		tst.a.add("v");
		validate = ValidationAccess.validate(tst);
		TestCase.assertTrue(validate.isError());		
	}
	public void test20(){
		FixedBoundTest3 tst=new FixedBoundTest3();
		CodeAndMessage validate = ValidationAccess.validate(tst);
		TestCase.assertTrue(validate.isError());
		tst.a.add("a");
		validate = ValidationAccess.validate(tst);
		TestCase.assertTrue(!validate.isError());		
		tst.a.add(null);
		validate = ValidationAccess.validate(tst);
		TestCase.assertTrue(validate.isError());
		TestCase.assertTrue(validate.getMessage().equals("Field is required"));
	}
	
	class RR{
		@Range(min = 1, max = 150)
		int age = 15;
		
	}
	public void test21(){
		PropertyAccess.setValue("age", new RR(),20);
		
		try{
		PropertyAccess.setValue("age", new RR(),200);
		}catch (IllegalArgumentException e) {
			return;
		}
		TestCase.assertTrue(false);
	}
}
