package com.onpositive.semantic.model.api.expressions;

import com.onpositive.semantic.model.api.access.IExternalizer;
import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;

//TODO FIXME THINK ABOUT BINDINGS and expressions
public class ExpressionAccess {

	static IExpressionParser parser;

	public static void setExpresssionParser(IExpressionParser ps) {
		parser = ps;
	}

	public static IExpressionParser getExpressionParser() {
		initParserIfNeeded();
		return parser;
	}

	private static void initParserIfNeeded() {
		if (parser == null) {
			String property = System
					.getProperty("expressions.parser",
							"com.onpositive.semantic.model.expressions.impl.ExpressionParserV2");
			try {
				parser = (IExpressionParser) ExpressionAccess.class
						.getClassLoader().loadClass(property).newInstance();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public static String calculateAsString(String string, IHasMeta meta,
			Object parentObject, Object object) {
		initParserIfNeeded();
		IExternalizer service = DefaultMetaKeys.getService(meta,
				IExternalizer.class);
		if (service != null) {
			string = service.externalizeMessage(string);
		}
		Object calculate = calculate(string, meta, parentObject, object);
		if (calculate instanceof String) {
			return (String) calculate;
		}
		if (calculate == null) {
			return string;//
		}
		return LabelAccess.getLabel(calculate);
	}

	public static Object calculate(String expression, IHasMeta context,
			Object parent, Object value) {
		initParserIfNeeded();
		IExpressionParser ps = ExpressionAccess.getExpressionParser();
		if (context instanceof IExpressionEnvironment) {
			IListenableExpression<?> parse = ps.parse(expression,(IExpressionEnvironment) context);
			if (parse != null) {
				Object vl = parse.getValue();
				parse.disposeExpression();
				return vl;
			}
		}
		BasicLookup bl = new BasicLookup(context, value);
		if (parent != null) {
			bl.registerChild("$", parent);
		}
		IListenableExpression<?> parse = ps.parse(expression, bl);
		if (parse != null) {
			Object vl = parse.getValue();
			parse.disposeExpression();
			return vl;
		}

		return null;
	}

	public static Object calculate(String expression, Object parent,
			Object value) {
		IHasMeta meta = MetaAccess.getMeta(value);
		return calculate(expression, meta, parent, value);
	}

	public static Object calculate(String expression, Object value) {
		IHasMeta meta = MetaAccess.getMeta(value);
		return calculate(expression, meta, null, value);
	}

	public static IListenableExpression<?> parse(String expression,
			IExpressionEnvironment environment) {
		initParserIfNeeded();
		return parser.parse(expression, environment);
	}

	public static boolean isExpression(String id) {
		boolean isExpression = false;
		for (int a = 0; a < id.length(); a++) {
			char charAt = id.charAt(a);
			if (charAt != '$' && !Character.isJavaIdentifierPart(charAt)) {
				isExpression = true;
				break;
			}
		}
		return isExpression;
	}
}